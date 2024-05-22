package app.ui.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatClientProperties;

import app.ui.manager.panelHandler;
import app.utils.DB;
import net.miginfocom.swing.MigLayout;

public class netBanking extends JPanel{
    private JTextField username;    
    private JPasswordField loginPwd;
    private JPasswordField transactionPwd;
    private JButton next;

    private String accNo;

    public netBanking(String accNo) {
        this.accNo = accNo;
        init();
    }

    private boolean validateFields() {
        // check if the username is not filled.
        if (username.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            username.requestFocus();
            return false;
        } else {
            // check if the username is already taken
            String query = "SELECT COUNT(*) FROM login_credentials WHERE username LIKE ?";

            try (
                Connection conn = DB.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)
            ) {
                pstmt.setString(1, username.getText().trim());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Username not available", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        // check if the login passsword is empty
        if (loginPwd.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Login Password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            loginPwd.requestFocus();
            return false;
        }

        // check if the transaction password is empty 
        if (transactionPwd.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Transaction Password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            loginPwd.requestFocus();
            return false;
        }

        String loginPwdStrength = checkPasswordStrength(new String(loginPwd.getPassword()));
        if (!loginPwdStrength.equals("strong")) {
            JOptionPane.showMessageDialog(this, "Login " + loginPwdStrength, "Error", JOptionPane.ERROR_MESSAGE);
            transactionPwd.requestFocus();
            return false;
        }

        String transactionPwdStrength = checkPasswordStrength(new String(transactionPwd.getPassword()));
        if (!transactionPwdStrength.equals("strong")) {
            JOptionPane.showMessageDialog(this, "Transaction " + transactionPwdStrength, "Error", JOptionPane.ERROR_MESSAGE);
            transactionPwd.requestFocus();
            return false;
        }

        // check if the login and transaction password are the same.
        if (Arrays.equals(loginPwd.getPassword(), transactionPwd.getPassword())) {
            JOptionPane.showMessageDialog(this, "Login and Transaction Password can't be same", "Error", JOptionPane.ERROR_MESSAGE);
            loginPwd.requestFocus();
            return false;
        }

        return true;
    }

    // constraints to check password strength
    private String checkPasswordStrength(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number";
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return "Password must contain at least one special character (!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]";
        }
        return "strong";
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        username = new JTextField();
        loginPwd = new JPasswordField();
        loginPwd.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        transactionPwd = new JPasswordField();
        transactionPwd.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        next = new JButton("Next");
        next.putClientProperty(
            FlatClientProperties.STYLE, 
            "[light]background: darken(@background, 10%);" +
            "focusWidth: 0;" +
            "borderWidth: 0"
        );

        next.addActionListener(e -> {
            if (validateFields()) {
                if (insertData()) {
                    panelHandler.getInstance().show(new Upi(accNo));
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert record into database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel pane = new JPanel(new MigLayout("fillx, wrap, insets 35 40 35 40", "fill, 300:400"));
        pane.putClientProperty(
            FlatClientProperties.STYLE,
            "arc: 20;" +
            "[light]background: darken(@background, 4%)"
        );
        JLabel header = new JLabel("Net Banking"); 
        header.putClientProperty(
            FlatClientProperties.STYLE, 
            "font:bold +10"
        );
        pane.add(header, "gapy 8");

        pane.add(new JLabel("Username"), "gapy 8");
        pane.add(username);
        pane.add(new JLabel("Login Password"), "gapy 8");
        pane.add(loginPwd);
        pane.add(new JLabel("Transaction Password"), "gapy 8");
        pane.add(transactionPwd);
        pane.add(next, "gapy 12");

        add(pane);
    }

    private boolean insertData() {
        String nbSql = "INSERT INTO login_credentials(acc_no, username, login_pwd, transaction_pwd)" +
                    "VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DB.connect();
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(nbSql);
            pstmt.setString(1, accNo);
            pstmt.setString(2, username.getText().trim());
            pstmt.setString(3, new String(loginPwd.getPassword()));
            pstmt.setString(4, new String(transactionPwd.getPassword()));

            pstmt.executeUpdate();

            conn.commit(); // Commit transaction
            return true;
        } catch  (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction in case of error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

