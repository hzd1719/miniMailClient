package org.example.exceptions;

public class FolderNotFoundException extends RuntimeException{
    public FolderNotFoundException() {
    }

    public FolderNotFoundException(String message) {
        super(message);
    }

    public FolderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
