package com.bechtle.api.model;

import com.bechtle.api.util.MatchtypeConverter;
import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Matches")
public class Match {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Expose
    private long id = 0L;

    @Expose
    private Date timestamp;

    @Expose
    private int resultWhite;

    @Expose
    private int resultBlack;

    @ManyToOne(optional = false)
    @Expose
    /**In case of deathmatch --> represent one player*/
    private Player keeperWhite;

    @ManyToOne(optional = false)
    @Expose
    /**In case of deathmatch --> represent one player*/
    private Player strikerWhite;

    @ManyToOne
    @Expose
    private Player keeperBlack;

    @ManyToOne
    @Expose
    private Player strikerBlack;

    @Convert(converter = MatchtypeConverter.class)
    @Expose
    private Matchtype matchtype;

    @ManyToOne(optional = false)
    @JoinColumn(name = "season_id")
    @Expose
    private Season season;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "Players_Matches",
            joinColumns = { @JoinColumn(name = "match_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "player_id", referencedColumnName = "id") }
    )
    private Set<Player> players = new HashSet<>();

    /**Constructor for regular match*/
    public Match(
            Player keeperWhite,
            Player strikerWhite,
            Player keeperBlack,
            Player strikerBlack,
            int resultTeamWhite,
            int resultTeamBlack,
            Matchtype matchtype,
            Season season) {
        this.keeperWhite = keeperWhite;
        this.strikerWhite = strikerWhite;
        this.keeperBlack = keeperBlack;
        this.strikerBlack = strikerBlack;
        this.resultWhite = resultTeamWhite;
        this.resultBlack = resultTeamBlack;
        this.matchtype = matchtype;
        this.season = season;
        this.timestamp = new Date();
    }

    /**Constructor for deathmatch*/
    public Match(
            Player player1,
            Player player2,
            int resultWhite,
            int resultBlack,
            Matchtype matchtype,
            Season season) {
        this.keeperWhite = player1;
        this.strikerWhite = player2;
        this.resultWhite = resultWhite;
        this.resultBlack = resultBlack;
        this.matchtype = matchtype;
        this.season = season;
        this.timestamp = new Date();
    }

    public Match() {

    }

    /**Getter and Setter*/

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getResultWhite() {
        return resultWhite;
    }

    public void setResultWhite(int resultWhite) {
        this.resultWhite = resultWhite;
    }

    public int getResultBlack() {
        return resultBlack;
    }

    public void setResultBlack(int resultBlack) {
        this.resultBlack = resultBlack;
    }

    public Player getKeeperWhite() {
        return keeperWhite;
    }

    public void setKeeperWhite(Player keeperWhite) {
        this.keeperWhite = keeperWhite;
    }

    public Player getStrikerWhite() {
        return strikerWhite;
    }

    public void setStrikerWhite(Player strikerWhite) {
        this.strikerWhite = strikerWhite;
    }

    public Player getKeeperBlack() {
        return keeperBlack;
    }

    public void setKeeperBlack(Player keeperBlack) {
        this.keeperBlack = keeperBlack;
    }

    public Player getStrikerBlack() {
        return strikerBlack;
    }

    public void setStrikerBlack(Player strikerBlack) {
        this.strikerBlack = strikerBlack;
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

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player){players.add(player);}

}

