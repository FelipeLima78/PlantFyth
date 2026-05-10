package com.api.api_plantfyth.model;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plantio")
public class Plantio {
 
    // atributos
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

     @ManyToOne
    @JoinColumn(name = "especime_id")
    @JsonBackReference
    private Especime especime;
}
