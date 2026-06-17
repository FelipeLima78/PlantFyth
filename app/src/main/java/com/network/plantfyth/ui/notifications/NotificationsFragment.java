package com.network.plantfyth.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.network.plantfyth.TelaDeLogin;
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
    private final RetroFitService retroFitService = new RetroFitService();
    private final PlantFythAPI api = retroFitService.getRetrofit().create(PlantFythAPI.class);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        String usuarioEmail = requireActivity()
                .getSharedPreferences("USER_EMAIL", requireActivity().MODE_PRIVATE)
                .getString("usuario_email", "");
        int usuarioId = requireActivity()
                .getSharedPreferences("USER_DATA", requireActivity().MODE_PRIVATE)
                .getInt("usuario_id", -1);

        carregarUsuario(usuarioEmail);
        configurarBotoes(usuarioId);

        return binding.getRoot();
    }

    private void carregarUsuario(String usuarioEmail) {
        mostrarCarregando(true);

        api.buscarUsuarioPorEmail(usuarioEmail).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (binding == null) return;
                mostrarCarregando(false);

                if (!response.isSuccessful() || response.body() == null) {
                    mostrarErroUsuario();
                    return;
                }

                Usuario usuario = response.body();
                binding.txtNomeUsuario.setText(usuario.getNome());
                binding.txtEmailUsuario.setText(usuario.getEmail());
                binding.txtDataConta.setText("Sua conta foi criada em: " + formatarData(usuario.getDataCriacao()));
                binding.txtUltimoLogin.setText("Voce logou a ultima vez em: " + formatarData(usuario.getUltimoLogin()));
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable throwable) {
                if (binding == null) return;
                mostrarCarregando(false);
                mostrarErroUsuario();
                Toast.makeText(getContext(), "Erro: " + throwable, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarBotoes(int usuarioId) {
        binding.btnSairConta.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), TelaDeLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        binding.btnEditarUsuario.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), TelaEditarUsuario.class);
            startActivity(intent);
        });

        binding.btnExcluirConta.setOnClickListener(view -> {
            api.deleteUsuario(usuarioId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (!isAdded()) return;
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
    }

    private void mostrarCarregando(boolean carregando) {
        if (binding == null) return;

        binding.progressBarNotifications.setVisibility(carregando ? View.VISIBLE : View.GONE);
        binding.scrollConta.setVisibility(carregando ? View.GONE : View.VISIBLE);
    }

    private void mostrarErroUsuario() {
        String mensagem = "Nao foi possivel coletar as informacoes";
        binding.txtNomeUsuario.setText(mensagem);
        binding.txtEmailUsuario.setText(mensagem);
        binding.txtDataConta.setText(mensagem);
        binding.txtUltimoLogin.setText(mensagem);
    }

    private String formatarData(String data) {
        if (data == null || data.length() < 10) return "-";
        return data.substring(0, 10);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
