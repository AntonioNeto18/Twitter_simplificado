package com.antonio.twitter.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.antonio.twitter.domain.entity.User;
import com.antonio.twitter.domain.valueobject.Biography;
import com.antonio.twitter.domain.valueobject.Password;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.domain.valueobject.Username;
import com.antonio.twitter.infrastructure.persistence.entity.UserJpaEntity;

@Component
public class UserMapper {

    public UserJpaEntity toJpa(User user) {
        UserJpaEntity jpaEntity = new UserJpaEntity();
        jpaEntity.setId(user.getId().value());
        jpaEntity.setUsername(user.getUsername().value());
        jpaEntity.setBiography(user.getBiography() != null ? user.getBiography().value() : null);
        jpaEntity.setPassword(user.getPassword().value());
        jpaEntity.setRole(user.getRole());
        jpaEntity.setStatus(user.getStatus());

        return jpaEntity;
    }

    public User toDomain(UserJpaEntity jpaEntity) {
        return User.reconstitute(
            new UserId(jpaEntity.getId()),
            new Username(jpaEntity.getUsername()),
            jpaEntity.getBiography() != null ? new Biography(jpaEntity.getBiography()) : null,
            new Password(jpaEntity.getPassword()),
            jpaEntity.getRole(),
            jpaEntity.getStatus()
        );
    }

}
