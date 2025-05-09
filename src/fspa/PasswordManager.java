/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package fspa;

import java.sql.*;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RyanByrne
 */
public class PasswordManager {

    // SQLite database file
    private static final String DB_URL = "jdbc:sqlite:FSPA.db";

    static {
        // Create the database table if it doesn't exist
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS passwords ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT UNIQUE NOT NULL,"
                    + "password TEXT NOT NULL);";
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void savePassword(String name, String passwordValue) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String encryptedPassword = CryptoUtil.encrypt(passwordValue);
            String query = "INSERT INTO passwords (name, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, encryptedPassword);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Name already exists. Choose a different name.");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String searchPassword(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
        String query = "SELECT password FROM passwords WHERE name = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String encryptedPassword = rs.getString("password");
            return CryptoUtil.decrypt(encryptedPassword);
        } else {
            return "Password not found.";
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return "Database error.";
    } catch (Exception e) {
        e.printStackTrace();
        return "Decryption error.";
    }
}

    public static List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT name FROM passwords";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public static int countPasswordUsage(String passwordValue) {
        int count = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT COUNT(*) AS count FROM passwords WHERE password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, passwordValue);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public static boolean isPasswordPwned(String password) {
        try {
            String sha1 = sha1Hash(password).toUpperCase();
            String prefix = sha1.substring(0, 5);
            String suffix = sha1.substring(5);

            URL url = new URL("https://api.pwnedpasswords.com/range/" + prefix);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith(suffix)) {
                    return true;
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getAllPasswordEntries() {
        List<String> entries = new ArrayList<>();
        String query = "SELECT name, password FROM passwords";

        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password");
                entries.add(name + " : " + password);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    private static String sha1Hash(String input) throws Exception {
        MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean deletePassword(String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "DELETE FROM passwords WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;  // true if something was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
