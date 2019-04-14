package com.bechtle.controller;

import com.bechtle.model.Player;
import com.bechtle.service.PlayerService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;

public class StatisticsController {

    public static ModelAndView getStats(Request request, Response response) {
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final HashMap<String, Object> objectHashMap = new HashMap<>();

        final List<Player> players = playerService.getPlayers();

        final HashMap<Player, Long> lokList = new HashMap<>();
        final HashMap<Player, Long> numberOfGamesList = new HashMap<>();

        players.stream().forEach(player -> {
            lokList.put(player, (long) playerService.getLostDeathmachtesForPlayer(player).size());
            numberOfGamesList.put(player, playerService.getNumberOfPlayedGamesForPlayer(player));
        });

        // Lokstats
        objectHashMap.put("lokList", lokList);
        // Number of games stats
        objectHashMap.put("numberOfGamesList", numberOfGamesList);

        return new ModelAndView(objectHashMap, "views/statistics/statistics.vm");
    }
}
