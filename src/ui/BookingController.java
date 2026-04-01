//nora did this
package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Booking;
import service.BookingService;
import util.InputValidator;

import java.util.List;

// Controls the Booking screen
// Lets receptionists create and search bookings
public class BookingController {

    @FXML
    private TextField guestNameField;

    @FXML
    private TextField guestPhoneField;

    @FXML
    private TextField roomIdField;

    @FXML
    private TextField checkInField;

    @FXML
    private TextField checkOutField;

    @FXML
    private TextField searchField;

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<String> resultsList;

    private BookingService bookingService = new BookingService();

    // Called when Create Booking button is clicked
    @FXML
    private void handleCreate() {
        String guestName  = guestNameField.getText();
        String guestPhone = guestPhoneField.getText();
        String roomIdText = roomIdField.getText();
        String checkIn    = checkInField.getText();
        String checkOut   = checkOutField.getText();

        // Validate all inputs before doing anything
        if (!InputValidator.validateText(guestName) || !InputValidator.validateText(guestPhone)) {
            showError("Please enter valid guest name and phone.");
            return;
        }

        if (!InputValidator.validateDate(checkIn) || !InputValidator.validateDate(checkOut)) {
            showError("Please enter dates in format yyyy-MM-dd.");
            return;
        }

        int roomId;
        try {
            roomId = Integer.parseInt(roomIdText);
        } catch (NumberFormatException e) {
            showError("Room ID must be a number.");
            return;
        }

        // Build the booking object with sanitized inputs
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

    // Called when Search button is clicked
    @FXML
    private void handleSearch() {
        String criteria = searchField.getText();

        if (criteria.isEmpty()) {
            showError("Please enter a name, phone, or booking ID to search.");
            return;
        }

        List<Booking> results = bookingService.searchBooking(criteria);
        resultsList.getItems().clear();

        if (results.isEmpty()) {
            showError("No bookings found.");
        } else {
            for (Booking b : results) {
                resultsList.getItems().add(
                    "Booking #" + b.getBookingId() +
                    " | " + b.getGuestName() +
                    " | Room " + b.getRoomId() +
                    " | " + b.getCheckInDate() + " to " + b.getCheckOutDate() +
                    " | " + b.getStatus()
                );
            }
            messageLabel.setText("");
        }
    }

    // Helper to show red error messages
    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red;");
        messageLabel.setText(message);
    }

    // Clears the form fields after a successful booking
    private void clearForm() {
        guestNameField.clear();
        guestPhoneField.clear();
        roomIdField.clear();
        checkInField.clear();
        checkOutField.clear();
    }
}
