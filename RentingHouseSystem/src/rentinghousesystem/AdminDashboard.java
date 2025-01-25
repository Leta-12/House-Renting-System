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

public class AdminDashboard {

    private final ObservableList<Property> propertyList = FXCollections.observableArrayList();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    public Scene getScene(Stage primaryStage) {
        Label welcomeLabel = new Label("Welcome, Admin!");
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
                searchPropertiesAndUsers(keyword);
            }
        });

        // Tabs for managing properties and users
        TabPane tabPane = new TabPane();

        // Tab for managing properties
        Tab propertiesTab = new Tab("Manage Properties");
        propertiesTab.setClosable(false);

        TableView<Property> propertyTable = createPropertyTable();
        Button approveButton = new Button("Approve Property");
        approveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        approveButton.setOnAction(e -> approveProperty(propertyTable));

        Button deleteButton = new Button("Delete Property");
        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteProperty(propertyTable));

        VBox propertyLayout = new VBox(10, propertyTable, approveButton, deleteButton);
        propertyLayout.setAlignment(Pos.CENTER);
        propertiesTab.setContent(propertyLayout);

        // Tab for managing users
        Tab usersTab = new Tab("Manage Users");
        usersTab.setClosable(false);

        TableView<User> userTable = createUserTable();
        Button addUserButton = new Button("Add User");
        addUserButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        addUserButton.setOnAction(e -> openAddUserScreen(primaryStage));

        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteUserButton.setOnAction(e -> deleteUser(userTable));

        VBox userLayout = new VBox(10, userTable, addUserButton, deleteUserButton);
        userLayout.setAlignment(Pos.CENTER);
        usersTab.setContent(userLayout);

        tabPane.getTabs().addAll(propertiesTab, usersTab);

        VBox layout = new VBox(10, welcomeLabel, searchField, searchButton, tabPane);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 20;");

        return new Scene(layout, 800, 600);
    }

    private TableView<Property> createPropertyTable() {
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

        return propertyTable;
    }

    private TableView<User> createUserTable() {
        TableView<User> userTable = new TableView<>();

        TableColumn<User, Integer> userIdColumn = new TableColumn<>("ID");
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> userRoleColumn = new TableColumn<>("Role");
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        userTable.getColumns().addAll(userIdColumn, userNameColumn, userRoleColumn);
        userTable.setItems(userList);

        return userTable;
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
            e.printStackTrace();
        }
    }

private void loadUsers() {
    userList.clear();
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT * FROM users";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            userList.add(new User(
                rs.getInt("id"),           // User ID
                rs.getString("username"),  // Username
                rs.getString("role"),      // Role
                rs.getString("email")      // Email (Adjust based on your schema)
            ) {
                @Override
                public void performRoleSpecificAction() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    private void approveProperty(TableView<Property> propertyTable) {
        Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
        if (selectedProperty != null) {
            showAlert("Success", "Property approved successfully.");
            loadProperties();
        } else {
            showAlert("Error", "Please select a property to approve.");
        }
    }

    private void deleteProperty(TableView<Property> propertyTable) {
        Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
        if (selectedProperty != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM properties WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selectedProperty.getId());
                stmt.executeUpdate();
                showAlert("Success", "Property deleted successfully.");
                loadProperties();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete property.");
            }
        } else {
            showAlert("Error", "Please select a property to delete.");
        }
    }

    private void deleteUser(TableView<User> userTable) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM users WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, selectedUser.getId());
                stmt.executeUpdate();
                showAlert("Success", "User deleted successfully.");
                loadUsers();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to delete user.");
            }
        } else {
            showAlert("Error", "Please select a user to delete.");
        }
    }

    private void openAddUserScreen(Stage primaryStage) {
        // Placeholder for "Add User" functionality.
        showAlert("Info", "Add User functionality not implemented yet.");
    }

    private void searchPropertiesAndUsers(String keyword) {
        // Placeholder for search functionality.
        showAlert("Info", "Search functionality not implemented yet.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
