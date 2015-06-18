package com.github.p4535992.extractor.object.dao.jdbc.generic;

import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 16/04/2015.
 */
public interface IGenericDao<T> {

    /////////////////
    //BASE - SPRING//
    /////////////////
    void setNewJdbcTemplate();
    void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database);
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml) throws IOException;
    void loadSpringConfig(String[] filesPathsXml) throws IOException;

    /////////
    //OTHER//
    /////////
    DataSource getDataSource();
    SessionFactory getSessionFactory();
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    void setTableUpdate(String nameOfTable);



    /////////
    //JDBC///
    /////////
    String[] getColumnsInsertTable();
    void create(String SQL) throws Exception;
    void create(String SQL, boolean erase) throws Exception;
    boolean verifyDuplicate(String columnWhereName, String valueWhereName);
    int getCount();
    void deleteAll();

    List<Object> select(String column, String column_where, Object value_where,Integer limit,Integer offset,String condition);
    Object select(String column, String column_where, Object value_where);

    String prepareUpdateQuery(String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition);

    List<T> trySelect(String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition);
    //List<T> trySelect(String[] columns,Object[] values,Integer limit, Integer offset,String condition);
    List<List<Object[]>> select(String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset, String condition);
    List<Object> select(String column, Integer limit, Integer offset, Class<?> clazz);

    List<T> trySelectWithRowMap(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit, Integer offset,String condition);
    List<T> trySelectWithResultSetExtractor(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition);

    //List<T> trySelect(String column, String datatype,int limit, int offset);
    void insertAndTrim(String[] columns,Object[] params,int[] types);
    void insert(String[] columns,Object[] params,int[] types);
    void tryInsert(T object);
    /////////////
    //MANAGER////
    /////////////
    void insert(T object);
    void delete(final Object id);
    void delete(String whereColumn, String whereValue);
    T find(final Object id);
    T update(final T t);

    void update(String[] columns, Object[] values, String[] columns_where,Object[] values_where);
    void update(String[] columns,Object[] values,String columns_where,String values_where);
    void update(String queryString);
    long countAll(Map<String, Object> params);

    String prepareInsertIntoQuery(String[] columns,Object[] values);
    String prepareSelectQuery(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition);
    void trim(String column);

}
