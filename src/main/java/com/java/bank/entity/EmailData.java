package com.java.bank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "email", length = 200, nullable = false, unique = true)
    @Email
    private String email;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EmailData emailData = (EmailData) object;
        return Objects.equals(email, emailData.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
