package com.ote.user.business;

import com.ote.user.api.IUserRightsService;
import com.ote.user.api.PerimeterPath;
import com.ote.user.api.exception.ApplicationNotFoundException;
import com.ote.user.api.exception.PerimeterPathNotFoundException;
import com.ote.user.api.exception.UserNotFoundException;
import com.ote.user.api.model.Privilege;
import com.ote.user.spi.IUserRightsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class UserRightsService implements IUserRightsService {

    private final IUserRightsRepository userRightsRepository;

    @Override
    public List<Privilege> getPrivileges(String user, String application, PerimeterPath perimeterPath) throws UserNotFoundException, ApplicationNotFoundException, PerimeterPathNotFoundException {

        if (!userRightsRepository.isUserDefined(user)) {
            throw new UserNotFoundException(user);
        }

        if (!userRightsRepository.isApplicationDefined(application)) {
            throw new ApplicationNotFoundException(application);
        }

        if (!userRightsRepository.isApplicationDefined(user, application)) {
            throw new ApplicationNotFoundException(user, application);
        }

        if (!userRightsRepository.isPerimeterPathDefined(application, perimeterPath)) {
            throw new PerimeterPathNotFoundException(application, perimeterPath);
        }

        return userRightsRepository.getPrivileges(user, application, perimeterPath);
    }
}
