package com.network.plantfyth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaEditarPlanta extends AppCompatActivity {

    private Button btnSalvarEdicao, btnCancelarEdicao;
    private EditText edtNomePlantaEditar, edtDataPlantioEditar;
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);
    private Plantio planta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_editar_planta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSalvarEdicao = findViewById(R.id.btnSalvarEdicao);
        btnCancelarEdicao = findViewById(R.id.btnCancelarEdicao);
        edtNomePlantaEditar = findViewById(R.id.edtNomePlantaEditar);
        edtDataPlantioEditar = findViewById(R.id.edtDataPlantioEditar);
        int plantioId = getIntent().getIntExtra("plantio_id", -1);
        PegarDados(plantioId);
    }
    public void AbrirCalendario(View view){
        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,(v, anoSelecionado, mesSelecionado, diaSelecionado)-> {
            String data = String.format("%02d/%02d/%04d", diaSelecionado, mesSelecionado + 1, anoSelecionado);
            edtDataPlantioEditar.setText(data);
        }, ano, mes, dia);

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    public void PegarDados(int id){
        Ap.buscarPlantaPorId(id).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(Call<Plantio> call, Response<Plantio> response) {
                planta = response.body();
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(TelaEditarPlanta.this, "Planta não encontrada", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                edtNomePlantaEditar.setText(planta.getNome());
                String data = planta.getDataQueFoiPlantado();
                edtDataPlantioEditar.setText(data);

           }
            @Override
            public void onFailure(Call<Plantio> call, Throwable throwable) {
                Toast.makeText(TelaEditarPlanta.this,"Não foi possivel achar a planta!!" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SalvarEdicao(View view) {
        if (planta == null) return;
        if (edtNomePlantaEditar.getText().toString().trim().isEmpty()
                || edtDataPlantioEditar.getText().toString().isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        planta.setNome(edtNomePlantaEditar.getText().toString().trim());
        planta.setDataQueFoiPlantado(edtDataPlantioEditar.getText().toString());

        Ap.atualizarPlanta(planta.getId(), planta).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(Call<Plantio> call, Response<Plantio> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TelaEditarPlanta.this, "Plantio atualizado!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TelaEditarPlanta.this, "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Plantio> call, Throwable t) {
                Toast.makeText(TelaEditarPlanta.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void CancelarEdicao(View view){
        finish();
    }
}