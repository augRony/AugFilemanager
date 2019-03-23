package com.augustro.filemanager.exceptions;

public class StreamNotFoundException extends Exception {
    private static final String MESSAGE = "Can't get stream";

    public StreamNotFoundException() { super(MESSAGE); }
    public StreamNotFoundException(String message) { super(message); }
    public StreamNotFoundException(String message, Throwable cause) { super(message, cause); }
    public StreamNotFoundException(Throwable cause) { super(MESSAGE, cause); }
}
