package com.bechtle.controller;

import com.bechtle.model.*;
import com.bechtle.service.MatchService;
import com.bechtle.service.PlayerService;
import com.bechtle.service.SeasonService;
import com.bechtle.util.Constants;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

public class MatchController {

//    private static final MatchService matchService = new MatchService();
//    private static final SeasonService seasonService = new SeasonService();
//    private static final PlayerService playerService = new PlayerService();

    public static ModelAndView showMatch(Request request, Response response){
        final String matchId = request.params(":id");

        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        final Match match = matchService.getMatch(Long.parseLong(matchId));
        final HashMap<String, Object> map = new HashMap<>();
        map.put("match", match);

        return new ModelAndView(map, "views/match/show_match.vm");
    }

    public static ModelAndView listMatches(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        final List<Match> allMatches = matchService.getAllMatches();

        final List<Match> filteredMatches = allMatches.stream()
                .filter(match -> match.getStatus() == Status.FINISHED)
                .collect(Collectors.toList());

        final HashMap<String, List<Match>> matchesMap = new HashMap<>();
        matchesMap.put("matches", filteredMatches);

        return new ModelAndView(matchesMap, "views/match/matches.vm");
    }

    public static ModelAndView listUnfinishedMatches(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        final List<Match> allMatches = matchService.getAllMatches();

        final List<Match> filteredMatches = allMatches.stream()
                .filter(match -> match.getStatus() != Status.FINISHED)
                .collect(Collectors.toList());

        final HashMap<String, List<Match>> matchesMap = new HashMap<>();
        matchesMap.put("matches", filteredMatches);

        return new ModelAndView(matchesMap, "views/match/matches.vm");
    }

    public static String updateMatch(Request request, Response response){

        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        // -- required attributes
        final Long matchId = Long.parseLong(request.queryParams("matchId"));
        final int goalsTeam1 = Integer.parseInt(request.queryParams("goalsTeam1"));
        final int goalsTeam2 = Integer.parseInt(request.queryParams("goalsTeam2"));

        matchService.updateMatch(matchId, goalsTeam1, goalsTeam2);
        return "";
    }

    public static String finishMatch(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        final Long matchId = Long.parseLong(request.queryParams("matchId"));
        final Optional<Match> newMatchOpt = matchService.finishMatch(matchId);
        return ""+newMatchOpt.map(m -> m.getId()).orElse(null);
    }

    public static String instantFinish(Request request, Response response){
        MatchController.updateMatch(request, response);
        return MatchController.finishMatch(request, response);
    }

    public static ModelAndView showNewMatchFrom(Request request, Response response){
        final EntityManager em = request.attribute("em");
        return new ModelAndView(prepareMatchForm(em), "views/match/new_match.vm");
    }


    public static ModelAndView createNewMatch(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);
        final PlayerService playerService = new PlayerService(em);
        final SeasonService seasonService = new SeasonService(em);

        final String matchType = request.queryParams("matchType");
        // -- you receive only ids --
        final String season = request.queryParams("season");
        final String keeperTeam1 = request.queryParams("keeperTeam1");
        final String keeperTeam2 = request.queryParams("keeperTeam2");
        final String strikerTeam1 = request.queryParams("strikerTeam1");
        final String strikerTeam2 = request.queryParams("strikerTeam2");

        final Season s = seasonService.getSeason(Long.parseLong(season));
        //Season s = em.find(Season.class, Long.parseLong(season));

        final Player kT1 = playerService.getPlayer(Long.parseLong(keeperTeam1));
        //Player kT1 = em.find(Player.class, Long.parseLong(keeperTeam1));
        final Player kT2 = playerService.getPlayer(Long.parseLong(keeperTeam2));
        //Player kT2 = em.find(Player.class, Long.parseLong(keeperTeam2));

        Match newMatch = null;

        if (Matchtype.REGULAR.toString().equals(matchType)){

            final Player sT1 = playerService.getPlayer(Long.parseLong(strikerTeam1));
            //Player sT1 = em.find(Player.class, Long.parseLong(strikerTeam1));
            final Player sT2 = playerService.getPlayer(Long.parseLong(strikerTeam2));
            //Player sT2 = em.find(Player.class, Long.parseLong(strikerTeam2));

            final boolean playersValid = matchService.playersValid(kT1, kT2, sT1, sT2);

            if(playersValid == false){
                return getStateAndValidation(em, season, matchType, keeperTeam1, keeperTeam2, strikerTeam1, strikerTeam2);
            }

            newMatch = matchService.createMatch(kT1,sT1,kT2,sT2,s);
        }
        else {
            if (Matchtype.DEATH_MATCH.toString().equals(matchType)){
                final boolean playersValid = matchService.playersValid(kT1, kT2);
                if(playersValid == false){
                    return getStateAndValidation(em, season, matchType, keeperTeam1, keeperTeam2, strikerTeam1, strikerTeam2);
                }
                newMatch = matchService.createMatch(kT1, kT2, Matchtype.DEATH_MATCH, s);
            }
            else if(Matchtype.DEATH_MATCH_BO3.toString().equals(matchType))
            {
                final boolean playersValid = matchService.playersValid(kT1, kT2);
                if(playersValid == false){
                    return getStateAndValidation(em, season, matchType, keeperTeam1, keeperTeam2, strikerTeam1, strikerTeam2);
                }
                newMatch = matchService.createMatch(kT1, kT2, Matchtype.DEATH_MATCH_BO3, s);
            }
        }

        response.redirect("/match/"+newMatch.getId());
//        response.redirect("/dashboard");
        // you never get to this state because of the redirect before ... but it is necessary
        return new ModelAndView(new HashMap<>(), "views/player/new_match.vm");
    }

    public static String deleteMatch(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        final String matchId = request.params(":id");
        final long matchIdLong = Long.parseLong(matchId);

        matchService.deleteMatch(matchIdLong);
        return "Ok";
    }

    // helpers ...
    private static ModelAndView getStateAndValidation(EntityManager em,
                                                      String season, String matchType,
                                                      String keeperTeam1, String keeperTeam2,
                                                      String strikerTeam1, String strikerTeam2) {
        // -- prepare form like you do on /new
        final Map map = prepareMatchForm(em);

        // -- add also the validation info
        final List<String> validionProblems = new LinkedList<>();
        validionProblems.add("Duplicate player(s)! Every match should consist of distinct players");
        map.put(Constants.VALIDATION, validionProblems);

        // now you need to restore the state of the form as it was before submitting
        final Map<String, Object> state = new HashMap<>();
        state.put("seasonId", season);
        state.put("matchTypeId", matchType);
        state.put("keeperTeam1Id", keeperTeam1);
        state.put("keeperTeam2Id", keeperTeam2);
        state.put("strikerTeam1Id", strikerTeam1);
        state.put("strikerTeam2Id", strikerTeam2);
        map.put("state", state);

        return new ModelAndView(map, "views/match/new_match.vm");
    }

    private static Map prepareMatchForm(EntityManager em){
        final HashMap<String, Object> map = new HashMap<>();

        final PlayerService playerService = new PlayerService(em);
        final SeasonService seasonService = new SeasonService(em);

        // now get all players
        final List<Player> players = playerService.getPlayers();
        players.sort(Comparator.comparing(Player::getForename));

        // ... get all seasons
        final List<Season> allSeasons = seasonService.getAllSeasons();

        map.put(Constants.PLAYERS, players);
        map.put(Constants.SEASONS, allSeasons);
        map.put(Constants.MATCH_TYPES, Matchtype.values());

        return map;
    }
}
