package com.network.plantfyth;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.model.Usuario;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;
import com.network.plantfyth.ui.dashboard.DashboardFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaEditarUsuario extends AppCompatActivity {
    private EditText edtNomeUsuarioEditar, edtEmailUsuarioEditar, edtSenhaUsuarioEditar, edtConfirmeSenhaEditar;
    private Button btnSalvarUsuario, btnCancelarUsuario;
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_editar_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
        edtNomeUsuarioEditar =findViewById(R.id.edtNomeUsuarioEditar);
        edtEmailUsuarioEditar = findViewById(R.id.edtEmailUsuarioEditar);
        edtSenhaUsuarioEditar = findViewById(R.id.edtSenhaUsuarioEditar);
        edtConfirmeSenhaEditar = findViewById(R.id.edtConfirmeSenhaEditar);
        btnCancelarUsuario = findViewById(R.id.btnCancelarUsuario);
        btnSalvarUsuario = findViewById(R.id.btnSalvarUsuario);
        PuxarDados();
        }


    public void CancelarEdicaoUsuario(View view) {
        finish();
    }

    public void SalvarEdicao(View view){
        if(edtNomeUsuarioEditar.getText().toString().isEmpty() || edtEmailUsuarioEditar.getText().toString().isEmpty() || edtSenhaUsuarioEditar.getText().toString().isEmpty() || edtConfirmeSenhaEditar.getText().toString().isEmpty()){
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        String senha = String.valueOf(edtSenhaUsuarioEditar.getText());
        String Confirmacao = String.valueOf(edtConfirmeSenhaEditar.getText());
        if (!senha.equals(Confirmacao)) {
            edtConfirmeSenhaEditar.setError("As senhas não são iguais");
            edtConfirmeSenhaEditar.requestFocus();
            return;
        }
        int usuarioId = getSharedPreferences("USER_DATA", MODE_PRIVATE).getInt("usuario_id", -1);
        Usuario usuario = new Usuario();
        usuario.setNome(edtNomeUsuarioEditar.getText().toString());
        usuario.setHashSenha(edtSenhaUsuarioEditar.getText().toString());
        usuario.setEmail(edtEmailUsuarioEditar.getText().toString());
      Ap.atualizarUsuario(usuario, usuarioId).enqueue(new Callback<Usuario>() {
          @Override
          public void onResponse(Call<Usuario> call, Response<Usuario> response) {
              Toast.makeText(TelaEditarUsuario.this, "Atualizado", Toast.LENGTH_SHORT).show();
              SharedPreferences prefs = getSharedPreferences("USER_EMAIL", MODE_PRIVATE);
              SharedPreferences.Editor editor = prefs.edit();
              editor.putString("usuario_email", usuario.getEmail());
              editor.apply();
              finish();
          }

          @Override
          public void onFailure(Call<Usuario> call, Throwable throwable) {
            Toast.makeText(TelaEditarUsuario.this, "Erro: "+ throwable, Toast.LENGTH_SHORT).show();
          }
      });




    }

    public void PuxarDados(){
       String usuarioEmail = getSharedPreferences("USER_EMAIL", MODE_PRIVATE).getString("usuario_email", "");
        Ap.buscarUsuarioPorEmail(usuarioEmail).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Usuario usuario = response.body();
                edtNomeUsuarioEditar.setText(usuario.getNome());
                edtEmailUsuarioEditar.setText(usuario.getEmail());
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable throwable) {
                Toast.makeText(TelaEditarUsuario.this, "Erro: "+throwable, Toast.LENGTH_SHORT).show();
            }
        });



    }
}