package com.network.plantfyth.model;

import java.text.Normalizer;
import java.util.Locale;

public final class PlantioSearch {

    private PlantioSearch() {
    }

    public static boolean matches(Plantio plantio, String query) {
        String busca = normalizar(query);
        if (busca.isEmpty()) return true;
        if (plantio == null) return false;

        if (normalizar(plantio.getNome()).contains(busca)) return true;
        Especime especime = plantio.getEspecime();
        return especime != null
                && (normalizar(especime.getNome_popular()).contains(busca)
                || normalizar(especime.getNome_cientifico()).contains(busca));
    }

    private static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }
}
