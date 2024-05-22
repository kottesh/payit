package app.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatClientProperties;

import app.ui.manager.panelHandler;
import app.utils.DB;
import net.miginfocom.swing.MigLayout;

public class Login extends JPanel {
    public Login() {
        init();
    }

    private void init() {
        username = new JTextField();
        passwd = new JPasswordField();
        login = new JButton("Login");
        back = new JButton("Back");

        setLayout(new MigLayout("fill,  insets 20", "[center]", "[center]"));
        JPanel pane = new JPanel(new MigLayout("fillx, wrap, insets 35 45 30 45", "fill, 300:400")); 
        pane.putClientProperty(
            FlatClientProperties.STYLE, "arc: 20;" + 
            "[light]background: darken(@background, 3%);"
        );

        username.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter username");
        passwd.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        passwd.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter password");

        login.putClientProperty(
            FlatClientProperties.STYLE,
            "[light]background:darken(@background, 10%);" +
            "borderWidth: 0;" +
            "focusWidth: 0;"
        );

        login.addActionListener(e -> {
            if (validateLogin()) {
                panelHandler.getInstance().show(new BankHome(username.getText().trim()));
                JOptionPane.showMessageDialog(this, "Logged In", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        back.putClientProperty(
            FlatClientProperties.STYLE,
            "[light]background:darken(@background, 10%);" +
            "borderWidth: 0;" +
            "focusWidth: 0;"
        );

        back.addActionListener(e -> {
            panelHandler.getInstance().show(new Home());
            System.out.println("Back to Main page!");
        });

        JLabel greet = new JLabel("Welcome back!"); 
        JLabel description = new JLabel("Please sign in to view your account.");
        greet.putClientProperty(FlatClientProperties.STYLE, "font:bold +10");
        description.putClientProperty(
            FlatClientProperties.STYLE,
            "[light]foreground:lighten(@foreground, 30%)"    
        );

        List<String> banksList = getBanks();
        banksList.add(0, "Select Bank");
        banks = new JComboBox<>(banksList.toArray(new String[0]));


        pane.add(greet, "gapy 12");
        pane.add(description);
        pane.add(new JLabel("Bank"), "gapy 6");
        pane.add(banks);
        pane.add(new JLabel("Username"), "gapy 6");
        pane.add(username);
        pane.add(new JLabel("Password"), "gapy 6");
        pane.add(passwd);
        pane.add(back, "gapy 12, split 2");
        pane.add(login);
        pane.add(createSignUp(), "gapy 8");

        add(pane);
    }

    private boolean validateLogin() {
        if (banks.getSelectedItem() == null || "Select Bank".equals(banks.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(this, "Please select a bank", "Error", JOptionPane.ERROR_MESSAGE);
            banks.requestFocus();
            return false;
        }

        if (username.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Empty username", "Error", JOptionPane.ERROR_MESSAGE);
            username.requestFocus();
            return false;
        } else {
            int selectedBankId = getBankId(banks.getSelectedItem().toString());

            String query = "SELECT COUNT(*) FROM login_credentials lc " +
                           "JOIN accounts a ON lc.acc_no = a.acc_no " +
                           "WHERE lc.username = ? AND a.bank_id = ?";
            try (
                Connection conn = DB.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)
            ) {
                pstmt.setString(1, username.getText().trim());
                pstmt.setInt(2, selectedBankId);

                ResultSet rs = pstmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Username NOT FOUND. Please register or check if the Bank is correct", "Error", JOptionPane.ERROR_MESSAGE);
                    username.requestFocus();
                    return false;
                }
            } catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        if (passwd.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            passwd.requestFocus();
            return false;
        } else {
            String query = "SELECT login_pwd FROM login_credentials WHERE username = ?";
            try (
                Connection conn = DB.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)
            ) {
                pstmt.setString(1, username.getText().trim());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next() && !rs.getString(1).equals(new String(passwd.getPassword()))) {
                    JOptionPane.showMessageDialog(this, "Incorrect Password. Please check!", "Error", JOptionPane.ERROR_MESSAGE);
                    passwd.requestFocus();
                    return false;
                }
            } catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return true;
    }

    public int getBankId(String bankName) {
        int bankId = -1;
        String query = "SELECT bank_id FROM banks WHERE bank_name = ?";
        try (Connection conn = DB.connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bankName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bankId = rs.getInt("bank_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bankId;
    }

    private Component createSignUp() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(
            FlatClientProperties.STYLE,
            "background: null"
        );

        JButton createAccount = new JButton("<html><a href=\"#\">Create Account</a></html>");
        createAccount.putClientProperty(FlatClientProperties.STYLE, "border: 3, 3, 3, 3");
        createAccount.setContentAreaFilled(false);
        createAccount.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createAccount.addActionListener(e -> {
            //System.out.println("Opening Create Account Form");
            panelHandler.getInstance().show(new createAccount());
        });

        JLabel label = new JLabel("Don't have one ?");
        label.putClientProperty(
            FlatClientProperties.STYLE, 
            "[light]foreground:lighten(@foreground, 20%)"
        );
        panel.add(label);
        panel.add(createAccount);

        return panel;
    }

    public static List<String> getBanks() {
        List<String> bankNames = new ArrayList<>();
        String query = "SELECT DISTINCT bank_name FROM banks ORDER BY bank_name";

        try (Connection conn = DB.connect();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                bankNames.add(rs.getString("bank_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bankNames;
    }

    private JTextField username;
    private JPasswordField passwd;
    private JButton login;
    private JButton back;
    private JComboBox<String> banks;
}