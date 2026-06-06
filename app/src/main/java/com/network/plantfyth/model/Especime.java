package com.network.plantfyth.model;


import com.google.gson.annotations.SerializedName;

public class Especime {
    //construtor

    @Override
    public String toString() {
        return this.nome_popular != null ? this.nome_popular : "Planta sem nome";
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPeriodo_rrigacao() {
        return periodo_irrigacao;
    }

    public void setPeriodo_rrigacao(String periodo_irrigacao) {
        this.periodo_irrigacao = periodo_irrigacao;
    }

    public String getUnidade_irrigacao() {
        return unidade_irrigacao;
    }

    public void setUnidade_irrigacao(String unidade_irrigacao) {
        this.unidade_irrigacao = unidade_irrigacao;
    }

    public String getExposicao_A_Luz() {
        return exposicao_a_luz;
    }

    public void setExposicao_A_Luz(String exposicao_a_luz) {
        this.exposicao_a_luz = exposicao_a_luz;
    }

    public String getPeriodo_poda() {
        return periodo_poda;
    }

    public void setPeriodo_poda(String periodo_poda) {
        this.periodo_poda = periodo_poda;
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

    public Float getTamanho_muda_cm() {
        return tamanho_muda_cm;
    }

    public void setTamanho_muda_cm(Float tamanho_muda_cm) {
        this.tamanho_muda_cm = tamanho_muda_cm;
    }

    public String getCaminho_imagem_padrao() {
        return caminho_imagem_padrao;
    }

    public void setCaminho_imagem_padrao(String caminho_imagem_padrao) {
        this.caminho_imagem_padrao = caminho_imagem_padrao;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public Float getCrescimento_diario() {
        return crescimento_diario;
    }

    public void setCrescimento_diario(Float crescimento_diario) {
        this.crescimento_diario = crescimento_diario;
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

    public Integer getPerenual_id() {
        return perenual_id;
    }

    public void setPerenual_id(Integer perenual_id) {
        this.perenual_id = perenual_id;
    }


    public Especime() {
    }

    public String getPeriodo_irrigacao() {
        return periodo_irrigacao;
    }

    public void setPeriodo_irrigacao(String periodo_irrigacao) {
        this.periodo_irrigacao = periodo_irrigacao;
    }

    public String getExposicao_a_luz() {
        return exposicao_a_luz;
    }

    public void setExposicao_a_luz(String exposicao_a_luz) {
        this.exposicao_a_luz = exposicao_a_luz;
    }

    private Integer id;

    private String periodo_irrigacao;

    private String unidade_irrigacao;

    private String exposicao_a_luz;

    private String periodo_poda;
    private String descricao;

    private Float tamanho_adulto_cm;

    private Float tamanho_muda_cm;

    private String caminho_imagem_padrao;

    private String familia;

    private Float crescimento_diario;

    private String ciclo;

    private String crescimento;

    private String nome_cientifico;

    @SerializedName("nome_popular")
    private String nome_popular;

    private Integer perenual_id;


}
