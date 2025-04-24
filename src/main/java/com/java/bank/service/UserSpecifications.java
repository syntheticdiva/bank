package com.java.bank.service;

import com.java.bank.entity.EmailData;
import com.java.bank.entity.PhoneData;
import com.java.bank.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecifications {

    public static Specification<User> hasNameStartingWith(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(root.get("name"), name + "%");
    }

    public static Specification<User> bornAfterOrEqual(LocalDate date) {
        return (root, query, cb) ->
                date == null ? null :
                        cb.greaterThanOrEqualTo(root.get("dateOfBirth"), date);
    }
    public static Specification<User> hasExactEmail(String email) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<User, EmailData> emailJoin = root.join("emails");
            return cb.equal(emailJoin.get("email"), email);
        };
    }

    public static Specification<User> hasExactPhone(String phone) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<User, PhoneData> phoneJoin = root.join("phones");
            return cb.equal(phoneJoin.get("phone"), phone);
        };
    }

}

