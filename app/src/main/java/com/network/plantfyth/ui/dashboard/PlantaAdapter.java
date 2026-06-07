package com.network.plantfyth.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.R;
import com.network.plantfyth.model.Plantio;

import java.util.List;

public class PlantaAdapter extends RecyclerView.Adapter<PlantaAdapter.ViewHolder> {

    private List<Plantio> plantas;

    public PlantaAdapter(List<Plantio> plantas) {
        this.plantas = plantas;
    }

    public void atualizarLista(List<Plantio> novaLista) {
        this.plantas = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Plantio plantio = plantas.get(position);

        // Nome dado ao plantio pelo usuário
        String nomePlantio = plantio.getNome();
        holder.txtNomePlantio.setText(
                nomePlantio != null && !nomePlantio.isEmpty() ? nomePlantio : "Sem nome"
        );

        // Nome da espécie — tenta nome popular, cai pro científico
        if (plantio.getEspecime() != null) {
            String nomeEspecie = plantio.getEspecime().getNome_popular();
            if (nomeEspecie == null || nomeEspecie.isEmpty()) {
                nomeEspecie = plantio.getEspecime().getNome_cientifico();
            }
            holder.txtNomeEspecie.setText(
                    nomeEspecie != null ? nomeEspecie : "Espécie desconhecida"
            );
        } else {
            holder.txtNomeEspecie.setText("Espécie desconhecida");
        }
    }

    @Override
    public int getItemCount() {
        return plantas != null ? plantas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomePlantio, txtNomeEspecie;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomePlantio = itemView.findViewById(R.id.txtNomePlantio);
            txtNomeEspecie = itemView.findViewById(R.id.txtNomeEspecie);
        }
    }
}
