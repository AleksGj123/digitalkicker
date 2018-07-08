package com.bechtle.api.dao;

import com.bechtle.model.Match;
import com.bechtle.model.Player;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;


//TODO Class als generischer Typ + besseres Errorhandling
//TODO Nochmal prüfen wie ein DAO korrekt aufgebaut werden sollte
public class GenericDAO <MODEL>{

    private final String PERSISTENCE_UNIT_NAME = "KickerPersistence";
    private final EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


    /** findById
     * @return Result_MODEL
     * */
    public MODEL findById(Long id, Class<MODEL> modelClass){
        EntityManager db = factory.createEntityManager();
        db.getTransaction().begin();
        final MODEL result = db.find(modelClass, id);
        db.close();
        return result;
    }

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
        EntityManager db = factory.createEntityManager();
        db.getTransaction().begin();
        MODEL result = model;
        if(id != 0L){
            result = db.merge(model);
        } else {
            db.persist(model);
        }
        db.getTransaction().commit();
        return result;
    }

    /** delete MODEL with ID
     * @param id
     * @param modelClass
     * @return Boolean
     * */
    public Boolean delete(Long id, Class<MODEL> modelClass){
        MODEL model2Remove = this.findById(id, modelClass);
        EntityManager db = factory.createEntityManager();
        db.getTransaction().begin();
        db.remove(model2Remove);
        db.getTransaction().commit();
        db.close();
        return true;
    }

    public Boolean updateManyToMany(Match match){
        EntityManager db = factory.createEntityManager();
        db.getTransaction().begin();

        db.close();
        return true;
    }
}