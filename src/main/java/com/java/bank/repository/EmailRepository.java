package com.java.bank.repository;

import com.java.bank.entity.EmailData;
import com.java.bank.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = "emails")
public interface EmailRepository extends JpaRepository<EmailData, Long> {
    @Cacheable(key = "'email_exists:' + #email", unless = "#result == false")
    boolean existsByEmail(String email);

    int countByUser(User user);
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmailData e WHERE e.email = :email AND e.user.id <> :userId")
    @Cacheable(key = "'email_exists_except_user:' + #email + ':' + #userId", unless = "#result == false")
    boolean existsByEmailAndOtherUser(@Param("email") String email, @Param("userId") Long userId);

    @Override
    @CacheEvict(key = "'email_exists:' + #entity.email")
    <S extends EmailData> S save(S entity);

    @Override
    @CacheEvict(key = "'email_exists:' + #entity.email")
    void delete(EmailData entity);
}

