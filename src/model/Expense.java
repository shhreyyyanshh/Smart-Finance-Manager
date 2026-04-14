package model;

import java.time.LocalDate;

/**
 * Represents money spent.
 */
public class Expense extends Transaction {

    public Expense(String title, double amount, String category, LocalDate date) {
        super(title, amount, category, date);
    }

    @Override
    public String getTransactionType() {
        return "Expense";
    }
}
