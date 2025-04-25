package com.java.bank.repository;



import com.java.bank.entity.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = "users")
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = {"emails", "phones"})
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"emails", "phones"})
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.emails e LEFT JOIN FETCH u.phones p WHERE e.email = :credential OR p.phone = :credential")
    @Cacheable(key = "'user_by_credential:' + #credential", unless = "#result == null")
    Optional<User> findByEmailOrPhone(@Param("credential") String credential);

    @EntityGraph(attributePaths = {"emails"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    @Cacheable(key = "'user_with_emails:' + #userId", unless = "#result == null")
    Optional<User> findByIdWithEmails(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"emails"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findUserWithEmails(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"emails", "phones"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    @Cacheable(key = "'user_full:' + #userId", unless = "#result == null")
    Optional<User> findByIdWithEmailsAndPhones(@Param("userId") Long userId);

    @Override
    @Caching(
            evict = {
                    @CacheEvict(key = "'user_full:' + #entity.id"),
                    @CacheEvict(key = "'user_with_emails:' + #entity.id"),
                    @CacheEvict(key = "'user_by_credential:' + #entity.emails.![email]"),
                    @CacheEvict(key = "'user_by_credential:' + #entity.phones.![phone]")
            }
    )
    <S extends User> S save(S entity);

    @Override
    @Caching(
            evict = {
                    @CacheEvict(key = "'user_full:' + #entity.id"),
                    @CacheEvict(key = "'user_with_emails:' + #entity.id"),
                    @CacheEvict(key = "'user_by_credential:' + #entity.emails.![email]"),
                    @CacheEvict(key = "'user_by_credential:' + #entity.phones.![phone]")
            }
    )
    void delete(User entity);

}



