package view;

import model.Carrinho;
import model.Produto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TelaCompras extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultListModel<Produto> modeloProdutos  = new DefaultListModel<>();
    private final DefaultListModel<Carrinho> modeloCarrinho = new DefaultListModel<>();
    private final JList<Produto> lstProdutos   = new JList<>(modeloProdutos);
    private final JList<Carrinho> lstCarrinho  = new JList<>(modeloCarrinho);

    private final JButton btnAdd         = new JButton("Adicionar >");
    private final JButton btnRemover     = new JButton("< Remover");
    private final JButton btnRemoverTudo = new JButton("< Tudo");
    private final JButton btnExibir      = new JButton("Exibir");
    private final JButton btnFinalizar   = new JButton("Finalizar Compra");

    private final JLabel lblTotalValor = new JLabel("R$ 0,00");

    public TelaCompras() {
        setBackground(new Color(255, 182, 193));
        setLayout(null);
        setPreferredSize(new Dimension(700, 500));


        JLabel titulo = new JLabel("Compras", SwingConstants.CENTER);
        titulo.setFont(new Font("Baskerville Old Face", Font.BOLD | Font.ITALIC, 30));
        titulo.setBounds(246, 31, 193, 28);
        add(titulo);

        JLabel lblDisp = new JLabel("Produtos disponíveis:");
        lblDisp.setBounds(36, 70, 200, 18);
        add(lblDisp);

        JLabel lblCarr = new JLabel("Carrinho:");
        lblCarr.setBounds(412, 70, 200, 18);
        add(lblCarr);

        lstProdutos.setBounds(22, 99, 260, 260);
        lstCarrinho.setBounds(412, 99, 260, 260);
        lstProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCarrinho.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        lstProdutos.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel r = new JLabel();
            r.setOpaque(true);
            if (value != null) {
                r.setText(value.getNome() + " — " + moeda.format(value.getPreco()) + " (estoque: " + value.getQtd() + ")");
            }
            r.setBackground(isSelected ? new Color(255, 220, 230) : Color.white);
            r.setBorder(new EmptyBorder(3,6,3,6));
            return r;
        });
        lstCarrinho.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel r = new JLabel();
            r.setOpaque(true);
            if (value != null) {
                r.setText(value.getProdutoNome() + "  x" + value.getQuantidade() + "  —  " + moeda.format(value.getSubtotal()));
            }
            r.setBackground(isSelected ? new Color(255, 220, 230) : Color.white);
            r.setBorder(new EmptyBorder(3,6,3,6));
            return r;
        });

        add(lstProdutos);
        add(lstCarrinho);
        btnAdd.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });

        btnAdd.setBounds(292, 105, 110, 24);
        btnRemover.setBounds(292, 140, 110, 24);
        btnRemoverTudo.setBounds(292, 335, 110, 24);
        add(btnAdd);
        add(btnRemover);
        add(btnRemoverTudo);

        btnExibir.setBounds(22, 366, 80, 24);
        add(btnExibir);

        JLabel lblTotal = new JLabel("Total:");
        lblTotal.setBounds(580, 366, 40, 24);
        add(lblTotal);

        lblTotalValor.setBounds(630, 366, 66, 24);
        add(lblTotalValor);
        btnFinalizar.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });

        btnFinalizar.setBounds(548, 401, 130, 26);
        add(btnFinalizar);
    }

    public void setProdutos(List<Produto> produtos) {
        modeloProdutos.clear();
        if (produtos != null) produtos.forEach(modeloProdutos::addElement);
        if (modeloProdutos.size() > 0) lstProdutos.setSelectedIndex(0);
    }
    
    public void setCarrinho(List<Carrinho> itens) {
        modeloCarrinho.clear();
        if (itens != null) itens.forEach(modeloCarrinho::addElement);
    }

    public Integer getProdutoSelecionadoId() {
        Produto p = lstProdutos.getSelectedValue();
        return (p == null) ? null : p.getId();
    }
    
    public Integer getProdutoCarrinhoSelecionadoId() {
        Carrinho c = lstCarrinho.getSelectedValue();
        return (c == null) ? null : c.getIdProduto();
    }

    public void setTotalTexto(String txt) { lblTotalValor.setText(txt == null ? "R$ 0,00" : txt); }

    public void adicionar(java.awt.event.ActionListener l)    { btnAdd.addActionListener(l); }
    public void remover(java.awt.event.ActionListener l)      { btnRemover.addActionListener(l); }
    public void removerTudo(java.awt.event.ActionListener l)  { btnRemoverTudo.addActionListener(l); }
    public void exibir(java.awt.event.ActionListener l)       { btnExibir.addActionListener(l); }
    public void finalizar(java.awt.event.ActionListener l)    { btnFinalizar.addActionListener(l); }

	public void limparCarrinhoUI() {
		// TODO Auto-generated method stub
		
	}

	public void atualizarTotais() {
		// TODO Auto-generated method stub
		
	}
}
