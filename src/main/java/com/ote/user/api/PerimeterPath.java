package com.ote.user.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PerimeterPath implements Iterable<String> {

    private List<String> perimeterPath;

    public static PerimeterPath builder() {
        return new PerimeterPath();
    }

    public PerimeterPath startsWith(String perimeter) {
        perimeterPath = new ArrayList<>();
        perimeterPath.add(perimeter);
        return this;
    }

    public PerimeterPath then(String perimeter, String... otherPerimeter) {
        perimeterPath.add(perimeter);
        perimeterPath.addAll(Arrays.asList(otherPerimeter));
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

    @Override
    public Iterator<String> iterator() {
        return perimeterPath.iterator();
    }
}
