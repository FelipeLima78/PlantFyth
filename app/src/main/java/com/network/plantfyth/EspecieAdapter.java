package com.network.plantfyth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.model.Especime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EspecieAdapter extends RecyclerView.Adapter<EspecieAdapter.VH> {

    public interface OnEspecieClick {
        void onClick(Especime especime);
    }

    private final List<Especime> todas = new ArrayList<>();
    private final List<Especime> exibidas = new ArrayList<>();
    private final OnEspecieClick listener;

    public EspecieAdapter(OnEspecieClick listener) {
        this.listener = listener;
    }

    public void setDados(List<Especime> dados) {
        todas.clear();
        todas.addAll(dados);
        Collections.sort(todas, (a, b) -> nomeExibicao(a).compareToIgnoreCase(nomeExibicao(b)));
        filtrar("");
    }

    public void filtrar(String consulta) {
        String q = CuidadoCalculator.normalizar(consulta == null ? "" : consulta).trim();
        exibidas.clear();
        for (Especime e : todas) {
            if (q.isEmpty() || contem(e.getNome_popular(), q) || contem(e.getNome_cientifico(), q)) {
                exibidas.add(e);
            }
        }
        notifyDataSetChanged();
    }

    private static boolean contem(String texto, String consulta) {
        return texto != null && CuidadoCalculator.normalizar(texto).contains(consulta);
    }

    public static String nomeExibicao(Especime e) {
        String popular = e.getNome_popular();
        if (popular != null && !popular.trim().isEmpty()) return popular;
        String cientifico = e.getNome_cientifico();
        return cientifico != null && !cientifico.trim().isEmpty() ? cientifico : "Espécie sem nome";
    }

    private static String valor(String s) {
        return s != null && !s.trim().isEmpty() ? s : "—";
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_especie, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Especime e = exibidas.get(position);
        h.txtPopular.setText(nomeExibicao(e));
        h.txtCientifico.setText(valor(e.getNome_cientifico()));
        h.txtResumo.setText("Luz: " + valor(e.getExposicao_a_luz())
                + "  •  Rega: " + valor(e.getVolume_irrigacao()));
        h.itemView.setOnClickListener(v -> listener.onClick(e));
    }

    @Override
    public int getItemCount() {
        return exibidas.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView txtPopular, txtCientifico, txtResumo;

        VH(@NonNull View itemView) {
            super(itemView);
            txtPopular = itemView.findViewById(R.id.txtPopular);
            txtCientifico = itemView.findViewById(R.id.txtCientifico);
            txtResumo = itemView.findViewById(R.id.txtResumo);
        }
    }
}