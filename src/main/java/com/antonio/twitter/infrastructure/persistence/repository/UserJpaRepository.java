package com.antonio.twitter.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.antonio.twitter.infrastructure.persistence.entity.UserJpaEntity;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    boolean existsByUsername(String username);
    Optional<UserJpaEntity> findByUsername(String username);
}
