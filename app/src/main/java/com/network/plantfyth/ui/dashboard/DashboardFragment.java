 package com.network.plantfyth.ui.dashboard;

 import android.content.Intent;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.fragment.app.Fragment;
 import androidx.recyclerview.widget.LinearLayoutManager;

 import com.google.android.material.floatingactionbutton.FloatingActionButton;
 import com.network.plantfyth.TelaCadastroPlanta;
 import com.network.plantfyth.databinding.FragmentDashboardBinding;
 import com.network.plantfyth.model.Plantio;
 import com.network.plantfyth.retrofit.PlantFythAPI;
 import com.network.plantfyth.retrofit.RetroFitService;

 import java.util.ArrayList;
 import java.util.List;

 import retrofit2.Call;
 import retrofit2.Callback;
 import retrofit2.Response;

 public class DashboardFragment extends Fragment {
     private FloatingActionButton btnAdicionarPlanta;
     private FragmentDashboardBinding binding;
     private PlantaAdapter adapter;
     private List<Plantio> listaPlantas = new ArrayList<>();
     private PlantFythAPI api;

     @Override
     public View onCreateView(@NonNull LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {

         binding = FragmentDashboardBinding.inflate(inflater, container, false);
         Log.d("TESTE", String.valueOf(binding.btnAdicionarPlanta));
         api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);

         configurarRecyclerView();
         configurarBotaoAdicionar();

         return binding.getRoot();
     }

     @Override
     public void onResume() {
         super.onResume();
         // Recarrega toda vez que o fragment fica visível,
         // isso garante atualização automática ao voltar do cadastro
         carregarPlantas();
     }

     private void configurarRecyclerView() {
         adapter = new PlantaAdapter(listaPlantas);
         binding.recyclerPlantas.setLayoutManager(new LinearLayoutManager(getContext()));
         binding.recyclerPlantas.setAdapter(adapter);
     }

     private void configurarBotaoAdicionar() {
         binding.btnAdicionarPlanta.setOnClickListener(v -> {
             Intent intent = new Intent(getActivity(), TelaCadastroPlanta.class);
             startActivity(intent);
         });
     }

     private void carregarPlantas() {
         // Pega o id do usuário logado salvo no SharedPreferences
         int usuarioId = requireActivity()
                 .getSharedPreferences("USER_DATA", requireActivity().MODE_PRIVATE)
                 .getInt("usuario_id", -1);

         if (usuarioId == -1) {
             Toast.makeText(getContext(), "Usuário não identificado.", Toast.LENGTH_SHORT).show();
             return;
         }

         api.buscarPlantasUsuario(usuarioId).enqueue(new Callback<List<Plantio>>() {
             @Override
             public void onResponse(Call<List<Plantio>> call, Response<List<Plantio>> response) {
                 if (response.isSuccessful() && response.body() != null) {
                     adapter.atualizarLista(response.body());
                 } else {
                     Log.e("DASHBOARD", "Resposta sem sucesso: " + response.code());
                     Toast.makeText(getContext(),
                             "Erro ao carregar plantas: " + response.code(),
                             Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onFailure(Call<List<Plantio>> call, Throwable t) {
                 Log.e("DASHBOARD", "Falha: " + t.getMessage());
                 Toast.makeText(getContext(),
                         "Falha de conexão: " + t.getMessage(),
                         Toast.LENGTH_SHORT).show();
             }
         });
     }

     @Override
     public void onDestroyView() {
         super.onDestroyView();
         binding = null;
     }
     public void Intent(View view){
         Intent intent = new Intent(getActivity(), TelaCadastroPlanta.class);
         startActivity(intent);
     }

 }