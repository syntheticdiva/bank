package com.java.bank.repository;

import com.java.bank.entity.PhoneData;
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
@CacheConfig(cacheNames = "phones")
public interface PhoneRepository extends JpaRepository<PhoneData, Long> {
    @Cacheable(key = "'phone_exists:' + #phone", unless = "#result == false")
    boolean existsByPhone(String phone);
    @Query("SELECT COUNT(p) FROM PhoneData p WHERE p.user = :user")
    int countByUser(@Param("user") User user);
    @Cacheable(key = "'phone_exists_except_user:' + #phone + ':' + #user.id", unless = "#result == false")
    boolean existsByPhoneAndUserNot(String phone, User user);

    @Override
    @CacheEvict(key = "'phone_exists:' + #entity.phone")
    <S extends PhoneData> S save(S entity);

    @Override
    @CacheEvict(key = "'phone_exists:' + #entity.phone")
    void delete(PhoneData entity);
}
