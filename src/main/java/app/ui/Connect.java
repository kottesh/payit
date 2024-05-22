package app.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    private String URL;
    private String USR_NAME;
    private String PWD;
    private String JDBC_DRIVER;

    private Connection connection;

    public Connect(String dbName) {

        URL = "jdbc:oracle:thin:@localhost:1521/skibi";
        USR_NAME = "system";
        PWD = "LaP62Ogm";
        JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

        connection = null;
    }

    public Connection DBconnection() {

        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(URL, USR_NAME, PWD);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getMessage());
        }

        return connection;
    }

    public void closeConn() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error connecting DB.");
            } 
        }
    }

    public static void main(String[] args) {
        Connect con = new Connect(null);
        Connection conn = con.DBconnection();
        System.out.println(conn);
    }
}
