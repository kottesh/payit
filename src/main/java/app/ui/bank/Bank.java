package app.ui.bank;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

public class Bank extends JPanel {
    private JTextField username;   
    private JPasswordField pwd;

    Bank() {
        username = new JTextField();
        pwd = new JPasswordField();

        JPanel panel = new JPanel(new MigLayout(""));
    }
}
