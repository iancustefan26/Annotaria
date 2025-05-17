package org.example.wepproject.Post;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
