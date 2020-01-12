package com.bechtle.controller;

import com.bechtle.controller.util.PlayerPair;
import com.bechtle.model.*;
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
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatisticsController {

    private static Long MINIMUM_MATCHES_FOR_STATS = 20l;

    public static ModelAndView getStats(Request request, Response response) {
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);
        final SeasonService seasonService = new SeasonService(em);
        final StatisticsService statisticsService = new StatisticsService(em);

        final HashMap<String, Object> objectHashMap = new HashMap<>();

        final List<Player> players = playerService.getActivePlayers();
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
        final SortedMap<Player, Double> versatility = new TreeMap<>(playerComparator);
        final SortedMap<Player, Double> bestLokConductorSingle = new TreeMap<>(playerComparator);
        final SortedMap<Player, Double> colorful = new TreeMap<>(playerComparator);

        final SortedMap<Player, Double> lokOfTheYear = new TreeMap<>(playerComparator);

        players.stream().forEach(player -> {
            final long numberOfPlayedGamesForPlayer = statisticsService.getNumberOfPlayedGamesForPlayer(player, currentSeason);

            numberOfGamesList.put(player, numberOfPlayedGamesForPlayer);
            // only count statistics for player with more then 10 played matches!
            if (numberOfPlayedGamesForPlayer > MINIMUM_MATCHES_FOR_STATS) {
                final List<Match> allMatchesForPlayer = statisticsService.getAllMatches(player, currentSeason);
                final List<Match> deathmachtesForPlayer = statisticsService.getDeathmachesForPlayer(player, currentSeason);
                final List<Match> lostDeathmachtesForPlayer = statisticsService.getLostDeathmachesForPlayer(player, currentSeason);
                final long numberOfWonGamesForPlayer = statisticsService.getNumberOfWonGamesForPlayer(player, currentSeason);
                final long numberOfPlayedGamesAsKeeper = statisticsService.getNumberOfPlayedGamesAsKeeper(player, currentSeason);
                final long numberOfGoalsShotTotal = getNumberOfGoalsShotAsATeam(allMatchesForPlayer, player);
                final long numberOfGoalsConcededAsKeeper = getNumberOfGoalsConcededAsKeeper(allMatchesForPlayer, player);
                final double playersVersatility = getPlayersVersatility(allMatchesForPlayer, player);
                final double playersColors = getPlayersColors(allMatchesForPlayer, player);
                final long numberOfLoksConducted = getNumberOfLoksConducted(allMatchesForPlayer, player);

                lokList.put(player, (long) lostDeathmachtesForPlayer.size());
                deathmatchPossibility.put(player, 1.0 * deathmachtesForPlayer.size() / numberOfPlayedGamesForPlayer);
                lokPossibility.put(player, 1.0 * lostDeathmachtesForPlayer.size() / numberOfPlayedGamesForPlayer);
                wonMatches.put(player, 1.0 * numberOfWonGamesForPlayer / numberOfPlayedGamesForPlayer);
                bestStriker.put(player, 1.0 * numberOfGoalsShotTotal / numberOfPlayedGamesForPlayer);
                versatility.put(player, playersVersatility);
                bestLokConductorSingle.put(player, 1.0 * numberOfLoksConducted / numberOfPlayedGamesForPlayer);
                colorful.put(player, playersColors);

                if (!deathmachtesForPlayer.isEmpty()) {
                    wonDeatchmatches.put(player, 1.0 * (deathmachtesForPlayer.size() - lostDeathmachtesForPlayer.size()) / deathmachtesForPlayer.size());
                }

                if (numberOfPlayedGamesAsKeeper > MINIMUM_MATCHES_FOR_STATS) {
                    bestKeeper.put(player, 1.0 * numberOfGoalsConcededAsKeeper / numberOfPlayedGamesAsKeeper);
                }

                lokOfTheYear.put(player, lokPossibility.get(player) * 100);
            }
        });

        final List<Match> allMatches = currentSeason.getMatches();

        final SortedMap<PlayerPair, Double> mostWinningTeam = getMostWinningTeam(allMatches);

        final SortedMap<PlayerPair, Long> bestLokBuddies = getBestLokBuddies(allMatches);
        final SortedMap<PlayerPair, Long> bestLokConductors = getBestLokConductors(allMatches);

        final SortedMap<String, Double> colors = getColors(allMatches);

        final NumberFormat percentageFormat = NumberFormat.getPercentInstance();
        percentageFormat.setMinimumFractionDigits(2);
        final NumberFormat ratioFormat = NumberFormat.getInstance();
        ratioFormat.setMinimumFractionDigits(0);
        ratioFormat.setMaximumFractionDigits(0);
        final NumberFormat doubleFormat = NumberFormat.getInstance();
        doubleFormat.setMinimumFractionDigits(2);
        doubleFormat.setMaximumFractionDigits(2);

        // Lokstats
        objectHashMap.put("percentageFormat", percentageFormat);
        objectHashMap.put("ratioFormat", ratioFormat);
        objectHashMap.put("doubleFormat", doubleFormat);
        objectHashMap.put("minimumMatches", MINIMUM_MATCHES_FOR_STATS);

        objectHashMap.put("seasons", allSeasons);
        objectHashMap.put("currentSeason", currentSeason);

        // games stats
        objectHashMap.put("lokOfTheYear", entriesSortedByValues(lokOfTheYear, true));

        objectHashMap.put("numberOfGamesList", entriesSortedByValues(numberOfGamesList, true));
        objectHashMap.put("wonMatches", entriesSortedByValues(wonMatches, true));

        objectHashMap.put("lokList", entriesSortedByValues(lokList, true));
        objectHashMap.put("wonDeatchmatches", entriesSortedByValues(wonDeatchmatches, true));

        objectHashMap.put("deathmatchPossibility", entriesSortedByValues(deathmatchPossibility, true));
        objectHashMap.put("lokPossibility", entriesSortedByValues(lokPossibility, true));

        objectHashMap.put("bestStriker", entriesSortedByValues(bestStriker, true));
        objectHashMap.put("bestKeeper", entriesSortedByValues(bestKeeper));

        objectHashMap.put("versatility", entriesSortedByAbsValues(versatility));
        objectHashMap.put("colorful", entriesSortedByAbsValues(colorful));

        objectHashMap.put("mostWinningTeam", entriesSortedByValues(mostWinningTeam, true));
        objectHashMap.put("bestLokConductorSingle", entriesSortedByValues(bestLokConductorSingle, true));

        objectHashMap.put("bestLokBuddies", entriesSortedByValues(bestLokBuddies, true));
        objectHashMap.put("bestLokConductors", entriesSortedByValues(bestLokConductors, true));

        objectHashMap.put("colors", entriesSortedByValues(colors, true));

        return new ModelAndView(objectHashMap, "views/statistics/statistics.vm");
    }

    private static double getPlayersVersatility(List<Match> allMatchesForPlayer, Player player) {
        long playersTrend = (long) allMatchesForPlayer.stream()
                .map(m -> {
                    if (player.getId() == m.getStrikerTeam1().getId() || player.getId() == m.getStrikerTeam2().getId()) {
                        // player was playing as striker
                        return 1;
                    } else {
                        // player was playing as keeper
                        return -1;
                    }
                })
                .reduce(0, Integer::sum);

        return 1.0 * playersTrend / allMatchesForPlayer.size();
    }

    private static double getPlayersColors(List<Match> allMatchesForPlayer, Player player) {
        long playersTrend = (long) allMatchesForPlayer.stream()
                .map(m -> {
                    if (player.getId() == m.getKeeperTeam2().getId() || player.getId() == m.getStrikerTeam2().getId()) {
                        // player was playing in team 2
                        return 1;
                    } else {
                        // player was playing in team 1
                        return -1;
                    }
                })
                .reduce(0, Integer::sum);

        return 1.0 * playersTrend / allMatchesForPlayer.size();
    }

    private static long getNumberOfGoalsShotAsATeam(List<Match> allMatchesForPlayer, Player player) {
        return (long) allMatchesForPlayer.stream()
                .map(m -> {
                    if (player.getId() == m.getKeeperTeam1().getId() || player.getId() == m.getStrikerTeam1().getId()) {
                        // player was playing in team 1
                        return m.getGoalsTeam1();
                    } else {
                        // player was playing in team 2
                        return m.getGoalsTeam2();
                    }
                })
                .reduce(0, Integer::sum);
    }

    private static long getNumberOfGoalsConcededAsKeeper(List<Match> allMatchesForPlayer, Player player) {
        return (long) allMatchesForPlayer.stream()
                .filter(m -> m.getKeeperTeam1().getId() == player.getId() || m.getKeeperTeam2().getId() == player.getId())
                .map(m -> {
                    if (player.getId() == m.getKeeperTeam1().getId()) {
                        // player was keeper in team 1
                        return m.getGoalsTeam2();
                    } else {
                        // player was keeper in team 2
                        return m.getGoalsTeam1();
                    }
                })
                .reduce(0, Integer::sum);
    }

    private static SortedMap<PlayerPair, Double> getMostWinningTeam(List<Match> allMatches) {
        List<Match> allRegularMatches = allMatches.stream()
                .filter(m -> m.getMatchtype() == Matchtype.REGULAR)
                .filter(m -> m.getStatus() == Status.FINISHED)
                .collect(Collectors.toList());

        Map<PlayerPair, Integer> wonMatches = allRegularMatches.stream()
                .map(match -> {
                    if (match.getGoalsTeam1() > match.getGoalsTeam2()) {
                        return new PlayerPair(match.getKeeperTeam1(), match.getStrikerTeam1());
                    } else {
                        return new PlayerPair(match.getKeeperTeam2(), match.getStrikerTeam2());
                    }
                })
                .collect(Collectors.groupingBy(Function.identity()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        Map<PlayerPair, Integer> lostMatches = allRegularMatches.stream()
                .map(match -> {
                    if (match.getGoalsTeam1() < match.getGoalsTeam2()) {
                        return new PlayerPair(match.getKeeperTeam1(), match.getStrikerTeam1());
                    } else {
                        return new PlayerPair(match.getKeeperTeam2(), match.getStrikerTeam2());
                    }
                })
                .collect(Collectors.groupingBy(Function.identity()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));

        Set<PlayerPair> allPlayerPairs = new HashSet<>();
        allPlayerPairs.addAll(wonMatches.keySet());
        allPlayerPairs.addAll(lostMatches.keySet());

        final SortedMap<PlayerPair, Double> mostWinningTeam = new TreeMap<>(playerPairComparator);
        allPlayerPairs.stream().forEach(playerPair -> {

            Integer matchesWon = wonMatches.getOrDefault(playerPair, 0);
            Integer matchesPlayed = matchesWon + lostMatches.getOrDefault(playerPair, 0);

            if (matchesPlayed > MINIMUM_MATCHES_FOR_STATS) {
                mostWinningTeam.put(playerPair, 1.0 * matchesWon / matchesPlayed);
            }
        });

        return mostWinningTeam;
    }

    private static long getNumberOfLoksConducted(List<Match> allMatchesForPlayer, Player player) {
        return (long) allMatchesForPlayer.stream()
                .filter(m -> (m.getGoalsTeam1() + m.getGoalsTeam2()) == 5) // only lok matches
                .map(m -> {
                    if (player.getId() == m.getKeeperTeam1().getId() ||
                    player.getId() == m.getStrikerTeam1().getId()) {
                        // player was in team 1
                        return (m.getGoalsTeam1() == 5 ? 1 : 0);
                    } else {
                        // player was in team 2
                        return (m.getGoalsTeam2() == 5 ? 1 : 0);
                    }
                })
                .reduce(0, Integer::sum);
    }

    private static SortedMap<PlayerPair, Long> getBestLokConductors(List<Match> allMatches) {
        List<Match> allRegularMatches = allMatches.stream()
                .filter(m -> m.getMatchtype() == Matchtype.REGULAR)
                .filter(m -> m.getStatus() == Status.FINISHED)
                .filter(m -> (m.getGoalsTeam1() + m.getGoalsTeam2()) == 5)
                .collect(Collectors.toList());
        final SortedMap<PlayerPair, Long> bestLokConductors = new TreeMap<>(playerPairComparator);
        allRegularMatches.stream().forEach(match -> {
            final PlayerPair playerPair;
            if (match.getGoalsTeam1() > match.getGoalsTeam2()) {
                playerPair = new PlayerPair(match.getKeeperTeam1(), match.getStrikerTeam1());
            } else {
                playerPair = new PlayerPair(match.getKeeperTeam2(), match.getStrikerTeam2());
            }
            final Long loksConducted = bestLokConductors.get(playerPair);
            if (loksConducted != null) {
                bestLokConductors.put(playerPair, loksConducted + 1l);
            } else {
                bestLokConductors.put(playerPair, 1l);
            }
        });

        return bestLokConductors;
    }

    private static SortedMap<PlayerPair, Long> getBestLokBuddies(List<Match> allMatches) {
        List<Match> allDeathmatchesPlayed = allMatches.stream()
                .filter(m -> m.getMatchtype() == Matchtype.DEATH_MATCH || m.getMatchtype() == Matchtype.DEATH_MATCH_BO3)
                .filter(m -> m.getStatus() == Status.FINISHED)
                .collect(Collectors.toList());
        final SortedMap<PlayerPair, Long> bestLokBuddies = new TreeMap<>(playerPairComparator);
        allDeathmatchesPlayed.stream().forEach(deathMatch -> {
            final PlayerPair playerPair = new PlayerPair(deathMatch.getKeeperTeam1(), deathMatch.getKeeperTeam2());
            final Long numberOfDeathmatches = bestLokBuddies.get(playerPair);
            if (numberOfDeathmatches != null) {
                bestLokBuddies.put(playerPair, numberOfDeathmatches + 1l);
            } else {
                bestLokBuddies.put(playerPair, 1l);
            }
        });

        return bestLokBuddies;
    }

    private static SortedMap<String, Double> getColors(List<Match> allMatches) {
        final List<Match> allRegularMatches = allMatches.stream()
                .filter(m -> m.getMatchtype() == Matchtype.REGULAR)
                .filter(m -> m.getStatus() == Status.FINISHED)
                .collect(Collectors.toList());
        final SortedMap<String, Double> colors = new TreeMap<>();
        final long whiteTeamWonTotal = allRegularMatches.stream()
                .filter(m -> m.getGoalsTeam1() > m.getGoalsTeam2())
                .count();
        colors.put("White team", 1.0 * whiteTeamWonTotal / allRegularMatches.size());
        colors.put("Black team", 1.0 * (allRegularMatches.size() - whiteTeamWonTotal) / allRegularMatches.size());

        return colors;
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

    static <K>
    SortedSet<Map.Entry<K, Double>> entriesSortedByAbsValues(Map<K, Double> map) {
        return entriesSortedByAbsValues(map, false);
    }

    static <K>
    SortedSet<Map.Entry<K, Double>> entriesSortedByAbsValues(Map<K, Double> map, boolean reverse) {
        SortedSet<Map.Entry<K, Double>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = Double.valueOf(Math.abs(e1.getValue())).compareTo(Double.valueOf(Math.abs(e2.getValue())));
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