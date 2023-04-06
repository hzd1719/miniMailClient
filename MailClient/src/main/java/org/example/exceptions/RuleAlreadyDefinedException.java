package org.example.exceptions;

public class RuleAlreadyDefinedException extends RuntimeException{
    public RuleAlreadyDefinedException() {
    }

    public RuleAlreadyDefinedException(String message) {
        super(message);
    }

    public RuleAlreadyDefinedException(String message, Throwable cause) {
        super(message, cause);
    }
}
