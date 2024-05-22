package app.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import com.formdev.flatlaf.FlatClientProperties;

import app.ui.manager.panelHandler;
import app.utils.DB;
import net.miginfocom.swing.MigLayout;

public class Pay extends JPanel {
    private String accountNo;
    private String amount;

    private JButton cancel;
    private JButton proceed;
    private JComboBox<String> selectPay;
    JComboBox<String> bankComboBox;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private String[] payOptions = {"Select Payment Method", "UPI", "Net Banking", "Credit / Debit"};
    private Map<String, List<JComponent>> inputFieldsStore = new HashMap<>();

    public Pay(String accNo, String amount) {
        this.accountNo = accNo;
        this.amount = amount;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 40", "[center]", "[center]"));
        JLabel caption = new JLabel("Choose a Payment Method");
        caption.putClientProperty(
            FlatClientProperties.STYLE, 
            "font:bold +8;" +
            "[light]foreground: lighten(@foreground, 20%);"
        );

        selectPay = new JComboBox<>(payOptions);
        selectPay.setPreferredSize(new Dimension(200, 30));
        selectPay.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index == -1 && value != null && ((String) value).equals("Select Payment Method")) {
                    setText((String) value);
                    setForeground(UIManager.getColor("TextField.placeholderForeground"));
                } else {
                    setForeground(list.getForeground());
                    setText((value == null) ? "" : value.toString());
                }
                return this;
            }
        });
        selectPay.addActionListener(e -> showPayOptionPage());

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // add panels for each payment option
        for (String option : payOptions) {
            JPanel optionPanel = createOptionPanel(option);
            cardPanel.add(optionPanel, option);
        }

        // Add a default panel for "Select Payment Method"
        JPanel defaultPanel = new JPanel();
        cardPanel.add(defaultPanel, "Select Payment Method");

        cancel = new JButton("CANCEL");
        cancel.addActionListener(e -> {
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

        proceed = new JButton("PROCEED");
        proceed.addActionListener(e -> {
            String selectedOption = (String) selectPay.getSelectedItem();
            if (!selectedOption.equals("Select Payment Method")) {
                if (validateInput(selectedOption)) {
                    if ("UPI".equals(selectedOption)) {
                        List<JComponent> inputFields = inputFieldsStore.get(selectedOption);
                        JTextField upiIdField = (JTextField) inputFields.get(0);
                        String upiId = upiIdField.getText().trim();
                        panelHandler.getInstance().show(new ProcessUPI(upiId, accountNo, amount));
                    } else if ("Net Banking".equals(selectedOption)) {
                        String bankName = (String) bankComboBox.getSelectedItem();
                        EventQueue.invokeLater(() -> new NetBankingUI(bankName, accountNo, Double.parseDouble(amount)));
                    } else if ("Credit / Debit".equals(selectedOption)) {
                        EventQueue.invokeLater(() -> new CreditDebitCardFrame(accountNo, Double.parseDouble(amount)).setVisible(true));
                    }
                } 
            } else {
                JOptionPane.showMessageDialog(this, "Please select a payment method.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        System.out.print("Dei error eh... inga aachu irukkiyaa");

        JPanel pane = new JPanel(new MigLayout("fill, wrap, insets 35 45 35 40", "fill, 400:500"));
        pane.putClientProperty(
            FlatClientProperties.STYLE, "arc: 20;" +
            "[light]background: darken(@background, 3%);"
        );
        pane.add(caption);
        pane.add(selectPay, "wrap");
        pane.add(cardPanel, "span, grow, wrap");
        pane.add(cancel, "gapy 6, split 2");
        pane.add(proceed);

        add(pane, "wrap");

        // Show the default panel
        cardLayout.show(cardPanel, "Select Payment Method");
    }

    private JPanel createOptionPanel(String option) {
        JPanel optionPanel = new JPanel(new MigLayout("fill, insets 20", "[center]", "[center]"));
        List<JComponent> inputFields = new ArrayList<>();

        switch (option) {
            case "UPI":
                JTextField upiIdField = new JTextField(20);
                optionPanel.add(new JLabel("UPI ID:"));
                optionPanel.add(upiIdField, "wrap");
                inputFields.add(upiIdField);
                break;
            case "Net Banking":
                List<String> banks = getBanks();
                banks.add(0, "Select Bank");
                bankComboBox = new JComboBox<>(banks.toArray(new String[0]));
                // JTextField fromAccountField = new JTextField(20);
                // applyDocumentFilter(fromAccountField, new DigitFilter(10));
                optionPanel.add(new JLabel("Select Bank:"));
                optionPanel.add(bankComboBox, "wrap");
                // optionPanel.add(new JLabel("Account No:"));
                // optionPanel.add(fromAccountField, "wrap");
                // inputFields.add(bankComboBox);
                // inputFields.add(fromAccountField);
                break;
            case "Credit / Debit":
                // JTextField cardNumberField = new JTextField(20);
                // JTextField cvvField = new JTextField(3);
                // JTextField expiryField = new JTextField("", 7);
                // expiryField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "MM/YYYY");
                // applyDocumentFilter(cardNumberField, new DigitFilter(16));
                // applyDocumentFilter(cvvField, new DigitFilter(3));
                // applyDocumentFilter(expiryField, new DateFilter());
                // optionPanel.add(new JLabel("Card Number:"));
                // optionPanel.add(cardNumberField, "wrap");
                // optionPanel.add(new JLabel("CVV:"));
                // optionPanel.add(cvvField, "wrap");
                // optionPanel.add(new JLabel("Expiry Date:"));
                // optionPanel.add(expiryField, "wrap");
                // inputFields.add(cardNumberField);
                // inputFields.add(cvvField);
                // inputFields.add(expiryField);
                break;
            default:
                optionPanel.add(new JLabel("Please select a payment method."));
                break;
        }

        inputFieldsStore.put(option, inputFields); // store input fields in the map
        optionPanel.setPreferredSize(new Dimension(400, 300)); // adjust size as needed
        return optionPanel;
    }

    private void showPayOptionPage() {
        String selectedOption = (String) selectPay.getSelectedItem();
        if ("Select Payment Method".equals(selectedOption)) {
            cardPanel.setVisible(false);
        } else {
            cardPanel.setVisible(true);
            cardLayout.show(cardPanel, selectedOption);
        }
        cardPanel.revalidate();
        cardPanel.repaint();
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

    private boolean validateInput(String paymentMethod) {
        List<JComponent> inputFields = inputFieldsStore.get(paymentMethod);
        
        if (inputFields == null) {
            JOptionPane.showMessageDialog(this, "Error: No input fields found for the selected payment method.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        switch (paymentMethod) {
            case "UPI":
                JTextField upiIdField = (JTextField) inputFields.get(0);
                if (upiIdField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a UPI ID.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                if (!((String) upiIdField.getText().trim()).matches("^[\\w.-]+@[a-zA-Z]+[\\w.-]*$")) {
                    JOptionPane.showMessageDialog(this, "Invalid UPI ID.");
                    return false;
                }

                // checks if the upi id is in the db. 
                String query = "SELECT COUNT(*) FROM upi_service WHERE upi_vpa = ?"; 
                try (
                    Connection conn = DB.connect();
                    PreparedStatement pstmt = conn.prepareStatement(query)
                ) {
                    pstmt.setString(1, upiIdField.getText().trim());
                    ResultSet res = pstmt.executeQuery();

                    if (res.next()) {
                        if (!(res.getInt(1) != 0)) {
                            JOptionPane.showMessageDialog(this, "UPI NOT FOUND. Please check.", "Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                break;
            case "Net Banking":
                // if (inputFields == null || inputFields.size() < 1) {
                //     JOptionPane.showMessageDialog(this, "Input fields are missing.", "Error", JOptionPane.ERROR_MESSAGE);
                //     return false;
                // }
            
                // JComboBox<String> bankComboBox;
                // try {
                //     bankComboBox = (JComboBox<String>) inputFields.get(0);
                // } catch (ClassCastException e) {
                //     JOptionPane.showMessageDialog(this, "Invalid input field type.", "Error", JOptionPane.ERROR_MESSAGE);
                //     return false;
                // }
            
                // if (bankComboBox.getSelectedIndex() == 0) {
                //     JOptionPane.showMessageDialog(this, "Please select a bank.", "Warning", JOptionPane.WARNING_MESSAGE);
                //     return false;
                // }
                // if (fromAccountField.getText().trim().isEmpty()) {
                //     JOptionPane.showMessageDialog(this, "Please enter an account number.", "Warning", JOptionPane.WARNING_MESSAGE);
                //     return false;
                // }

                // System.out.println("Inga irukka.");
                // query = "SELECT COUNT(*) FROM accounts WHERE acc_no = ? AND bank_id = (SELECT bank_id FROM banks WHERE bank_name = ?)";
                // try (
                //     Connection conn = DB.connect();
                //     PreparedStatement pstmt = conn.prepareStatement(query)
                // ) {
                //     pstmt.setString(1, fromAccountField.getText().trim());
                //     System.out.println("Inga irukka.");
                //     pstmt.setString(2, (String) bankComboBox.getSelectedItem());
                //     System.out.println("Inga irukka.");
                //     ResultSet res = pstmt.executeQuery();

                //     if (res.next()) {
                //         if (!(res.getInt(1) > 0)) {
                //             JOptionPane.showMessageDialog(this, "Account Number NOT FOUND.", "Error", JOptionPane.ERROR_MESSAGE);
                //             return false;
                //         } else {
                //             return true;
                //         }
                //     }
                // } catch (SQLException e) {
                //     e.printStackTrace();
                //     return false;
                // }
                break;
            case "Credit / Debit":
                // JTextField cardNumberField = (JTextField) inputFields.get(0);
                // JTextField cvvField = (JTextField) inputFields.get(1);
                // JTextField expiryField = (JTextField) inputFields.get(2);
                // if (cardNumberField.getText().trim().isEmpty() || cvvField.getText().trim().isEmpty() || expiryField.getText().trim().isEmpty()) {
                //     JOptionPane.showMessageDialog(this, "Please fill in all card details.", "Warning", JOptionPane.WARNING_MESSAGE);
                //     return false;
                // }

                // // validates the card number and CVV from the database
                // SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                // sdf.setLenient(false);
                // String cardQuery = "SELECT card_cvv, card_expiry FROM cards WHERE card_number = ?";
                // try (
                //     Connection conn = DB.connect();
                //     PreparedStatement pstmt = conn.prepareStatement(cardQuery)
                // ) {
                //     pstmt.setString(1, cardNumberField.getText().trim());
                //     ResultSet res = pstmt.executeQuery();

                //     if (res.next()) {
                //         if (!res.getString("card_cvv").equals(cvvField.getText().trim())) {
                //             JOptionPane.showMessageDialog(this, "CVV doesn't match.", "Error", JOptionPane.ERROR_MESSAGE);
                //             return false;
                //         }
                //         Date cardExpiry = res.getDate("card_expiry");
                //         if (!sdf.format(cardExpiry).equals(expiryField.getText().trim())) {
                //             JOptionPane.showMessageDialog(this, "Expiry date doesn't match.", "Error", JOptionPane.ERROR_MESSAGE);
                //             return false;
                //         }
                //     } else {
                //         JOptionPane.showMessageDialog(this, "Card Number NOT FOUND.", "Error", JOptionPane.ERROR_MESSAGE);
                //         return false;
                //     }
                // } catch (SQLException e) {
                //     e.printStackTrace();
                //     JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                //     return false;
                // }

                // // validates the card expiry date format and check if it's expired
                // try {
                //     Date expiry = sdf.parse(expiryField.getText().trim());
                //     if (expiry.before(new Date())) {
                //         JOptionPane.showMessageDialog(this, "The card is expired.", "Error", JOptionPane.ERROR_MESSAGE);
                //         return false;
                //     }
                // } catch (ParseException e) {
                //     JOptionPane.showMessageDialog(this, "Invalid expiry date format. Use MM/yyyy.", "Error", JOptionPane.ERROR_MESSAGE);
                //     return false;
                // }
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid payment method selected.", "Error", JOptionPane.ERROR_MESSAGE);
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


class DateFilter extends DocumentFilter {
    private Pattern pattern = Pattern.compile("(0[1-9]|1[012])/[0-9]{4}");

    private boolean isValid(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.matches() || text.matches("^(0[1-9]|1[012])?/?([0-9]{0,4})?$");
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.insert(offset, string);

        if (isValid(sb.toString())) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        if (isValid(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
