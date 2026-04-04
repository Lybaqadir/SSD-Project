package ui;
//lyba

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

// Shared navigation helper used by all controllers
public class NavigationHelper {

    public static void navigateTo(Node sourceNode, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            System.err.println("Navigation error to " + fxmlPath + ": " + e.getMessage());
        }
    }
}