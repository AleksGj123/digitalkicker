package com.bechtle.controller;

import com.bechtle.controller.util.PlayerPair;
import com.bechtle.model.Match;
import com.bechtle.model.Player;
import com.bechtle.model.Season;
import com.bechtle.service.MatchService;
import com.bechtle.service.PlayerService;
import com.bechtle.service.SeasonService;
import com.bechtle.service.StatisticsService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import javax.servlet.MultipartConfigElement;
import java.text.NumberFormat;
import java.util.*;

public class StatisticsController {

    private static Long MINIMUM_MATCHES_FOR_STATS = 20l;

    public static ModelAndView getStats(Request request, Response response) {
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);
        final SeasonService seasonService = new SeasonService(em);
        final StatisticsService statisticsService = new StatisticsService(em);

        final HashMap<String, Object> objectHashMap = new HashMap<>();

        final List<Player> players = playerService.getPlayers();
        final List<Season> allSeasons = seasonService.getAllSeasons();

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        final String seasonParam = request.queryParams("season");
        final Season currentSeason;
        if (seasonParam != null && !seasonParam.isEmpty()) {
            currentSeason = allSeasons.stream()
                    .filter(s -> s.getId() == Long.parseLong(seasonParam))
                    .findFirst().get();
        } else {
            currentSeason = allSeasons.stream()
                    .sorted(Comparator.comparing(Season::getEndDate).reversed())
                    .findFirst().get();
        }

        final SortedMap<Player, Long> numberOfGamesList = new TreeMap<>(playerComparator);
        final SortedMap<Player, Double> wonMatches = new TreeMap<>(playerComparator);
        final SortedMap<Player, Long> lokList = new TreeMap<>(playerComparator);
        final SortedMap<Player, Double> bestStriker = new TreeMap<>(playerComparator);
        final SortedMap<Player, Double> bestKeeper = new TreeMap<>(playerComparator);

        final SortedMap<Player, Double> deathmatchPossibility = new TreeMap<>(playerComparator);
        final SortedMap<Player, Double> lokPossibility = new TreeMap<>(playerComparator);
        final SortedMap<Player, Double> wonDeatchmatches = new TreeMap<>(playerComparator);

        final NumberFormat percentageFormat = NumberFormat.getPercentInstance();
        percentageFormat.setMinimumFractionDigits(2);

        players.stream().forEach(player -> {
            final long numberOfPlayedGamesForPlayer = statisticsService.getNumberOfPlayedGamesForPlayer(player, currentSeason);

            numberOfGamesList.put(player, numberOfPlayedGamesForPlayer);
            // only count statistics for player with more then 10 played matches!
            if (numberOfPlayedGamesForPlayer > MINIMUM_MATCHES_FOR_STATS) {
                final List<Match> deathmachtesForPlayer = statisticsService.getDeathmachesForPlayer(player, currentSeason);
                final List<Match> lostDeathmachtesForPlayer = statisticsService.getLostDeathmachesForPlayer(player, currentSeason);
                final long numberOfWonGamesForPlayer = statisticsService.getNumberOfWonGamesForPlayer(player, currentSeason);
                final long numberOfPlayedGamesAsKeeper = statisticsService.getNumberOfPlayedGamesAsKeeper(player, currentSeason);
                final long numberOfWonGamesAsKeeper = statisticsService.getNumberOfWonGamesAsKeeper(player, currentSeason);
                final long numberOfPlayedGamesAsStriker = statisticsService.getNumberOfPlayedGamesAsStriker(player, currentSeason);
                final long numberOfWonGamesAsStriker = statisticsService.getNumberOfWonGamesAsStriker(player, currentSeason);

                lokList.put(player, (long) lostDeathmachtesForPlayer.size());

                if (numberOfPlayedGamesForPlayer > 0) {
                    deathmatchPossibility.put(player, 1.0 * deathmachtesForPlayer.size() / numberOfPlayedGamesForPlayer);
                    lokPossibility.put(player, 1.0 * lostDeathmachtesForPlayer.size() / numberOfPlayedGamesForPlayer);
                    wonMatches.put(player, 1.0 * numberOfWonGamesForPlayer / numberOfPlayedGamesForPlayer);
                } else {
                    deathmatchPossibility.put(player, 0.0);
                    lokPossibility.put(player, 0.0);
                    wonMatches.put(player, 0.0);
                }

                if (!deathmachtesForPlayer.isEmpty()) {
                    wonDeatchmatches.put(player, 1.0 * (deathmachtesForPlayer.size() - lostDeathmachtesForPlayer.size()) / deathmachtesForPlayer.size());
                } else {
                    wonDeatchmatches.put(player, 0.0);
                }

                if (numberOfPlayedGamesAsStriker > 0) {
                    bestStriker.put(player, 1.0 * numberOfWonGamesAsStriker / numberOfPlayedGamesAsStriker);
                } else {
                    bestStriker.put(player, 0.0);
                }

                if (numberOfPlayedGamesAsKeeper > 0) {
                    bestKeeper.put(player, 1.0 * numberOfWonGamesAsKeeper / numberOfPlayedGamesAsKeeper);
                } else {
                    bestKeeper.put(player, 0.0);
                }
            }
        });

