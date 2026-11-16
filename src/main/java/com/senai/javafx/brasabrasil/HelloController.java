package com.senai.javafx.brasabrasil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.net.URL;


import java.io.IOException;


public class HelloController {



    @FXML
    public void abrirCadastro(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("cadastro-usuario.fxml")
        );

        Parent root = loader.load();

        ((Node) event.getSource()).getScene().setRoot(root);
    }

    @FXML
    public void abrirHistorico(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("historico.fxml")
        );

        Parent root = loader.load();

        ((Node) event.getSource()).getScene().setRoot(root);
    }



    @FXML
    private void abrirCardapio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("Cardapio.fxml")
            );

            Parent root = loader.load();

            ((Node) event.getSource()).getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void abrirMenu(ActionEvent actionEvent) {
    }
}

