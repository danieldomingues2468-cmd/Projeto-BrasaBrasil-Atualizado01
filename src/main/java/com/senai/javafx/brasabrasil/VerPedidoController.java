package com.senai.javafx.brasabrasil;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class VerPedidoController {

    // Todos os pedidos aparecem como pendentes (pagamento ilustrativo, sem cobrança real).
    private static final String STATUS = "Pendente";

    @FXML private TableView<Pedido> tabelaPedidos;
    @FXML private TableColumn<Pedido, String> colunaNumero;
    @FXML private TableColumn<Pedido, String> colunaData;
    @FXML private TableColumn<Pedido, String> colunaValor;
    @FXML private TableColumn<Pedido, String> colunaStatus;

    @FXML private Label detalheTitulo;
    @FXML private ListView<String> listaItens;
    @FXML private Label detalheTotal;
    @FXML private Label detalheStatus;

    @FXML private ToggleButton botaoDebito;
    @FXML private ToggleButton botaoCredito;
    @FXML private Label mensagem;

    private final ToggleGroup formaPagamento = new ToggleGroup();
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        botaoDebito.setToggleGroup(formaPagamento);
        botaoCredito.setToggleGroup(formaPagamento);
        botaoDebito.setSelected(true);
        // Impede ficar sem nenhuma forma selecionada ao clicar de novo no mesmo botão.
        formaPagamento.selectedToggleProperty().addListener((obs, antigo, novo) -> {
            if (novo == null && antigo != null) {
                antigo.setSelected(true);
            }
        });

        listaItens.setPlaceholder(new Label("Selecione um pedido"));

        colunaNumero.setCellValueFactory(d -> new javafx.beans.property.ReadOnlyStringWrapper(
                "#" + d.getValue().getId()));
        colunaData.setCellValueFactory(d -> new javafx.beans.property.ReadOnlyStringWrapper(
                d.getValue().getData().format(formatoData)));
        colunaValor.setCellValueFactory(d -> new javafx.beans.property.ReadOnlyStringWrapper(
                moeda.format(d.getValue().getTotal())));
        colunaStatus.setCellValueFactory(d -> new javafx.beans.property.ReadOnlyStringWrapper(STATUS));

        tabelaPedidos.setPlaceholder(new Label("Você ainda não fez nenhum pedido"));

        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario != null) {
            tabelaPedidos.setItems(FXCollections.observableArrayList(
                    BancoDeDados.getInstance().getPedidosDoUsuario(usuario.getGmail())));
        }

        tabelaPedidos.getSelectionModel().selectedItemProperty().addListener(
                (obs, antigo, novo) -> mostrarDetalhe(novo));

        if (!tabelaPedidos.getItems().isEmpty()) {
            tabelaPedidos.getSelectionModel().selectFirst();
        } else {
            mostrarDetalhe(null);
        }
    }

    private void mostrarDetalhe(Pedido pedido) {
        mensagem.setText("");

        if (pedido == null) {
            detalheTitulo.setText("Seu pedido");
            listaItens.getItems().clear();
            detalheTotal.setText(moeda.format(0));
            detalheStatus.setText("—");
            return;
        }

        detalheTitulo.setText("Seu pedido #" + pedido.getId());
        listaItens.getItems().setAll(pedido.getProdutos());
        detalheTotal.setText(moeda.format(pedido.getTotal()));
        detalheStatus.setText(STATUS.toUpperCase());
    }

    @FXML
    private void pagar() {
        Pedido pedido = tabelaPedidos.getSelectionModel().getSelectedItem();

        if (pedido == null) {
            mensagem.setText("Selecione um pedido para pagar.");
            return;
        }

        String forma = botaoCredito.isSelected() ? "Cartão de crédito" : "Cartão de débito";

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Pagamento ilustrativo");
        alerta.setHeaderText("Forma de pagamento: " + forma);
        alerta.setContentText(
                "Pedido #" + pedido.getId() + " — " + moeda.format(pedido.getTotal())
                        + "\n\nEsta é apenas uma demonstração: nenhum valor foi cobrado "
                        + "e o pedido continua como Pendente.");
        alerta.showAndWait();
    }

    @FXML
    private void irParaCardapio() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Cardapio.fxml"));
            tabelaPedidos.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
