//Aljory did this
package util;

import java.sql.Connection;
import java.sql.PreparedStatement;

// A utility class that audits actions and saves them to the database
public class AuditLogger {

    // log() is called every time a sensitive action happens
    // userId = who did it, action = what they did, details = extra info
    public static void log(int userId, String action, String details) {

        // ? marks are placeholders - PreparedStatement fills them safely
        // this prevents SQL injection (T03 in our STRIDE model)
        String sql = "INSERT INTO audit_logs (userId, action, details, timestamp) VALUES (?, ?, ?, NOW())";

        // try-catch so the system does not crash if logging fails
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, details);

            ps.executeUpdate(); // runs the INSERT query
            System.out.println("Audit log saved.");

        } catch (Exception e) {
            System.out.println("Error saving audit log: " + e.getMessage());
        }
    }
}