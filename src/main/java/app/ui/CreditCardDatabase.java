import java.sql.*;

public class CreditCardDatabase {
    private Connection connection;
    private final String url = "jdbc:oracle:thin:@localhost:1521:xe"; // JDBC URL for Oracle database
    private final String username = "anuz004";
    private final String password = "2491";

    public CreditCardDatabase() {
        try {
            // Load the Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Establish the database connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to Oracle database.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertCreditCardUser(String name, String cardNumber, String expiry, String cvv, String email) {
        String sql = "INSERT INTO credit_card_users (name, card_number, expiry, cvv, email) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, cardNumber);
            statement.setString(3, expiry);
            statement.setString(4, cvv);
            statement.setString(5, email);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User data inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CreditCardDatabase database = new CreditCardDatabase();
        // Example of inserting credit card user data
        database.insertCreditCardUser("John Doe", "1234567890123456", "12/2025", "123", "john.doe@example.com");
        database.closeConnection();
    }
}
