package com.mwalagho.ferdinand.redditclone.exceptions;

/**
 * Exception class abstracts error message to the end user
 */

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String exMessage) {
        super(exMessage);
    }
}
