package com.bechtle.service;

import com.bechtle.model.*;
import com.bechtle.util.JPAUtil;
import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.bechtle.model.Matchtype.DEATH_MATCH;
import static com.bechtle.model.Matchtype.DEATH_MATCH_BO3;
import static com.bechtle.model.Matchtype.REGULAR;

public class MatchService {

    public List<Match> getAllMatches() {
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        List<Match> allMatches = entityManager.createQuery("select m from Matches as m").getResultList();

        entityManager.close();
        JPAUtil.shutdown();
        return allMatches;
    }

    public Match getCurrentMatch(){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        List<Match> allMatches = entityManager.createQuery("select m from Matches as m where m.status = '0'").getResultList();

        entityManager.close();
        JPAUtil.shutdown();
        return allMatches.get(0);
    }

    public Match getMatch(Long id) {
        EntityManager entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();

        Match match = entityManager.find(Match.class, id);

        JPAUtil.shutdown();
        return match;
    }

    public boolean playersValid(Player... pX ){

        List<Player> collect = Arrays.stream(pX).filter(distinctByKey(Player::getId)).collect(Collectors.toList());

        if(pX.length == collect.size())return true;
        return false;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Create a match, if striker team 1 (white) and striker team 2 (black) is not present the match is either of type
     * Matchtype.
     * @param keeperTeam1
     * @param strikerTeam1
     * @param keeperTeam2
     * @param strikerTeam2
     * @param season
     * @return
     */
    public long createMatch(Player keeperTeam1, Player strikerTeam1, Player keeperTeam2, Player strikerTeam2, Season season) {

        EntityManager entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();


        Match match = new Match(keeperTeam1, strikerTeam1, keeperTeam2, strikerTeam2, REGULAR, season);

        Season s = entityManager.find(Season.class, season.getId());

        s.getMatches();
        s.addMatch(match);

        entityManager.merge(season);
        entityManager.persist(match);

        keeperTeam1.addMatch(match);
        entityManager.merge(keeperTeam1);

        keeperTeam2.addMatch(match);
        entityManager.merge(keeperTeam2);

        strikerTeam1.addMatch(match);
        entityManager.merge(strikerTeam1);

        strikerTeam2.addMatch(match);
        entityManager.merge(strikerTeam2);

        entityManager.getTransaction().commit();
        JPAUtil.shutdown();

        return match.getId();
    }

    public long createMatch(Player player1, Player player2, Matchtype matchtype, Season season) {

        EntityManager entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();

        Match m = new Match(player1, player2, matchtype, season);

        Season s = entityManager.find(Season.class, season.getId());

        s.getMatches();
        s.addMatch(m);

        entityManager.persist(m);

        player1.addMatch(m);
        entityManager.merge(player1);

        player2.addMatch(m);
        entityManager.merge(player2);

        entityManager.getTransaction().commit();
        JPAUtil.shutdown();

        return m.getId();
    }

    /**
     * Updates a match
     *
     * @param matchId : the id of that match that needs to be updated
     * @param goalsTeam1 : the goals for team 1
     * @param goalsTeam2 : the goals for team 2
     * @return : a match id if a follow up game is created
     */
    public Long updateMatch(long matchId, int goalsTeam1, int goalsTeam2) {
        EntityManager entityManager = JPAUtil.getEntityManager();

        Match match = entityManager.find(Match.class, matchId);
        match.setGoalsTeam1(goalsTeam1);
        match.setGoalsTeam2(goalsTeam2);

        Long followUpMatch = null;

        // check if this match needs to be set to status finished
        switch (match.getMatchtype())
        {
            case DEATH_MATCH_BO3:
                if(goalsTeam1 == 2 || goalsTeam2 == 2) match.setStatus(Status.FINISHED);
                break;
            case DEATH_MATCH:
                if(goalsTeam1 == 1 || goalsTeam2 == 1) match.setStatus(Status.FINISHED);
                break;
            case REGULAR :
                if(goalsTeam1 == 5 || goalsTeam2 == 5){
                    match.setStatus(Status.FINISHED);
                    // -- check if Deathmatch is necessary --
                    if (goalsTeam1 == 0){
                        followUpMatch = createFollowUpGame(match, match.getKeeperTeam1(), match.getStrikerTeam1());
                    }
                    else if (goalsTeam2 == 0){
                        followUpMatch = createFollowUpGame(match, match.getKeeperTeam2(), match.getStrikerTeam2());
                    }
                }
                break;
        }

        if(!entityManager.isOpen()){
            entityManager = JPAUtil.getEntityManager();
        }

        // in any case update the match
        entityManager.getTransaction().begin();
        entityManager.merge(match);

        entityManager.getTransaction().commit();
        JPAUtil.shutdown();

        return  followUpMatch;
    }

    private long createFollowUpGame(Match match, Player player1, Player player2){

        if(this.teamIsLokSafe(player1, player2)){
            return createMatch(player1, player2, DEATH_MATCH_BO3, match.getSeason());
        }
        else{
            return createMatch(player1, player2, DEATH_MATCH, match.getSeason());
        }
    }

    public boolean teamIsLokSafe(Player player1, Player player2){
        return  (Boolean.TRUE.equals(player1.getLokSafe()) || Boolean.TRUE.equals(player2.getLokSafe()));
    }


}
