package service;
//noora
import dao.UserDAO;
import model.User;
import util.PasswordHasher;
import util.AuditLogger;

// Handles login and logout for the hotel system
// Tracks the current logged-in user and their role
// Used for: Log In use case, User Authentication Protection, Session Hijacking prevention
public class AuthService {

    // Stores the currently logged-in user for the whole session
    private static User currentUser = null;
    private static String currentRole = null;

    // Gets the user from database, checks password, sets current session
    public boolean login(String username, String password) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByUsername(username);

        if (user == null) {
            // User not found in database
            return false;
        }

        // Check the entered password against the stored hash using BCrypt
        boolean passwordMatches = PasswordHasher.verify(password, user.getPasswordHash());

        if (passwordMatches) {
            currentUser = user;
            currentRole = user.getRole();

            // Log the successful login to the audit table
            AuditLogger.log(user.getUserId(), "LOGIN", "User " + username + " logged in successfully.");
            return true;
        }

        // Log failed login attempt
        AuditLogger.log(0, "FAILED_LOGIN", "Failed login attempt for username: " + username);
        return false;
    }

    // Clears the session on logout
    public void logout() {
        if (currentUser != null) {
            AuditLogger.log(currentUser.getUserId(), "LOGOUT", "User " + currentUser.getUsername() + " logged out.");
        }
        currentUser = null;
        currentRole = null;
    }

    // Returns the role of whoever is logged in (used by other services for access control)
    public static String getCurrentRole() {
        return currentRole;
    }

    // Returns the full user object of whoever is logged in
    public static User getCurrentUser() {
        return currentUser;
    }
}
