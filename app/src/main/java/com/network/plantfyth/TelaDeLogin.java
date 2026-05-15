package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.network.plantfyth.model.Usuario;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaDeLogin extends AppCompatActivity {

    private EditText edtUsuario, edtSenha;
    private Button btnEntrarLogin, btnCadastroLogin, btnEsqueciASenhaLogin;
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_tela_de_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edtUsuario = findViewById(R.id.edtUsuario);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrarLogin = findViewById(R.id.btnEntrarLogin);
        btnCadastroLogin = findViewById(R.id.btnCadastroLogin);
        btnEsqueciASenhaLogin = findViewById(R.id.btnEsqueciASenhaLogin);
    }
    public void irParaCadastro(View view){
        Intent intent = new Intent(TelaDeLogin.this, TelaCadastro.class);
        startActivity(intent);
    }
    public void DarLogin(View view) {

        Usuario request = new Usuario();

        request.setEmail(edtUsuario.getText().toString().trim());
        request.setHashSenha(edtSenha.getText().toString().trim());

        Call<ResponseBody> call = Ap.LoginUsuario(request);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String result = response.body().string();

                        Toast.makeText(TelaDeLogin.this, result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TelaDeLogin.this, MainActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(TelaDeLogin.this, "Erro ao ler resposta", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(TelaDeLogin.this,
                            "Erro no login: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TelaDeLogin.this,
                        "Falha de conexão: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }
}