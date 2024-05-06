package app;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CardVerification {

    // private static UserDetails user;

    private Connection connectToDB() {

        final String URL = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
        final String USR_NAME = "system";
        final String PWD = "sk";
        final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

        Connection connection = null;

        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(URL, USR_NAME, PWD);

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }

        return connection;
    }

    public boolean authenticate(String cardNumber, Date expDate, int cvv, int amount, String toAccountNumber) {

        /*
         * Authenticate Card:
         * 1. Checking if the amount is valid (not less than 1).
         * 2. validateDetails() method checks if the card number exists in the database.
         *    If it exists, other details (expiry date and cvv) is checked.
         * 3. OTP.verifyOTP() is a method in OTP module, where it generates and verifies the otp.
         * 4. authoriseTransaction() method is used to check if the amount can be debited from the account or not.
         * 5. transferAmount() method updates balance amount in both (sender's and reciver's) account in the database.
         */

        if (amount <= 0) {
            System.out.println("Payment must be atleast ₹1");
            return false;
        }

        if (!validateDetails(cardNumber, expDate, cvv)) {
            System.out.println("Error validating card\nCV: Card verifivation error.");
            return false;
        }

        // Verify using otp
        
        if (!verifyOTP(cardNumber)) {
            System.err.println("Error validating Credit card: OTP verification failed.");
            return false;
        }

        // Check for suficient fund.
        if (!authoriseTransaction(amount, cardNumber)) {
            System.out.println("Error code-65: Insuficient fund!");
            return false;
        }

        if (!transferAmount(amount, toAccountNumber, cardNumber)) {
            System.out.println("Error code-66: Transfer unsuccessful! Please try again later.");
            return false;
        }

        return true;

    }

    private boolean verifyOTP(String cardNumber) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectToDB();

            String cardTableQuery = "SELECT phone_number FROM customers WHERE customer_id = (SELECT customer_id FROM accounts WHERE acc_no = (SELECT acc_no FORM cards WHERE card_number = ?))";
            statement = connection.prepareStatement(cardTableQuery);
            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) return false;

            if (!OTP.verifyOTP(resultSet.getString("phone_number"))) {
                System.err.println("Error validating Credit card: OTP verification failed.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return true;
    }

    private boolean transferAmount(long amount, String toAccountNumber, String cardNumber) {

        PreparedStatement statement = null;
        Connection connection = null;
        try {
            connection = connectToDB();
            connection.setAutoCommit(false);

            String query1 = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";
            statement = connection.prepareStatement(query1);
            statement.setLong(1, amount);
            statement.setString(2, toAccountNumber);
            if (statement.executeUpdate() != 1) {
                connection.rollback();
                throw new RuntimeException("Could not update the reciver's account");
            }

            String query2 = "UPDATE accounts SET balance = balance - ? WHERE acc_no = (SELECT acc_no FROM cards WHERE card_number = ?)";
            statement = connection.prepareStatement(query2);
            statement.setLong(1, amount);
            statement.setString(2, cardNumber);
            if (statement.executeUpdate() != 1) {
                connection.rollback();
                throw new RuntimeException("Could not update the reciver's account");
            }
        } catch (SQLException | RuntimeException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return true;
    }

    public boolean validateDetails(String cardNumber, Date expDate, int cvv) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectToDB();

            String cardTableQuery = "SELECT card_expire, card_cvv FROM cards WHERE card_number = ?";
            statement = connection.prepareStatement(cardTableQuery);
            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                // No such card in database
                return false;
            }

            // Convert string date to util date for comparison.
            SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
            try {
                Date dbExpDate = format.parse(resultSet.getString("card_expire"));
                if (!dbExpDate.after(expDate)) {
                    System.out.println("Error code-54: Expired card.");
                    return false;
                }
            } catch (ParseException e) {
                System.err.println(e);
                return false;
            }

            if (cvv != resultSet.getInt("card_cvv")) {
                System.out.println("Error code-97: Invalid CVV.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return true;
    }

    public boolean authoriseTransaction(int amount, String cardNumber) {

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectToDB();

            String sqlQuery = "SELECT a.balance, a.limit, c.card_type FROM accounts a INNER JOIN cards c ON a.acc_no = c.acc_no WHERE acc_no = (SELECT acc_no FROM cards WHERE card_number = ?)";
            statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return false;
            }

            long limit = resultSet.getLong("limit");
            long balance = resultSet.getLong("balance");

            if (resultSet.getString("card_type").equalsIgnoreCase("credit"))
                if ((limit - balance) + amount >= limit)
                    return false;

            if (resultSet.getString("card_type").equalsIgnoreCase("debit"))
                if (balance - amount < limit)
                    return false;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return true;
    }

    public static void main(String[] args) {

        // Create an instance of the creditCard class and call the validateCreditCard
        // method
        CardVerification card = new CardVerification();
        card.authenticate("1234567890123456", new Date(), 123, 48999, "1234123412341234");

    }

}
