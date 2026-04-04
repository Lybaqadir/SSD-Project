//lybaQadir did this
package service;

import dao.RoomDAO;
import model.Room;
import util.AuditLogger;
import util.InputValidator;
import java.util.List;

/**
 * COVERS USE CASES:
 *   Update Room Availability | Update Room Rates (N003) | Mark Room Dirty
 */
public class RoomService {

    private RoomDAO roomDAO = new RoomDAO();

    public boolean updateAvailability(int roomId, String status) {
        if (!InputValidator.validateText(status)) {
            System.err.println("[RoomService] Invalid status value: " + status);
            return false;
        }
        return roomDAO.updateStatus(roomId, status);
    }

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
     * Called by Receptionist after a guest checks out.
     * Sets the room's cleaningStatus to "dirty" so Cleaning Staff can see it.
     * @param roomId — the room to mark as dirty
     * @return true if update succeeded
     */
    public boolean markRoomDirty(int roomId) {
        String currentRole = AuthService.getCurrentRole();
        if (!"Receptionist".equals(currentRole) && !"Manager".equals(currentRole)) {
            System.err.println("[RoomService] Access denied. Only Receptionist or Manager can mark rooms dirty.");
            return false;
        }
        boolean updated = roomDAO.updateCleaningStatus(roomId, "dirty");
        if (updated && AuthService.getCurrentUser() != null) {
            AuditLogger.log(
                    AuthService.getCurrentUser().getUserId(),
                    "ROOM_MARKED_DIRTY",
                    "Room ID " + roomId + " marked as dirty by user ID " +
                            AuthService.getCurrentUser().getUserId()
            );
        }
        return updated;
    }

    public List<Room> getRoomsByStatus(String status) {
        return roomDAO.findByStatus(status);
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }
}