package model;

import java.sql.*;

public class UsuarioDAO {

	private static final String URL =
		  "jdbc:mysql://localhost:3306/loja?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
	private static final String USER = "root";       
	private static final String PASS = "NovaSenha!2025";  


    private static final String SQL_INSERT =
        "INSERT INTO usuarios (usuario, cpf, is_admin) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
        "SELECT usuario, cpf, is_admin FROM usuarios WHERE usuario = ?";

    public boolean cadastrarUsuario(String usuario, String cpf, boolean isAdmin) throws SQLException {
        if (usuario == null || usuario.isBlank()) throw new IllegalArgumentException("Usuário vazio.");
        cpf = util.CpfUtils.unmask(cpf);
        if (!util.CpfUtils.has11(cpf)) throw new IllegalArgumentException("CPF deve ter 11 dígitos.");

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setString(1, usuario.trim());
            ps.setString(2, cpf);
            ps.setBoolean(3, isAdmin);
            return ps.executeUpdate() > 0;
        }
    }

    public Usuario logar(String usuario, String cpf) throws SQLException {
        if (usuario == null || usuario.isBlank()) return null;
        cpf = util.CpfUtils.unmask(cpf);

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setString(1, usuario.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String cpfDb  = rs.getString("cpf");
                boolean adminDb = rs.getBoolean("is_admin");

                return cpf.equals(cpfDb)
                        ? new Usuario(usuario.trim(), cpfDb, adminDb)
                        : null;
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
