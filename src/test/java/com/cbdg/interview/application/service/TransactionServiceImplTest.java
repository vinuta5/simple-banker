package com.cbdg.interview.application.service;

import com.cbdg.interview.application.exception.InsufficientFundsException;
import com.cbdg.interview.application.exception.ResourceNotFoundException;
import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.model.Transaction;
import com.cbdg.interview.application.repository.AccountRepository;
import com.cbdg.interview.application.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_shouldReturnCreatedTransaction() {
        Transaction transactionToCreate = new Transaction();
        transactionToCreate.setAmount(BigDecimal.valueOf(100));

        when(transactionRepository.save(transactionToCreate)).thenReturn(transactionToCreate);

        Transaction createdTransaction = transactionService.createTransaction(transactionToCreate);

        assertEquals(transactionToCreate, createdTransaction);
        verify(transactionRepository, times(1)).save(transactionToCreate);
    }

    @Test
    void getMonthlyStatement_shouldReturnListOfTransactions() {
        Long accountId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = LocalDateTime.now();

        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        List<Transaction> expectedTransactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findByDateBetweenAndFromAccountIdOrToAccountId(
                startDate, endDate, accountId, accountId))
                .thenReturn(expectedTransactions);

        List<Transaction> actualTransactions = transactionService.getMonthlyStatement(accountId, startDate, endDate);

        assertEquals(expectedTransactions, actualTransactions);
        verify(transactionRepository, times(1))
                .findByDateBetweenAndFromAccountIdOrToAccountId(startDate, endDate, accountId, accountId);
    }

    @Test
    void transferMoney_shouldTransferMoneyAndReturnTransaction() {
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        Account fromAccount = new Account();
        fromAccount.setId(fromAccountId);
        fromAccount.setBalance(BigDecimal.valueOf(200));

        Account toAccount = new Account();
        toAccount.setId(toAccountId);
        toAccount.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setFromAccount(fromAccount);
        expectedTransaction.setToAccount(toAccount);
        expectedTransaction.setAmount(amount);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        Transaction actualTransaction = transactionService.transferMoney(fromAccountId, toAccountId, amount);

        assertEquals(expectedTransaction, actualTransaction);
        assertEquals(BigDecimal.valueOf(100), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(150), toAccount.getBalance());
        verify(accountRepository, times(1)).save(fromAccount);
        verify(accountRepository, times(1)).save(toAccount);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transferMoney_shouldThrowResourceNotFoundException_whenFromAccountNotFound() {
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.transferMoney(fromAccountId, toAccountId, amount));
    }

    @Test
    void transferMoney_shouldThrowResourceNotFoundException_whenToAccountNotFound() {
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        Account fromAccount = new Account();
        fromAccount.setId(fromAccountId);
        fromAccount.setBalance(BigDecimal.valueOf(200));

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.transferMoney(fromAccountId, toAccountId, amount));
    }

    @Test
    void transferMoney_shouldThrowInsufficientFundsException_whenInsufficientFunds() {
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(300);

        Account fromAccount = new Account();
        fromAccount.setId(fromAccountId);
        fromAccount.setBalance(BigDecimal.valueOf(200));

        Account toAccount = new Account();
        toAccount.setId(toAccountId);
        toAccount.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.transferMoney(fromAccountId, toAccountId, amount));
    }
}