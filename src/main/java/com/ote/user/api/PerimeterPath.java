package com.ote.user.api;

import com.ote.user.api.model.Perimeter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PerimeterPath {

    @Getter
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

    public boolean isEmpty() {
        return perimeterPath.isEmpty();
    }

    @Override
    public String toString() {

        return IntStream.range(0, perimeterPath.size() - 1).
                mapToObj(i -> perimeterPath.get(i)).
                map(p -> p + "/").
                collect(Collectors.joining("/"))
                +
                perimeterPath.get(perimeterPath.size() - 1);

    }

    public Optional<Perimeter> getPerimeterFromPath(final List<Perimeter> perimeters) {

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
    }
}
