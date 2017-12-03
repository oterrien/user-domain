package com.ote.user.spi;

import com.ote.user.api.model.Application;
import com.ote.user.api.model.Perimeter;
import com.ote.user.api.model.User;

import java.util.List;

public interface IUserRightsRepository {

    boolean isUserDefined(User user);

    boolean isApplicationDefined(Application application);

    boolean isRoleDefined(User user, Application application);

    List<Perimeter> getPrivileges(User user, Application application);
}
