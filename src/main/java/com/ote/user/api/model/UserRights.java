package com.ote.user.api.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class UserRights {

    private final String user;
    private final String application;
    private final List<Perimeter> perimeters = new ArrayList<>();
}
