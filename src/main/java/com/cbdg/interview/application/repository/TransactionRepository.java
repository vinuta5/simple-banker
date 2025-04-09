package com.cbdg.interview.application.repository;

import com.cbdg.interview.application.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDateBetweenAndFromAccountIdOrToAccountId(
            LocalDateTime startDate, LocalDateTime endDate, Long fromAccountId, Long toAccountId);
}