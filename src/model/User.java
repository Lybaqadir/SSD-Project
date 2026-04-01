package model;
//lyba

// Plain Java object that holds user data
// Each field maps to one column in the users table
public class User {

    private int    userId;
    private String username;
    private String passwordHash; // User Authentication Protection - database stores hashed passwords
    private String role;
    private String firstName;
    private String lastName;

    // Empty constructor
    public User() {}

    // Constructor with all fields
    public User(int userId, String username, String passwordHash,
                String role, String firstName, String lastName) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.firstName    = firstName;
        this.lastName     = lastName;
    }

    // Getters
    public int    getUserId()       { return userId; }
    public String getUsername()     { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole()         { return role; }
    public String getFirstName()    { return firstName; }
    public String getLastName()     { return lastName; }

    // Setters
    public void setUserId(int userId)             { this.userId = userId; }
    public void setUsername(String username)       { this.username = username; }
    public void setPasswordHash(String hash)       { this.passwordHash = hash; }
    public void setRole(String role)               { this.role = role; }
    public void setFirstName(String firstName)     { this.firstName = firstName; }
    public void setLastName(String lastName)       { this.lastName = lastName; }

    // We never print passwordHash in toString - it is sensitive data
    @Override
    public String toString() {
        return "User{" +
                "id="         + userId    +
                ", username='" + username  + '\'' +
                ", role='"     + role      + '\'' +
                ", name='"     + firstName + " " + lastName + '\'' +
                '}';
    }
}
