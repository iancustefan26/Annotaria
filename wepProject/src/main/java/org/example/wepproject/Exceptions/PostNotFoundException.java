package org.example.wepproject.Exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
