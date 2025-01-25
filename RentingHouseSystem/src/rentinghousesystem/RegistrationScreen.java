package rentinghousesystem;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationScreen {

    public Scene getScene(Stage primaryStage) {
        // Labels and Input Fields
        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        Label roleLabel = new Label("Role:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Customer", "Owner");
        roleComboBox.setPromptText("Select your role");

        // Register Button
        Button registerButton = new Button("Register");
        Label feedbackLabel = new Label(); // For success or error messages
        feedbackLabel.setStyle("-fx-text-fill: red;");

        registerButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleComboBox.getValue();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
                feedbackLabel.setText("All fields are required!");
            } else if (registerUser(name, email, password, role)) {
                feedbackLabel.setStyle("-fx-text-fill: green;");
                feedbackLabel.setText("Registration successful!");
                // Clear fields after successful registration
                nameField.clear();
                emailField.clear();
                passwordField.clear();
                roleComboBox.setValue(null);
            } else {
                feedbackLabel.setText("Registration failed. Try again.");
            }
        });

        // Layout
        VBox layout = new VBox(10, nameLabel, nameField, emailLabel, emailField,
                passwordLabel, passwordField, roleLabel, roleComboBox,
                registerButton, feedbackLabel);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, 400, 400);
    }

    private boolean registerUser(String name, String email, String password, String role) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Database connection failed.");
                return false;
            }

            String query;
            if (role.equalsIgnoreCase("owner")) {
                query = "INSERT INTO owners (name, email, password) VALUES (?, ?, ?)";
            } else {
                query = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, password);
                if (!role.equalsIgnoreCase("owner")) {
                    stmt.setString(4, role);
                }
                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                System.out.println("Email already exists.");
                return false;
            }
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}