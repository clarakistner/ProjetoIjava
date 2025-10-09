package model;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private static final String URL =
		    "jdbc:mysql://localhost:3306/loja?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		private static final String USER = "root";      
		private static final String PASS = "NovaSenha!2025";  


    private static final String SQL_INSERT =
        "INSERT INTO produtos (nome, descricao, preco, quantidade) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_ID =
        "SELECT id, nome, descricao, preco, quantidade FROM produtos WHERE id = ?";

    private static final String SQL_SELECT_ALL =
        "SELECT id, nome, descricao, preco, quantidade FROM produtos ORDER BY nome";

    private static final String SQL_UPDATE =
        "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade = ? WHERE id = ?";

    private static final String SQL_DELETE =
        "DELETE FROM produtos WHERE id = ?";

    public int adicionarProduto(Produto p) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setBigDecimal(3, BigDecimal.valueOf(p.getPreco()));
            ps.setInt(4, p.getQtd());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    public Produto exibirProduto(int id) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Produto> exibirProduto() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    public boolean editarProduto(Produto p) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescricao());
            ps.setBigDecimal(3, BigDecimal.valueOf(p.getPreco()));
            ps.setInt(4, p.getQtd());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluirProduto(int id) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Produto map(ResultSet rs) throws SQLException {
        int id       = rs.getInt("id");
        String nome  = rs.getString("nome");
        String desc  = rs.getString("descricao");
        float preco  = rs.getBigDecimal("preco").floatValue();
        int qtd      = rs.getInt("quantidade");
        return new Produto(nome, desc, id, qtd, preco);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static String formatarListaParaTextArea(List<Produto> produtos) {
        if (produtos == null || produtos.isEmpty()) return "Sem produtos cadastrados.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s | %-20s | %-6s | %-8s\n", "ID", "Nome", "Qtd", "Preço"));
        sb.append("------------------------------------------------------\n");
        for (Produto p : produtos) {
            sb.append(String.format("%-4d | %-20s | %-6d | R$ %-8.2f\n",
                    p.getId(), p.getNome(), p.getQtd(), p.getPreco()));
        }
        return sb.toString();
    }
}