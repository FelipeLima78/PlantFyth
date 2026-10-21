package com.network.plantfyth.model;


import com.google.gson.annotations.SerializedName;

public class Especime {
    //construtor
    public Especime() {
    }
    public Especime(Integer id, Integer periodo_irrigacao, String exposicao_a_luz, String descricao, Float tamanho_adulto_cm, String familia, String ciclo, String crescimento, String nome_cientifico, String nome_popular, String periodo_poda, String adubacao, String troca_de_vaso, String volume_irrigacao, String temperatura) {
        this.id = id;
        this.periodo_irrigacao = periodo_irrigacao;
        this.exposicao_a_luz = exposicao_a_luz;
        this.descricao = descricao;
        this.tamanho_adulto_cm = tamanho_adulto_cm;
        this.familia = familia;
        this.ciclo = ciclo;
        this.crescimento = crescimento;
        this.nome_cientifico = nome_cientifico;
        this.nome_popular = nome_popular;
        this.periodo_poda = periodo_poda;
        this.adubacao = adubacao;
        this.troca_de_vaso = troca_de_vaso;
        this.volume_irrigacao = volume_irrigacao;
        this.temperatura = temperatura;
    }
    //getters e setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPeriodo_irrigacao() {
        return periodo_irrigacao;
    }

    public void setPeriodo_irrigacao(Integer periodo_irrigacao) {
        this.periodo_irrigacao = periodo_irrigacao;
    }

    public String getExposicao_a_luz() {
        return exposicao_a_luz;
    }

    public void setExposicao_a_luz(String exposicao_a_luz) {
        this.exposicao_a_luz = exposicao_a_luz;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Float getTamanho_adulto_cm() {
        return tamanho_adulto_cm;
    }

    public void setTamanho_adulto_cm(Float tamanho_adulto_cm) {
        this.tamanho_adulto_cm = tamanho_adulto_cm;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCrescimento() {
        return crescimento;
    }

    public void setCrescimento(String crescimento) {
        this.crescimento = crescimento;
    }

    public String getNome_cientifico() {
        return nome_cientifico;
    }

    public void setNome_cientifico(String nome_cientifico) {
        this.nome_cientifico = nome_cientifico;
    }

    public String getNome_popular() {
        return nome_popular;
    }

    public void setNome_popular(String nome_popular) {
        this.nome_popular = nome_popular;
    }

    public String getPeriodo_poda() {
        return periodo_poda;
    }

    public void setPeriodo_poda(String periodo_poda) {
        this.periodo_poda = periodo_poda;
    }

    public String getAdubacao() {
        return adubacao;
    }

    public void setAdubacao(String adubacao) {
        this.adubacao = adubacao;
    }

    public String getTroca_de_vaso() {
        return troca_de_vaso;
    }

    public void setTroca_de_vaso(String troca_de_vaso) {
        this.troca_de_vaso = troca_de_vaso;
    }

    public String getVolume_irrigacao() {
        return volume_irrigacao;
    }

    public void setVolume_irrigacao(String volume_irrigacao) {
        this.volume_irrigacao = volume_irrigacao;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }



    //atributos
    private Integer id;
    @Override
    public String toString() {
        return nome_popular;
    }
    @SerializedName("periodoIrrigacao")
    private Integer periodo_irrigacao;
    @SerializedName("exposicaoALuz")
    private String exposicao_a_luz;
    @SerializedName("descricao")
    private String descricao;
    @SerializedName("tamanho_adulto_cm")
    private Float tamanho_adulto_cm;
    @SerializedName("familia")
    private String familia;
    @SerializedName("ciclo")
    private String ciclo;
    @SerializedName("crescimento")
    private String crescimento;
    @SerializedName("nome_cientifico")
    private String nome_cientifico;
    @SerializedName("nome_popular")
    private String nome_popular;
    @SerializedName("periodoPoda")
    private String periodo_poda;
    @SerializedName("adubacao")
    private String adubacao;
    @SerializedName("troca_de_vaso")
    private String troca_de_vaso;

    @SerializedName("volume_irrigacao")
    private String volume_irrigacao;
    @SerializedName("temperatura")
    private String temperatura;

}
