package com.antonio.twitter.domain.valueobject;

public record Username(String value) {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 15;

    public Username {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        value = value.trim();
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Username must have between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters");
        }
    }

}
