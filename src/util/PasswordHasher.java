package util;
//noora
import org.mindrot.jbcrypt.BCrypt;

// This class handles password hashing and verification using BCrypt
// Used for the User Authentication Protection security use case
public class PasswordHasher {

    // Hashes a plain text password before saving it to the database
    public static String hash(String password)
    {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Checks if a plain text password matches the stored hash
    public static boolean verify(String password, String hash)
    {
        return BCrypt.checkpw(password, hash);
    }
}
