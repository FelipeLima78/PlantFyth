package com.api.api_plantfyth.model;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
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
    @Column(name = "nome")
    private String nome;

    @JsonFormat(
            pattern = "dd/MM/yyyy"
    )
    @Column(name = "data_que_foi_plantado")
    private LocalDate data_que_foi_Plantado;
   
    @Column(name = "foi_regado_hoje")
    private Boolean foiRegadoHoje;

    @Column(name = "horario_ate_irrigar")
    private Date horarioAteIrrigar;

    @Column(name = "previsao_proxima_irrigacao")
    private Date previsaoProximaIrrigacao;

    @Column(name = "previsao_proxima_poda")
    private Date previsaoProximaPoda;

    @Column(name = "tamanho_atual_cm")
    private Float tamanhoAtualCM;

    @Column(name = "previsao_tamanho_cm")
    private Float previsaoTamanhoCM;

    @Column(name = "plantada_como")
    private String plantadaComo;

    @Column(name = "imagem_personalizada")
    private String imagemPersonalizada;

    @Column(name = "fk_usuario_id")
    private Integer usuarioId;
    
    @ManyToOne
    @JoinColumn(name = "fk_usuario_id",insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;

    @Column(name = "fk_especime_id")
    private Integer especimeId;

    @ManyToOne
    @JoinColumn(name = "fk_especime_id", insertable = false, updatable = false)
    @JsonIgnore
    private Especime especime;

}
