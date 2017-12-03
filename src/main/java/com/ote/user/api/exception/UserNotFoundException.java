package com.ote.user.api.exception;

import com.ote.user.api.model.User;

public class UserNotFoundException extends Exception {

    private static final String USR_NOT_FOUND_MESSAGE = "User '%s' not found";

    public UserNotFoundException(User user) {
        super(String.format(USR_NOT_FOUND_MESSAGE, user.getLogin()));
    }
}
