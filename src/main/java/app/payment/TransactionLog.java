package app.payment;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import app.utils.DB;

public class TransactionLog {

    private String referenceNumber;

    private enum modeID {
        NB("NET BANKING"),
        UPI("UPI"),
        CC("CARDS"),
        DC("CARDS");

        private String value;

        private modeID(String value) {
            this.value = value;
        }
    }

    private enum payment {
        UPI(16),
        NB(99),
        CC(80),
        DC(87);

        private final int VALUE;

        private payment(int value) {
            this.VALUE = value;
        }
    }

    public boolean updateTransactionLog(String bankName, String fromAccount, String toAccount, double amount, String type, String mode, String status) {
        Connection connection = null;
        PreparedStatement statement = null;

        if (bankName == null) {
            bankName = "Global Bank";
        }

        referenceNumber = generateReferenceNumber(status, bankName, mode);

        String modeStr = modeID.valueOf(mode).value;

        try {
            connection = DB.connect();

            String query = "INSERT INTO transactions (transaction_time, from_account_number, transaction_status, transaction_reference_number, transaction_type, transaction_amount, to_account_number, transaction_mode) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, fromAccount);
            statement.setString(2, capitalize(status));
            statement.setString(3, referenceNumber);
            statement.setString(4, capitalize(type));
            statement.setDouble(5, amount);
            statement.setString(6, toAccount);
            statement.setString(7, modeStr);

            statement.executeUpdate();

            String Rtype;
            if (type.equals("Credit"))
                Rtype = "Debit";
            else
                Rtype = "Credit";

            query = "INSERT INTO transactions (transaction_time, from_account_number, transaction_status, transaction_reference_number, transaction_type, transaction_amount, to_account_number, transaction_mode) VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, toAccount);
            statement.setString(2, capitalize(status));
            statement.setString(3, referenceNumber);
            statement.setString(4, capitalize(Rtype));
            statement.setDouble(5, amount);
            statement.setString(6, fromAccount);
            statement.setString(7, modeStr);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Server error!");
            return false;
        }
        return true;
    }

    private String capitalize(String value) {
        value = value.toLowerCase();
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private String generateReferenceNumber(String status, String bankName, String mode) {
        StringBuilder referenceNumberBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        // Fetch the bank code from the database
        String bankCode = fetchBankCode(bankName);
        if (bankCode == null) {
            System.err.println("Invalid bank name provided: " + bankName);
            throw new IllegalArgumentException("Invalid bank name provided.");
        }

        /*
         * (Assuming) The reference number is of length 16.
         * This is split as 2 + 4 + 1 + 6 + 3 
         * 2 - mode code
         * 4 - Bank code
         * 1 - status
         * 6 - Transaction day
         * 3 - transaction number
         */

        // Mode of payment
        referenceNumberBuilder.append(payment.valueOf(mode.toUpperCase()).VALUE);

        // Bank code
        referenceNumberBuilder.append(bankCode);

        // Status
        referenceNumberBuilder.append((status.equalsIgnoreCase("success")) ? 1 : 0);

        // Date
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        referenceNumberBuilder.append(cal.get(Calendar.MONTH));
        referenceNumberBuilder.append(cal.get(Calendar.DAY_OF_MONTH) + 1);
        referenceNumberBuilder.append((cal.get(Calendar.YEAR) + "").substring(2));

        // Random transaction number
        for (int i = 0; i < 3; i++) {
            referenceNumberBuilder.append(random.nextInt(10));
        }

        return referenceNumberBuilder.toString();
    }

    private static String fetchBankCode(String bankName) {
        String bankCode = null;
        String query = "SELECT SUBSTR(bank_name, 1, 4) AS bank_code FROM banks WHERE bank_name = ?";
        try (Connection conn = DB.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bankName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    bankCode = rs.getString("bank_code").toUpperCase();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error fetching bank code for bank name: " + bankName);
        }
        return bankCode;
    }
}
