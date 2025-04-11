package com.cbdg.interview.application.controller;

import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(TestSecurityConfig.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "SCOPE_read")
    void getAccountsByCustomerId_shouldReturnAccounts() throws Exception {
        Long customerId = 1L;
        List<Account> accounts = Arrays.asList(
                createAccount(1L, "123456", "Savings", 1000.0, customerId),
                createAccount(2L, "789012", "Checking", 2000.0, customerId)
        );
        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(accounts);

        mockMvc.perform(get("/v1/accounts/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read")
    void getAccountDetails_shouldReturnAccount() throws Exception {
        Long accountId = 1L;
        Account account = createAccount(accountId, "123456", "Savings", 1000.0, 1L);
        when(accountService.getAccountDetails(accountId)).thenReturn(account);

        mockMvc.perform(get("/v1/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(account)));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_write")
    void createAccount_shouldReturnCreatedAccount() throws Exception {
        Account accountToCreate = createAccount(null, "123456", "Savings", 1000.0, 1L);
        Account createdAccount = createAccount(1L, "123456", "Savings", 1000.0, 1L);

        when(accountService.createAccount(any(Account.class))).thenReturn(createdAccount);

        mockMvc.perform(post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountToCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdAccount)));
    }

    @Test
    void unauthorizedAccess_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/accounts/1"))
                .andExpect(status().isUnauthorized());
    }

    private Account createAccount(Long id, String accountNumber, String accountType, Double balance, Long customerId) {
        Account account = new Account();
        account.setId(id);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.valueOf(balance));
        account.setCustomerId(customerId);
        return account;
    }
}