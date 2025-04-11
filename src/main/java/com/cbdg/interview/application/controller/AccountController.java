package com.cbdg.interview.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Accounts", description = "Account management APIs")
@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create a new account", description = "Creates a new account with the provided details")
    @PostMapping
    //@PreAuthorize("hasAuthority('SCOPE_write')")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @Operation(summary = "Get account details", description = "Retrieves the details of an account by its ID")
    @GetMapping("/{id}")
    //@PreAuthorize("hasAuthority('SCOPE_read')")
    public ResponseEntity<Account> getAccountDetails(@PathVariable Long id) {
        Account account = accountService.getAccountDetails(id);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Get accounts by customer ID", description = "Retrieves all accounts associated with a customer ID")
    @GetMapping("/customer/{customerId}")
    //@PreAuthorize("hasAuthority('SCOPE_read')")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable Long customerId) {
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(accounts);
    }
}