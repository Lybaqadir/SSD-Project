package ui;
//lybaQadir did this
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Room;
import service.AuthService;
import service.RoomService;

public class RoomController {

    @FXML private TableView<Room>            roomTable;
    @FXML private TableColumn<Room, Integer> colRoomId;
    @FXML private TableColumn<Room, String>  colRoomNumber;
    @FXML private TableColumn<Room, String>  colRoomType;
    @FXML private TableColumn<Room, Double>  colRate;
    @FXML private TableColumn<Room, String>  colStatus;
    @FXML private TableColumn<Room, String>  colCleaning;
    @FXML private ComboBox<String>           statusFilterCombo;
    @FXML private TextField                  rateRoomIdField;
    @FXML private TextField                  newRateField;
    @FXML private Button                     updateRateBtn;
    @FXML private Label                      statusLabel;

    // Nav buttons hidden based on role
    @FXML private Button navStaff;          // Manager only
    @FXML private Button navReviewRecords;  // Manager only
    @FXML private Button navBookings;       // Receptionist only
    @FXML private Button navCheckInOut;     // Receptionist only
    @FXML private Button navPayment;        // Receptionist only

    private RoomService roomService = new RoomService();

    @FXML
    public void initialize() {
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colCleaning.setCellValueFactory(new PropertyValueFactory<>("cleaningStatus"));

        statusFilterCombo.getItems().addAll("All", "available", "occupied", "reserved");
        statusFilterCombo.setValue("All");

        String role = AuthService.getCurrentRole();

        if ("Manager".equals(role)) {
            // Manager sees: Staff Accounts, Review Records, Rooms
            hideButton(navBookings);
            hideButton(navCheckInOut);
            hideButton(navPayment);
            // Rate update enabled for Manager
        } else if ("Receptionist".equals(role)) {
            // Receptionist sees: Bookings, Check-In/Out, Payment, Rooms
            hideButton(navStaff);
            hideButton(navReviewRecords);
            // Rate update disabled for Receptionist
            updateRateBtn.setDisable(true);
        } else {
            hideButton(navStaff);
            hideButton(navReviewRecords);
            hideButton(navBookings);
            hideButton(navCheckInOut);
            hideButton(navPayment);
            updateRateBtn.setDisable(true);
        }

        refreshTable(null);
    }

    //Navigation
    @FXML private void goToStaff()         { NavigationHelper.navigateTo(rateRoomIdField, "/ui/StaffView.fxml"); }
    @FXML private void goToReviewRecords() { NavigationHelper.navigateTo(rateRoomIdField, "/ui/ReviewRecordsView.fxml"); }
    @FXML private void goToRooms()         { /* already here */ }
    @FXML private void goToBooking()       { NavigationHelper.navigateTo(rateRoomIdField, "/ui/BookingView.fxml"); }
    @FXML private void goToCheckInOut()    { NavigationHelper.navigateTo(rateRoomIdField, "/ui/CheckInOutView.fxml"); }
    @FXML private void goToPayment()       { NavigationHelper.navigateTo(rateRoomIdField, "/ui/PaymentView.fxml"); }

    @FXML
    private void handleLogout() {
        new AuthService().logout();
        NavigationHelper.navigateTo(rateRoomIdField, "/ui/LoginView.fxml");
    }

    //Actions
    @FXML
    private void handleApplyFilter() {
        String selected = statusFilterCombo.getValue();
        refreshTable((selected == null || selected.equals("All")) ? null : selected);
    }

    @FXML
    private void handleShowAll() {
        statusFilterCombo.setValue("All");
        refreshTable(null);
    }

    @FXML
    private void handleUpdateRate() {
        String roomIdText = rateRoomIdField.getText().trim();
        if (roomIdText.isEmpty()) { showError("Please enter a Room ID."); return; }
        int roomId;
        try { roomId = Integer.parseInt(roomIdText); }
        catch (NumberFormatException e) { showError("Room ID must be a number."); return; }

        String rateText = newRateField.getText().trim();
        if (rateText.isEmpty()) { showError("Please enter the new rate."); return; }
        double newRate;
        try { newRate = Double.parseDouble(rateText); }
        catch (NumberFormatException e) { showError("Rate must be a valid number."); return; }

        if (newRate <= 0) { showError("Rate must be greater than zero."); return; }

        int managerId = AuthService.getCurrentUser().getUserId();
        boolean success = roomService.updateRate(roomId, newRate, managerId);
        if (success) {
            showSuccess("Rate updated for Room " + roomId + " → " + String.format("%.2f", newRate));
            handleClearRate();
            refreshTable(null);
        } else {
            showError("Failed to update rate.");
        }
    }

    @FXML
    private void handleClearRate() {
        rateRoomIdField.setText("");
        newRateField.setText("");
        statusLabel.setText("");
    }

    // ── Helpers ──────────────────────────────────────────────
    private void hideButton(Button btn) {
        btn.setVisible(false);
        btn.setManaged(false);
    }

    private void refreshTable(String statusFilter) {
        java.util.List<Room> roomList = (statusFilter == null)
                ? roomService.getAllRooms()
                : roomService.getRoomsByStatus(statusFilter);
        ObservableList<Room> obs = FXCollections.observableArrayList(roomList);
        roomTable.setItems(obs);
    }

    private void showError(String msg) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        statusLabel.setText("✗  " + msg);
    }

    private void showSuccess(String msg) {
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        statusLabel.setText("✓  " + msg);
    }
}