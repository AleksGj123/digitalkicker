package com.bechtle.model;

import net.formio.validation.constraints.Email;
import net.formio.validation.constraints.NotEmpty;

import javax.persistence.*;
import java.util.List;

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
    @NotEmpty
    private String password;

    @Transient
    @NotEmpty
    private String passwordRepeat;

    private String passwordHash;

    private String biography;

    private String nickname;



    public Player(String forename, String surname, String email, String password, String passwordRepeat,
                  String biography, String nickname) {
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.biography = biography;
        this.nickname = nickname;
    }

    public Player() {
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
}
