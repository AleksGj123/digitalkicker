package com.bechtle.api.controller;
import com.bechtle.api.dao.GenericDAO;
import com.bechtle.api.dto.MatchDTO;
import com.bechtle.api.service.Converter;
import com.bechtle.api.service.ErrorHandler;
import com.bechtle.api.service.UrlParser;
import com.bechtle.api.util.ResultWithPagination;
import com.bechtle.api.util.SearchCTX;
import com.bechtle.model.Match;
import com.bechtle.model.Matchtype;
import com.bechtle.model.Player;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class MatchController {

    private static final GenericDAO<Match> dao = new GenericDAO<>(Match.class);

    public static String findBy(Request req, Response res){
        SearchCTX ctx = UrlParser.getSearchCTX(req);
        ResultWithPagination result = dao.findBy(ctx);
        if(result != null){
            return Converter.toJSON(result);
        }
        res.status(400);
        return new ErrorHandler("No user with id '%s' found").getMessage();
    }

    public static String get(Request req, Response res){
        return "";
    }

    public static String create(Request req, Response res){
        //Convert DTO to model
        List<Match> matches2Create = Converter.toModel(req.body(), MatchDTO.class).convertToModels();
        List<Match> createdMatches = new ArrayList<>();

        for(Match match : matches2Create) {
            Matchtype matchType = match.getMatchtype();
            Match createdMatch = dao.saveOrUpdate(0L, match);

            //Handle relation to players
            if(matchType != Matchtype.DEATH_MATCH){
                Player keeperBlack = createdMatch.getKeeperBlack();
                Player keeperWhite = createdMatch.getKeeperWhite();
                Player strikerBlack = createdMatch.getStrikerBlack();
                Player strikerWhite = createdMatch.getStrikerWhite();
                createdMatch.addPlayer(keeperBlack);
                createdMatch.addPlayer(keeperWhite);
                createdMatch.addPlayer(strikerBlack);
                createdMatch.addPlayer(strikerWhite);
            } else {
                Player dPlayerOne = createdMatch.getKeeperWhite();
                Player dPlayerTwo = createdMatch.getStrikerWhite();
                createdMatch.addPlayer(dPlayerOne);
                createdMatch.addPlayer(dPlayerTwo);
            }
            dao.saveOrUpdate(createdMatch.getId(), createdMatch);
            createdMatches.add(createdMatch);
        }
        return Converter.toJSON(createdMatches);
    }

    public static String update(Request req, Response res){
        return "";
    }

    public static String delete(Request req, Response res){
        return "";
    }
}
