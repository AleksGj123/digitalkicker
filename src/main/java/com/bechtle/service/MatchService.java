package com.bechtle.service;

import com.bechtle.model.Match;
import com.bechtle.model.Matchtype;
import com.bechtle.model.Player;
import com.bechtle.model.Season;
import com.bechtle.util.Constants;
import com.bechtle.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;

public class MatchService {


    public long createMatch(List<Player> players, Matchtype matchtype, Season season){

        EntityManager entityManager = JPAUtil.getEntityManager();

        Match m = new Match(players.get(0),players.get(1), players.get(2), players.get(3), matchtype, season);
        entityManager.persist(m);
        entityManager.getTransaction().commit();
        JPAUtil.shutdown();

        return m.getId();
    }

    public void finishMatch(String matchId){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        Match match = entityManager.find(Match.class, matchId);
        match.setStatus(Constants.MATCH_STATUS_FINISHED);
        entityManager.getTransaction( ).commit( );
        JPAUtil.shutdown();
    }

}
