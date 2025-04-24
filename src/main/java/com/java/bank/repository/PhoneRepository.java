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
    Optional<PhoneData> findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<PhoneData> findByUser(User user);

    @Query("SELECT COUNT(p) FROM PhoneData p WHERE p.user = :user")
    int countByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM PhoneData p WHERE p.user = :user AND p.phone = :phone")
    void deleteByUserAndPhone(@Param("user") User user,
                              @Param("phone") String phone);
    boolean existsByPhoneAndUserNot(String phone, User user);
    Optional<PhoneData> findByPhoneAndUser(String phone, User user);
}
