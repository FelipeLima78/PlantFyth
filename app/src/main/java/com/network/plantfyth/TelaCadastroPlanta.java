package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;
import com.network.plantfyth.ui.dashboard.DashboardFragment;
import com.network.plantfyth.ui.home.HomeFragment;

import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaCadastroPlanta extends AppCompatActivity {

    private Button btnSalvarPlanta;
    private EditText edtNomePlanta, edtDataPlantio, edtTamanhoAtual;
    private Spinner spinnerEspecime;
    private RadioGroup radioPlantadaComo;
    private RadioButton rbSemente, rbMuda, rbEstaca;
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
        edtTamanhoAtual = findViewById(R.id.edtTamanhoAtual);
        spinnerEspecime = findViewById(R.id.spinnerEspecime);
        radioPlantadaComo = findViewById(R.id.radioPlantadaComo);
        rbSemente = findViewById(R.id.rbSemente);
        rbMuda = findViewById(R.id.rbMuda);
        rbEstaca = findViewById(R.id.rbEstaca);
        carregarEspecimes();
    }

    public void CadastrarPlanta(View view){
        String nome = String.valueOf(edtNomePlanta.getText());
        String data = String.valueOf(edtDataPlantio.getText());

        String textoTamanho = edtTamanhoAtual.getText().toString().trim();
        float tamanho = 0;
        if(!textoTamanho.isEmpty()){
            tamanho = Float.parseFloat(textoTamanho);
        }

        String plantadoComo = null;
        if(rbSemente.isChecked()){
            plantadoComo = rbSemente.getText().toString();
        }
        if(rbEstaca.isChecked()){
            plantadoComo = rbEstaca.getText().toString();
        }
        if(rbMuda.isChecked()){
            plantadoComo = rbMuda.getText().toString();
        }
        Plantio plantio = new Plantio();
        plantio.setNome(nome);
        if(data.length() != 8){Toast.makeText(this, "Digite 8 números na data", Toast.LENGTH_SHORT).show();
            return;
        }
        //puxa do shared preferences que fiz la no login
        int usuarioId = getSharedPreferences("USER_DATA", MODE_PRIVATE).getInt("usuario_id", -1);
        Usuario usuario = new Usuario();
        String dataFormatada = data.substring(0,2) + "/" + data.substring(2,4) + "/" + data.substring(4,8);
        plantio.setDataQueFoiPlantado(dataFormatada);
        Especime especimeSelecionado = (Especime) spinnerEspecime.getSelectedItem();
        plantio.setEspecimeId(especimeSelecionado.getId());
        plantio.setUsuarioId(usuarioId);
        plantio.setTamanhoAtualCM(tamanho);
        plantio.setPlantadaComo(plantadoComo);
         Ap.savePlantio(plantio)
                .enqueue(new Callback<Plantio>() {

                    @Override
                    public void onResponse(Call<Plantio> call, Response<Plantio> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(TelaCadastroPlanta.this,
                                    "Cadastro realizado!",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(TelaCadastroPlanta.this,
                                    MainActivity.class));

                        } else {

                            Toast.makeText(TelaCadastroPlanta.this,
                                    "Erro: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Plantio> call, Throwable t) {

                        Toast.makeText(TelaCadastroPlanta.this,
                                "Falha: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();

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