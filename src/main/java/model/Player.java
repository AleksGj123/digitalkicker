package model;

import net.formio.validation.constraints.Email;
import net.formio.validation.constraints.NotEmpty;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE
    private long id;
    @NotEmpty
    private String forename;
    @NotEmpty
    private String surname;
    @Email
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordRepeat;
    private String passwordHash;

    private String biography;
    private String nickname;

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
}
