package app.ui;

import java.sql.*;

import app.payment.TransactionLog;

class Payment {

    private enum statusStr {
        TRUE("SUCCESS"),
        FALSE("FAILED");

        private String value;

        private statusStr(String value) {
            this.value = value;
        }
    }

    private final long TRANSACTION_LIMIT = 100000;

    protected String bankName;
    protected String toAccount;
    protected double amount;
    protected String fromAccount;
    protected boolean status;
    protected String mode;

    protected boolean validateAmount(double amount) {
        if (amount >= TRANSACTION_LIMIT) {
            return false;
        }

        return true;
    }

    public boolean checkBalance(double amount) {

        Connection connection = null;
        PreparedStatement statement = null;
        Connect newConn = null;

        try {
            newConn = new Connect("orcl");
            connection = newConn.DBconnection();

            String sqlQuery = "SELECT a.balance, a.min_bal, c.card_type FROM accounts a INNER JOIN cards c ON c.acc_no = a.acc_no WHERE a.acc_no = ?";
            statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, fromAccount);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return false;
            }

            double limit = resultSet.getDouble("min_bal");
            double balance = resultSet.getDouble("balance");

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
                if (newConn != null)
                    newConn.closeConn();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return true;
    }

    protected boolean transferAmount(double amount, String toAccountNumber, String fromAccountNumber) {

        PreparedStatement statement = null;
        Connection connection = null;
        Connect newConn = null;

        try {
            newConn = new Connect("orcl");
            connection = newConn.DBconnection();
            connection.setAutoCommit(false);

            String query1 = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";
            statement = connection.prepareStatement(query1);
            statement.setDouble(1, amount);
            statement.setString(2, toAccountNumber);
            System.out.print(statement.executeUpdate());

            String query2 = "UPDATE accounts SET balance = balance - ? WHERE acc_no = ?";
            statement = connection.prepareStatement(query2);
            statement.setDouble(1, amount);
            statement.setString(2, fromAccountNumber);
            statement.executeUpdate();

            if (makeTransactionLog("Credit")) {
                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction.");
                }
            }
            System.err.println("Error executing transaction." + e.getMessage());
            return false;
        } finally {
            try {
                if (statement != null)
                    statement.close();
                if (newConn != null)
                    newConn.closeConn();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
    }

    protected boolean makeTransactionLog(String type) {
        String statusString = statusStr.valueOf((String.valueOf(status)).toUpperCase()).value;

        System.out.println(bankName);
        System.out.println(fromAccount);
        System.out.println(toAccount);
        System.out.println(amount);
        System.out.println(mode);
        System.out.println(type);
        System.out.println(statusString);

        return new TransactionLog().updateTransactionLog(bankName, fromAccount, toAccount, amount, type, mode, statusString);
    }

}
