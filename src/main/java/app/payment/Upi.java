package app.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import app.utils.DB;

public class Upi {
    // returns true if the vpa is in format
    // and record is in the database  
    public static boolean validateVPA(String VPA) {
        // validates if the VPA exists in the database.
        String sql = "SELECT COUNT(*) FROM upi_service WHERE upi_vpa = ?";
        
        try (Connection conn = DB.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, VPA);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                if (!(rs.getInt(1) > 0)) {
                    JOptionPane.showMessageDialog(null, "VPA not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // returns true if the pin matches otherwise returns false 
    public static boolean validatePIN(String VPA, String PIN) {
        if (PIN.length() != 4) {
            JOptionPane.showMessageDialog(null, "Fill in 4-digit PIN", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "SELECT upi_pin FROM upi_service WHERE upi_vpa = ?";
        
        try (Connection conn = DB.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, VPA);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                if (rs.getInt(1) == Integer.valueOf(PIN)) {// if the pin matches returns true else return false.
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect PIN", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // checks if the amount is valid and has the account has sufficient balance
    // if so then returns true otherwise returns false
    public static boolean validateAmount(String VPA, double amount) {
        // validate if the account has sufficient balance for the transfer
        String sql = "SELECT balance, min_bal FROM accounts WHERE acc_no = (SELECT acc_no FROM upi_service WHERE upi_vpa = ?)";
        
        try (Connection conn = DB.connect();
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
            JOptionPane.showMessageDialog(null, "REASON: Insufficient Funds", "Information", JOptionPane.INFORMATION_MESSAGE);
            return false; // otherwise cannot transfer the amount.
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // retruns true if the transfer is complete otherwise returns false
    public static boolean transferAmount(String fromVPA, String accountNo, double amount) {
        // perform the amount transfer between two accounts
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DB.connect();
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
            String sqlAdd = "UPDATE accounts SET balance = balance + ? WHERE acc_no = ?";
            pstmt = conn.prepareStatement(sqlAdd);
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountNo);
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
        transferAmount("kottesh@upi", "9313752277", 100);
    }
}
