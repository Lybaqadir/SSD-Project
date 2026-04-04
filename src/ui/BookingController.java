package ui;
//nora and lyba did this
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import service.AuthService;
import service.BookingService;
import util.InputValidator;
import dao.AuditLogDAO;
import java.util.List;

public class BookingController {

    @FXML private TextField guestNameField;
    @FXML private TextField guestPhoneField;
    @FXML private TextField roomIdField;
    @FXML private TextField checkInField;
    @FXML private TextField checkOutField;
    @FXML private TextField searchField;
    @FXML private Label     messageLabel;
    @FXML private ListView<String> resultsList;

    // Modify booking fields
    @FXML private TextField modifyBookingIdField;
    @FXML private TextField modifyCheckInField;
    @FXML private TextField modifyCheckOutField;

    // Nav buttons
    @FXML private Button navBookings;
    @FXML private Button navCheckInOut;
    @FXML private Button navPayment;
    @FXML private Button navRooms;
    @FXML private Button navStaff;

    private BookingService bookingService = new BookingService();
    private AuditLogDAO    auditLogDAO    = new AuditLogDAO();

    @FXML
    public void initialize() {
        String role = AuthService.getCurrentRole();
        if ("Receptionist".equals(role)) {
            hideButton(navStaff);
        } else if ("Manager".equals(role)) {
            hideButton(navPayment);
        }
    }

    // Navigation
    @FXML private void goToBooking()    { /* already here */ }
    @FXML private void goToCheckInOut() { NavigationHelper.navigateTo(guestNameField, "/ui/CheckInOutView.fxml"); }
    @FXML private void goToPayment()    { NavigationHelper.navigateTo(guestNameField, "/ui/PaymentView.fxml"); }
    @FXML private void goToRooms()      { NavigationHelper.navigateTo(guestNameField, "/ui/RoomView.fxml"); }
    @FXML private void goToStaff()      { NavigationHelper.navigateTo(guestNameField, "/ui/StaffView.fxml"); }

    @FXML
    private void handleLogout() {
        new AuthService().logout();
        NavigationHelper.navigateTo(guestNameField, "/ui/LoginView.fxml");
    }

    // Actions
    @FXML
    private void handleCreate() {
        String guestName  = guestNameField.getText();
        String guestPhone = guestPhoneField.getText();
        String roomIdText = roomIdField.getText();
        String checkIn    = checkInField.getText();
        String checkOut   = checkOutField.getText();

        if (!InputValidator.validateText(guestName) || !InputValidator.validateText(guestPhone)) {
            showError("Please enter valid guest name and phone.");
            return;
        }
        if (!InputValidator.validateDate(checkIn) || !InputValidator.validateDate(checkOut)) {
            showError("Please enter dates in format yyyy-MM-dd.");
            return;
        }
        int roomId;
        try { roomId = Integer.parseInt(roomIdText); }
        catch (NumberFormatException e) { showError("Room ID must be a number."); return; }

        Booking booking = new Booking();
        booking.setGuestName(InputValidator.sanitize(guestName));
        booking.setGuestPhone(InputValidator.sanitize(guestPhone));
        booking.setRoomId(roomId);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setStatus("booked");

        boolean created = bookingService.createBooking(booking);
        if (created) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Booking created successfully.");
            clearForm();
        } else {
            showError("Failed to create booking. Please check availability.");
        }
    }

    // N004 — Receptionist modifies booking dates
    @FXML
    private void handleModify() {
        String idText   = modifyBookingIdField.getText().trim();
        String checkIn  = modifyCheckInField.getText().trim();
        String checkOut = modifyCheckOutField.getText().trim();

        if (idText.isEmpty()) { showError("Please enter the Booking ID to modify."); return; }
        int bookingId;
        try { bookingId = Integer.parseInt(idText); }
        catch (NumberFormatException e) { showError("Booking ID must be a number."); return; }

        if (!InputValidator.validateDate(checkIn) || !InputValidator.validateDate(checkOut)) {
            showError("Please enter dates in format yyyy-MM-dd.");
            return;
        }

        Booking updates = new Booking();
        updates.setCheckInDate(checkIn);
        updates.setCheckOutDate(checkOut);

        boolean modified = bookingService.modifyBooking(bookingId, updates);
        if (modified) {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Booking #" + bookingId + " updated successfully.");
            modifyBookingIdField.clear();
            modifyCheckInField.clear();
            modifyCheckOutField.clear();
        } else {
            showError("Failed to modify booking. Check the ID or date availability.");
        }
    }

    @FXML
    private void handleSearch() {
        String criteria = searchField.getText();
        if (criteria.isEmpty()) { showError("Please enter a name or phone to search."); return; }

        List<Booking> results = bookingService.searchBooking(criteria);
        resultsList.getItems().clear();

        if (results.isEmpty()) {
            showError("No bookings found.");
        } else {
            for (Booking b : results) {
                StringBuilder line = new StringBuilder();
                line.append("Booking #").append(b.getBookingId());
                line.append(" | ").append(b.getGuestName());
                line.append(" | Room ").append(b.getRoomId());
                line.append(" | Planned: ").append(b.getCheckInDate())
                        .append(" to ").append(b.getCheckOutDate());
                line.append(" | Status: ").append(b.getStatus());

                String actualCheckIn = auditLogDAO.getCheckInTimeByBookingId(b.getBookingId());
                if (actualCheckIn != null) {
                    line.append(" | Checked in: ").append(actualCheckIn);
                }
                String actualCheckOut = auditLogDAO.getCheckOutTimeByBookingId(b.getBookingId());
                if (actualCheckOut != null) {
                    line.append(" | Checked out: ").append(actualCheckOut);
                }

                resultsList.getItems().add(line.toString());
            }
            messageLabel.setText("");
        }
    }

    // Helpers
    private void hideButton(Button btn) {
        btn.setVisible(false);
        btn.setManaged(false);
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    private void clearForm() {
        guestNameField.clear();
        guestPhoneField.clear();
        roomIdField.clear();
        checkInField.clear();
        checkOutField.clear();
    }
}