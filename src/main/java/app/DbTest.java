package app;

import java.sql.*;

// this is a test code. not realted to project.
public class DbTest {
    public static void main (String[] args) {
        final String jdbc_url = "jdbc:oracle:thin:@localhost:1521:FREE";
        final String user_name = "system";
        final String pwd = "sk";

        final String jdbc_driver = "oracle.jdbc.driver.OracleDriver";

        String sql_query = "SELECT * FROM GLOBAL_NAME ";

        try {
            Class.forName(jdbc_driver);
            Connection con = DriverManager.getConnection(jdbc_url, user_name, pwd); 
            // System.out.println(con);
            Statement stmnt = con.createStatement();
            ResultSet rst = stmnt.executeQuery(sql_query);
            rst.next();
            System.out.println(rst.getString(1));

            con.close();
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }    
}
