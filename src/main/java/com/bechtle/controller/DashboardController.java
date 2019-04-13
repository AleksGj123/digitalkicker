package com.bechtle.controller;

import com.bechtle.model.Match;
import com.bechtle.model.Matchtype;
import com.bechtle.model.Player;
import com.bechtle.model.Season;
import com.bechtle.service.MatchService;
import com.bechtle.service.PlayerService;
import com.bechtle.service.SeasonService;
import redis.clients.jedis.Jedis;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DashboardController {

    public static ModelAndView showDashboard(Request request, Response Request) {
        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        final Optional<Match> currentMatchOpt = matchService.getCurrentMatch();
        final Match currentMatch = currentMatchOpt.orElseGet(() -> createPrematch(em));

        final HashMap<String, Object> map = new HashMap<>();
        map.put("match", currentMatch);
        map.put("matchtype", currentMatch.getMatchtype().name());

        return new ModelAndView(map, "views/dashboard/dashboard.vm");
    }

    private static Match createPrematch(EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final PlayerService playerService = new PlayerService(em);

        final Player startPlayer = playerService.getPlayers().stream()
                .sorted(Comparator.comparing(Player::getId))
                .findFirst()
                .get();

        return matchService.createPreMatch(startPlayer, Matchtype.REGULAR, getCurrentSeason(em));
    }

    private static Season getCurrentSeason(EntityManager em) {
        final SeasonService seasonService = new SeasonService(em);
        final List<Season> allSeasons = seasonService.getAllSeasons();
        long count = allSeasons.stream().count();
        Season currentSeason = allSeasons.stream()
                .sorted(Comparator.comparing(Season::getEndDate))
                .skip(count - 1).findFirst().get();
        return currentSeason;
    }

    public static ModelAndView showController(Request request, Response Request) {
        final HashMap<String, Match> map = new HashMap<>();

        return new ModelAndView(map, "views/dashboard/dashboard_controller.vm");
    }

    public static String putControl(Request request, Response response) {
        final EntityManager em = request.attribute("em");
        final MatchService matchService = new MatchService(em);

        final String channel = request.queryParams("channel");
        final String message = request.queryParams("message");

        Jedis jSubscriber = new Jedis();

        return "" + jSubscriber.publish(channel, message);
    }
}
