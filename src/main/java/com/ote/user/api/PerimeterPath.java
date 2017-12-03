package com.ote.user.api;

import com.ote.user.api.model.Perimeter;
import com.ote.user.api.model.Privilege;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PerimeterPath {

    private List<String> perimeterPath;

    public static PerimeterPath builder() {
        return new PerimeterPath();
    }

    public PerimeterPath startsWith(String perimeter) {
        perimeterPath = new ArrayList<>();
        perimeterPath.add(perimeter);
        return this;
    }

    public PerimeterPath then(String perimeter) {
        perimeterPath.add(perimeter);
        return this;
    }

    public PerimeterPath build() {
        return this;
    }

    @Override
    public String toString() {
        return perimeterPath.stream().
                collect(Collectors.joining("/"));
    }

    public List<Privilege> getPrivileges(final List<Perimeter> perimeters) {

        List<Privilege> privileges = new ArrayList<>();

        List<Perimeter> perimetersToSearch = new ArrayList<>();
        perimetersToSearch.addAll(perimeters);

        for (String element : perimeterPath) {
            Optional<Perimeter> perimeter = perimetersToSearch.stream().filter(p -> Objects.equals(p.getCode(), element)).findAny();
            if (perimeter.isPresent()) {
                privileges.addAll(perimeter.get().getPrivileges());
                perimetersToSearch = perimeter.get().getChildren();
            } else {
                perimeter = perimetersToSearch.stream().filter(p -> p.isAll()).findAny();
                if (perimeter.isPresent()) {
                    privileges.addAll(perimeter.get().getPrivileges());
                    perimetersToSearch = perimeter.get().getChildren();
                } else {
                    break;
                }
            }
        }

        return privileges;
    }

 /*   public Optional<Perimeter> getPerimeterFromPath(final List<Perimeter> perimeters) {

        List<Perimeter> perimetersToSearch = new ArrayList<>();
        perimetersToSearch.addAll(perimeters);

        Optional<Perimeter> perimeter = Optional.empty();
        for (String element : perimeterPath) {
            perimeter = perimetersToSearch.stream().filter(p -> Objects.equals(p.getCode(), element)).findAny();
            if (perimeter.isPresent()) {
                perimetersToSearch = perimeter.get().getChildren();
            } else {
                break;
            }
        }
        return perimeter;
    }*/
}
