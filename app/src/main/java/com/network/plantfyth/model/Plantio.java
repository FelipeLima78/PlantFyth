package com.network.plantfyth.model;

import java.time.LocalDateTime;
import java.util.Date;

public class Plantio {
    private Integer id;
    private String nome;
    private Date dataQueFoiPlantado;
    private boolean foiRegado;

    //datas importantes
    private Date dataAdubo;
    private LocalDateTime horarioAreIrrigar;

    //previsoes automaticas
    private Date previsaoProximaIrrigacao;
    private Date previsaoProximaAdubacao;
    private Date previsaoProximaPoda;
    //Informações dinâmicas do app
    private int nivelUmidade;
    private float tamanhoAtualCM;
    private float previsaoTamanho;
    private String plantadaComo;
    private String imagemPersonalizada;
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

    public Date getDataQueFoiPlantado() {
        return dataQueFoiPlantado;
    }

    public void setDataQueFoiPlantado(Date dataQueFoiPlantado) {
        this.dataQueFoiPlantado = dataQueFoiPlantado;
    }

    public Boolean getFoiRegado() {
        return foiRegado;
    }

    public void setFoiRegado(Boolean foiRegado) {
        this.foiRegado = foiRegado;
    }

    public Date getDataAdubo() {
        return dataAdubo;
    }

    public void setDataAdubo(Date dataAdubo) {
        this.dataAdubo = dataAdubo;
    }

    public LocalDateTime getHorarioAreIrrigar() {
        return horarioAreIrrigar;
    }

    public void setHorarioAreIrrigar(LocalDateTime horarioAreIrrigar) {
        this.horarioAreIrrigar = horarioAreIrrigar;
    }

    public Date getPrevisaoProximaIrrigacao() {
        return previsaoProximaIrrigacao;
    }

    public void setPrevisaoProximaIrrigacao(Date previsaoProximaIrrigacao) {
        this.previsaoProximaIrrigacao = previsaoProximaIrrigacao;
    }

    public Date getPrevisaoProximaAdubacao() {
        return previsaoProximaAdubacao;
    }

    public void setPrevisaoProximaAdubacao(Date previsaoProximaAdubacao) {
        this.previsaoProximaAdubacao = previsaoProximaAdubacao;
    }

    public Date getPrevisaoProximaPoda() {
        return previsaoProximaPoda;
    }

    public void setPrevisaoProximaPoda(Date previsaoProximaPoda) {
        this.previsaoProximaPoda = previsaoProximaPoda;
    }

    public int getNivelUmidade() {
        return nivelUmidade;
    }

    public void setNivelUmidade(int nivelUmidade) {
        this.nivelUmidade = nivelUmidade;
    }

    public float getTamanhoAtualCM() {
        return tamanhoAtualCM;
    }

    public void setTamanhoAtualCM(float tamanhoAtualCM) {
        this.tamanhoAtualCM = tamanhoAtualCM;
    }

    public float getPrevisaoTamanho() {
        return previsaoTamanho;
    }

    public void setPrevisaoTamanho(float previsaoTamanho) {
        this.previsaoTamanho = previsaoTamanho;
    }

    public String getPlantadaComo() {
        return plantadaComo;
    }

    public void setPlantadaComo(String plantadaComo) {
        this.plantadaComo = plantadaComo;
    }

    public String getImagemPersonalizada() {
        return imagemPersonalizada;
    }

    public void setImagemPersonalizada(String imagemPersonalizada) {
        this.imagemPersonalizada = imagemPersonalizada;
    }
}
