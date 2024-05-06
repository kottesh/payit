import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JLabel titleLabel;

    public MainFrame() {
        setTitle("Jus' PayIT");
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background Image
        ImageIcon backgroundImageIcon = new ImageIcon("Java/ecommerce-2607114.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImageIcon);
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(900, 50));

        // Menu Options
        JMenu menuOptions = new JMenu("☰");
        JMenuItem faqOption = new JMenuItem("FAQ");
        JMenuItem contactUsOption = new JMenuItem("Contact Us");
        JMenuItem termsConditionsOption = new JMenuItem("Terms & Conditions");

        menuOptions.add(faqOption);
        menuOptions.add(contactUsOption);
        menuOptions.add(termsConditionsOption);

        menuBar.add(menuOptions);
        menuBar.add(Box.createHorizontalGlue());

        // Burger Menu
        JMenu burgerMenu = new JMenu("☰");
        JMenuItem createAccountMenuItem = new JMenuItem("Create Account");
        JMenuItem createUpiIdMenuItem = new JMenuItem("Create UPI ID");
        JMenuItem applyCardMenuItem = new JMenuItem("Apply for Credit/Debit Card");
        JMenuItem faqMenuItem = new JMenuItem("FAQ");

        burgerMenu.add(createAccountMenuItem);
        burgerMenu.add(createUpiIdMenuItem);
        burgerMenu.add(applyCardMenuItem);
        burgerMenu.add(faqMenuItem);

        menuBar.add(burgerMenu);

        setJMenuBar(menuBar);

        // Title Label with Marquee Effect
        titleLabel = new JLabel("PayIT karo!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        menuBar.add(titleLabel);

        // Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        // Buttons
        JButton upiPaymentButton = new JButton("UPI Payment");
        JButton netbankingButton = new JButton("Netbanking");
        JButton cardButton = new JButton("Credit/Debit Card");

        // Customize buttons
        customizeButton(upiPaymentButton);
        customizeButton(netbankingButton);
        customizeButton(cardButton);

        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(upiPaymentButton);
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(netbankingButton);
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(cardButton);

        // Add ActionListener to UPI Payment button
        upiPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open UPI Payment UI
                UpiPaymentUI upiPaymentUI = new UpiPaymentUI();
                upiPaymentUI.setVisible(true);

                dispose();
            }
        });

        // Copyrights
        JLabel copyrightsLabel = new JLabel("© 2024 PayIT. All Rights Reserved.");
        copyrightsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightsLabel.setForeground(Color.GRAY);
        copyrightsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(Box.createVerticalStrut(50), BorderLayout.NORTH);
        mainPanel.add(Box.createVerticalStrut(50), BorderLayout.SOUTH);
        mainPanel.add(Box.createHorizontalStrut(50), BorderLayout.WEST);
        mainPanel.add(Box.createHorizontalStrut(50), BorderLayout.EAST);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(new JPanel(), BorderLayout.SOUTH); // Placeholder for alignment

        // Add main panel to content pane
        backgroundLabel.add(mainPanel, BorderLayout.CENTER);
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 102, 204)); // Blue color
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
