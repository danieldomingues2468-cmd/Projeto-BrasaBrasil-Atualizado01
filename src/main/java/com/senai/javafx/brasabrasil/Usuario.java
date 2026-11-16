package com.senai.javafx.brasabrasil;

public class Usuario {

    private String nome;
    private String gmail;
    private String senha;

    // Variável estática para guardar quem acedeu à conta
    private static Usuario usuarioLogado;

    public Usuario(String nome, String gmail, String senha) {
        this.nome = nome;
        this.gmail = gmail;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getGmail() {
        return gmail;
    }

    public String getSenha() {
        return senha;
    }

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}