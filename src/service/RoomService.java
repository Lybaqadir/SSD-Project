//lybaQadir did this
package service;

import dao.RoomDAO;
import model.Room;
import util.AuditLogger;
import util.InputValidator;
import java.util.List;

/**
 * COVERS USE CASES:
 *   Update Room Availability | Update Room Rates (N003)
 */
public class RoomService {

    // One instance of RoomDAO — this is how we talk to the rooms table
    private RoomDAO roomDAO = new RoomDAO();

    /**
     * @param roomId — which room to update
     * @param status — new status: "available", "occupied", or "reserved"
     * @return true if the update worked, false if it failed
     */
    public boolean updateAvailability(int roomId, String status) {
        if (!InputValidator.validateText(status)) {
            System.err.println("[RoomService] Invalid status value: " + status);
            return false;
        }
        return roomDAO.updateStatus(roomId, status);
    }

    /**
     * @param roomId  — which room to update
     * @param newRate — the new price per night (e.g. 250.00)
     * @param userId  — who is making this change (for audit log)
     * @return true if the update worked
     **/
    public boolean updateRate(int roomId, double newRate, int userId) {
        String currentRole = AuthService.getCurrentRole();
        if (!"Manager".equals(currentRole)) {
            System.err.println("[RoomService] Access denied. Only Manager can update rates.");
            return false;
        }

        if (!InputValidator.validateAmount(newRate)) {
            System.err.println("[RoomService] Invalid rate value: " + newRate);
            return false;
        }

        boolean updated = roomDAO.updateRate(roomId, newRate);

        if (updated) {
            AuditLogger.log(
                    userId,
                    "RATE_UPDATED",
                    "Room ID " + roomId + " rate changed to " + newRate + " by user ID " + userId
            );
        }

        return updated;
    }

    /**
     * @param status — "available", "occupied", or "reserved"
     * @return List of Room objects matching that status (can be empty if none found)
     */
    public List<Room> getRoomsByStatus(String status) {
        return roomDAO.findByStatus(status);
    }

    //Returns every room in the database regardless of status.
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }
}