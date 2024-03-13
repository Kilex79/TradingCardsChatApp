package com.hikari.spacecardscollection.Security.PasswordEncriptation;

public class AuthenticationHelper {
    public static boolean checkPassword(String inputPassword, String storedPassword) {
        // Encrypt the input password using SHA-256
        String hashedInputPassword = PasswordHelper.encryptPassword(inputPassword);
        // Compare the hashed input password with the stored hashed password
        return hashedInputPassword.equals(storedPassword);
    }
}