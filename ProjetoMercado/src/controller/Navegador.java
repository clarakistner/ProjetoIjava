package controller;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Navegador {
    private final JFrame frame;
    private final JPanel root;

    public Navegador(JFrame frame) {
        this.frame = frame;
        this.root  = new JPanel(new CardLayout());
        this.frame.setContentPane(root);
    }

    public void adicionarPainel(String nome, JPanel painel) {
        root.add(painel, nome);
    }

    public void navegarPara(String nome) {
        ((CardLayout) root.getLayout()).show(root, nome);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }
}
