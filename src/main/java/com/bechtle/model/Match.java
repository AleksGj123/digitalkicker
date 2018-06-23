package com.bechtle.model;

import com.bechtle.util.MatchtypeConverter;
import com.google.gson.annotations.Expose;
import net.formio.validation.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Payload;
import java.util.*;

@Entity
@Table(name = "matches")
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
    private Player keeperWhite;

    @ManyToOne(optional = false)
    @Expose
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

    public List<Player> getPlayers(){
        List<Player> players = new ArrayList<>();
        if(this.keeperBlack != null && this.strikerBlack != null) {
            players.add(this.keeperBlack);
            players.add(this.strikerBlack);
        }
        players.add(this.keeperWhite);
        players.add(this.strikerWhite);
        return players;
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
}
