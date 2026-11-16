package com.senai.javafx.brasabrasil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class HistoricoController {

    @FXML
    private TableView<Usuario> tabelaUsuarios;

    @FXML
    private TableColumn<Usuario, String> colunaNome;

    @FXML
    private TableColumn<Usuario, String> colunaGmail;

    @FXML
    private Label totalClientes;

    @FXML
    private TextField campoPesquisa;

    @FXML
    private TableColumn<Usuario, Number> colunaCompras;

    @FXML
    private TableColumn<Usuario, String> colunaTotalGasto;

    private ObservableList<Usuario> listaUsuarios;
    private FilteredList<Usuario> usuariosFiltrados;

    @FXML
    public void initialize() {
        if (!Sessao.isAdministrador()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acesso restrito");
            alert.setHeaderText(null);
            alert.setContentText("Somente o usuário principal pode visualizar o histórico geral.");
            alert.show();

            Platform.runLater(() -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("Cardapio.fxml"));
                    tabelaUsuarios.getScene().setRoot(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        colunaNome.setCellValueFactory(
                new PropertyValueFactory<>("nome")
        );

        colunaGmail.setCellValueFactory(
                new PropertyValueFactory<>("gmail")
        );

        colunaCompras.setCellValueFactory(dados ->
                new javafx.beans.property.ReadOnlyIntegerWrapper(
                        BancoDeDados.getInstance().contarCompras(
                                dados.getValue().getGmail()
                        )
                )
        );

        java.text.NumberFormat moeda =
                java.text.NumberFormat.getCurrencyInstance(
                        new java.util.Locale("pt", "BR")
                );

        colunaTotalGasto.setCellValueFactory(dados ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        moeda.format(
                                BancoDeDados.getInstance().calcularTotalGasto(
                                        dados.getValue().getGmail()
                                )
                        )
                )
        );

        BancoDeDados banco = BancoDeDados.getInstance();

        listaUsuarios = FXCollections.observableArrayList(
                banco.getUsuarios()
        );

        usuariosFiltrados = new FilteredList<>(
                listaUsuarios,
                usuario -> true
        );

        tabelaUsuarios.setItems(usuariosFiltrados);

        // Lógica de pesquisa por Nome ou E-mail
        campoPesquisa.textProperty().addListener(
                (observable, antigo, novo) -> {
                    String pesquisa = novo == null
                            ? ""
                            : novo.trim().toLowerCase();

                    usuariosFiltrados.setPredicate(usuario -> {
                        if (pesquisa.isEmpty()) {
                            return true;
                        }

                        String nome = usuario.getNome() == null
                                ? ""
                                : usuario.getNome().toLowerCase();

                        String gmail = usuario.getGmail() == null
                                ? ""
                                : usuario.getGmail().toLowerCase();

                        return nome.contains(pesquisa)
                                || gmail.contains(pesquisa);
                    });

                    totalClientes.setText(String.valueOf(usuariosFiltrados.size()));
                }
        );

        // Duplo clique na linha para acessar a conta
        tabelaUsuarios.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tabelaUsuarios.getSelectionModel().getSelectedItem() != null) {
                acessarContaSelecionada();
            }
        });

        atualizarTabela();
    }

    @FXML
    private void atualizarTabela() {
        if (!Sessao.isAdministrador()) {
            return;
        }
        BancoDeDados banco = BancoDeDados.getInstance();
        listaUsuarios.setAll(banco.getUsuarios());
        tabelaUsuarios.refresh();
        totalClientes.setText(String.valueOf(listaUsuarios.size()));
    }

    @FXML
    private void acessarContaSelecionada() {
        if (!Sessao.isAdministrador()) {
            new Alert(Alert.AlertType.WARNING, "Acesso restrito ao usuário principal.").showAndWait();
            return;
        }
        Usuario usuarioSelecionado = tabelaUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSelecionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nenhum Cliente Selecionado");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecione um cliente na tabela para acessar a conta.");
            alert.showAndWait();
            return;
        }

        // 1. Inicia a sessão com o cliente selecionado do histórico
        Sessao.iniciar(usuarioSelecionado);
        Usuario.setUsuarioLogado(usuarioSelecionado);

        // 2. Redireciona direto para a tela de Cardápio/Pedidos
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Cardapio.fxml"));
            Parent root = loader.load();

            tabelaUsuarios.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText("Não foi possível carregar a tela de Cardápio.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }}