package com.java.bank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Column(name = "initial_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal initialBalance;

}
