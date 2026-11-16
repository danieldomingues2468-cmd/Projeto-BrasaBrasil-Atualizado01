package com.senai.javafx.brasabrasil;

public class Produto {

    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private String categoria;

    public Produto(int id, String nome, String descricao, double preco, String categoria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
    }

    public Produto(String nome, double preco, String categoria) {
        this(0, nome, "", preco, categoria);
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return nome + " - R$ " + String.format("%.2f", preco);
    }
}
