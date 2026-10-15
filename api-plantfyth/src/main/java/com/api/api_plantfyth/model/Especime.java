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
@AllArgsConstructor
@NoArgsConstructor
public class Especime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("periodoIrrigacao")
    @Column(name = "periodo_irrigacao")
    private Integer periodoIrrigacao;

    @Column(name = "exposicao_a_luz")
    private String exposicaoALuz;
    
    @Column(name = "descricao", columnDefinition = "TEXT")
    @JsonProperty("descricao")
    private String descricao;

    @Column(name = "tamanho_adulto_cm")
    private Float tamanho_adulto_cm;

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

    @Column(name = "periodo_poda")
    private String periodoPoda;

    @JsonProperty("adubacao")
    @Column(name = "adubacao")
    private String adubacao;

    @JsonProperty("troca_de_vaso")
    @Column(name = "troca_de_vaso")
    private String troca_de_vaso;

    @JsonProperty("volume_irrigacao")
    @Column(name = "volume_irrigacao")
    private String volume_irrigacao;

    @JsonProperty("temperatura")
    @Column(name = "temperatura")
    private String temperatura;

    @JsonProperty("unidade_irrigacao")
    @Column(name = "unidade_irrigacao")
    private String unidade_irrigacao;
       

    

    @OneToMany(mappedBy = "especime", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Plantio> plantios;
}