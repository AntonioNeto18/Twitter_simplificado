package com.antonio.twitter.domain.valueobject;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
    }

    public UserId() {
        this(UUID.randomUUID());
    }

    public UserId(String value) {
        this(UUID.fromString(value));
    }

}
