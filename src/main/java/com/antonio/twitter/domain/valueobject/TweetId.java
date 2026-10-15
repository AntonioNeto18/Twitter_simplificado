package com.antonio.twitter.domain.valueobject;

import java.util.UUID;

public record TweetId(UUID value) {
    public TweetId {
        if (value == null) {
            throw new IllegalArgumentException("TweetId cannot be null");
        }
    }

    public TweetId() {
        this(UUID.randomUUID());
    }

    public TweetId(String value) {
        this(UUID.fromString(value));
    }
}
