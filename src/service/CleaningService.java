//lybaqadir did this
package service;

import dao.RoomDAO;
import util.AuditLogger;
import util.InputValidator;

//USE CASE: Update Room Cleaning Status

public class CleaningService {

    // RoomDAO handles the SQL — we call it from here
    private RoomDAO roomDAO = new RoomDAO();
    /**
     * @param roomId
     * @param cleaningStatus
     * @param userId
     * @return true if the update succeeded, false if any check failed
     */
    public boolean updateCleaningStatus(int roomId, String cleaningStatus, int userId) {
        // AuthService.getCurrentRole() returns the role of whoever is logged in right now.
        String currentRole = AuthService.getCurrentRole();

        if (!"Cleaning Staff".equals(currentRole)) {
            System.err.println("[CleaningService] Access denied. Only Cleaning Staff can update cleaning status.");
            return false;
            // Return false immediately — nothing else in this method will run
        }
        // validateText() checks the value is not empty and doesn't contain
        // dangerous SQL characters (prevents injection through this field)
        if (!InputValidator.validateText(cleaningStatus)) {
            System.err.println("[CleaningService] Invalid cleaning status value: " + cleaningStatus);
            return false;
        }
        // Also check it's one of the valid allowed values — nothing else should go to the DB
        if (!cleaningStatus.equals("clean") &&
                !cleaningStatus.equals("dirty") &&
                !cleaningStatus.equals("in_progress")) {
            System.err.println("[CleaningService] Status must be: clean, dirty, or in_progress");
            return false;
        }

        boolean updated = roomDAO.updateCleaningStatus(roomId, cleaningStatus);

        if (updated) {
            AuditLogger.log(
                    userId,
                    "CLEANING_STATUS_UPDATED",
                    "Room ID " + roomId + " cleaning status set to '" +
                            cleaningStatus + "' by user ID " + userId
            );
        }

        return updated;
    }
}
