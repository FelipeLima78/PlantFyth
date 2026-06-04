package com.network.plantfyth.retrofit;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.model.Usuario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlantFythAPI {

    @GET("especimes")
    Call<List<Especime>> listarEspecimes();

    @GET("plantios")
    Call<List<Plantio>> listarTodos();
    @POST("plantios/inserir")
    Call<Plantio> savePlantio(@Body Plantio plantio);

    @GET("plantios/usuario/{id}") Call<List<Plantio>>
    buscarPlantasUsuario (@Path("id") Integer usuarioId);

    @POST("usuarios/login")
    Call<ResponseBody> LoginUsuario(@Body Usuario request);

    @GET("usuarios/email/{email}")
    Call<Usuario> buscarUsuarioPorEmail(
            @Path("email") String email);
    @POST("usuarios/inserir")
    Call<Usuario> saveUsuario(@Body Usuario usuario);

    @DELETE("usuarios/deletar/{id}")
    Call<ResponseBody> deleteUsuario(@Path("id")Integer id);

}
