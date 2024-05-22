package app.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    // creates and returns a database connection
    public static Connection connect() {
        final String url = "jdbc:oracle:thin:@localhost:1521/skibi";
        final String user_name = "system";
        final String pwd = "LaP62Ogm";
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

    // public static void main(String[] args) {
    //     if (connect() != null) {
    //         System.out.println("OK!");
    //     } else {
    //         System.err.println("Can't connect to DB.");
    //     }
    // }
}
