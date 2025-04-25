package com.java.bank.repository;



import com.java.bank.entity.User;
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
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = {"emails", "phones"})
    Page<User> findAll(Specification<User> spec, Pageable pageable);
    @EntityGraph(attributePaths = {"emails", "phones"})
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.emails e " +
            "LEFT JOIN FETCH u.phones p " +
            "WHERE e.email = :credential OR p.phone = :credential")
    Optional<User> findByEmailOrPhone(@Param("credential") String credential);
    @EntityGraph(attributePaths = {"emails"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdWithEmails(@Param("userId") Long userId);
    @EntityGraph(attributePaths = {"emails"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findUserWithEmails(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"emails", "phones"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdWithEmailsAndPhones(@Param("userId") Long userId);

}



