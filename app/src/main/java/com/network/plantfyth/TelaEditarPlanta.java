package com.network.plantfyth;

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

import java.util.List;

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

    public void PegarDados(int id){
        Ap.buscarPlantaPorId(id).enqueue(new Callback<Plantio>() {
            @Override
            public void onResponse(Call<Plantio> call, Response<Plantio> response) {
                Plantio plantio = response.body();
                edtNomePlantaEditar.setText(plantio.getNome());
                String data = plantio.getDataQueFoiPlantado();
                edtDataPlantioEditar.setText(data.replace("/",""));
                edtTamanhoAtualEditar.setText(String.valueOf(plantio.getTamanhoAtualCM()));

                if (plantio.getPlantadaComo() != null){
                String plantadocomo = plantio.getPlantadaComo();
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
        if(edtDataPlantioEditar.getText().toString().length() != 8){Toast.makeText(this, "Digite 8 números na data", Toast.LENGTH_SHORT).show();
            return;
        }
        String data = edtDataPlantioEditar.getText().toString();
        String dataFormatada = data.substring(0, 2) + "/" + data.substring(2, 4) + "/" + data.substring(4, 8);

        int plantioId = getIntent().getIntExtra("plantio_id", -1);

        Plantio plantio = new Plantio();
        plantio.setNome(edtNomePlantaEditar.getText().toString());
        plantio.setDataQueFoiPlantado(data);
        if (edtTamanhoAtualEditar.getText().toString() != null){
        plantio.setTamanhoAtualCM(Float.parseFloat(edtTamanhoAtualEditar.getText().toString()));
        }
        if (rbSementeEditar.isChecked()) {
            plantio.setPlantadaComo(rbSementeEditar.getText().toString());
        }
        if (rbEstacaEditar.isChecked()) {
            plantio.setPlantadaComo(rbEstacaEditar.getText().toString());
        }
        if (rbMudaEditar.isChecked()) {
            plantio.setPlantadaComo(rbMudaEditar.getText().toString());
        }
        Ap.atualizarPlanta(plantioId, plantio).enqueue(new Callback<Plantio>() {
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
        });
    }
    public void CancelarEdicao(View view){
        finish();
    }
}