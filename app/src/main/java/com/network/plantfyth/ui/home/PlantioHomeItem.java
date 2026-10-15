package com.network.plantfyth.ui.home;

import android.graphics.Color;

import com.network.plantfyth.model.Plantio;

public class PlantioHomeItem {

    public static final int COR_ATRASADO = Color.parseColor("#D32F2F");
    public static final int COR_HOJE = Color.parseColor("#F57C00");
    public static final int COR_OK = Color.parseColor("#2E7D32");
    public static final int COR_INDISPONIVEL = Color.parseColor("#8A8A8A");

    public Plantio plantio;
    public AcaoStatus irrigacao;
    public AcaoStatus poda;
    public AcaoStatus sol;
    public AcaoStatus adubo;

    public HomeFragment.TipoAcao salvandoAcao;

    public static class AcaoStatus {
        public final String textoTempo;
        public final int cor;
        public final boolean atrasada;
        public final boolean disponivel;
        public final boolean abrirInfo;
        public final boolean semBotao;


        public AcaoStatus(String textoTempo, int cor, boolean atrasada, boolean disponivel) {
            this(textoTempo, cor, atrasada, disponivel, false, false);
        }

        public AcaoStatus(String textoTempo, int cor, boolean atrasada, boolean disponivel,
                          boolean abrirInfo, boolean semBotao) {
            this.textoTempo = textoTempo;
            this.cor = cor;
            this.atrasada = atrasada;
            this.disponivel = disponivel;
            this.abrirInfo = abrirInfo;
            this.semBotao = semBotao;
        }

        public static AcaoStatus consultarInfo() {
            return new AcaoStatus("Consulte as informacoes", COR_INDISPONIVEL,
                    false, true, true, false);
        }

        public static AcaoStatus naoNecessario() {
            return new AcaoStatus("Nao precisa", COR_INDISPONIVEL,
                    false, false, false, true);
        }
    }
}
