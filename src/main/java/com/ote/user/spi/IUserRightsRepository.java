package com.ote.user.spi;

import com.ote.user.api.PerimeterPath;
import com.ote.user.api.model.Privilege;

import java.util.List;

public interface IUserRightsRepository {

    boolean isUserDefined(String user);

    boolean isApplicationDefined(String application);

    boolean isApplicationDefined(String user, String application);

    boolean isPerimeterPathDefined(String application, PerimeterPath perimeterPath);

    List<Privilege> getPrivileges(String user, String application, PerimeterPath perimeterPath);
}
