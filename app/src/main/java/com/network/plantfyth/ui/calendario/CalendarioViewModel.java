package com.network.plantfyth.ui.calendario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.network.plantfyth.Model.EventoCalendario;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarioViewModel extends ViewModel {

    private final MutableLiveData<List<EventoCalendario>> eventos = new MutableLiveData<>();
    private final MutableLiveData<List<EventoCalendario>> eventosDoDia = new MutableLiveData<>();

    public CalendarioViewModel() {
        carregarEventos();
    }

    private void carregarEventos() {
        List<EventoCalendario> lista = new ArrayList<>();
        Calendar hoje = Calendar.getInstance();

        // Irrigação amanhã
        Calendar amanha = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_MONTH, 1);
        lista.add(new EventoCalendario(
                "Samambaia", EventoCalendario.TipoEvento.IRRIGACAO, amanha));

        // Adubação em 2 dias
        Calendar em2dias = Calendar.getInstance();
        em2dias.add(Calendar.DAY_OF_MONTH, 2);
        lista.add(new EventoCalendario(
                "Costela de Adão", EventoCalendario.TipoEvento.ADUBACAO, em2dias));

        // Poda em 12 dias
        Calendar em12dias = Calendar.getInstance();
        em12dias.add(Calendar.DAY_OF_MONTH, 12);
        lista.add(new EventoCalendario(
                "Samambaia", EventoCalendario.TipoEvento.PODA, em12dias));

        eventos.setValue(lista);

        // Por padrão mostra eventos de hoje
        filtrarEventosPorDia(hoje);
    }

    public void filtrarEventosPorDia(Calendar diaSelecionado) {
        List<EventoCalendario> lista = eventos.getValue();
        List<EventoCalendario> dodia = new ArrayList<>();

        if (lista == null) return;

        for (EventoCalendario e : lista) {
            if (mesmoDia(e.getData(), diaSelecionado)) {
                dodia.add(e);
            }
        }
        eventosDoDia.setValue(dodia);
    }

    private boolean mesmoDia(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR)         == b.get(Calendar.YEAR)
                && a.get(Calendar.MONTH)        == b.get(Calendar.MONTH)
                && a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH);
    }

    public LiveData<List<EventoCalendario>> getEventos()      { return eventos; }
    public LiveData<List<EventoCalendario>> getEventosDoDia() { return eventosDoDia; }
}