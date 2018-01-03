package com.bechtle.model;

import net.formio.validation.constraints.NotEmpty;

import javax.persistence.*;

@Entity(name = "Matches")
public class Match {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private long id;

    @NotEmpty
    private Status status;

    private int goalsTeam1;

    private int goalsTeam2;

    @OneToOne(optional = false)
    private Player keeperTeam1;

    @OneToOne
    private Player strikerTeam1;

    @OneToOne(optional = false)
    private Player keeperTeam2;

    @OneToOne
    private Player strikerTeam2;

    @OneToOne(optional = false)
    private Matchtype matchtype;

    @OneToOne(optional = false)
    private Season season;

    // ---------------- constructors ------------------

    public Match(Player keeperTeam1, Player strikerTeam1, Player keeperTeam2, Player strikerTeam2, Matchtype matchtype,
                 Season season) {
        this.keeperTeam1 = keeperTeam1;
        this.strikerTeam1 = strikerTeam1;
        this.keeperTeam2 = keeperTeam2;
        this.strikerTeam2 = strikerTeam2;
        this.matchtype = matchtype;
        this.season = season;
    }

    public Match() {
    }


    // ---------------- getters and setters ------------------


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public Player getKeeperTeam1() {
        return keeperTeam1;
    }

    public void setKeeperTeam1(Player keeperTeam1) {
        this.keeperTeam1 = keeperTeam1;
    }

    public Player getStrikerTeam1() {
        return strikerTeam1;
    }

    public void setStrikerTeam1(Player strikerTeam1) {
        this.strikerTeam1 = strikerTeam1;
    }

    public Player getKeeperTeam2() {
        return keeperTeam2;
    }

    public void setKeeperTeam2(Player keeperTeam2) {
        this.keeperTeam2 = keeperTeam2;
    }

    public Player getStrikerTeam2() {
        return strikerTeam2;
    }

    public void setStrikerTeam2(Player strikerTeam2) {
        this.strikerTeam2 = strikerTeam2;
    }

    public Matchtype getMatchtype() {
        return matchtype;
    }

    public void setMatchtype(Matchtype matchtype) {
        this.matchtype = matchtype;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }
}
