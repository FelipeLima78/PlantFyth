 package com.network.plantfyth.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.network.plantfyth.R;
import com.network.plantfyth.TelaCadastroPlanta;
import com.network.plantfyth.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private FloatingActionButton btnAdicionarPlanta;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(
                R.layout.fragment_dashboard,
                container,
                false
        );

        FloatingActionButton btnAdicionar =
                root.findViewById(
                        R.id.btnAdicionarPlanta
                );

        btnAdicionar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            getActivity(),
                            TelaCadastroPlanta.class
                    );

            startActivity(intent);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}