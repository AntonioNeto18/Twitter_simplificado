package com.antonio.twitter.domain.valueobject;

public record Password(String value) {

    public Password {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
    }
}
