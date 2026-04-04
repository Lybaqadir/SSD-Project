package dao;
//noora
import model.Booking;
import util.DBConnection;

import java.sql.*;
import java.util.*;

// Talks to the bookings table in the database
// Uses PreparedStatements to prevent SQL injection
public class BookingDAO {

    // Saves a new booking to the database
    public boolean saveBooking(Booking booking) {
        String sql = "INSERT INTO bookings (guestName, guestPhone, roomId, checkInDate, checkOutDate, status) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, booking.getGuestName());
            stmt.setString(2, booking.getGuestPhone());
            stmt.setInt(3, booking.getRoomId());
            stmt.setString(4, booking.getCheckInDate());
            stmt.setString(5, booking.getCheckOutDate());
            stmt.setString(6, booking.getStatus());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error saving booking: " + e.getMessage());
        }

        return false;
    }

    // Finds a booking by its ID
    public Booking findById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE bookingId = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error finding booking: " + e.getMessage());
        }

        return null;
    }

    // Updates an existing booking — status AND dates if provided
    public boolean updateBooking(int bookingId, Booking booking) {
        String sql;
        if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
            sql = "UPDATE bookings SET status = ?, checkInDate = ?, checkOutDate = ? WHERE bookingId = ?";
        } else {
            sql = "UPDATE bookings SET status = ? WHERE bookingId = ?";
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, booking.getStatus());
            if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                stmt.setString(2, booking.getCheckInDate());
                stmt.setString(3, booking.getCheckOutDate());
                stmt.setInt(4, bookingId);
            } else {
                stmt.setInt(2, bookingId);
            }
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating booking: " + e.getMessage());
        }

        return false;
    }

    // Searches bookings by guest name or phone number
    public List<Booking> findByCriteria(String criteria) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE guestName LIKE ? OR guestPhone LIKE ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + criteria + "%");
            stmt.setString(2, "%" + criteria + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error searching bookings: " + e.getMessage());
        }

        return list;
    }

    // Maps a database row to a Booking object
    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("bookingId"));
        b.setGuestName(rs.getString("guestName"));
        b.setGuestPhone(rs.getString("guestPhone"));
        b.setRoomId(rs.getInt("roomId"));
        b.setCheckInDate(rs.getString("checkInDate"));
        b.setCheckOutDate(rs.getString("checkOutDate"));
        b.setStatus(rs.getString("status"));
        return b;
    }
}