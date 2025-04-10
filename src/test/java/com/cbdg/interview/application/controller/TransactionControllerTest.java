package com.cbdg.interview.application.controller;

import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.model.Transaction;
import com.cbdg.interview.application.service.TransactionService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String VALID_TOKEN = "valid_token";

    @BeforeEach
    void setUp() {
        when(tokenService.validateToken(VALID_TOKEN)).thenReturn(true);
    }

    @Test
    void createTransaction_shouldReturnCreatedTransaction() throws Exception {
        Transaction transactionToCreate = createTransaction(null, 1L, 2L, BigDecimal.valueOf(100));
        Transaction createdTransaction = createTransaction(1L, 1L, 2L, BigDecimal.valueOf(100));

        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(createdTransaction);

        Map<String, Object> requestBody = new HashMap<>(objectMapper.convertValue(transactionToCreate, Map.class));
        requestBody.put("token", VALID_TOKEN);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(createdTransaction)));
    }

    @Test
    void getMonthlyStatement_shouldReturnTransactions() throws Exception {
        Long accountId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 5, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 5, 31, 23, 59);

        List<Transaction> transactions = Arrays.asList(
                createTransaction(1L, accountId, 2L, BigDecimal.valueOf(100)),
                createTransaction(2L, 2L, accountId, BigDecimal.valueOf(50))
        );

        when(transactionService.getMonthlyStatement(accountId, startDate, endDate)).thenReturn(transactions);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("token", VALID_TOKEN);
        requestBody.put("startDate", startDate.toString());
        requestBody.put("endDate", endDate.toString());

        mockMvc.perform(post("/api/transactions/statement/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactions)));
    }

    @Test
    void transferMoney_shouldReturnTransaction() throws Exception {
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        Transaction transaction = createTransaction(1L, fromAccountId, toAccountId, amount);

        when(transactionService.transferMoney(fromAccountId, toAccountId, amount)).thenReturn(transaction);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("token", VALID_TOKEN);
        requestBody.put("fromAccountId", fromAccountId);
        requestBody.put("toAccountId", toAccountId);
        requestBody.put("amount", amount);

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transaction)));
    }

    @Test
    void invalidToken_shouldReturnUnauthorized() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", "invalid_token");

        mockMvc.perform(post("/api/transactions/statement/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isUnauthorized());
    }

    private Transaction createTransaction(Long id, Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setFromAccount(createAccount(fromAccountId));
        transaction.setToAccount(createAccount(toAccountId));
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        return transaction;
    }

    private Account createAccount(Long id) {
        Account account = new Account();
        account.setId(id);
        // Set other necessary fields
        return account;
    }
}