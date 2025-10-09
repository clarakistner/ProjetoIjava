package view;

import model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TelaCadastroProdutos extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTextField txtNome = new JTextField();
    private final JTextField txtDescricao = new JTextField();
    private final JTextField txtPreco = new JTextField();
    private final JTextField txtQuantidade = new JTextField();

    private final JButton btnCadastrar = new JButton("Cadastrar");
    private final JButton btnEditar    = new JButton("Editar");
    private final JButton btnExcluir   = new JButton("Excluir");
    private final JButton btnExibir    = new JButton("Exibir");

    private final JTable tabela;
    private final DefaultTableModel modelo;
    private List<Produto> data = new ArrayList<>();

    public TelaCadastroProdutos() {
        setBackground(new Color(255, 182, 193));
        setLayout(null);
        setPreferredSize(new Dimension(700, 500));


        JLabel lblTitulo = new JLabel("Cadastro de Produtos");
        lblTitulo.setFont(new Font("Baskerville Old Face", Font.BOLD | Font.ITALIC, 24));
        lblTitulo.setBounds(220, 29, 230, 30);
        add(lblTitulo);

        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setBounds(100, 72, 70, 20);
        add(lblDescricao);

        txtDescricao.setBounds(180, 70, 420, 24);
        add(txtDescricao);

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(100, 107, 37, 20);
        add(lblNome);

        txtNome.setBounds(180, 105, 200, 24);
        add(txtNome);

        JLabel lblQtd = new JLabel("Quantidade:");
        lblQtd.setBounds(390, 107, 60, 20);
        add(lblQtd);

        txtQuantidade.setBounds(475, 105, 125, 24);
        add(txtQuantidade);

        JLabel lblPreco = new JLabel("Preço:");
        lblPreco.setBounds(100, 140, 70, 20);
        add(lblPreco);

        txtPreco.setBounds(180, 138, 295, 24);
        add(txtPreco);

        btnCadastrar.setBounds(485, 138, 115, 24);
        add(btnCadastrar);

        modelo = new DefaultTableModel(new Object[]{"ID", "Nome", "Quantidade", "Preço"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(tabela);
        sp.setBounds(100, 187, 500, 260);
        add(sp);

        btnExibir.setBounds(183, 458, 100, 26);
        btnExcluir.setBounds(310, 458, 100, 26);
        btnEditar.setBounds(435, 458, 100, 26);
        add(btnExibir);
        add(btnExcluir);
        add(btnEditar);
    }

    public void onCadastrar(ActionListener l) { btnCadastrar.addActionListener(l); }
    public void onAtualizar(ActionListener l) { btnEditar.addActionListener(l); }
    public void onExcluir(ActionListener l)   { btnExcluir.addActionListener(l); }
    public void onListar(ActionListener l)    { btnExibir.addActionListener(l); }

    public String getIdText() { return ""; }
    public void setIdText(String s) {  }

    public String getNomeText()       { return txtNome.getText().trim(); }
    public String getDescricaoText()  { return txtDescricao.getText().trim(); }
    public String getPrecoText()      { return txtPreco.getText().trim(); }
    public String getQuantidadeText() { return txtQuantidade.getText().trim(); }

    public void setNomeText(String s)       { txtNome.setText(s == null ? "" : s); }
    public void setDescricaoText(String s)  { txtDescricao.setText(s == null ? "" : s); }
    public void setPrecoText(String s)      { txtPreco.setText(s == null ? "" : s); }
    public void setQuantidadeText(String s) { txtQuantidade.setText(s == null ? "" : s); }

    public void requestFocusNome() { txtNome.requestFocusInWindow(); }

    public void setProdutos(List<Produto> produtos) {
        data = (produtos == null) ? new ArrayList<>() : new ArrayList<>(produtos);
        modelo.setRowCount(0);
        for (Produto p : data) {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getNome(),
                p.getQtd(),
                String.format(java.util.Locale.US, "%.2f", p.getPreco())
            });
        }
        if (modelo.getRowCount() > 0) tabela.setRowSelectionInterval(0, 0);
    }

    public Produto getProdutoSelecionado() {
        int idx = tabela.getSelectedRow();
        if (idx < 0 || idx >= data.size()) return null;
        return data.get(idx);
    }

    public Integer getIdSelecionado() {
        Produto p = getProdutoSelecionado();
        return (p == null ? null : p.getId());
    }

    public Component getComponent() { return this; }
}
