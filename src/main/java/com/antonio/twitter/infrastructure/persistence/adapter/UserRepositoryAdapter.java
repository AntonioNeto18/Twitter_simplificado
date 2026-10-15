package com.antonio.twitter.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.antonio.twitter.domain.entity.User;
import com.antonio.twitter.domain.repository.UserRepository;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.domain.valueobject.Username;
import com.antonio.twitter.infrastructure.persistence.mapper.UserMapper;
import com.antonio.twitter.infrastructure.persistence.repository.UserJpaRepository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User entity) {
        return mapper.toDomain(userJpaRepository.save(mapper.toJpa(entity)));
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UserId id) {
        return userJpaRepository.existsById(id.value());
    }

    @Override
    public void delete(User entity) {
        userJpaRepository.delete(mapper.toJpa(entity));
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return userJpaRepository.findByUsername(username.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return userJpaRepository.existsByUsername(username.value());
    }

}
