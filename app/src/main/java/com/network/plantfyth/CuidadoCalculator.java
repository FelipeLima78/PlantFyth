package com.network.plantfyth;

import com.network.plantfyth.model.Especime;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CuidadoCalculator {

    // O texto INTEIRO precisa ser um intervalo: "2 semanas", "a cada 3 meses", "12 horas".
    // Frase com outras informações ("a floração dura 2 semanas") NÃO vira data.
    private static final Pattern INTERVALO = Pattern.compile(
            "^(?:a cada\\s+)?(\\d+(?:[.,]\\d+)?)\\s*(horas?|hrs?|h|dias?|semanas?|meses|mes|anos?)$");

    private CuidadoCalculator() {
    }

    /**
     * Poda e adubação. Aceita um intervalo ("2 semanas", "A cada 3 meses") ou um nome
     * (Diária, Semanal, Quinzenal, Mensal, Bimestral, Trimestral, Semestral, Anual).
     * Qualquer outro texto devolve null (o card mostra "Ver info").
     */
    public static Date proximaPorPeriodo(String texto) {
        if (texto == null) return null;

        String t = normalizar(texto).trim();
        if (t.isEmpty() || t.length() > 40) return null;

        Calendar cal = Calendar.getInstance();

        Matcher m = INTERVALO.matcher(t);
        if (m.matches()) {
            double n = Double.parseDouble(m.group(1).replace(",", "."));
            String u = m.group(2);

            if (u.startsWith("h")) {
                cal.add(Calendar.HOUR_OF_DAY, Math.max(1, (int) Math.ceil(n)));
            } else if (u.startsWith("d")) {
                cal.add(Calendar.DAY_OF_YEAR, Math.max(1, (int) Math.ceil(n)));
            } else if (u.startsWith("s")) {                       // semana vira dias
                cal.add(Calendar.DAY_OF_YEAR, Math.max(1, (int) Math.round(n * 7)));
            } else if (u.startsWith("m")) {
                cal.add(Calendar.MONTH, Math.max(1, (int) Math.ceil(n)));
            } else {
                cal.add(Calendar.YEAR, Math.max(1, (int) Math.ceil(n)));
            }
            return cal.getTime();
        }

        switch (t) {
            case "diaria":
            case "diariamente":
                cal.add(Calendar.DAY_OF_YEAR, 1);
                return cal.getTime();
            case "semanal":
                cal.add(Calendar.DAY_OF_YEAR, 7);
                return cal.getTime();
            case "quinzenal":
                cal.add(Calendar.DAY_OF_YEAR, 15);
                return cal.getTime();
            case "mensal":
                cal.add(Calendar.MONTH, 1);
                return cal.getTime();
            case "bimestral":
                cal.add(Calendar.MONTH, 2);
                return cal.getTime();
            case "trimestral":
                cal.add(Calendar.MONTH, 3);
                return cal.getTime();
            case "semestral":
                cal.add(Calendar.MONTH, 6);
                return cal.getTime();
            case "anual":
                cal.add(Calendar.YEAR, 1);
                return cal.getTime();
            default:
                return null;
        }
    }

    /** Irrigação: o banco guarda o intervalo em HORAS. */
    public static Date proximaIrrigacao(Especime e) {
        int horas = (e == null || e.getPeriodo_irrigacao() == null)
                ? 24
                : Math.max(1, e.getPeriodo_irrigacao().intValue());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, horas);
        return cal.getTime();
    }

    public static String paraBackend(Date data) {
        if (data == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(data);
    }

    /** Tira acento e põe em minúsculas. Nunca devolve null. */
    public static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    public static boolean naoPrecisa(String texto) {
        if (texto == null) return false;
        String t = normalizar(texto);
        return t.contains("nao precisa")
                || t.contains("nao necessita")
                || t.contains("nao requer")
                || t.contains("sem poda")
                || t.contains("dispensa");
    }
}