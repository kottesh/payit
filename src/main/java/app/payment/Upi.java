package app.payment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class Upi {
    private Connection connect() {
        // creates and returns a database connection
        final String url = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
        final String user_name = "system";
        final String pwd = "sk";
        final String  jdbc_driver = "oracle.jdbc.driver.OracleDriver";

        Connection conn = null;

        try {
            Class.forName(jdbc_driver);
            conn = DriverManager.getConnection(url, user_name, pwd);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    // returns true if the vpa is in format
    // and record is in the database  
    public boolean validateVPA(String VPA) {
        // regex to validate the VPA format (e.g., user@upi)
        String regex = "^[\\w.-]+@[\\w.-]+$";
        Pattern pattern = Pattern.compile(regex);
        
        // checks if the VPA matches the regex pattern
        if (!pattern.matcher(VPA).matches()) {
            System.err.println("Invalid VPA format.");
            return false;
        }

        // validates if the VPA exists in the database.
        String sql = "SELECT COUNT(*) FROM upi_service WHERE upi_vpa = ?";
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, VPA);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // returns true if the pin matches otherwise returns false 
    public boolean validatePIN(String VPA, int PIN) {
        String sql = "SELECT upi_pin FROM upi_service WHERE upi_vpa = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, VPA);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == PIN; // if the pin matches returns true else return false.
            } else {
                System.err.println("Incorrect PIN");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // checks if the amount is valid and has the account has sufficient balance
    // if so then returns true otherwise returns false
    public boolean validateAmount(String VPA, double amount) {
        if (amount <= 0) {
            System.err.println("Invalid Amount: " + amount);
            return false; 
        }
        // validate if the account has sufficient balance for the transfer
        String sql = "SELECT balance, min_bal FROM accounts WHERE acc_no = (SELECT acc_no FROM upi_service WHERE upi_vpa = ?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, VPA);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                double min_bal = rs.getDouble("min_bal");
                // after sub the balance from the amount the result is greater than the min_bal
                // then can transfer the fund other can't
                if ((balance - amount) >= min_bal) {
                    return true; // if amount > balance return true; can't intiate transfer
                }
            }
            System.err.println("Insuffcient Funds");
            return false; // otherwise cannot transfer the amount.
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // retruns true if the transfer is complete otherwise returns false
    public boolean transferAmount(String fromVPA, String toVPA, double amount) {
        // perform the amount transfer between two accounts
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = this.connect();
            conn.setAutoCommit(false); // start transaction
            // check if the sender has enough balance
            if (!validateAmount(fromVPA, amount)) {
                    return false; // not enough balance
            }
            
            // deduct amount from sender's account
            String sqlDeduct = "UPDATE accounts SET balance = balance - ? WHERE acc_no = (SELECT acc_no FROM upi_service WHERE upi_vpa = ?)";
            pstmt = conn.prepareStatement(sqlDeduct);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, fromVPA);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows != 1) {
                conn.rollback();
                return false; // update failed
            }
            
            // add amount to receiver's account
            String sqlAdd = "UPDATE accounts SET balance = balance + ? WHERE acc_no = (SELECT acc_no FROM upi_service WHERE upi_vpa = ?)";
            pstmt = conn.prepareStatement(sqlAdd);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, toVPA);
            affectedRows = pstmt.executeUpdate();
            if (affectedRows != 1) {
                conn.rollback();
                return false; // update failed
            }

            conn.commit(); // commit transaction
            return true; // transfer successful
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // rollback transaction on error
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            System.out.println(e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Upi upi = new Upi();
        System.out.println(upi.validateVPA("jane.smith@upi"));
        System.out.println(upi.validatePIN("john.doe@upi", 1111));
        System.out.println(upi.validateAmount("john.doe@upi", 5000));
        System.out.println(upi.validateAmount("john.doe@upi", 50000));
        System.out.println(upi.transferAmount("jane.smith@upi", "john.doe@upi", 10));
    }
}
