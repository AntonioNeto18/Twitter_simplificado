package com.antonio.twitter.domain.repository;

import java.util.List;
import java.util.Optional;

public interface TwitterRepository<T, ID> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    void delete(T entity);
}
