//lybaQadir did this

package service;

import dao.RoomDAO;          // handles all SQL for the rooms table
import model.Room;           // the Room data container
import util.AuditLogger;     // records every rate change in audit_logs
import util.InputValidator;  // validates the new rate value before saving

import java.util.List;       // List interface (like a resizable array)

/**

 * SECURITY:
 *   • updateRate() checks the current user is a Manager before doing anything
 *     → mitigates T10 (Privilege Escalation) and T05 (Rate Tampering)
 *   • All rate changes are logged with AuditLogger
 *     → mitigates T06 (Repudiation) — manager can't deny changing a rate
 *   • InputValidator.validateAmount() checks the new rate is a valid positive number
 *
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

        return roomDAO.getRoomsByStatus(status);
    }

    /**
     * Returns every room in the database regardless of status.
     * Used by RoomController to show the full room list to the manager.
     */
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }
}
