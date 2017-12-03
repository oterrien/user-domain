package com.ote.user;

import com.ote.user.api.model.Application;
import com.ote.user.api.model.Perimeter;
import com.ote.user.api.model.User;
import com.ote.user.api.model.UserRights;
import com.ote.user.spi.IUserRightsRepository;
import lombok.Getter;

import java.util.*;

public class UserRightsRepositoryMock implements IUserRightsRepository {

    @Getter
    private final List<UserRights> userRightsList = new ArrayList<>();

    @Override
    public boolean isUserDefined(User user) {
        return userRightsList.stream().anyMatch(p -> Objects.equals(p.getUser().getLogin(), user.getLogin()));
    }

    @Override
    public boolean isApplicationDefined(Application application) {
        return userRightsList.stream().
                anyMatch(p -> Objects.equals(p.getApplication().getCode(), application.getCode()));
    }

    @Override
    public boolean isRoleDefined(User user, Application application) {
        return userRightsList.stream().
                filter(p -> Objects.equals(p.getUser().getLogin(), user.getLogin())).
                anyMatch(p -> Objects.equals(p.getApplication().getCode(), application.getCode()));
    }

    @Override
    public List<Perimeter> getPerimeters(User user, Application application) {
        Optional<UserRights> userRights = userRightsList.stream().
                filter(p -> Objects.equals(p.getUser().getLogin(), user.getLogin())).
                filter(p -> Objects.equals(p.getApplication().getCode(), application.getCode())).
                findAny();

        if (userRights.isPresent()) {
            return userRights.map(p -> p.getPerimeters()).get();
        }
        return Collections.emptyList();
    }
}
