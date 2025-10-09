package model;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrinhoDAO {

	private static final String URL =
		    "jdbc:mysql://localhost:3306/loja?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		private static final String USER = "root";       
		private static final String PASS = "NovaSenha!2025";  

		private static final String SQL_ADD_UPSERT =
        "INSERT INTO carrinho (id_usuario, id_produto, quantidade) VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE quantidade = quantidade + VALUES(quantidade)";

    private static final String SQL_GET_QTD =
        "SELECT quantidade FROM carrinho WHERE id_usuario = ? AND id_produto = ?";

    private static final String SQL_UPDATE_QTD =
        "UPDATE carrinho SET quantidade = ? WHERE id_usuario = ? AND id_produto = ?";

    private static final String SQL_DELETE_ITEM =
        "DELETE FROM carrinho WHERE id_usuario = ? AND id_produto = ?";

    private static final String SQL_DELETE_ALL =
        "DELETE FROM carrinho WHERE id_usuario = ?";

    private static final String SQL_LIST_JOIN =
        "SELECT c.id, c.id_usuario, c.id_produto, c.quantidade, " +
        "       p.nome AS produto_nome, p.preco " +
        "  FROM carrinho c " +
        "  JOIN produtos p ON p.id = c.id_produto " +
        " WHERE c.id_usuario = ? " +
        " ORDER BY p.nome";

    private static final String SQL_TOTAL =
        "SELECT COALESCE(SUM(c.quantidade * p.preco), 0) AS total " +
        "  FROM carrinho c " +
        "  JOIN produtos p ON p.id = c.id_produto " +
        " WHERE c.id_usuario = ?";

    public boolean adicionarProduto(String idUsuario, int idProduto, int quantidade) throws SQLException {
        if (idUsuario == null || idUsuario.isBlank()) throw new IllegalArgumentException("Usuário inválido");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser > 0");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_ADD_UPSERT)) {
            ps.setString(1, idUsuario.trim());
            ps.setInt(2, idProduto);
            ps.setInt(3, quantidade);
            return ps.executeUpdate() > 0;
        }
    }

     public boolean removerProduto(String idUsuario, int idProduto, int quantidade) throws SQLException {
        if (idUsuario == null || idUsuario.isBlank()) return false;
        if (quantidade <= 0) return false;

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psSel = con.prepareStatement(SQL_GET_QTD)) {
                psSel.setString(1, idUsuario.trim());
                psSel.setInt(2, idProduto);
                try (ResultSet rs = psSel.executeQuery()) {
                    if (!rs.next()) { con.rollback(); return false; }

                    int atual = rs.getInt("quantidade");
                    int novo = atual - quantidade;

                    int changed;
                    if (novo <= 0) {
                        try (PreparedStatement psDel = con.prepareStatement(SQL_DELETE_ITEM)) {
                            psDel.setString(1, idUsuario.trim());
                            psDel.setInt(2, idProduto);
                            changed = psDel.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement psUpd = con.prepareStatement(SQL_UPDATE_QTD)) {
                            psUpd.setInt(1, novo);
                            psUpd.setString(2, idUsuario.trim());
                            psUpd.setInt(3, idProduto);
                            changed = psUpd.executeUpdate();
                        }
                    }
                    con.commit();
                    return changed > 0;
                }
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Carrinho> exibirProduto(String idUsuario) throws SQLException {
        List<Carrinho> lista = new ArrayList<>();
        if (idUsuario == null || idUsuario.isBlank()) return lista;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LIST_JOIN)) {
            ps.setString(1, idUsuario.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int qtd = rs.getInt("quantidade");
                    BigDecimal preco = rs.getBigDecimal("preco");
                    BigDecimal subtotal = preco.multiply(BigDecimal.valueOf(qtd));

                    Carrinho c = new Carrinho(
                        rs.getInt("id"),
                        rs.getString("id_usuario"),
                        rs.getInt("id_produto"),
                        qtd,
                        rs.getString("produto_nome"),
                        preco,
                        subtotal
                    );
                    lista.add(c);
                }
            }
        }
        return lista;
    }

    public int removerTodosProdutos(String idUsuario) throws SQLException {
        if (idUsuario == null || idUsuario.isBlank()) return 0;
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE_ALL)) {
            ps.setString(1, idUsuario.trim());
            return ps.executeUpdate();
        }
    }

    public BigDecimal mostrarTotal(String idUsuario) throws SQLException {
        if (idUsuario == null || idUsuario.isBlank()) return BigDecimal.ZERO;
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_TOTAL)) {
            ps.setString(1, idUsuario.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
                return BigDecimal.ZERO;
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
