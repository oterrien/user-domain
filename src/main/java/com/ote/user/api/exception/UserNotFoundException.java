package com.ote.user.api.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String user) {
        super("User " + user + " has not been found");
    }
}
