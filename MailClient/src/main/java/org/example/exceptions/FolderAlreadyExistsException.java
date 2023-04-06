package org.example.exceptions;

public class FolderAlreadyExistsException extends RuntimeException{
    public FolderAlreadyExistsException() {
    }

    public FolderAlreadyExistsException(String message) {
        super(message);
    }

    public FolderAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
