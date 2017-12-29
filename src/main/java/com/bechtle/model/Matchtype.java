package com.bechtle.model;

import net.formio.validation.constraints.NotEmpty;

public class Matchtype {

    @NotEmpty
    private String name;

    @NotEmpty
    private int goalLimit;

    public Matchtype(String name, int goalLimit) {
        this.name = name;
        this.goalLimit = goalLimit;
    }
}
