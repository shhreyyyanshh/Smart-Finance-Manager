package model;

import java.time.LocalDate;

/**
 * Base abstraction for all financial transactions.
 */
public abstract class Transaction {
    private final String title;
    private final double amount;
    private final String category;
    private final LocalDate date;

    public Transaction(String title, double amount, String category, LocalDate date) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    /**
     * Implemented differently by each child class to demonstrate polymorphism.
     */
    public abstract String getTransactionType();

    public void printSummary() {
        System.out.printf(
                "%-8s | %-20s | %-12s | %-10.2f | %s%n",
                getTransactionType(),
                title,
                category,
                amount,
                date
        );
    }
}
