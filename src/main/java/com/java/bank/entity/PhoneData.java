package com.java.bank.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "phone_data")
@Getter
@Setter
@ToString
public class PhoneData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "phone", length = 12, nullable = false, unique = true)
    private String phone;
    @Column(name = "is_primary", nullable = false)
    private boolean primary;
}
