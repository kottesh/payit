package app.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class NetBanking extends Payment{

    private String userName;

    NetBanking(String bankName, String toAccount, double amount) {
        this.mode = "NB";
        this.bankName = bankName;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    public boolean login(String userName, String password) {

        /* 
         * Checks the login credentials.
         * -- username and password.
         */

        Connection connection = null;
        PreparedStatement statement = null;
        Connect newConn = null;

        // String acc_no = getAccountNumber(userName);

        // if (acc_no == null) {
        //     return false;
        // }

        // this.fromAccount = acc_no;

        try {
            newConn = new Connect("orcl");
            connection = newConn.DBconnection();

            String query = "SELECT login_pwd FROM login_credentials WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()){
                return false;
            }

            String dbPassword = resultSet.getString("login_pwd");
            
            if (dbPassword.equals(password)) {
                this.userName = userName;
                return true;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error validating username/password!");
        }

        return false;
    }

    public String getAccountNumber(String userName) {
        Connect newConn = null;
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            newConn = new Connect(null);
            connection = newConn.DBconnection();

            String query = "SELECT acc_no FROM login_credentials WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }
            return resultSet.getString("acc_no");

        } catch(SQLException e) {
            System.err.println(e.getMessage());
            return null;
        } finally {
            try {
                connection.close();
            } catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean validateTransaction(String userName, String transactPWD) {
        Connection connection = null;
        PreparedStatement statement = null;
        Connect newConn = null;

        try {
            newConn = new Connect(null);
            connection = newConn.DBconnection();

            String query = "SELECT transaction_pwd FROM login_credentials WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, userName);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()){
                return false;
            }

            String dbPassword = resultSet.getString("transaction_pwd");
            
            if (dbPassword.equals(transactPWD)) {
                status = true;
                return true;
            } else {
                status = false;
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }
    
    
    public boolean authenticate(String fromAccountNumber, String toAccountNumber, String amount) {
        
        if (!validateAmount(Integer.valueOf(amount))) {
            return false;
        }

        if (!checkBalance(Integer.valueOf(amount))) {
            return false;
        }

        return true;
    }


    // public static void main(String[] args) {
    //     NetBanking netBanking = new NetBanking();
    //     netBanking.login("john.doe", "password123");
    // }
}
