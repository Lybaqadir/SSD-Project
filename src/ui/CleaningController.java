package ui;
//lyba qadir did this
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Room;
import service.AuthService;
import service.CleaningService;
import service.RoomService;
import java.util.List;

public class CleaningController {

    @FXML private TextField        roomIdField;
    @FXML private ComboBox<String> cleaningStatusCombo;
    @FXML private Label            statusLabel;
    @FXML private ListView<String> dirtyRoomsList;

    private CleaningService cleaningService = new CleaningService();
    private RoomService     roomService     = new RoomService();

    @FXML
    public void initialize() {
        // "dirty" removed — only Receptionist can mark a room dirty
        cleaningStatusCombo.getItems().addAll("clean", "in_progress");
        cleaningStatusCombo.setValue("clean");
        statusLabel.setText("");

        if (!"Cleaning Staff".equals(AuthService.getCurrentRole())) {
            showError("Access Denied. Only Cleaning Staff can update cleaning status.");
        }

        loadDirtyRooms();
    }

    // ── Navigation ──────────────────────────────────────────
    @FXML private void goToCleaning() { /* already here */ }

    @FXML
    private void handleLogout() {
        new AuthService().logout();
        NavigationHelper.navigateTo(roomIdField, "/ui/LoginView.fxml");
    }

    // ── Actions ─────────────────────────────────────────────
    @FXML
    private void handleUpdateStatus() {
        statusLabel.setText("");
        String roomIdText = roomIdField.getText().trim();
        if (roomIdText.isEmpty()) { showError("Please enter a Room ID."); return; }

        int roomId;
        try { roomId = Integer.parseInt(roomIdText); }
        catch (NumberFormatException e) { showError("Room ID must be a number."); return; }

        String selectedStatus = cleaningStatusCombo.getValue();
        if (selectedStatus == null || selectedStatus.isEmpty()) {
            showError("Please select a cleaning status.");
            return;
        }
        if (AuthService.getCurrentUser() == null) { showError("No user logged in."); return; }

        int userId = AuthService.getCurrentUser().getUserId();
        boolean success = cleaningService.updateCleaningStatus(roomId, selectedStatus, userId);
        if (success) {
            showSuccess("Room " + roomId + " status updated to: " + selectedStatus);
            handleClear();
            loadDirtyRooms(); // refresh the dirty rooms list after update
        } else {
            showError("Update failed. Check the Room ID or your access permissions.");
        }
    }

    @FXML
    private void handleClear() {
        roomIdField.setText("");
        cleaningStatusCombo.setValue("clean");
        statusLabel.setText("");
    }

    // ── Helpers ──────────────────────────────────────────────

    // Loads all rooms with cleaningStatus = "dirty" into the list
    // so the cleaner knows which rooms need attention
    private void loadDirtyRooms() {
        dirtyRoomsList.getItems().clear();
        List<Room> allRooms = roomService.getAllRooms();
        for (Room r : allRooms) {
            if ("dirty".equals(r.getCleaningStatus())) {
                dirtyRoomsList.getItems().add(
                        "Room " + r.getRoomNumber() + " (ID: " + r.getRoomId() + ") — " + r.getRoomType()
                );
            }
        }
        if (dirtyRoomsList.getItems().isEmpty()) {
            dirtyRoomsList.getItems().add("No dirty rooms at the moment.");
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        statusLabel.setText("✗  " + message);
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        statusLabel.setText("✓  " + message);
    }
}