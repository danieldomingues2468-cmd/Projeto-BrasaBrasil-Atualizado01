package com.senai.javafx.brasabrasil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CadastroController {
    @FXML private TextField campoNome;
    @FXML private TextField campoGmail;
    @FXML private PasswordField campoSenha;
    @FXML private PasswordField campoConfirmarSenha;
    @FXML private Label mensagem;

    @FXML
    public void cadastrar() {
        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String gmail = campoGmail.getText() == null ? "" : campoGmail.getText().trim();
        String senha = campoSenha.getText() == null ? "" : campoSenha.getText();
        String confirmar = campoConfirmarSenha.getText() == null ? "" : campoConfirmarSenha.getText();

        if (nome.isEmpty() || gmail.isEmpty() || senha.isEmpty() || confirmar.isEmpty()) {
            mensagem.setText("Preencha todos os campos.");
            return;
        }
        if (!senha.equals(confirmar)) {
            mensagem.setText("As senhas não são iguais.");
            return;
        }
        if (senha.length() < 4) {
            mensagem.setText("A senha deve ter pelo menos 4 caracteres.");
            return;
        }

        try {
            BancoDeDados.getInstance().adicionarUsuario(new Usuario(nome, gmail, senha));
            mensagem.setText("Cadastro realizado! Agora entre com seu nome e senha.");
            campoNome.clear(); campoGmail.clear(); campoSenha.clear(); campoConfirmarSenha.clear();
        } catch (IllegalArgumentException e) {
            mensagem.setText(e.getMessage());
        } catch (RuntimeException e) {
            mensagem.setText("Não foi possível salvar o cadastro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirEntrar() {
        trocarTela("Entrar.fxml");
    }

    private void trocarTela(String arquivo) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(arquivo));
            campoNome.getScene().setRoot(root);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            mensagem.setText("Não foi possível abrir a tela: " + arquivo);
        }
    }
}
