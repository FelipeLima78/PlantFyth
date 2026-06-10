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
    private EditText edtNomePlantaEditar, edtDataPlantioEditar, edtTamanhoAtualEditar;
    private RadioGroup radioPlantadaComoEditar;
    private RadioButton rbSementeEditar, rbMudaEditar, rbEstacaEditar;
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);
    private String plantadoComoEditar;
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
        edtTamanhoAtualEditar = findViewById(R.id.edtTamanhoAtualEditar);
        radioPlantadaComoEditar = findViewById(R.id.radioPlantadaComoEditar);
        rbSementeEditar = findViewById(R.id.rbSementeEditar);
        rbMudaEditar = findViewById(R.id.rbMudaEditar);
        rbEstacaEditar = findViewById(R.id.rbEstacaEditar);
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
                edtNomePlantaEditar.setText(planta.getNome());
                String data = planta.getDataQueFoiPlantado();
                edtDataPlantioEditar.setText(data);
                edtTamanhoAtualEditar.setText(String.valueOf(planta.getTamanhoAtualCM()));

                if (planta.getPlantadaComo() != null){
                String plantadocomo = planta.getPlantadaComo();
                switch(plantadocomo){
                    case "Muda":
                        rbMudaEditar.setChecked(true);
                        break;
                    case "Estaca":
                        rbEstacaEditar.setChecked(true);
                        break;
                    case "Semente":
                        rbSementeEditar.setChecked(true);
                        break;
                    default:
                }
            }}
            @Override
            public void onFailure(Call<Plantio> call, Throwable throwable) {
                Toast.makeText(TelaEditarPlanta.this,"Não foi possivel achar a planta!!" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void SalvarEdicao(View view){
        if (edtNomePlantaEditar.getText().toString().isEmpty() || edtDataPlantioEditar.getText().toString().isEmpty()){
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = edtDataPlantioEditar.getText().toString();
        String dataformatada = data;


        planta.setNome(edtNomePlantaEditar.getText().toString());
        planta.setDataQueFoiPlantado(dataformatada);
        if (edtTamanhoAtualEditar.getText().toString() != null){
        planta.setTamanhoAtualCM(Float.parseFloat(edtTamanhoAtualEditar.getText().toString()));
        }
        if (rbSementeEditar.isChecked()) {
            planta.setPlantadaComo(rbSementeEditar.getText().toString());
        }
        if (rbEstacaEditar.isChecked()) {
            planta.setPlantadaComo(rbEstacaEditar.getText().toString());
        }
        if (rbMudaEditar.isChecked()) {
            planta.setPlantadaComo(rbMudaEditar.getText().toString());
        }
        Ap.buscarDetalhes(planta.getEspecime().getPerenual_id()).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(Call<Especime> call, Response<Especime> response) {
                Especime especime = response.body();
                if (especime.getPeriodo_irrigacao() != null && especime.getUnidade_irrigacao() != null) {
                    String valorLimpo = especime.getPeriodo_irrigacao()
                            .replaceAll("[^0-9\\-]", "")
                            .trim();

                    if (valorLimpo.isEmpty()) return;

                    int valor = Integer.parseInt(valorLimpo.split("-")[0]);
                    long horasIrrigacao;

                    switch (especime.getUnidade_irrigacao().toLowerCase()) {
                        case "days":
                            horasIrrigacao = valor * 24;
                            break;
                        case "weeks":
                            horasIrrigacao = valor * 24 * 7;
                            break;
                        case "months":
                            horasIrrigacao = valor * 24 * 30;
                            break;
                        default:
                            horasIrrigacao = 24;
                            break;
                    }

                    Calendar proxIrrigacao = Calendar.getInstance();
                    proxIrrigacao.add(Calendar.HOUR, (int) horasIrrigacao);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    planta.setPrevisaoProximaIrrigacao(sdf.format(proxIrrigacao.getTime()));
                }
                if (especime.getPeriodo_poda() != null) {
                    String[] meses = especime.getPeriodo_poda().toLowerCase().split(", ");
                    try {
                        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
                        String dataStr = "01/" + meses[0] + "/" + anoAtual;
                        SimpleDateFormat sdfParse = new SimpleDateFormat("dd/MMMM/yyyy",new Locale("pt", "BR"));
                        SimpleDateFormat sdfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        Date dataPoda = sdfParse.parse(dataStr);
                        Calendar calendarioPoda = Calendar.getInstance();
                        calendarioPoda.setTime(dataPoda);
                        if (calendarioPoda.getTime().before(new Date())) {calendarioPoda.add(Calendar.YEAR, 1);}
                        planta.setPrevisaoProximaPoda(sdfISO.format(calendarioPoda.getTime()));
                    } catch (ParseException e) {
                        planta.setPrevisaoProximaPoda(null);
                    }}
                Ap.atualizarPlanta(planta.getId(), planta).enqueue(new Callback<Plantio>() {
                    @Override
                    public void onResponse(Call<Plantio> call, Response<Plantio> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(TelaEditarPlanta.this, "Plantio Atualizado!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(TelaEditarPlanta.this, "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                        }}
                    @Override
                    public void onFailure(Call<Plantio> call, Throwable throwable) {
                        Toast.makeText(TelaEditarPlanta.this, "Falha: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });}
                    @Override
                    public void onFailure(Call<Especime> call, Throwable throwable) {
                        Toast.makeText(TelaEditarPlanta.this, "Falha: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void CancelarEdicao(View view){
        finish();
    }
}