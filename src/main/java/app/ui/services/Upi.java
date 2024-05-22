package app.ui.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.formdev.flatlaf.FlatClientProperties;

import app.ui.Login;
import app.ui.manager.panelHandler;
import app.utils.DB;
import net.miginfocom.swing.MigLayout;

public class Upi extends JPanel {
    private JTextField vpa;
    private JPasswordField pin; 
    private JButton next;
    private JButton skip;
    private String accNo;

    public Upi(String accNo) {
        this.accNo = accNo;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        vpa = new JTextField();

        pin = new JPasswordField();
        pin.setHorizontalAlignment(JPasswordField.CENTER);
        pin.putClientProperty(
            FlatClientProperties.STYLE, 
            "showRevealButton: true;"
        );

        // document filter to allow only digits and limit to 4 characters
        ((AbstractDocument) pin.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null)
                    return;

                if (isNumeric(string) && (fb.getDocument().getLength() + string.length() <= 4)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null)
                    return;

                if (isNumeric(text) && (fb.getDocument().getLength() + text.length() - length <= 4)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean isNumeric(String text) {
                return text.matches("\\d+");
            }
        });

        next = new JButton("Next");
        next.putClientProperty(
            FlatClientProperties.STYLE, 
            "[light]background: darken(@background, 10%);" +
            "focusWidth: 0;" +
            "borderWidth: 0"
        );

        next.addActionListener(event -> {
            if (validateFields()) {
                if (insertData()) {
                    JOptionPane.showMessageDialog(this, "Account Created", "Response", JOptionPane.OK_OPTION);
                    panelHandler.getInstance().show(new Login()); 
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to insert record into database.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        skip = new JButton("Skip");
        skip.putClientProperty(
            FlatClientProperties.STYLE, 
            "[light]background: darken(@background, 10%);" +
            "focusWidth: 0;" +
            "borderWidth: 0"
        );

        skip.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Account Created", "Response", JOptionPane.OK_OPTION);
            panelHandler.getInstance().show(new Login()); 
            System.out.println("Skipping UPI setup");
        });

        JPanel pane = new JPanel(new MigLayout("fillx, wrap, insets 35 40 35 40", "fill, 300:400"));
        pane.putClientProperty(
            FlatClientProperties.STYLE,
            "arc: 20;" +
            "[light]background: darken(@background, 4%)"
        );

        JLabel header = new JLabel("UPI"); 
        header.putClientProperty(
            FlatClientProperties.STYLE, 
            "font:bold +10"
        );
        pane.add(header, "gapy 8");

        pane.add(new JLabel("VPA"), "gapy 8");
        pane.add(vpa);
        pane.add(new JLabel("PIN"), "gapy 8");
        pane.add(pin);
        pane.add(skip, "gapy 12, split 2");
        pane.add(next);

        add(pane);
    }

    private boolean insertData() {
        String nbSql = "INSERT INTO upi_service(acc_no, upi_vpa, upi_limit, upi_transaction_limit, upi_pin)" +
                    "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DB.connect();
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(nbSql);
            pstmt.setString(1, accNo);
            pstmt.setString(2, vpa.getText().trim());
            pstmt.setInt(3, 25); // default upi transfer limit 25
            pstmt.setDouble(4, 100000); // default upi amount transfer limit is 1L;
            pstmt.setInt(5, Integer.parseInt(new String(pin.getPassword())));

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

    private boolean validateFields() {
        if (vpa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "VPA cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            vpa.requestFocus();
            return false;
        } else if (!vpa.getText().trim().matches("[a-zA-Z0-9.@]+")) {
            JOptionPane.showMessageDialog(this, "VPA cannot have special characters", "Error", JOptionPane.ERROR_MESSAGE);
            vpa.requestFocus();
            return false;
        } else {
            String query = "SELECT COUNT(*) FROM upi_service WHERE upi_vpa = ?";

            try (
                Connection conn = DB.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)
            ) {
                pstmt.setString(1, vpa.getText().trim());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "VPA already exists. choose another vpa", "Error", JOptionPane.ERROR_MESSAGE);
                    vpa.requestFocus();
                    return false;
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        if (pin.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "PIN is empty", "Error", JOptionPane.ERROR_MESSAGE);
            pin.requestFocus();
            return false;
        }

        if (pin.getPassword().length != 4) {
            JOptionPane.showMessageDialog(this, "Enter 4-digit PIN", "Error", JOptionPane.ERROR_MESSAGE);
            pin.requestFocus();
            return false;
        }

        return true;
    }
}
