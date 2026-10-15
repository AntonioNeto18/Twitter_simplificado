package com.antonio.twitter.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.antonio.twitter.domain.entity.User;
import com.antonio.twitter.domain.enums.Role;
import com.antonio.twitter.domain.enums.UserStatus;
import com.antonio.twitter.domain.valueobject.Biography;
import com.antonio.twitter.domain.valueobject.Password;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.domain.valueobject.Username;
import com.antonio.twitter.infrastructure.persistence.entity.UserJpaEntity;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    private static UserJpaEntity jpaEntity(UUID id, String username, String biography) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(id);
        entity.setUsername(username);
        entity.setBiography(biography);
        entity.setPassword("secret");
        entity.setRole(Role.ADMIN);
        entity.setStatus(UserStatus.BANNED);
        return entity;
    }

    @Nested
    @DisplayName("toJpa")
    class ToJpa {

        @Test
        @DisplayName("copia todos os campos do domínio")
        void copiesAllFields() {
            UserId id = new UserId();
            User user = User.reconstitute(id, new Username("antonio"), new Biography("dev"), new Password("secret"), Role.ADMIN, UserStatus.INACTIVE);

            UserJpaEntity entity = mapper.toJpa(user);

            assertThat(entity.getId()).isEqualTo(id.value());
            assertThat(entity.getUsername()).isEqualTo("antonio");
            assertThat(entity.getBiography()).isEqualTo("dev");
            assertThat(entity.getPassword()).isEqualTo("secret");
            assertThat(entity.getRole()).isEqualTo(Role.ADMIN);
            assertThat(entity.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        @DisplayName("biografia nula vira coluna nula")
        void nullBiography() {
            User user = User.create(new Username("antonio"), null, new Password("secret"));

            assertThat(mapper.toJpa(user).getBiography()).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("reconstrói todos os campos sem aplicar defaults de criação")
        void rebuildsAllFields() {
            UUID id = UUID.randomUUID();

            User user = mapper.toDomain(jpaEntity(id, "antonio", "dev"));

            assertThat(user.getId()).isEqualTo(new UserId(id));
            assertThat(user.getUsername()).isEqualTo(new Username("antonio"));
            assertThat(user.getBiography()).isEqualTo(new Biography("dev"));
            assertThat(user.getPassword()).isEqualTo(new Password("secret"));
            assertThat(user.getRole()).isEqualTo(Role.ADMIN);
            assertThat(user.getStatus()).isEqualTo(UserStatus.BANNED);
        }

        @Test
        @DisplayName("coluna de biografia nula vira biografia nula")
        void nullBiography() {
            User user = mapper.toDomain(jpaEntity(UUID.randomUUID(), "antonio", null));

            assertThat(user.getBiography()).isNull();
        }

        @Test
        @DisplayName("dado inválido vindo do banco falha na reconstrução")
        void rejectsInvalidData() {
            assertThatThrownBy(() -> mapper.toDomain(jpaEntity(null, "antonio", null)))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> mapper.toDomain(jpaEntity(UUID.randomUUID(), " ", null)))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("ida e volta domínio → jpa → domínio preserva os dados")
    void roundTrip() {
        User original = User.reconstitute(new UserId(), new Username("antonio"), new Biography("dev"), new Password("secret"), Role.ADMIN, UserStatus.BANNED);

        User result = mapper.toDomain(mapper.toJpa(original));

        assertThat(result).isEqualTo(original);
        assertThat(result).usingRecursiveComparison().isEqualTo(original);
    }
}
