package service;
//noora
import dao.BookingDAO;
import model.Booking;
import util.AuditLogger;

// Handles guest check-in and check-out
// On check-in: updates booking status and marks room as occupied
// On check-out: updates booking status and marks room as available
// Used for: Check-In Guest and Check-Out Guest use cases
public class CheckInOutService {

    private BookingDAO bookingDAO;
    private RoomService roomService;

    public CheckInOutService() {
        bookingDAO  = new BookingDAO();
        roomService = new RoomService();
    }

    // Checks in a guest using their booking ID
    public boolean checkIn(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);

        if (booking == null) {
            System.out.println("Booking not found: " + bookingId);
            return false;
        }

        // Update booking status to checked-in
        booking.setStatus("checked-in");
        boolean updated = bookingDAO.updateBooking(bookingId, booking);

        if (updated) {
            // Mark the room as occupied
            roomService.updateAvailability(booking.getRoomId(), "occupied");

            // Log the check-in action
            AuditLogger.log(bookingId, "CHECK_IN", "Guest " + booking.getGuestName() + " checked in to room " + booking.getRoomId());
        }

        return updated;
    }

    // Checks out a guest using their booking ID
    public boolean checkOut(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);

        if (booking == null) {
            System.out.println("Booking not found: " + bookingId);
            return false;
        }

        // Update booking status to checked-out
        booking.setStatus("checked-out");
        boolean updated = bookingDAO.updateBooking(bookingId, booking);

        if (updated) {
            // Mark the room as available again
            roomService.updateAvailability(booking.getRoomId(), "available");

            // Log the check-out action
            AuditLogger.log(bookingId, "CHECK_OUT", "Guest " + booking.getGuestName() + " checked out of room " + booking.getRoomId());
        }

        return updated;
    }
}
