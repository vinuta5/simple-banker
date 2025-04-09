package com.cbdg.interview.application.service;

import com.cbdg.interview.application.exception.InsufficientFundsException;
import com.cbdg.interview.application.exception.ResourceNotFoundException;
import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.model.Transaction;
import com.cbdg.interview.application.repository.AccountRepository;
import com.cbdg.interview.application.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getMonthlyStatement(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateBetweenAndFromAccountIdOrToAccountId(
                startDate, endDate, accountId, accountId);
    }

    @Override
    @Transactional
    public Transaction transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Retrieve accounts and check for existence
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("To account not found"));

        // Check for sufficient funds
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + fromAccountId);
        }

        // Update account balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Save updated accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create and save transaction record
        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}