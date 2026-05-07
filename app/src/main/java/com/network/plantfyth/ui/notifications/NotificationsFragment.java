package com.network.plantfyth.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.network.plantfyth.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // BOTÃO EDITAR
        binding.btnEditar.setOnClickListener(view ->
                Toast.makeText(getContext(), "Tela de editar (exemplo)", Toast.LENGTH_SHORT).show()
        );

        // BOTÃO TROCAR CONTA
        binding.btnTrocarConta.setOnClickListener(view ->
                Toast.makeText(getContext(), "Trocar conta (exemplo)", Toast.LENGTH_SHORT).show()
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}