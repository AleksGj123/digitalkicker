package com.bechtle.model;

import net.formio.binding.Ignored;
import net.formio.validation.constraints.Email;
import net.formio.validation.constraints.NotEmpty;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private long id;

    @NotEmpty
    private String forename;

    @NotEmpty
    private String surname;

    @Email
    private String email;

    @Transient
    private String password;

    @Transient
    private String passwordRepeat;

    private String passwordHash;

    private String biography;

    private String nickname;

    private Boolean lokSafe;

    // field for NFC Card ID
    //private String nfcId;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="Player_Matches",
            joinColumns=@JoinColumn(name="player_id", referencedColumnName="id", unique = false),
            inverseJoinColumns=@JoinColumn(name="match_id", referencedColumnName="id", unique = false),
            indexes = {
                    @Index(name = "idx_player_matechs_player_id", columnList = "player_id"),
                    @Index(name = "idx_player_matechs_match_id", columnList = "match_id")
            })
    private List<Match> matches = new ArrayList<>();

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

    @Ignored
    public List<Match> getMatches() {
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
