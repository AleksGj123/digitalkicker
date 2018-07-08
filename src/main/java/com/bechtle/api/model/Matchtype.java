package com.bechtle.api.model;

public enum  Matchtype {

    REGULAR, DEATH_MATCH, DEATH_MATCH_BO3;

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace("_", " ");
    }
}
