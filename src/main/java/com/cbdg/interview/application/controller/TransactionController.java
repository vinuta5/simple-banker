package com.cbdg.interview.application.controller;

import com.cbdg.interview.application.model.Transaction;
import com.cbdg.interview.application.service.TransactionService;
import com.cbdg.interview.application.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Map<String, Object> request) {
        if (!tokenService.validateToken((String) request.get("token"))) {
            return ResponseEntity.status(401).build();
        }
        Transaction transaction = new Transaction();
        // Set transaction properties from request
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @PostMapping("/statement/{accountId}")
    public ResponseEntity<List<Transaction>> getMonthlyStatement(
            @PathVariable Long accountId,
            @RequestBody Map<String, Object> request) {
        if (!tokenService.validateToken((String) request.get("token"))) {
            return ResponseEntity.status(401).build();
        }
        LocalDateTime startDate = LocalDateTime.parse((String) request.get("startDate"));
        LocalDateTime endDate = LocalDateTime.parse((String) request.get("endDate"));
        return ResponseEntity.ok(transactionService.getMonthlyStatement(accountId, startDate, endDate));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transferMoney(@RequestBody Map<String, Object> request) {
        if (!tokenService.validateToken((String) request.get("token"))) {
            return ResponseEntity.status(401).build();
        }
        Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
        Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        return ResponseEntity.ok(transactionService.transferMoney(fromAccountId, toAccountId, amount));
    }
}