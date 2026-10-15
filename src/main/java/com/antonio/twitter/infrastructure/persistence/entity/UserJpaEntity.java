package com.antonio.twitter.infrastructure.persistence.entity;

import java.util.UUID;

import com.antonio.twitter.domain.enums.Role;
import com.antonio.twitter.domain.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_users")
public class UserJpaEntity {
    
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 15)
    private String username;

    @Column(length = 160)
    private String biography;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

}
