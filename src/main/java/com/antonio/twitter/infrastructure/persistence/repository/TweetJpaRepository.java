package com.antonio.twitter.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.antonio.twitter.infrastructure.persistence.entity.TweetJpaEntity;

public interface TweetJpaRepository extends JpaRepository<TweetJpaEntity, UUID> {
    boolean existsByAuthorId(UUID authorId);
    List<TweetJpaEntity> findByAuthorId(UUID value);    
}
