import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Calendar;

public class CreditDebitCardFrame extends JFrame {
    public CreditDebitCardFrame() {
        setTitle("Credit/Debit Card Details");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left panel for image
        ImageIcon imageIcon = new ImageIcon("Java/image-500x800.png");
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
        JTextField nameField = new JTextField(20);
        customizeTextField(nameField);

        JLabel cardNumberLabel = new JLabel("Card Number (16 digits):");
        JTextField cardNumberField = new JTextField(16);
        customizeTextField(cardNumberField);
        cardNumberField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkCardNumberLength(cardNumberField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkCardNumberLength(cardNumberField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkCardNumberLength(cardNumberField);
            }
        });

        // Expiry date selection
        JLabel expiryLabel = new JLabel("Expiry:");
        JComboBox<String> monthComboBox = new JComboBox<>(getMonths());
        JComboBox<String> yearComboBox = new JComboBox<>(getYears());
        JPanel expiryPanel = new JPanel(new GridLayout(1, 2));
        expiryPanel.add(monthComboBox);
        expiryPanel.add(yearComboBox);
        expiryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cvvLabel = new JLabel("CVV:");
        JTextField cvvField = new JTextField(3);
        customizeTextField(cvvField);
        cvvField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkCVVLength(cvvField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkCVVLength(cvvField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkCVVLength(cvvField);
            }
        });

        JLabel emailLabel = new JLabel("Email ID:");
        JTextField emailField = new JTextField(20);
        customizeTextField(emailField);

        // Account number
        JLabel accountNumberLabel = new JLabel("To Bank Account Number:");
        JTextField accountNumberField = new JTextField(16);
        customizeTextField(accountNumberField);

        // Confirm payment button
        JButton confirmButton = new JButton("Confirm Payment");
        customizeButton(confirmButton);
        confirmButton.addActionListener(e -> {
            if (isAllDataValid()) {
                JOptionPane.showMessageDialog(this, "Payment Done!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(createCenterAlignedPanel(emailLabel));
        detailsPanel.add(emailField);
        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(createCenterAlignedPanel(accountNumberLabel));
        detailsPanel.add(accountNumberField);
        detailsPanel.add(Box.createVerticalStrut(40));
        detailsPanel.add(createCenterAlignedPanel(confirmButton));

        // Add right panel to main panel
        mainPanel.add(createCenterAlignedPanel(detailsPanel));

        // Add main panel to content pane
        getContentPane().add(mainPanel);
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

    private void checkCardNumberLength(JTextField cardNumberField) {
        String cardNumber = cardNumberField.getText();
        if (cardNumber.length() > 16) {
            JOptionPane.showMessageDialog(this, "Card number cannot exceed 16 digits", "Error", JOptionPane.ERROR_MESSAGE);
            cardNumberField.setText(cardNumber.substring(0, 16));
        }
    }

    private void checkCVVLength(JTextField cvvField) {
        String cvv = cvvField.getText();
        if (cvv.length() > 3) {
            JOptionPane.showMessageDialog(this, "CVV cannot exceed 3 digits", "Error", JOptionPane.ERROR_MESSAGE);
            cvvField.setText(cvv.substring(0, 3));
        }
    }

    private String[] getMonths() {
        return new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
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
        return true; // Placeholder, replace with actual validation
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CreditDebitCardFrame frame = new CreditDebitCardFrame();
                frame.setVisible(true);
            }
        });
    }
}
