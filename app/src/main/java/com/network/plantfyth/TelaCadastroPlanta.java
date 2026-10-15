package com.network.plantfyth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.model.Usuario;
import com.network.plantfyth.notifications.NotificationPermissionHelper;
import com.network.plantfyth.notifications.PlantNotificationScheduler;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;
import com.network.plantfyth.ui.dashboard.DashboardFragment;
import com.network.plantfyth.ui.home.HomeFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaCadastroPlanta extends AppCompatActivity {

    private Button btnSalvarPlanta;
    private EditText edtNomePlanta, edtDataPlantio;
    private Spinner spinnerEspecime;
    private RadioGroup radioPlantadaComo;
    private RadioButton rbSemente, rbMuda, rbEstaca;
    private CheckBox checkFoiRegadoHoje, checkPegouSolHoje;
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);
    private List<Especime> listaEspecimes;
    private String plantadoComo;
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
        spinnerEspecime = findViewById(R.id.spinnerEspecime);
        checkFoiRegadoHoje = findViewById(R.id.checkFoiRegadoHoje);
        checkPegouSolHoje = findViewById(R.id.checkPegouSolHoje);
        carregarEspecimes();
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

        Especime especime = (Especime) spinnerEspecime.getSelectedItem();

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

                            NotificationPermissionHelper.requestIfNeeded(
                                    TelaCadastroPlanta.this
                            );

                            PlantNotificationScheduler
                                    .refreshPlantCareAlarmsAndNotifyDue(
                                            getApplicationContext(),
                                            usuarioId,
                                            true
                                    );

                            finish();

                        } else {

                            Toast.makeText(
                                    TelaCadastroPlanta.this,
                                    "Erro: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Plantio> call,
                            Throwable t) {

                        Toast.makeText(
                                TelaCadastroPlanta.this,
                                "Falha: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        Log.e("API_ERROR", t.getMessage());
                    }
                });
    }

    private void carregarEspecimes() {
        Ap.listarEspecimes().enqueue(new Callback<List<Especime>>() {
            @Override
            public void onResponse(Call<List<Especime>> call, Response<List<Especime>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaEspecimes = response.body();
                    ArrayAdapter<Especime> adapter = new ArrayAdapter<>(
                            TelaCadastroPlanta.this,
                            android.R.layout.simple_spinner_item,
                            listaEspecimes
                    );

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerEspecime.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Especime>> call, Throwable t) {
                Toast.makeText(TelaCadastroPlanta.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERRO", "Falha: " + t.getMessage());
            }
        });
    }
}
