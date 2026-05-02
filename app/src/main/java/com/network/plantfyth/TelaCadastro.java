package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaCadastro extends AppCompatActivity {
    Button btnCadastro;
    EditText edtCadastroUsuario, edtCadastroEmail, edtCadastroSenha, edtConfirmeSenha;

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
        Intent intent = new Intent(TelaCadastro.this, TelaDeLogin.class);
        startActivity(intent);
    }
}