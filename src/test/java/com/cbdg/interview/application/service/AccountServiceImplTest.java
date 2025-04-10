package com.cbdg.interview.application.service;

import com.cbdg.interview.application.exception.ResourceNotFoundException;
import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAccountsByCustomerId_shouldReturnListOfAccounts() {
        Long customerId = 1L;
        Account account1 = new Account();
        account1.setId(1L);
        account1.setCustomerId(customerId);
        Account account2 = new Account();
        account2.setId(2L);
        account2.setCustomerId(customerId);
        List<Account> expectedAccounts = Arrays.asList(account1, account2);

        when(accountRepository.findByCustomerId(customerId)).thenReturn(expectedAccounts);

        List<Account> actualAccounts = accountService.getAccountsByCustomerId(customerId);

        assertEquals(expectedAccounts, actualAccounts);
        verify(accountRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void getAccountDetails_shouldReturnAccount_whenAccountExists() {
        Long accountId = 1L;
        Account expectedAccount = new Account();
        expectedAccount.setId(accountId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount));

        Account actualAccount = accountService.getAccountDetails(accountId);

        assertEquals(expectedAccount, actualAccount);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountDetails_shouldThrowException_whenAccountNotFound() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountDetails(accountId));
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void createAccount_shouldReturnCreatedAccount() {
        Account accountToCreate = new Account();
        accountToCreate.setAccountNumber("1234567890");
        accountToCreate.setAccountType("Savings");
        accountToCreate.setBalance(BigDecimal.valueOf(1000.0));
        accountToCreate.setCustomerId(1L);

        Account expectedCreatedAccount = new Account();
        expectedCreatedAccount.setId(1L);
        expectedCreatedAccount.setAccountNumber("1234567890");
        expectedCreatedAccount.setAccountType("Savings");
        expectedCreatedAccount.setBalance(BigDecimal.valueOf(1000.0));
        expectedCreatedAccount.setCustomerId(1L);

        when(accountRepository.save(accountToCreate)).thenReturn(expectedCreatedAccount);

        Account actualCreatedAccount = accountService.createAccount(accountToCreate);

        assertEquals(expectedCreatedAccount, actualCreatedAccount);
        verify(accountRepository, times(1)).save(accountToCreate);
    }
}