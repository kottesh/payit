package app.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

public class Pay extends JPanel {
    private JLabel titleLabel;

    public Pay() {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 16));
        FlatMacLightLaf.setup();
        
        // Background Image
        ImageIcon backgroundImageIcon = new ImageIcon("src/main/java/app/ui/pics/ecommerce-2607114.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImageIcon);
        backgroundLabel.setLayout(new BorderLayout());
        add(backgroundLabel);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setPreferredSize(new Dimension(900, 50));

        // Menu Options
        JMenu menuOptions = new JMenu("-->");
        JMenuItem faqOption = new JMenuItem("FAQ");
        JMenuItem contactUsOption = new JMenuItem("Contact Us");
        JMenuItem termsConditionsOption = new JMenuItem("Terms & Conditions");

        menuOptions.add(faqOption);
        menuOptions.add(contactUsOption);
        menuOptions.add(termsConditionsOption);

        menuBar.add(menuOptions);
        menuBar.add(Box.createHorizontalGlue());

        // Burger Menu
        JMenu burgerMenu = new JMenu("<--");
        JMenuItem createAccountMenuItem = new JMenuItem("Create Account");
        JMenuItem createUpiIdMenuItem = new JMenuItem("Create UPI ID");
        JMenuItem applyCardMenuItem = new JMenuItem("Apply for Credit/Debit Card");
        JMenuItem faqMenuItem = new JMenuItem("FAQ");

        burgerMenu.add(createAccountMenuItem);
        burgerMenu.add(createUpiIdMenuItem);
        burgerMenu.add(applyCardMenuItem);
        burgerMenu.add(faqMenuItem);

        menuBar.add(burgerMenu);

        add(menuBar);

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
                UpiPayment upiPaymentUI = new UpiPayment();
                // upiPaymentUI.setVisible(true);
                // dispose();
            }
        });

        /*netbankingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                
            }
        })*/

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
}
