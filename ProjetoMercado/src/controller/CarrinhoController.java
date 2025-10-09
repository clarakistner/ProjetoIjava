package controller;

import model.Carrinho;
import model.CarrinhoDAO;
import model.Produto;
import model.ProdutoDAO;
import view.TelaCompras;

import javax.swing.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CarrinhoController {
    private final TelaCompras view;
    private String usuarioLogado;
    private final ProdutoDAO  produtoDAO  = new ProdutoDAO();
    private final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();

    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

    public CarrinhoController(TelaCompras view, String usuarioInicial) {
        this.view = view;
        this.usuarioLogado = (usuarioInicial == null ? null : usuarioInicial.trim());
        wire();
        recarregar();
    }

    public void setUsuario(String usuario) {
        this.usuarioLogado = (usuario == null ? null : usuario.trim());
        recarregar();
    }

    public void recarregar() {
        carregarProdutos();
        if (isUsuarioValido()) {
            atualizarCarrinhoETotal();
        } else {
            view.setCarrinho(Collections.emptyList());
            view.setTotalTexto(moeda.format(BigDecimal.ZERO));
        }
    }

    private void wire() {
        view.adicionar(e   -> adicionar1());
        view.remover(e     -> remover1());
        view.removerTudo(e -> removerTudo());
        view.exibir(e      -> exibirSelecionado());
        
    }

    private void carregarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.exibirProduto();
            view.setProdutos(produtos);
        } catch (Exception ex) {
            erro("Erro ao carregar produtos: " + ex.getMessage());
        }
    }

    private void atualizarCarrinhoETotal() {
        try {
            if (!isUsuarioValido()) return;
            List<Carrinho> itens = carrinhoDAO.exibirProduto(usuarioLogado);
            view.setCarrinho(itens);
            BigDecimal total = carrinhoDAO.mostrarTotal(usuarioLogado);
            view.setTotalTexto(moeda.format(total));
        } catch (Exception ex) {
            erro("Erro ao atualizar carrinho: " + ex.getMessage());
        }
    }

    private void adicionar1() {
        if (!garanteUsuario()) return;
        try {
            Integer id = view.getProdutoSelecionadoId();
            if (id == null) { warn("Selecione um produto na lista da esquerda."); return; }
            boolean ok = carrinhoDAO.adicionarProduto(usuarioLogado, id, 1);
            if (!ok) warn("Estoque insuficiente ou produto inexistente.");
            recarregar(); 
        } catch (Exception ex) {
            erro("Erro ao adicionar: " + ex.getMessage());
        }
    }

    private void remover1() {
        if (!garanteUsuario()) return;
        try {
            Integer id = view.getProdutoCarrinhoSelecionadoId();
            if (id == null) { warn("Selecione um item no carrinho."); return; }
            boolean ok = carrinhoDAO.removerProduto(usuarioLogado, id, 1);
            if (!ok) warn("Produto não estava no carrinho.");
            recarregar(); // ✅
        } catch (Exception ex) {
            erro("Erro ao remover: " + ex.getMessage());
        }
    }

    private void removerTudo() {
        if (!garanteUsuario()) return;
        try {
            int r = JOptionPane.showConfirmDialog(view, "Remover todos os itens do carrinho?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return;
            carrinhoDAO.removerTodosProdutos(usuarioLogado); 
            recarregar(); 
        } catch (Exception ex) {
            erro("Erro ao limpar: " + ex.getMessage());
        }
    }

    private void exibirSelecionado() {
        try {
            Integer id = view.getProdutoSelecionadoId();
            if (id == null) id = view.getProdutoCarrinhoSelecionadoId();
            if (id == null) { warn("Selecione um produto em alguma das listas."); return; }

            Produto p = produtoDAO.exibirProduto(id);
            if (p == null) { warn("Produto não encontrado."); return; }

            JOptionPane.showMessageDialog(view, formatProduto(p), "Produto", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            erro("Erro ao exibir: " + ex.getMessage());
        }
    }

    private boolean isUsuarioValido() {
        return usuarioLogado != null && !usuarioLogado.isBlank();
    }

    private boolean garanteUsuario() {
        if (isUsuarioValido()) return true;
        warn("Nenhum usuário logado. Faça login para usar o carrinho.");
        return false;
    }

    private String formatProduto(Produto p) {
        return "ID: " + p.getId() + "\n" +
               "Nome: " + p.getNome() + "\n" +
               "Descrição: " + (p.getDescricao()==null? "-" : p.getDescricao()) + "\n" +
               "Estoque: " + p.getQtd() + "\n" +
               "Preço: " + moeda.format(p.getPreco());
    }

    private void warn(String s){ JOptionPane.showMessageDialog(view, s, "Atenção", JOptionPane.WARNING_MESSAGE); }
    private void erro(String s){ JOptionPane.showMessageDialog(view, s, "Erro", JOptionPane.ERROR_MESSAGE); }
}
