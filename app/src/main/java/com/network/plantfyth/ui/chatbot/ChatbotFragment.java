package com.network.plantfyth.ui.chatbot;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.network.plantfyth.R;
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
    private final List<ChatMessage> mensagens = new ArrayList<>();
    private ChatAdapter adapter;
    private final PlantFythAPI api = new RetroFitService().getRetrofit().create(PlantFythAPI.class);
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatbotBinding.inflate(inflater, container, false);

        adapter = new ChatAdapter(mensagens);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerChat.setAdapter(adapter);

        adicionarMensagem("Ola! Sou o PlantBot. Pergunte-me sobre suas plantas!", false);

        binding.editMessage.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_DONE) {
                enviarTextoDigitado();
                return true;
            }
            return false;
        });

        binding.btnSend.setOnClickListener(v -> enviarTextoDigitado());

        configurarBarraAcimaDoTeclado();

        return binding.getRoot();
    }

    private void configurarBarraAcimaDoTeclado() {
        View root = binding.getRoot();
        View inputCard = binding.cardInputMessage;
        View navView = requireActivity().findViewById(R.id.nav_view);

        keyboardLayoutListener = () -> {
            if (binding == null) return;

            Rect visibleFrame = new Rect();
            root.getWindowVisibleDisplayFrame(visibleFrame);

            int[] inputLocation = new int[2];
            inputCard.getLocationOnScreen(inputLocation);

            int inputBottom = inputLocation[1] + inputCard.getHeight();
            int overlap = inputBottom - visibleFrame.bottom;
            boolean keyboardVisible = root.getRootView().getHeight() - visibleFrame.bottom > 180;

            if (keyboardVisible && overlap > 0) {
                inputCard.setTranslationY(-(overlap + dpToPx(12)));
            } else {
                inputCard.setTranslationY(0);
            }

            if (navView != null) {
                navView.setVisibility(keyboardVisible ? View.GONE : View.VISIBLE);
            }
        };

        root.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void enviarTextoDigitado() {
        if (binding == null) return;

        String texto = binding.editMessage.getText().toString().trim();
        if (!texto.isEmpty()) {
            enviarMensagem(texto);
            binding.editMessage.setText("");
        }
    }

    private void enviarMensagem(String texto) {
        adicionarMensagem(texto, true);
        adicionarMensagem("Digitando...", false);

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), texto);

        api.chat(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                removerMensagemDigitando();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        adicionarMensagem(response.body().string(), false);
                    } catch (Exception e) {
                        adicionarMensagem("Erro ao ler resposta.", false);
                    }
                } else {
                    adicionarMensagem("Erro ao conectar.", false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                removerMensagemDigitando();
                adicionarMensagem("Falha: " + t.getMessage(), false);
            }
        });
    }

    private void removerMensagemDigitando() {
        if (!mensagens.isEmpty()) {
            int ultimaPosicao = mensagens.size() - 1;
            mensagens.remove(ultimaPosicao);
            adapter.notifyItemRemoved(ultimaPosicao);
        }
    }

    private void adicionarMensagem(String texto, boolean isUsuario) {
        mensagens.add(new ChatMessage(texto, isUsuario));
        adapter.notifyItemInserted(mensagens.size() - 1);
        binding.recyclerChat.scrollToPosition(mensagens.size() - 1);
    }

    @Override
    public void onDestroyView() {
        if (binding != null && keyboardLayoutListener != null) {
            binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
            keyboardLayoutListener = null;
        }
        super.onDestroyView();
        binding = null;
    }
}
