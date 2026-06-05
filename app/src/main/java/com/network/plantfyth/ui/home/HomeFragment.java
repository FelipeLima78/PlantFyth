package com.network.plantfyth.ui.home;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.R;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;
import com.network.plantfyth.ui.home.PlantioHomeItem;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // ------------------------------------------------------------
    // Views
    // ------------------------------------------------------------
    private RecyclerView recyclerView;
    private PlantioHomeAdapter adapter;

    // ------------------------------------------------------------
    // Retrofit
    // ------------------------------------------------------------
    private PlantFythAPI api;

    // ------------------------------------------------------------
    // Dados
    // ------------------------------------------------------------
    private final List<PlantioHomeItem> itens = new ArrayList<>();

    // ------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetroFitService service = new RetroFitService();
        api = service.getRetrofit().create(PlantFythAPI.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Infla o layout do fragment (fragment_home.xml — veja o arquivo de layout fornecido)
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerPlantasHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PlantioHomeAdapter(itens, this::onAcaoMarcada);
        recyclerView.setAdapter(adapter);
    }

    // Sempre que o fragment ficar visível (inclusive ao voltar de outra tela),
    // recarrega as plantas do usuário — isso resolve o requisito de atualização automática.
    @Override
    public void onResume() {
        super.onResume();
        carregarPlantas();
    }

    // ------------------------------------------------------------
    // Carregamento de dados
    // ------------------------------------------------------------

    private void carregarPlantas() {
        int usuarioId = getUsuarioId();
        if (usuarioId == -1) {
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("HOME", "Usuario ID = " + usuarioId);
        api.buscarPlantasUsuario(usuarioId).enqueue(new Callback<List<Plantio>>() {
            @Override
            public void onResponse(Call<List<Plantio>> call, Response<List<Plantio>> response) {
                if (!isAdded()) return; // Fragment pode ter sido desanexado

                if (response.isSuccessful() && response.body() != null) {
                    processarPlantas(response.body());
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao buscar plantas: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Plantio>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "Falha ao buscar plantas", t);
                Toast.makeText(requireContext(),
                        "Falha de conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Converte cada Plantio recebido da API em um PlantioHomeItem,
     * calculando os dias/horas restantes para cada ação.
     */
    private void processarPlantas(List<Plantio> plantas) {
        itens.clear();

        for (Plantio p : plantas) {
            PlantioHomeItem item = new PlantioHomeItem();
            item.plantio = p;

            // --- Irrigação ---
            item.diasParaIrrigar  = calcularDiasRestantes(p.getPrevisaoProximaIrrigacao());

            // --- Adubação ---
            item.diasParaAdubar   = calcularDiasRestantes(p.getPrevisaoProximaAdubacao());

            // --- Poda ---
            item.diasParaPodar    = calcularDiasRestantes(p.getPrevisaoProximaPoda());

            itens.add(item);
        }

        adapter.notifyDataSetChanged();
    }

    // ------------------------------------------------------------
    // Callback dos botões de ação (irrigar / adubar / podar)
    // ------------------------------------------------------------

    /**
     * Chamado pelo adapter quando o usuário marca uma ação como feita.
     *
     * @param posicao  posição do item na lista
     * @param tipoAcao TipoAcao.IRRIGAR | ADUBAR | PODAR
     */
    private void onAcaoMarcada(int posicao, TipoAcao tipoAcao) {
        if (posicao < 0 || posicao >= itens.size()) return;

        PlantioHomeItem item   = itens.get(posicao);
        Plantio         planta = item.plantio;

        // Busca o espécime para saber o período da ação e calcular a próxima data
        // O período já está dentro do Plantio via FK (especime).
        if (planta.getEspecime() == null) {
            Toast.makeText(requireContext(),
                    "Espécime não carregado, não foi possível recalcular",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Date hoje = new Date();

        switch (tipoAcao) {
            case IRRIGAR: {
                int periodo = planta.getEspecime().getPeriodoIrrigacao(); // dias
                Date proxima = somarDias(hoje, periodo);
                planta.setPrevisaoProximaIrrigacao(proxima);
                item.diasParaIrrigar = calcularDiasRestantes(proxima);
                break;
            }
            case ADUBAR: {
                int periodo = planta.getEspecime().getPeriodoAdubacao();
                Date proxima = somarDias(hoje, periodo);
                planta.setPrevisaoProximaAdubacao(proxima);
                item.diasParaAdubar = calcularDiasRestantes(proxima);
                break;
            }
            case PODAR: {
                int periodo = planta.getEspecime().getPeriodoPoda();
                Date proxima = somarDias(hoje, periodo);
                planta.setPrevisaoProximaPoda(proxima);
                item.diasParaPodar = calcularDiasRestantes(proxima);
                break;
            }
        }

        // Persiste na API (PUT /plantios/id/{id})
        salvarPlantioAtualizado(planta, posicao);
    }

    /**
     * Envia o Plantio atualizado para a API e, na resposta, atualiza apenas
     * o item afetado no RecyclerView — sem recarregar a lista toda.
     */
    private void salvarPlantioAtualizado(Plantio planta, int posicao) {
        Log.d("HOME", "Plantio ID = " + planta.getId());

        api.atualizarPlantio(planta, planta.getId()).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(Call<Plantio> call, Response<Plantio> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    // Atualiza localmente com a resposta do servidor
                    PlantioHomeItem item = itens.get(posicao);
                    Plantio atualizado = response.body();

                    // Re-calcula os dias com base no que o servidor retornou
                    item.plantio             = atualizado;
                    item.diasParaIrrigar     = calcularDiasRestantes(atualizado.getPrevisaoProximaIrrigacao());
                    item.diasParaAdubar      = calcularDiasRestantes(atualizado.getPrevisaoProximaAdubacao());
                    item.diasParaPodar       = calcularDiasRestantes(atualizado.getPrevisaoProximaPoda());

                    // Notifica apenas a linha alterada — animação suave
                    adapter.notifyItemChanged(posicao);

                    Toast.makeText(requireContext(), "✓ Atualizado!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao salvar: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Plantio> call, Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "Falha ao salvar plantio", t);
                Toast.makeText(requireContext(),
                        "Falha ao salvar: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ------------------------------------------------------------
    // Utilitários de data
    // ------------------------------------------------------------

    /**
     * Calcula quantos dias faltam entre hoje e uma data futura.
     * Retorna valor negativo se a data já passou (planta atrasada).
     * Aceita tanto java.util.Date quanto String no formato "dd/MM/yyyy".
     */
    public static long calcularDiasRestantes(Object dataObj) {
        if (dataObj == null) return Long.MAX_VALUE;

        Date dataAlvo = null;

        if (dataObj instanceof Date) {
            dataAlvo = (Date) dataObj;
        } else if (dataObj instanceof String) {
            String[] formatos = {"dd/MM/yyyy", "yyyy-MM-dd"};
            for (String fmt : formatos) {
                try {
                    dataAlvo = new SimpleDateFormat(fmt, Locale.getDefault()).parse((String) dataObj);
                    break;
                } catch (ParseException ignored) { }
            }
        }

        if (dataAlvo == null) return Long.MAX_VALUE;

        long diffMs = dataAlvo.getTime() - truncarParaDia(new Date()).getTime();
        return TimeUnit.MILLISECONDS.toDays(diffMs);
    }

    /** Zera horas/minutos/segundos para comparação apenas por dia. */
    private static Date truncarParaDia(Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /** Soma N dias a uma data e retorna o resultado. */
    private static Date somarDias(Date base, int dias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DAY_OF_YEAR, dias);
        return cal.getTime();
    }

    // ------------------------------------------------------------
    // SharedPreferences
    // ------------------------------------------------------------

    private int getUsuarioId() {
        return requireActivity()
                .getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                .getInt("usuario_id", -1);
    }

    // ------------------------------------------------------------
    // Enum interno: tipos de ação
    // ------------------------------------------------------------

    public enum TipoAcao {
        IRRIGAR, ADUBAR, PODAR
    }


    // ------------------------------------------------------------
    // Interface de callback para o Adapter
    // ------------------------------------------------------------

    public interface OnAcaoListener {
        void onAcaoMarcada(int posicao, TipoAcao tipoAcao);
    }
}