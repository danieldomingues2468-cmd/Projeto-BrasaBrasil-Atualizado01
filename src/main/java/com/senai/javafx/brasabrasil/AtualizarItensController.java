package com.senai.javafx.brasabrasil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.NumberFormat;
import java.util.Locale;

public class AtualizarItensController {

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colunaNome;
    @FXML private TableColumn<Produto, String> colunaCategoria;
    @FXML private TableColumn<Produto, String> colunaPreco;
    @FXML private TableColumn<Produto, String> colunaDescricao;
    @FXML private TextField campoPesquisa;

    @FXML private TextField campoNome;
    @FXML private TextField campoPreco;
    @FXML private ComboBox<String> campoCategoria;
    @FXML private TextArea campoDescricao;
    @FXML private Button botaoSalvar;
    @FXML private Label mensagem;

    private ObservableList<Produto> lista;
    private FilteredList<Produto> filtrados;
    private Produto selecionado;

    private final NumberFormat moeda =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

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
                    tabelaProdutos.getScene().setRoot(root);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        campoCategoria.getItems().setAll(AdicionarItemController.CATEGORIAS);

        colunaNome.setCellValueFactory(d ->
                new javafx.beans.property.ReadOnlyStringWrapper(d.getValue().getNome()));
        colunaCategoria.setCellValueFactory(d ->
                new javafx.beans.property.ReadOnlyStringWrapper(d.getValue().getCategoria()));
        colunaPreco.setCellValueFactory(d ->
                new javafx.beans.property.ReadOnlyStringWrapper(moeda.format(d.getValue().getPreco())));
        colunaDescricao.setCellValueFactory(d ->
                new javafx.beans.property.ReadOnlyStringWrapper(d.getValue().getDescricao()));

        lista = FXCollections.observableArrayList(BancoDeDados.getInstance().getProdutos());
        filtrados = new FilteredList<>(lista, p -> true);
        tabelaProdutos.setItems(filtrados);

        campoPesquisa.textProperty().addListener((obs, antigo, novo) -> {
            String busca = novo == null ? "" : novo.trim().toLowerCase();
            filtrados.setPredicate(p -> busca.isEmpty()
                    || p.getNome().toLowerCase().contains(busca)
                    || p.getCategoria().toLowerCase().contains(busca));
        });

        tabelaProdutos.getSelectionModel().selectedItemProperty().addListener(
                (obs, antigo, novo) -> carregarNoFormulario(novo));

        carregarNoFormulario(null);
    }

    private void carregarNoFormulario(Produto produto) {
        selecionado = produto;
        boolean ativo = produto != null;

        campoNome.setDisable(!ativo);
        campoPreco.setDisable(!ativo);
        campoCategoria.setDisable(!ativo);
        campoDescricao.setDisable(!ativo);
        botaoSalvar.setDisable(!ativo);

        if (!ativo) {
            campoNome.clear();
            campoPreco.clear();
            campoDescricao.clear();
            campoCategoria.getSelectionModel().clearSelection();
            mensagem.setText("Selecione um item na tabela para editar.");
            return;
        }

        campoNome.setText(produto.getNome());
        campoPreco.setText(String.format(Locale.forLanguageTag("pt-BR"), "%.2f", produto.getPreco()));
        campoCategoria.setValue(produto.getCategoria());
        campoDescricao.setText(produto.getDescricao());
        mensagem.setText("");
    }

    @FXML
    private void salvar() {
        if (!Sessao.isAdministrador() || selecionado == null) {
            return;
        }

        String nome = campoNome.getText() == null ? "" : campoNome.getText().trim();
        String descricao = campoDescricao.getText() == null ? "" : campoDescricao.getText().trim();
        String categoria = campoCategoria.getValue();

        if (nome.isEmpty()) {
            mensagem.setText("O nome não pode ficar vazio.");
            return;
        }

        Double preco = AdicionarItemController.lerPreco(campoPreco.getText());
        if (preco == null) {
            mensagem.setText("Digite um valor válido maior que zero. Ex.: 12,50");
            return;
        }

        if (categoria == null) {
            mensagem.setText("Escolha a categoria.");
            return;
        }

        Produto atualizado = new Produto(selecionado.getId(), nome, descricao, preco, categoria);

        try {
            BancoDeDados.getInstance().atualizarProduto(atualizado);
        } catch (RuntimeException e) {
            e.printStackTrace();
            mensagem.setText("Não foi possível salvar as alterações.");
            return;
        }

        int indice = lista.indexOf(selecionado);
        if (indice >= 0) {
            lista.set(indice, atualizado);
            tabelaProdutos.getSelectionModel().select(atualizado);
        }

        mensagem.setText("✔ Item atualizado!");
    }
}
