package com.ote.user.api.exception;

public class ApplicationNotFoundException extends Exception {

    public ApplicationNotFoundException(String application) {
        super("Application " + application + " has not been found");
    }

    public ApplicationNotFoundException(String user, String application) {
        super("Application " + application + " is not defined for user " + user);
    }
}
