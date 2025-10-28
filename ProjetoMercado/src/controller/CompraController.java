package controller;

import model.Compras;
import model.ComprasDAO;
import model.ItemCompra;
import model.CarrinhoDAO;
import view.TelaCompras;

import javax.swing.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;

public class CompraController {

    private final TelaCompras view;
    private final ComprasDAO compraDAO;
    private final CarrinhoDAO carrinhoDAO;
    private final Navegador navegador;
    private final CadastroProdutosController cadastroProdutosController; 
    private String usuarioLogado;

    public CompraController(
            TelaCompras view,
            String usuarioLogado,
            Navegador navegador,
            CadastroProdutosController cadastroProdutosController 
    ) {
        this.view = view;
        this.usuarioLogado = (usuarioLogado == null ? null : usuarioLogado.trim());
        this.compraDAO = new ComprasDAO();
        this.carrinhoDAO = new CarrinhoDAO();
        this.navegador = navegador;
        this.cadastroProdutosController = cadastroProdutosController;
        wire();
    }

    private void wire() {
        view.finalizar(e -> finalizarCompra());
        view.sair(e -> navegador.navegarPara("LOGIN"));
    }

    private void finalizarCompra() {
        try {
            Compras compra = compraDAO.finalizarCompra(usuarioLogado);
            if (compra == null) {
                JOptionPane.showMessageDialog(view, "Carrinho vazio.", "Atenção",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            StringBuilder sb = new StringBuilder();
            sb.append("Compra realizada com sucesso!\n\n");
            sb.append("Data:  ").append(sdf.format(compra.getDataCompra())).append('\n');
            sb.append("Total: ").append(moeda.format(compra.getTotalPago())).append("\n\n");
            sb.append("Itens:\n");

            if (compra.getItens() == null || compra.getItens().isEmpty()) {
                sb.append("  (sem itens)\n");
            } else {
                for (ItemCompra it : compra.getItens()) {
                    String nome = (it.getNomeProduto() != null ? it.getNomeProduto()
                            : ("Produto #" + it.getIdProduto()));
                    sb.append(String.format("  - %s x%d  = %s%n",
                            nome,
                            it.getQuantidade(),
                            moeda.format(it.getSubtotal())));
                }
            }

            JOptionPane.showMessageDialog(view, sb.toString(),
                    "Compra Finalizada", JOptionPane.INFORMATION_MESSAGE);
         
            try { carrinhoDAO.removerTodosProdutos(usuarioLogado); } catch (Exception ignore) {}
            view.setCarrinho(Collections.emptyList());
            view.setTotalTexto(moeda.format(0));

            if (cadastroProdutosController != null) {
                cadastroProdutosController.atualizarListaExterna(); 
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Erro ao finalizar compra: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setUsuario(String usu) {
        this.usuarioLogado = (usu == null ? null : usu.trim());
    }
}
