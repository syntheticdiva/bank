package com.java.bank.service;

import com.java.bank.entity.EmailData;
import com.java.bank.entity.PhoneData;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;

public class UserSpecification {
    public static Specification<User> hasNameStartingWith(String name) {
        return (root, query, cb) ->
                name != null ? cb.like(cb.lower(root.get("name")), name.toLowerCase() + "%") : null;
    }

    public static Specification<User> bornAfter(LocalDate date) {
        return (root, query, cb) ->
                date != null ? cb.greaterThan(root.get("dateOfBirth"), date) : null;
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null) return null;
            Join<User, EmailData> emailJoin = root.join("emails");
            return cb.equal(emailJoin.get("email"), email);
        };
    }

    public static Specification<User> hasPhone(String phone) {
        return (root, query, cb) -> {
            if (phone == null) return null;
            Join<User, PhoneData> phoneJoin = root.join("phones");
            return cb.equal(phoneJoin.get("phone"), phone);
        };
    }
}
