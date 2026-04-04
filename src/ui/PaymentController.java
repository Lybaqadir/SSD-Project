package ui;
//Aljory
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.AuthService;
import service.PaymentService;

public class PaymentController {

    @FXML private Label lblTotalAmount;
    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private Button btnConfirmPayment;
    @FXML private Label lblStatus;

    private PaymentService paymentService = new PaymentService();
    private int currentBookingId;

    public void setBookingId(int bookingId) {
        this.currentBookingId = bookingId;
    }

    @FXML
    public void initialize() {
        cmbPaymentMethod.getItems().addAll("Cash", "Card", "Online");
    }

    // ── Navigation ──────────────────────────────────────────
    @FXML private void goToBooking()    { NavigationHelper.navigateTo(lblTotalAmount, "/ui/BookingView.fxml"); }
    @FXML private void goToCheckInOut() { NavigationHelper.navigateTo(lblTotalAmount, "/ui/CheckInOutView.fxml"); }
    @FXML private void goToPayment()    { /* already here */ }
    @FXML private void goToRooms()      { NavigationHelper.navigateTo(lblTotalAmount, "/ui/RoomView.fxml"); }

    @FXML
    private void handleLogout() {
        new AuthService().logout();
        NavigationHelper.navigateTo(lblTotalAmount, "/ui/LoginView.fxml");
    }

    // Actions
    @FXML
    public void handleConfirmPayment() {
        String method = cmbPaymentMethod.getValue();

        if (method == null || method.isEmpty()) {
            lblStatus.setText("Please select a payment method.");
            return;
        }

        double amount = paymentService.calculateTotal(currentBookingId);
        boolean success = paymentService.processPayment(currentBookingId, amount, method);

        if (success) {
            lblStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            lblStatus.setText("✓  Payment Successful.");
            btnConfirmPayment.setDisable(true);
        } else {
            lblStatus.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            lblStatus.setText("✗  Payment failed. Please try again.");
        }
    }
}