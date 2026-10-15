package com.antonio.twitter.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ValueObjectTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Content rejeita nulo ou vazio")
    void contentRejectsBlank(String value) {
        assertThatThrownBy(() -> new Content(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Content aceita até 280 caracteres")
    void contentMaxLength() {
        assertThat(new Content("a".repeat(280)).value()).hasSize(280);
        assertThatThrownBy(() -> new Content("a".repeat(281))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Biography aceita nulo e até 160 caracteres")
    void biographyMaxLength() {
        assertThat(new Biography(null).value()).isNull();
        assertThat(new Biography("a".repeat(160)).value()).hasSize(160);
        assertThatThrownBy(() -> new Biography("a".repeat(161))).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "ab", "abcdefghijklmnop"})
    @DisplayName("Username rejeita vazio, menos de 3 ou mais de 15 caracteres")
    void usernameRejectsInvalid(String value) {
        assertThatThrownBy(() -> new Username(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Username remove espaços nas pontas")
    void usernameTrims() {
        assertThat(new Username("  antonio  ")).isEqualTo(new Username("antonio"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Password rejeita nulo ou vazio")
    void passwordRejectsBlank(String value) {
        assertThatThrownBy(() -> new Password(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Ids rejeitam nulo e geram valores únicos")
    void ids() {
        assertThatThrownBy(() -> new UserId((UUID) null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TweetId((UUID) null)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new UserId()).isNotEqualTo(new UserId());
        assertThat(new TweetId()).isNotEqualTo(new TweetId());
    }
}
