package com.github.p4535992.extractor.object.dao.jdbc.generic;

import javax.sql.DataSource;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 16/04/2015.
 * @author  4535992.
 * @version 2015-07-01.
 */
@SuppressWarnings("unused")
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
    void setTableDelete(String nameOfTable);
    String getMyInsertTable();
    String getMySelectTable();
    String getMyUpdateTable();
    String getMyDeleteTable();

    /////////
    //JDBC///
    /////////
    String[] getColumnsInsertTable(String nameOfTable);

    void create(String SQL) throws Exception;
    void create(String SQL, boolean erase) ;

    void deleteAll();
    void deleteDuplicateRecords(String[] columns, String nameKeyColumn);
    void deleteDuplicateRecords(String[] columns);
    void deleteDuplicateRecords(String[] columns, Object[] values, boolean high);

    Object select(String column, String column_where, Object value_where);
    List<Object> select(
            String column, String column_where, Object value_where,List<org.jooq.Condition> condition);
    List<Object> select(
            String column, String column_where, Object value_where,Integer limit,Integer offset,List<org.jooq.Condition> condition);
    List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where,List<org.jooq.Condition> conditions);
    List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset,List<org.jooq.Condition> condition);
    List<Object> select(
            String column, Integer limit, Integer offset, Class<?> clazz);
    List<T> trySelectWithRowMap(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit, Integer offset,List<org.jooq.Condition> condition);
    List<T> trySelectWithResultSetExtractor(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,List<org.jooq.Condition> condition);
    List<T> trySelect(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,List<org.jooq.Condition> condition);

    void insertAndTrim(String[] columns,Object[] params,int[] types);
    void insertAndTrim(String[] columns, Object[] values, Integer[] types);

    void insert(String[] columns,Object[] params,int[] types);
    void insert(String[] columns, Object[] values, Integer[] types);
    void tryInsert(T object);

    void update(String[] columns, Object[] values, String[] columns_where,Object[] values_where);
    void update(String[] columns,Object[] values,String columns_where,String values_where);
    void update(String queryString);

    /////////////
    //MANAGER////
    /////////////
    void insert(T object);
    void delete(final Object id);
    void delete(String whereColumn, String whereValue);
    T find(final Object id);
    T update(final T t);

    /////////////
    //OTHER////
    /////////////
    void trim(String column);
    long countAll(Map<String, Object> params);
    boolean verifyDuplicate(String columnWhereName, String valueWhereName) throws MySQLSyntaxErrorException;
    int getCount(String nameOfTable);

    ////////////////////////////
    //PREPARED STRING QUERY ////
    ////////////////////////////

    /*String prepareInsertIntoQuery(String[] columns,Object[] values);

    String prepareInsertIntoQuery(String[] columns, Object[] values, Integer[] types);

    String prepareInsertIntoQuery(String[] columns, Object[] values, int[] types);

    String prepareSelectQuery(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition);

    String prepareUpdateQuery(
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition);

    String prepareDeleteQuery(
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition);*/

}
