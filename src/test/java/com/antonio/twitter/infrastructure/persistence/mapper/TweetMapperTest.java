package com.antonio.twitter.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.antonio.twitter.domain.entity.Tweet;
import com.antonio.twitter.domain.valueobject.Content;
import com.antonio.twitter.domain.valueobject.TweetId;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.infrastructure.persistence.entity.TweetJpaEntity;

class TweetMapperTest {

    private final TweetMapper mapper = new TweetMapper();

    private static TweetJpaEntity jpaEntity(UUID id, String content, UUID authorId) {
        TweetJpaEntity entity = new TweetJpaEntity();
        entity.setId(id);
        entity.setContent(content);
        entity.setAuthorId(authorId);
        return entity;
    }

    @Nested
    @DisplayName("toJpa")
    class ToJpa {

        @Test
        @DisplayName("copia todos os campos do domínio")
        void copiesAllFields() {
            TweetId id = new TweetId();
            UserId authorId = new UserId();
            Tweet tweet = Tweet.reconstitute(id, new Content("olá"), authorId);

            TweetJpaEntity entity = mapper.toJpa(tweet);

            assertThat(entity.getId()).isEqualTo(id.value());
            assertThat(entity.getContent()).isEqualTo("olá");
            assertThat(entity.getAuthorId()).isEqualTo(authorId.value());
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("reconstrói todos os campos")
        void rebuildsAllFields() {
            UUID id = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();

            Tweet tweet = mapper.toDomain(jpaEntity(id, "olá", authorId));

            assertThat(tweet.getId()).isEqualTo(new TweetId(id));
            assertThat(tweet.getContent()).isEqualTo(new Content("olá"));
            assertThat(tweet.getAuthorId()).isEqualTo(new UserId(authorId));
        }

        @Test
        @DisplayName("dado inválido vindo do banco falha na reconstrução")
        void rejectsInvalidData() {
            assertThatThrownBy(() -> mapper.toDomain(jpaEntity(null, "olá", UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> mapper.toDomain(jpaEntity(UUID.randomUUID(), null, UUID.randomUUID())))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> mapper.toDomain(jpaEntity(UUID.randomUUID(), "olá", null)))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("ida e volta domínio → jpa → domínio preserva os dados")
    void roundTrip() {
        Tweet original = Tweet.reconstitute(new TweetId(), new Content("olá"), new UserId());

        Tweet result = mapper.toDomain(mapper.toJpa(original));

        assertThat(result).isEqualTo(original);
        assertThat(result).usingRecursiveComparison().isEqualTo(original);
    }
}
