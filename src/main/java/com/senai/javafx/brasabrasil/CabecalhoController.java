
package com.senai.javafx.brasabrasil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.net.URL;

public class CabecalhoController {

    @FXML
    private Button botaoHistorico;

    @FXML
    private Button botaoVerPedido;

    @FXML
    private Button botaoAdicionarItem;

    @FXML
    private Button botaoAtualizarItens;

    @FXML
    private void initialize() {
        boolean administrador = Sessao.isAdministrador();
        if (botaoVerPedido != null) {
            botaoVerPedido.setVisible(Sessao.estaLogado());
            botaoVerPedido.setManaged(Sessao.estaLogado());
        }
        for (Button botao : new Button[]{botaoHistorico, botaoAdicionarItem, botaoAtualizarItens}) {
            if (botao != null) {
                botao.setVisible(administrador);
                botao.setManaged(administrador);
            }
        }
    }

    @FXML
    private void abrirVerPedido(ActionEvent event) {
        if (!Sessao.estaLogado()) {
            new Alert(Alert.AlertType.WARNING, "Entre na sua conta para ver seus pedidos.").showAndWait();
            return;
        }
        trocarTela(event, "ver-pedido.fxml");
    }

    @FXML
    private void abrirAdicionarItem(ActionEvent event) {
        if (!Sessao.isAdministrador()) {
            new Alert(Alert.AlertType.WARNING, "Acesso restrito ao usuário principal.").showAndWait();
            return;
        }
        trocarTela(event, "adicionar-item.fxml");
    }

    @FXML
    private void abrirAtualizarItens(ActionEvent event) {
        if (!Sessao.isAdministrador()) {
            new Alert(Alert.AlertType.WARNING, "Acesso restrito ao usuário principal.").showAndWait();
            return;
        }
        trocarTela(event, "atualizar-itens.fxml");
    }

    @FXML
    private void abrirMenu(ActionEvent event) {
        trocarTela(event, "hello-view.fxml");
    }

    @FXML
    private void abrirCadastro(ActionEvent event) {
        trocarTela(event, "cadastro-usuario.fxml");
    }



    @FXML
    private void abrirCardapio(ActionEvent event) {
        System.out.println("1 - BOTÃO CARDÁPIO CLICADO!");

        URL url = getClass().getResource(
                "/com/senai/javafx/brasabrasil/Cardapio.fxml"
        );

        System.out.println("2 - Caminho do FXML: " + url);

        trocarTela(event, "Cardapio.fxml");
    }



    @FXML
    private void abrirHistorico(ActionEvent event) {
        if (!Sessao.isAdministrador()) {
            new Alert(Alert.AlertType.WARNING, "Acesso restrito ao usuário principal.").showAndWait();
            return;
        }
        trocarTela(event, "historico.fxml");
    }


    private void trocarTela(ActionEvent event, String arquivo) {
        try {
            URL url = getClass().getResource(
                    "/com/senai/javafx/brasabrasil/" + arquivo
            );

            if (url == null) {
                System.err.println("FXML não encontrado: " + arquivo);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Node origem = (Node) event.getSource();
            Scene cena = origem.getScene();

            cena.setRoot(root);

            System.out.println("Tela aberta: " + arquivo);

        } catch (Exception e) {
            System.err.println("Erro ao abrir: " + arquivo);
            e.printStackTrace();


        }


    }

}

