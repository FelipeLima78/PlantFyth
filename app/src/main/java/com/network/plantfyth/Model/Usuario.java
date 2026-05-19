package com.network.plantfyth.Model;

public class Usuario {

    private String nome;
    private String email;
    private String senha;
    private String confirmeSenha;

    public Usuario(String nome, String email, String senha, String confirmeSenha) {
        this.nome          = nome;
        this.email         = email;
        this.senha         = senha;
        this.confirmeSenha = confirmeSenha;
    }

    // ── Getters ──────────────────────────────────────────

    public String getNome()          { return nome; }
    public String getEmail()         { return email; }
    public String getSenha()         { return senha; }
    public String getConfirmeSenha() { return confirmeSenha; }

    // ── Validações (regras de negócio ficam no Model) ────

    public boolean camposVazios() {
        return nome.isEmpty() || email.isEmpty()
                || senha.isEmpty() || confirmeSenha.isEmpty();
    }

    public boolean senhasConferem() {
        return senha.equals(confirmeSenha);
    }

    public boolean senhaValida() {
        // Mínimo 8 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 especial
        return senha.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,}$");
    }

    public boolean emailValido() {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}