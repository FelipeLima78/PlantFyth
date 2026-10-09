package com.network.plantfyth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaInformacoesPlanta extends AppCompatActivity {

    private static final String FORMATO_API = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String VAZIO = "—";

    // Status
    private TextView txtNomePlanta, txtFoiRegado, txtDataPlantio, txtRecebeuLuz, txtVaso;
    // Previsões
    private TextView txtProximaIrrigacao, txtHorarioIrrigar, txtProximaPoda, txtProximaAdubacao;
    // Espécie
    private TextView txtNomePopular, txtNomeCientifico, txtFamilia, txtCrescimento, txtCiclo,
            txtTamanhoAdulto, txtVolumeIrrigacao, txtExposicaoLuz, txtTemperaturaIdeal,
            txtUnidadeIrrigacao, txtPeriodoPoda, txtDescricao;

    private Button btnSairInformacao;

    private final RetroFitService retroFitService = new RetroFitService();
    private final PlantFythAPI Ap = retroFitService.getRetrofit().create(PlantFythAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_informacoes_planta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNomePlanta = findViewById(R.id.txtNomePlanta);
        txtFoiRegado = findViewById(R.id.txtFoiRegado);
        txtDataPlantio = findViewById(R.id.txtDataPlantio);
        txtRecebeuLuz = findViewById(R.id.txtRecebeuLuz);
        txtVaso = findViewById(R.id.txtVaso);

        txtProximaIrrigacao = findViewById(R.id.txtProximaIrrigacao);
        txtHorarioIrrigar = findViewById(R.id.txtHorarioIrrigar);
        txtProximaPoda = findViewById(R.id.txtProximaPoda);
        txtProximaAdubacao = findViewById(R.id.txtProximaAdubacao);

        txtNomePopular = findViewById(R.id.txtNomePopular);
        txtNomeCientifico = findViewById(R.id.txtNomeCientifico);
        txtFamilia = findViewById(R.id.txtFamilia);
        txtCrescimento = findViewById(R.id.txtCrescimento);
        txtCiclo = findViewById(R.id.txtCiclo);
        txtTamanhoAdulto = findViewById(R.id.txtTamanhoAdulto);
        txtVolumeIrrigacao = findViewById(R.id.txtVolumeIrrigacao);
        txtExposicaoLuz = findViewById(R.id.txtExposicaoLuz);
        txtTemperaturaIdeal = findViewById(R.id.txtTemperaturaIdeal);
        txtUnidadeIrrigacao = findViewById(R.id.txtUnidadeIrrigacao);
        txtPeriodoPoda = findViewById(R.id.txtPeriodoPoda);
        txtDescricao = findViewById(R.id.txtDescricao);

        btnSairInformacao = findViewById(R.id.btnSairInformacao);

        PuxarDados();
    }

    public void PuxarDados() {
        int plantaId = getIntent().getIntExtra("plantio_id", -1);

        Ap.buscarPlantaPorId(plantaId).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(Call<Plantio> call, Response<Plantio> response) {
                Plantio plantio = response.body();
                if (!response.isSuccessful() || plantio == null) {
                    erro("Planta não encontrada (" + response.code() + ")");
                    return;
                }

                // Status
                txtNomePlanta.setText(textoOuVazio(plantio.getNome()));
                txtFoiRegado.setText(plantio.isFoi_regado_hoje() ? "Sim" : "Não");
                txtDataPlantio.setText(textoOuVazio(plantio.getDataQueFoiPlantado()));
                txtRecebeuLuz.setText(plantio.isPegou_sol_hoje() ? "Sim" : "Não");

                // Previsões
                String previsaoIrrigacao = plantio.getPrevisaoProximaIrrigacao();
                txtProximaIrrigacao.setText(formatarData(previsaoIrrigacao));
                txtHorarioIrrigar.setText(tempoRestante(previsaoIrrigacao));
                txtProximaPoda.setText(formatarData(plantio.getPrevisaoProximaPoda()));

                // txtProximaAdubacao.setText(formatarData(plantio.getPrevisaoProximaAdubacao()));

                carregandoEspecie();
                buscarEspecime(plantio.getEspecimeId());
            }

            @Override
            public void onFailure(Call<Plantio> call, Throwable throwable) {
                erro("Erro: " + throwable.getMessage());
            }
        });
    }

    private void buscarEspecime(int especimeId) {
        Ap.buscarEspecimePorId(especimeId).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(Call<Especime> call, Response<Especime> response) {
                Especime especime = response.body();
                if (!response.isSuccessful() || especime == null) {
                    erro("Espécie não encontrada (" + response.code() + ")");
                    return;
                }
                txtVaso.setText(textoOuVazio(especime.getTroca_de_vaso()));
                txtProximaAdubacao.setText(textoOuVazio(especime.getAdubacao()));
                txtNomePopular.setText(textoOuVazio(especime.getNome_popular()));
                txtNomeCientifico.setText(textoOuVazio(especime.getNome_cientifico()));
                txtFamilia.setText(textoOuVazio(especime.getFamilia()));
                txtCiclo.setText(textoOuVazio(especime.getCiclo()));
                txtCrescimento.setText(textoOuVazio(especime.getCrescimento())); // antes checava getCiclo()

                Float adulto = especime.getTamanho_adulto_cm();
                txtTamanhoAdulto.setText(adulto != null && adulto != 0 ? adulto + " cm" : VAZIO);

                txtExposicaoLuz.setText(textoOuVazio(especime.getExposicao_a_luz()));
                txtUnidadeIrrigacao.setText(textoOuVazio(especime.getUnidade_irrigacao()));
                txtPeriodoPoda.setText(textoOuVazio(especime.getPeriodo_poda()));
                txtDescricao.setText(textoOuVazio(especime.getDescricao()));
                txtVolumeIrrigacao.setText(textoOuVazio(especime.getVolume_irrigacao()));
                txtTemperaturaIdeal.setText(textoOuVazio(especime.getTemperatura()));
            }

            @Override
            public void onFailure(Call<Especime> call, Throwable throwable) {
                limparEspecie();
                erro("Erro: " + throwable.getMessage());
            }
        });
    }


    private void carregandoEspecie() {
        TextView[] campos = {txtNomePopular, txtNomeCientifico, txtFamilia, txtCiclo, txtCrescimento,
                txtTamanhoAdulto, txtVolumeIrrigacao, txtExposicaoLuz, txtTemperaturaIdeal,
                txtUnidadeIrrigacao, txtPeriodoPoda, txtDescricao};
        for (TextView t : campos) t.setText("Buscando dados...");
    }

    private void limparEspecie() {
        TextView[] campos = {txtNomePopular, txtNomeCientifico, txtFamilia, txtCiclo, txtCrescimento,
                txtTamanhoAdulto, txtVolumeIrrigacao, txtExposicaoLuz, txtTemperaturaIdeal,
                txtUnidadeIrrigacao, txtPeriodoPoda, txtDescricao};
        for (TextView t : campos) t.setText(VAZIO);
    }

    private String textoOuVazio(String s) {
        return s != null && !s.isEmpty() ? s : VAZIO;
    }

    private Date parseData(String s) {
        if (s == null) return null;
        try {
            return new SimpleDateFormat(FORMATO_API, Locale.getDefault()).parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    private String formatarData(String s) {
        Date d = parseData(s);
        return d != null ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d) : VAZIO;
    }

    private String tempoRestante(String s) {
        Date d = parseData(s);
        if (d == null) return VAZIO;
        long diffMs = d.getTime() - System.currentTimeMillis();
        if (diffMs < 0) return "Atrasada";
        long horas = diffMs / (1000 * 60 * 60);
        return horas >= 24 ? (horas / 24) + " dias" : horas + "h";
    }

    private void erro(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void SairInformacao(View view) {
        finish();
    }
}