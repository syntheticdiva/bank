package com.java.bank.repository;



import com.java.bank.entity.User;
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

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.name = :name")
    boolean existsByName(@Param("name") String name);

    // Для спецификаций
    default List<User> findAll(Specification<User> spec) {
        return findAll(spec, Sort.unsorted());
    }
//    Optional<User> findByEmail(String email);


    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.emails " +
            "LEFT JOIN FETCH u.phones " +
            "WHERE u.id = :userId")
    Optional<User> findUserWithDetails(@Param("userId") Long userId);

    @Query("SELECT u FROM User u " +
            "JOIN FETCH u.emails e " +
            "WHERE e.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

//    @Query("SELECT u FROM User u " +
//            "LEFT JOIN FETCH u.emails " +
//            "LEFT JOIN FETCH u.phones " +
//            "WHERE EXISTS " +
//            "(SELECT e FROM EmailData e WHERE e.user = u AND e.email = :login) OR " +
//            "EXISTS (SELECT p FROM PhoneData p WHERE p.user = u AND p.phone = :login)")
//    Optional<User> findByEmailOrPhone(@Param("login") String login);
    @Query("SELECT u FROM User u WHERE EXISTS " +
            "(SELECT e FROM EmailData e WHERE e.user = u AND e.email = :credential) OR " +
            "EXISTS (SELECT p FROM PhoneData p WHERE p.user = u AND p.phone = :credential)")
    Optional<User> findByEmailOrPhone(@Param("credential") String credential);

    @EntityGraph(attributePaths = {"emails", "phones"})
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findUserWithContacts(@Param("userId") Long userId);

    @Query("SELECT COUNT(e) > 0 FROM EmailData e WHERE e.email = :email AND e.user.id <> :userId")
    boolean existsByEmailAndNotUser(@Param("email") String email,
                                    @Param("userId") Long userId);

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



