package com.api.api_plantfyth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "especime")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Especime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "perenual_id")
    private Integer perenualId;

    @JsonProperty("periodoIrrigacao")
@Column(name = "periodo_irrigacao")
private String periodoIrrigacao;

@JsonProperty("unidadeIrrigacao")
@Column(name = "unidade_irrigacao")
private String unidadeIrrigacao;

    @Column(name = "exposicao_a_luz")
    private String exposicaoALuz;

    @Column(name = "periodo_poda")
    private String periodoPoda;

    @Column(name = "descricao", columnDefinition = "TEXT")
    @JsonProperty("descricao")
    private String descricao;

    @Column(name = "tamanho_adulto_cm")
    private Float tamanhoAdultoCM;

    @Column(name = "familia")
    @JsonProperty("familia")
    private String familia;

    @Column(name = "ciclo")
    @JsonProperty("ciclo")
    private String ciclo;

    @Column(name = "crescimento")
    @JsonProperty("crescimento")
    private String crescimento;

    @Column(name = "nome_cientifico", columnDefinition = "TEXT")
    @JsonProperty("nome_cientifico")
    private String nomeCientifico;

    @Column(name = "nome_popular")
    @JsonProperty("nome_popular")
    private String nomePopular;

    @OneToMany(mappedBy = "especime", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Plantio> plantios;
}