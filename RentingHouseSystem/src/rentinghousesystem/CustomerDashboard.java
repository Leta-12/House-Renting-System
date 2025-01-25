package rentinghousesystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerDashboard {

    private ObservableList<Property> propertyList = FXCollections.observableArrayList();

    public Scene getScene(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome, Customer!");
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

        // Button to view details of selected property
        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        viewDetailsButton.setOnAction(e -> {
            Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
            if (selectedProperty != null) {
                showPropertyDetails(primaryStage, selectedProperty);
            } else {
                showAlert("Error", "Please select a property to view details.");
            }
        });

        // Load properties
        loadProperties();

        VBox layout = new VBox(10, welcomeLabel, searchField, searchButton, propertyTable, viewDetailsButton);
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
        } catch (SQLException e) {
            showAlert("Error", "Failed to load properties from database. Please try again later.");
        }
    }

    private void filterProperties(String keyword) {
        propertyList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM properties WHERE name LIKE ? OR description LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                propertyList.add(new Property(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to search properties. Please try again later.");
        }
    }

    private void showPropertyDetails(Stage primaryStage, Property property) {
        VBox detailsLayout = new VBox(10);

        Label nameLabel = new Label("Name: " + property.getName());
        Label descriptionLabel = new Label("Description: " + property.getDescription());
        Label priceLabel = new Label("Price: $" + property.getPrice());

        Button inquireButton = new Button("Inquire about Property");
        inquireButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        inquireButton.setOnAction(e -> showAlert("Inquiry", "You have successfully inquired about this property."));

        detailsLayout.getChildren().addAll(nameLabel, descriptionLabel, priceLabel, inquireButton);
        detailsLayout.setAlignment(Pos.CENTER);
        detailsLayout.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 20;");

        primaryStage.setScene(new Scene(detailsLayout, 400, 300));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
