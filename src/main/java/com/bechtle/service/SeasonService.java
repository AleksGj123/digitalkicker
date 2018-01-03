package com.bechtle.service;

import com.bechtle.model.Season;
import com.bechtle.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.List;


public class SeasonService {


    public Long createSeason(Season s) {
        EntityManager entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(s);
        entityManager.getTransaction().commit();

        entityManager.close();
        JPAUtil.shutdown();

        return s.getId();
    }

    public Season getSeason(long seasonId){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        Season season = entityManager.find(Season.class, seasonId );
        entityManager.close();
        JPAUtil.shutdown();
        return season;

    }

    public List<Season> getAllSeasons(){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        List<Season> allSeasons = entityManager.createQuery("select s from Season as s").getResultList();

        entityManager.close();
        JPAUtil.shutdown();
        return allSeasons;
    }
}
