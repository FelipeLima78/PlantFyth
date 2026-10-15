package com.network.plantfyth.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.app.AlarmManager;
import android.net.Uri;
import android.provider.Settings;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AlertDialog;

import com.network.plantfyth.TelaCadastroPlanta;
import com.network.plantfyth.TelaInformacoesPlanta;
import com.network.plantfyth.databinding.FragmentHomeBinding;
import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.notifications.PlantCareDailyWorker;
import com.network.plantfyth.notifications.PlantNotificationScheduler;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;
import com.network.plantfyth.CuidadoCalculator;

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
    private FragmentHomeBinding binding;
    private PlantFythAPI api;
    private PlantioHomeAdapter adapter;
    private final List<PlantioHomeItem> itens = new ArrayList<>();
    //private final List<Plantio> ultimasPlantasCarregadas = new ArrayList<>();
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    //private boolean notificarDepoisDaPermissao = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                permitido -> {if (isAdded()) pedirAlarmeExatoSeNecessario();});
    }

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
        adapter = new PlantioHomeAdapter(itens, this::onAcaoMarcada, this::abrirInfo);;
        binding.recyclerPlantasHome.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPlantasHome.setAdapter(adapter);
        binding.fabAdicionarPlanta.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), TelaCadastroPlanta.class)));
    }
    private void abrirInfo(PlantioHomeItem item) {
        Intent intent = new Intent(requireActivity(), TelaInformacoesPlanta.class);
        intent.putExtra("plantio_id", item.plantio.getId());
        startActivity(intent);
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
        PlantCareDailyWorker.scheduleDailyChecks(requireContext(), usuarioId);

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
        verificarNotificacoesDaHome(plantas);
    }

    private PlantioHomeItem criarItem(Plantio plantio) {
        PlantioHomeItem item = new PlantioHomeItem();
        item.plantio = plantio;
        recalcularStatus(item);
        return item;
    }

    private void recalcularStatus(PlantioHomeItem item) {
        Plantio plantio = item.plantio;
        Especime e = plantio.getEspecime();

        item.irrigacao = criarStatus(parseDataPlantio(plantio.getPrevisaoProximaIrrigacao()));

        item.poda = statusCuidado(
                e == null ? null : e.getPeriodo_poda(),
                plantio.getPrevisaoProximaPoda());

        item.adubo = statusCuidado(
                e == null ? null : e.getAdubacao(),
                plantio.getPrevisao_proxima_adubacao());
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
        String dataAnterior = getDataAcao(plantio, tipoAcao);
        String proximaData = calcularProximaData(plantio, tipoAcao);

        if (proximaData == null) {
            Toast.makeText(requireContext(),
                    "Essa acao ainda nao tem previsao cadastrada.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setDataAcao(plantio, tipoAcao, proximaData);

        if (tipoAcao == TipoAcao.IRRIGAR) {
            plantio.setFoi_regado_hoje(true);
        }

        item.salvandoAcao = tipoAcao;
        recalcularStatus(item);
        adapter.notifyItemChanged(posicao);
        atualizarAviso();

        salvarPlantioAtualizado(item, posicao, tipoAcao, dataAnterior, proximaData);
    }

    private void salvarPlantioAtualizado(
            PlantioHomeItem item,
            int posicao,
            TipoAcao tipoAcao,
            String dataAnterior,
            String proximaData
    ) {
        Plantio plantioLocal = item.plantio;

        api.atualizarPlanta(plantioLocal.getId(), plantioLocal).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(@NonNull Call<Plantio> call, @NonNull Response<Plantio> response) {
                if (!isAdded() || binding == null || posicao >= itens.size()) return;

                int pos = itens.indexOf(item);
                if (pos == -1) return;
                PlantioHomeItem itemAtual = item;
                itemAtual.salvandoAcao = null;

                if (response.isSuccessful() && response.body() != null) {
                    Plantio atualizado = response.body();
                    preservarDataNaoRetornada(atualizado, tipoAcao, proximaData);

                    itemAtual.plantio = atualizado;
                    PlantNotificationScheduler.schedulePlantCareAlarms(requireContext(), atualizado);
                    recalcularStatus(itemAtual);
                    adapter.notifyItemChanged(pos);
                    atualizarAviso();

                    Toast.makeText(requireContext(), "Acao registrada.", Toast.LENGTH_SHORT).show();
                } else {
                    setDataAcao(itemAtual.plantio, tipoAcao, dataAnterior);
                    recalcularStatus(itemAtual);
                    adapter.notifyItemChanged(pos);
                    atualizarAviso();

                    Toast.makeText(requireContext(),
                            "Nao foi possivel salvar: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Plantio> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null || posicao >= itens.size()) return;

                int pos = itens.indexOf(item);
                if (pos == -1) return;
                PlantioHomeItem itemAtual = item;
                itemAtual.salvandoAcao = null;

                setDataAcao(itemAtual.plantio, tipoAcao, dataAnterior);
                recalcularStatus(itemAtual);
                adapter.notifyItemChanged(posicao);
                atualizarAviso();

                Toast.makeText(requireContext(),
                        "Falha ao salvar: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void preservarDataNaoRetornada(Plantio atualizado, TipoAcao tipoAcao, String proximaData) {
        if (tipoAcao == TipoAcao.IRRIGAR && atualizado.getPrevisaoProximaIrrigacao() == null) {
            atualizado.setPrevisaoProximaIrrigacao(proximaData);
        } else if (tipoAcao == TipoAcao.PODAR && atualizado.getPrevisaoProximaPoda() == null) {
            atualizado.setPrevisaoProximaPoda(proximaData);
        } else if (tipoAcao == TipoAcao.ADUBAR && atualizado.getPrevisao_proxima_adubacao() == null) {
            atualizado.setPrevisao_proxima_adubacao(proximaData);
        }
    }

    private String calcularProximaData(Plantio plantio, TipoAcao tipoAcao) {
        Especime especime = plantio.getEspecime();

        switch (tipoAcao) {
            case IRRIGAR:
                return formatarDataParaBackend(calcularProximaIrrigacao(especime));

            case PODAR:
                return formatarDataParaBackend(CuidadoCalculator.proximaPorPeriodo(especime == null ? null : especime.getPeriodo_poda()));

            case ADUBAR:
                return formatarDataParaBackend(CuidadoCalculator.proximaPorPeriodo(especime == null ? null : especime.getAdubacao()));
            default: return null;
        }
    }

    private Date calcularProximaIrrigacao(Especime especime) {
        if (especime == null) return somarDias(new Date(), 1);

        Double periodo = extrairPrimeiroNumero(String.valueOf(especime.getPeriodo_irrigacao()), 1);
        String unidade = especime.getUnidade_irrigacao();
        Calendar calendar = Calendar.getInstance();

        if (unidade != null && unidade.toLowerCase(Locale.ROOT).contains("horas")) {
            calendar.add(Calendar.HOUR_OF_DAY, Math.max(1, (int) Math.ceil(periodo)));
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, Math.max(1, (int) Math.ceil(periodo)));
        }

        return calendar.getTime();
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


    private String getDataAcao(Plantio plantio, TipoAcao tipoAcao) {
        switch (tipoAcao) {
            case IRRIGAR:
                return plantio.getPrevisaoProximaIrrigacao();

            case PODAR:
                return plantio.getPrevisaoProximaPoda();

            case ADUBAR:
                return plantio.getPrevisao_proxima_adubacao();
            default:
                return null;
        }
    }

    private void setDataAcao(Plantio plantio, TipoAcao tipoAcao, String data) {
        switch (tipoAcao) {
            case IRRIGAR:
                plantio.setPrevisaoProximaIrrigacao(data);
                break;

            case PODAR:
                plantio.setPrevisaoProximaPoda(data);
                break;

            case ADUBAR:
                plantio.setPrevisao_proxima_adubacao(data);
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
            if (item.irrigacao.atrasada || item.poda.atrasada || item.adubo.atrasada) {
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

    /*private void verificarNotificacoesDaHome(List<Plantio> plantas) {
        if (!isAdded()) return;

        ultimasPlantasCarregadas.clear();
        ultimasPlantasCarregadas.addAll(plantas);

        PlantNotificationScheduler.schedulePlantCareAlarms(requireContext(), plantas);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            notificarDepoisDaPermissao = true;
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            return;
        }

        notificarDepoisDaPermissao = false;
        PlantNotificationScheduler.notifyDuePlants(requireContext(), plantas);
    } */

    private void verificarNotificacoesDaHome(List<Plantio> plantas) {
        if (!isAdded()) return;

        PlantNotificationScheduler.schedulePlantCareAlarms(requireContext(), plantas);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            return;
        }
        pedirAlarmeExatoSeNecessario();
    }

    private Date parseDataPlantio(String dataTexto) {
        if (dataTexto == null || dataTexto.trim().isEmpty()) return null;

        String[] formatos = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "dd/MM/yyyy"
        };

        for (String formatoTexto : formatos) {
            try {
                SimpleDateFormat formato = new SimpleDateFormat(formatoTexto, Locale.US);
                formato.setLenient(false);
                return formato.parse(dataTexto);
            } catch (ParseException ignored) {
            }
        }

        return null;
    }
    private void mostrarCarregando(boolean carregando) {
        binding.progressBarHome.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.recyclerPlantasHome.setVisibility(carregando ? View.GONE : View.VISIBLE);
    }
    private String formatarDataParaBackend(Date data) {
        if (data == null) return null;

        SimpleDateFormat formato = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                Locale.US
        );

        return formato.format(data);
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
    public interface OnInfoListener {
        void onAbrirInfo(PlantioHomeItem item);
    }

    private PlantioHomeItem.AcaoStatus statusCuidado(String periodo, String dataTexto) {
        if (CuidadoCalculator.naoPrecisa(periodo)) {
            return PlantioHomeItem.AcaoStatus.naoNecessario();
        }
        if (CuidadoCalculator.proximaPorPeriodo(periodo) == null) {
            return PlantioHomeItem.AcaoStatus.consultarInfo();
        }
        Date data = parseDataPlantio(dataTexto);
        if (data == null) {
            return new PlantioHomeItem.AcaoStatus(
                    "Sem registro", PlantioHomeItem.COR_HOJE, false, true);
        }
        return criarStatus(data);
    }

    //  notificação alarme
    private void pedirAlarmeExatoSeNecessario() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return;

        AlarmManager am = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (am == null || am.canScheduleExactAlarms()) return;

        android.content.SharedPreferences prefs =
                requireContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        if (prefs.getBoolean("alarme_exato_perguntado", false)) return;
        prefs.edit().putBoolean("alarme_exato_perguntado", true).apply();

        new AlertDialog.Builder(requireContext())
                .setTitle("Lembretes no horário certo")
                .setMessage("Para avisar na hora de cuidar das plantas, permita 'Alarmes e lembretes'.")
                .setPositiveButton("Permitir", (d, w) -> startActivity(new Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + requireContext().getPackageName()))))
                .setNegativeButton("Agora não", null)
                .show();
    }
}
