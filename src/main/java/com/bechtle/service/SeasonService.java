//package com.bechtle.service;
//
//import com.bechtle.model.Season;
//
//import javax.persistence.EntityManager;
//import java.util.List;
//
//
//public class SeasonService extends Service {
//
//    public SeasonService(EntityManager em) {
//        super(em);
//    }
//
//    public Long createSeason(Season s) {
//        em.getTransaction().begin();
//        em.persist(s);
//        em.getTransaction().commit();
//        return s.getId();
//    }
//
//    public Season getSeason(long seasonId){
//        final Season season = em.find(Season.class, seasonId );
//        return season;
//
//    }
//
//    public List<Season> getAllSeasons(){
//        final List<Season> allSeasons = em.createQuery("select s from Season as s").getResultList();
//        return allSeasons;
//    }
//}
