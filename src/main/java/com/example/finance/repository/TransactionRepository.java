package com.example.finance.repository;

import com.example.finance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for transaction database operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
