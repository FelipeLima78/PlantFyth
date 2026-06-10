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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaInformacoesPlanta extends AppCompatActivity {
    private TextView txtNomePlanta, txtFoiRegado, txtDataPlantio, txtPlantadaComo, txtTamanhoAtual, txtPrevisaoTamanho, txtProximaIrrigacao, txtHorarioIrrigar, txtProximaPoda, txtNomePopular, txtNomeCientifico, txtFamilia, txtCiclo, txtCrescimento,  txtTamanhoAdulto,  txtExposicaoLuz, txtPeriodoIrrigacao, txtUnidadeIrrigacao, txtPeriodoPoda, txtDescricao;
    RetroFitService retroFitService = new RetroFitService();
    private Button btnSairInformacao;
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);
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
        txtPlantadaComo = findViewById(R.id.txtPlantadaComo);
        txtTamanhoAtual = findViewById(R.id.txtTamanhoAtual);
      //  txtPrevisaoTamanho = findViewById(R.id.txtPrevisaoTamanho);
        txtProximaIrrigacao = findViewById(R.id.txtProximaIrrigacao);
        txtHorarioIrrigar = findViewById(R.id.txtHorarioIrrigar);
        txtProximaPoda = findViewById(R.id.txtProximaPoda);
        txtNomePopular = findViewById(R.id.txtNomePopular);
        txtNomeCientifico = findViewById(R.id.txtNomeCientifico);
        txtFamilia = findViewById(R.id.txtFamilia);
        txtCiclo = findViewById(R.id.txtCiclo);
        txtCrescimento = findViewById(R.id.txtCrescimento);
        txtTamanhoAdulto = findViewById(R.id.txtTamanhoAdulto);
        txtExposicaoLuz = findViewById(R.id.txtExposicaoLuz);
        txtPeriodoIrrigacao = findViewById(R.id.txtPeriodoIrrigacao);
        txtUnidadeIrrigacao = findViewById(R.id.txtUnidadeIrrigacao);
        txtPeriodoPoda = findViewById(R.id.txtPeriodoPoda);
        txtDescricao = findViewById(R.id.txtDescricao);
        btnSairInformacao = findViewById(R.id.btnSairInformacao);
        PuxarDados();
    }

    public void PuxarDados(){
        int plantaId = getIntent().getIntExtra("plantio_id", -1);
        Ap.buscarPlantaPorId(plantaId).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(Call<Plantio> call, Response<Plantio> response) {
                Plantio plantio = response.body();
                txtNomePlanta.setText(plantio.getNome());
                if (plantio.isFoi_regado_hoje()) {
                    txtFoiRegado.setText("Sim");
                } else {
                    txtFoiRegado.setText("Não");
                }

                txtDataPlantio.setText(plantio.getDataQueFoiPlantado());
                txtPlantadaComo.setText(plantio.getPlantadaComo() != null ? plantio.getPlantadaComo(): "-");
                float tamanho = plantio.getTamanhoAtualCM();
                txtTamanhoAtual.setText(tamanho != 0 ? tamanho + " cm" : "Não registrado");
                String previsaoStr = plantio.getPrevisaoProximaIrrigacao();
                if (previsaoStr != null) {
                    try {
                        SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat sdfExibir = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date previsao = sdfParse.parse(previsaoStr);
                        txtProximaIrrigacao.setText(sdfExibir.format(previsao));
                    } catch (ParseException e) {
                        txtProximaIrrigacao.setText("—");
                    }
                }
                String horarioStr = plantio.getPrevisaoProximaIrrigacao();
                if (horarioStr != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        Date previsao = sdf.parse(horarioStr);
                        Date agora = new Date();
                        long diferencaMs = previsao.getTime() - agora.getTime();
                        long horasRestantes = diferencaMs / (1000 * 60 * 60);

                        if (horasRestantes >= 24) {
                            long dias = horasRestantes / 24;
                            txtHorarioIrrigar.setText(dias + " dias");
                        } else {
                            txtHorarioIrrigar.setText(horasRestantes + "h");
                        }
                    } catch (ParseException e) {
                        txtHorarioIrrigar.setText("—");
                    }
                }
                String previsaoPoda = plantio.getPrevisaoProximaPoda();
                if (previsaoPoda != null) {
                    try {
                        SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat sdfExibir = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date previsao = sdfParse.parse(previsaoPoda);
                        txtProximaPoda.setText(sdfExibir.format(previsao));
                    } catch (ParseException e) {
                        txtProximaPoda.setText("—");
                    }
                }
                //por conta do delay
                Toast.makeText(TelaInformacoesPlanta.this, "Buscando dados...",Toast.LENGTH_SHORT).show();
                txtNomePopular.setText("Buscando dados...");
                txtNomeCientifico.setText("Buscando dados...");
                txtFamilia.setText("Buscando dados...");
                txtCiclo.setText("Buscando dados...");
                txtCrescimento.setText("Buscando dados...");
                txtTamanhoAdulto.setText("Buscando dados...");
                txtExposicaoLuz.setText("Buscando dados...");
                txtPeriodoIrrigacao.setText("Buscando dados...");
                txtUnidadeIrrigacao.setText("Buscando dados...");
                txtPeriodoPoda.setText("Buscando dados...");
                txtDescricao.setText("Buscando dados...");
                Ap.buscarEspecimePorId(plantio.getEspecimeId()).enqueue(new Callback<Especime>() {
                    @Override
                    public void onResponse(Call<Especime> call, Response<Especime> response) {
                        Especime especime = response.body();
                        // txtNomePopular, txtNomeCientifico, txtFamilia, txtCiclo, txtCrescimento, txtCrescimentoDiario,
                        // txtTamanhoAdulto, txtTamanhoMuda, txtExposicaoLuz, txtPeriodoIrrigacao, txtUnidadeIrrigacao, txtPeriodoPoda, txtDescricao
                        txtNomePopular.setText(especime.getNome_popular()!= null ? especime.getNome_popular() : "-");
                        txtNomeCientifico.setText(especime.getNome_cientifico()!= null ? especime.getNome_cientifico() : "-");
                        txtFamilia.setText(especime.getFamilia()!= null ? especime.getFamilia() : "-");
                        txtCiclo.setText(especime.getCiclo()!= null ? especime.getCiclo() : "-");
                        txtCrescimento.setText(especime.getCiclo()!= null ? especime.getCrescimento() : "-");
                        float adulto = especime.getTamanho_adulto_cm() != null ? especime.getTamanho_adulto_cm() : 0;
                        txtTamanhoAdulto.setText(adulto != 0 ? adulto + " cm" : "-");
                        txtExposicaoLuz.setText(especime.getExposicao_A_Luz()!= null ? especime.getExposicao_A_Luz() : "-");
                        txtPeriodoIrrigacao.setText(especime.getPeriodo_irrigacao()!= null ? especime.getPeriodo_irrigacao() : "-");
                        txtUnidadeIrrigacao.setText(especime.getUnidade_irrigacao()!= null ? especime.getUnidade_irrigacao(): "-");
                        txtPeriodoPoda.setText(especime.getPeriodo_poda()!= null ? especime.getPeriodo_poda() : "-");
                        txtDescricao.setText(especime.getDescricao()!= null ? especime.getDescricao() : "-");
                    }

                    @Override
                    public void onFailure(Call<Especime> call, Throwable throwable) {
                        Toast.makeText(TelaInformacoesPlanta.this, "Erro: "+throwable, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Call<Plantio> call, Throwable throwable) {
                Toast.makeText(TelaInformacoesPlanta.this, "Erro: " + throwable, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void SairInformacao(View view){
        finish();
    }

}