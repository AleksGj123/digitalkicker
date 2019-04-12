package com.bechtle.util;

import com.bechtle.Start;
import com.bechtle.model.*;
import com.bechtle.service.MatchService;
import com.bechtle.service.PlayerService;
import com.bechtle.service.SeasonService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@WebSocket
public class WebSocketUpdateHandler {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    static Map<Session, String> listeners = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void connected(Session session) {

        //System.out.println("someone conneced");
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        //System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }

    public static void broadcastMessage(String channel, String message) {
        EntityManager em = null;
        try {
            em = Start.factory.createEntityManager();


            switch (channel) {
                case WebSocketChannels.RUNNING_EVENT:
                    processRunningEvent(message, em);
                    break;
                case WebSocketChannels.EVENT_CONTROL:
                    processEventControll(message, em);
                    break;
            }

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private static void processEventControll(String message, EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final Match currentMatch = matchService.getCurrentMatch();

        switch (currentMatch.getStatus()) {
            case PREMATCH:
                processPrematch(message, currentMatch, em);
                break;
            case STARTED:
                processStarted(message, currentMatch, em);
                break;
            case FINISHED:
                break;
            default:
                break;
        }
    }

    private static void processStarted(String message, Match currentMatch, EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final PlayerService playerService = new PlayerService(em);
        final Season currentSeason = getCurrentSeason(em);

        switch (message) {
            case WebSocketMessages.BTN_OK:

                if (matchIsFinishable(currentMatch)) {

                    matchService.finishMatch(currentMatch.getId());

                    final Player startPlayer = playerService.getPlayers().stream()
                            .sorted(Comparator.comparing(Player::getId))
                            .findFirst()
                            .get();

                    matchService.createPreMatch(startPlayer, Matchtype.REGULAR, currentSeason);
                    final Match newlyCurrentMatch = matchService.getCurrentMatch();

                    sendDataToClient(newlyCurrentMatch);
                }
                break;
            default:
                break;
        }
    }

    private static boolean matchIsFinishable(Match match) {
        int g1 = match.getGoalsTeam1();
        int g2 = match.getGoalsTeam2();
        return g1 >= getMaxGoalCount(match) || g2 >= getMaxGoalCount(match);

    }

    private static void processPrematch(String message, Match currentMatch, EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final PlayerService playerService = new PlayerService(em);
        final Season currentSeason = getCurrentSeason(em);
        final List<Player> allPlayers = playerService.getSelectablePlayers(currentMatch);

        // get Player to change
        Player isThereAnyPlayer = getCurrentPlayerToChange(currentMatch);

        if (isThereAnyPlayer == null) {
            isThereAnyPlayer = playerService.getPlayers().stream()
                    .sorted(Comparator.comparing(Player::getId))
                    .findFirst()
                    .get();

            saveNextFreeSlot(currentMatch, isThereAnyPlayer, em);
        }

        final Player currentPlayerToChange = isThereAnyPlayer;


        switch (message) {
            case WebSocketMessages.BTN_NXT:

                // iter player next
                List<Player> sortedPlayers = allPlayers.stream()
                        .sorted(Comparator.comparing(Player::getId))
                        .collect(Collectors.toList());

                final Optional<Player> nextPlayerOpt = sortedPlayers.stream()
                        .filter(player -> player.getId() > currentPlayerToChange.getId())
                        .findFirst();

                final Player baseNextPlayer = sortedPlayers.size() > 0 ? sortedPlayers.get(0) : currentPlayerToChange;
                final Player nextPlayer = nextPlayerOpt.orElse(baseNextPlayer);

                // set new current player
                saveCurrentPlayerToChange(currentMatch, nextPlayer, em);
                break;

            case WebSocketMessages.BTN_PRVS:

                // iter player next
                List<Player> reverseSortedPlayers = allPlayers.stream()
                        .sorted(Comparator.comparing(Player::getId).reversed())
                        .collect(Collectors.toList());

                final Optional<Player> previousPlayerOpt = reverseSortedPlayers.stream()
                        .filter(player -> player.getId() < currentPlayerToChange.getId())
                        .findFirst();

                final Player basePreviousPlayer = reverseSortedPlayers.size() > 0 ? reverseSortedPlayers.get(0) : currentPlayerToChange;
                final Player previousPlayer = previousPlayerOpt.orElse(basePreviousPlayer);

                // set new current player
                saveCurrentPlayerToChange(currentMatch, previousPlayer, em);
                break;

            case WebSocketMessages.BTN_OK:

                if (currentMatch.getKeeperTeam1() != null && currentMatch.getStrikerTeam1() != null && currentMatch.getKeeperTeam2() != null && currentMatch.getStrikerTeam2() != null) {
                    currentMatch.setStatus(Status.STARTED);

                    // in any case update the match
                    em.getTransaction().begin();
                    em.merge(currentMatch);
                    em.getTransaction().commit();
                } else {
                    final Player startPlayer = playerService.getSelectablePlayers(currentMatch).stream()
                            .sorted(Comparator.comparing(Player::getId))
                            .findFirst()
                            .get();

                    saveNextFreeSlot(currentMatch, startPlayer, em);
                }
                break;
        }

        sendDataToClient(currentMatch);
    }

    private static void sendDataToClient(Match currentMatch) {
        final Player t1p1 = currentMatch.getKeeperTeam1();
        final Player t1p2 = currentMatch.getStrikerTeam1();
        final Player t2p1 = currentMatch.getKeeperTeam2();
        final Player t2p2 = currentMatch.getStrikerTeam2();

        final String t1p1String = t1p1 != null ? t1p1.getForename() + " (" + t1p1.getNickname() + ") " + t1p1.getSurname() : "";
        final String t1p2String = t1p2 != null ? t1p2.getForename() + " (" + t1p2.getNickname() + ") " + t1p2.getSurname() : "";
        final String t2p1String = t2p1 != null ? t2p1.getForename() + " (" + t2p1.getNickname() + ") " + t2p1.getSurname() : "";
        final String t2p2String = t2p2 != null ? t2p2.getForename() + " (" + t2p2.getNickname() + ") " + t2p2.getSurname() : "";


        // send to websocket
        sessions.stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(
                        String.valueOf(new JSONObject()
                                .put("goalsTeam1", currentMatch.getGoalsTeam1())
                                .put("goalsTeam2", currentMatch.getGoalsTeam2())
                                .put("t1p1", t1p1String)
                                .put("t1p2", t1p2String)
                                .put("t2p1", t2p1String)
                                .put("t2p2", t2p2String)
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void saveNextFreeSlot(Match currentMatch, Player nextPlayer, EntityManager em) {
        if (currentMatch.getKeeperTeam1() == null) {
            currentMatch.setKeeperTeam1(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        } else if (currentMatch.getStrikerTeam1() == null) {
            currentMatch.setStrikerTeam1(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        } else if (currentMatch.getKeeperTeam2() == null) {
            currentMatch.setKeeperTeam2(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        } else if (currentMatch.getStrikerTeam2() == null) {
            currentMatch.setStrikerTeam2(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        }

        // in any case update the match
        em.getTransaction().begin();
        em.merge(currentMatch);
        em.merge(nextPlayer);
        em.getTransaction().commit();
    }

    private static void saveCurrentPlayerToChange(Match currentMatch, Player nextPlayer, EntityManager em) {
        Player previousPlayer;

        if (currentMatch.getStrikerTeam2() != null) {
            previousPlayer = currentMatch.getStrikerTeam2();
            previousPlayer.getMatches().remove(currentMatch);
            currentMatch.setStrikerTeam2(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        } else if (currentMatch.getKeeperTeam2() != null) {
            previousPlayer = currentMatch.getKeeperTeam2();
            previousPlayer.getMatches().remove(currentMatch);
            currentMatch.setKeeperTeam2(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        } else if (currentMatch.getStrikerTeam1() != null) {
            previousPlayer = currentMatch.getStrikerTeam1();
            previousPlayer.getMatches().remove(currentMatch);
            currentMatch.setStrikerTeam1(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        } else {
            previousPlayer = currentMatch.getKeeperTeam1();
            previousPlayer.getMatches().remove(currentMatch);
            currentMatch.setKeeperTeam1(nextPlayer);
            nextPlayer.getMatches().add(currentMatch);
        }

        // in any case update the match
        em.getTransaction().begin();
        em.merge(currentMatch);
        em.merge(previousPlayer);
        em.merge(nextPlayer);
        em.getTransaction().commit();
    }

    private static Player getCurrentPlayerToChange(Match currentMatch) {
        if (currentMatch.getStrikerTeam2() != null) {
            return currentMatch.getStrikerTeam2();
        } else if (currentMatch.getKeeperTeam2() != null) {
            return currentMatch.getKeeperTeam2();
        } else if (currentMatch.getStrikerTeam1() != null) {
            return currentMatch.getStrikerTeam1();
        } else {
            return currentMatch.getKeeperTeam1();
        }
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

    private static void processRunningEvent(String message, EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final Match currentMatch = matchService.getCurrentMatch();

        int g1 = currentMatch.getGoalsTeam1();
        int g2 = currentMatch.getGoalsTeam2();
        if (currentMatch.getStatus() == Status.STARTED) {
            switch (message) {
                case WebSocketMessages.GOAL_1_UP:
                    if (g1 < getMaxGoalCount(currentMatch)) {
                        g1++;
                    }
                    break;
                case WebSocketMessages.GOAL_1_DOWN:
                    if (g1 > 0) {
                        g1--;
                    }
                    break;
                case WebSocketMessages.GOAL_2_UP:
                    if (g2 < getMaxGoalCount(currentMatch)) {
                        g2++;
                    }
                    break;
                case WebSocketMessages.GOAL_2_DOWN:
                    if (g2 > 0) {
                        g2--;
                    }
                    break;
                default:
                    break;
            }
        }

        currentMatch.setGoalsTeam1(g1);
        currentMatch.setGoalsTeam2(g2);
        matchService.updateMatch(currentMatch.getId(), currentMatch.getGoalsTeam1(), currentMatch.getGoalsTeam2());

        sessions.stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(
                        String.valueOf(new JSONObject()
                                .put("goalsTeam1", currentMatch.getGoalsTeam1())
                                .put("goalsTeam2", currentMatch.getGoalsTeam2()))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private static int getMaxGoalCount(Match match) {
        switch (match.getMatchtype()) {
            case REGULAR:
                return 5;
            case DEATH_MATCH_BO3:
                return 2;
            default:
                return 1;
        }
    }
}
