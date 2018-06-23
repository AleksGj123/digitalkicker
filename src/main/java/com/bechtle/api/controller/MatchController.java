package com.bechtle.api.controller;
import com.bechtle.api.dao.GenericDAO;
import com.bechtle.api.dto.MatchDTO;
import com.bechtle.api.service.Converter;
import com.bechtle.api.service.ErrorHandler;
import com.bechtle.model.Match;
import com.bechtle.model.Player;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class MatchController {

    private static final GenericDAO<Match> dao = new GenericDAO<>();
    private static final GenericDAO<Player> daoPlayer = new GenericDAO<>();

    public static String getAll(Request req, Response res){

        List<Match> result = dao.findBy("select m from Matches as m");
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
        List<Match> matches2Create = Converter.toModel(req.body(), MatchDTO.class).convertToModels();
        List<Match> createdMatches = new ArrayList<>();
        for(Match match : matches2Create) {
            Match createdMatch = dao.saveOrUpdate(0L, match);
            createdMatches.add(createdMatch);
        }
        for(Match createdMatch : createdMatches){
            List<Player> players = createdMatch.getPlayers();
            for(Player player : players){
                player.addMatch(createdMatch);
                daoPlayer.saveOrUpdate(player.getId(), player);
            }
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
