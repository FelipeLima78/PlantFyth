package com.network.plantfyth.ui.calendario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.network.plantfyth.databinding.FragmentCalendarioBinding;

import java.util.Calendar;

public class CalendarioFragment extends Fragment {

    private FragmentCalendarioBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCalendarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CalendarView calendar = binding.calendarView;

        Calendar hoje = Calendar.getInstance();

        // 🔵 limita mínimo = 1 de janeiro do ano atual
        Calendar min = Calendar.getInstance();
        min.set(hoje.get(Calendar.YEAR), Calendar.JANUARY, 1);
        calendar.setMinDate(min.getTimeInMillis());

        // 🔵 limita máximo = 31 de dezembro do ano atual
        Calendar max = Calendar.getInstance();
        max.set(hoje.get(Calendar.YEAR), Calendar.DECEMBER, 31);
        calendar.setMaxDate(max.getTimeInMillis());

        // 🔵 marca o dia atual automaticamente
        calendar.setDate(hoje.getTimeInMillis(), true, true);

        // 🔵 ouvir clique em uma data
        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Aqui você pode fazer algo com a data clicada
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}