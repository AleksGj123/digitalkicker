package com.bechtle.model;

import net.formio.binding.Ignored;
import net.formio.validation.constraints.NotEmpty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private Date startDate;

    @NotEmpty
    private Date endDate;

    @OneToMany(targetEntity = Match.class)
    private List<Match> matches = new ArrayList<>();

    // ---------------- constructors ------------------

    public Season(String name, Date startDate, Date endDate)
    {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Season() {
    }


    // ---------------- getters and setters ------------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void addMatch(Match match){
        matches.add(match);
    }


    @Ignored
    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
