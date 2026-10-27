package com.network.plantfyth.ui.home;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.network.plantfyth.CuidadoCalculator;
import com.network.plantfyth.TelaCadastroPlanta;
import com.network.plantfyth.TelaInformacoesPlanta;
import com.network.plantfyth.databinding.FragmentHomeBinding;
import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.notifications.PlantCareDailyWorker;
import com.network.plantfyth.notifications.PlantNotificationScheduler;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private PlantFythAPI api;
    private PlantioHomeAdapter adapter;
    private final List<PlantioHomeItem> itens = new ArrayList<>();
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                permitido -> {
                    if (isAdded()) {
                        pedirAlarmeExatoSeNecessario();
                        notificarSolPendenteDaLista();
                    }
                });
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
        adapter = new PlantioHomeAdapter(itens, this::onAcaoMarcada, this::abrirInfo);
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

    // ------------------------------------------------------------- carregar

    private void carregarPlantas() {
        int usuarioId = getUsuarioId();
        if (usuarioId == -1) {
            mostrarCarregando(false);
            mostrarEstadoVazio(true);
            Toast.makeText(requireContext(), "Usuário não identificado.", Toast.LENGTH_SHORT).show();
            return;
        }
        PlantCareDailyWorker.scheduleDailyChecks(requireContext(), usuarioId);

        // só mostra a barra na primeira carga; senão a lista pisca toda vez que a tela volta
        if (itens.isEmpty()) {
            mostrarCarregando(true);
        }

        api.buscarPlantasUsuario(usuarioId).enqueue(new Callback<List<Plantio>>() {
            @Override
            public void onResponse(@NonNull Call<List<Plantio>> call,
                                   @NonNull Response<List<Plantio>> response) {
                if (!isAdded() || binding == null) return;
                mostrarCarregando(false);

                if (response.isSuccessful() && response.body() != null) {
                    atualizarLista(response.body());
                } else {
                    Log.e(TAG, "Falha ao carregar plantas: HTTP " + response.code());
                    Toast.makeText(requireContext(),
                            "Não foi possível carregar as plantas.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Plantio>> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null) return;
                mostrarCarregando(false);
                Log.e(TAG, "Falha ao carregar plantas", t);
                Toast.makeText(requireContext(),
                        "Sem conexão com o servidor. Verifique a internet.",
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
        PlantNotificationScheduler.notifySunPending(requireContext(), plantas);
    }

    private void notificarSolPendenteDaLista() {
        if (!isAdded()) return;
        List<Plantio> plantas = new ArrayList<>();
        for (PlantioHomeItem item : itens) plantas.add(item.plantio);
        PlantNotificationScheduler.notifySunPending(requireContext(), plantas);
    }

    private PlantioHomeItem criarItem(Plantio plantio) {
        PlantioHomeItem item = new PlantioHomeItem();
        item.plantio = plantio;
        recalcularStatus(item);
        return item;
    }

    // --------------------------------------------------------------- status

    private void recalcularStatus(PlantioHomeItem item) {
        Plantio plantio = item.plantio;
        Especime e = plantio.getEspecime();

        item.irrigacao = statusIrrigacao(parseDataPlantio(plantio.getPrevisaoProximaIrrigacao()));

        item.poda = statusCuidado(
                e == null ? null : e.getPeriodo_poda(),
                plantio.getPrevisaoProximaPoda());

        item.adubo = statusCuidado(
                e == null ? null : e.getAdubacao(),
                plantio.getPrevisao_proxima_adubacao());

        item.sol = criarStatusSol(plantio.isPegou_sol_hoje());
    }

    private PlantioHomeItem.AcaoStatus criarStatusSol(boolean pegou) {
        return pegou
                ? new PlantioHomeItem.AcaoStatus("Tomou sol hoje", PlantioHomeItem.COR_OK, false, false)
                : new PlantioHomeItem.AcaoStatus("Sol pendente", PlantioHomeItem.COR_HOJE, false, true);
    }

    /** Poda e adubação: contagem regressiva e, vencido, "Atrasado". */
    private PlantioHomeItem.AcaoStatus criarStatus(Date proximaData) {
        if (proximaData == null) {
            return new PlantioHomeItem.AcaoStatus(
                    "Não definido", PlantioHomeItem.COR_INDISPONIVEL, false, false);
        }

        long diffMs = proximaData.getTime() - System.currentTimeMillis();
        if (diffMs <= 0) {
            return new PlantioHomeItem.AcaoStatus(
                    textoAtraso(-diffMs), PlantioHomeItem.COR_ATRASADO, true, true);
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

    /**
     * Rega: a fonte orienta regar pela necessidade do solo, não pelo calendário.
     * Vencido, o texto pede para CONFERIR o solo (laranja), em vez de acusar atraso.
     */
    private PlantioHomeItem.AcaoStatus statusIrrigacao(Date proximaData) {
        if (proximaData == null) return criarStatus(null);

        long diffMs = proximaData.getTime() - System.currentTimeMillis();
        if (diffMs > 0) return criarStatus(proximaData);

        long atrasoMs = -diffMs;
        String texto = TimeUnit.MILLISECONDS.toHours(atrasoMs) < 24
                ? "Conferir solo"
                : "Conferir há " + duracaoCurta(atrasoMs);
        // atrasada = true para a rega continuar contando no card de aviso do topo
        return new PlantioHomeItem.AcaoStatus(texto, PlantioHomeItem.COR_HOJE, true, true);
    }

    private String textoAtraso(long atrasoMs) {
        if (TimeUnit.MILLISECONDS.toHours(atrasoMs) < 24) return "Atrasado hoje";
        return "Atraso: " + duracaoCurta(atrasoMs);
    }

    private String duracaoCurta(long ms) {
        long dias = TimeUnit.MILLISECONDS.toDays(ms);
        if (dias < 7) return dias + "d";
        if (dias < 30) return (dias / 7) + " sem";
        return "30d+";
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

    // ---------------------------------------------------------------- ações

    private void onAcaoMarcada(PlantioHomeItem item, TipoAcao tipoAcao) {
        int posicao = itens.indexOf(item);
        if (posicao == -1 || item.salvandoAcao != null) return;

        Plantio plantio = item.plantio;
        boolean regadoAnterior = plantio.isFoi_regado_hoje();
        String dataAnterior = getDataAcao(plantio, tipoAcao);

        if (tipoAcao == TipoAcao.SOL) {
            if (plantio.isPegou_sol_hoje()) return;
            plantio.setPegou_sol_hoje(true);
            iniciarSalvamento(item, posicao, tipoAcao, dataAnterior, regadoAnterior, null);
            return;
        }

        String proximaData = calcularProximaData(plantio, tipoAcao);
        if (proximaData == null) {
            Toast.makeText(requireContext(),
                    "Esta espécie não tem previsão cadastrada para esse cuidado.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setDataAcao(plantio, tipoAcao, proximaData);
        if (tipoAcao == TipoAcao.IRRIGAR) {
            plantio.setFoi_regado_hoje(true);
        }

        iniciarSalvamento(item, posicao, tipoAcao, dataAnterior, regadoAnterior, proximaData);
    }

    private void iniciarSalvamento(PlantioHomeItem item, int posicao, TipoAcao tipoAcao,
                                   String dataAnterior, boolean regadoAnterior,
                                   String proximaData) {
        item.salvandoAcao = tipoAcao;
        recalcularStatus(item);
        adapter.notifyItemChanged(posicao);
        atualizarAviso();

        salvarPlantioAtualizado(item, tipoAcao, dataAnterior, regadoAnterior, proximaData);
    }

    /** Desfaz a alteração local se o servidor não confirmar. */
    private void desfazerAcao(Plantio p, TipoAcao tipo, String dataAnterior, boolean regadoAnterior) {
        if (tipo == TipoAcao.SOL) {
            p.setPegou_sol_hoje(false);
            return;
        }
        setDataAcao(p, tipo, dataAnterior);
        if (tipo == TipoAcao.IRRIGAR) {
            p.setFoi_regado_hoje(regadoAnterior);
        }
    }

    private void salvarPlantioAtualizado(PlantioHomeItem item, TipoAcao tipoAcao,
                                         String dataAnterior, boolean regadoAnterior,
                                         String proximaData) {
        Plantio plantioLocal = item.plantio;

        api.atualizarPlanta(plantioLocal.getId(), plantioLocal).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(@NonNull Call<Plantio> call, @NonNull Response<Plantio> response) {
                if (!isAdded() || binding == null) return;

                int pos = itens.indexOf(item);
                if (pos == -1) return;
                item.salvandoAcao = null;

                if (response.isSuccessful() && response.body() != null) {
                    Plantio atualizado = response.body();
                    preservarDataNaoRetornada(atualizado, tipoAcao, proximaData);
                    if (atualizado.getEspecime() == null) {
                        atualizado.setEspecime(plantioLocal.getEspecime());
                    }
                    item.plantio = atualizado;
                    PlantNotificationScheduler.schedulePlantCareAlarms(requireContext(), atualizado);
                    notificarSolPendenteDaLista();

                    Toast.makeText(requireContext(), "Ação registrada.", Toast.LENGTH_SHORT).show();
                } else {
                    desfazerAcao(plantioLocal, tipoAcao, dataAnterior, regadoAnterior);
                    Log.e(TAG, "PUT falhou: HTTP " + response.code());
                    Toast.makeText(requireContext(),
                            "Não foi possível salvar. Tente novamente.",
                            Toast.LENGTH_SHORT).show();
                }

                recalcularStatus(item);
                adapter.notifyItemChanged(pos);
                atualizarAviso();
            }

            @Override
            public void onFailure(@NonNull Call<Plantio> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null) return;

                int pos = itens.indexOf(item);
                if (pos == -1) return;
                item.salvandoAcao = null;

                desfazerAcao(plantioLocal, tipoAcao, dataAnterior, regadoAnterior);
                Log.e(TAG, "Falha de rede ao salvar", t);

                recalcularStatus(item);
                adapter.notifyItemChanged(pos);
                atualizarAviso();

                Toast.makeText(requireContext(),
                        "Sem conexão com o servidor. Verifique a internet.",
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
                return CuidadoCalculator.paraBackend(CuidadoCalculator.proximaIrrigacao(especime));

            case PODAR:
                return CuidadoCalculator.paraBackend(CuidadoCalculator.proximaPorPeriodo(
                        especime == null ? null : especime.getPeriodo_poda()));

            case ADUBAR:
                return CuidadoCalculator.paraBackend(CuidadoCalculator.proximaPorPeriodo(
                        especime == null ? null : especime.getAdubacao()));

            default:
                return null;
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
            default:
                break;
        }
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
                    ? "1 planta precisa de atenção hoje."
                    : atrasadas + " plantas precisam de atenção hoje.");
        } else {
            binding.cardAvisos.setVisibility(View.GONE);
        }
    }

    // --------------------------------------------------------- notificações

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
                .setMessage("Para avisar na hora de conferir suas plantas, permita “Alarmes e lembretes”.")
                .setPositiveButton("Permitir", (d, w) -> startActivity(new Intent(
                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + requireContext().getPackageName()))))
                .setNegativeButton("Agora não", null)
                .show();
    }

    // ---------------------------------------------------------------- datas

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

    // ------------------------------------------------------------ visibilidade

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
        PODAR,
        SOL
    }

    public interface OnAcaoListener {
        void onAcaoMarcada(PlantioHomeItem item, TipoAcao tipoAcao);
    }

    public interface OnInfoListener {
        void onAbrirInfo(PlantioHomeItem item);
    }
}
