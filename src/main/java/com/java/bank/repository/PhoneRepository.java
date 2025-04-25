package com.java.bank.repository;

import com.java.bank.entity.PhoneData;
import com.java.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhoneRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhone(String phone);
    @Query("SELECT COUNT(p) FROM PhoneData p WHERE p.user = :user")
    int countByUser(@Param("user") User user);
    boolean existsByPhoneAndUserNot(String phone, User user);
}
