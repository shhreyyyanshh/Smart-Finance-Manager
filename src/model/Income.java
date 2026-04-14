package model;

import java.time.LocalDate;

/**
 * Represents money received.
 */
public class Income extends Transaction {

    public Income(String title, double amount, String category, LocalDate date) {
        super(title, amount, category, date);
    }

    @Override
    public String getTransactionType() {
        return "Income";
    }
}
