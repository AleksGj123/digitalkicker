package com.bechtle.model;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @Expose
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
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
    private String passwordRepeat;
    @Expose
    private String passwordHash;
    @Expose
    private String biography;
    @Expose
    private String nickname;
    @Expose
    private Boolean lokSafe;
    @Expose
    private Boolean inactive;


    @ManyToMany(fetch= FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name="player_matches",
            joinColumns=@JoinColumn(name="player_id"),
            inverseJoinColumns=@JoinColumn(name="matches_id")
    )
    private Set<Match> matches = new HashSet<>();

    // ---------------- constructors ------------------

    public Player(String forename, String surname, String email, String password, String passwordRepeat,
                  String biography, String nickname) {
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.biography = biography;
        this.nickname = nickname;
        this.lokSafe = true;
    }

    public Player() {
    }

    // ---------------- getters and setters ------------------


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
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

    public void addMatch(Match match){
        matches.add(match);
    }

    /*
    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }*/

    /**
     * Checks if there is a Field that is null for an instance of Player
     * @return List of field names that are empty
     */
    public List<String> getNullAndEmptyFields(){
        ArrayList<String> missingFields = new ArrayList<>();

        List<String> m = Arrays.stream(getClass().getDeclaredFields())
                .filter(field -> field.getName() != "passwordHash")
                .filter(field -> {
                    try {
                        if (field.get(this) == null || field.get(this).equals("")) return true;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                    return false;
                })
                .map(field -> field.getName()).collect(Collectors.toList());


        return m;
    }
}
