package com.cbdg.interview.application.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Account number is required")
    @Column(unique = true, nullable = false)
    private String accountNumber;

    @NotBlank(message = "Account type is required")
    @Column(nullable = false)
    private String accountType;

    @Positive(message = "Balance must be positive")
    @Column(nullable = false)
    private BigDecimal balance;

    @Positive(message = "Customer ID must be positive")
    @Column(nullable = false)
    private Long customerId;
}