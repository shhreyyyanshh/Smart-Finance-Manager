package com.example.finance.service;

import com.example.finance.entity.Transaction;
import com.example.finance.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles transaction business logic.
 */
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
