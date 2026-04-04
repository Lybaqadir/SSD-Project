package ui;
//nora did this
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.AuthService;
import service.CheckInOutService;
import service.RoomService;

public class CheckInOutController {

    @FXML private TextField bookingIdField;
    @FXML private TextField roomIdDirtyField;
    @FXML private Label     messageLabel;

    // Nav buttons — used to show/hide based on role
    @FXML private Button navBookings;
    @FXML private Button navCheckInOut;
    @FXML private Button navPayment;
    @FXML private Button navRooms;
    @FXML private Button navStaff;

    private CheckInOutService checkInOutService = new CheckInOutService();
    private RoomService       roomService       = new RoomService();

    @FXML
    public void initialize() {
        String role = AuthService.getCurrentRole();
        if ("Receptionist".equals(role)) {
            hideButton(navStaff);
        } else if ("Manager".equals(role)) {
            hideButton(navPayment);
        }
    }

    // ── Navigation ──────────────────────────────────────────
    @FXML private void goToBooking()    { NavigationHelper.navigateTo(bookingIdField, "/ui/BookingView.fxml"); }
    @FXML private void goToCheckInOut() { /* already here */ }
    @FXML private void goToPayment()    { NavigationHelper.navigateTo(bookingIdField, "/ui/PaymentView.fxml"); }
    @FXML private void goToRooms()      { NavigationHelper.navigateTo(bookingIdField, "/ui/RoomView.fxml"); }
    @FXML private void goToStaff()      { NavigationHelper.navigateTo(bookingIdField, "/ui/StaffView.fxml"); }

    @FXML
    private void handleLogout() {
        new AuthService().logout();
        NavigationHelper.navigateTo(bookingIdField, "/ui/LoginView.fxml");
    }

    // ── Actions ─────────────────────────────────────────────
    @FXML
    private void handleCheckIn() {
        String idText = bookingIdField.getText();
        if (idText.isEmpty()) { showError("Please enter a booking ID."); return; }
        int bookingId;
        try { bookingId = Integer.parseInt(idText); }
        catch (NumberFormatException e) { showError("Booking ID must be a number."); return; }

        boolean success = checkInOutService.checkIn(bookingId);
        if (success) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            messageLabel.setText("✓  Guest checked in successfully.");
        } else {
            showError("Check-in failed. Please verify the booking ID.");
        }
    }

    @FXML
    private void handleCheckOut() {
        String idText = bookingIdField.getText();
        if (idText.isEmpty()) { showError("Please enter a booking ID."); return; }
        int bookingId;
        try { bookingId = Integer.parseInt(idText); }
        catch (NumberFormatException e) { showError("Booking ID must be a number."); return; }

        boolean success = checkInOutService.checkOut(bookingId);
        if (success) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            messageLabel.setText("✓  Guest checked out. Redirecting to payment...");
            // N012: after checkout, redirect to Payment screen
            NavigationHelper.navigateTo(bookingIdField, "/ui/PaymentView.fxml");
        } else {
            showError("Check-out failed. Please verify the booking ID.");
        }
    }

    // Receptionist marks a room as dirty (e.g. after a guest leaves)
    // The Cleaning Staff will then see this room in their dirty rooms list
    @FXML
    private void handleMarkDirty() {
        String idText = roomIdDirtyField.getText().trim();
        if (idText.isEmpty()) { showError("Please enter a Room ID to mark as dirty."); return; }
        int roomId;
        try { roomId = Integer.parseInt(idText); }
        catch (NumberFormatException e) { showError("Room ID must be a number."); return; }

        boolean success = roomService.markRoomDirty(roomId);
        if (success) {
            messageLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
            messageLabel.setText("✓  Room " + roomId + " marked as dirty. Cleaning staff notified.");
            roomIdDirtyField.clear();
        } else {
            showError("Failed to mark room as dirty. Check the Room ID.");
        }
    }

    // ── Helpers ──────────────────────────────────────────────
    private void hideButton(Button btn) {
        btn.setVisible(false);
        btn.setManaged(false);
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        messageLabel.setText("✗  " + message);
    }
}