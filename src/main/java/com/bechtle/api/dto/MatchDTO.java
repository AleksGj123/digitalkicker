package com.bechtle.api.dto;

import com.bechtle.api.dao.GenericDAO;
import com.bechtle.model.Match;
import com.bechtle.model.Matchtype;
import com.bechtle.model.Player;
import com.bechtle.model.Season;

import java.util.ArrayList;
import java.util.List;

public class MatchDTO {
    private long id;
    private long keeperWhite;
    private long keeperBlack;
    private long strikerWhite;
    private long strikerBlack;
    private int resultWhite;
    private int resultBlack;
    private long season;


    private Long dPlayerWhite;
    private Long dPlayerBlack;
    private int dResultWhite;
    private int dResultBlack;

    public List<Match> convertToModels(){
        final GenericDAO<Player> playerDAO = new GenericDAO<>(Player.class);
        final GenericDAO<Season> seasonDAO = new GenericDAO<>(Season.class);
        List<Match> matches2Create = new ArrayList<>();
        Player keeperWhite = playerDAO.findById(this.keeperWhite, Player.class);
        Player keeperBlack = playerDAO.findById(this.keeperBlack, Player.class);
        Player strikerWhite = playerDAO.findById(this.strikerWhite, Player.class);
        Player strikerBlack = playerDAO.findById(this.strikerBlack, Player.class);
        Season season = seasonDAO.findById(this.season, Season.class);

        Match matchModel = new Match(
                keeperWhite,
                strikerWhite,
                keeperBlack,
                strikerBlack,
                this.resultWhite,
                this.resultBlack,
                Matchtype.REGULAR,
                season
        );
        matches2Create.add(matchModel);
            if(isDeathmatch()){
                Player deathPlayerWhite = playerDAO.findById(dPlayerWhite, Player.class);
                Player deathPlayerBlack = playerDAO.findById(dPlayerBlack, Player.class);

                Match deathmatchModel = new Match(
                        deathPlayerWhite,
                        deathPlayerBlack,
                        this.dResultWhite,
                        this.dResultBlack,
                        Matchtype.DEATH_MATCH,
                        season
                );
                matches2Create.add(deathmatchModel);
            }

        return matches2Create;
    }

    public boolean isDeathmatch(){
        return (dPlayerBlack != null && dPlayerWhite != null);
    }
}
