package com.network.plantfyth.ui.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.network.plantfyth.databinding.FragmentChatbotBinding;
import com.network.plantfyth.model.ChatMessage;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {

    private FragmentChatbotBinding binding;
    private List<ChatMessage> mensagens = new ArrayList<>();
    private ChatAdapter adapter;
    PlantFythAPI api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatbotBinding.inflate(inflater, container, false);

        // Configura RecyclerView
        adapter = new ChatAdapter(mensagens);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerChat.setAdapter(adapter);

        // Mensagem de boas vindas
        adicionarMensagem("Olá! Sou o PlantBot 🌿 Pergunte-me sobre suas plantas!", false);

        // Botão enviar
        binding.btnSend.setOnClickListener(v -> {
            // Adiciona isso dentro do onCreateView, depois do binding.btnSend.setOnClickListener:
            binding.editMessage.setOnEditorActionListener((textView, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    String texto = binding.editMessage.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        enviarMensagem(texto);
                        binding.editMessage.setText("");
                    }
                    return true;
                }
                return false;
            });
            String texto = binding.editMessage.getText().toString().trim();
            if (!texto.isEmpty()) {
                enviarMensagem(texto);
                binding.editMessage.setText("");
            }
        });

        return binding.getRoot();
    }

    private void enviarMensagem(String texto) {
        adicionarMensagem(texto, true);
        adicionarMensagem("Digitando...", false);

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), texto);

        api.perguntarChatbot(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Remove o "Digitando..."
                mensagens.remove(mensagens.size() - 1);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String resposta = response.body().string();
                        adicionarMensagem(resposta, false);
                    } catch (Exception e) {
                        adicionarMensagem("Erro ao ler resposta.", false);
                    }
                } else {
                    adicionarMensagem("Erro ao conectar com o servidor.", false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mensagens.remove(mensagens.size() - 1);
                adicionarMensagem("Falha: " + t.getMessage(), false);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void adicionarMensagem(String texto, boolean isUsuario) {
        mensagens.add(new ChatMessage(texto, isUsuario));
        adapter.notifyItemInserted(mensagens.size() - 1);
        binding.recyclerChat.scrollToPosition(mensagens.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}