package com.example.kiotz.utilities;

public class EmailUtils {
    public static String getUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        return email.split("@")[0];
    }
}
