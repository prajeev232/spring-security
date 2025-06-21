package com.prajeev.secureweb.repositories;

import com.prajeev.secureweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authorities WHERE u.username = :username")
    Optional<User> findByUsernameWithAuthorities(@Param("username") String username);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
