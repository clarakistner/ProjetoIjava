package view;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroUsuarios extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTextField textFieldUsuario;
    private JTextField textFieldSenha;   
    private JCheckBox chckbxAdm;
    private JButton btnCadastrar;

    public TelaCadastroUsuarios() {
        setBackground(new Color(255, 182, 193));
        setLayout(null);
        setPreferredSize(new Dimension(700, 500));


        JLabel lblCadastro = new JLabel("Cadastro");
        lblCadastro.setFont(new Font("Baskerville Old Face", Font.BOLD | Font.ITALIC, 30));
        lblCadastro.setBounds(302, 43, 122, 49);
        add(lblCadastro);

        textFieldUsuario = new JTextField();
        textFieldUsuario.setBounds(175, 152, 358, 25);
        add(textFieldUsuario);

        textFieldSenha = new JTextField();                 
        textFieldSenha.setBounds(175, 200, 358, 25);
        add(textFieldSenha);

        chckbxAdm = new JCheckBox("Administrador?");       
        chckbxAdm.setBounds(76, 253, 174, 29);
        add(chckbxAdm);

        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setFont(new Font("Baskerville Old Face", Font.BOLD, 20));
        lblUsuario.setBounds(76, 152, 104, 29);
        add(lblUsuario);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Baskerville Old Face", Font.BOLD, 20));
        lblSenha.setBounds(75, 206, 70, 17);
        add(lblSenha);

        btnCadastrar = new JButton("Cadastrar");           
        btnCadastrar.setBounds(314, 335, 110, 23);
        add(btnCadastrar);
    }

     public String getUsuario() {
        return textFieldUsuario.getText().trim();
    }

    public String getSenha() {
        return textFieldSenha.getText().trim();            
    }

    public boolean isAdminSelecionado() {
        return chckbxAdm.isSelected();
    }

    public void cadastrar(java.awt.event.ActionListener al) {
        btnCadastrar.addActionListener(al);
    }

    public void limparCampos() {
        textFieldUsuario.setText("");
        textFieldSenha.setText("");
        chckbxAdm.setSelected(false);
    }
}
