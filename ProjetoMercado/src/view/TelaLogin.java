package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TelaLogin extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField textFieldUsuario;
    private JPasswordField textFieldCPF;

    private JButton btnEntrar;
    private JButton btnCadastro;

    public TelaLogin() {
        setBackground(new Color(255, 182, 193));
        setLayout(null);
        setPreferredSize(new Dimension(700, 500));


        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("Baskerville Old Face", Font.BOLD | Font.ITALIC, 30));
        lblLogin.setBounds(304, 66, 100, 31);
        add(lblLogin);

        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setFont(new Font("Baskerville Old Face", Font.BOLD, 20));
        lblUsuario.setBounds(114, 163, 83, 31);
        add(lblUsuario);

        JLabel lblCPF = new JLabel("CPF:");
        lblCPF.setFont(new Font("Baskerville Old Face", Font.BOLD, 20));
        lblCPF.setBounds(117, 223, 66, 20);
        add(lblCPF);

        textFieldUsuario = new JTextField();
        textFieldUsuario.setBounds(220, 169, 249, 20);
        add(textFieldUsuario);

        textFieldCPF = new JPasswordField();
        textFieldCPF.setBounds(220, 224, 249, 20);
        add(textFieldCPF);

        btnCadastro = new JButton("Cadastro");
        btnCadastro.setBounds(549, 427, 100, 23);
        add(btnCadastro);

        btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(304, 331, 89, 23);
        add(btnEntrar);
    }

    public String getUsuario() {
        return textFieldUsuario.getText();
    }

    public String getCPF() {
        return new String(textFieldCPF.getPassword());
    } 

    public void entrar(ActionListener al) {
        btnEntrar.addActionListener(al);
    }

    public void cadastro(ActionListener al) {
        btnCadastro.addActionListener(al);
    }

    public void limparCampos() {
        textFieldCPF.setText("");
        textFieldUsuario.setText("");
    }

}
