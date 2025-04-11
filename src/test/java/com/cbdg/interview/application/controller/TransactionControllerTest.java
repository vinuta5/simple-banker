package com.cbdg.interview.application.controller;

import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.model.Transaction;
import com.cbdg.interview.application.service.TransactionService;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(TestSecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "SCOPE_write")
    void createTransaction_shouldReturnCreatedTransaction() throws Exception {
        Transaction transactionToCreate = createTransaction(null, 1L, 2L, BigDecimal.valueOf(100));
        Transaction createdTransaction = createTransaction(1L, 1L, 2L, BigDecimal.valueOf(100));

        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(createdTransaction);

        mockMvc.perform(post("/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionToCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdTransaction)));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_read")
    void getMonthlyStatement_shouldReturnTransactions() throws Exception {
        Long accountId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 5, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 5, 31, 23, 59);

        List<Transaction> transactions = Arrays.asList(
                createTransaction(1L, accountId, 2L, BigDecimal.valueOf(100)),
                createTransaction(2L, 2L, accountId, BigDecimal.valueOf(50))
        );

        when(transactionService.getMonthlyStatement(accountId, startDate, endDate)).thenReturn(transactions);

        mockMvc.perform(get("/v1/transactions/statement/{accountId}", accountId)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactions)));
    }

    @Test
    @WithMockUser(authorities = "SCOPE_write")
    void transferMoney_shouldReturnTransaction() throws Exception {
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = BigDecimal.valueOf(100);

        Transaction transaction = createTransaction(1L, fromAccountId, toAccountId, amount);

        when(transactionService.transferMoney(fromAccountId, toAccountId, amount)).thenReturn(transaction);

        mockMvc.perform(post("/v1/transactions/transfer")
                        .param("fromAccountId", fromAccountId.toString())
                        .param("toAccountId", toAccountId.toString())
                        .param("amount", amount.toString()))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(transaction)));
    }

    @Test
    void unauthorizedAccess_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/transactions/statement/1"))
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
        return account;
    }
}