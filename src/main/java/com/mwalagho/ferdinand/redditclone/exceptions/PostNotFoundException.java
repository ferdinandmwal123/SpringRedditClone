package com.mwalagho.ferdinand.redditclone.exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
}
