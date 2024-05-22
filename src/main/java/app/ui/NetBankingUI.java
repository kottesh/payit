package app.ui;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

/*
 * class AlphaNumericFilter extends DocumentFilter {
 * 
 * @Override
 * public void insertString(FilterBypass fb, int offset, String string,
 * AttributeSet attr) throws BadLocationException {
 * 
 * if (string != null) {
 * for (int i = 0; i < string.length(); i++) {
 * if (!Character.isDigit(string.charAt(i)) && !Character.isAlphabetic(i)) {
 * return;
 * }
 * }
 * }
 * 
 * super.insertString(fb, offset, string, attr);
 * }
 * 
 * @Override
 * public void replace(FilterBypass fb, int offset, int length, String text,
 * AttributeSet attrs) throws BadLocationException {
 * 
 * if (text != null) {
 * for (int i = 0; i < text.length(); i++) {
 * if (!Character.isDigit(text.charAt(i)) && !Character.isAlphabetic(i)) {
 * return;
 * }
 * }
 * }
 * 
 * super.replace(fb, offset, length, text, attrs);
 * }
 * }
 */

// Class for custom JPanel with background image
class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        // Set panel size to match background image size
        setPreferredSize(new Dimension(backgroundImage.getWidth(null), backgroundImage.getHeight(null)));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        g.drawImage(backgroundImage, 0, 0, null);
    }
}

// Main class for NetBankingUI
public class NetBankingUI extends JFrame implements ActionListener {
    // Declarations of UI components and variables
    private NetBanking nb; // Instance of NetBanking class

    // Buttons
    private JButton loginButton;
    private JButton submitButton;
    private JButton submitButton2;
    private JButton submitButton3;
    private JLabel usernamelabel, passwordlabel; // Labels for username and password fields
    private final JTextField usernameField; // Username text field
    private final JPasswordField passwordField; // Password text field

    private String bankName;
    private String toAccount;
    private double amount;

    // Constructor
    // NetBankingUI() {
    // netBanking = new NetBanking();
    // usernameField = new JTextField(15);
    // passwordField = new JPasswordField(15);
    // }

    public NetBankingUI(String bankName, String toAccount, double amount) {

        this.bankName = bankName;
        this.toAccount = toAccount;
        this.amount = amount;

        nb = new NetBanking(bankName, toAccount, amount);

        // Initialize username and password fields
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        // Initialize labels and login button
        usernamelabel = new JLabel("USERNAME:");
        passwordlabel = new JLabel("PASSWORD:");
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(80, 30));

