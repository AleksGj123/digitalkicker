package com.bechtle.service;

import com.bechtle.model.*;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.bechtle.model.Matchtype.*;

public class MatchService extends Service {

    public MatchService(EntityManager em) {
        super(em);
    }

    public List<Match> getAllMatches() {
        final List<Match> allMatches = em.createQuery("select m from Matches as m order by m.id desc").getResultList();
        return allMatches;
    }

    public Optional<Match> getCurrentMatch() {
        final List<Match> activeMatches = em.createQuery("select m from Matches as m where m.status != 1 order by m.id desc").getResultList();
        Optional<Match> first = activeMatches.stream().findFirst();
        return first;
    }

    public Optional<Match> getLastRegularMatch() {
        final List<Match> activeMatches = em.createQuery("select m from Matches as m where m.matchtype = 'R' order by m.id desc").getResultList();
        Optional<Match> first = activeMatches.stream().findFirst();
        return first;
    }

    /**
     * Delete a match by id
     *
     * @param matchId the Id of the match you want to delete
     * @return false if match could not be delete because it was not found
     */
    public boolean deleteMatch(Long matchId) {

        em.getTransaction().begin();
        final Match match = em.find(Match.class, matchId);
        em.remove(match);
        em.getTransaction().commit();

        return true;
    }

    public Match getMatch(Long id) {
        final Match match = em.find(Match.class, id);
        return match;
    }

    public boolean playersValid(Player... pX) {
        final List<Player> collect = Arrays.stream(pX).filter(distinctByKey(Player::getId)).collect(Collectors.toList());
        return pX.length == collect.size();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Create a match, if striker team 1 (white) and striker team 2 (black) is not present the match is either of type
     * Matchtype.
     *
     * @param keeperTeam1
     * @param strikerTeam1
     * @param keeperTeam2
     * @param strikerTeam2
     * @param season
     * @return
     */
    public Match createMatch(Player keeperTeam1, Player strikerTeam1, Player keeperTeam2, Player strikerTeam2, Season season) {

        em.getTransaction().begin();

        final Match match = new Match(keeperTeam1, strikerTeam1, keeperTeam2, strikerTeam2, REGULAR, season);

        final List<Player> loksafePlayers = checkIfPlayerIsLoksafe(Arrays.asList(keeperTeam1, strikerTeam1, keeperTeam2, strikerTeam2));
        if (!loksafePlayers.isEmpty()) {
            match.setLoksafePlayer(loksafePlayers.get(0));
        }

        //final Season s = em.find(Season.class, season.getId());

        //Session session = em.unwrap(Session.class);

        //s.getMatches();
        //s.addMatch(match);

        //entityManager.merge(season);
        em.persist(match);

        em.getTransaction().commit();
        //JPAUtil.shutdown();

        return match;
    }

    public Match createMatch(Player player1, Player player2, Matchtype matchtype, Season season) {

        em.getTransaction().begin();

        final Match m = new Match(player1, player2, matchtype, season);

        final List<Player> loksafePlayers = checkIfPlayerIsLoksafe(Arrays.asList(player1, player2));
        if (!loksafePlayers.isEmpty()) {
            m.setLoksafePlayer(loksafePlayers.get(0));
        }

        final Season s = em.find(Season.class, season.getId());

        s.getMatches();
        s.addMatch(m);

        em.persist(m);

        em.getTransaction().commit();

        return m;
    }

    public Match createPreMatch(Player startPlayer, Matchtype matchtype, Season season) {
        em.getTransaction().begin();

        final Match m = new Match(startPlayer, matchtype, season);

        final Season s = em.find(Season.class, season.getId());

        s.getMatches();
        s.addMatch(m);

        em.persist(m);

        em.getTransaction().commit();

        return m;
    }

    /**
     * Updates a match
     *
     * @param matchId    : the id of that match that needs to be updated
     * @param goalsTeam1 : the goals for team 1
     * @param goalsTeam2 : the goals for team 2
     * @return : a match id if a follow up game is created
     */
    public void updateMatch(long matchId, int goalsTeam1, int goalsTeam2) {

        final Match match = em.find(Match.class, matchId);
        match.setGoalsTeam1(goalsTeam1);
        match.setGoalsTeam2(goalsTeam2);

        // in any case update the match
        em.getTransaction().begin();
        em.merge(match);
        em.getTransaction().commit();
    }

    public Optional<Match> finishMatch(long matchId) {

        final Match match = em.find(Match.class, matchId);
        final int goalsTeam1 = match.getGoalsTeam1();
        final int goalsTeam2 = match.getGoalsTeam2();
        final Status previousStatus = match.getStatus();

        Optional<Match> followUpMatch = Optional.empty();

        // check if this match needs to be set to status finished
        switch (match.getMatchtype()) {
            case DEATH_MATCH_BO3:
                if (goalsTeam1 == 2 || goalsTeam2 == 2) match.setStatus(Status.FINISHED);
                break;
            case DEATH_MATCH:
                if (goalsTeam1 == 1 || goalsTeam2 == 1) match.setStatus(Status.FINISHED);
                break;
            case REGULAR:
                if (goalsTeam1 == 5 || goalsTeam2 == 5) {
                    match.setStatus(Status.FINISHED);
                    // -- check if Deathmatch is necessary --
                    if (goalsTeam1 == 0) {
                        followUpMatch = Optional.of(createFollowUpGame(
                                match.getSeason(),
                                match.getKeeperTeam1(),
                                match.getStrikerTeam1()
                        ));
                    } else if (goalsTeam2 == 0) {
                        followUpMatch = Optional.of(createFollowUpGame(
                                match.getSeason(),
                                match.getKeeperTeam2(),
                                match.getStrikerTeam2()
                        ));
                    }
                }
                break;
        }

        // in any case update the match
        em.getTransaction().begin();
        em.merge(match);
        em.getTransaction().commit();

        return followUpMatch;
    }

    private List<Player> checkIfPlayerIsLoksafe(List<Player> playerList) {
        final List<Player> playersLockSafe = playerList.stream()
                .filter(player -> Boolean.TRUE.equals(player.getLokSafe()))
                .collect(Collectors.toList());
        return playersLockSafe;
    }

    private Match createFollowUpGame(Season season, Player player1, Player player2) {
        if (this.teamIsLokSafe(player1, player2)) {
            return createMatch(player1, player2, DEATH_MATCH_BO3, season);
        } else {
            return createMatch(player1, player2, DEATH_MATCH, season);
        }
    }

    public boolean teamIsLokSafe(Player player1, Player player2) {
        return Boolean.TRUE.equals(player1.getLokSafe())
                || Boolean.TRUE.equals(player2.getLokSafe());
    }

}
