package main;

import model.User;
import service.AuthService;
import service.DatabaseManager;
import service.FinanceManager;

import java.util.Scanner;

/**
 * Console-based entry point for the Smart Personal Finance Manager.
 */
public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final FinanceManager financeManager = new FinanceManager();
    private static final AuthService authService = new AuthService();
    private static User currentUser;

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        if (!showAuthMenu()) {
            System.out.println("Application closed.");
            scanner.close();
            return;
        }

        boolean running = true;

        System.out.println("=======================================");
        System.out.println(" Smart Personal Finance Manager");
        System.out.println(" Welcome, " + currentUser.getName());
        System.out.println("=======================================");

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addIncome();
                    break;
                case 2:
                    addExpense();
                    break;
                case 3:
                    setBudget();
                    break;
                case 4:
                    viewSummary();
                    break;
                case 5:
                    financeManager.printAllTransactions();
                    break;
                case 6:
                    showTopSpendingCategory();
                    break;
                case 7:
                    financeManager.printReport();
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting Smart Personal Finance Manager. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static boolean showAuthMenu() {
        while (true) {
            System.out.println("\nAuthentication Menu:");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("0. Exit");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    signUp();
                    break;
                case 2:
                    if (login()) {
                        return true;
                    }
                    break;
                case 0:
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add Income");
        System.out.println("2. Add Expense");
        System.out.println("3. Set Budget");
        System.out.println("4. View Total Income and Expense");
        System.out.println("5. View All Transactions");
        System.out.println("6. Find Top Spending Category");
        System.out.println("7. Print Report");
        System.out.println("0. Exit");
    }

    private static void signUp() {
        System.out.println("\nCreate Account");
        String name = readText("Enter your name: ");
        String username = readText("Choose a username: ");
        String password = readText("Choose a password: ");

        User user = new User(name, username, password);
        authService.signUp(user);
    }

    private static boolean login() {
        System.out.println("\nLogin");
        String username = readText("Enter username: ");
        String password = readText("Enter password: ");

        User user = authService.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful.");
            return true;
        }

        System.out.println("Invalid username or password.");
        return false;
    }

    private static void addIncome() {
        System.out.println("\nAdd Income");
        String title = readText("Enter income title: ");
        double amount = readDouble("Enter income amount: ");
        String category = readText("Enter income category: ");

        financeManager.addIncome(title, amount, category);
    }

    private static void addExpense() {
        System.out.println("\nAdd Expense");
        String title = readText("Enter expense title: ");
        double amount = readDouble("Enter expense amount: ");
        String category = readText("Enter expense category: ");

        financeManager.addExpense(title, amount, category);
    }

    private static void setBudget() {
        System.out.println("\nSet Budget");
        double budget = readDouble("Enter budget amount: ");
        financeManager.setBudget(budget);
    }

    private static void viewSummary() {
        System.out.println("\n========== SUMMARY ==========");
        System.out.printf("Total Income  : %.2f%n", financeManager.getTotalIncome());
        System.out.printf("Total Expense : %.2f%n", financeManager.getTotalExpense());
        System.out.printf("Balance       : %.2f%n", financeManager.getBalance());

        if (financeManager.getBudgetLimit() > 0) {
            System.out.printf("Budget Limit  : %.2f%n", financeManager.getBudgetLimit());
            System.out.printf("Budget Used   : %.2f%%%n", financeManager.getBudgetUsagePercentage());
        } else {
            System.out.println("Budget Limit  : Not set");
        }
        System.out.println("=============================");
    }

    private static void showTopSpendingCategory() {
        System.out.println("\nTop Spending Category:");
        System.out.println(financeManager.findTopSpendingCategory());
    }

    private static String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value < 0) {
                    System.out.println("Please enter a non-negative amount.");
                    continue;
                }
                return value;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
