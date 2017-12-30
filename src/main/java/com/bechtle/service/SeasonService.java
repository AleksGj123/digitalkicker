package com.bechtle.service;

import com.bechtle.model.Season;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.List;

public class SeasonService {

    private static EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("KickerPersistence");
    private static EntityManager entityManager = emFactory.createEntityManager();

    public static Season createSeason(String name, Date start, Date end) {

        entityManager.getTransaction().begin();

        Season season = new Season(name, start, end);
        entityManager.persist(season);
        entityManager.getTransaction().commit();

        close();

        return season;
    }

    public static Season getSeasons(long seasonId){

        entityManager.getTransaction().begin();
        Season season = entityManager.find(Season.class, seasonId );

        return season;

    }

    public static List<Season> getAllSeasons(){

        entityManager.getTransaction().begin();
        List<Season> allSeasons = entityManager.createQuery("select s from Season as s").getResultList();

        close();
        return allSeasons;
    }

    private static void close(){
        entityManager.close();
        emFactory.close();
    }
}
