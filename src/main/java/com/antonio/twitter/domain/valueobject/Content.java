package com.antonio.twitter.domain.valueobject;

public record Content(String value) {

    private static final int MAX_LENGTH = 280;

    public Content {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or blank");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Content cannot exceed " + MAX_LENGTH + " characters");
        }
    }

}
