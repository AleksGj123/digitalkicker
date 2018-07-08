package com.bechtle.api.dao;

import com.bechtle.api.dto.ResultWithPagination;
import com.bechtle.api.util.SearchCTX;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericDAO <MODEL>{

    private Class<MODEL> modelClass;

    public GenericDAO(Class<MODEL> mC){
        this.modelClass = mC;
    }

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
     * @param ctx
     * @return Result_MODELS
     * */
    public ResultWithPagination<MODEL> findBy(SearchCTX ctx){
        EntityManager db = factory.createEntityManager();
        CriteriaBuilder criteriaBuilder = db.getCriteriaBuilder();

        db.getTransaction().begin();

        //This query is getting results with the aid of ctx
        TypedQuery<MODEL> typedQuery = getCriteraByCTX(db, criteriaBuilder, ctx);
        final List<MODEL> result =  typedQuery.getResultList();

        //This query is getting the count
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(modelClass)));
        Long count = db.createQuery(countQuery).getSingleResult();

        db.close();

        return new ResultWithPagination<>(
                ctx.getSize(),
                ctx.getPage(),
                count,
                result);
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

    private TypedQuery<MODEL> getCriteraByCTX(EntityManager db, CriteriaBuilder criteriaBuilder, SearchCTX ctx){

        Map<String, String[]> filters = ctx.getFilter();
        Map<String, String[]> sortings = ctx.getSorting();

        //Sort and filter
        CriteriaQuery<MODEL> criteria = criteriaBuilder.createQuery(modelClass);
        Root<MODEL> root = criteria.from(modelClass);
        CriteriaQuery<MODEL> criteriaQuery = criteria.select(root);
        List<Predicate> predicates = new ArrayList<>();
        for(Map.Entry<String, String[]> filter: filters.entrySet()){
            for(String value: filter.getValue()){
                predicates.add(criteriaBuilder.equal(root.get(filter.getKey()), value));
            }
        }
        criteria.select(root).where(predicates.toArray(new Predicate[]{}));

        //ADD PaginationInfos
        TypedQuery<MODEL> typedQuery = db.createQuery(criteriaQuery);
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(ctx.getSize());

        return typedQuery;
    }
}