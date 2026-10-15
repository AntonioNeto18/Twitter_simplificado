package com.antonio.twitter.domain.valueobject;

public record Biography(String value) {

    private static final int MAX_LENGTH = 160;

    public Biography {
        if (value != null && value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Biography cannot exceed " + MAX_LENGTH + " characters");
        }
    }

}
