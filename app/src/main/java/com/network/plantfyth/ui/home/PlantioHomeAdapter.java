package com.network.plantfyth.ui.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.plantfyth.R;

import java.util.List;

public class PlantioHomeAdapter extends RecyclerView.Adapter<PlantioHomeAdapter.ViewHolder> {

    // ------------------------------------------------------------
    // Cores de status
    // ------------------------------------------------------------
    private static final int COR_ATRASADO  = Color.parseColor("#EF5350"); // vermelho
    private static final int COR_HOJE      = Color.parseColor("#FF9800"); // laranja
    private static final int COR_OK        = Color.parseColor("#4CAF50"); // verde
    private static final int COR_FEITO     = Color.parseColor("#9E9E9E"); // cinza (botão após marcar)

    // ------------------------------------------------------------
    // Dados e callback
    // ------------------------------------------------------------
    private final List<PlantioHomeItem> itens;
    private final HomeFragment.OnAcaoListener listener;

    public PlantioHomeAdapter(List<PlantioHomeItem> itens, HomeFragment.OnAcaoListener listener) {
        this.itens    = itens;
        this.listener = listener;
    }

    // ------------------------------------------------------------
    // Adapter obrigatório
    // ------------------------------------------------------------

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_planta_home, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PlantioHomeItem item   = itens.get(position);
        com.network.plantfyth.model.Plantio p = item.plantio;

        // --- Cabeçalho ---
        h.txtNomePlanta.setText(p.getNome() != null ? p.getNome() : "—");

        String nomeEspecime = (p.getEspecime() != null && p.getEspecime().getNome() != null)
                ? p.getEspecime().getNome() : "Espécime desconhecido";
        h.txtNomeEspecime.setText(nomeEspecime);

        // --- Irrigação ---
        configurarBotaoAcao(
                h.btnIrrigar,
                h.txtIrrigar,
                item.diasParaIrrigar,
                "💧 Irrigar",
                position,
                HomeFragment.TipoAcao.IRRIGAR
        );

        // --- Adubação ---
        configurarBotaoAcao(
                h.btnAdubar,
                h.txtAdubar,
                item.diasParaAdubar,
                "🌿 Adubar",
                position,
                HomeFragment.TipoAcao.ADUBAR
        );

        // --- Poda ---
        configurarBotaoAcao(
                h.btnPodar,
                h.txtPodar,
                item.diasParaPodar,
                "✂️ Podar",
                position,
                HomeFragment.TipoAcao.PODAR
        );
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    // ------------------------------------------------------------
    // Lógica de exibição do botão + label de dias
    // ------------------------------------------------------------

    /**
     * Configura cor, label de dias e comportamento do botão de ação.
     *
     * @param botao      Button de ação (irrigar / adubar / podar)
     * @param txtDias    TextView que mostra "X dias" ou "Hoje!"
     * @param dias       dias restantes (negativo = atrasado)
     * @param rotulo     texto do botão (ex: "💧 Irrigar")
     * @param posicao    posição no adapter
     * @param tipoAcao   enum da ação
     */
    private void configurarBotaoAcao(
            Button botao,
            TextView txtDias,
            long dias,
            String rotulo,
            int posicao,
            HomeFragment.TipoAcao tipoAcao) {

        // Garante que o botão começa habilitado a cada bind
        botao.setEnabled(true);
        botao.setText(rotulo);

        // --- Cor e texto do label de dias ---
        if (dias == Long.MAX_VALUE) {
            // Data não informada
            txtDias.setText("—");
            txtDias.setTextColor(Color.GRAY);
            botao.setBackgroundTintList(ColorStateList.valueOf(COR_OK));

        } else if (dias < 0) {
            // Atrasado
            txtDias.setText("Atrasado " + Math.abs(dias) + "d");
            txtDias.setTextColor(COR_ATRASADO);
            botao.setBackgroundTintList(ColorStateList.valueOf(COR_ATRASADO));

        } else if (dias == 0) {
            // Hoje!
            txtDias.setText("Hoje!");
            txtDias.setTextColor(COR_HOJE);
            botao.setBackgroundTintList(ColorStateList.valueOf(COR_HOJE));

        } else {
            // Futuro
            txtDias.setText("Em " + dias + "d");
            txtDias.setTextColor(COR_OK);
            botao.setBackgroundTintList(ColorStateList.valueOf(COR_OK));
        }

        // --- Clique: marca como feito, desabilita botão (UX instantâneo)
        //     enquanto a API processa. O adapter.notifyItemChanged vai
        //     re-habilitar caso necessite.
        botao.setOnClickListener(v -> {
            botao.setEnabled(false);
            botao.setBackgroundTintList(ColorStateList.valueOf(COR_FEITO));
            botao.setText("✓ Feito");
            listener.onAcaoMarcada(posicao, tipoAcao);
        });
    }

    // ------------------------------------------------------------
    // ViewHolder
    // ------------------------------------------------------------

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNomePlanta;
        TextView txtNomeEspecime;

        // Labels de dias restantes
        TextView txtIrrigar;
        TextView txtAdubar;
        TextView txtPodar;

        // Botões de ação
        Button btnIrrigar;
        Button btnAdubar;
        Button btnPodar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomePlanta   = itemView.findViewById(R.id.txtNomePlanta);
            txtNomeEspecime = itemView.findViewById(R.id.txtNomeEspecime);
            txtIrrigar      = itemView.findViewById(R.id.txtDiasIrrigar);
            txtAdubar       = itemView.findViewById(R.id.txtDiasAdubar);
            txtPodar        = itemView.findViewById(R.id.txtDiasPodar);
            btnIrrigar      = itemView.findViewById(R.id.btnIrrigar);
            btnAdubar       = itemView.findViewById(R.id.btnAdubar);
            btnPodar        = itemView.findViewById(R.id.btnPodar);
        }
    }
}