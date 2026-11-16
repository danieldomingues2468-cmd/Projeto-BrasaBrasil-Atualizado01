package com.senai.javafx.brasabrasil;

import javafx.event.ActionEvent;

import java.util.ArrayList;

public class Cardapio {

    private ArrayList<Produto> produtos = new ArrayList<>();

    public Cardapio() {
        produtos.addAll(BancoDeDados.getInstance().getProdutos());
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }

    public ArrayList<Produto> buscarPorCategoria(String categoria) {

        ArrayList<Produto> resultado = new ArrayList<>();

        for (Produto produto : produtos) {

            if (produto.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(produto);
            }
        }

        return resultado;
    }

    public void mostrarBebidas(ActionEvent actionEvent) {
    }

    public void mostrarHamburgueres(ActionEvent actionEvent) {
    }

    public void mostrarSobremesas(ActionEvent actionEvent) {
    }

    public void mostrarPromocoes(ActionEvent actionEvent) {
    }

    public void limparPedido(ActionEvent actionEvent) {
    }

    public void finalizarPedido(ActionEvent actionEvent) {
    }
}

