package com.ote.user.api;

import com.ote.user.api.exception.ApplicationNotFoundException;
import com.ote.user.api.exception.RoleNotFoundException;
import com.ote.user.api.exception.UserNotFoundException;
import com.ote.user.api.model.Application;
import com.ote.user.api.model.Privilege;
import com.ote.user.api.model.User;

import java.util.List;
import java.util.Objects;

public interface IUserRightsService {

    List<Privilege> getPrivileges(User user, Application application, PerimeterPath perimeterPath) throws UserNotFoundException, ApplicationNotFoundException, RoleNotFoundException;

    default boolean hasPrivilege(Privilege privilege, User user, Application application, PerimeterPath perimeterPath) throws UserNotFoundException, ApplicationNotFoundException, RoleNotFoundException {
        return getPrivileges(user, application, perimeterPath).stream().anyMatch(p -> Objects.equals(p.getCode(), privilege.getCode()));
    }
}
