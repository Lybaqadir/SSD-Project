package dao;

import model.User;
import util.DBConnection;

import java.sql.*;

// Talks to the users table in the database
// Uses PreparedStatements to prevent SQL injection
public class UserDAO {

    // Finds a user by their username (used during login)
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("userId"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("passwordHash"));
                user.setRole(rs.getString("role"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }

        return null;
    }

    // Saves a new user to the database
    public boolean save(User user) {
        String sql = "INSERT INTO users (username, passwordHash, role, firstName, lastName) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }

        return false;
    }

    // Updates an existing user record
    public boolean update(int userId, User user) {
        String sql = "UPDATE users SET username = ?, role = ?, firstName = ?, lastName = ? WHERE userId = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setInt(5, userId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }

        return false;
    }

    // Deletes a user from the database by their ID
    public boolean delete(int userId) {
        String sql = "DELETE FROM users WHERE userId = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }

        return false;
    }
}
