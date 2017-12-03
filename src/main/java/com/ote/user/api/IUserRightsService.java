package com.ote.user.api;

import com.ote.user.api.exception.ApplicationNotFoundException;
import com.ote.user.api.exception.PerimeterPathNotFoundException;
import com.ote.user.api.exception.UserNotFoundException;
import com.ote.user.api.model.Privilege;

import java.util.List;
import java.util.Objects;

public interface IUserRightsService {

    List<Privilege> getPrivileges(String user, String application, PerimeterPath perimeterPath) throws UserNotFoundException, ApplicationNotFoundException, PerimeterPathNotFoundException;

    default boolean hasPrivilege(String privilege, String user, String application, PerimeterPath perimeterPath) throws UserNotFoundException, ApplicationNotFoundException, PerimeterPathNotFoundException {
        return getPrivileges(user, application, perimeterPath).stream().anyMatch(p -> Objects.equals(p.getCode(), privilege));
    }
}
