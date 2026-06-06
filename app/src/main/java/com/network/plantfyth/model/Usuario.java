package com.network.plantfyth.model;

import java.time.LocalDateTime;

public class Usuario {
    //construtor
    public Usuario(Integer id, String nome, String email, String hash_senha, String foto_perfil, String data_criacao, String ultimo_login) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.hash_senha = hash_senha;
        this.foto_perfil = foto_perfil;
        this.data_criacao = data_criacao;
        this.ultimo_login = ultimo_login;
    }
    public Usuario() {
    }
    //atributos
    private Integer id;
    private String nome;
    private String email;
    private String hash_senha;
    private String foto_perfil;
    private String data_criacao;
    //Coloquei como LocalDateTime por conta dos conflitos da classe date com o timestamp, mas voce pode mudar pro date e tratar a string pra ela remover o "z"
    //do timestamp.
    private String ultimo_login;

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
        return hash_senha;
    }

    public void setHashSenha(String hash_senha) {
        this.hash_senha = hash_senha;
    }

    public String getFotoPerfil() {
        return foto_perfil;
    }

    public void setFotoPerfil(String foto_perfil) {
        this.foto_perfil = foto_perfil;
    }

    public String getDataCriacao() {
        return data_criacao;
    }

    public void setDataCriacao(String data_criacao) {
        this.data_criacao = data_criacao;
    }

    public String getUltimoLogin() {
        return ultimo_login;
    }

    public void setUltimoLogin(String ultimo_login) {
        this.ultimo_login = ultimo_login;
    }
}
