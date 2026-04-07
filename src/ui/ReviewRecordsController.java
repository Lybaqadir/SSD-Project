package ui;
// N009 — Review Booking and Payment Records (Manager only)
//lyba
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import model.Payment;
import service.AuthService;
import dao.BookingDAO;
import dao.PaymentDAO;
import java.util.List;

public class ReviewRecordsController {

    @FXML private ListView<String> bookingsList;
    @FXML private ListView<String> paymentsList;
    @FXML private Label            statusLabel;

    private BookingDAO bookingDAO = new BookingDAO();
    private PaymentDAO paymentDAO = new PaymentDAO();

    @FXML
    public void initialize() {
        if (!"Manager".equals(AuthService.getCurrentRole())) {
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            statusLabel.setText("✗  Access Denied. Only Manager can view records.");
            return;
        }
        loadBookings();
        loadPayments();
    }

    // Navigation
    @FXML private void goToStaff()
    { NavigationHelper.navigateTo(bookingsList, "/ui/StaffView.fxml"); }
    @FXML private void goToRooms()
    { NavigationHelper.navigateTo(bookingsList, "/ui/RoomView.fxml"); }
    @FXML private void goToReviewRecords()
    { /* already here */ }

    @FXML
    private void handleLogout() {
        new AuthService().logout();
        NavigationHelper.navigateTo(bookingsList, "/ui/LoginView.fxml");
    }

    // Actions
    @FXML
    private void handleRefresh() {
        loadBookings();
        loadPayments();
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText("✓  Records refreshed.");
    }

    // ── Helpers ──────────────────────────────────────────────
    private void loadBookings() {
        bookingsList.getItems().clear();
        List<Booking> bookings = bookingDAO.findByCriteria(""); // get all
        if (bookings.isEmpty()) {
            bookingsList.getItems().add("No booking records found.");
            return;
        }
        for (Booking b : bookings) {
            bookingsList.getItems().add(
                    "Booking #" + b.getBookingId()
                            + " | " + b.getGuestName()
                            + " | Room " + b.getRoomId()
                            + " | " + b.getCheckInDate() + " → " + b.getCheckOutDate()
                            + " | Status: " + b.getStatus()
            );
        }
    }

    private void loadPayments() {
        paymentsList.getItems().clear();
        // We search all bookings and look up payments for each
        List<Booking> bookings = bookingDAO.findByCriteria("");
        boolean anyPayment = false;
        for (Booking b : bookings) {
            Payment p = paymentDAO.findByBookingId(b.getBookingId());
            if (p != null) {
                anyPayment = true;
                paymentsList.getItems().add(
                        "Payment #" + p.getPaymentId()
                                + " | Booking #" + p.getBookingId()
                                + " | Amount: " + String.format("%.2f", p.getAmount())
                                + " | Method: " + p.getMethod()
                                + " | Status: " + p.getStatus()
                                + " | " + p.getTimestamp()
                );
            }
        }
        if (!anyPayment) {
            paymentsList.getItems().add("No payment records found.");
        }
    }
}