package rentinghousesystem;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/renting_house"; // Update with your DB name
            String user = "root"; // Replace with your username
            String password = ""; // Replace with your password
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}