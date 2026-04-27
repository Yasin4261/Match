package com.match.adapter.persistence.repository;

import com.match.adapter.persistence.jpa.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSpringDataRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}