        // Adding key listeners to buttons
        loginButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "login");
        loginButton.getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });

        // // Create the left panel (image panel)
        // ImagePanel imagePanel = new ImagePanel(backgroundImage);

        // Create the right panel for the login form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridy++;
        rightPanel.add(usernamelabel, gbc);
        gbc.gridy++;
        rightPanel.add(usernameField, gbc);
        gbc.gridy++;
        rightPanel.add(passwordlabel, gbc);
        gbc.gridy++;
        rightPanel.add(passwordField, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(loginButton, gbc);

        // Create the main panel (border layout)
        JPanel mainPanel = new JPanel(new BorderLayout());
        // mainPanel.add(imagePanel, BorderLayout.WEST); // Image panel on the left
        mainPanel.add(rightPanel, BorderLayout.CENTER); // Login form on the right

        // Set up Log in frame
        add(mainPanel); // Set the main panel as the content pane
        loginButton.addActionListener(this); // Add action listener to login button
        setTitle(bankName + " - Netbanking");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    // ActionListener method for handling button clicks
    /*
     * 1. Log in
     * - username validation
     * - password validation
     * 2. Transaction details
     * - from account number
     * - to account number
     * - amount
     * 3. Transaction Password validation
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String usernameValue = usernameField.getText();
        char[] passwordValue = passwordField.getPassword();

        // Log in.
        if (nb.login(usernameValue, String.valueOf(passwordValue))) {
            // If login successful, show success message.
            String fromAccount = nb.getAccountNumber(usernameValue);
            if (fromAccount == null) {
                JOptionPane.showMessageDialog(this, "No account is linked with the user!", "Error Login", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            JOptionPane.showMessageDialog(this, "Welcome, " + usernameValue + " :)", "Login Successful", JOptionPane.INFORMATION_MESSAGE);

            // Close current frame.
            dispose();

            JFrame newFrame2 = new JFrame(bankName + " - Netbanking");
            JPanel panel2 = new JPanel(new GridBagLayout());
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridx = 0;
            gbc2.gridy = 0;
            gbc2.anchor = GridBagConstraints.WEST;
            gbc2.insets = new Insets(10, 10, 10, 10);

            // Create components for transaction password
            JLabel TPLabel = new JLabel("Transaction Password:");
            JPasswordField TPField = new JPasswordField(15);
            panel2.add(TPLabel, gbc2);
            gbc2.gridy++;
            panel2.add(TPField, gbc2);

            submitButton2 = new JButton("Submit");
            submitButton2.setPreferredSize(new Dimension(80, 30));
            gbc2.gridy++;
            gbc2.fill = GridBagConstraints.HORIZONTAL;
            panel2.add(submitButton2, gbc2);

            // Adding key listeners to buttons
            submitButton2.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit2");
            submitButton2.getActionMap().put("submit2", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    submitButton2.doClick();
                }
            });

            // Configure the new frame for transaction password
            newFrame2.getContentPane().add(panel2);
            newFrame2.setSize(500, 400);
            newFrame2.setLocationRelativeTo(null);
            newFrame2.setVisible(true);

            // Action listener for submit button in transaction password frame
            submitButton2.addActionListener(e2 -> {
                // Get transaction password value
                char[] TPValue = TPField.getPassword();

                // Validate transaction password
                if (nb.validateTransaction(usernameValue, String.valueOf(TPValue))) {
                    // If transaction password is valid, proceed to OTP verification
                    JFrame newFrame3 = new JFrame(bankName + " - Netbanking");
                    JPanel panel3 = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc3 = new GridBagConstraints();
                    gbc3.gridx = 0;
                    gbc3.gridy = 0;
                    gbc3.anchor = GridBagConstraints.WEST;
                    gbc3.insets = new Insets(10, 10, 10, 10);

                    // Create components for OTP input
                    JLabel OTPLabel = new JLabel("OTP:");
                    JTextField OTPField = new JPasswordField(15);
                    panel3.add(OTPLabel, gbc3);
                    gbc3.gridy++;
                    panel3.add(OTPField, gbc3);

                    submitButton3 = new JButton("Submit");
                    gbc3.gridy++;
                    gbc3.fill = GridBagConstraints.HORIZONTAL;
                    panel3.add(submitButton3, gbc3);

                    // Adding key listeners to buttons
                    submitButton3.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit3");
                    submitButton3.getActionMap().put("submit3", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            submitButton3.doClick();
                        }
                    });

                    // Configure the new frame for OTP verification
                    newFrame3.getContentPane().add(panel3);
                    newFrame3.setSize(500, 400);
                    newFrame3.setLocationRelativeTo(null);
                    newFrame3.setVisible(true);

                    // Generate OTP
                    OTP otp = new OTP(bankName);
                    String otpCheck = otp.generateOTP();
                    if (otpCheck == null) {
                        JOptionPane.showMessageDialog(null, "Error generating OTP.");
                        return;
                    }

                    // Action listener for submit button in OTP verification frame
                    submitButton3.addActionListener(e3 -> {
                        // Verify OTP entered by the user
                        if (otp.verifyOTP(OTPField.getText())) {
                            if (!nb.validateAmount(amount)) {
                                JOptionPane.showMessageDialog(null, "You have exceeded the bank limit of ₹50,000 for this payment. Retry with smaller amount.", "", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (!nb.checkBalance(amount)) {
                                JOptionPane.showMessageDialog(null, "Insufficient balance in your account.", "", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            // If OTP is valid, proceed with transaction
                            if (!nb.transferAmount(amount, toAccount, fromAccount)) {
                                JOptionPane.showMessageDialog(null,
                                        "Unexpected error occurred while making transaction", "Transaction failed!", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Display transaction information on a new frame
                            JFrame infoFrame = new JFrame("Transaction Information");
                            JPanel infoPanel = new JPanel(new GridBagLayout());
                            GridBagConstraints infoGBC = new GridBagConstraints();
                            infoGBC.gridx = 0;
                            infoGBC.gridy = 0;
                            infoGBC.anchor = GridBagConstraints.WEST;
                            infoGBC.insets = new Insets(10, 10, 10, 10);

                            // Get current date and time
                            LocalDateTime now = LocalDateTime.now();
                            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(" HH:mm:ss");
                            String date = now.format(formatter1);
                            String time = now.format(formatter2);

                            // Create labels for transaction information
                            JLabel fromInfoLabel = new JLabel(" From               : " + fromAccount);
                            JLabel toInfoLabel = new JLabel(" To                   : " + toAccount);
                            JLabel amountInfoLabel = new JLabel(" Amount           : " + amount);
                            JLabel dateInfoLabel = new JLabel(" Date               : " + date);
                            JLabel timeInfoLabel = new JLabel(" Time               : " + time);
                            JLabel tsInfoLabel = new JLabel("\n\nTransaction status : Success");
                            tsInfoLabel.setForeground(Color.BLUE);

                            // Add transaction information labels to panel
                            infoPanel.add(fromInfoLabel, infoGBC);
                            infoGBC.gridy++;
                            infoPanel.add(toInfoLabel, infoGBC);
                            infoGBC.gridy++;
                            infoPanel.add(amountInfoLabel, infoGBC);
                            infoGBC.gridy++;
                            infoPanel.add(dateInfoLabel, infoGBC);
                            infoGBC.gridy++;
                            infoPanel.add(timeInfoLabel, infoGBC);
                            infoGBC.gridy++;
                            infoPanel.add(tsInfoLabel, infoGBC);
                            infoGBC.gridy++;

                            // Configure the frame for transaction information
                            infoFrame.getContentPane().add(infoPanel);
                            infoFrame.setSize(400, 300);
                            infoFrame.setLocationRelativeTo(null);
                            infoFrame.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid OTP!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid transaction password!", "Transactoin failed!", JOptionPane.ERROR_MESSAGE);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this,"Username/password is incorrect.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        // Run the application on the Event Dispatch Thread
        // SwingUtilities.invokeLater(() -> new NetBankingUI());
        NetBankingUI nb = new NetBankingUI("Global", "1234567890", 1000);
    }
}
