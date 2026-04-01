package ui;
//nora did this
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.CheckInOutService;

// Controls the Check-In / Check-Out screen
// Receptionist enters a booking ID and clicks Check-In or Check-Out
public class CheckInOutController {

    @FXML
    private TextField bookingIdField;

    @FXML
    private Label messageLabel;

    private CheckInOutService checkInOutService = new CheckInOutService();

    // Called when Check-In button is clicked
    @FXML
    private void handleCheckIn() {
        String idText = bookingIdField.getText();

        if (idText.isEmpty()) {
            showError("Please enter a booking ID.");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showError("Booking ID must be a number.");
            return;
        }

        boolean success = checkInOutService.checkIn(bookingId);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Guest checked in successfully.");
        } else {
            showError("Check-in failed. Please verify the booking ID.");
        }
    }

    // Called when Check-Out button is clicked
    @FXML
    private void handleCheckOut() {
        String idText = bookingIdField.getText();

        if (idText.isEmpty()) {
            showError("Please enter a booking ID.");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showError("Booking ID must be a number.");
            return;
        }

        boolean success = checkInOutService.checkOut(bookingId);

        if (success) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Guest checked out successfully.");
        } else {
            showError("Check-out failed. Please verify the booking ID.");
        }
    }

    // Helper to show red error messages
    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }
}
