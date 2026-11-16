package com.senai.javafx.brasabrasil;

public class Sessao {

    private static Usuario usuarioLogado;
    private static boolean administrador;

    public static void iniciar(Usuario usuario) {
        usuarioLogado = usuario;
        administrador = false;
    }

    public static void iniciarAdministrador(Usuario usuario) {
        usuarioLogado = usuario;
        administrador = true;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static boolean estaLogado() {
        return usuarioLogado != null;
    }

    public static boolean isAdministrador() {
        return estaLogado() && administrador;
    }

    public static void encerrar() {
        usuarioLogado = null;
        administrador = false;
    }
}
