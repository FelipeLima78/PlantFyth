package com.network.plantfyth.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.network.plantfyth.TelaCadastroPlanta;
import com.network.plantfyth.databinding.FragmentHomeBinding;
import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int DIAS_PADRAO_ADUBACAO = 30;

    private FragmentHomeBinding binding;
    private PlantFythAPI api;
    private PlantioHomeAdapter adapter;
    private final List<PlantioHomeItem> itens = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new PlantioHomeAdapter(itens, this::onAcaoMarcada);
        binding.recyclerPlantasHome.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPlantasHome.setAdapter(adapter);
        binding.fabAdicionarPlanta.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), TelaCadastroPlanta.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarPlantas();
    }

    private void carregarPlantas() {
        int usuarioId = getUsuarioId();
        if (usuarioId == -1) {
            mostrarCarregando(false);
            mostrarEstadoVazio(true);
            Toast.makeText(requireContext(), "Usuario nao identificado.", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarCarregando(true);
        api.buscarPlantasUsuario(usuarioId).enqueue(new Callback<List<Plantio>>() {
            @Override
            public void onResponse(@NonNull Call<List<Plantio>> call,
                                   @NonNull Response<List<Plantio>> response) {
                if (!isAdded() || binding == null) return;
                mostrarCarregando(false);

                if (response.isSuccessful() && response.body() != null) {
                    atualizarLista(response.body());
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao carregar plantas: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Plantio>> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null) return;
                mostrarCarregando(false);
                Log.e(TAG, "Falha ao carregar plantas", t);
                Toast.makeText(requireContext(),
                        "Falha de conexao: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarLista(List<Plantio> plantas) {
        itens.clear();
        for (Plantio plantio : plantas) {
            itens.add(criarItem(plantio));
        }

        adapter.notifyDataSetChanged();
        mostrarEstadoVazio(itens.isEmpty());
        atualizarAviso();
    }

    private PlantioHomeItem criarItem(Plantio plantio) {
        PlantioHomeItem item = new PlantioHomeItem();
        item.plantio = plantio;
        recalcularStatus(item);
        return item;
    }

    private void recalcularStatus(PlantioHomeItem item) {
        Plantio plantio = item.plantio;
        item.irrigacao = criarStatus(plantio.getPrevisaoProximaIrrigacao());
        item.adubacao = criarStatus(plantio.getPrevisaoProximaAdubacao());
        item.poda = criarStatus(plantio.getPrevisaoProximaPoda());
    }

    private PlantioHomeItem.AcaoStatus criarStatus(Date proximaData) {
        if (proximaData == null) {
            return new PlantioHomeItem.AcaoStatus("Nao definido", PlantioHomeItem.COR_INDISPONIVEL, false, false);
        }

        long diffMs = proximaData.getTime() - new Date().getTime();
        if (diffMs <= 0) {
            long atrasoHoras = Math.max(1, TimeUnit.MILLISECONDS.toHours(Math.abs(diffMs)));
            String texto = atrasoHoras < 24
                    ? "Atrasado hoje"
                    : "Atrasado ha " + TimeUnit.HOURS.toDays(atrasoHoras) + "d";
            return new PlantioHomeItem.AcaoStatus(texto, PlantioHomeItem.COR_ATRASADO, true, true);
        }

        if (diffMs < TimeUnit.HOURS.toMillis(1)) {
            return new PlantioHomeItem.AcaoStatus("Em menos de 1h", PlantioHomeItem.COR_HOJE, false, true);
        }

        if (diffMs < TimeUnit.DAYS.toMillis(1)) {
            long horas = arredondarParaCima(diffMs, TimeUnit.HOURS.toMillis(1));
            return new PlantioHomeItem.AcaoStatus("Em " + horas + "h", PlantioHomeItem.COR_HOJE, false, true);
        }

        long dias = arredondarParaCima(diffMs, TimeUnit.DAYS.toMillis(1));
        return new PlantioHomeItem.AcaoStatus("Em " + dias + "d", PlantioHomeItem.COR_OK, false, true);
    }

    private void onAcaoMarcada(PlantioHomeItem item, TipoAcao tipoAcao) {
        int posicao = itens.indexOf(item);
        if (posicao == -1 || item.salvandoAcao != null) return;

        Plantio plantio = item.plantio;
        Date dataAnterior = getDataAcao(plantio, tipoAcao);
        Date proximaData = calcularProximaData(plantio, tipoAcao);

        if (proximaData == null) {
            Toast.makeText(requireContext(),
                    "Essa acao ainda nao tem previsao cadastrada.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setDataAcao(plantio, tipoAcao, proximaData);
        if (tipoAcao == TipoAcao.IRRIGAR) {
            plantio.setFoiRegado(true);
        }

        item.salvandoAcao = tipoAcao;
        recalcularStatus(item);
        adapter.notifyItemChanged(posicao);
        atualizarAviso();

        salvarPlantioAtualizado(item, posicao, tipoAcao, dataAnterior, proximaData);
    }

    private void salvarPlantioAtualizado(PlantioHomeItem item, int posicao, TipoAcao tipoAcao,
                                         Date dataAnterior, Date proximaData) {
        Plantio plantioLocal = item.plantio;
        api.atualizarPlantio(plantioLocal, plantioLocal.getId()).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(@NonNull Call<Plantio> call, @NonNull Response<Plantio> response) {
                if (!isAdded() || binding == null || posicao >= itens.size()) return;

                PlantioHomeItem itemAtual = itens.get(posicao);
                itemAtual.salvandoAcao = null;

                if (response.isSuccessful() && response.body() != null) {
                    Plantio atualizado = response.body();
                    preservarDataNaoRetornada(atualizado, tipoAcao, proximaData);
                    itemAtual.plantio = atualizado;
                    recalcularStatus(itemAtual);
                    adapter.notifyItemChanged(posicao);
                    atualizarAviso();
                    Toast.makeText(requireContext(), "Acao registrada.", Toast.LENGTH_SHORT).show();
                } else {
                    setDataAcao(itemAtual.plantio, tipoAcao, dataAnterior);
                    recalcularStatus(itemAtual);
                    adapter.notifyItemChanged(posicao);
                    atualizarAviso();
                    Toast.makeText(requireContext(),
                            "Nao foi possivel salvar: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Plantio> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null || posicao >= itens.size()) return;

                PlantioHomeItem itemAtual = itens.get(posicao);
                itemAtual.salvandoAcao = null;
                setDataAcao(itemAtual.plantio, tipoAcao, dataAnterior);
                recalcularStatus(itemAtual);
                adapter.notifyItemChanged(posicao);
                atualizarAviso();

                Log.e(TAG, "Falha ao salvar plantio", t);
                Toast.makeText(requireContext(),
                        "Falha ao salvar: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void preservarDataNaoRetornada(Plantio atualizado, TipoAcao tipoAcao, Date proximaData) {
        if (tipoAcao == TipoAcao.IRRIGAR && atualizado.getPrevisaoProximaIrrigacao() == null) {
            atualizado.setPrevisaoProximaIrrigacao(proximaData);
        } else if (tipoAcao == TipoAcao.ADUBAR && atualizado.getPrevisaoProximaAdubacao() == null) {
            atualizado.setPrevisaoProximaAdubacao(proximaData);
        } else if (tipoAcao == TipoAcao.PODAR && atualizado.getPrevisaoProximaPoda() == null) {
            atualizado.setPrevisaoProximaPoda(proximaData);
        }
    }

    private Date calcularProximaData(Plantio plantio, TipoAcao tipoAcao) {
        Especime especime = plantio.getEspecime();
        switch (tipoAcao) {
            case IRRIGAR:
                return calcularProximaIrrigacao(especime);
            case ADUBAR:
                return plantio.getPrevisaoProximaAdubacao() == null
                        ? null
                        : somarDias(new Date(), DIAS_PADRAO_ADUBACAO);
            case PODAR:
                return calcularProximaPoda(especime);
            default:
                return null;
        }
    }

    private Date calcularProximaIrrigacao(Especime especime) {
        if (especime == null) return somarDias(new Date(), 1);

        double periodo = extrairPrimeiroNumero(especime.getPeriodo_irrigacao(), 1);
        String unidade = especime.getUnidade_irrigacao();
        Calendar calendar = Calendar.getInstance();

        if (unidade != null && unidade.toLowerCase(Locale.ROOT).contains("hour")) {
            calendar.add(Calendar.HOUR_OF_DAY, Math.max(1, (int) Math.ceil(periodo)));
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, Math.max(1, (int) Math.ceil(periodo)));
        }

        return calendar.getTime();
    }

    private Date calcularProximaPoda(Especime especime) {
        if (especime == null || especime.getPeriodo_poda() == null) return null;

        String periodoPoda = especime.getPeriodo_poda();
        if (contemNumero(periodoPoda)) {
            int dias = Math.max(1, (int) Math.ceil(extrairPrimeiroNumero(periodoPoda, 180)));
            return somarDias(new Date(), dias);
        }

        Date proximaPorMes = calcularProximaPodaPorMes(periodoPoda);
        return proximaPorMes != null ? proximaPorMes : null;
    }

    private Date calcularProximaPodaPorMes(String periodoPoda) {
        List<Integer> meses = extrairMeses(periodoPoda);
        if (meses.isEmpty()) return null;

        Calendar agora = Calendar.getInstance();
        Calendar candidato = Calendar.getInstance();
        candidato.set(Calendar.DAY_OF_MONTH, 1);
        candidato.set(Calendar.HOUR_OF_DAY, 9);
        candidato.set(Calendar.MINUTE, 0);
        candidato.set(Calendar.SECOND, 0);
        candidato.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 24; i++) {
            candidato.set(Calendar.YEAR, agora.get(Calendar.YEAR));
            candidato.set(Calendar.MONTH, agora.get(Calendar.MONTH));
            candidato.add(Calendar.MONTH, i);

            if (meses.contains(candidato.get(Calendar.MONTH)) && candidato.after(agora)) {
                return candidato.getTime();
            }
        }
        return null;
    }

    private List<Integer> extrairMeses(String texto) {
        String normalizado = texto.toLowerCase(Locale.ROOT);
        List<Integer> meses = new ArrayList<>();
        String[][] nomes = {
                {"january", "janeiro"},
                {"february", "fevereiro"},
                {"march", "marco", "marco", "mar."},
                {"april", "abril"},
                {"may", "maio"},
                {"june", "junho"},
                {"july", "julho"},
                {"august", "agosto"},
                {"september", "setembro"},
                {"october", "outubro"},
                {"november", "novembro"},
                {"december", "dezembro"}
        };

        for (int mes = 0; mes < nomes.length; mes++) {
            for (String nome : nomes[mes]) {
                if (normalizado.contains(nome) && !meses.contains(mes)) {
                    meses.add(mes);
                    break;
                }
            }
        }
        return meses;
    }

    private boolean contemNumero(String texto) {
        return texto != null && Pattern.compile("\\d+").matcher(texto).find();
    }

    private double extrairPrimeiroNumero(String texto, double padrao) {
        if (texto == null) return padrao;
        Matcher matcher = Pattern.compile("\\d+(?:[,.]\\d+)?").matcher(texto);
        if (!matcher.find()) return padrao;
        try {
            return Double.parseDouble(matcher.group().replace(",", "."));
        } catch (NumberFormatException ignored) {
            return padrao;
        }
    }

    private Date getDataAcao(Plantio plantio, TipoAcao tipoAcao) {
        switch (tipoAcao) {
            case IRRIGAR:
                return plantio.getPrevisaoProximaIrrigacao();
            case ADUBAR:
                return plantio.getPrevisaoProximaAdubacao();
            case PODAR:
                return plantio.getPrevisaoProximaPoda();
            default:
                return null;
        }
    }

    private void setDataAcao(Plantio plantio, TipoAcao tipoAcao, Date data) {
        switch (tipoAcao) {
            case IRRIGAR:
                plantio.setPrevisaoProximaIrrigacao(data);
                break;
            case ADUBAR:
                plantio.setPrevisaoProximaAdubacao(data);
                break;
            case PODAR:
                plantio.setPrevisaoProximaPoda(data);
                break;
        }
    }

    private Date somarDias(Date base, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(base);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }

    private long arredondarParaCima(long valor, long tamanhoUnidade) {
        return Math.max(1, (valor + tamanhoUnidade - 1) / tamanhoUnidade);
    }

    private void atualizarAviso() {
        int atrasadas = 0;
        for (PlantioHomeItem item : itens) {
            if (item.irrigacao.atrasada || item.adubacao.atrasada || item.poda.atrasada) {
                atrasadas++;
            }
        }

        if (atrasadas > 0) {
            binding.cardAvisos.setVisibility(View.VISIBLE);
            binding.txtAviso.setText(atrasadas == 1
                    ? "1 planta precisa de atencao hoje."
                    : atrasadas + " plantas precisam de atencao hoje.");
        } else {
            binding.cardAvisos.setVisibility(View.GONE);
        }
    }

    private void mostrarCarregando(boolean carregando) {
        binding.progressBarHome.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.recyclerPlantasHome.setVisibility(carregando ? View.GONE : View.VISIBLE);
    }

    private void mostrarEstadoVazio(boolean vazio) {
        binding.layoutVazio.setVisibility(vazio ? View.VISIBLE : View.GONE);
        binding.recyclerPlantasHome.setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    private int getUsuarioId() {
        return requireActivity()
                .getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                .getInt("usuario_id", -1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public enum TipoAcao {
        IRRIGAR,
        ADUBAR,
        PODAR
    }

    public interface OnAcaoListener {
        void onAcaoMarcada(PlantioHomeItem item, TipoAcao tipoAcao);
    }
}
