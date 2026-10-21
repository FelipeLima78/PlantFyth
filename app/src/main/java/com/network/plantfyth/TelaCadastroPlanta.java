package com.network.plantfyth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.notifications.PlantNotificationScheduler;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaCadastroPlanta extends AppCompatActivity {

    private Button btnSalvarPlanta;
    private EditText edtNomePlanta, edtDataPlantio, edtEspecie;
    private CheckBox checkFoiRegadoHoje, checkPegouSolHoje;
    private TextView txtVolumeIrrigacao, txtExposicaoLuz;
    private Especime especimeSelecionado;

    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro_planta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSalvarPlanta = findViewById(R.id.btnSalvarPlanta);
        edtNomePlanta = findViewById(R.id.edtNomePlanta);
        edtDataPlantio = findViewById(R.id.edtDataPlantio);
        checkFoiRegadoHoje = findViewById(R.id.checkFoiRegadoHoje);
        checkPegouSolHoje = findViewById(R.id.checkPegouSolHoje);
        txtVolumeIrrigacao = findViewById(R.id.txtVolumeIrrigacao);
        txtExposicaoLuz = findViewById(R.id.txtExposicaoLuz);
        edtEspecie = findViewById(R.id.edtEspecie);
    }
        private final ActivityResultLauncher<Intent> seletorEspecie = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                int id = result.getData().getIntExtra("especime_id", -1);
                if (id != -1) carregarEspecime(id);
            });
    public void AbrirSeletorEspecie(View view) {
        seletorEspecie.launch(new Intent(this, EscolherEspecieActivity.class));
    }
    private void carregarEspecime(int id) {
        Ap.buscarEspecimePorId(id).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(Call<Especime> call, Response<Especime> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(TelaCadastroPlanta.this, "Espécie não encontrada", Toast.LENGTH_SHORT).show();
                    return;
                }
                especimeSelecionado = response.body();
                edtEspecie.setText(EspecieAdapter.nomeExibicao(especimeSelecionado));
                Glossario.aplicar(TelaCadastroPlanta.this, txtVolumeIrrigacao,
                        "Volume de irrigação", especimeSelecionado.getVolume_irrigacao());
                Glossario.aplicar(TelaCadastroPlanta.this, txtExposicaoLuz,
                        "Exposição à luz", especimeSelecionado.getExposicao_a_luz());
            }

            @Override
            public void onFailure(Call<Especime> call, Throwable t) {
                Toast.makeText(TelaCadastroPlanta.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void AbrirCalendario(View view){
        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,(v, anoSelecionado, mesSelecionado, diaSelecionado)-> {
                String data = String.format("%02d/%02d/%04d", diaSelecionado, mesSelecionado + 1, anoSelecionado);
                edtDataPlantio.setText(data);
    }, ano, mes, dia);

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    dialog.show();
    }


    // a execução do especime deve ser feita antes da do save, lembra dissso
    public void CadastrarPlanta(View view) {
        Especime especime = especimeSelecionado;
        if (especime == null) {
            Toast.makeText(this, "Selecione uma espécie", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtNomePlanta.getText().toString().isEmpty()
                || edtDataPlantio.getText().toString().isEmpty()) {

            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Plantio plantio = new Plantio();

        plantio.setNome(edtNomePlanta.getText().toString());
        plantio.setDataQueFoiPlantado(edtDataPlantio.getText().toString());


        int usuarioId = getSharedPreferences(
                "USER_DATA",
                MODE_PRIVATE
        ).getInt("usuario_id", -1);

        plantio.setEspecimeId(especime.getId());
        plantio.setUsuarioId(usuarioId);

        plantio.setFoi_regado_hoje(
                checkFoiRegadoHoje.isChecked()
        );

        plantio.setPegou_sol_hoje(
                checkPegouSolHoje.isChecked()
        );

        Date irrigacao = plantio.isFoi_regado_hoje()
                ? CuidadoCalculator.proximaIrrigacao(especime)
                : new Date();
        plantio.setPrevisaoProximaIrrigacao(CuidadoCalculator.paraBackend(irrigacao));

        plantio.setPrevisaoProximaPoda(CuidadoCalculator.paraBackend(
                CuidadoCalculator.proximaPorPeriodo(especime.getPeriodo_poda())));

        plantio.setPrevisao_proxima_adubacao(CuidadoCalculator.paraBackend(
                CuidadoCalculator.proximaPorPeriodo(especime.getAdubacao())));
        btnSalvarPlanta.setEnabled(false);
        Ap.savePlantio(plantio)
                .enqueue(new Callback<Plantio>() {

                    @Override
                    public void onResponse(
                            Call<Plantio> call,
                            Response<Plantio> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    TelaCadastroPlanta.this,
                                    "Cadastro realizado!",
                                    Toast.LENGTH_SHORT
                            ).show();


                            PlantNotificationScheduler
                                    .refreshPlantCareAlarmsAndNotifyDue(
                                            getApplicationContext(),
                                            usuarioId,
                                            true
                                    );

                            finish();

                        } else {
                            btnSalvarPlanta.setEnabled(true);
                            Toast.makeText(TelaCadastroPlanta.this, "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Plantio> call,
                            Throwable t) {
                        btnSalvarPlanta.setEnabled(false);
                        Toast.makeText(TelaCadastroPlanta.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", t.getMessage());
                    }
                });
    }


}
