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

    boolean existsByEmail(String email);
    int countByUser(User user);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM EmailData e " +
            "WHERE e.email = :email AND e.user.id <> :userId")
    boolean existsByEmailAndOtherUser(
            @Param("email") String email,
            @Param("userId") Long userId
    );
}

