package com.network.plantfyth.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Date;

public class Plantio {
    public Plantio() {
    }

    public Plantio(Integer id, String nome, String data_que_foi_plantado, boolean foi_regado_hoje, String previsao_proxima_irrigacao,  String previsao_proxima_poda, float tamanho_atual_cm, float previsaoTamanho, String plantada_como, String imagem_personalizada) {
        this.id = id;
        this.nome = nome;
        this.data_que_foi_plantado = data_que_foi_plantado;
        this.foi_regado_hoje = foi_regado_hoje;
        this.previsao_proxima_irrigacao = previsao_proxima_irrigacao;
        this.previsao_proxima_poda = previsao_proxima_poda;
        this.tamanho_atual_cm = tamanho_atual_cm;
        this.previsao_tamanho_cm = previsao_tamanho_cm;
        this.plantada_como = plantada_como;
        this.imagem_personalizada = imagem_personalizada;
    }

    @SerializedName("especimeId")
    private Integer especimeId;

    public Integer getEspecimeId() {
        return especimeId;
    }

    public void setEspecimeId(Integer especimeId) {
        this.especimeId = especimeId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    @SerializedName("usuarioId")
    private Integer usuarioId;
    @SerializedName("usuario")
    private Usuario usuario;
    @SerializedName("especime")
    private Especime especime;
    @SerializedName("id")
    private Integer id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("data_que_foi_Plantado")
    private String data_que_foi_plantado;
    @SerializedName("foiRegadoHoje")
    private boolean foi_regado_hoje;


    //previsoes automaticas
    @SerializedName("previsaoProximaIrrigacao")
    private String previsao_proxima_irrigacao;
    @SerializedName("previsaoProximaPoda")
    private String previsao_proxima_poda;
    //Informações dinâmicas do app
    @SerializedName("tamanhoAtualCM")
    private float tamanho_atual_cm;
    @SerializedName("previsaoTamanhoCM")
    private float previsao_tamanho_cm;
    @SerializedName("plantadaComo")
    private String plantada_como;
    @SerializedName("imagemPersonalizada")
    private String imagem_personalizada;
    //getters e setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataQueFoiPlantado() {
        return data_que_foi_plantado;
    }

    public void setDataQueFoiPlantado(String data_que_foi_plantado) {
        this.data_que_foi_plantado = data_que_foi_plantado;
    }

    public boolean isFoi_regado_hoje() {
        return foi_regado_hoje;
    }

    public void setFoi_regado_hoje(boolean foi_regado_hoje) {
        this.foi_regado_hoje = foi_regado_hoje;
    }

    public String getPrevisaoProximaIrrigacao() {
        return previsao_proxima_irrigacao;
    }

    public void setPrevisaoProximaIrrigacao(String previsao_proxima_irrigacao) {
        this.previsao_proxima_irrigacao = previsao_proxima_irrigacao;
    }

    public String getPrevisaoProximaPoda() {
        return previsao_proxima_poda;
    }

    public void setPrevisaoProximaPoda(String previsao_proxima_poda) {
        this.previsao_proxima_poda = previsao_proxima_poda;
    }
    public float getTamanhoAtualCM() {
        return tamanho_atual_cm;
    }

    public void setTamanhoAtualCM(float tamanho_atual_cm) {
        this.tamanho_atual_cm = tamanho_atual_cm;
    }

    public float getPrevisaoTamanho() {
        return previsao_tamanho_cm;
    }

    public void setPrevisaoTamanho(float previsao_tamanho_cm) {
        this.previsao_tamanho_cm = previsao_tamanho_cm;
    }

    public String getPlantadaComo() {
        return plantada_como;
    }

    public void setPlantadaComo(String plantada_como) {
        this.plantada_como = plantada_como;
    }

    public String getImagemPersonalizada() {
        return imagem_personalizada;
    }

    public void setImagemPersonalizada(String imagem_personalizada) {
        this.imagem_personalizada = imagem_personalizada;
    }
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            Usuario usuario
    ) {
        this.usuario = usuario;
    }

    public Especime getEspecime() {
        return especime;
    }

    public void setEspecime(
            Especime especime
    ) {
        this.especime = especime;
    }
}

