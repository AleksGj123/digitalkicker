package com.bechtle.service;

import com.bechtle.model.Match;
import com.bechtle.model.Matchtype;
import com.bechtle.model.Player;
import com.bechtle.model.Season;

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
                " and m.season = :season" +
                " and (" +
                "m.keeperTeam1 = :playerId" +
                " or m.keeperTeam2 = :playerId" +
                ")")
                .setParameter("season", season)
                .setParameter("matchTypes", new HashSet<>(Arrays.asList(Matchtype.DEATH_MATCH, Matchtype.DEATH_MATCH_BO3)))
                .setParameter("playerId", player)
                .getResultList();
    }

    public List<Match> getLostDeathmachesForPlayer(Player player, Season season) {
        return em.createQuery("select m from Matches as m where m.matchtype in :matchTypes" +
                " and m.season = :season" +
                " and (" +
                "(m.keeperTeam1 = :playerId and m.goalsTeam1 < m.goalsTeam2)" +
                " or (m.keeperTeam2 = :playerId and m.goalsTeam1 > m.goalsTeam2)" +
                ")")
                .setParameter("season", season)
                .setParameter("matchTypes", new HashSet<>(Arrays.asList(Matchtype.DEATH_MATCH, Matchtype.DEATH_MATCH_BO3)))
                .setParameter("playerId", player)
                .getResultList();
    }

    public long getNumberOfPlayedGamesForPlayer(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.season = :season" +
                " and (m.keeperTeam1 = :playerId or m.strikerTeam1 = :playerId or m.keeperTeam2 = :playerId or m.strikerTeam2 = :playerId)")
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .getSingleResult();
    }

    public long getNumberOfWonGamesForPlayer(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.season = :season" +
                " and (" +
                " (m.keeperTeam1 = :playerId or m.strikerTeam1 = :playerId) and m.goalsTeam1 = :goals" +
                " or (m.keeperTeam2 = :playerId or m.strikerTeam2 = :playerId) and m.goalsTeam2 = :goals" +
                ")")
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .setParameter("goals", 5)
                .getSingleResult();
    }

    public long getNumberOfPlayedGamesAsStriker(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.season = :season" +
                " and " +
                " (m.strikerTeam1 = :playerId or m.strikerTeam2 = :playerId)")
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .getSingleResult();
    }

    public long getNumberOfWonGamesAsStriker(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.season = :season" +
                " and " +
                " (m.strikerTeam1 = :playerId and m.goalsTeam1 = :goals" +
                " or m.strikerTeam2 = :playerId and m.goalsTeam2 = :goals)")
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .setParameter("goals", 5)
                .getSingleResult();
    }

    public long getNumberOfPlayedGamesAsKeeper(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.season = :season" +
                " and " +
                " (m.keeperTeam1 = :playerId or m.keeperTeam2 = :playerId)")
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .getSingleResult();
    }

    public long getNumberOfWonGamesAsKeeper(Player player, Season season) {
        return (Long) em.createQuery("select count(m) from Matches as m where m.matchtype = :matchType" +
                " and m.season = :season" +
                " and " +
                " (m.keeperTeam1 = :playerId and m.goalsTeam1 = :goals" +
                " or m.keeperTeam2 = :playerId and m.goalsTeam2 = :goals)")
                .setParameter("season", season)
                .setParameter("matchType", Matchtype.REGULAR)
                .setParameter("playerId", player)
                .setParameter("goals", 5)
                .getSingleResult();
    }

    public List<Match> getAllDeathatches(Season season) {
        final List<Match> allDeathmatches = em.createQuery("select m from Matches as m where m.matchtype in :matchTypes" +
                " and m.season = :season " +
                " order by m.id desc")
                .setParameter("season", season)
                .setParameter("matchTypes", new HashSet<>(Arrays.asList(Matchtype.DEATH_MATCH, Matchtype.DEATH_MATCH_BO3)))
                .getResultList();
        return allDeathmatches;
    }

}
