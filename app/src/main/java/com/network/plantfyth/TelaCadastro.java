package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.snackbar.Snackbar;
import com.network.plantfyth.model.Usuario;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaCadastro extends AppCompatActivity {
    Button btnCadastro;
    EditText edtCadastroUsuario, edtCadastroEmail, edtCadastroSenha, edtConfirmeSenha;

    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        btnCadastro = findViewById(R.id.btnCadastro);
        edtCadastroUsuario = findViewById(R.id.edtCadastroUsuario);
        edtCadastroEmail = findViewById(R.id.edtCadastroEmail);
        edtCadastroSenha = findViewById(R.id.edtCadastroSenha);
        edtConfirmeSenha = findViewById(R.id.edtConfirmeSenha);
    }
    public void Cadastrar(View view){
        String nome = String.valueOf(edtCadastroUsuario.getText());
        String email = String.valueOf(edtCadastroEmail.getText());
        String senha = String.valueOf(edtCadastroSenha.getText());
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setHashSenha(senha);
        String Confirmacao = String.valueOf(edtConfirmeSenha.getText());
        if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || Confirmacao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!senha.equals(Confirmacao)) {
            edtConfirmeSenha.setError("As senhas não são iguais");
            edtConfirmeSenha.requestFocus();
            return;
        }
        Ap.saveUsuario(usuario)
                .enqueue(new Callback<Usuario>() {
                             @Override
                             public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                                 if (response.isSuccessful()) {

                                     Toast.makeText(TelaCadastro.this,
                                             "Cadastro realizado!",
                                             Toast.LENGTH_SHORT).show();

                                     startActivity(new Intent(TelaCadastro.this,
                                             TelaDeLogin.class));
                                        finish();
                                 } else {

                                     Toast.makeText(TelaCadastro.this,
                                             "Erro: " + response.code(),
                                             Toast.LENGTH_SHORT).show();
                                 }
                             }
                             @Override
                             public void onFailure(Call<Usuario> call, Throwable t) {

                                 Toast.makeText(TelaCadastro.this,
                                         "Falha: " + t.getMessage(),
                                         Toast.LENGTH_LONG).show();

                                 Log.e("API_ERROR", t.getMessage());
                             }
                         });


    }
}