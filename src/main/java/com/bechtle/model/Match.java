package com.bechtle.model;

import com.bechtle.util.MatchtypeConverter;
import net.formio.validation.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Matches")
public class Match {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private long id;

    private Date timestamp;

    @NotEmpty
    private Status status;

    private int goalsTeam1;

    private int goalsTeam2;

    // a recorded Match contains max one loksafe player
    @ManyToOne
    private Player loksafePlayer;

    @ManyToOne(optional = false)
    private Player keeperTeam1;

    @ManyToOne
    private Player strikerTeam1;

    @ManyToOne(optional = false)
    private Player keeperTeam2;

    @ManyToOne
    private Player strikerTeam2;

    @Convert(converter = MatchtypeConverter.class)
    private Matchtype matchtype;

    @ManyToOne(optional = false)
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
        this.timestamp = new Date();
        this.status = Status.STARTED;
    }

    public Match(Player player1, Player player2, Matchtype matchtype, Season season) {
        this.keeperTeam1 = player1;
        this.keeperTeam2 = player2;
        this.matchtype = matchtype;
        this.season = season;
        this.timestamp = new Date();
        this.status = Status.STARTED;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
