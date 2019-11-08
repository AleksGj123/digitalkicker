package com.bechtle.service;

import com.bechtle.model.*;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class StatisticsService extends Service {

    public StatisticsService(EntityManager em) {
        super(em);
    }

    public List<Match> getDeathmachesForPlayer(Player player, Season season) {
        return em.createQuery("select m from Matches as m where m.matchtype in :matchTypes" +
                " and m.status = :status " +
                " and m.season = :season" +
                " and (" +
                "m.keeperTeam1 = :playerId" +
                " or m.keeperTeam2 = :playerId" +
                ")")
                .setParameter("status", Status.FINISHED)
                .setParameter("season", season)
                .setParameter("matchTypes", new HashSet<>(Arrays.asList(Matchtype.DEATH_MATCH, Matchtype.DEATH_MATCH_BO3)))
                .setParameter("playerId", player)
                .getResultList();
    }

    public List<Match> getLostDeathmachesForPlayer(Player player, Season season) {
        return em.createQuery("select m from Matches as m where m.matchtype in :matchTypes" +
                " and m.status = :status " +
                " and m.season = :season" +
                " and (" +
                "(m.keeperTeam1 = :playerId and m.goalsTeam1 < m.goalsTeam2)" +
                " or (m.keeperTeam2 = :playerId and m.goalsTeam1 > m.goalsTeam2)" +
                ")")
                .setParameter("status", Status.FINISHED)
                .setParameter("season", season)
                .setParameter("matchTypes", new HashSet<>(Arrays.asList(Matchtype.DEATH_MATCH, Matchtype.DEATH_MATCH_BO3)))
                .setParameter("playerId", player)
                .getResultList();
    }

    public long getNumberOfPlayedGamesForPlayer(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.status = :status " +
                " and m.season = :season" +
                " and (m.keeperTeam1 = :playerId or m.strikerTeam1 = :playerId or m.keeperTeam2 = :playerId or m.strikerTeam2 = :playerId)")
                .setParameter("status", Status.FINISHED)
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .getSingleResult();
    }

    public long getNumberOfWonGamesForPlayer(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.status = :status " +
                " and m.season = :season" +
                " and (" +
                " (m.keeperTeam1 = :playerId or m.strikerTeam1 = :playerId) and m.goalsTeam1 = :goals" +
                " or (m.keeperTeam2 = :playerId or m.strikerTeam2 = :playerId) and m.goalsTeam2 = :goals" +
                ")")
                .setParameter("status", Status.FINISHED)
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .setParameter("goals", 5)
                .getSingleResult();
    }

    public long getNumberOfPlayedGamesAsKeeper(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.status = :status " +
                " and m.season = :season" +
                " and " +
                " (m.keeperTeam1 = :playerId or m.keeperTeam2 = :playerId)")
                .setParameter("status", Status.FINISHED)
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .getSingleResult();
    }

    public List<Match> getAllMatches(Player player, Season season) {
        final List<Match> allMatches = em.createQuery("select m from Matches as m where m.matchtype in :matchTypes" +
                " and m.status = :status " +
                " and m.season = :season " +
                " and (m.keeperTeam1 = :playerId or m.strikerTeam1 = :playerId or m.keeperTeam2 = :playerId or m.strikerTeam2 = :playerId)" +
                " order by m.id desc")
                .setParameter("status", Status.FINISHED)
                .setParameter("season", season)
                .setParameter("matchTypes", new HashSet<>(Arrays.asList(Matchtype.REGULAR)))
                .setParameter("playerId", player)
                .getResultList();
        return allMatches;
    }


    public List<Match> getAllDeathatches(Season season) {
        final List<Match> allDeathmatches = em.createQuery("select m from Matches as m where m.matchtype in :matchTypes" +
                " and m.status = :status " +
                " and m.season = :season " +
                " order by m.id desc")
                .setParameter("status", Status.FINISHED)
                .setParameter("season", season)
                .setParameter("matchTypes", new HashSet<>(Arrays.asList(Matchtype.DEATH_MATCH, Matchtype.DEATH_MATCH_BO3)))
                .getResultList();
        return allDeathmatches;
    }

}
