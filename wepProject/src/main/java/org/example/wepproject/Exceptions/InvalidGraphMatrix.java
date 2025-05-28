package org.example.wepproject.Exceptions;

public class InvalidGraphMatrix extends RuntimeException {
    public InvalidGraphMatrix(String message) {
        super("Invalid graph matrix: " + message);
    }
}
