package rentinghousesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OwnerDashboard {

    private ObservableList<Property> propertyList = FXCollections.observableArrayList();

    public Scene getScene(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome, Owner!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setPrefWidth(200);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        searchButton.setOnAction(e -> {
            String keyword = searchField.getText().trim();
            if (!keyword.isEmpty()) {
                filterProperties(keyword);
            }
        });

        // TableView to display properties
        TableView<Property> propertyTable = new TableView<>();
        TableColumn<Property, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Property, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Property, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Property, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        propertyTable.getColumns().addAll(idColumn, nameColumn, descriptionColumn, priceColumn);
        propertyTable.setItems(propertyList);

        // Buttons for property management
        Button addPropertyButton = new Button("Add Property");
        addPropertyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        addPropertyButton.setOnAction(e -> openAddPropertyScreen(primaryStage));

        Button editPropertyButton = new Button("Edit Property");
        editPropertyButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        editPropertyButton.setOnAction(e -> {
            Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
            if (selectedProperty != null) {
                openEditPropertyScreen(primaryStage, selectedProperty);
            } else {
                showAlert("Error", "Please select a property to edit.");
            }
        });

        Button deletePropertyButton = new Button("Delete Property");
        deletePropertyButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deletePropertyButton.setOnAction(e -> {
            Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
            if (selectedProperty != null) {
                deleteProperty(selectedProperty);
                loadProperties(); // Refresh the table
            } else {
                showAlert("Error", "Please select a property to delete.");
            }
        });

        // Load properties
        loadProperties();

        VBox layout = new VBox(10, welcomeLabel, searchField, searchButton, propertyTable, addPropertyButton, editPropertyButton, deletePropertyButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 20;");

        return new Scene(layout, 600, 400);
    }

    private void loadProperties() {
        propertyList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM properties";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                propertyList.add(new Property(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterProperties(String keyword) {
        propertyList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM properties WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                propertyList.add(new Property(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAddPropertyScreen(Stage primaryStage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Property Name");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Property Description");
        descriptionField.setPrefHeight(100);

        TextField priceField = new TextField();
        priceField.setPromptText("Property Price");

        Button submitButton = new Button("Add Property");
        submitButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String price = priceField.getText();
            addPropertyToDatabase(name, description, price);

            nameField.clear();
            descriptionField.clear();
            priceField.clear();

            primaryStage.setScene(getScene(primaryStage));
        });

        VBox layout = new VBox(10, nameField, descriptionField, priceField, submitButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 20;");

        primaryStage.setScene(new Scene(layout, 400, 300));
    }

    private void deleteProperty(Property property) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM properties WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, property.getId());
            stmt.executeUpdate();
            showAlert("Success", "Property deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete property.");
        }
    }

    private void addPropertyToDatabase(String name, String description, String price) {
        if (name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            showAlert("Error", "All fields must be filled out.");
            return;
        }

        try {
            double parsedPrice = Double.parseDouble(price);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO properties (name, description, price) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setDouble(3, parsedPrice);
                stmt.executeUpdate();
                showAlert("Success", "Property added successfully.");
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid price.");
        } catch (SQLException e) {
            showAlert("Error", "Database error occurred while adding property.");
            Logger.getLogger(OwnerDashboard.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openEditPropertyScreen(Stage primaryStage, Property selectedProperty) {
        // Implementation to edit property if required
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
