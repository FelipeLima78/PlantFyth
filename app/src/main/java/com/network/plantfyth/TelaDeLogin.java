package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.network.plantfyth.model.Usuario;
import com.network.plantfyth.notifications.PlantCareDailyWorker;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaDeLogin extends AppCompatActivity {

    private EditText edtUsuario, edtSenha;
    private Button btnEntrarLogin, btnCadastroLogin;
    private ImageButton btnMostrarSenhaLogin;
    private View loginLoadingContainer;
    private boolean senhaVisivel;
    private boolean loginEmAndamento;
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_tela_de_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset);
            return insets;
        });
        edtUsuario = findViewById(R.id.edtUsuario);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrarLogin = findViewById(R.id.btnEntrarLogin);
        btnCadastroLogin = findViewById(R.id.btnCadastroLogin);
        btnMostrarSenhaLogin = findViewById(R.id.btnMostrarSenha_login);
        loginLoadingContainer = findViewById(R.id.loginLoadingContainer);
        btnMostrarSenhaLogin.setOnClickListener(v -> {
            senhaVisivel = !senhaVisivel;
            alternarVisibilidadeSenha(edtSenha, btnMostrarSenhaLogin, senhaVisivel);
        });
    }

    private void definirCarregamento(boolean carregando) {
        loginEmAndamento = carregando;
        loginLoadingContainer.setVisibility(carregando ? View.VISIBLE : View.GONE);
        btnEntrarLogin.setEnabled(!carregando);
        btnCadastroLogin.setEnabled(!carregando);
        edtUsuario.setEnabled(!carregando);
        edtSenha.setEnabled(!carregando);
        btnMostrarSenhaLogin.setEnabled(!carregando);
    }

    private void alternarVisibilidadeSenha(EditText campo, ImageButton botao, boolean visivel) {
        int posicaoCursor = campo.getSelectionStart();
        campo.setTransformationMethod(visivel
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance());
        campo.setSelection(Math.max(0, Math.min(posicaoCursor, campo.length())));
        botao.setImageResource(visivel ? R.drawable.ic_login_eye_on : R.drawable.ic_login_eye_off);
        botao.setContentDescription(visivel ? "Ocultar senha" : "Mostrar senha");
    }
    public void irParaCadastro(View view){
        Intent intent = new Intent(TelaDeLogin.this, TelaCadastro.class);
        startActivity(intent);
    }
    public void DarLogin(View view) {
        if (loginEmAndamento) {
            return;
        }

        Usuario request = new Usuario();

        request.setEmail(edtUsuario.getText().toString().trim());
        request.setHashSenha(edtSenha.getText().toString().trim());

        definirCarregamento(true);

        Call<ResponseBody> call = Ap.LoginUsuario(request);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String result = response.body().string();

                        Toast.makeText(TelaDeLogin.this, result, Toast.LENGTH_SHORT).show();
                        buscarUsuario(request.getEmail());
                    } catch (Exception e) {
                        e.printStackTrace();
                        definirCarregamento(false);
                        Toast.makeText(TelaDeLogin.this, "Erro ao ler resposta", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    definirCarregamento(false);
                    Toast.makeText(TelaDeLogin.this,
                            "Erro no login: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                definirCarregamento(false);
                Toast.makeText(TelaDeLogin.this,
                        "Falha de conexão: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void buscarUsuario(String email) {
        Call<Usuario> call = Ap.buscarUsuarioPorEmail(email);
        call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                        if(response.isSuccessful() && response.body() != null){
                            Usuario usuario = response.body();
                            Integer idUsuario = usuario.getId();
                            if (idUsuario == null) {
                                definirCarregamento(false);
                                Toast.makeText(TelaDeLogin.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            getSharedPreferences("USER_EMAIL", MODE_PRIVATE).edit()
                                    .putString("usuario_email", usuario.getEmail()).apply();
                            getSharedPreferences("USER_DATA", MODE_PRIVATE).edit()
                                    .putInt("usuario_id", idUsuario)
                                    .putBoolean("usuario_admin", usuario.isAdmin()).apply();

                            if (usuario.isAdmin()) {
                                definirCarregamento(false);
                                startActivity(new Intent(TelaDeLogin.this, TelaListaEspecimesAdmin.class));
                                finish();
                                return;
                            }

                            definirCarregamento(false);
                            PlantCareDailyWorker.scheduleDailyChecks(TelaDeLogin.this, idUsuario);
                            startActivity(new Intent(TelaDeLogin.this, MainActivity.class));
                            finish();
                        } else {
                            definirCarregamento(false);
                            Toast.makeText(TelaDeLogin.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        } }
                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        definirCarregamento(false);
                        Toast.makeText(TelaDeLogin.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();}
                });
    }
}
