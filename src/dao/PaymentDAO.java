//lybaqadir did this
package dao;

import model.Payment;
import util.DBConnection;
import java.sql.*;

/**
 * dao package (DAO = Data Access Object)
 * The ONLY class allowed to run SQL on the "payments" table.
 * PaymentService calls this class — it never touches SQL directly.
 * savePayment() — INSERT a new payment row into the payments table
 * findByBookingId() — SELECT a payment by its booking_id
 **/
public class PaymentDAO {

    //@return true if the INSERT succeeded, false if something went wrong
    public boolean savePayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, amount, method, timestamp, status) " +
                "VALUES (?, ?, ?, NOW(), ?)";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql))
        //The SQL structure is locked and fixed. The only thing that can change is the data
        // going into the placeholders.
        {
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getMethod());
            stmt.setString(4, payment.getStatus());

            int rows = stmt.executeUpdate(); //returns number of rows affected in db after execution.

            if (rows > 0) {
                return true; //rows affected save worked !
            }

        } catch (SQLException e) {
            // Print the error to the error console (shows in red in IntelliJ)
            System.err.println("[PaymentDAO] savePayment error: " + e.getMessage());
            return false;
            // Return false so the caller knows the save failed
        }
        return false;
    }

    /**
     * @param bookingId — the booking whose payment we want to look up
     * @return a Payment object if found, or null if not found
     */
    public Payment findByBookingId(int bookingId) {

        String sql = "SELECT * FROM payments WHERE booking_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);

            // executeQuery() is used for SELECT statements
            // It returns a ResultSet — like a mini-table of results
            ResultSet rs = stmt.executeQuery();

            // rs.next() moves to the first row returned in rs table.
            // If a row exists, rs.next() returns true - > enter the if block.
            if (rs.next()) {
                /** reads and gets data from each column that row to create object and return it **/
                return new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("booking_id"),
                        rs.getDouble("amount"),
                        rs.getString("method"),
                        rs.getTimestamp("timestamp"), // when the payment was made
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.err.println("[PaymentDAO] findByBookingId error: " + e.getMessage());
        }

        // If we reach here, no payment was found for this booking → return null
        return null;
    }
}