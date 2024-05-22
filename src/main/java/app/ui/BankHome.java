package app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import app.ui.manager.panelHandler;
import app.utils.DB;

public class BankHome extends JPanel {
    private JLabel accountNumberValueLabel;
    private JLabel balanceValueLabel;
    private JTable transactionTable;
    private JLabel noTransactionLabel;

    public BankHome(String username) {
        setLayout(new BorderLayout());

        // create the menu bar with burger menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("\u2630"); // burger menu symbol
        menu.setFont(new Font("Arial", Font.PLAIN, 12));
        menu.setVerticalAlignment(JMenu.CENTER);
        menu.setHorizontalAlignment(JMenu.CENTER);
        JMenuItem servicesMenuItem = new JMenuItem("Services");
        JMenuItem closeAccountMenuItem = new JMenuItem("Close Account");
        JMenuItem logOutMenuItem = new JMenuItem("Log Out");

        logOutMenuItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(logOutMenuItem, "Are you sure want to logout ?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                panelHandler.getInstance().show(new Login());
            }
        });

        menu.add(servicesMenuItem);
        menu.add(closeAccountMenuItem);
        menu.add(logOutMenuItem);
        menuBar.add(menu);

        // create the top panel with account info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(menuBar, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel accountNumberLabel = new JLabel("Account No:");
        accountNumberValueLabel = new JLabel("1234567890"); // placeholder for account number
        JLabel balanceLabel = new JLabel("Balance:");
        balanceValueLabel = new JLabel("$0.00"); // placeholder for balance

        // Set font size for account number and balance labels
        accountNumberValueLabel.setFont(new Font(accountNumberValueLabel.getFont().getName(), Font.PLAIN, 18));
        balanceValueLabel.setFont(new Font(balanceValueLabel.getFont().getName(), Font.PLAIN, 18));

        infoPanel.add(accountNumberLabel);
        infoPanel.add(accountNumberValueLabel);
        infoPanel.add(balanceLabel);
        infoPanel.add(balanceValueLabel);
        topPanel.add(infoPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Create the table for transaction logs
        String[] columnNames = {"DATE", "REFERENCE ID", "TYPE", "MODE", "AMOUNT", "STATUS"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { // this makes the cells not editable 
                return false;
            }
        };
        transactionTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        noTransactionLabel = new JLabel("No transactions to show", SwingConstants.CENTER);
        noTransactionLabel.setForeground(Color.RED);
        noTransactionLabel.setVisible(false); // initially hidden
        add(noTransactionLabel, BorderLayout.SOUTH);

        // populate account information and transaction logs from the database
        fetchAccountInfo(username);
        fetchTransactionLogs();
    }

    private void fetchAccountInfo(String username) {
        String acc_sql = "SELECT acc_no FROM login_credentials WHERE username = ?";
        String balance_sql = "SELECT balance FROM accounts WHERE acc_no = ?";
        try (Connection conn = DB.connect()) {
            String accountNumber = null;

            // fetch the account number
            try (PreparedStatement pstmt = conn.prepareStatement(acc_sql)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        accountNumber = rs.getString("acc_no");
                        accountNumberValueLabel.setText(accountNumber);
                    }
                }
            }

            // fetch the balance using the account number
            if (accountNumber != null) {
                try (PreparedStatement pstmt = conn.prepareStatement(balance_sql)) {
                    pstmt.setString(1, accountNumber);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            balanceValueLabel.setText(rs.getString("balance"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void fetchTransactionLogs() {
        String trans_sql = "SELECT * FROM transactions WHERE from_account_number = ?";
        try (Connection conn = DB.connect();
             PreparedStatement pstmt = conn.prepareStatement(trans_sql)) {
            
            // Get the account number from the label
            String accountNumber = accountNumberValueLabel.getText();
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Date transactionDate = rs.getDate("transaction_time");
                String transactionStatus = rs.getString("transaction_status");
                String transactionReferenceNumber = rs.getString("transaction_reference_number");
                String transactionType = rs.getString("transaction_type");
                String transactionAmount = rs.getString("transaction_amount");
                String transactionMode = rs.getString("transaction_mode");
                
                model.addRow(new Object[]{
                    transactionDate, transactionReferenceNumber, 
                    transactionType, transactionMode, transactionAmount, transactionStatus
                });
            }
            
            if (!hasData) {
                noTransactionLabel.setVisible(true); // show the "No transactions" label
            }

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < transactionTable.getColumnCount(); i++) {
                transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
