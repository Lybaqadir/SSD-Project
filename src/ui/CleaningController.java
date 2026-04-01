//lyba qadir did this
package ui;

import javafx.fxml.FXML;                // marks @FXML fields and methods
import javafx.scene.control.*;
import service.AuthService;
import service.CleaningService;

public class CleaningController {

    @FXML private TextField        roomIdField;
    @FXML private ComboBox<String> cleaningStatusCombo;
    @FXML private Label            statusLabel;

    private CleaningService cleaningService = new CleaningService();

    // INITIALIZE — runs automatically when screen loads

    @FXML
    public void initialize() {

        // getItems() gives access to list created in combo box.
        //u need to add items in that default list
        cleaningStatusCombo.getItems().addAll("clean", "dirty", "in_progress");

        cleaningStatusCombo.setValue("clean");

        statusLabel.setText("");


        // AuthService.getCurrentRole() returns the role of whoever is logged in.
        // We check it here as a UI-level gate (first security layer).
        String currentRole = AuthService.getCurrentRole();

        if (!"Cleaning Staff".equals(currentRole)) {
            showError("Access Denied. Only Cleaning Staff can update cleaning status.");

        }
    }

    @FXML
    private void handleUpdateStatus() {

        // Clears any previous status message
        statusLabel.setText("");


        // getText() gets whatever is in the text field
        // trim() removes any extra spaces the user may have typed by accident
        String roomIdText = roomIdField.getText().trim();

        if (roomIdText.isEmpty()) {
            showError("Please enter a Room ID.");
            return;

        }

        int roomId;
        try {
            roomId = Integer.parseInt(roomIdText);

        } catch (NumberFormatException e) {
            showError("Room ID must be a number.");
            return;
        }

        // getValue() returns the currently selected item in the ComboBox
        String selectedStatus = cleaningStatusCombo.getValue();

        if (selectedStatus == null || selectedStatus.isEmpty()) {
            showError("Please select a cleaning status.");
            return;
        }

        // AuthService.getCurrentUser() returns the User object of whoever is logged in
        // .getUserId() gets their ID number from that User object
        if (AuthService.getCurrentUser() == null) {
            showError("No user logged in.");
            return;
        }
        int userId = AuthService.getCurrentUser().getUserId();


        boolean success = cleaningService.updateCleaningStatus(roomId, selectedStatus, userId);


        if (success) {
            showSuccess("Room " + roomId + " status updated to: " + selectedStatus);
            handleClear(); // reset the form after a successful update
        } else {
            showError("Update failed. Check the Room ID or your access permissions.");
        }
    }


    /**
     * Handle Clear is Called when the "Clear" button is clicked, or after a successful update.
     * Resets the form to its empty/default state.
     */
    @FXML
    private void handleClear() {
        roomIdField.setText("");
        cleaningStatusCombo.setValue("clean");
        statusLabel.setText("");
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        // #e74c3c is a red hex colour code
        statusLabel.setText("✗  " + message);
    }

    /**
     * Shows a success message in GREEN.
     */
    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        // #27ae60 is a green hex colour code
        statusLabel.setText("✓  " + message);
    }
}