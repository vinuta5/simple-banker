package com.cbdg.interview.application.service;

import com.cbdg.interview.application.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Transaction createTransaction(Transaction transaction);
    List<Transaction> getMonthlyStatement(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    Transaction transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount);
}