package com.network.plantfyth.ui.home;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.R;
import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;

import java.util.List;

public class PlantioHomeAdapter extends RecyclerView.Adapter<PlantioHomeAdapter.ViewHolder> {

    private final List<PlantioHomeItem> itens;
    private final HomeFragment.OnAcaoListener listener;

    public PlantioHomeAdapter(List<PlantioHomeItem> itens, HomeFragment.OnAcaoListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planta_home, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlantioHomeItem item = itens.get(position);
        Plantio plantio = item.plantio;

        holder.txtNomePlanta.setText(textoOuPadrao(plantio.getNome(), "Planta sem nome"));
        holder.txtNomeEspecime.setText(nomeEspecime(plantio.getEspecime()));

        configurarAcao(holder.txtTempoIrrigar, holder.btnIrrigar, item, item.irrigacao,
                HomeFragment.TipoAcao.IRRIGAR, "Irriguei");
        configurarAcao(holder.txtTempoPodar, holder.btnPodar, item, item.poda,
                HomeFragment.TipoAcao.PODAR, "Podei");
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    private void configurarAcao(TextView txtTempo, Button botao, PlantioHomeItem item,
                                PlantioHomeItem.AcaoStatus status,
                                HomeFragment.TipoAcao tipoAcao, String textoBotao) {
        boolean salvandoEstaAcao = item.salvandoAcao == tipoAcao;
        boolean habilitado = status.disponivel && item.salvandoAcao == null;

        txtTempo.setText(status.textoTempo);
        txtTempo.setTextColor(status.cor);
        botao.setText(salvandoEstaAcao ? "Salvando" : textoBotao);
        botao.setEnabled(habilitado);
        botao.setAlpha(habilitado || salvandoEstaAcao ? 1f : 0.55f);
        botao.setBackgroundTintList(ColorStateList.valueOf(status.cor));
        botao.setOnClickListener(v -> listener.onAcaoMarcada(item, tipoAcao));
    }

    private String nomeEspecime(Especime especime) {
        if (especime == null) return "Especie desconhecida";
        String nomePopular = especime.getNome_popular();
        if (nomePopular != null && !nomePopular.trim().isEmpty()) return nomePopular;
        return textoOuPadrao(especime.getNome_cientifico(), "Especie desconhecida");
    }

    private String textoOuPadrao(String valor, String padrao) {
        return valor != null && !valor.trim().isEmpty() ? valor : padrao;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomePlanta;
        TextView txtNomeEspecime;
        TextView txtTempoIrrigar;
        TextView txtTempoAdubar;
        TextView txtTempoPodar;
        Button btnIrrigar;
        Button btnAdubar;
        Button btnPodar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomePlanta = itemView.findViewById(R.id.txtNomePlanta);
            txtNomeEspecime = itemView.findViewById(R.id.txtNomeEspecime);
            txtTempoIrrigar = itemView.findViewById(R.id.txtTempoIrrigar);
            txtTempoPodar = itemView.findViewById(R.id.txtTempoPodar);
            btnIrrigar = itemView.findViewById(R.id.btnIrrigar);
            btnPodar = itemView.findViewById(R.id.btnPodar);
        }
    }
}
