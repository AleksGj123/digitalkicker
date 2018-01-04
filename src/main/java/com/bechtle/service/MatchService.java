package com.bechtle.service;

import com.bechtle.model.*;
import com.bechtle.util.JPAUtil;
import javax.persistence.EntityManager;
import java.util.List;

public class MatchService {

    public List<Match> getAllMatches() {
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        List<Match> allMatches = entityManager.createQuery("select m from Matches as m").getResultList();

        entityManager.close();
        JPAUtil.shutdown();
        return allMatches;
    }

    public Match getMatch(Long id) {
        EntityManager entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();

        Match match = entityManager.find(Match.class, id);

        JPAUtil.shutdown();
        return match;
    }


    public long createMatch(Player keeperTeam1, Player strikerTeam1, Player keeperTeam2, Player strikerTeam2, Season season) {

        EntityManager entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();


        Match match = new Match(keeperTeam1, strikerTeam1, keeperTeam2, strikerTeam2, Matchtype.REGULAR, season);

        entityManager.persist(match);
        /*if (keeperTeam1 != null) {
            keeperTeam1.addMatch(match);
            entityManager.merge(keeperTeam1);
        }
        if (keeperTeam2 != null) {
            keeperTeam2.addMatch(match);
            entityManager.merge(keeperTeam2);
        }
        if (strikerTeam1 != null) {
            strikerTeam1.addMatch(match);
            entityManager.merge(strikerTeam1);
        }
        if (strikerTeam2 != null) {
            strikerTeam2.addMatch(match);
            entityManager.merge(strikerTeam2);
        }*/
        entityManager.getTransaction().commit();
        JPAUtil.shutdown();

        return match.getId();
    }

    public long createMatch(Player player1, Player player2, Matchtype matchtype, Season season) {

        EntityManager entityManager = JPAUtil.getEntityManager();

        Match m = new Match(player1, player2, matchtype, season);
        entityManager.persist(m);
        entityManager.getTransaction().commit();
        JPAUtil.shutdown();

        return m.getId();
    }

    public void updateMatch(Match match) {
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.merge(match);

        entityManager.getTransaction().commit();
        JPAUtil.shutdown();
    }

    public boolean teamOneIsLokSafe(Match match){
        return  (Boolean.TRUE.equals(match.getKeeperTeam1().getLokSafe()) || Boolean.TRUE.equals(match.getStrikerTeam1().getLokSafe()));
    }

    public boolean teamTwoIsLokSafe(Match match){
        return  (Boolean.TRUE.equals(match.getKeeperTeam2().getLokSafe()) || Boolean.TRUE.equals(match.getStrikerTeam2().getLokSafe()));
    }


}
