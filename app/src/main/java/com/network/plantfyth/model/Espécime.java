package com.network.plantfyth.model;


public class Espécime {
    //construtor

    public Espécime(Integer id, String nome, int periodoIrrigacao, int periodoAdubacao, String exposicaoALuz, int periodoPoda, String descricao, float tamanhoAdulto, float tamanhoMuda, float crescimentoDiario, float vasoMinimoCM, String caminhoImagemPadrao, int umidadeIdeal, int temperaturaIdealMin, int temperaturaIdealMax) {
        this.id = id;
        this.nome = nome;
        this.periodoIrrigacao = periodoIrrigacao;
        this.periodoAdubacao = periodoAdubacao;
        this.exposicaoALuz = exposicaoALuz;
        this.periodoPoda = periodoPoda;
        this.descricao = descricao;
        this.tamanhoAdulto = tamanhoAdulto;
        this.tamanhoMuda = tamanhoMuda;
        this.crescimentoDiario = crescimentoDiario;
        this.vasoMinimoCM = vasoMinimoCM;
        this.caminhoImagemPadrao = caminhoImagemPadrao;
        this.umidadeIdeal = umidadeIdeal;
        this.temperaturaIdealMin = temperaturaIdealMin;
        this.temperaturaIdealMax = temperaturaIdealMax;
    }

    public Espécime() {
    }

    private Integer id;
    private String nome;
    private int periodoIrrigacao;
    private int periodoAdubacao;
    private String exposicaoALuz;
    private int periodoPoda;
    private String descricao;

    //dados de crescimento

    private float tamanhoAdulto;
    private float tamanhoMuda;
    private float crescimentoDiario;

    //dados gerais
    private float vasoMinimoCM;
    private String caminhoImagemPadrao;
    private int umidadeIdeal;
    private int temperaturaIdealMin;
    private int temperaturaIdealMax;

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

    public int getPeriodoIrrigacao() {
        return periodoIrrigacao;
    }

    public void setPeriodoIrrigacao(int periodoIrrigacao) {
        this.periodoIrrigacao = periodoIrrigacao;
    }

    public int getPeriodoAdubacao() {
        return periodoAdubacao;
    }

    public void setPeriodoAdubacao(int periodoAdubacao) {
        this.periodoAdubacao = periodoAdubacao;
    }

    public String getExposicaoALuz() {
        return exposicaoALuz;
    }

    public void setExposicaoALuz(String exposicaoALuz) {
        this.exposicaoALuz = exposicaoALuz;
    }

    public int getPeriodoPoda() {
        return periodoPoda;
    }

    public void setPeriodoPoda(int periodoPoda) {
        this.periodoPoda = periodoPoda;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public float getTamanhoAdulto() {
        return tamanhoAdulto;
    }

    public void setTamanhoAdulto(float tamanhoAdulto) {
        this.tamanhoAdulto = tamanhoAdulto;
    }

    public float getTamanhoMuda() {
        return tamanhoMuda;
    }

    public void setTamanhoMuda(float tamanhoMuda) {
        this.tamanhoMuda = tamanhoMuda;
    }

    public float getCrescimentoDiario() {
        return crescimentoDiario;
    }

    public void setCrescimentoDiario(float crescimentoDiario) {
        this.crescimentoDiario = crescimentoDiario;
    }

    public float getVasoMinimoCM() {
        return vasoMinimoCM;
    }

    public void setVasoMinimoCM(float vasoMinimoCM) {
        this.vasoMinimoCM = vasoMinimoCM;
    }

    public String getCaminhoImagemPadrao() {
        return caminhoImagemPadrao;
    }

    public void setCaminhoImagemPadrao(String caminhoImagemPadrao) {
        this.caminhoImagemPadrao = caminhoImagemPadrao;
    }

    public int getUmidadeIdeal() {
        return umidadeIdeal;
    }

    public void setUmidadeIdeal(int umidadeIdeal) {
        this.umidadeIdeal = umidadeIdeal;
    }

    public int getTemperaturaIdealMin() {
        return temperaturaIdealMin;
    }

    public void setTemperaturaIdealMin(int temperaturaIdealMin) {
        this.temperaturaIdealMin = temperaturaIdealMin;
    }

    public int getTemperaturaIdealMax() {
        return temperaturaIdealMax;
    }

    public void setTemperaturaIdealMax(int temperaturaIdealMax) {
        this.temperaturaIdealMax = temperaturaIdealMax;
    }
}
