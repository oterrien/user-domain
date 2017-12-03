package com.ote.user.api.exception;

import com.ote.user.api.PerimeterPath;

public class PerimeterPathNotFoundException extends Exception {

    public PerimeterPathNotFoundException(String application, PerimeterPath perimeterPath) {
        super("Perimeter path " + perimeterPath.toString() + " is not defined for application " + application);
    }
}
