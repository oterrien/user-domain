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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

        List<Perimeter> perimeters = userRightsRepository.getPerimeters(user, application);

        if (perimeters.isEmpty()) {
            throw new RoleNotFoundException(user, application);
        }

        List<Privilege> privileges = getPrivileges(perimeterPath, perimeters);

        if (privileges.isEmpty()) {
            throw new RoleNotFoundException(user, application, perimeterPath);
        }

        return privileges;
    }

    private List<Privilege> getPrivileges(PerimeterPath perimeterPath, List<Perimeter> perimeters) {

        List<Privilege> privileges = new ArrayList<>();

        final List<Perimeter> perimetersToSearch = new ArrayList<>();
        perimetersToSearch.addAll(perimeters);

        perimeterPath.forEach(pathElement -> {
            Optional<Perimeter> perimeterOpt = searchPerimeterByCode(perimetersToSearch, pathElement);
            OptionalConsumer.of(perimeterOpt).
                    ifPresent(perimeter -> apply(perimeter, privileges, perimetersToSearch)).
                    ifNotPresent(() -> searchPerimeterAll(perimetersToSearch).
                            ifPresent(perimeter -> apply(perimeter, privileges, perimetersToSearch)));
        });

        return privileges;
    }

    private Optional<Perimeter> searchPerimeterByCode(List<Perimeter> perimetersToSearch, String code) {
        return searchPerimeter(perimetersToSearch, perimeter -> Objects.equals(perimeter.getCode(), code));
    }

    private Optional<Perimeter> searchPerimeterAll(List<Perimeter> perimetersToSearch) {
        return searchPerimeter(perimetersToSearch, perimeter -> perimeter.isAll());
    }

    private Optional<Perimeter> searchPerimeter(List<Perimeter> perimetersToSearch, Predicate<Perimeter> predicate) {
        return perimetersToSearch.stream().filter(predicate).findAny();
    }

    private void apply(Perimeter perimeter, List<Privilege> privileges, List<Perimeter> perimetersToSearch) {
        privileges.addAll(perimeter.getPrivileges());
        perimetersToSearch.clear();
        perimetersToSearch.addAll(perimeter.getChildren());
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OptionalConsumer<T> {

        private final Optional<T> optional;

        public static <T> OptionalConsumer<T> of(Optional<T> optional) {
            return new OptionalConsumer<>(optional);
        }

        public OptionalConsumer<T> ifPresent(Consumer<T> consumer) {
            optional.ifPresent(consumer);
            return this;
        }

        public OptionalConsumer<T> ifNotPresent(Runnable runnable) {
            if (!optional.isPresent()) {
                runnable.run();
            }
            return this;
        }
    }
}
