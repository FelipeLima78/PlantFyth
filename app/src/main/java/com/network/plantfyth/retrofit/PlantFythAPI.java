package com.network.plantfyth.retrofit;

import com.network.plantfyth.model.Especime;
import com.network.plantfyth.model.Plantio;
import com.network.plantfyth.model.Usuario;
import retrofit2.http.PUT;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlantFythAPI {

    //Listas
    @GET("plantios")
    Call<List<Plantio>> listarPlantios();
    @GET("especimes")
    Call<List<Especime>> listarEspecimes();


    //plantios

    @GET("api/plants/indoor")
    Call<List<Especime>> listarIndoorPlants();
    @POST("plantios/inserir")
    Call<Plantio> savePlantio(@Body Plantio plantio);
    @PUT("plantios/id/{id}")
    Call<Plantio> atualizarPlantio(@Body Plantio plantio, @Path("id") Integer id);
    @GET("plantios/usuario/{id}") Call<List<Plantio>>
    buscarPlantasUsuario (@Path("id") Integer usuarioId);
    @DELETE("plantios/{id}")
    Call<Void> deletarPlantio (@Path("id") Integer plantioId);
    @GET("plantios/id/{id}")
    Call<Plantio> buscarPlantaPorId (@Path("id") Integer plantioId);
    @PUT("plantios/id/{id}")
    Call<Plantio> atualizarPlanta (@Path("id") Integer id, @Body Plantio plantio);

    //especimes
    @GET("especimes/indoor/{perenualId}")
    Call<Especime> buscarDetalhes(@Path("perenualId")int perenualId);
    @GET("especimes/id/{id}")
    Call<Especime> buscarEspecimePorId(@Path("id") Integer id);


    //usuario
    @POST("usuarios/login")
    Call<ResponseBody> LoginUsuario(@Body Usuario request);

    @GET("usuarios/email/{email}")
    Call<Usuario> buscarUsuarioPorEmail(@Path("email") String email);
    @POST("usuarios/inserir")
    Call<Usuario> saveUsuario(@Body Usuario usuario);

    @DELETE("usuarios/deletar/{id}")
    Call<ResponseBody> deleteUsuario(@Path("id")Integer id);

    //chatbot
    @POST("chat")
    @Headers("Content-Type: text/plain")
    Call<ResponseBody> chat(@Body RequestBody pergunta);
}
