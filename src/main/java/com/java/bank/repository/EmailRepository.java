package com.java.bank.repository;

import com.java.bank.entity.EmailData;
import com.java.bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailData, Long> {
    Optional<EmailData> findByEmail(String email);

    boolean existsByEmail(String email);

    List<EmailData> findByUser(User user);

    @Query("SELECT COUNT(e) FROM EmailData e WHERE e.user = :user")
    int countByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM EmailData e WHERE e.user = :user AND e.email = :email")
    void deleteByUserAndEmail(@Param("user") User user,
                              @Param("email") String email);
}
