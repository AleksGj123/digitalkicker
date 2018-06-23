package com.bechtle.api.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class Service <MODEL> {

    private final String PERSISTENCE_UNIT_NAME = "KickerPersistence";
    private final EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


    /** findBy SQL Query
     * @param sqlQuery
     * @return Result_MODELS
     * */
    public List<MODEL> findBy(String sqlQuery){
        EntityManager db = factory.createEntityManager();
        db.getTransaction().begin();
        final List<MODEL> result =  db.createQuery(sqlQuery).getResultList();
        db.close();
        return result;
    }

    /** saveOrUpdate MODEL with ID
     * @param id
     * @param model
     * @return Saved_MODEL
     * */
    public MODEL saveOrUpdate(Long id, MODEL model) {
        if(id == 0){

        } else {

        }
        EntityManager db = factory.createEntityManager();
        db.getTransaction().begin();
        final MODEL result = db.merge(model);
        db.getTransaction().commit();
        db.close();
        return result;
    }

    /** delete MODEL with ID
     * @param model
     * @return Boolean
     * */
    public Boolean delete(MODEL model){
        EntityManager db = factory.createEntityManager();
        db.getTransaction().begin();
        db.remove(model);
        db.getTransaction().commit();
        db.close();
        return true;
    }

}