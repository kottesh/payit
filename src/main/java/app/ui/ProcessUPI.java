package app.ui;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.formdev.flatlaf.FlatClientProperties;

import net.miginfocom.swing.MigLayout;
import app.payment.TransactionLog;
import app.payment.Upi;
import app.ui.manager.panelHandler;
import app.utils.DB;

public class ProcessUPI extends JPanel {
    private String UPI; // sender's UPI ID
    private String toAccNo;
    private String amount; 
    JPasswordField pinInputField;

    public ProcessUPI(String UPI, String accNo, String amount) {
        this.UPI = UPI;
        this.toAccNo = accNo;;
        this.amount = amount;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        JPanel pinInputPanel = new JPanel(new MigLayout("insets 35 40 35 45", "[grow, fill, 300::300]", "[]20[]"));
        pinInputPanel.putClientProperty(
            FlatClientProperties.STYLE, "arc: 20;" + 
            "[light]background: darken(@background, 3%)"
        );
        
        pinInputField = new JPasswordField(4);
        pinInputField.setHorizontalAlignment(JPasswordField.CENTER);
        pinInputField.putClientProperty(
            FlatClientProperties.STYLE, 
            "showRevealButton: true;"
        );

        JLabel header = new JLabel("PIN"); 
        header.putClientProperty(
            FlatClientProperties.STYLE, 
            "[light]foreground: lighten(@foreground, 20%);" +
            "font: bold +10;"
        );
        pinInputPanel.add(header, "wrap");
        pinInputPanel.add(new JLabel("UPI PIN"));
        pinInputPanel.add(pinInputField, "wrap");
        applyDocumentFilter(pinInputField, new DigitFilter(4));
        
        // numpad for keys
        JPanel numpadPanel = new JPanel(new GridLayout(4, 3)); // 4 rows, 3 columns for a numpad
        for (int i = 1; i <= 9; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.addActionListener(e -> pinInputField.setText(pinInputField.getText() + e.getActionCommand()));
            numpadPanel.add(button);
        }
        // add a "0" button
        numpadPanel.add(new JLabel(""));
        JButton zeroButton = new JButton("0");
        zeroButton.addActionListener(e -> pinInputField.setText(pinInputField.getText() + e.getActionCommand()));
        numpadPanel.add(zeroButton);
        numpadPanel.add(new JLabel(""));
        
        pinInputPanel.add(numpadPanel, "span, grow, wrap");
        
        JButton makePaymentButton = new JButton("Make Payment");
        pinInputPanel.add(makePaymentButton, "span, grow");

        JButton cancelBtn = new JButton("Cancel");
        pinInputField.add(cancelBtn);

        makePaymentButton.addActionListener(e -> {
            if (Upi.validatePIN(this.UPI, new String(pinInputField.getPassword()))) {
                if (Upi.transferAmount(this.UPI, this.toAccNo, Double.valueOf(this.amount))) {
                    String fromAccNo = "";
                    String query = "SELECT acc_no FROM upi_service WHERE upi_vpa = ?";
                    try (
                        Connection conn = DB.connect();
                        PreparedStatement pstmnt = conn.prepareStatement(query)
                    ) {
                        pstmnt.setString(1, this.UPI);

                        ResultSet res = pstmnt.executeQuery();
                        if (res.next()) {
                            fromAccNo = res.getString("acc_no");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    // update logs in senders account;
                    new TransactionLog().updateTransactionLog("Global Bank", fromAccNo, toAccNo, Double.valueOf(amount), "Debit", "UPI","Success");
                    // update logs in acquirers account;
                    // new TransactionLog().updateTransactionLog("SBI", toAccNo, fromAccNo, +Double.valueOf(amount), "Credit", "UPI","Success");
                    JOptionPane.showMessageDialog(this, "Transaction Successful", "Information", JOptionPane.INFORMATION_MESSAGE);
                    panelHandler.getInstance().show(new Home()); // completing the transaction go to main home
                }
            }
        });

        add(pinInputPanel);
    }

    private void applyDocumentFilter(JTextField textField, DocumentFilter filter) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(filter);
    }
}

class DigitFilter extends DocumentFilter {
    private int maxCharacters;
    
    public DigitFilter(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }
    
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) {
            return;
        }
        if ((fb.getDocument().getLength() + string.length()) <= maxCharacters && string.matches("\\d+")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) {
            return;
        }
        if ((fb.getDocument().getLength() - length + text.length()) <= maxCharacters && text.matches("\\d+")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}

