package com.ote.user.business;

import com.ote.user.api.IUserRightsService;
import com.ote.user.api.PerimeterPath;
import com.ote.user.api.exception.ApplicationNotFoundException;
import com.ote.user.api.exception.RoleNotFoundException;
import com.ote.user.api.exception.UserNotFoundException;
import com.ote.user.api.model.Application;
import com.ote.user.api.model.Perimeter;
import com.ote.user.api.model.Privilege;
import com.ote.user.api.model.User;
import com.ote.user.spi.IUserRightsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class UserRightsService implements IUserRightsService {

    private final IUserRightsRepository userRightsRepository;

    @Override
    public List<Privilege> getPrivileges(User user, Application application, PerimeterPath perimeterPath) throws UserNotFoundException, ApplicationNotFoundException, RoleNotFoundException {

        if (!userRightsRepository.isUserDefined(user)) {
            throw new UserNotFoundException(user);
        }

        if (!userRightsRepository.isApplicationDefined(application)) {
            throw new ApplicationNotFoundException(application);
        }

        if (!userRightsRepository.isRoleDefined(user, application)) {
            throw new RoleNotFoundException(user, application);
        }

        List<Perimeter> perimeters = userRightsRepository.getPrivileges(user, application);

        if (perimeters.isEmpty()) {
            throw new RoleNotFoundException(user, application);
        }

        List<Privilege> privileges = perimeterPath.getPrivileges(perimeters);

        if (privileges.isEmpty()) {
            throw new RoleNotFoundException(user, application, perimeterPath);
        }

        return privileges;
    }

    /*@Override
    public List<Privilege> getPrivileges(User user, Application application, PerimeterPath perimeterPath) {
        Optional<Perimeter> perimeter = userRightsList.stream().
                filter(p -> Objects.equals(p.getUser().getLogin(), user.getLogin())).
                filter(p -> Objects.equals(p.getApplication().getCode(), application.getCode())).
                map(p -> perimeterPath.getPerimeterFromPath(p.getPerimeters())).
                filter(Optional::isPresent).
                map(Optional::get).
                findAny();

        if (perimeter.isPresent()) {
            return perimeter.get().getPrivileges();
        }
        return null;
    }*/
}
