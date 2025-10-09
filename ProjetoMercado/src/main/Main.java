package main;

import javax.swing.SwingUtilities;

import controller.Navegador;
import controller.CadastroController;
import controller.CadastroProdutosController;
import controller.CarrinhoController;
import controller.CompraController;

import model.UsuarioDAO;
import model.ProdutoDAO;

import view.Janela;
import view.TelaLogin;
import view.TelaCadastroUsuarios;
import view.TelaCadastroProdutos;
import view.TelaCompras;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Janela janela = new Janela();
            janela.setTitle("Kronos - Loja");
            janela.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

            Navegador navegador = new Navegador(janela);

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            ProdutoDAO produtoDAO = new ProdutoDAO();

            TelaLogin            telaLogin            = new TelaLogin();
            TelaCadastroUsuarios telaCadastroUsuarios = new TelaCadastroUsuarios();
            TelaCadastroProdutos telaCadastroProdutos = new TelaCadastroProdutos();
            TelaCompras          telaCompras          = new TelaCompras();

            CarrinhoController carrinhoController = new CarrinhoController(telaCompras, null);
            CompraController   compraController   = new CompraController(telaCompras,   null);

            new CadastroController(
                telaLogin,
                telaCadastroUsuarios,
                usuarioDAO,
                navegador,
                carrinhoController,
                compraController
            );

            new CadastroProdutosController(
                telaCadastroProdutos,
                produtoDAO,
                navegador
            );

            navegador.adicionarPainel("LOGIN",             telaLogin);
            navegador.adicionarPainel("CADASTRO_USUARIOS", telaCadastroUsuarios);
            navegador.adicionarPainel("CADASTRO_PRODUTOS", telaCadastroProdutos);
            navegador.adicionarPainel("COMPRAS",           telaCompras);

            janela.pack();
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);

            navegador.navegarPara("LOGIN");
        });
    }
}
