package app.ui;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import app.ui.manager.panelHandler;

// Document filters
class NumberOnlyFilter extends DocumentFilter {

    private int maxChar;

    NumberOnlyFilter(int maxChar) {
        this.maxChar = maxChar;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

        if (string != null) {
            for (int i = 0; i < string.length(); i++) {
                if (!Character.isDigit(string.charAt(i))) {
                    return;
                }
            }
        }

        if (maxChar == 0)
            super.insertString(fb, offset, string, attr);
        else if ((fb.getDocument().getLength() + string.length()) <= maxChar)
            super.insertString(fb, offset, string, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                if (!Character.isDigit(text.charAt(i))) {
                    return;
                }
            }
        }

        if (maxChar == 0)
            super.replace(fb, offset, length, text, attrs);
        if ((fb.getDocument().getLength() + text.length()) <= maxChar)
            super.replace(fb, offset, length, text, attrs);
    }
}

public class CreditDebitCardFrame extends JFrame implements ActionListener {

    private JTextField nameField;
    private JTextField cardNumberField;
    private JTextField cvvField;
    private JButton confirmButton;
    JComboBox<String> monthComboBox;
    JComboBox<String> yearComboBox;

    private String cardHolderName;
    private String cardNumber;
    private String cvv;
    private CardVerification cv;

