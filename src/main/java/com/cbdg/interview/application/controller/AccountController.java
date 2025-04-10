package com.cbdg.interview.application.controller;

import com.cbdg.interview.application.model.Account;
import com.cbdg.interview.application.service.AccountService;
import com.cbdg.interview.application.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(
            @PathVariable Long customerId,
            @RequestBody Map<String, String> request) {
        if (!tokenService.validateToken(request.get("token"))) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
    }

    @PostMapping("/{accountId}")
    public ResponseEntity<Account> getAccountDetails(
            @PathVariable Long accountId,
            @RequestBody Map<String, String> request) {
        if (!tokenService.validateToken(request.get("token"))) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(accountService.getAccountDetails(accountId));
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Map<String, Object> request) {
        if (!tokenService.validateToken((String) request.get("token"))) {
            return ResponseEntity.status(401).build();
        }
        Account account = new Account();
        // Set account properties from request
        return ResponseEntity.ok(accountService.createAccount(account));
    }
}