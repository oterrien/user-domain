package com.ote.user.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserRights {

    private final User user;
    private final Application application;
    private final List<Perimeter> perimeters = new ArrayList<>();
}
