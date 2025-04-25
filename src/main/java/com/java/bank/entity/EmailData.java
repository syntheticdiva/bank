package com.java.bank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "email_data")
@Getter
@Setter
@ToString
public class EmailData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "email", length = 200, nullable = false, unique = true)
    @Email
    private String email;
    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailData)) return false;
        return id != null && id.equals(((EmailData) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
