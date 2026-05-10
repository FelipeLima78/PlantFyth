package com.api.api_plantfyth.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="usuario")
public class Usuario {
//Atributos

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String email;
    private String hashSenha;
    private String fotoPerfil;
    private LocalDateTime dataCriacao;
    private LocalDateTime ultimoLogin;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference
    private List<Plantio> plantios;
    
}
