package com.cbdg.interview.application.controller;

import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.service.AccountService;
import com.cbdg.interview.application.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String VALID_TOKEN = "valid_token";

    @BeforeEach
    void setUp() {
        when(tokenService.validateToken(VALID_TOKEN)).thenReturn(true);
        when(tokenService.validateToken("invalid_token")).thenReturn(false);
    }

    @Test
    void getAccountsByCustomerId_withValidToken_shouldReturnAccounts() throws Exception {
        Long customerId = 1L;
        List<Account> accounts = Arrays.asList(
                createAccount(1L, "123456", "Savings", 1000.0, customerId),
                createAccount(2L, "789012", "Checking", 2000.0, customerId)
        );
        when(accountService.getAccountsByCustomerId(customerId)).thenReturn(accounts);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", VALID_TOKEN);

        mockMvc.perform(post("/api/accounts/customer/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accounts)));
    }

    @Test
    void getAccountDetails_withValidToken_shouldReturnAccount() throws Exception {
        Long accountId = 1L;
        Account account = createAccount(accountId, "123456", "Savings", 1000.0, 1L);
        when(accountService.getAccountDetails(accountId)).thenReturn(account);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", VALID_TOKEN);

        mockMvc.perform(post("/api/accounts/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(account)));
    }

    @Test
    void createAccount_withValidToken_shouldReturnCreatedAccount() throws Exception {
        Account accountToCreate = createAccount(null, "123456", "Savings", 1000.0, 1L);
        Account createdAccount = createAccount(1L, "123456", "Savings", 1000.0, 1L);

        when(accountService.createAccount(any(Account.class))).thenReturn(createdAccount);

        Map<String, Object> requestBody = new HashMap<>(objectMapper.convertValue(accountToCreate, Map.class));
        requestBody.put("token", VALID_TOKEN);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(createdAccount)));
    }

    @Test
    void anyEndpoint_withInvalidToken_shouldReturnForbidden() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", "invalid_token");

        mockMvc.perform(post("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isForbidden());
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