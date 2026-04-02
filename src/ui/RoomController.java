package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Room;
import service.AuthService;
import service.RoomService;

// Controls the Room Management screen
// Section 1: View/filter all rooms (all roles)
// Section 2: Update room rate (Manager only)
public class RoomController {

    // Table and its columns
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, Integer> colRoomId;
    @FXML private TableColumn<Room, String>  colRoomNumber;
    @FXML private TableColumn<Room, String>  colRoomType;
    @FXML private TableColumn<Room, Double>  colRate;
    @FXML private TableColumn<Room, String>  colStatus;
    @FXML private TableColumn<Room, String>  colCleaning;

    // Filter section
    @FXML private ComboBox<String> statusFilterCombo;

    // Rate update section
    @FXML private TextField rateRoomIdField;
    @FXML private TextField newRateField;
    @FXML private Button    updateRateBtn;

    // Status message
    @FXML private Label statusLabel;

    private RoomService roomService = new RoomService();

    // INITIALIZE — runs automatically when screen loads
    @FXML
    public void initialize() {

        // Connect each column to its matching field in Room.java
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCleaning.setCellValueFactory(new PropertyValueFactory<>("cleaningStatus"));

        // Set up filter dropdown options
        statusFilterCombo.getItems().addAll("All", "available", "occupied", "reserved");
        statusFilterCombo.setValue("All");

        // Disable Update Rate button for non-Managers (UI-level access control)
        String currentRole = AuthService.getCurrentRole();
        if (!"Manager".equals(currentRole)) {
            updateRateBtn.setDisable(true);
        }

        // Load all rooms into the table on startup
        refreshTable(null);
    }

    // Called when "Apply Filter" button is clicked
    @FXML
    private void handleApplyFilter() {
        String selected = statusFilterCombo.getValue();

        if (selected == null || selected.equals("All")) {
            refreshTable(null);
        } else {
            refreshTable(selected);
        }
    }

    // Called when "Show All Rooms" button is clicked
    @FXML
    private void handleShowAll() {
        statusFilterCombo.setValue("All");
        refreshTable(null);
    }

    // Called when "Update Rate" button is clicked
    @FXML
    private void handleUpdateRate() {

        // Validate Room ID
        String roomIdText = rateRoomIdField.getText().trim();
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

        // Validate New Rate
        String rateText = newRateField.getText().trim();
        if (rateText.isEmpty()) {
            showError("Please enter the new rate.");
            return;
        }

        double newRate;
        try {
            newRate = Double.parseDouble(rateText);
        } catch (NumberFormatException e) {
            showError("Rate must be a valid number (e.g. 200.00).");
            return;
        }

        if (newRate <= 0) {
            showError("Rate must be greater than zero.");
            return;
        }

        // Get manager ID for audit log
        int managerId = AuthService.getCurrentUser().getUserId();

        boolean success = roomService.updateRate(roomId, newRate, managerId);

        if (success) {
            showSuccess("Rate updated successfully for Room ID: " + roomId +
                    " → New rate: " + String.format("%.2f", newRate));
            handleClearRate();
            refreshTable(null);
        } else {
            showError("Failed to update rate. Check the Room ID and try again.");
        }
    }

    // Called when "Clear" button is clicked
    @FXML
    private void handleClearRate() {
        rateRoomIdField.setText("");
        newRateField.setText("");
        statusLabel.setText("");
    }

    // Loads rooms from database into the table
    // null = no filter, show all rooms
    private void refreshTable(String statusFilter) {
        java.util.List<Room> roomList;

        if (statusFilter == null) {
            roomList = roomService.getAllRooms();
        } else {
            roomList = roomService.getRoomsByStatus(statusFilter);
        }

        // ObservableList auto-updates the table when data changes
        ObservableList<Room> observableRooms = FXCollections.observableArrayList(roomList);
        roomTable.setItems(observableRooms);
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