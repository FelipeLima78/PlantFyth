package com.api.api_plantfyth.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "especime")
public class Especime {
    //atributos

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

    @OneToMany(mappedBy = "plantio")
    @JsonManagedReference
    private List<Plantio> plantios;
}
