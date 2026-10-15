package com.network.plantfyth.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Date;

public class Plantio {
    //construtor
    public Plantio() {
    }

    public Plantio(Integer especimeId, Integer usuarioId, Usuario usuario, Especime especime, Integer id, String nome, String data_que_foi_plantado, boolean foi_regado_hoje, boolean pegou_sol_hoje, String previsao_proxima_irrigacao, String previsao_proxima_poda, String previsao_proxima_adubacao) {
        this.especimeId = especimeId;
        this.usuarioId = usuarioId;
        this.usuario = usuario;
        this.especime = especime;
        this.id = id;
        this.nome = nome;
        this.data_que_foi_plantado = data_que_foi_plantado;
        this.foi_regado_hoje = foi_regado_hoje;
        this.pegou_sol_hoje = pegou_sol_hoje;
        this.previsao_proxima_irrigacao = previsao_proxima_irrigacao;
        this.previsao_proxima_poda = previsao_proxima_poda;
        this.previsao_proxima_adubacao = previsao_proxima_adubacao;
    }
    //gettes e setters
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

    public boolean isPegou_sol_hoje() {
        return pegou_sol_hoje;
    }

    public void setPegou_sol_hoje(boolean pegou_sol_hoje) {
        this.pegou_sol_hoje = pegou_sol_hoje;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            Usuario usuario
    ) {
        this.usuario = usuario;
    }

    public String getPrevisao_proxima_adubacao() {
        return previsao_proxima_adubacao;
    }

    public void setPrevisao_proxima_adubacao(String previsao_proxima_adubacao) {
        this.previsao_proxima_adubacao = previsao_proxima_adubacao;
    }

    public Especime getEspecime() {
        return especime;
    }

    public void setEspecime(
            Especime especime
    ) {
        this.especime = especime;
    }

    //atributos
    @SerializedName("especimeId")
    private Integer especimeId;
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

    @SerializedName("pegouSolHoje")
    private boolean pegou_sol_hoje;

    //previsoes automaticas
    @SerializedName("previsaoProximaIrrigacao")
    private String previsao_proxima_irrigacao;
    @SerializedName("previsaoProximaPoda")
    private String previsao_proxima_poda;
    @SerializedName("previsaoProximaAdubacao")
    private String previsao_proxima_adubacao;

}

