package app.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CreditDebitCardApplicationForm extends JFrame {
    private JTextField accountNumberField;
    private JTextField cardHolderNameField;
    private JRadioButton creditRadioButton;
    private JRadioButton debitRadioButton;

    public CreditDebitCardApplicationForm() {
        setTitle("Credit/Debit Card Application Form");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Account Number
        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberField = new JTextField(12); // Adjusted text field size
        customizeTextField(accountNumberField);
        mainPanel.add(createLabelFieldPanel(accountNumberLabel, accountNumberField));

        // Card Holder Name
        JLabel cardHolderNameLabel = new JLabel("Card Holder Name:");
        cardHolderNameField = new JTextField(12); // Adjusted text field size
        customizeTextField(cardHolderNameField);
        mainPanel.add(createLabelFieldPanel(cardHolderNameLabel, cardHolderNameField));

        // Credit/Debit Selection
        JLabel cardTypeLabel = new JLabel("Credit/Debit:");
        creditRadioButton = new JRadioButton("Credit");
        debitRadioButton = new JRadioButton("Debit");
        ButtonGroup cardTypeGroup = new ButtonGroup();
        cardTypeGroup.add(creditRadioButton);
        cardTypeGroup.add(debitRadioButton);
        JPanel cardTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cardTypePanel.add(creditRadioButton);
        cardTypePanel.add(debitRadioButton);
        mainPanel.add(createLabelFieldPanel(cardTypeLabel, cardTypePanel));

        // Apply for card button
        JButton applyButton = new JButton("Apply for Card");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyForCard();
            }
        });
        customizeButton(applyButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(applyButton);

        getContentPane().add(mainPanel);
    }

    private JPanel createLabelFieldPanel(JLabel label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void applyForCard() {
        String accountNumber = accountNumberField.getText();
        String cardHolderName = cardHolderNameField.getText();
        String cardType = creditRadioButton.isSelected() ? "Credit" : "Debit";
        String cardNumber = generateCardNumber();

        String message = "Card Holder Name: " + cardHolderName + "\n" +
                "Card Number: " + cardNumber + "\n" +
                "Card Type: " + cardType;

        JOptionPane.showMessageDialog(this, message, "Card Application Submitted", JOptionPane.INFORMATION_MESSAGE);
    }

    private String generateCardNumber() {
        long timestamp = System.currentTimeMillis();
        Random random = new Random(timestamp);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private void customizeTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(150, 30)); // Adjusted dimension
        textField.setMaximumSize(new Dimension(150, 30)); // Adjusted dimension
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 153, 255)); // Blue color
        button.setBorder(BorderFactory.createLineBorder(new Color(51, 153, 255)));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CreditDebitCardApplicationForm frame = new CreditDebitCardApplicationForm();
                frame.setVisible(true);
            }
        });
    }
}
