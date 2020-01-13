package com.bechtle.util;

import com.bechtle.Start;
import com.bechtle.model.*;
import com.bechtle.service.MatchService;
import com.bechtle.service.PlayerService;
import com.bechtle.service.SeasonService;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@WebSocket
public class WebSocketUpdateHandler {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

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
                    processEventControl(message, em);
                    break;
            }

        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private static void processRunningEvent(String message, EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final Optional<Match> currentMatchOpt = matchService.getCurrentMatch();
        final Match currentMatch = currentMatchOpt.orElseGet(() -> createPrematch(em));

        // only process running match event, if really did already begin
        switch (currentMatch.getStatus()) {
            case STARTED:
                int g1 = currentMatch.getGoalsTeam1();
                int g2 = currentMatch.getGoalsTeam2();
                switch (message) {
                    case WebSocketMessages.GOAL_1_UP:
                        if (g1 < getSingleMaxGoalCount(currentMatch) && (g1 + g2) < getSumMaxGoalCount(currentMatch)) {
                            g1++;
                            checkPlaySound(currentMatch, g1, g2);
                        }
                        break;
                    case WebSocketMessages.GOAL_1_DOWN:
                        if (g1 > 0) {
                            g1--;
                        }
                        break;
                    case WebSocketMessages.GOAL_2_UP:
                        if (g2 < getSingleMaxGoalCount(currentMatch) && (g1 + g2) < getSumMaxGoalCount(currentMatch)) {
                            g2++;
                            checkPlaySound(currentMatch, g1, g2);
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

                currentMatch.setGoalsTeam1(g1);
                currentMatch.setGoalsTeam2(g2);
                matchService.updateMatch(currentMatch.getId(), currentMatch.getGoalsTeam1(), currentMatch.getGoalsTeam2());

                sendDataToClient(currentMatch);
                break;

            default:
                break;
        }
    }

    private static void checkPlaySound(Match currentMatch, int g1, int g2) {
        // currently only for regular games
        if (Matchtype.REGULAR.equals(currentMatch.getMatchtype())) {
            if ((g1 + g2) == 4 && (g1 == 0 || g2 == 0)) {
                playFile("003");
            } else if ((g1 + g2) == 5 && (g1 == 1 && currentMatch.getGoalsTeam1() == 0 || g2 == 1 && currentMatch.getGoalsTeam2() == 0)) {
                playFile("01");
            }

            final Set<Long> motherAndDaughter = new HashSet<>(Arrays.asList(2l, 8l));

            // check sheriff baby shark
            boolean team1Matches = motherAndDaughter.contains(new Long(currentMatch.getKeeperTeam1().getId())) &&
                    motherAndDaughter.contains(new Long(currentMatch.getStrikerTeam1().getId()));
            boolean team2Matches = motherAndDaughter.contains(new Long(currentMatch.getKeeperTeam2().getId())) &&
                    motherAndDaughter.contains(new Long(currentMatch.getStrikerTeam2().getId()));
            if (g1 > currentMatch.getGoalsTeam1() && team1Matches ||
                    g2 > currentMatch.getGoalsTeam2() && team2Matches) {
                playFile("855");
            }
        }
    }

    private static void processEventControl(String message, EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final Optional<Match> currentMatchOpt = matchService.getCurrentMatch();
        final Match currentMatch = currentMatchOpt.orElseGet(() -> createPrematch(em));

        switch (currentMatch.getStatus()) {
            case PREMATCH:
                processPrematch(message, currentMatch, em);
                break;
            case STARTED:
                processStarted(message, currentMatch, em);
                break;
            case CANCEL_REQUEST:
                processCancelRequest(message, currentMatch, em);
                break;
            case REMATCH_YES:
            case REMATCH_NO:
                processRematch(message, currentMatch, em);
                break;
            default:
                break;
        }
    }

    private static void processPrematch(String message, Match currentMatch, EntityManager em) {
        final PlayerService playerService = new PlayerService(em);
        final List<Player> selectablePlayers = playerService.getSelectablePlayers(currentMatch);
        List<Player> sortedPlayers = selectablePlayers.stream()
                .sorted(Comparator.comparing(Player::getWholeName))
                .collect(Collectors.toList());
        List<Player> reverseSortedPlayers = selectablePlayers.stream()
                .sorted(Comparator.comparing(Player::getWholeName).reversed())
                .collect(Collectors.toList());

        // get Player to change, fail safe so look for first one and create if it isn't already created
        Player isThereAnyPlayer = getPlayerOfCurrentSlot(currentMatch);
        if (isThereAnyPlayer == null) {
            isThereAnyPlayer = playerService.getActivePlayers().stream()
                    .sorted(Comparator.comparing(Player::getWholeName))
                    .findFirst()
                    .get();
            saveNextFreeSlot(currentMatch, isThereAnyPlayer, em);
        }
        final Player currentPlayerToChange = isThereAnyPlayer;


        switch (message) {
            case WebSocketMessages.BTN_NXT:
                // iter player next
                final Optional<Player> nextPlayerOpt = sortedPlayers.stream()
                        .filter(player -> player.getWholeName().compareTo(currentPlayerToChange.getWholeName()) > 0)
                        .findFirst();

                final Player baseNextPlayer = sortedPlayers.size() > 0 ? sortedPlayers.get(0) : currentPlayerToChange;
                final Player nextPlayer = nextPlayerOpt.orElse(baseNextPlayer);

                // set new current player
                saveCurrentSlot(currentMatch, nextPlayer, em);
                break;

            case WebSocketMessages.BTN_PRVS:
                // iter player previous
                final Optional<Player> previousPlayerOpt = reverseSortedPlayers.stream()
                        .filter(player -> player.getWholeName().compareTo(currentPlayerToChange.getWholeName()) < 0)
                        .findFirst();

                final Player basePreviousPlayer = reverseSortedPlayers.size() > 0 ? reverseSortedPlayers.get(0) : currentPlayerToChange;
                final Player previousPlayer = previousPlayerOpt.orElse(basePreviousPlayer);

                // set new current player
                saveCurrentSlot(currentMatch, previousPlayer, em);
                break;

            case WebSocketMessages.BTN_DOWN:
                // iter player next
                final Optional<Player> upPlayerOpt = sortedPlayers.stream()
                        .filter(player -> player.getWholeName().toLowerCase().charAt(0) > currentPlayerToChange.getWholeName().toLowerCase().charAt(0))
                        .findFirst();

                final Player baseUpPlayer = sortedPlayers.size() > 0 ? sortedPlayers.get(0) : currentPlayerToChange;
                final Player upPlayer = upPlayerOpt.orElse(baseUpPlayer);

                // set new current player
                saveCurrentSlot(currentMatch, upPlayer, em);
                break;

            case WebSocketMessages.BTN_UP:
                // iter player previous
                final List<Player> downPreFilteredPlayers = reverseSortedPlayers.stream()
                        .filter(player -> player.getWholeName().toLowerCase().charAt(0) < currentPlayerToChange.getWholeName().toLowerCase().charAt(0))
                        .collect(Collectors.toList());
                final Player anyDownPlayer = downPreFilteredPlayers.stream().findFirst().orElse(currentPlayerToChange);
                Optional<Player> downPlayerOpt = downPreFilteredPlayers.stream()
                        .filter(player -> player.getWholeName().toLowerCase().charAt(0) == anyDownPlayer.getWholeName().toLowerCase().charAt(0))
                        .sorted(Comparator.comparing(Player::getWholeName))
                        .findFirst();

                final Player baseDownPlayer = reverseSortedPlayers.size() > 0 ? reverseSortedPlayers.get(0) : currentPlayerToChange;
                final Player downPlayer = downPlayerOpt.orElse(baseDownPlayer);

                // set new current player
                saveCurrentSlot(currentMatch, downPlayer, em);
                break;

            case WebSocketMessages.BTN_OK:
                if (allPlayersSet(currentMatch)) {
                    currentMatch.setStatus(Status.STARTED);
                    // in any case update the match
                    em.getTransaction().begin();
                    em.merge(currentMatch);
                    em.getTransaction().commit();
                } else {
                    final Player startPlayer = playerService.getSelectablePlayers(currentMatch).stream()
                            .sorted(Comparator.comparing(Player::getWholeName))
                            .findFirst()
                            .get();

                    saveNextFreeSlot(currentMatch, startPlayer, em);
                }
                break;

            case WebSocketMessages.BTN_CANCEL:
                saveCurrentSlot(currentMatch, null, em);
                break;

            default:
                if ("abcdefghijklmnopqrstuvwxyz".contains(message)) {
                    final List<Player> allPlayers = selectablePlayers.subList(0, selectablePlayers.size());
                    allPlayers.add(currentPlayerToChange);

                    final List<Player> sortedSearchPlayers = allPlayers.stream()
                            .sorted(Comparator.comparing(Player::getWholeName))
                            .filter(player -> player.getWholeName().toLowerCase().startsWith(message))
                            .collect(Collectors.toList());

                    // iter player next
                    final Optional<Player> searchNextPlayerOpt = sortedSearchPlayers.stream()
                            .filter(player -> player.getWholeName().compareTo(currentPlayerToChange.getWholeName()) > 0)
                            .findFirst();

                    final Player baseSearchPlayer = sortedSearchPlayers.size() > 0 ? sortedSearchPlayers.get(0) : currentPlayerToChange;
                    final Player searchPlayer = searchNextPlayerOpt.orElse(baseSearchPlayer);

                    // set new current player
                    saveCurrentSlot(currentMatch, searchPlayer, em);
                }
                break;
        }

        sendDataToClient(currentMatch);
    }

    private static void processStarted(String message, Match currentMatch, EntityManager em) {
        final MatchService matchService = new MatchService(em);

        switch (message) {
            case WebSocketMessages.BTN_OK:
                if (matchIsFinishable(currentMatch)) {
                    final Optional<Match> followUp = matchService.finishMatch(currentMatch.getId());
                    if (followUp.isPresent()) {
                        // deathmatch will follow
                        playFile("0");
                        sendDataToClient(followUp.get());
                    } else {
                        currentMatch.setStatus(Status.REMATCH_NO);
                        // in any case update the match
                        em.getTransaction().begin();
                        em.merge(currentMatch);
                        em.getTransaction().commit();
                        sendDataToClient(currentMatch);
                    }
                }
                break;

            case WebSocketMessages.BTN_CANCEL:
                if (Matchtype.REGULAR.equals(currentMatch.getMatchtype())) {
                    currentMatch.setStatus(Status.CANCEL_REQUEST);
                    // in any case update the match
                    em.getTransaction().begin();
                    em.merge(currentMatch);
                    em.getTransaction().commit();
                    sendDataToClient(currentMatch);
                }
                break;

            default:
                break;
        }
    }

    private static void processRematch(String message, Match currentMatch, EntityManager em) {
        final MatchService matchService = new MatchService(em);

        switch (message) {
            case WebSocketMessages.BTN_NXT:
            case WebSocketMessages.BTN_PRVS:
                if (Status.REMATCH_YES.equals(currentMatch.getStatus())) {
                    currentMatch.setStatus(Status.REMATCH_NO);
                } else {
                    currentMatch.setStatus(Status.REMATCH_YES);
                }
                // in any case update the match
                em.getTransaction().begin();
                em.merge(currentMatch);
                em.getTransaction().commit();
                sendDataToClient(currentMatch);
                break;

            case WebSocketMessages.BTN_OK:
                if (Status.REMATCH_YES.equals(currentMatch.getStatus())) {
                    final Optional<Match> followUp = matchService.finishMatch(currentMatch.getId());
                    sendDataToClient(followUp.orElseGet(() -> createRematch(em)));
                } else {
                    final Optional<Match> followUp = matchService.finishMatch(currentMatch.getId());
                    sendDataToClient(followUp.orElseGet(() -> createPrematch(em)));
                }
                break;

            case WebSocketMessages.BTN_CANCEL:
                if (Matchtype.REGULAR.equals(currentMatch.getMatchtype())) {
                    currentMatch.setStatus(Status.STARTED);
                    // in any case update the match
                    em.getTransaction().begin();
                    em.merge(currentMatch);
                    em.getTransaction().commit();
                    sendDataToClient(currentMatch);
                }
                break;

            default:
                break;
        }
    }

    private static void processCancelRequest(String message, Match currentMatch, EntityManager em) {
        switch (message) {
            case WebSocketMessages.BTN_OK:
                currentMatch.setStatus(Status.PREMATCH);
                currentMatch.setGoalsTeam1(0);
                currentMatch.setGoalsTeam2(0);
                // in any case update the match
                em.getTransaction().begin();
                em.merge(currentMatch);
                em.getTransaction().commit();
                sendDataToClient(currentMatch);
                break;

            case WebSocketMessages.BTN_CANCEL:
                currentMatch.setStatus(Status.STARTED);
                // in any case update the match
                em.getTransaction().begin();
                em.merge(currentMatch);
                em.getTransaction().commit();
                sendDataToClient(currentMatch);
                break;

            default:
                break;
        }
    }

    private static boolean matchIsFinishable(Match match) {
        int g1 = match.getGoalsTeam1();
        int g2 = match.getGoalsTeam2();
        return Status.STARTED.equals(match.getStatus())
                && (g1 >= getSingleMaxGoalCount(match)
                || g2 >= getSingleMaxGoalCount(match));

    }

    private static boolean allPlayersSet(Match match) {
        return match.getKeeperTeam1() != null
                && match.getStrikerTeam1() != null
                && match.getKeeperTeam2() != null
                && match.getStrikerTeam2() != null;
    }

    private static void sendDataToClient(Match currentMatch) {
        final Player kt1 = currentMatch.getKeeperTeam1();
        final Player st1 = currentMatch.getStrikerTeam1();
        final Player kt2 = currentMatch.getKeeperTeam2();
        final Player st2 = currentMatch.getStrikerTeam2();

        // send to websocket
        sessions.stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(
                        String.valueOf(new JSONObject()
                                .put("goalsTeam1", currentMatch.getGoalsTeam1())
                                .put("goalsTeam2", currentMatch.getGoalsTeam2())
                                .put("keeperTeam1", createPlayerJson(kt1))
                                .put("strikerTeam1", createPlayerJson(st1))
                                .put("keeperTeam2", createPlayerJson(kt2))
                                .put("strikerTeam2", createPlayerJson(st2))
                                .put("status", currentMatch.getStatus().toString())
                                .put("matchtype", currentMatch.getMatchtype().name())
                                .put("finishable", matchIsFinishable(currentMatch))
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static JSONObject createPlayerJson(Player player) {
        if (player == null) {
            return new JSONObject()
                    .put("forename", "")
                    .put("nickname", "")
                    .put("surname", "")
                    .put("id", "");
        } else {
            return new JSONObject()
                    .put("forename", player.getForename())
                    .put("nickname", player.getNickname())
                    .put("surname", player.getSurname())
                    .put("id", player.getId());
        }
    }

    private static void saveNextFreeSlot(Match currentMatch, Player nextPlayer, EntityManager em) {
        if (currentMatch.getKeeperTeam1() == null) {
            currentMatch.setKeeperTeam1(nextPlayer);
        } else if (currentMatch.getStrikerTeam1() == null) {
            currentMatch.setStrikerTeam1(nextPlayer);
        } else if (currentMatch.getStrikerTeam2() == null) {
            currentMatch.setStrikerTeam2(nextPlayer);
        } else if (currentMatch.getKeeperTeam2() == null) {
            currentMatch.setKeeperTeam2(nextPlayer);
        }

        // in any case update the match
        em.getTransaction().begin();
        em.merge(currentMatch);
        em.merge(nextPlayer);
        em.getTransaction().commit();
    }

    private static void saveCurrentSlot(Match currentMatch, Player currentPlayer, EntityManager em) {
        if (currentMatch.getKeeperTeam2() != null) {
            currentMatch.setKeeperTeam2(currentPlayer);
        } else if (currentMatch.getStrikerTeam2() != null) {
            currentMatch.setStrikerTeam2(currentPlayer);
        } else if (currentMatch.getStrikerTeam1() != null) {
            currentMatch.setStrikerTeam1(currentPlayer);
        } else if (currentPlayer != null) {
            // you can set the first slot but cannot unset it with the cancel button
            currentMatch.setKeeperTeam1(currentPlayer);
        }

        // in any case update the match
        em.getTransaction().begin();
        em.merge(currentMatch);
        em.getTransaction().commit();
    }

    private static Player getPlayerOfCurrentSlot(Match currentMatch) {
        if (currentMatch.getKeeperTeam2() != null) {
            return currentMatch.getKeeperTeam2();
        } else if (currentMatch.getStrikerTeam2() != null) {
            return currentMatch.getStrikerTeam2();
        } else if (currentMatch.getStrikerTeam1() != null) {
            return currentMatch.getStrikerTeam1();
        } else {
            return currentMatch.getKeeperTeam1();
        }
    }

    private static Match createPrematch(EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final PlayerService playerService = new PlayerService(em);

        final Player startPlayer = playerService.getActivePlayers().stream()
                .sorted(Comparator.comparing(Player::getWholeName))
                .findFirst()
                .get();

        return matchService.createPreMatch(startPlayer, Matchtype.REGULAR, getCurrentSeason(em));
    }

    private static Match createRematch(EntityManager em) {
        final MatchService matchService = new MatchService(em);
        final Optional<Match> lastRegularMatchOption = matchService.getLastRegularMatch();

        return lastRegularMatchOption.map(lastReqularMatch -> matchService.createMatch(
                lastReqularMatch.getKeeperTeam1(),
                lastReqularMatch.getStrikerTeam1(),
                lastReqularMatch.getKeeperTeam2(),
                lastReqularMatch.getStrikerTeam2(),
                getCurrentSeason(em)
        )).orElseGet(() -> createPrematch(em));
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

    private static int getSingleMaxGoalCount(Match match) {
        switch (match.getMatchtype()) {
            case REGULAR:
                return 5;
            case DEATH_MATCH_BO3:
                return 2;
            default:
                return 1;
        }
    }

    private static int getSumMaxGoalCount(Match match) {
        switch (match.getMatchtype()) {
            case REGULAR:
                return 9;
            case DEATH_MATCH_BO3:
                return 3;
            default:
                return 1;
        }
    }

    private static void playFile(String number) {
        final Map<String, String> videoMap = new HashMap<>();
        //42["queue-add",{"fullname":"01 gerettet.mp4","number":"01","previewImageUrl":"/previews/01 gerettet.png","animatedPreviewUrl":"/previews/01 gerettet.gif"}]
        switch (number) {
            case "0":
                videoMap.put("fullname", "0 Lok.mp4");
                videoMap.put("number", "0");
                videoMap.put("previewImageUrl", "/previews/0 Lok.png");
                videoMap.put("animatedPreviewUrl", "/previews/0 Lok.gif");
                break;
            case "01":
                videoMap.put("fullname", "01 gerettet.mp4");
                videoMap.put("number", "01");
                videoMap.put("previewImageUrl", "/previews/01 gerettet.png");
                videoMap.put("animatedPreviewUrl", "/previews/01 gerettet.gif");
                break;
            case "003":
                videoMap.put("fullname", "003 drei.mp4");
                videoMap.put("number", "003");
                videoMap.put("previewImageUrl", "/previews/003 drei.png");
                videoMap.put("animatedPreviewUrl", "previews/003 drei.gif");
                break;
            case "855":
                videoMap.put("fullname", "855-huckepack.mp4");
                videoMap.put("number", "855");
                videoMap.put("previewImageUrl", "/previews/855-huckepack.png");
                videoMap.put("animatedPreviewUrl", "previews/855-huckepack.gif");
                break;
            default:
                break;
        }

        try {
            final Socket socket = IO.socket("http://10.1.50.160:9001");
            socket.on(Socket.EVENT_CONNECT, args -> {
                JSONObject obj = new JSONObject();
                obj.put("fullname", videoMap.get("fullname"));
                obj.put("number", videoMap.get("number"));
                obj.put("previewImageUrl", videoMap.get("previewImageUrl"));
                obj.put("animatedPreviewUrl", videoMap.get("animatedPreviewUrl"));

                socket.emit("queue-add", obj);
                socket.disconnect();
            });
            socket.connect();
        } catch (Exception ex) {
        }
    }
}