        final List<Match> allDeathatches = statisticsService.getAllDeathatches(currentSeason);
        final SortedMap<PlayerPair, Long> bestLokBuddies = new TreeMap<>(playerPairComparator);
        allDeathatches.stream().forEach(deathMatch -> {
            final PlayerPair playerPair = new PlayerPair(deathMatch.getKeeperTeam1(), deathMatch.getKeeperTeam2());
            final Long numberOfDeathmatches = bestLokBuddies.get(playerPair);
            if (numberOfDeathmatches != null) {
                bestLokBuddies.put(playerPair, numberOfDeathmatches + 1l);
            } else {
                bestLokBuddies.put(playerPair, 1l);
            }
        });

        // Lokstats
        objectHashMap.put("percentageFormat", percentageFormat);
        objectHashMap.put("minimumMatches", MINIMUM_MATCHES_FOR_STATS);

        objectHashMap.put("seasons", allSeasons);
        objectHashMap.put("currentSeason", currentSeason);

        // games stats
        objectHashMap.put("numberOfGamesList", entriesSortedByValues(numberOfGamesList, true));
        objectHashMap.put("wonMatches", entriesSortedByValues(wonMatches, true));

        objectHashMap.put("lokList", entriesSortedByValues(lokList, true));
        objectHashMap.put("wonDeatchmatches", entriesSortedByValues(wonDeatchmatches, true));

        objectHashMap.put("deathmatchPossibility", entriesSortedByValues(deathmatchPossibility, true));
        objectHashMap.put("lokPossibility", entriesSortedByValues(lokPossibility, true));

        objectHashMap.put("bestStriker", entriesSortedByValues(bestStriker, true));
        objectHashMap.put("bestKeeper", entriesSortedByValues(bestKeeper, true));

        objectHashMap.put("bestLokBuddies", entriesSortedByValues(bestLokBuddies, true));


        return new ModelAndView(objectHashMap, "views/statistics/statistics.vm");
    }

    static <K, V extends Comparable<? super V>>
    SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
        return entriesSortedByValues(map, false);
    }

    static <K, V extends Comparable<? super V>>
    SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map, boolean reverse) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = e1.getValue().compareTo(e2.getValue());
                    int correctedRes = res != 0 ? res : 1;
                    return reverse ? correctedRes * -1 : correctedRes;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    final static Comparator<Player> playerComparator = Comparator.comparing(Player::getWholeName);
    final static Comparator<PlayerPair> playerPairComparator = Comparator.comparing(PlayerPair::getHashable);
}