package com.ote.user.api.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Perimeter {

    private final String code;
    private final List<Perimeter> children = new ArrayList<>();
    private final List<Privilege> privileges = new ArrayList<>();
    private boolean isAll;

    public Perimeter(String code, boolean isAll) {
        this.code = code;
        this.isAll = isAll;
    }

    public Perimeter(String code) {
        this(code, false);
    }
}
