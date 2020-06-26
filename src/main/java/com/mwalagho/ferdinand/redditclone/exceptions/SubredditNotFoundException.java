package com.mwalagho.ferdinand.redditclone.exceptions;

public class SubredditNotFoundException extends RuntimeException {
    public SubredditNotFoundException(String message) {
        super(message);
    }
}
