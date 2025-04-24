package com.java.bank.repository;



import com.java.bank.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE EXISTS " +
            "(SELECT e FROM EmailData e WHERE e.user = u AND e.email = :login) OR " +
            "EXISTS (SELECT p FROM PhoneData p WHERE p.user = u AND p.phone = :login)")
    Optional<User> findByEmailOrPhone(@Param("login") String login);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.name = :name")
    boolean existsByName(@Param("name") String name);

    // Для спецификаций
    default List<User> findAll(Specification<User> spec) {
        return findAll(spec, Sort.unsorted());
    }
//    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u JOIN u.emails e WHERE e.email = :email")
    Optional<User> findByEmail(@Param("email") String email);


}
