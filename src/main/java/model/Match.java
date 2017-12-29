package model;

import net.formio.validation.constraints.NotEmpty;

import java.util.List;

public class Match {

    @NotEmpty
    private String status;
    @NotEmpty
    private int goalsTeam1;
    @NotEmpty
    private int goalsTeam2;
    @NotEmpty
    private List<Player> team1;
    @NotEmpty
    private List<Player> team2;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}
