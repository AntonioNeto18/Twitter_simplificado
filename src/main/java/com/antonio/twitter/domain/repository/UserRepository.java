package com.antonio.twitter.domain.repository;

import java.util.Optional;

import com.antonio.twitter.domain.entity.User;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.domain.valueobject.Username;

public interface UserRepository extends TwitterRepository<User, UserId> {
    Optional<User> findByUsername(Username username);
    boolean existsByUsername(Username username);
}
