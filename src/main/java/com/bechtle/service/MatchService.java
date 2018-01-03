package com.bechtle.service;

import com.bechtle.model.*;
import com.bechtle.util.Constants;
import com.bechtle.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class MatchService {

    public List<Match> getAllMatches(){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        List<Match> allMatches = entityManager.createQuery("select m from Matches as m").getResultList();

        entityManager.close();
        JPAUtil.shutdown();
        return allMatches;
    }


    public long createMatch(Player keeperTeam1, Player strikerTeam1, Player keeperTeam2, Player strikerTeam2,
                            Matchtype matchtype, Season season){

        EntityManager entityManager = JPAUtil.getEntityManager();

        Match m = new Match(keeperTeam1,strikerTeam1, keeperTeam2, strikerTeam2, matchtype, season);
        entityManager.persist(m);
        entityManager.getTransaction().commit();
        JPAUtil.shutdown();

        return m.getId();
    }

    public void finishMatch(String matchId){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        Match match = entityManager.find(Match.class, matchId);

        match.setStatus(Status.FINISHED);


        entityManager.merge(match);

        Player keeperTeam1 = match.getKeeperTeam1();
        Player keeperTeam2 = match.getKeeperTeam2();
        Player strikerTeam1 = match.getStrikerTeam1();
        Player strikerTeam2 = match.getStrikerTeam2();

        if(keeperTeam1 != null){
            keeperTeam1.addMatch(match);
            entityManager.merge(keeperTeam1);
        }
        if(keeperTeam2 != null){
            keeperTeam2.addMatch(match);
            entityManager.merge(keeperTeam2);
        }
        if(strikerTeam1 != null){
            strikerTeam1.addMatch(match);
            entityManager.merge(strikerTeam1);
        }
        if(strikerTeam2 != null){
            strikerTeam2.addMatch(match);
            entityManager.merge(strikerTeam2);
        }

        entityManager.getTransaction().commit( );
        JPAUtil.shutdown();
    }

}
