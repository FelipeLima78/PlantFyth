    package com.network.plantfyth.model;

    import java.time.LocalDateTime;
    import java.util.Date;

    public class Plantio {
        public Plantio() {
        }

        public Plantio(Integer id, String nome, String dataQueFoiPlantado, boolean foiRegado, Date dataAdubo, LocalDateTime horarioAreIrrigar, Date previsaoProximaIrrigacao, Date previsaoProximaAdubacao, Date previsaoProximaPoda, int nivelUmidade, float tamanhoAtualCM, float previsaoTamanho, String plantadaComo, String imagemPersonalizada) {
            this.id = id;
            this.nome = nome;
            this.dataQueFoiPlantado = dataQueFoiPlantado;
            this.foiRegado = foiRegado;
            this.dataAdubo = dataAdubo;
            this.horarioAreIrrigar = horarioAreIrrigar;
            this.previsaoProximaIrrigacao = previsaoProximaIrrigacao;
            this.previsaoProximaAdubacao = previsaoProximaAdubacao;
            this.previsaoProximaPoda = previsaoProximaPoda;

            this.nivelUmidade = nivelUmidade;
            this.tamanhoAtualCM = tamanhoAtualCM;
            this.previsaoTamanho = previsaoTamanho;
            this.plantadaComo = plantadaComo;
            this.imagemPersonalizada = imagemPersonalizada;
        }

        private Usuario usuario;
        private Especime especime;
        private Integer id;
        private String nome;
        private String dataQueFoiPlantado;
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
        private LocalDateTime ultimaIrrigacao;
        private LocalDateTime ultimaAdubacao;
        private LocalDateTime ultimaPoda;
        //getters e setters

        public Integer getId() {
            return id;
        }

        public void setFoiRegado(boolean foiRegado) {
            this.foiRegado = foiRegado;
        }

        public LocalDateTime getUltimaIrrigacao() {
            return ultimaIrrigacao;
        }

        public void setUltimaIrrigacao(LocalDateTime ultimaIrrigacao) {
            this.ultimaIrrigacao = ultimaIrrigacao;
        }

        public LocalDateTime getUltimaAdubacao() {
            return ultimaAdubacao;
        }

        public void setUltimaAdubacao(LocalDateTime ultimaAdubacao) {
            this.ultimaAdubacao = ultimaAdubacao;
        }

        public LocalDateTime getUltimaPoda() {
            return ultimaPoda;
        }

        public void setUltimaPoda(LocalDateTime ultimaPoda) {
            this.ultimaPoda = ultimaPoda;
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
            return dataQueFoiPlantado;
        }

        public void setDataQueFoiPlantado(String dataQueFoiPlantado) {
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
