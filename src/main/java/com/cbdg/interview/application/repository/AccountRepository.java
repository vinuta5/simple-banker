package com.cbdg.interview.application.repository;

import com.cbdg.interview.application.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);
}

