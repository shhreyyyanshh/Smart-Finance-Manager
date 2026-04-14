package service;

import model.Expense;
import model.Income;
import model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles all finance operations for the application.
 */
public class FinanceManager {
    private final List<Transaction> transactions;
    private double budgetLimit;

    public FinanceManager() {
        this.transactions = new ArrayList<>();
        this.budgetLimit = 0.0;
    }

    public void addIncome(String title, double amount, String category) {
        transactions.add(new Income(title, amount, category, LocalDate.now()));
        System.out.println("Income added successfully.");
    }

    public void addExpense(String title, double amount, String category) {
        transactions.add(new Expense(title, amount, category, LocalDate.now()));
        System.out.println("Expense added successfully.");
        printBudgetAlert();
    }

    public void setBudget(double budgetLimit) {
        this.budgetLimit = budgetLimit;
        System.out.printf("Budget set to: %.2f%n", budgetLimit);
        printBudgetAlert();
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public double getTotalIncome() {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction instanceof Income) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    public double getTotalExpense() {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction instanceof Expense) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    public double getBudgetUsagePercentage() {
        if (budgetLimit <= 0) {
            return 0.0;
        }
        return (getTotalExpense() / budgetLimit) * 100;
    }

    public void printBudgetAlert() {
        if (budgetLimit <= 0) {
            return;
        }

        double usage = getBudgetUsagePercentage();

        if (usage > 100) {
            System.out.printf("ALERT: Budget exceeded! You have used %.2f%%%n", usage);
        } else if (usage >= 80) {
            System.out.printf("WARNING: You have used %.2f%% of your budget.%n", usage);
        }
    }

    public String findTopSpendingCategory() {
        Map<String, Double> categoryTotals = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction instanceof Expense) {
                String category = transaction.getCategory();
                double updatedAmount = categoryTotals.getOrDefault(category, 0.0) + transaction.getAmount();
                categoryTotals.put(category, updatedAmount);
            }
        }

        if (categoryTotals.isEmpty()) {
            return "No expense categories available.";
        }

        String topCategory = "";
        double highestAmount = 0.0;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > highestAmount) {
                highestAmount = entry.getValue();
                topCategory = entry.getKey();
            }
        }

        return topCategory + " (Total spent: " + String.format("%.2f", highestAmount) + ")";
    }

    public void printAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\nType     | Title                | Category     | Amount     | Date");
        System.out.println("---------------------------------------------------------------------");
        for (Transaction transaction : transactions) {
            transaction.printSummary();
        }
    }

    public void printReport() {
        System.out.println("\n========== FINANCE REPORT ==========");
        System.out.printf("Total Income      : %.2f%n", getTotalIncome());
        System.out.printf("Total Expense     : %.2f%n", getTotalExpense());
        System.out.printf("Current Balance   : %.2f%n", getBalance());

        if (budgetLimit > 0) {
            System.out.printf("Budget Limit      : %.2f%n", budgetLimit);
            System.out.printf("Budget Used       : %.2f%%%n", getBudgetUsagePercentage());
        } else {
            System.out.println("Budget Limit      : Not set");
        }

        System.out.println("Top Spending Area : " + findTopSpendingCategory());
        System.out.println("====================================");
    }
}
