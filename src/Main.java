import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Entry point for the Hotel Management System
// Launches JavaFX and loads the Login screen first
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the login screen first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}