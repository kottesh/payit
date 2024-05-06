import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpiPaymentUI extends JFrame {
    private JTextField fromUpiField, toUpiField, amountField, remarksField, pinField;
    private JButton makePaymentButton;

    private final int TRANSACTION_LIMIT = 25000;

    public UpiPaymentUI() {
        setTitle("UPI Payments - by PayIT");
        setSize(650, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("UPI Payments - by PayIT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.setBackground(new Color(240, 240, 240));
        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.setBackground(new Color(240, 240, 240));

        mainPanel.add(new JLabel("From UPI ID:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("To UPI ID:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Remarks:"), gbc);
        gbc.gridy++;
        mainPanel.add(new JLabel("Mobile Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fromUpiField = createTextField(20);
        mainPanel.add(fromUpiField, gbc);
        gbc.gridy++;
        toUpiField = createTextField(20);
        mainPanel.add(toUpiField, gbc);
        gbc.gridy++;
        amountField = createTextField(20);
        mainPanel.add(amountField, gbc);
        gbc.gridy++;
        remarksField = createTextField(20);
        mainPanel.add(remarksField, gbc);

        gbc.gridy++;
        JTextField mobileNumberField = createTextField(20);
        mainPanel.add(mobileNumberField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        makePaymentButton = createButton("Make Payment");
        makePaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Check if From UPI ID and To UPI ID contain '@'
                if (!fromUpiField.getText().contains("@") || !toUpiField.getText().contains("@")) {
                    JOptionPane.showMessageDialog(UpiPaymentUI.this, "UPI IDs must contain '@'.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if transaction amount is within limit
                try {
                    int amount = Integer.parseInt(amountField.getText());
                    if (amount > TRANSACTION_LIMIT) {
                        JOptionPane.showMessageDialog(UpiPaymentUI.this, "Transaction amount exceeds limit.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(UpiPaymentUI.this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Proceed to PIN page
                showPinDialog();
            }
        });
        mainPanel.add(makePaymentButton, gbc);

        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        add(mainPanel, BorderLayout.CENTER);
    }

    private JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setDocument(new UpiDocument());
        textField.setBackground(new Color(255, 255, 255));
        textField.setForeground(new Color(0, 0, 0));
        textField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        return textField;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 153, 204));
        button.setForeground(new Color(255, 255, 255));
        return button;
    }

    private void showPinDialog() {
        // PIN Dialog UI
        JFrame pinFrame = new JFrame("Enter PIN");
        pinFrame.setSize(300, 400);
        pinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pinFrame.setLayout(new BorderLayout());

        // PIN Field
        pinField = new JTextField(4);
        pinField.setHorizontalAlignment(JTextField.CENTER);
        pinField.setEditable(false); // Disable manual entry
        pinFrame.add(pinField, BorderLayout.NORTH);

        // PIN Panel
        JPanel pinPanel = new JPanel();
        pinPanel.setLayout(new GridLayout(4, 3, 10, 10)); // Adding spacing between buttons
        pinPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adding padding
        for (int i = 1; i <= 9; i++) {
            JButton button = new JButton(Integer.toString(i));
            button.setBackground(new Color(200, 200, 200)); // Setting button background color
            button.setForeground(Color.BLACK); // Setting button text color
            button.setFont(new Font("Arial", Font.PLAIN, 20)); // Setting button font
            button.addActionListener(new PinButtonListener(pinField));
            pinPanel.add(button);
        }
        pinPanel.add(new JLabel()); // Empty cell
        JButton zeroButton = new JButton("0");
        zeroButton.setBackground(new Color(200, 200, 200)); // Setting button background color
        zeroButton.setForeground(Color.BLACK); // Setting button text color
        zeroButton.setFont(new Font("Arial", Font.PLAIN, 20)); // Setting button font
        zeroButton.addActionListener(new PinButtonListener(pinField));
        pinPanel.add(zeroButton);
        pinPanel.add(new JLabel()); // Empty cell
        pinFrame.add(pinPanel, BorderLayout.CENTER);

        // Confirm Payment Button
        JButton confirmPaymentButton = new JButton("Confirm Payment");
        confirmPaymentButton.setBackground(new Color(0, 153, 204)); // Setting button background color
        confirmPaymentButton.setForeground(Color.WHITE); // Setting button text color
        confirmPaymentButton.setFont(new Font("Arial", Font.BOLD, 16)); // Setting button font
        confirmPaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Check PIN length
                if (pinField.getText().length() != 4) {
                    JOptionPane.showMessageDialog(pinFrame, "PIN must be 4 digits.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Perform payment
                // Placeholder action
                JOptionPane.showMessageDialog(pinFrame, "Payment Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                pinFrame.dispose(); // Close PIN dialog
            }
        });
        pinFrame.add(confirmPaymentButton, BorderLayout.SOUTH);

        // Centering PIN Frame
        pinFrame.setLocationRelativeTo(null);
        pinFrame.setVisible(true);
    }

    // PinButtonListener class
    class PinButtonListener implements ActionListener {
        private JTextField pinField;
    
        public PinButtonListener(JTextField pinField) {
            this.pinField = pinField;
        }
    
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (pinField.getText().length() < 4) {
                pinField.setText(pinField.getText() + button.getText());
            } else {
                JOptionPane.showMessageDialog(null, "PIN must be 4 digits.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    

    private boolean isValidTransaction() {
        // Implement your validation logic here
        return true;
    }

    private void showTransactionDetails(String from, String to, String amount, String status) {
        // Implement displaying transaction details
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UpiPaymentUI().setVisible(true);
            }
        });
    }

    class UpiDocument extends PlainDocument {
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) {
                return;
            }

            String newStr = getText(0, getLength()) + str;
            if (newStr.contains("@") && newStr.indexOf("@") != newStr.lastIndexOf("@")) {
                return;
            }

            super.insertString(offs, str, a);
        }
    }
}
