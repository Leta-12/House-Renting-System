package rentinghousesystem;

import javafx.application.Application;
import javafx.stage.Stage;

public class RentingHouseSystem extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Instantiate the LoginScreen to show the login screen initially
        LoginScreen loginScreen = new LoginScreen();

        // Set the scene to the login screen (login screen is the first screen displayed)
        primaryStage.setScene(loginScreen.getScene(primaryStage));

        // Set the stage title
        primaryStage.setTitle("Renting House System - Login");

        // Optionally, prevent the window from being resized
        primaryStage.setResizable(false); 

        // Show the primary stage (window)
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
}
