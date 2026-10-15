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

    private static final Pattern PERIODO = Pattern.compile(
            "(\\d+(?:[.,]\\d+)?)\\s*(horas?|hrs?|h|dias?|semanas?|meses|mes|anos?)");

    private CuidadoCalculator() {}

    public static Date proximaPorPeriodo(String texto) {
        if (texto == null || texto.trim().isEmpty()) return null;

        // tira acento e põe em minúsculas
        String t = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        if (t.contains("vez")) return null;

        Calendar cal = Calendar.getInstance();
        Matcher m = PERIODO.matcher(t);
        if (m.find()) {
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
            } else if (u.startsWith("a")) {
                cal.add(Calendar.YEAR, Math.max(1, (int) Math.ceil(n)));
            } else {
                return null;
            }
            return cal.getTime();
        }


        if (t.contains("diari"))       { cal.add(Calendar.DAY_OF_YEAR, 1);  return cal.getTime(); }
        if (t.contains("semanal"))     { cal.add(Calendar.DAY_OF_YEAR, 7);  return cal.getTime(); }
        if (t.contains("quinzenal"))   { cal.add(Calendar.DAY_OF_YEAR, 15); return cal.getTime(); }
        if (t.contains("mensal"))      { cal.add(Calendar.MONTH, 1);        return cal.getTime(); }
        if (t.contains("bimestral"))   { cal.add(Calendar.MONTH, 2);        return cal.getTime(); }
        if (t.contains("trimestral"))  { cal.add(Calendar.MONTH, 3);        return cal.getTime(); }
        if (t.contains("semestral"))   { cal.add(Calendar.MONTH, 6);        return cal.getTime(); }
        if (t.contains("anual"))       { cal.add(Calendar.YEAR, 1);         return cal.getTime(); }

        return null;
    }

    public static Date proximaIrrigacao(Especime e) {
        Calendar cal = Calendar.getInstance();

        if (e == null || e.getPeriodo_irrigacao() == null) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            return cal.getTime();
        }

        int n = Math.max(1, (int) Math.ceil(e.getPeriodo_irrigacao().doubleValue()));
        String unidade = e.getUnidade_irrigacao();

        if (unidade != null && unidade.toLowerCase(Locale.ROOT).contains("horas")) {
            cal.add(Calendar.HOUR_OF_DAY, n);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, n);
        }
        return cal.getTime();
    }

    public static String paraBackend(Date data) {
        if (data == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(data);
    }

    private static String normalizar(String texto) {
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