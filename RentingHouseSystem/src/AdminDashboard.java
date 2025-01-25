import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class AdminDashboard {

    public Scene getScene() {
        StackPane root = new StackPane();
        Label label = new Label("Welcome to the Admin Dashboard");
        root.getChildren().add(label);

        return new Scene(root, 600, 400);
    }
}
