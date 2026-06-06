package com.network.plantfyth.model;

public class ChatMessage {
    private String texto;
    private boolean ehUsuario;
    public ChatMessage(String texto, boolean ehUsuario) {
        this.texto = texto;
        this.ehUsuario = ehUsuario;
    }

    public String getTexto() { return texto; }
    public boolean isUsuario() { return ehUsuario; }

}