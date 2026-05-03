package com.network.plantfyth.DatabaseConnection;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private RequestQueue requestQueue;

    // Troque pela URL do seu site no InfinityFree
    public static final String BASE_URL = "https://planfyth.free.nf";

    private DatabaseConnection(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized DatabaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseConnection(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}