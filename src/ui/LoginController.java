package ui;
//nora did this
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.AuthService;

// Controls the Login screen
// Calls AuthService to verify credentials when Login button is clicked
// Navigates to the correct screen based on the user's role after login
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         messageLabel;

    private AuthService authService = new AuthService();

    // Called when the Login button is clicked
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check that fields are not empty before trying to login
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        boolean success = authService.login(username, password);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Login successful! Welcome " + AuthService.getCurrentUser().getFirstName());
            loadScreenForRole(AuthService.getCurrentRole());
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Invalid username or password.");
        }
    }

    // Loads the correct screen based on the logged-in user's role
    private void loadScreenForRole(String role) {
        String fxmlFile;

        switch (role) {
            case "Manager":
                fxmlFile = "/ui/StaffView.fxml";
                break;
            case "Receptionist":
                fxmlFile = "/ui/BookingView.fxml";
                break;
            case "Cleaning Staff":
                fxmlFile = "/ui/CleaningView.fxml";
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Unknown role. Contact your administrator.");
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());

            // Gets the current window and switches the scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);

        } catch (Exception e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error loading screen: " + e.getMessage());
            System.err.println("Screen load error: " + e.getMessage());
        }
    }
}