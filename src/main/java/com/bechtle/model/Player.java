package com.bechtle.model;
import com.google.gson.annotations.Expose;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Players")
public class Player {

    @Id
    @Expose
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Expose
    private String forename;
    @Expose
    private String surname;
    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String biography;
    @Expose
    private String nickname;
    @Expose
    private Boolean lokSafe;
    @Expose
    private Boolean inactive;

    /**Relation to players*/
    @ManyToMany(mappedBy = "players")
    private Set<Match> matches;


    /**Constructors*/
    public Player(String forename, String surname, String email, String password, String passwordRepeat,
                  String biography, String nickname) {
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.biography = biography;
        this.nickname = nickname;
        this.lokSafe = true;
    }

    public Player() {
    }

    /**Getter and setter*/
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLokSafe() {
        return lokSafe;
    }

    public void setLokSafe(Boolean lokSafe) {
        this.lokSafe = lokSafe;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Set<Match> getMatches() {
        return matches;
    }

    public void setMatches(Set<Match> matches) {
        this.matches = matches;
    }

    public void addMatch(Match match){
        matches.add(match);
    }

}
