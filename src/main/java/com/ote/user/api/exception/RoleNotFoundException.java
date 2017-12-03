package com.ote.user.api.exception;

import com.ote.user.api.PerimeterPath;
import com.ote.user.api.model.Application;
import com.ote.user.api.model.User;

public class RoleNotFoundException extends Exception {

    private static final String ROLE_APP_NOT_FOUND_MESSAGE = "Role not found for user '%s' and application '%s'";
    private static final String ROLE_APP_PATH_NOT_FOUND_MESSAGE = "Role not found for user '%s' and application '%s' and perimeters '%s'";

    public RoleNotFoundException(User user, Application application) {
        super(String.format(ROLE_APP_NOT_FOUND_MESSAGE, user.getLogin(), application.getCode()));
    }

    public RoleNotFoundException(User user, Application application, PerimeterPath perimeterPath) {
        super(String.format(ROLE_APP_PATH_NOT_FOUND_MESSAGE, user.getLogin(), application.getCode(), perimeterPath.toString()));
    }
}
