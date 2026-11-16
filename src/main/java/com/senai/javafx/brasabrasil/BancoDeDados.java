
package com.senai.javafx.brasabrasil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class BancoDeDados {

    private static BancoDeDados singleton;

    private static final String URL = "jdbc:sqlite:brasabrasil.db";

    private BancoDeDados() {
        criarTabelas();
    }

    public static synchronized BancoDeDados getInstance() {
        if (singleton == null) {
            singleton = new BancoDeDados();
        }

        return singleton;
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelas() {

        String usuarios =
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "gmail TEXT PRIMARY KEY COLLATE NOCASE, " +
                "nome TEXT NOT NULL, " +
                "senha TEXT NOT NULL" +
                ")";


        String pedidos =
                "CREATE TABLE IF NOT EXISTS pedidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "gmail_usuario TEXT NOT NULL, " +
                "produtos TEXT NOT NULL, " +
                "total REAL NOT NULL, " +
                "data TEXT NOT NULL, " +
                "FOREIGN KEY (gmail_usuario) " +
                "REFERENCES usuarios(gmail)" +
                ")";

        String produtos =
                "CREATE TABLE IF NOT EXISTS produtos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "descricao TEXT NOT NULL DEFAULT '', " +
                "preco REAL NOT NULL, " +
                "categoria TEXT NOT NULL" +
                ")";

        try (Connection conexao = conectar();
             Statement stmt = conexao.createStatement()) {

            stmt.execute(usuarios);
            stmt.execute(pedidos);
            stmt.execute(produtos);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao criar as tabelas do banco de dados.", e);
        }

        popularCardapioInicial();
    }

    // Na primeira execução (tabela vazia) grava o cardápio original, para
    // que o administrador possa editá-lo depois. Não roda de novo se já houver itens.
    private void popularCardapioInicial() {
        try (Connection conexao = conectar();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM produtos")) {

            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar o cardápio.", e);
        }

        Object[][] iniciais = {
                {"X-Burger", 18.00, "Hambúrgueres", "Um delicioso hambúrguer para seu pedido."},
                {"X-Salada", 22.00, "Hambúrgueres", "Um delicioso hambúrguer para seu pedido."},
                {"Brasa Burger", 28.00, "Hambúrgueres", "Um delicioso hambúrguer para seu pedido."},
                {"Coca-Cola", 6.00, "Bebidas", "Uma bebida para acompanhar seu lanche."},
                {"Suco de Laranja", 8.00, "Bebidas", "Uma bebida para acompanhar seu lanche."},
                {"Milkshake", 12.00, "Bebidas", "Uma bebida para acompanhar seu lanche."},
                {"Brownie", 10.00, "Sobremesas", "Uma sobremesa deliciosa para finalizar."},
                {"Sorvete", 8.00, "Sobremesas", "Uma sobremesa deliciosa para finalizar."}
        };

        for (Object[] item : iniciais) {
            adicionarProduto(new Produto(0, (String) item[0], (String) item[3],
                    (Double) item[1], (String) item[2]));
        }
    }

    // ===================== CARDÁPIO (produtos) =====================

    public ArrayList<Produto> getProdutos() {
        ArrayList<Produto> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao, preco, categoria FROM produtos ORDER BY id";

        try (Connection conexao = conectar();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco"),
                        rs.getString("categoria")));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar o cardápio.", e);
        }

        return lista;
    }

    public void adicionarProduto(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, preco, categoria) VALUES (?, ?, ?, ?)";

        try (Connection conexao = conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setString(4, produto.getCategoria());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar o item no cardápio.", e);
        }
    }

    public void atualizarProduto(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, categoria = ? WHERE id = ?";

        try (Connection conexao = conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setString(4, produto.getCategoria());
            stmt.setInt(5, produto.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o item do cardápio.", e);
        }
    }

    public ArrayList<Usuario> getUsuarios() {
        ArrayList<Usuario> lista = new ArrayList<>();

        String sql = "SELECT nome, gmail, senha FROM usuarios";

        try (Connection conexao = conectar();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getString("nome"),
                        rs.getString("gmail"),
                        rs.getString("senha")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar usuários.", e);
        }

        return lista;
    }

    public Usuario autenticarPorNome(String nome, String senha) {
        String sql = "SELECT nome, gmail, senha FROM usuarios WHERE nome = ? COLLATE NOCASE AND senha = ? LIMIT 1";
        try (Connection conexao = conectar(); PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getString("nome"), rs.getString("gmail"), rs.getString("senha"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar os dados de acesso.", e);
        }
    }

    public void adicionarUsuario(Usuario usuario) {
        String sql =
                "INSERT INTO usuarios (nome, gmail, senha) " +
                "VALUES (?, ?, ?)";

        try (Connection conexao = conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getGmail().trim());
            stmt.setString(3, usuario.getSenha());

            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage() != null
                    && e.getMessage().toLowerCase().contains("unique")) {
                throw new IllegalArgumentException(
                        "Já existe uma conta cadastrada com esse Gmail.", e);
            }

            throw new RuntimeException("Erro ao cadastrar usuário.", e);
        }
    }

    public ArrayList<Pedido> getPedidos() {
        ArrayList<Pedido> lista = new ArrayList<>();

        String sql =
                "SELECT gmail_usuario, produtos, total, data " +
                "FROM pedidos " +
                "ORDER BY id DESC";

        try (Connection conexao = conectar();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String produtosSalvos = rs.getString("produtos");

                ArrayList<String> produtos = new ArrayList<>(
                        Arrays.asList(produtosSalvos.split("\n", -1))
                );

                Pedido pedido = new Pedido(
                        rs.getString("gmail_usuario"),
                        produtos,
                        rs.getDouble("total"),
                        LocalDateTime.parse(rs.getString("data"))
                );

                lista.add(pedido);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar pedidos.", e);
        }

        return lista;
    }

    // Retorna somente as compras da conta informada, em ordem da mais recente.
    public ArrayList<Pedido> getPedidosDoUsuario(String gmailUsuario) {
        ArrayList<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id, gmail_usuario, produtos, total, data FROM pedidos " +
                     "WHERE gmail_usuario = ? COLLATE NOCASE ORDER BY id DESC";
        try (Connection conexao = conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, gmailUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArrayList<String> produtos = new ArrayList<>(
                            Arrays.asList(rs.getString("produtos").split("\n", -1)));
                    lista.add(new Pedido(rs.getInt("id"), rs.getString("gmail_usuario"), produtos,
                            rs.getDouble("total"), LocalDateTime.parse(rs.getString("data"))));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar o histórico deste cliente.", e);
        }
        return lista;
    }

    public void adicionarPedido(Pedido pedido) {
        String sql =
                "INSERT INTO pedidos " +
                "(gmail_usuario, produtos, total, data) " +
                "VALUES (?, ?, ?, ?)";

        String produtos = String.join("\n", pedido.getProdutos());

        try (Connection conexao = conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, pedido.getGmailUsuario());
            stmt.setString(2, produtos);
            stmt.setDouble(3, pedido.getTotal());
            stmt.setString(4, pedido.getData().toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pedido.", e);
        }
    }

    public int contarCompras(String gmailUsuario) {
        String sql =
                "SELECT COUNT(*) " +
                "FROM pedidos " +
                "WHERE gmail_usuario = ? COLLATE NOCASE";

        try (Connection conexao = conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, gmailUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar compras.", e);
        }
    }

    public double calcularTotalGasto(String gmailUsuario) {
        String sql =
                "SELECT COALESCE(SUM(total), 0) " +
                "FROM pedidos " +
                "WHERE gmail_usuario = ? COLLATE NOCASE";

        try (Connection conexao = conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, gmailUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular gastos.", e);
        }
    }
}
