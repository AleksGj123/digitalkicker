package com.bechtle.model;

import net.formio.validation.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Match {

    @NotEmpty
    private String status;

    @NotEmpty
    private int goalsTeam1;

    @NotEmpty
    private int goalsTeam2;

    //@OneToMany(mappedBy = "match")
    @NotEmpty
    private List<Player> team1 = new ArrayList<>();

    //@OneToMany(mappedBy = "match")
    @NotEmpty
    private List<Player> team2 = new ArrayList<>();




    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getGoalsTeam1() {
        return goalsTeam1;
    }

    public void setGoalsTeam1(int goalsTeam1) {
        this.goalsTeam1 = goalsTeam1;
    }

    public int getGoalsTeam2() {
        return goalsTeam2;
    }

    public void setGoalsTeam2(int goalsTeam2) {
        this.goalsTeam2 = goalsTeam2;
    }


    @Id
    @GeneratedValue
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
