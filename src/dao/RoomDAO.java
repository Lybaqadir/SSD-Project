package dao;

import model.Room;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//Aljory

public class RoomDAO{

    //To find room by ID:
    public Room findById(int roomId){
        String sql="SELECT * FROM rooms WHERE roomId=?";
        try(Connection con = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return mapRow(rs);
            }
        } catch (SQL Exception e){
            System.err.println("Find by ID Error "+e.getMessage());
        }
        return null;
    }

    //To update room status:
    public boolean updateStatus(int roomId, String status) {
        String sql = 'UPDATE rooms SET status =? WHERE roomId =?';
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQL Exception e){
            System.err.println("Room update status Error " + e.getMessage());
        }
        return false;
    }

    //To update room rate:
    public boolean updateRate(int roomId, double rate) {
        String sql = 'UPDATE rooms SET status =? WHERE roomId =?';
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, rate);
            stmt.setInt(2, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQL Exception e){
            System.err.println("Room update rate Error " + e.getMessage());
        }
        return false;
    }

    //To find room by status:
    public List<Room> findByStatus(String status){
        List<Room> rooms = new ArrayList<>();
        String sql="SELECT * FROM rooms WHERE status=?";
        try(Connection con = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                rooms.add(mapRow(rs));
            }
        } catch (SQL Exception e){
            System.err.println("Find by Status Error "+e.getMessage());
        }
        return rooms;
    }

    //To check room availability on a given date:
    public boolean hasAvailableRoom(Date checkIn, Date checkOut) {
        String sql = " SELECT COUNT(*) FROM rooms r " + "WHERE r.status = 'available'" +
                "AND r.roomId NOT IN (" + "SELECT b.roomId FROM bookings b " +
                "WHERE b.status != 'cancelled" + "AND b.checkInDate<? And b.checkOutDate>?" +
                ")";
        try (Connection con = DBConnection.getConnection();
             PreparedStatment stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(checkOut.getTime()));
            stmt.setDate(2, new java.sql.Date(checkIn.getTime()));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Find available room error " + e.getMessage());
        }
        return false;
    }

    //To update cleaning status:
    public boolean updateCleaningStatus(int roomId, String cleaningStatus) {
        String sql = 'UPDATE rooms SET cleaningStatus =? WHERE roomId =?';
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cleaningStatus);
            stmt.setInt(2, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQL Exception e){
            System.err.println("Room update cleaningStatus Error " + e.getMessage());
        }
        return false;
    }

    //Mapping rs row to a room
    private Room mapRow(ResultSet rs) throws SQLException{
        Room room = new Room();
        room.setRoomId(rs.getInt("roomId"));
        room.setRoomNumber(rs.getString("roomNumber"));
        room.setRoomType(rs.getString("roomType"));
        room.setRate(rs.getDouble("rate"));
        room.setStatus(rs.getString("status"));
        room.setCleaningStatus(rs.getString("cleaningStatus"));
        return room;
    }
}