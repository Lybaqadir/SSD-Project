import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Entry point for the Hotel Management System
// Launches the JavaFX application and shows the login screen first
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the login screen FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
