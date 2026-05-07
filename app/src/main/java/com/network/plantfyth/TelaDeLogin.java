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

import com.network.plantfyth.ui.dashboard.DashboardFragment;
import com.network.plantfyth.ui.home.HomeFragment;

public class TelaDeLogin extends AppCompatActivity {

    EditText edtUsuario, edtSenha;
    Button btnEntrarLogin, btnCadastroLogin, btnEsqueciASenhaLogin;

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
    public void irParaTeste(View view){
        Intent intent = new Intent(TelaDeLogin.this, MainActivity.class);
        startActivity(intent);
    }
}