    public CreditDebitCardFrame(String toAccount, double amount) {

        cv = new CardVerification(toAccount, amount);

        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 16));
        FlatMacLightLaf.setup();
        setTitle("Credit/Debit Card Details");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left panel for image
        ImageIcon imageIcon = new ImageIcon("src/main/java/app/ui/pics/image-500x800.png");
        Image image = imageIcon.getImage();
        Image resizedImage = image.getScaledInstance(500, -1, Image.SCALE_SMOOTH); // Resize image
        ImageIcon resizedImageIcon = new ImageIcon(resizedImage);
        JLabel imageLabel = new JLabel(resizedImageIcon);
        mainPanel.add(imageLabel);

        // Right panel for credit card details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Credit card details
        JLabel nameLabel = new JLabel("Card Holder Name:");
        nameField = new JTextField(20);
        nameField.setEditable(false);

        JLabel cardNumberLabel = new JLabel("Card Number (16 digits):");
        cardNumberField = new JTextField(16);
        cardNumberField.setDocument(new PlainDocument());
        ((AbstractDocument) cardNumberField.getDocument()).setDocumentFilter(new NumberOnlyFilter(16));

        cardNumberField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateNameField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateNameField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateNameField();
            }

            private void updateNameField() {
                String cardNumber = cardNumberField.getText();
                if (cardNumber.length() == 16){
                    String name = cv.getName(cardNumber);
                    nameField.setText(name);
                } else {
                    nameField.setText("");
                }
            }
        });

        // Expiry date selection
        JLabel expiryLabel = new JLabel("Expiry:");
        monthComboBox = new JComboBox<>(getMonths());
        yearComboBox = new JComboBox<>(getYears());
        JPanel expiryPanel = new JPanel(new GridLayout(1, 2));
        expiryPanel.add(monthComboBox);
        expiryPanel.add(yearComboBox);
        expiryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cvvLabel = new JLabel("CVV:");
        cvvField = new JPasswordField(3);
        cvvField.putClientProperty(FlatClientProperties.STYLE, "" + "showRevealButton: true");
        cvvField.setDocument(new PlainDocument());
        ((AbstractDocument) cvvField.getDocument()).setDocumentFilter(new NumberOnlyFilter(3));

        // Confirm payment button
        confirmButton = new JButton("Confirm Payment");
        customizeButton(confirmButton);
        confirmButton.setEnabled(false); // Initially disable the button
        confirmButton.addActionListener(e -> {
            cardHolderName = nameField.getText();
            cardNumber = cardNumberField.getText();
            cvv = cvvField.getText();

            // Get the selected values as strings
            String selectedMonth = (String) monthComboBox.getSelectedItem();
            String selectedYear = (String) yearComboBox.getSelectedItem();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String dateStr = selectedYear + "-" + selectedMonth + "-31";
            LocalDate date = LocalDate.parse(dateStr, formatter);
            System.out.println(date);

            if (!cv.validateDetails(cardNumber, date, Integer.parseInt(cvv))) {
                return;
            }

            OTP otp = new OTP("Global Bank");
            String otpCheck = otp.generateOTP();
            if (otpCheck == null) {
                JOptionPane.showMessageDialog(null, "Error generating OTP.");
                return;
            }

            JPanel otpPanel = new JPanel();
            otpPanel.setLayout(new BoxLayout(otpPanel, BoxLayout.Y_AXIS));
            JLabel otpLabel = new JLabel("Enter OTP:");
            JTextField otpField = new JTextField(6);
            otpPanel.add(otpLabel);
            otpPanel.add(otpField);
            int result = JOptionPane.showConfirmDialog(null, otpPanel, "OTP Verification",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (otp.verifyOTP(otpField.getText())) {
                    if (cv.makeTransaction()) {
                        JOptionPane.showMessageDialog(null, "Payment Done!", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        this.dispose();
                        panelHandler.getInstance().show(new Home());
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Error making transaction!", "Failed",
                                JOptionPane.ERROR_MESSAGE);
                                nameField.setText("");
                                cardNumberField.setText("");
                                cvvField.setText("");
                                monthComboBox.setSelectedIndex(0);
                                yearComboBox.setSelectedIndex(0);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid OTP!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add components to details panel
        detailsPanel.add(createCenterAlignedPanel(nameLabel));
        detailsPanel.add(nameField);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(createCenterAlignedPanel(cardNumberLabel));
        detailsPanel.add(cardNumberField);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(createCenterAlignedPanel(expiryLabel));
        detailsPanel.add(expiryPanel);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(createCenterAlignedPanel(cvvLabel));
        detailsPanel.add(cvvField);
        detailsPanel.add(Box.createVerticalStrut(40));
        detailsPanel.add(createCenterAlignedPanel(confirmButton));

        // Add right panel to main panel
        mainPanel.add(createCenterAlignedPanel(detailsPanel));

        // Add main panel to content pane
        getContentPane().add(mainPanel);

        // Document listeners for input fields to enable/disable the Confirm Payment
        // button
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }
        });

        cardNumberField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }
        });

        cvvField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkEnableConfirmButton();
            }
        });
    }

    private JPanel createCenterAlignedPanel(JComponent component) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(component, constraints);
        return panel;
    }

    private void customizeTextField(JTextField textField) {
        textField.setFont(new Font("Calibri", Font.PLAIN, 16)); // Change font to Calibri
        textField.setPreferredSize(new Dimension(300, 30));
        textField.setMaximumSize(new Dimension(300, 30));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Calibri", Font.BOLD, 16)); // Change font to Calibri
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 153, 255)); // Blue color
        button.setBorder(BorderFactory.createLineBorder(new Color(51, 153, 255)));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
    }

    private String[] getMonths() {
        return new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
    }

    private String[] getYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[10];
        for (int i = 0; i < 10; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        return years;
    }

    private boolean isAllDataValid() {
        // Add your validation logic here
        return !nameField.getText().isEmpty() &&
                !cardNumberField.getText().isEmpty() &&
                !cvvField.getText().isEmpty(); // Add any additional validation checks if needed
    }

    // private boolean verifyOTP(String otp) {
    // // Replace this with your OTP verification logic
    // return otp != null && otp.equals("123456");
    // }

    private void checkEnableConfirmButton() {
        boolean enableButton = !nameField.getText().isEmpty() &&
                !cardNumberField.getText().isEmpty() &&
                !cvvField.getText().isEmpty() &&
                isAllDataValid(); // Add any additional validation checks if needed
        confirmButton.setEnabled(enableButton);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        System.out.println("here");

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CreditDebitCardFrame frame = new CreditDebitCardFrame("0123456789", 1000);
                frame.setVisible(true);
            }
        });
    }
}