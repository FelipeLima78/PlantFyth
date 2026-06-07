package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
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
    private EditText edtNomePlanta, edtDataPlantio, edtTamanhoAtual;
    private Spinner spinnerEspecime;
    private RadioGroup radioPlantadaComo;
    private RadioButton rbSemente, rbMuda, rbEstaca;
    private CheckBox checkFoiRegadoHoje;
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
        checkFoiRegadoHoje = findViewById(R.id.checkFoiRegadoHoje);
        carregarEspecimes();
    }



    // a execução do especime deve ser feita antes da do save, lembra dissso
    public void CadastrarPlanta(View view){
        Especime especimeSelecionado = (Especime) spinnerEspecime.getSelectedItem();
        String data = String.valueOf(edtDataPlantio.getText());
        if(data.length() != 8){Toast.makeText(this, "Digite 8 números na data", Toast.LENGTH_SHORT).show();
            return;
        }
        //chama ap de buscar detalhes
        Log.d("DEBUG", "perenual_id: " + especimeSelecionado.getPerenual_id());
        Ap.buscarDetalhes(especimeSelecionado.getPerenual_id()).enqueue(new Callback<Especime>() {
            @Override
            public void onResponse(Call<Especime> call, Response<Especime> response) {
                Especime especime = response.body();
                Plantio plantio = new Plantio();
                plantio.setNome(String.valueOf(edtNomePlanta.getText()));
                String dataFormatada = data.substring(0, 2) + "/" + data.substring(2, 4) + "/" + data.substring(4, 8);
                plantio.setDataQueFoiPlantado(dataFormatada);
                //tratamento do float
                String textoTamanho = edtTamanhoAtual.getText().toString().trim();
                float tamanho = 0;
                if (!textoTamanho.isEmpty()) {
                    tamanho = Float.parseFloat(textoTamanho);
                }
                String plantadoComo = null;
                if (rbSemente.isChecked()) {
                    plantadoComo = rbSemente.getText().toString();
                }
                if (rbEstaca.isChecked()) {
                    plantadoComo = rbEstaca.getText().toString();
                }
                if (rbMuda.isChecked()) {
                    plantadoComo = rbMuda.getText().toString();
                }
                // puxa o id la do login
                int usuarioId = getSharedPreferences("USER_DATA", MODE_PRIVATE).getInt("usuario_id", -1);
                plantio.setEspecimeId(especimeSelecionado.getPerenual_id());
                plantio.setUsuarioId(usuarioId);
                plantio.setTamanhoAtualCM(tamanho);
                plantio.setPlantadaComo(plantadoComo);
                plantio.setFoi_regado_hoje(checkFoiRegadoHoje.isChecked());


                // dados pegos do especime
                // dados ja pegos ate agr: data que foi plantado, como foi plantado , foiregadohoje, setplantadacomo, setamanhoatualcm, setusuarioid, nome
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
                    plantio.setPrevisaoProximaIrrigacao(sdf.format(proxIrrigacao.getTime()));
                }
                if (especime.getPeriodo_poda() != null) {
                    String[] meses = especime.getPeriodo_poda().toLowerCase().split(", ");
                    try {
                        String dataStr = "01/" + meses[0] + "/" + Calendar.getInstance().get(Calendar.YEAR);
                        SimpleDateFormat sdfParse = new SimpleDateFormat("dd/MMMM/yyyy", Locale.ENGLISH);
                        SimpleDateFormat sdfISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        Date dataPoda = sdfParse.parse(dataStr);
                        plantio.setPrevisaoProximaPoda(sdfISO.format(dataPoda));
                    } catch (ParseException e) {
                        plantio.setPrevisaoProximaPoda(null);
                    }
                }
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
        @Override
        public void onFailure(Call<Especime> call, Throwable t) {
            Toast.makeText(TelaCadastroPlanta.this, "Erro ao buscar espécime", Toast.LENGTH_SHORT).show();
        }
    }
    );
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