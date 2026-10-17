package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.network.plantfyth.model.Especime;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaListaEspecimesAdmin extends AppCompatActivity {

    private static final String TAG = "ListaEspecimesAdmin";

    private final PlantFythAPI api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);

    private EspecieAdminAdapter adapter;
    private ProgressBar progress;
    private TextView txtVazio, txtContagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_lista_especimes_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        progress = findViewById(R.id.progressAdmin);
        txtVazio = findViewById(R.id.txtVazioAdmin);
        txtContagem = findViewById(R.id.txtContagemAdmin);
        SearchView searchView = findViewById(R.id.searchEspecieAdmin);
        RecyclerView recycler = findViewById(R.id.recyclerEspeciesAdmin);
        com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton fab =
                findViewById(R.id.fabNovaEspecie);
        View btnSair = findViewById(R.id.btnSairAdmin);

        adapter = new EspecieAdminAdapter(this::editar);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filtrar(newText);
                atualizarContagem();
                return true;
            }
        });


        fab.setOnClickListener(v -> startActivity(new Intent(this, TelaCadastroEspecime.class)));
        btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(
                    TelaListaEspecimesAdmin.this,
                    TelaDeLogin.class
            );

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
            carregar();
        }


    private void carregar() {
        if (adapter.getTotal() == 0) {
            progress.setVisibility(View.VISIBLE);
        }

        api.listarEspecimes().enqueue(new Callback<List<Especime>>() {
            @Override
            public void onResponse(@NonNull Call<List<Especime>> call,
                                   @NonNull Response<List<Especime>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setDados(response.body());
                    atualizarContagem();
                } else {
                    Log.e(TAG, "Falha ao listar: HTTP " + response.code());
                    Toast.makeText(TelaListaEspecimesAdmin.this,
                            "Não foi possível carregar as espécies.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Especime>> call, @NonNull Throwable t) {
                progress.setVisibility(View.GONE);
                Log.e(TAG, "Falha de rede ao listar", t);
                Toast.makeText(TelaListaEspecimesAdmin.this,
                        "Sem conexão com o servidor. Verifique a internet.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarContagem() {
        int total = adapter.getTotal();
        int exibidas = adapter.getItemCount();

        txtContagem.setText(exibidas == total
                ? total + (total == 1 ? " espécie" : " espécies")
                : exibidas + " de " + total + " espécies");

        txtVazio.setText(total == 0
                ? "Nenhuma espécie cadastrada.\nToque em + para adicionar."
                : "Nenhuma espécie encontrada");
        txtVazio.setVisibility(exibidas == 0 ? View.VISIBLE : View.GONE);
    }

    private void editar(Especime especime) {
        int id = especime.getId();
        Intent intent = new Intent(this, TelaCadastroEspecime.class);
        intent.putExtra(TelaCadastroEspecime.EXTRA_ID, id);
        startActivity(intent);
    }

}