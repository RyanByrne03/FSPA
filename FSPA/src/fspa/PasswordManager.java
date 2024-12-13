/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fspa;

import java.util.ArrayList;

/**
 *
 * @author RyanByrne
 */
public class PasswordManager {
    private static ArrayList<String> passwords = new ArrayList<>();

    public static void savePassword(String name, String password) {
        // Save name and password as one entry with a delimiter
        passwords.add(name + ":" + password);
    }

    public static String searchPassword(String name) {
        for (String entry : passwords) {
            String[] parts = entry.split(":", 2); // Split into name and password
            if (parts[0].equals(name)) {
                return parts[1]; // Return the password
            }
        }
        return "Password not found.";
    }
}