package com.cbdg.interview.application.service;

import com.cbdg.interview.application.model.Account;
import java.util.List;

public interface AccountService {
    List<Account> getAccountsByCustomerId(Long customerId);
    Account getAccountDetails(Long accountId);
    Account createAccount(Account account);
}