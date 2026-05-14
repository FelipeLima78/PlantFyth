package com.network.plantfyth.retrofit;

import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PlantFythAPI {

    @GET("/plantios")
    Call<List<Plantio>> listarTodos();


    @POST("usuarios/inserir")
    Call<Usuario> save(@Body Usuario usuario);



}
