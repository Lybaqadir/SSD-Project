package dao;

import util.DBConnection;
import java.sql.*;

// Reads from the audit_logs table
// Used to get the ACTUAL check-in and check-out times of a guest
// (as opposed to the planned dates stored in the bookings table)
public class AuditLogDAO {

    /**
     * Returns the actual timestamp of when a guest checked in.
     *
     * When CheckInOutService calls AuditLogger.log(bookingId, "CHECK_IN", ...),
     * the bookingId is stored in the userId column of audit_logs.
     * So we search: action = 'CHECK_IN' AND userId = bookingId
     *
     * @param bookingId — the booking we want the real check-in time for
     * @return timestamp string e.g. "2025-03-15 14:32:00", or null if not checked in yet
     */
    public String getCheckInTimeByBookingId(int bookingId) {
        String sql = "SELECT timestamp FROM audit_logs " +
                "WHERE action = 'CHECK_IN' AND userId = ? " +
                "ORDER BY timestamp DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("timestamp");
            }

        } catch (SQLException e) {
            System.err.println("[AuditLogDAO] getCheckInTimeByBookingId error: " + e.getMessage());
        }
        return null; // means not checked in yet
    }

    /**
     * Returns the actual timestamp of when a guest checked out.
     *
     * @param bookingId — the booking we want the real check-out time for
     * @return timestamp string, or null if not checked out yet
     */
    public String getCheckOutTimeByBookingId(int bookingId) {
        String sql = "SELECT timestamp FROM audit_logs " +
                "WHERE action = 'CHECK_OUT' AND userId = ? " +
                "ORDER BY timestamp DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("timestamp");
            }

        } catch (SQLException e) {
            System.err.println("[AuditLogDAO] getCheckOutTimeByBookingId error: " + e.getMessage());
        }
        return null; // means not checked out yet
    }
}