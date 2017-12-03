package com.ote.user.api.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Perimeter {

    private final String code;
    private final List<Perimeter> children = new ArrayList<>();
    private final List<Privilege> privileges = new ArrayList<>();
}
