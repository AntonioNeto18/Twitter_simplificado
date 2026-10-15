package com.antonio.twitter.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.antonio.twitter.domain.enums.Role;
import com.antonio.twitter.domain.enums.UserStatus;
import com.antonio.twitter.domain.valueobject.Content;
import com.antonio.twitter.domain.valueobject.Password;
import com.antonio.twitter.domain.valueobject.TweetId;
import com.antonio.twitter.domain.valueobject.UserId;
import com.antonio.twitter.domain.valueobject.Username;

class TweetTest {

    private static User user(UserStatus status) {
        return User.reconstitute(new UserId(), new Username("antonio"), null, new Password("secret"), Role.USER, status);
    }

    private static User activeUser() {
        return user(UserStatus.ACTIVE);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("cria tweet com id gerado e referência ao autor por id")
        void createsTweet() {
            User author = activeUser();

            Tweet tweet = Tweet.create(new Content("olá"), author);

            assertThat(tweet.getId()).isNotNull();
            assertThat(tweet.getContent()).isEqualTo(new Content("olá"));
            assertThat(tweet.getAuthorId()).isEqualTo(author.getId());
            assertThat(tweet.isAuthoredBy(author)).isTrue();
        }

        @Test
        @DisplayName("rejeita conteúdo ou autor nulos")
        void rejectsNulls() {
            assertThatThrownBy(() -> Tweet.create(null, activeUser())).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Tweet.create(new Content("olá"), null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("autor banido ou inativo não pode tweetar")
        void nonActiveAuthorCannotTweet() {
            assertThatThrownBy(() -> Tweet.create(new Content("olá"), user(UserStatus.BANNED)))
                .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> Tweet.create(new Content("olá"), user(UserStatus.INACTIVE)))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute")
    class Reconstitute {

        @Test
        @DisplayName("preserva todos os campos")
        void preservesFields() {
            TweetId id = new TweetId();
            UserId authorId = new UserId();

            Tweet tweet = Tweet.reconstitute(id, new Content("olá"), authorId);

            assertThat(tweet.getId()).isEqualTo(id);
            assertThat(tweet.getContent()).isEqualTo(new Content("olá"));
            assertThat(tweet.getAuthorId()).isEqualTo(authorId);
        }

        @Test
        @DisplayName("rejeita campos nulos")
        void rejectsNulls() {
            assertThatThrownBy(() -> Tweet.reconstitute(null, new Content("olá"), new UserId()))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Tweet.reconstitute(new TweetId(), null, new UserId()))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Tweet.reconstitute(new TweetId(), new Content("olá"), null))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("editContent")
    class EditContent {

        @Test
        @DisplayName("autor edita o conteúdo")
        void authorEdits() {
            User author = activeUser();
            Tweet tweet = Tweet.create(new Content("olá"), author);

            tweet.editContent(new Content("editado"), author);

            assertThat(tweet.getContent()).isEqualTo(new Content("editado"));
        }

        @Test
        @DisplayName("outro usuário não pode editar")
        void otherUserCannotEdit() {
            Tweet tweet = Tweet.create(new Content("olá"), activeUser());

            assertThatThrownBy(() -> tweet.editContent(new Content("hack"), activeUser()))
                .isInstanceOf(IllegalStateException.class);
            assertThat(tweet.getContent()).isEqualTo(new Content("olá"));
        }

        @Test
        @DisplayName("autor banido não pode editar")
        void bannedAuthorCannotEdit() {
            User author = activeUser();
            Tweet tweet = Tweet.create(new Content("olá"), author);
            User admin = User.reconstitute(new UserId(), new Username("admin"), null, new Password("pw"), Role.ADMIN, UserStatus.ACTIVE);
            author.ban(admin);

            assertThatThrownBy(() -> tweet.editContent(new Content("editado"), author))
                .isInstanceOf(IllegalStateException.class);
            assertThat(tweet.getContent()).isEqualTo(new Content("olá"));
        }

        @Test
        @DisplayName("rejeita conteúdo ou editor nulos")
        void rejectsNulls() {
            User author = activeUser();
            Tweet tweet = Tweet.create(new Content("olá"), author);

            assertThatThrownBy(() -> tweet.editContent(null, author)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tweet.editContent(new Content("x"), null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("igualdade")
    class Equality {

        @Test
        @DisplayName("tweets com o mesmo id são iguais")
        void equalById() {
            TweetId id = new TweetId();
            Tweet a = Tweet.reconstitute(id, new Content("a"), new UserId());
            Tweet b = Tweet.reconstitute(id, new Content("b"), new UserId());

            assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        }

        @Test
        @DisplayName("tweets com ids diferentes não são iguais")
        void differentIds() {
            User author = activeUser();

            assertThat(Tweet.create(new Content("a"), author)).isNotEqualTo(Tweet.create(new Content("a"), author));
        }
    }
}
