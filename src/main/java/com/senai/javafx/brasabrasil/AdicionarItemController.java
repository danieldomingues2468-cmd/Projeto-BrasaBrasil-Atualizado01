package com.senai.javafx.brasabrasil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AdicionarItemController {

    static final String[] CATEGORIAS = {"Hambúrgueres", "Bebidas", "Sobremesas", "Promoções"};

    @FXML private ComboBox<String> campoCategoria;
    @FXML private TextField campoNome;
    @FXML private TextField campoPreco;
    @FXML private TextArea campoDescricao;
    @FXML private Label mensagem;

    @FXML
    public void initialize() {
        if (!Sessao.isAdministrador()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acesso restrito");
            alert.setHeaderText(null);
            alert.setContentText("Somente o usuário principal pode gerenciar o cardápio.");
            alert.show();

            Platform.runLater(() -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("Cardapio.fxml"));
                    campoNome.getScene().setRoot(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        campoCategoria.getItems().setAll(CATEGORIAS);
        campoCategoria.getSelectionModel().selectFirst();
    }

    @FXML
    private void salvar() {
        if (!Sessao.isAdministrador()) {
            return;
        }

        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String descricao = campoDescricao.getText() == null ? "" : campoDescricao.getText().trim();
        String categoria = campoCategoria.getValue();

        if (nome.isEmpty()) {
            mensagem.setText("Digite o nome do item.");
            return;
        }

        Double preco = lerPreco(campoPreco.getText());
        if (preco == null) {
            mensagem.setText("Digite um valor válido maior que zero. Ex.: 12,50");
            return;
        }

        if (categoria == null) {
            mensagem.setText("Escolha a categoria.");
            return;
        }

        try {
            BancoDeDados.getInstance().adicionarProduto(
                    new Produto(0, nome, descricao, preco, categoria));
        } catch (RuntimeException e) {
            e.printStackTrace();
            mensagem.setText("Não foi possível salvar o item.");
            return;
        }

        mensagem.setText("✔ \"" + nome + "\" adicionado ao cardápio!");
        campoNome.clear();
        campoPreco.clear();
        campoDescricao.clear();
        campoNome.requestFocus();
    }

    // Aceita "12,50" ou "12.50" (e "R$ 12,50"). Retorna null se inválido.
    static Double lerPreco(String texto) {
        if (texto == null) {
            return null;
        }
        String limpo = texto.replace("R$", "").replace(" ", "").replace(",", ".");
        try {
            double valor = Double.parseDouble(limpo);
            if (Double.isNaN(valor) || Double.isInfinite(valor) || valor <= 0) {
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
