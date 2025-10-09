package controller;

import model.UsuarioDAO;
import model.Usuario;
import view.TelaCadastroUsuarios;
import view.TelaLogin;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CadastroController {

    private final TelaLogin telaLogin;
    private final TelaCadastroUsuarios telaCadastro;
    private final UsuarioDAO usuarioDAO;
    private final Navegador navegador;
    private final CarrinhoController carrinhoController;
    private final CompraController compraController; 
    
    public CadastroController(
            TelaLogin telaLogin,
            TelaCadastroUsuarios telaCadastro,
            UsuarioDAO usuarioDAO,
            Navegador navegador,
            CarrinhoController carrinhoController,
            CompraController compraController
    ) {
        this.telaLogin   = telaLogin;
        this.telaCadastro= telaCadastro;
        this.usuarioDAO  = usuarioDAO;
        this.navegador   = navegador;
        this.carrinhoController = carrinhoController;
        this.compraController   = compraController; 
        wireLogin();
        wireCadastro();
    }

    
    private void wireLogin() {
        telaLogin.entrar((ActionEvent e) -> fazerLogin());
        telaLogin.cadastro((ActionEvent e) -> navegador.navegarPara("CADASTRO_USUARIOS"));
    }

    private void wireCadastro() {
        telaCadastro.cadastrar((ActionEvent e) -> cadastrarUsuario());
    }

    private void fazerLogin() {
        String usuario = telaLogin.getUsuario();
        String senha   = telaLogin.getSenha();

        try {
            Usuario u = usuarioDAO.logar(usuario, senha);
            if (u == null) {
                JOptionPane.showMessageDialog(telaLogin, "Usuário ou senha inválidos.");
                return;
            }

            String usu = u.getUsuario();

            if (carrinhoController != null) carrinhoController.setUsuario(usu);
            if (compraController   != null) compraController.setUsuario(usu);

            if (u.isAdmin()) {
                navegador.navegarPara("CADASTRO_PRODUTOS");
            } else {
                navegador.navegarPara("COMPRAS");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(telaLogin, "Erro ao logar: " + ex.getMessage());
        }
    }

    private void cadastrarUsuario() {
        String usuario = telaCadastro.getUsuario();
        String senha   = telaCadastro.getSenha();
        boolean admin  = telaCadastro.isAdminSelecionado();

        if (usuario == null || usuario.isBlank()) {
            JOptionPane.showMessageDialog(telaCadastro, "Informe o usuário.");
            return;
        }
        if (senha == null) senha = "";

        try {
            boolean ok = usuarioDAO.cadastrarUsuario(usuario, senha, admin);
            if (ok) {
                JOptionPane.showMessageDialog(telaCadastro, "Usuário cadastrado com sucesso!");
                telaCadastro.limparCampos();
                navegador.navegarPara("LOGIN");
            } else {
                JOptionPane.showMessageDialog(telaCadastro, "Não foi possível cadastrar.");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException dup) {
            JOptionPane.showMessageDialog(telaCadastro, "Usuário já existe.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(telaCadastro, "Erro ao cadastrar: " + ex.getMessage());
        }
    }
}
