package model;

import java.sql.*;

public class UsuarioDAO {

	private static final String URL =
		    "jdbc:mysql://localhost:3306/loja?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		private static final String USER = "root";       
		private static final String PASS = "NovaSenha!2025";  


    private static final String SQL_INSERT =
        "INSERT INTO usuarios (usuario, senha, is_admin) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
        "SELECT usuario, senha, is_admin FROM usuarios WHERE usuario = ?";

    public boolean cadastrarUsuario(String usuario, String senha, boolean isAdmin) throws SQLException {
        if (usuario == null || usuario.isBlank()) throw new IllegalArgumentException("Usuário vazio.");
        if (senha == null) senha = "";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            ps.setString(1, usuario.trim());
            ps.setString(2, senha);
            ps.setBoolean(3, isAdmin);
            return ps.executeUpdate() > 0;
        }
    }

    public Usuario logar(String usuario, String senha) throws SQLException {
        if (usuario == null || usuario.isBlank()) return null;
        if (senha == null) senha = "";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setString(1, usuario.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String senhaDb  = rs.getString("senha");
                boolean adminDb = rs.getBoolean("is_admin");

                return senha.equals(senhaDb)
                        ? new Usuario(usuario.trim(), senhaDb, adminDb)
                        : null;
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
