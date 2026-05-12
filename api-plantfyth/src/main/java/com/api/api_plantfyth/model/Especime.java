package com.api.api_plantfyth.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private int periodoIrrigacao;
    private int periodoAdubacao;
    private String exposicao_A_Luz;

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

    @OneToMany(mappedBy = "especime")
    @JsonManagedReference
    private List<Plantio> plantios1;
}
