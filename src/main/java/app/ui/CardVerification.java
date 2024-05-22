package app.ui;

import java.sql.*;
import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

class ErrorDisplay {
    public static void showErrorDialog(String errorMessage, String title) {
        // Create an error message dialog
        JOptionPane.showMessageDialog(null, errorMessage, title, JOptionPane.ERROR_MESSAGE);
    }
}

public class CardVerification extends Payment {
    
    private String cardNumber;

    CardVerification(String toAccount, double amount) {
        this.toAccount = toAccount;
        this.amount = amount;
    }
    
    public String getAccountNumber(String cardNumber) {
        String accountNumber = null;
        Connect newConn = null;
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            newConn = new Connect("orcl");
            connection = newConn.DBconnection();

            String query = "SELECT acc_no FROM cards WHERE card_number = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                ErrorDisplay.showErrorDialog("Card not found!", "Error");
                return null;
            }
            accountNumber = resultSet.getString("acc_no");
            if (accountNumber != null){
                this.fromAccount = accountNumber;
                return accountNumber;
            }
            else
                return null;

        } catch(Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private void getBankName() {
        Connection connection = null;
        PreparedStatement statement = null;
        Connect newConn = null;
        
        try {
            newConn = new Connect(null);
            connection = newConn.DBconnection();

            String query = "SELECT bank_name FROM banks WHERE bank_id = (SELECT bank_id FROM accounts WHERE acc_no = ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, this.fromAccount);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.bankName = resultSet.getString(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (newConn != null)
                    newConn.closeConn();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean validateDetails(String cardNumber, LocalDate expDate, int cvv) {

        Connection connection = null;
        PreparedStatement statement = null;
        Connect newConn = null;

        try {
            newConn = new Connect("orcl");
            connection = newConn.DBconnection();

            String cardTableQuery = "SELECT card_expiry, card_cvv, card_type FROM cards WHERE card_number = ?";
            statement = connection.prepareStatement(cardTableQuery);
            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                ErrorDisplay.showErrorDialog("Card not found!", "Error");
                return false;
            }

            String cardType = resultSet.getString("card_type");
            if (cardType.equalsIgnoreCase("Credit")) {
                this.mode = "CC";
            } else {
                this.mode = "DC";
            }

            LocalDate dbExpDate = resultSet.getDate("card_expiry").toLocalDate();
            if (!dbExpDate.isEqual(expDate)) {
                System.out.println();
                return false;
            }

            if (LocalDate.now().compareTo(expDate) >= 0) {
                ErrorDisplay.showErrorDialog("Expired card.", "Error code-54");
                return false;
            }

            if (cvv != resultSet.getInt("card_cvv")) {
                ErrorDisplay.showErrorDialog("Invalid CVV.", "Error code-97");
                return false;
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (newConn != null)
                    newConn.closeConn();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        getBankName();
        this.cardNumber = cardNumber;
        return true;
    }

    public String getName(String cardNum) {
        String name = "";
        fromAccount = getAccountNumber(cardNum);
        if (this.toAccount.equals(this.fromAccount)) {
            ErrorDisplay.showErrorDialog("Cannot transfer to the same account.", "Error code-98"); 
            return null;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        Connect newConn = null;
        
        try {
            newConn = new Connect(null);
            connection = newConn.DBconnection();

            String query = "SELECT first_name, last_name FROM customers WHERE customer_id = (SELECT customer_id FROM accounts WHERE acc_no = ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, fromAccount);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                name = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (newConn != null)
                    newConn.closeConn();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println( );
        return name;

    }

    public boolean makeTransaction() { 
        this.status = true;
        return transferAmount(amount, toAccount, fromAccount);
    }

}
