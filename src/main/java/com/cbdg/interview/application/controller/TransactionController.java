package com.cbdg.interview.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.cbdg.interview.application.model.Transaction;
import com.cbdg.interview.application.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Transactions", description = "Transaction management APIs")
@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "createTransaction", description = "Creates a new transaction")
    @PostMapping
    //@PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @Operation(summary = "getMonthlyStatement", description = "Gets the monthly statements of all the transactions on the account")
    @GetMapping("/statement/{accountId}")
    //@PreAuthorize("hasAuthority('SCOPE_read')")
    public ResponseEntity<List<Transaction>> getMonthlyStatement(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.getMonthlyStatement(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "transferMoney", description = "Transfers money from one account to another account")
    @PostMapping("/transfer")
    //@PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<Transaction> transferMoney(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {
        Transaction transaction = transactionService.transferMoney(fromAccountId, toAccountId, amount);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
}