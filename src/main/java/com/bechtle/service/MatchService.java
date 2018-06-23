//package com.bechtle.service;
//
//import com.bechtle.model.*;
//import org.hibernate.Session;
//
//import javax.persistence.EntityManager;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//import static com.bechtle.model.Matchtype.DEATH_MATCH;
//import static com.bechtle.model.Matchtype.DEATH_MATCH_BO3;
//import static com.bechtle.model.Matchtype.REGULAR;
//
//public class MatchService extends Service {
//
//    public MatchService(EntityManager em) {
//        super(em);
//    }
//
//    public List<Match> getAllMatches() {
//        final List<Match> allMatches = em.createQuery("select m from Matches as m").getResultList();
//        return allMatches;
//    }
//
//    public Match getCurrentMatch(){
//        final List<Match> activeMatches = em.createQuery("select m from Matches as m where m.status = '0'").getResultList();
//
//        if (activeMatches.isEmpty()){
//            //SELECT * FROM Matches ORDER BY timestamp DESC LIMIT 1;
//            List<Match>  resultList = em.createQuery("SELECT m FROM Matches AS m ORDER BY m.timestamp DESC")
//                    .setMaxResults(1).getResultList();
//            return resultList.get(0);
//        }
//        return activeMatches.get(0);
//    }
//
//    /**
//     * Delete a match by id
//     *
//     * @param matchId the Id of the match you want to delete
//     * @return false if match could not be delete because it was not found
//     */
//    public boolean deleteMatch(Long matchId){
//
//        em.getTransaction().begin();
//
//        final Match match = em.find(Match.class, matchId);
//        final Season season = match.getSeason();
//        final Player keeperTeam1 = match.getKeeperTeam1();
//        final Player keeperTeam2 = match.getKeeperTeam2();
//        final Player strikerTeam1 = match.getStrikerTeam1();
//        final Player strikerTeam2 = match.getStrikerTeam2();
//
//        keeperTeam1.getMatches().removeIf(m -> m.getId() == matchId);
//        keeperTeam2.getMatches().removeIf(m -> m.getId() == matchId);
//
//        season.getMatches().removeIf(m -> m.getId() == matchId);
//
//        em.merge(season);
//        em.merge(keeperTeam1);
//        em.merge(keeperTeam2);
//
//        // only relevant for death match and death match bo3
//        if(strikerTeam1 !=  null){
//            strikerTeam1.getMatches().removeIf(m -> m.getId() == matchId);
//            em.merge(strikerTeam1);
//        }
//        if(strikerTeam2 != null){
//            strikerTeam2.getMatches().removeIf(m -> m.getId() == matchId);
//            em.merge(strikerTeam2);
//        }
//
//        em.remove(match);
//
//        em.getTransaction().commit();
//
//        return true;
//    }
//
//    public Match getMatch(Long id) {
//        final Match match = em.find(Match.class, id);
//        return match;
//    }
//
//    public boolean playersValid(Player... pX ){
//        final List<Player> collect = Arrays.stream(pX).filter(distinctByKey(Player::getId)).collect(Collectors.toList());
//        return pX.length == collect.size();
//    }
//
//    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
//        final Set<Object> seen = ConcurrentHashMap.newKeySet();
//        return t -> seen.add(keyExtractor.apply(t));
//    }
//
//    /**
//     * Create a match, if striker team 1 (white) and striker team 2 (black) is not present the match is either of type
//     * Matchtype.
//     * @param keeperTeam1
//     * @param strikerTeam1
//     * @param keeperTeam2
//     * @param strikerTeam2
//     * @param season
//     * @return
//     */
//    public long createMatch(Player keeperTeam1, Player strikerTeam1, Player keeperTeam2, Player strikerTeam2, Season season) {
//
//        em.getTransaction().begin();
//
//        final Match match = new Match();
//
//        final List<Player> loksafePlayers = checkIfPlayerIsLoksafe(Arrays.asList(keeperTeam1, strikerTeam1, keeperTeam2, strikerTeam2));
//        if(!loksafePlayers.isEmpty())
//        {
//            match.setLoksafePlayer(loksafePlayers.get(0));
//        }
//
//        //final Season s = em.find(Season.class, season.getId());
//
//        //Session session = em.unwrap(Session.class);
//
//        //s.getMatches();
//        //s.addMatch(match);
//
//        //entityManager.merge(season);
//        em.persist(match);
//
//        keeperTeam1.addMatch(match);
//        //session.saveOrUpdate(keeperTeam1);
//        //entityManager.merge(keeperTeam1);
//
//        keeperTeam2.addMatch(match);
//        //entityManager.merge(keeperTeam2);
//
//        strikerTeam1.addMatch(match);
//        //entityManager.merge(strikerTeam1);
//
//        strikerTeam2.addMatch(match);
//        //entityManager.merge(strikerTeam2);
//
//        em.getTransaction().commit();
//        //JPAUtil.shutdown();
//
//        return match.getId();
//    }
//
//    public long createMatch(Player player1, Player player2, Matchtype matchtype, Season season) {
//
//        em.getTransaction().begin();
//
//        final Match m = new Match();
//
//        final List<Player> loksafePlayers = checkIfPlayerIsLoksafe(Arrays.asList(player1, player2));
//        if(!loksafePlayers.isEmpty())
//        {
//            m.setLoksafePlayer(loksafePlayers.get(0));
//        }
//
//        final Season s = em.find(Season.class, season.getId());
//
//        s.getMatches();
//        s.addMatch(m);
//
//        em.persist(m);
//
//        player1.addMatch(m);
//        em.merge(player1);
//
//        player2.addMatch(m);
//        em.merge(player2);
//
//        em.getTransaction().commit();
//
//        return m.getId();
//    }
//
//    /**
//     * Updates a match
//     *
//     * @param matchId : the id of that match that needs to be updated
//     * @param goalsTeam1 : the goals for team 1
//     * @param goalsTeam2 : the goals for team 2
//     * @return : a match id if a follow up game is created
//     */
//    public void updateMatch(long matchId, int goalsTeam1, int goalsTeam2) {
//
//        final Match match = em.find(Match.class, matchId);
//        match.setGoalsTeam1(goalsTeam1);
//        match.setGoalsTeam2(goalsTeam2);
//
//        // in any case update the match
//        em.getTransaction().begin();
//        em.merge(match);
//        em.getTransaction().commit();
//    }
//
//    public Long finishMatch(long matchId){
//
//        final Match match = em.find(Match.class, matchId);
//        final int goalsTeam1 = match.getGoalsTeam1();
//        final int goalsTeam2 = match.getGoalsTeam2();
//
//        Long followUpMatch = null;
//
//
//        // in any case update the match
//        em.getTransaction().begin();
//        em.merge(match);
//        em.getTransaction().commit();
//
//        return followUpMatch;
//    }
//
//    private List<Player> checkIfPlayerIsLoksafe(List<Player> playerList){
//        final List<Player> playersLockSafe = playerList.stream()
//                .filter(player -> player.getLokSafe() == true)
//                .collect(Collectors.toList());
//        return playersLockSafe;
//    }
//
//    private long createFollowUpGame(Match match, Player player1, Player player2){
//        if(this.teamIsLokSafe(player1, player2)){
//            return createMatch(player1, player2, DEATH_MATCH_BO3, match.getSeason());
//        }
//        else{
//            return createMatch(player1, player2, DEATH_MATCH, match.getSeason());
//        }
//    }
//
//    public boolean teamIsLokSafe(Player player1, Player player2){
//        return player1.getLokSafe() || player2.getLokSafe();
//    }
//
//}
