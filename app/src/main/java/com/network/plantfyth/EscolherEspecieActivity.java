package com.network.plantfyth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EscolherEspecieActivity extends AppCompatActivity {

    private final PlantFythAPI api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);
    private EspecieAdapter adapter;
    private ProgressBar progress;
    private TextView txtVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_escolher_especie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + dp(20), bars.top + dp(20), bars.right + dp(20), bars.bottom + dp(20));
            return insets;
        });

        progress = findViewById(R.id.progressEspecies);
        txtVazio = findViewById(R.id.txtVazio);
        SearchView searchView = findViewById(R.id.searchEspecie);
        RecyclerView recycler = findViewById(R.id.recyclerEspecies);

        adapter = new EspecieAdapter(this::escolher);
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
                atualizarVazio();
                return true;
            }
        });

        carregar();
    }

    private void carregar() {
        progress.setVisibility(View.VISIBLE);
        api.listarEspecimes().enqueue(new Callback<List<Especime>>() {
            @Override
            public void onResponse(Call<List<Especime>> call, Response<List<Especime>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setDados(response.body());
                    atualizarVazio();
                } else {
                    Toast.makeText(EscolherEspecieActivity.this,
                            "Erro ao carregar espécies: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Especime>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(EscolherEspecieActivity.this,
                        "Falha de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void atualizarVazio() {
        txtVazio.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void escolher(Especime especime) {
        Intent resultado = new Intent();
        resultado.putExtra("especime_id", especime.getId());
        setResult(RESULT_OK, resultado);
        finish();
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }
}