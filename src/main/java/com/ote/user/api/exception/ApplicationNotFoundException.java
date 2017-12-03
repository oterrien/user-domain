package com.ote.user.api.exception;

import com.ote.user.api.model.Application;

public class ApplicationNotFoundException extends Exception {

    private static final String APP_NOT_FOUND_MESSAGE = "Application '%s' not found";

    public ApplicationNotFoundException(Application application) {
        super(String.format(APP_NOT_FOUND_MESSAGE, application.getCode()));
    }
}
