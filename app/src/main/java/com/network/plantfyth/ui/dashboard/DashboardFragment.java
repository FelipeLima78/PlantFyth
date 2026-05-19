package com.network.plantfyth.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.network.plantfyth.R;
import com.network.plantfyth.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LinearLayout containerPlantas = binding.containerPlantas;

        // 🔹 Inflate de 3 cards de exemplo
        for (int i = 0; i < 3; i++) {
            View card = getLayoutInflater().inflate(R.layout.item_planta, containerPlantas, false);

            // ❗ Aqui você pode alterar o texto só para apresentação
            card.findViewById(R.id.txtNomePlanta); // Exemplo de acesso
            // (Você pode alterar conteúdo se quiser)

            containerPlantas.addView(card);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}