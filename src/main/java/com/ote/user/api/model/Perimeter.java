package com.ote.user.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Perimeter {

    private final String code;
    private final List<Perimeter> children = new ArrayList<>();
    private final List<Privilege> privileges = new ArrayList<>();
}
