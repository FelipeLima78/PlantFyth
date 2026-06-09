package com.network.plantfyth.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.network.plantfyth.MainActivity;
import com.network.plantfyth.TelaDeLogin;
import com.network.plantfyth.TelaEditarPlanta;
import com.network.plantfyth.TelaEditarUsuario;
import com.network.plantfyth.databinding.FragmentNotificationsBinding;
import com.network.plantfyth.model.Usuario;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        String usuarioEmail = requireActivity().getSharedPreferences("USER_EMAIL", requireActivity().MODE_PRIVATE).getString("usuario_email", "");
        int usuarioId = requireActivity().getSharedPreferences("USER_DATA", requireActivity().MODE_PRIVATE).getInt("usuario_id", -1);
        Ap.buscarUsuarioPorEmail(usuarioEmail).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Usuario usuario = response.body();
                binding.txtNomeUsuario.setText(usuario.getNome());
                binding.txtEmailUsuario.setText(usuario.getEmail());

                binding.txtDataConta.setText("Sua conta foi criada em: "+usuario.getDataCriacao().substring(0,10));
                  binding.txtUltimoLogin.setText("Você logou a última vez em: "+usuario.getUltimoLogin().substring(0,10));
            }
            @Override
            public void onFailure(Call<Usuario> call, Throwable throwable) {
                binding.txtNomeUsuario.setText("Não foi possível coletar as informações");
                binding.txtEmailUsuario.setText("Não foi possível coletar as informações");
                binding.txtDataConta.setText("Não foi possível coletar as informações");
                binding.txtUltimoLogin.setText("Não foi possível coletar as informações");
                Toast.makeText(getContext(), "Erro: "+throwable, Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnSairConta.setOnClickListener(view ->{
                Intent intent = new Intent(getContext(), TelaDeLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
        }
        );
        binding.btnEditarUsuario.setOnClickListener(view -> {
                    Intent intent = new Intent(getContext(), TelaEditarUsuario.class);
                    startActivity(intent);
                }
        );
        binding.btnExcluirConta.setOnClickListener(view ->{
            Ap.deleteUsuario(usuarioId).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Intent intent = new Intent(requireActivity(), TelaDeLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("DELETE", "erro", t);
                    }
                });
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}