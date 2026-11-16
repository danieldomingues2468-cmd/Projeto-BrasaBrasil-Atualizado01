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

public class EntrarController {
    @FXML private TextField campoNome;
    @FXML private PasswordField campoSenha;
    @FXML private Label mensagem;

    @FXML
    private void entrar() {
        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String senha = campoSenha.getText() == null ? "" : campoSenha.getText();
        if (nome.isEmpty() || senha.isEmpty()) {
            mensagem.setText("Digite seu nome e sua senha.");
            return;
        }

        // Conta administrativa reservada: não depende de cadastro na tabela de clientes.
        // O privilégio é concedido na sessão somente após validar os dois dados.
        boolean loginAdministrador =
                "Kauan".equalsIgnoreCase(nome) && "civic".equals(senha);

        Usuario usuario;
        if (loginAdministrador) {
            usuario = new Usuario("Kauan", "admin@brasabrasil.local", senha);
            Sessao.iniciarAdministrador(usuario);
        } else {
            usuario = BancoDeDados.getInstance().autenticarPorNome(nome, senha);
            if (usuario == null) {
                mensagem.setText("Nome ou senha incorretos. Confira seus dados.");
                return;
            }
            Sessao.iniciar(usuario);
        }

        Usuario.setUsuarioLogado(usuario);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Cardapio.fxml"));
            campoNome.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            mensagem.setText("Login realizado, mas não foi possível abrir o cardápio.");
        }
    }

    @FXML
    private void abrirCadastro() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("cadastro-usuario.fxml"));
            campoNome.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            mensagem.setText("Não foi possível abrir o cadastro.");
        }
    }
}
