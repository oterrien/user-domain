package com.ote.user;

import com.ote.user.api.PerimeterPath;
import com.ote.user.api.model.Perimeter;
import com.ote.user.api.model.Privilege;
import com.ote.user.api.model.UserRights;
import com.ote.user.spi.IUserRightsRepository;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserRightsRepositoryMock implements IUserRightsRepository {

    @Getter
    private final List<UserRights> userRightsList = new ArrayList<>();

    @Override
    public boolean isUserDefined(String user) {
        return userRightsList.stream().anyMatch(p -> Objects.equals(p.getUser().getLogin(), user));
    }

    @Override
    public boolean isApplicationDefined(String application) {
        return userRightsList.stream().
                anyMatch(p -> Objects.equals(p.getApplication().getCode(), application));
    }

    @Override
    public boolean isApplicationDefined(String user, String application) {
        return userRightsList.stream().
                filter(p -> Objects.equals(p.getUser().getLogin(), user)).
                anyMatch(p -> Objects.equals(p.getApplication().getCode(), application));
    }

    @Override
    public boolean isPerimeterPathDefined(String application, PerimeterPath perimeterPath) {
        return userRightsList.stream().
                filter(p -> Objects.equals(p.getApplication().getCode(), application)).
                anyMatch(p -> perimeterPath.getPerimeterFromPath(p.getPerimeters()).isPresent());
    }

    @Override
    public List<Privilege> getPrivileges(String user, String application, PerimeterPath perimeterPath) {
        Optional<Perimeter> perimeter = userRightsList.stream().
                filter(p -> Objects.equals(p.getUser().getLogin(), user)).
                filter(p -> Objects.equals(p.getApplication().getCode(), application)).
                map(p -> perimeterPath.getPerimeterFromPath(p.getPerimeters())).
                filter(Optional::isPresent).
                map(Optional::get).
                findAny();

        if (perimeter.isPresent()) {
            return perimeter.get().getPrivileges();
        }
        return null;
    }
}
