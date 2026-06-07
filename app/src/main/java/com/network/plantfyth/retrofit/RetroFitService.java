package com.network.plantfyth.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitService {
    private Retrofit retrofit;

    public RetroFitService(){
        initializeRetroFit();
    }
    private void initializeRetroFit(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.15.15:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }


    private static class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        private static final String[] FORMATOS = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "dd/MM/yyyy"
        };

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
            formato.setTimeZone(TimeZone.getDefault());
            return new JsonPrimitive(formato.format(src));
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json == null || json.isJsonNull()) return null;

            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return new Date(primitive.getAsLong());
            }

            String valor = primitive.getAsString();
            for (String formatoTexto : FORMATOS) {
                try {
                    SimpleDateFormat formato = new SimpleDateFormat(formatoTexto, Locale.US);
                    formato.setLenient(false);
                    return formato.parse(valor);
                } catch (ParseException ignored) {
                }
            }

            try {
                SimpleDateFormat formatoLegado = new SimpleDateFormat("MMM d, yyyy, h:mm:ss a", Locale.ENGLISH);
                formatoLegado.setLenient(false);
                return formatoLegado.parse(valor);
            } catch (ParseException e) {
                throw new JsonParseException("Formato de data invalido: " + valor, e);
            }
        }
    }
}
