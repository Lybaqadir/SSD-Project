package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// This class validates and sanitizes all user input before it reaches the database
// Used for the Input Validation security use case (prevents SQL injection etc.)
public class InputValidator {

    // Checks that text is not empty and has no dangerous characters
    public static boolean validateText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        // Block SQL injection characters
        String dangerous = "';--";
        for (char c : dangerous.toCharArray()) {
            if (input.indexOf(c) >= 0) {
                return false;
            }
        }
        return true;
    }

    // Removes any extra whitespace from input
    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim();
    }

    // Checks that the date string is in the format yyyy-MM-dd
    public static boolean validateDate(String date) {
        if (date == null || date.isEmpty()) return false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // Checks that the amount is a positive number
    public static boolean validateAmount(double amount) {
        return amount > 0;
    }
}
