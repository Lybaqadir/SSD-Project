//lybaqadir did this
package service;

import dao.UserDAO;
import model.User;
import util.AuditLogger;
import util.InputValidator;
import util.PasswordHasher;
import java.util.List;

// COVERS USE CASE: Manage Staff Accounts

public class StaffService {
    private UserDAO userDAO = new UserDAO();

    /**
     * @param newUser       — a User object with the new staff's details.
     * @param plainPassword — the plain text password typed by the manager
     *                        (we hash this before saving — NEVER store plain passwords).
     * @param managerId     — the Manager's user ID (for audit log)
     * @return true if account was created successfully
     **/
    public boolean createStaffAccount(User newUser, String plainPassword, int managerId) {

        //Role check
        if (!"Manager".equals(AuthService.getCurrentRole())) {
            System.err.println("[StaffService] Access denied. Only Manager can create accounts.");
            return false;
        }

        // Validate inputs
        // validateText() returns false if input is empty or contains SQL injection characters
        if (!InputValidator.validateText(newUser.getUsername())) {
            System.err.println("[StaffService] Invalid username.");
            return false;
        }

        if (!InputValidator.validateText(newUser.getFirstName()) ||
                !InputValidator.validateText(newUser.getLastName())) {
            System.err.println("[StaffService] Invalid name fields.");
            return false;
        }

        // check the plain password is not empty
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            System.err.println("[StaffService] Password cannot be empty.");
            return false;
        }

        // PasswordHasher.hash() takes a plain text password and returns a BCrypt hash.
        String hashedPassword = PasswordHasher.hash(plainPassword);

        newUser.setPasswordHash(hashedPassword);

        boolean saved = userDAO.saveUser(newUser);
        if (saved) {
            AuditLogger.log(
                    managerId,
                    "ACCOUNT_CREATED",
                    "New " + newUser.getRole() + " account created: " + newUser.getUsername() +
                            " by Manager ID " + managerId
            );
        }

        return saved;
    }

    /**
     * Updates the details of an existing staff account.
     * @param staffId     — the ID of the account to update
     * @param updatedUser — a User object containing the new values
     * @param managerId   — who is making this change (for audit log)
     * @return true if the update succeeded
     */
    public boolean updateStaffAccount(int staffId, User updatedUser, int managerId) {

        // Role check — only Manager
        if (!"Manager".equals(AuthService.getCurrentRole())) {
            System.err.println("[StaffService] Access denied. Only Manager can update accounts.");
            return false;
        }

        // Validate the updated username
        if (!InputValidator.validateText(updatedUser.getUsername())) {
            System.err.println("[StaffService] Invalid username in update.");
            return false;
        }

        // Tell UserDAO to run the UPDATE SQL for this staffId
        boolean updated = userDAO.updateUser(staffId, updatedUser);

        if (updated) {
            AuditLogger.log(
                    managerId,
                    "ACCOUNT_UPDATED",
                    "Staff account ID " + staffId + " updated by Manager ID " + managerId
            );
        }

        return updated;
    }

    /**
     * @param staffId   — the ID of the account to delete
     * @param managerId — who is deleting this account (for audit log)
     * @return true if deletion succeeded
     */
    public boolean deleteStaffAccount(int staffId, int managerId) {

        // Role check — only Manager
        if (!"Manager".equals(AuthService.getCurrentRole())) {
            System.err.println("[StaffService] Access denied. Only Manager can delete accounts.");
            return false;
        }

        boolean deleted = userDAO.deleteUser(staffId);

        if (deleted) {
            AuditLogger.log(
                    managerId,
                    "ACCOUNT_DELETED",
                    "Staff account ID " + staffId + " deleted by Manager ID " + managerId
            );
        }

        return deleted;
    }

    // @return List of User objects (every staff member in the database)
    public List<User> getAllStaff() {
        return userDAO.getAllUsers();
    }
}