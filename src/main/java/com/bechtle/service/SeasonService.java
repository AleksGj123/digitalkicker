package com.bechtle.service;

import com.bechtle.model.Season;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

public class SeasonService {


    public void createSeason(Season s) {

        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("KickerPersistence");
        EntityManager entityManager= emFactory.createEntityManager();

        entityManager.getTransaction().begin();

        entityManager.persist(s);
        entityManager.getTransaction().commit();

        entityManager.close();
        emFactory.close();
    }

    public Season getSeasons(long seasonId){

        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("KickerPersistence");
        EntityManager entityManager= emFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Season season = entityManager.find(Season.class, seasonId );
        entityManager.close();
        emFactory.close();
        return season;

    }

    public List<Season> getAllSeasons(){

        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("KickerPersistence");
        EntityManager entityManager= emFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Season> allSeasons = entityManager.createQuery("select s from Season as s").getResultList();

        entityManager.close();
        emFactory.close();
        return allSeasons;
    }
}
