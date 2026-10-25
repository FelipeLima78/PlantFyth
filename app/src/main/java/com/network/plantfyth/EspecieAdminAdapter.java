package com.network.plantfyth;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.model.Especime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EspecieAdminAdapter extends RecyclerView.Adapter<EspecieAdminAdapter.VH> {

    public interface OnEditar {
        void editar(Especime especime);
    }

    private final List<Especime> todas = new ArrayList<>();
    private final List<Especime> exibidas = new ArrayList<>();
    private final OnEditar listener;
    private String consulta = "";

    public EspecieAdminAdapter(OnEditar listener) {
        this.listener = listener;
    }

    public void setDados(List<Especime> dados) {
        todas.clear();
        todas.addAll(dados);
        Collections.sort(todas, (a, b) ->
                EspecieAdapter.nomeExibicao(a).compareToIgnoreCase(EspecieAdapter.nomeExibicao(b)));
        filtrar(consulta); // mantém o filtro digitado depois de recarregar
    }

    public void filtrar(String texto) {
        consulta = texto == null ? "" : texto;
        String q = CuidadoCalculator.normalizar(consulta).trim();
        exibidas.clear();
        for (Especime e : todas) {
            if (q.isEmpty() || contem(e.getNome_popular(), q) || contem(e.getNome_cientifico(), q)) {
                exibidas.add(e);
            }
        }
        notifyDataSetChanged();
    }

    public int getTotal() {
        return todas.size();
    }

    private static boolean contem(String texto, String consulta) {
        return texto != null && CuidadoCalculator.normalizar(texto).contains(consulta);
    }

    private static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String valor(String s) {
        return vazio(s) ? "—" : s;
    }

    /** Lista os campos que ainda faltam preencher, ou null se estiver completa. */
    private static String faltando(Especime e) {
        List<String> f = new ArrayList<>();
        if (vazio(e.getExposicao_a_luz())) f.add("luz");
        if (vazio(e.getVolume_irrigacao())) f.add("água");
        if (e.getPeriodo_irrigacao() == null) f.add("intervalo de rega");
        if (vazio(e.getPeriodo_poda())) f.add("poda");
        if (vazio(e.getAdubacao())) f.add("adubação");
        if (vazio(e.getDescricao())) f.add("descrição");
        return f.isEmpty() ? null : TextUtils.join(", ", f);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_especie_admin, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Especime e = exibidas.get(position);

        h.txtPopular.setText(EspecieAdapter.nomeExibicao(e));
        h.txtCientifico.setText(valor(e.getNome_cientifico()));
        h.txtResumo.setText("Luz: " + valor(e.getExposicao_a_luz())
                + "  •  Água: " + valor(e.getVolume_irrigacao()));

        String falta = faltando(e);
        if (falta == null) {
            h.txtFaltando.setVisibility(View.GONE);
        } else {
            h.txtFaltando.setVisibility(View.VISIBLE);
            h.txtFaltando.setText("Faltam: " + falta);
        }

        View.OnClickListener abrir = v -> listener.editar(e);
        h.itemView.setOnClickListener(abrir);
        h.btnEditar.setOnClickListener(abrir);
    }

    @Override
    public int getItemCount() {
        return exibidas.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView txtPopular, txtCientifico, txtResumo, txtFaltando;
        final Button btnEditar;

        VH(@NonNull View itemView) {
            super(itemView);
            txtPopular = itemView.findViewById(R.id.txtPopularAdmin);
            txtCientifico = itemView.findViewById(R.id.txtCientificoAdmin);
            txtResumo = itemView.findViewById(R.id.txtResumoAdmin);
            txtFaltando = itemView.findViewById(R.id.txtFaltandoAdmin);
            btnEditar = itemView.findViewById(R.id.btnEditarEspecie);
        }
    }
}