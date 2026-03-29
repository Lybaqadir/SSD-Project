//lybaQadir did this

package service;

import dao.RoomDAO;          // handles all SQL for the rooms table
import model.Room;           // the Room data container
import util.AuditLogger;     // records every rate change in audit_logs
import util.InputValidator;  // validates the new rate value before saving

import java.util.List;       // List interface (like a resizable array)

/**
 * WHAT IS THIS FILE?
 *   The "brain" for everything related to rooms.
 *   It contains the business logic (rules) for:
 *     - Updating a room's occupancy status (available / occupied / reserved)
 *     - Updating a room's nightly rate (Manager only)
 *     - Getting rooms by their status
 *
 * HOW IT FITS IN THE LAYERS:
 *   RoomController / CheckInOutService  (calls this)
 *        ↓
 *   RoomService  (this file — applies rules)
 *        ↓
 *   RoomDAO  (runs the actual SQL)
 *        ↓
 *   MySQL Database
 *
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


    // ══════════════════════════════════════════════════════════════════════════
    // METHOD 1: Update a room's occupancy status
    // ══════════════════════════════════════════════════════════════════════════
    /**
     * Changes the occupancy status of a room.
     *
     * Called by CheckInOutService:
     *   - After check-in  → updateAvailability(roomId, "occupied")
     *   - After check-out → updateAvailability(roomId, "available")
     *
     * Also called by RoomController when the manager manually changes a room's status.
     *
     * @param roomId — which room to update
     * @param status — new status: "available", "occupied", or "reserved"
     * @return true if the update worked, false if it failed
     */
    public boolean updateAvailability(int roomId, String status) {

        // Validate the status string — make sure it is one of the allowed values
        // and does not contain dangerous characters
        if (!InputValidator.validateText(status)) {
            System.err.println("[RoomService] Invalid status value: " + status);
            return false;
        }

        // Delegate to RoomDAO to run the UPDATE SQL query
        return roomDAO.updateStatus(roomId, status);
    }


    // ══════════════════════════════════════════════════════════════════════════
    // METHOD 2: Update a room's nightly rate (Manager only)
    // ══════════════════════════════════════════════════════════════════════════
    /**
     * Changes the nightly price of a room.
     *
     * SECURITY — TWO CHECKS BEFORE DOING ANYTHING:
     *   1. Role check: only Manager is allowed to change rates
     *      (T10 Privilege Escalation — even if UI is bypassed, service blocks it)
     *   2. Amount check: rate must be a positive number
     *      (T05 Rate Tampering — prevents setting rate to 0 or negative)
     *
     * Then the change is logged to audit_logs (T06 Repudiation — can't deny it).
     *
     * @param roomId  — which room to update
     * @param newRate — the new price per night (e.g. 250.00)
     * @param userId  — who is making this change (for audit log)
     * @return true if the update worked
     */
    public boolean updateRate(int roomId, double newRate, int userId) {

        // ── SECURITY CHECK 1: Role check ──────────────────────────────────
        // AuthService.getCurrentRole() returns the role of whoever is logged in.
        // Only "Manager" is allowed to update rates.
        String currentRole = AuthService.getCurrentRole();
        if (!"Manager".equals(currentRole)) {
            // "Manager".equals(currentRole) is safer than currentRole.equals("Manager")
            // because if currentRole is null, the first version won't crash
            System.err.println("[RoomService] Access denied. Only Manager can update rates.");
            return false;
        }

        // ── SECURITY CHECK 2: Validate the rate value ───────────────────
        // validateAmount() returns false if the rate is 0, negative, or not a real number
        if (!InputValidator.validateAmount(newRate)) {
            System.err.println("[RoomService] Invalid rate value: " + newRate);
            return false;
        }

        // ── Save to database ────────────────────────────────────────────
        boolean updated = roomDAO.updateRate(roomId, newRate);

        // ── Log the rate change ──────────────────────────────────────────
        // Rate changes MUST be logged — this is a security requirement
        // It mitigates T05 (Rate Tampering) and T06 (Repudiation)
        if (updated) {
            AuditLogger.log(
                    userId,
                    "RATE_UPDATED",
                    "Room ID " + roomId + " rate changed to " + newRate + " by user ID " + userId
            );
        }

        return updated;
    }


    // ══════════════════════════════════════════════════════════════════════════
    // METHOD 3: Get rooms by occupancy status
    // ══════════════════════════════════════════════════════════════════════════
    /**
     * Returns a list of all rooms that have a specific occupancy status.
     *
     * Examples:
     *   getRoomsByStatus("available") → all free rooms
     *   getRoomsByStatus("occupied")  → all rooms with guests currently in them
     *
     * Used by RoomController to display rooms on the room management screen.
     *
     * @param status — "available", "occupied", or "reserved"
     * @return List of Room objects matching that status (can be empty if none found)
     */
    public List<Room> getRoomsByStatus(String status) {
        // No business logic needed here — just pass through to the DAO
        return roomDAO.getRoomsByStatus(status);
    }


    // ══════════════════════════════════════════════════════════════════════════
    // METHOD 4: Get all rooms (for the room list display)
    // ══════════════════════════════════════════════════════════════════════════
    /**
     * Returns every room in the database regardless of status.
     * Used by RoomController to show the full room list to the manager.
     */
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }
}
