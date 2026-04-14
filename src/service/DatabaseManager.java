package service;

import model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores user data in a simple local file so the CLI app can run without
 * requiring any external database driver setup.
 */
public class DatabaseManager {
    private static final Path USERS_FILE = Paths.get("finance_manager_users.txt");

    public static void initializeDatabase() {
        try {
            if (Files.notExists(USERS_FILE)) {
                Files.createFile(USERS_FILE);
            }
        } catch (IOException exception) {
            System.out.println("Failed to initialize user storage: " + exception.getMessage());
        }
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try {
            initializeDatabase();
            List<String> lines = Files.readAllLines(USERS_FILE, StandardCharsets.UTF_8);

            for (String line : lines) {
                if (line == null || line.isBlank()) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException exception) {
            System.out.println("Failed to read user storage: " + exception.getMessage());
        }

        return users;
    }

    public static boolean saveUser(User user) {
        try {
            initializeDatabase();
            String line = user.getName() + "|" + user.getUsername() + "|" + user.getPassword()
                    + System.lineSeparator();
            Files.writeString(USERS_FILE, line, StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND);
            return true;
        } catch (IOException exception) {
            System.out.println("Failed to save user details: " + exception.getMessage());
            return false;
        }
    }
}
