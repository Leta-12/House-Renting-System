import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class CustomerDashboard {

    public Scene getScene() {
        StackPane root = new StackPane();
        Label label = new Label("Welcome to the Customer Dashboard");
        root.getChildren().add(label);

        return new Scene(root, 600, 400);
    }
}
