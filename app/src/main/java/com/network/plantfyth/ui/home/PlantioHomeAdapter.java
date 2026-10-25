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

    private final HomeFragment.OnInfoListener infoListener;

    public PlantioHomeAdapter(List<PlantioHomeItem> itens,
                              HomeFragment.OnAcaoListener listener,
                              HomeFragment.OnInfoListener infoListener) {
        this.itens = itens;
        this.listener = listener;
        this.infoListener = infoListener;
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
        configurarAcao(holder.txtTempoAdubar, holder.btnAdubar, item, item.adubo,
                HomeFragment.TipoAcao.ADUBAR, "Adubei");
        configurarAcao(holder.txtTempoSol, holder.btnSol, item, item.sol,
                HomeFragment.TipoAcao.SOL, item.plantio.isPegou_sol_hoje() ? "Feito" : "Expus ao sol");

    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    private void configurarAcao(TextView txtTempo, Button botao, PlantioHomeItem item,
                                PlantioHomeItem.AcaoStatus status,
                                HomeFragment.TipoAcao tipoAcao, String textoBotao) {
        txtTempo.setText(status.textoTempo);
        txtTempo.setTextColor(status.cor);

        // cuidado que a espécie não precisa: só texto
        if (status.semBotao) {
            botao.setVisibility(View.INVISIBLE);
            return;
        }
        botao.setVisibility(View.VISIBLE);   // obrigatório: o RecyclerView reaproveita a view

        // sem previsão (estação, mês, texto ilegível): vai para as informações
        if (status.abrirInfo) {
            botao.setText("Ver Info");
            botao.setEnabled(true);
            botao.setAlpha(1f);
            botao.setBackgroundTintList(ColorStateList.valueOf(status.cor));
            botao.setOnClickListener(v -> infoListener.onAbrirInfo(item));
            return;
        }

        // 3) caminho normal (o que você já tinha)
        boolean salvandoEstaAcao = item.salvandoAcao == tipoAcao;
        boolean habilitado = status.disponivel && item.salvandoAcao == null;

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
        TextView txtTempoSol;
        Button btnIrrigar;
        Button btnAdubar;
        Button btnPodar;
        Button btnSol;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomePlanta = itemView.findViewById(R.id.txtNomePlanta);
            txtNomeEspecime = itemView.findViewById(R.id.txtNomeEspecime);
            txtTempoIrrigar = itemView.findViewById(R.id.txtTempoIrrigar);
            txtTempoPodar = itemView.findViewById(R.id.txtTempoPodar);
            txtTempoAdubar = itemView.findViewById(R.id.txtTempoAdubar);
            txtTempoSol = itemView.findViewById(R.id.txtTempoSol);
            btnIrrigar = itemView.findViewById(R.id.btnIrrigar);
            btnPodar = itemView.findViewById(R.id.btnPodar);
            btnAdubar = itemView.findViewById(R.id.btnAdubar);
            btnSol = itemView.findViewById(R.id.btnSol);
        }
    }
}
