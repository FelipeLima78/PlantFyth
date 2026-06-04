package com.api.api_plantfyth.model;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

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

    @JsonFormat(
            pattern = "dd/MM/yyyy"
    )
    private LocalDate dataQueFoiPlantado;
    private boolean foiRegado;

    //datas importantes
    private LocalDate dataAdubo;
    private LocalDateTime horarioAreIrrigar;

    //previsoes automaticas
    private LocalDate previsaoProximaIrrigacao;
    private LocalDate previsaoProximaAdubacao;
    private LocalDate previsaoProximaPoda;
    //Informações dinâmicas do app
    private int nivelUmidade;
    private float tamanhoAtualCM;
    private float previsaoTamanho;
    private String plantadaComo;
    private String imagemPersonalizada;
    
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference(value = "usuario-plantio")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "especime_id")
    @JsonBackReference(value = "especime-plantio")
    private Especime especime;
}
