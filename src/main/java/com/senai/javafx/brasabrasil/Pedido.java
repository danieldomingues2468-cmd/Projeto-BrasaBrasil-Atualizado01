
package com.senai.javafx.brasabrasil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    public Pedido(
            String gmailUsuario,
            List<String> produtos,
            double total,
            LocalDateTime data) {

        this.gmailUsuario = gmailUsuario;
        this.produtos = new ArrayList<>(produtos);
        this.total = total;
        this.data = data;
    }

    private int id;
    private final String gmailUsuario;
    private final List<String> produtos;
    private final double total;
    private final LocalDateTime data;

    public Pedido(String gmailUsuario, List<String> produtos, double total) {
        this.gmailUsuario = gmailUsuario;
        this.produtos = new ArrayList<>(produtos);
        this.total = total;
        this.data = LocalDateTime.now();
    }

    public Pedido(int id, String gmailUsuario, List<String> produtos, double total, LocalDateTime data) {
        this(gmailUsuario, produtos, total, data);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getGmailUsuario() {
        return gmailUsuario;
    }

    public List<String> getProdutos() {
        return new ArrayList<>(produtos);
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getData() {
        return data;
    }
}
