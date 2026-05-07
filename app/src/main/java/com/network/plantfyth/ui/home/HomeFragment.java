package com.network.plantfyth.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.network.plantfyth.R;
import com.network.plantfyth.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private boolean irrigacao1 = false;
    private boolean irrigacao2 = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        configurarToggle(binding.btnIrrigacao1, 1);
        configurarToggle(binding.btnIrrigacao2, 2);

        return binding.getRoot();
    }

    public void configurarToggle(Button botao, int numero) {

        botao.setOnClickListener(v -> {

            if (numero == 1) {
                irrigacao1 = !irrigacao1;

                atualizarBotao(botao, irrigacao1);

            } else {

                irrigacao2 = !irrigacao2;

                atualizarBotao(botao, irrigacao2);
            }
        });
    }

    private void atualizarBotao(Button botao, boolean estado) {

        if (estado) {

            botao.setText("YES");
            botao.setBackgroundResource(R.drawable.bg_toggle_sim);
            botao.setBackgroundColor(Color.GREEN);

        } else {

            botao.setText("NO");
            botao.setBackgroundColor(Color.parseColor("#EF5350"));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}