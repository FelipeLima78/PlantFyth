package com.network.plantfyth.model;

import java.time.LocalDateTime;

public class Usuario {
    //construtor
    public Usuario(Integer id, String nome, String email, String hashSenha, String fotoPerfil, String dataCriacao, String ultimoLogin) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
        this.fotoPerfil = fotoPerfil;
        this.dataCriacao = dataCriacao;
        this.ultimoLogin = ultimoLogin;
    }
    public Usuario() {
    }
    //atributos
    private Integer id;
    private String nome;
    private String email;
    private String hashSenha;
    private String fotoPerfil;
    private String dataCriacao;
    //Coloquei como LocalDateTime por conta dos conflitos da classe date com o timestamp, mas voce pode mudar pro date e tratar a string pra ela remover o "z"
    //do timestamp.
    private String ultimoLogin;

    //getters e setters

    public Integer getId() {
        return id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashSenha() {
        return hashSenha;
    }

    public void setHashSenha(String hashSenha) {
        this.hashSenha = hashSenha;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(String ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }
}
