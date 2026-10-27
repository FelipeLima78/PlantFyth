package com.api.api_plantfyth.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
   
    @Column(name = "foi_regado_hoje")
    private Boolean foiRegadoHoje;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Sao_Paulo")
    @Column(name = "previsao_proxima_irrigacao")
    private Date previsaoProximaIrrigacao;

    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "America/Sao_Paulo")
    @Column(name = "previsao_proxima_poda")
    private String previsaoProximaPoda;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "data_que_foi_plantado")
    private String data_que_foi_Plantado;

    @Column(name = "fk_usuario_id")
    private Integer usuarioId;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "data_ultima_vez_pegou_sol")
    private LocalDate dataUltimaVezPegouSol;

    @Column(name = "previsao_proxima_adubacao")
    private String previsaoProximaAdubacao;

    @ManyToOne
    @JoinColumn(name = "fk_usuario_id",insertable = false, updatable = false)
    @JsonIgnore
    private Usuario usuario;

   
    @JsonProperty("especimeId") 
    @Column(name = "fk_especime_id")
    private Integer especimeId;

    @ManyToOne
    @JoinColumn(name = "fk_especime_id", insertable = false, updatable = false)
    private Especime especime;

    @JsonProperty("pegouSolHoje")
    public boolean isPegouSolHoje() {
        return dataUltimaVezPegouSol != null
                && dataUltimaVezPegouSol.equals(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
    }

    @JsonProperty("pegouSolHoje")
    public void setPegouSolHoje(Boolean pegouSolHoje) {
        if (Boolean.TRUE.equals(pegouSolHoje)) {
            dataUltimaVezPegouSol = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        }
    }

}
