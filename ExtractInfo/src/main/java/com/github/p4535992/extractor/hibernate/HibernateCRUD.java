package com.github.p4535992.extractor.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by monica on 15/05/2015.
 * @author 4535992.
 * @version 2015-07-01.
 */
@SuppressWarnings("unused")
public class HibernateCRUD<T> extends Hibernate4Kit<T> {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( HibernateCRUD.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    @javax.transaction.Transactional
    @java.lang.Override
    public <T> Serializable insertRow(T object) {
        Serializable id = null;
        try {
            openSession();
            trns = session.beginTransaction();
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            logger.info("[HIBERNATE] Insert the item:" + object);
            id = session.getIdentifier(object);
            logger.info("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }

    @java.lang.Override
    @SuppressWarnings("unchecked")
    public  T selectRow(Serializable id){
        T object = null;
        try {
            openSession();
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            //WORK
            try {
                criteria.add(org.hibernate.criterion.Restrictions.eq("doc_id", id));
                List<T> results = criteria.list();
                logger.info("[HIBERNATE] Select the item:" + results.get(0));
            }catch(Exception e) {
                logger.error(gm()+e.getMessage(),e);
            }
            //NOT WORK
            //object = (T) criteria.setFirstResult((Integer) id);
            //SystemLog.message("[HIBERNATE] Select the item:" + object.toString());
            object = (T) session.load(cl, id);
            logger.info("[HIBERNATE] Select the item:" + object.toString());
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return object;
    }


    @SuppressWarnings("unchecked")
    @javax.transaction.Transactional
    @java.lang.Override
    public List<T> selectRows() {
        List<T> listT = new ArrayList<>();
        try {
            openSession();
            trns = session.beginTransaction();
            if(specificCriteria==null){
                criteria = session.createCriteria(cl);
            }else{
                criteria =specificCriteria;
            }
            listT =  criteria.list();
            if(listT.size() == 0){
                logger.warn("[HIBERNATE] The returned list is empty!1");
            }
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return listT;
    }

    @SuppressWarnings("unchecked")
    @javax.transaction.Transactional
    @java.lang.Override
    public List<T> selectRows(String nameColumn,int limit,int offset) {
        List<T> listT = new ArrayList<>();
        try {
            openSession();
            sql = "SELECT "+nameColumn+" FROM "+mySelectTable+"";
            SQLQuery = session.createSQLQuery(sql);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            listT = query.list();
            if(listT.size() == 0){
                logger.warn("[HIBERNATE] The returned list is empty!1");
            }
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return listT;
    }


    @SuppressWarnings("unchecked")
    @javax.transaction.Transactional
    @java.lang.Override
    public int getCount() {
        Object result = null;
        try {
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.setProjection(org.hibernate.criterion.Projections.rowCount());
            result = criteria.uniqueResult();
            logger.info("[HIBERNATE] The count of employees is :" + result);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return (int)result;
    }


    @SuppressWarnings("unchecked")
    @javax.transaction.Transactional
    @java.lang.Override
    public Serializable updateRow(String whereColumn, Object whereValue) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            //t.setName("Abigale");
            //t = object;
            session.saveOrUpdate(t);
            session.getTransaction().commit();
            logger.info("[HIBERNATE] Update the item:" + t.toString());
            id = session.getIdentifier(t);
            logger.info("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }


    @SuppressWarnings("unchecked")
    @javax.transaction.Transactional
    @java.lang.Override
    public Serializable updateRow(T object) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            session.saveOrUpdate(object);
            session.getTransaction().commit();
            logger.info("[HIBERNATE] Update the item:" + object.toString());
            id = session.getIdentifier(object);
            logger.info("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    @javax.transaction.Transactional
    @java.lang.Override
    public Serializable deleteRow(String whereColumn, Object whereValue) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            session.delete(t);
            session.getTransaction().commit();
            logger.info("[HIBERNATE] Delete the item:" + t);
            id = session.getIdentifier(t);
            logger.info("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm() + e.getMessage(), e);
        } finally {
            reset();
        }
        return id;
    }


    @SuppressWarnings("unchecked")
    @javax.transaction.Transactional
    @java.lang.Override
    public Serializable deleteRow(T object) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            session.delete(object);
            session.getTransaction().commit();
            logger.info("[HIBERNATE] Delete the item:" + object);
            id = session.getIdentifier(object);
            logger.info("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            logger.error(gm()+e.getMessage(),e);
        } finally {
            reset();
        }
        return id;
    }
}
