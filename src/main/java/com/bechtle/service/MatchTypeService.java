package com.bechtle.service;

import com.bechtle.model.Matchtype;
import com.bechtle.util.JPAUtil;

import javax.persistence.EntityManager;

public class MatchTypeService {

    public void createMatchType(String name, int goalLimit){

        EntityManager entityManager = JPAUtil.getEntityManager();

        Matchtype m = new Matchtype(name, goalLimit);
        entityManager.persist(m);
        entityManager.flush();
        entityManager.getTransaction().commit();
        JPAUtil.shutdown();
    }
}
