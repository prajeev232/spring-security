package com.prajeev.secureweb.repositories;

import com.prajeev.secureweb.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Set<Authority> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
