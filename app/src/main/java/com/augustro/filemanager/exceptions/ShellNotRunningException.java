package com.augustro.filemanager.exceptions;


public class ShellNotRunningException extends Exception {
    public ShellNotRunningException() {
        super("Shell stopped running!");
    }
}
