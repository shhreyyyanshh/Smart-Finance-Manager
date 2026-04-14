package service;

import model.User;

import java.util.List;

/**
 * Handles signup and login operations.
 */
public class AuthService {

    public boolean signUp(User user) {
        if (isUsernameTaken(user.getUsername())) {
            System.out.println("Username already exists. Please choose another username.");
            return false;
        }

        if (DatabaseManager.saveUser(user)) {
            System.out.println("Signup successful. You can now log in.");
            return true;
        }

        return false;
    }

    public User login(String username, String password) {
        List<User> users = DatabaseManager.getAllUsers();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }

        return null;
    }

    private boolean isUsernameTaken(String username) {
        List<User> users = DatabaseManager.getAllUsers();

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }
}
