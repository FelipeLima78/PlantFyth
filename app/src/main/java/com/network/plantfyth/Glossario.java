package com.network.plantfyth;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

public final class Glossario {

    private static final Map<String, String[]> TERMOS = new HashMap<>();

    private static void add(String chave, String explicacao, String dica) {
        TERMOS.put(chave, new String[]{explicacao, dica});
    }

    static {


        add("luz filtrada",
                "Luz solar filtrada pela copa das árvores durante todo o dia.", null);
        add("sol pleno", "Luz do sol em contato direto com o plantio por 6 ou mais horas todos os dias", null);
        add("sombra parcial",
                "Luz do sol em contato direto com o plantio por parte do dia, em geral de 2 a 6 horas.", null);
        add("sombra profunda",
                "Luz do sol em contato direto com o plantio por menos de 2 horas.", null);

        String dicaSolo = "Enfie o dedo uns 5 cm no substrato. Se estiver seco, provavelmente é hora de regar.";
        // irrigacao
        add("baixa", "Deixe o solo secar moderadamente entre as regras", dicaSolo);
        add("moderada", "Mantenha o solo levemente úmido", dicaSolo);
        add("alta", "Mantenha o solo constantemente úmido", dicaSolo);
    }

    private Glossario() {}

    public static void aplicar(Context ctx, TextView txt, String rotulo, String valor) {
        String v = (valor == null || valor.trim().isEmpty()) ? "—" : valor;
        txt.setText(rotulo == null ? v : rotulo + ": " + v);

        String[] info = valor == null ? null
                : TERMOS.get(CuidadoCalculator.normalizar(valor).trim());
        if (info == null) {
            txt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            txt.setOnClickListener(null);
            txt.setClickable(false);
            return;
        }

        txt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_info, 0);
        txt.setCompoundDrawablePadding(
                Math.round(6 * ctx.getResources().getDisplayMetrics().density));
        txt.setOnClickListener(x -> mostrar(ctx, v, info));
    }

    private static void mostrar(Context ctx, String titulo, String[] info) {
        SpannableStringBuilder msg = new SpannableStringBuilder(info[0]);
        if (info[1] != null) {
            msg.append("\n\n");                        // linha em branco
            int inicio = msg.length();
            msg.append("Dica: ");
            msg.setSpan(new StyleSpan(Typeface.BOLD), inicio, msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msg.append(info[1]);
        }
        new AlertDialog.Builder(ctx)
                .setTitle(titulo)
                .setMessage(msg)
                .setPositiveButton("Entendi", null)
                .show();
    }
}