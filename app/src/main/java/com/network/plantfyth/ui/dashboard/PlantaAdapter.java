package com.network.plantfyth.ui.dashboard;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.MainActivity;
import com.network.plantfyth.R;
import com.network.plantfyth.TelaEditarPlanta;
import com.network.plantfyth.TelaInformacoesPlanta;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.retrofit.PlantFythAPI;
import com.network.plantfyth.retrofit.RetroFitService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantaAdapter extends RecyclerView.Adapter<PlantaAdapter.ViewHolder> {

    private List<Plantio> plantas;

    public PlantaAdapter(List<Plantio> plantas) {
        this.plantas = plantas;
    }
    RetroFitService retroFitService = new RetroFitService();
    PlantFythAPI Ap  = retroFitService.getRetrofit().create(PlantFythAPI.class);
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
        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TelaEditarPlanta.class);
            intent.putExtra("plantio_id", plantio.getId());
            v.getContext().startActivity(intent);
        });


        holder.btnExcluir.setOnClickListener(v -> {
            if (position == RecyclerView.NO_ID) return;
            Ap.deletarPlantio(plantio.getId()).enqueue (new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(v.getContext(),"Excluido com sucesso",Toast.LENGTH_SHORT).show();
                    plantas.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());


                }
                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {
                    Toast.makeText(v.getContext(), "Não foi possível excluir, erro: "+ throwable, Toast.LENGTH_LONG).show();
                }
            });
        });
        holder.btnInfo.setOnClickListener(v ->{
            Intent intent = new Intent(v.getContext(), TelaInformacoesPlanta.class);
            intent.putExtra("plantio_id", plantio.getId());
            v.getContext().startActivity(intent);
                }
        );
    }
    @Override
    public int getItemCount() {
        return plantas != null ? plantas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomePlantio, txtNomeEspecie, btnEditar, btnExcluir, btnInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomePlantio = itemView.findViewById(R.id.txtNomePlantio);
            txtNomeEspecie = itemView.findViewById(R.id.txtNomeEspecie);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            btnInfo = itemView.findViewById(R.id.btnInfo);
        }
    }


}
