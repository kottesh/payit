package app.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.formdev.flatlaf.FlatClientProperties;

import app.ui.manager.panelHandler;
import app.utils.DB;
import net.miginfocom.swing.MigLayout;

public class ReceiversInfo extends JPanel {
    private JTextField toAccountField;
    private JComboBox<String> toBankComboBox;
    private JTextField amountField;
    private JButton nextBtn, backBtn;

    public ReceiversInfo() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        JPanel pane = new JPanel(new MigLayout("fillx, wrap, insets 35 45 30 45", "fill, 400:500"));
        pane.putClientProperty(
            FlatClientProperties.STYLE, 
            "[light]background: darken(@background, 4%);" + 
            "arc: 20"
        );

        JLabel caption = new JLabel("Receiver Infos");
        caption.putClientProperty(
            FlatClientProperties.STYLE, 
            "font:bold +10;" + 
            "[light]foreground: lighten(@foreground, 20%);"
        );

        pane.add(caption);

        // to account number
        toAccountField = new JTextField(20);
        applyDocumentFilter(toAccountField, new DigitFilter(10));
        pane.add(new JLabel("To Account No"), "split 2, gapy 12");
        pane.add(toAccountField);

        // banks
        toBankComboBox = new JComboBox<>(Pay.getBanks().toArray(new String[0]));
        toBankComboBox.insertItemAt("Select Bank", 0);
        toBankComboBox.setSelectedIndex(0);
        pane.add(new JLabel("Select Bank"), "split 2, gapy 8");
        pane.add(toBankComboBox);

        // amount field
        amountField = new JTextField(20);
        pane.add(new JLabel("Amount"), "split 2, gapy 8");
        pane.add(amountField);

        nextBtn = new JButton("Next");
        nextBtn.addActionListener(e -> {
            if (validateInputs()) {
                panelHandler.getInstance().show(new Pay(new String(toAccountField.getText().trim()), new String(amountField.getText().trim())));
            }
        });

        backBtn = new JButton("Cancel");
        backBtn.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to cancel?", 
                "Confirmation", 
                JOptionPane.YES_NO_OPTION
            );
            if (response == JOptionPane.YES_OPTION) {
                panelHandler.getInstance().show(new Home());
            }
        });

        pane.add(backBtn, "split 2, gapy 12");
        pane.add(nextBtn, "gapy 12");

        add(pane);
    }

    public boolean validateInputs() {
        // validate account number
        if (toAccountField.getText().trim().isEmpty() || toAccountField.getText().trim().length() != 10) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit to account number.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String query = "SELECT COUNT(*) FROM accounts WHERE acc_no = ? AND bank_id = (SELECT bank_id FROM banks WHERE bank_name = ?)";
        try (
            Connection conn = DB.connect();
            PreparedStatement pstmnt = conn.prepareStatement(query)
        ) {
            pstmnt.setString(1, toAccountField.getText().trim());
            pstmnt.setString(2, (String)toBankComboBox.getSelectedItem());
            ResultSet res = pstmnt.executeQuery();
            
            if (res.next()) {
                if (res.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Account NOT FOUND. Please check!", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }

        // validate bank selection
        if (toBankComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a bank.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // validate amount
        if (amountField.getText().trim().isEmpty() || !amountField.getText().trim().matches("\\d+(\\.\\d{1,2})?")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
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
