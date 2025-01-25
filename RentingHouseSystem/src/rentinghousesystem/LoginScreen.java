package rentinghousesystem;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.geometry.Pos;

public class LoginScreen {

    public Scene getScene(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome to House Renting System");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Label errorMessage = new Label();
        errorMessage.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin(primaryStage, emailField, passwordField, errorMessage));

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            RegistrationScreen registrationScreen = new RegistrationScreen();
            primaryStage.setScene(registrationScreen.getScene(primaryStage));
        });

        VBox layout = new VBox(10, welcomeLabel, emailLabel, emailField, passwordLabel, passwordField, loginButton, registerButton, errorMessage);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 20;");

        return new Scene(layout, 400, 300);
    }

    private void handleLogin(Stage primaryStage, TextField emailField, PasswordField passwordField, Label errorMessage) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Email and password cannot be empty.");
            return;
        }

        String role = validateLogin(email, password);
        if (role != null) {
            navigateToDashboard(primaryStage, role);
        } else {
            errorMessage.setText("Invalid email or password. Please try again.");
        }
    }

    private String validateLogin(String email, String password) {
        String role = null;
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                throw new IllegalStateException("Database connection failed.");
            }

            // Check in owners table
            String query = "SELECT 'owner' AS role, password FROM owners WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword != null && storedPassword.equals(password)) {
                        role = "owner";
                        return role;
                    }
                }
            }

            // Check in users table
            query = "SELECT role, password FROM users WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword != null && storedPassword.equals(password)) {
                        role = rs.getString("role");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    private void navigateToDashboard(Stage primaryStage, String role) {
        Scene dashboardScene;
        switch (role.toLowerCase()) {
            case "admin":
                dashboardScene = new AdminDashboard().getScene(primaryStage);
                break;
            case "customer":
                dashboardScene = new CustomerDashboard().getScene(primaryStage);
                break;
            case "owner":
                dashboardScene = new OwnerDashboard().getScene(primaryStage);
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
        primaryStage.setScene(dashboardScene);
    }
}
