package com.bechtle.model;

import net.formio.validation.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Matchtype {

    @Id
    @GeneratedValue
    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private int goalLimit;

    public Matchtype(String name, int goalLimit) {
        this.name = name;
        this.goalLimit = goalLimit;
    }

    public Matchtype() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGoalLimit() {
        return goalLimit;
    }

    public void setGoalLimit(int goalLimit) {
        this.goalLimit = goalLimit;
    }
}
