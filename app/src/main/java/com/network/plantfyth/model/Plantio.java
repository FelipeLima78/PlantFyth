package com.network.plantfyth.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Plantio {

    private Integer id;
    private String nome;

    @SerializedName(value = "data_que_foi_Plantado", alternate = {"data_que_foi_plantado"})
    private String data_que_foi_plantado;

    @SerializedName(value = "foiRegadoHoje", alternate = {"foi_regado_hoje"})
    private boolean foi_regado_hoje;

    private Date dataAdubo;

    @SerializedName(value = "horarioAteIrrigar", alternate = {"horario_ate_irrigar"})
    private Date horario_ate_irrigar;

    @SerializedName(value = "previsaoProximaIrrigacao", alternate = {"previsao_proxima_irrigacao"})
    private Date previsao_proxima_irrigacao;

    @SerializedName(value = "previsaoProximaAdubacao", alternate = {"previsao_proxima_adubacao"})
    private Date previsaoProximaAdubacao;

    @SerializedName(value = "previsaoProximaPoda", alternate = {"previsao_proxima_poda"})
    private Date previsao_proxima_poda;

    @SerializedName(value = "tamanhoAtualCM", alternate = {"tamanho_atual_cm"})
    private float tamanho_atual_cm;

    @SerializedName(value = "previsaoTamanhoCM", alternate = {"previsao_tamanho_cm"})
    private float previsao_tamanho_cm;

    @SerializedName(value = "plantadaComo", alternate = {"plantada_como"})
    private String plantada_como;

    @SerializedName(value = "imagemPersonalizada", alternate = {"imagem_personalizada"})
    private String imagem_personalizada;

    @SerializedName("usuarioId")
    private Integer usuarioId;

    @SerializedName("especimeId")
    private Integer especimeId;

    private Usuario usuario;
    private Especime especime;

    public Plantio() {
    }

    public Plantio(Integer id, String nome, String data_que_foi_plantado, boolean foi_regado_hoje,
                   Date dataAdubo, Date horario_ate_irrigar, Date previsao_proxima_irrigacao,
                   Date previsao_proxima_poda, float tamanho_atual_cm, float previsaoTamanho,
                   String plantada_como, String imagem_personalizada) {
        this.id = id;
        this.nome = nome;
        this.data_que_foi_plantado = data_que_foi_plantado;
        this.foi_regado_hoje = foi_regado_hoje;
        this.dataAdubo = dataAdubo;
        this.horario_ate_irrigar = horario_ate_irrigar;
        this.previsao_proxima_irrigacao = previsao_proxima_irrigacao;
        this.previsao_proxima_poda = previsao_proxima_poda;
        this.tamanho_atual_cm = tamanho_atual_cm;
        this.previsao_tamanho_cm = previsaoTamanho;
        this.plantada_como = plantada_como;
        this.imagem_personalizada = imagem_personalizada;
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

    public Boolean getFoiRegado() {
        return foi_regado_hoje;
    }

    public void setFoiRegado(Boolean foi_regado_hoje) {
        this.foi_regado_hoje = foi_regado_hoje != null && foi_regado_hoje;
    }

    public Date getDataAdubo() {
        return dataAdubo;
    }

    public void setDataAdubo(Date dataAdubo) {
        this.dataAdubo = dataAdubo;
    }

    public Date getHorarioAreIrrigar() {
        return horario_ate_irrigar;
    }

    public void setHorarioAreIrrigar(Date horario_ate_irrigar) {
        this.horario_ate_irrigar = horario_ate_irrigar;
    }

    public Date getPrevisaoProximaIrrigacao() {
        return previsao_proxima_irrigacao;
    }

    public void setPrevisaoProximaIrrigacao(Date previsao_proxima_irrigacao) {
        this.previsao_proxima_irrigacao = previsao_proxima_irrigacao;
    }

    public Date getPrevisaoProximaAdubacao() {
        return previsaoProximaAdubacao;
    }

    public void setPrevisaoProximaAdubacao(Date previsaoProximaAdubacao) {
        this.previsaoProximaAdubacao = previsaoProximaAdubacao;
    }

    public Date getPrevisaoProximaPoda() {
        return previsao_proxima_poda;
    }

    public void setPrevisaoProximaPoda(Date previsao_proxima_poda) {
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

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getEspecimeId() {
        return especimeId;
    }

    public void setEspecimeId(Integer especimeId) {
        this.especimeId = especimeId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Especime getEspecime() {
        return especime;
    }

    public void setEspecime(Especime especime) {
        this.especime = especime;
    }
}
