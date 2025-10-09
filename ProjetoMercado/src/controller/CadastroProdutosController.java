package controller;

import model.Produto;
import model.ProdutoDAO;
import view.TelaCadastroProdutos;
import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

public class CadastroProdutosController {

    private final TelaCadastroProdutos view;
    private final ProdutoDAO produtoDAO;
    private final Navegador navegador; 

    public CadastroProdutosController(TelaCadastroProdutos view, ProdutoDAO produtoDAO, Navegador navegador) {
        this.view = view;
        this.produtoDAO = produtoDAO;
        this.navegador = navegador;
        wire();
        listar(); 
    }

    private void wire() {
        view.onCadastrar(e -> cadastrar());
        view.onAtualizar(e -> editarOuSalvar());
        view.onExcluir(e -> excluir());
        view.onListar(e -> exibirSelecionado());
    }

    private void cadastrar() {
        try {
            Produto p = lerProdutoDaTela(false); 
            int idGerado = produtoDAO.adicionarProduto(p);
            if (idGerado > 0) {
                limparCampos();
                listar();
                info("Produto cadastrado com sucesso!");
            } else {
                warn("Não foi possível cadastrar o produto.");
            }
        } catch (Exception ex) {
            erro("Erro ao cadastrar: " + ex.getMessage(), ex);
        }
    }

    private void editarOuSalvar() {
        try {
            boolean camposVazios =
                trimOrEmpty(view.getNomeText()).isEmpty() &&
                trimOrEmpty(view.getDescricaoText()).isEmpty() &&
                trimOrEmpty(view.getPrecoText()).isEmpty() &&
                trimOrEmpty(view.getQuantidadeText()).isEmpty();

            if (camposVazios) {
                Produto sel = view.getProdutoSelecionado();
                if (sel == null) {
                    warn("Selecione um produto na tabela para carregar para edição.");
                    return;
                }
                preencherTela(sel);
                info("Produto carregado. Altere os campos e clique em Editar novamente para salvar.");
                return;
            }
           atualizar();

        } catch (Exception ex) {
            erro("Erro ao preparar/atualizar: " + ex.getMessage(), ex);
        }
    }

    private void atualizar() {
        try {
            Produto p = lerProdutoDaTela(true); 
            boolean ok = produtoDAO.editarProduto(p);
            if (ok) {
                listar();
                info("Produto atualizado com sucesso.");
            } else {
                warn("Nenhum registro alterado. Selecione um item válido.");
            }
        } catch (Exception ex) {
            erro("Erro ao atualizar: " + ex.getMessage(), ex);
        }
    }

    private void excluir() {
        try {
            Integer id = resolverIdObrigatorio();
            int confirm = JOptionPane.showConfirmDialog(
                    view.getComponent(),
                    "Confirma excluir o produto ID " + id + "?",
                    "Excluir produto",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = produtoDAO.excluirProduto(id);
            if (ok) {
                limparCampos();
                listar();
                info("Produto excluído com sucesso (ID " + id + ").");
            } else {
                warn("Produto não encontrado para exclusão.");
            }
        } catch (Exception ex) {
            erro("Erro ao excluir: " + ex.getMessage(), ex);
        }
    }

    private void listar() {
        try {
            List<Produto> lista = produtoDAO.exibirProduto();
            view.setProdutos(lista);
        } catch (Exception ex) {
            erro("Erro ao listar produtos: " + ex.getMessage(), ex);
        }
    }
    
    private void exibirSelecionado() {
        try {
            Produto p = view.getProdutoSelecionado();
            if (p == null) {
                warn("Selecione um produto na tabela para exibir.");
                return;
            }
            JOptionPane.showMessageDialog(
                    view.getComponent(),
                    formatProdutoDetalhe(p),
                    "Produto selecionado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            erro("Erro ao exibir: " + ex.getMessage(), ex);
        }
    }

    private void limparCampos() {
       try { view.setIdText(""); } catch (Throwable ignore) {}
        view.setNomeText("");
        view.setDescricaoText("");
        view.setPrecoText("");
        view.setQuantidadeText("");
        view.requestFocusNome();
    }

    private Produto lerProdutoDaTela(boolean paraAtualizar) {
        Integer id = null;
        if (paraAtualizar) {
            Produto sel = view.getProdutoSelecionado();
            if (sel == null || sel.getId() <= 0)
                throw new IllegalArgumentException("Selecione um produto na tabela para editar.");
            id = sel.getId();
        }

        String nome = trimOrEmpty(view.getNomeText());
        if (nome.isEmpty()) throw new IllegalArgumentException("Informe o nome.");

        String desc = trimOrEmpty(view.getDescricaoText());

        float preco = parsePreco(view.getPrecoText());
        if (preco < 0) throw new IllegalArgumentException("Preço não pode ser negativo.");

        int qtd = parseQuantidade(view.getQuantidadeText());
        if (qtd < 0) throw new IllegalArgumentException("Quantidade não pode ser negativa.");

        return new Produto(nome, desc, (id == null ? 0 : id), qtd, preco);
    }

    private void preencherTela(Produto p) {
        try { view.setIdText(String.valueOf(p.getId())); } catch (Throwable ignore) {}
        view.setNomeText(p.getNome());
        view.setDescricaoText(p.getDescricao() == null ? "" : p.getDescricao());
        view.setPrecoText(String.format(java.util.Locale.US, "%.2f", p.getPreco()));
        view.setQuantidadeText(String.valueOf(p.getQtd()));
    }

    private Integer resolverIdObrigatorio() {
        Produto sel = view.getProdutoSelecionado();
        if (sel != null && sel.getId() > 0) return sel.getId();
        throw new IllegalArgumentException("Selecione um produto na tabela.");
    }

    private static String trimOrEmpty(String s) { return s == null ? "" : s.trim(); }

    private static float parsePreco(String s) {
        String v = trimOrEmpty(s).replace(',', '.');
        if (v.isEmpty()) throw new IllegalArgumentException("Informe o preço.");
        try {
            return new BigDecimal(v).floatValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("Preço inválido.");
        }
    }

    private static int parseQuantidade(String s) {
        String v = trimOrEmpty(s);
        if (v.isEmpty()) throw new IllegalArgumentException("Informe a quantidade.");
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }
    }

    
    private String formatProdutoDetalhe(Produto p) {
        java.text.NumberFormat moeda =
                java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt","BR"));
        String precoFmt = moeda.format(p.getPreco());
        String desc = (p.getDescricao() == null || p.getDescricao().isBlank()) ? "-" : p.getDescricao().trim();
        return "ID: " + p.getId() + "\n"
             + "Nome: " + p.getNome() + "\n"
             + "Descrição: " + desc + "\n"
             + "Quantidade: " + p.getQtd() + "\n"
             + "Preço: " + precoFmt;
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(view.getComponent(), msg, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }
    private void warn(String msg) {
        JOptionPane.showMessageDialog(view.getComponent(), msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
    private void erro(String msg, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(view.getComponent(), msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
