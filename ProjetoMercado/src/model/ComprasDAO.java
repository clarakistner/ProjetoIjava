package model;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComprasDAO {

	private static final String URL =
		    "jdbc:mysql://localhost:3306/loja?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
    private static final String USER = "root";
    private static final String PASS = "NovaSenha!2025";

    private static final String SQL_ITENS_CARRINHO_FOR_UPDATE =
        "SELECT p.id           AS id_produto, " +
        "       c.quantidade   AS qtd_carrinho, " +
        "       p.preco        AS preco, " +
        "       p.nome         AS nome, " +
        "       p.quantidade   AS estoque_atual " +
        "FROM carrinho c " +
        "JOIN produtos p ON p.id = c.id_produto " +
        "WHERE c.id_usuario = ? " +
        "FOR UPDATE";

    private static final String SQL_TOTAL_CARRINHO =
        "SELECT COALESCE(SUM(c.quantidade * p.preco), 0) AS total " +
        "FROM carrinho c JOIN produtos p ON p.id = c.id_produto " +
        "WHERE c.id_usuario = ?";

    private static final String SQL_INSERE_COMPRA =
        "INSERT INTO compras (id_usuario, total_pago) VALUES (?, ?)";

    private static final String SQL_INSERE_ITEM =
        "INSERT INTO itens_compra (id_compra, id_produto, quantidade, preco) VALUES (?, ?, ?, ?)";

    private static final String SQL_LIMPA_CARRINHO =
        "DELETE FROM carrinho WHERE id_usuario = ?";

    private static final String SQL_GET_COMPRA =
        "SELECT id, id_usuario, total_pago, data_compra FROM compras WHERE id = ?";

    private static final String SQL_LISTA_COMPRAS_USUARIO =
        "SELECT id, id_usuario, total_pago, data_compra " +
        "FROM compras WHERE id_usuario = ? ORDER BY data_compra DESC";

    private static final String SQL_ITENS_DA_COMPRA =
        "SELECT ic.id, ic.id_compra, ic.id_produto, ic.quantidade, ic.preco, p.nome " +
        "FROM itens_compra ic JOIN produtos p ON p.id = ic.id_produto " +
        "WHERE ic.id_compra = ? ORDER BY p.nome";

   private static final String SQL_DEBITA_ESTOQUE =
        "UPDATE produtos SET quantidade = quantidade - ? " +
        "WHERE id = ? AND quantidade >= ?";

    public Compras finalizarCompra(String idUsuario) throws SQLException {
        if (idUsuario == null || idUsuario.isBlank())
            throw new IllegalArgumentException("Usuário inválido.");

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal total = BigDecimal.ZERO;
                try (PreparedStatement psTot = con.prepareStatement(SQL_TOTAL_CARRINHO)) {
                    psTot.setString(1, idUsuario.trim());
                    try (ResultSet rs = psTot.executeQuery()) {
                        if (rs.next()) total = rs.getBigDecimal("total");
                    }
                }
                if (total == null || BigDecimal.ZERO.compareTo(total) == 0) {
                    con.rollback();
                    return null;
                }

                List<ItemCompra> itens = new ArrayList<>();
                try (PreparedStatement psItens = con.prepareStatement(SQL_ITENS_CARRINHO_FOR_UPDATE)) {
                    psItens.setString(1, idUsuario.trim());
                    try (ResultSet rs = psItens.executeQuery()) {
                        while (rs.next()) {
                            int idProd = rs.getInt("id_produto");
                            int qtd = rs.getInt("qtd_carrinho");
                            BigDecimal preco = rs.getBigDecimal("preco");
                            String nome = rs.getString("nome");
                            int estoqueAtual = rs.getInt("estoque_atual");

                            if (qtd > estoqueAtual) {
                                con.rollback();
                                throw new SQLException("Estoque insuficiente para o produto '" +
                                    nome + "' (solicitado " + qtd + ", disponível " + estoqueAtual + ")");
                            }

                            itens.add(new ItemCompra(null, null, idProd, nome, qtd, preco));
                        }
                    }
                }

                if (itens.isEmpty()) {
                    con.rollback();
                    return null;
                }

                try (PreparedStatement psDeb = con.prepareStatement(SQL_DEBITA_ESTOQUE)) {
                    for (ItemCompra it : itens) {
                        psDeb.setInt(1, it.getQuantidade());
                        psDeb.setInt(2, it.getIdProduto());
                        psDeb.setInt(3, it.getQuantidade());
                        int upd = psDeb.executeUpdate();
                        if (upd != 1) {
                            con.rollback();
                            throw new SQLException("Falha ao debitar estoque do produto id=" + it.getIdProduto());
                        }
                    }
                }

                Integer idCompraGerado = null;
                try (PreparedStatement psIns = con.prepareStatement(SQL_INSERE_COMPRA, Statement.RETURN_GENERATED_KEYS)) {
                    psIns.setString(1, idUsuario.trim());
                    psIns.setBigDecimal(2, total);
                    psIns.executeUpdate();
                    try (ResultSet keys = psIns.getGeneratedKeys()) {
                        if (keys.next()) idCompraGerado = keys.getInt(1);
                    }
                }

                try (PreparedStatement psInsItem = con.prepareStatement(SQL_INSERE_ITEM)) {
                    for (ItemCompra it : itens) {
                        psInsItem.setInt(1, idCompraGerado);
                        psInsItem.setInt(2, it.getIdProduto());
                        psInsItem.setInt(3, it.getQuantidade());
                        psInsItem.setBigDecimal(4, it.getPreco());
                        psInsItem.addBatch();
                    }
                    psInsItem.executeBatch();
                }

                try (PreparedStatement psDel = con.prepareStatement(SQL_LIMPA_CARRINHO)) {
                    psDel.setString(1, idUsuario.trim());
                    psDel.executeUpdate();
                }

                con.commit();

                Compras compra = obterCompra(idCompraGerado, con);
                compra.setItens(itens);
                return compra;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }


    public Compras obterCompra(int idCompra) throws SQLException {
        try (Connection con = getConnection()) {
            return obterCompra(idCompra, con);
        }
    }

    private Compras obterCompra(int idCompra, Connection con) throws SQLException {
        Compras compra = null;
        try (PreparedStatement ps = con.prepareStatement(SQL_GET_COMPRA)) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    compra = new Compras(
                        rs.getInt("id"),
                        rs.getString("id_usuario"),
                        rs.getBigDecimal("total_pago"),
                        rs.getTimestamp("data_compra")
                    );
                }
            }
        }
        if (compra != null) compra.setItens(listarItensDaCompra(idCompra, con));
        return compra;
    }

    public List<ItemCompra> listarItensDaCompra(int idCompra) throws SQLException {
        try (Connection con = getConnection()) {
            return listarItensDaCompra(idCompra, con);
        }
    }

    private List<ItemCompra> listarItensDaCompra(int idCompra, Connection con) throws SQLException {
        List<ItemCompra> itens = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_ITENS_DA_COMPRA)) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    itens.add(new ItemCompra(
                        rs.getInt("id"),
                        rs.getInt("id_compra"),
                        rs.getInt("id_produto"),
                        rs.getString("nome"),
                        rs.getInt("quantidade"),
                        rs.getBigDecimal("preco")
                    ));
                }
            }
        }
        return itens;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
