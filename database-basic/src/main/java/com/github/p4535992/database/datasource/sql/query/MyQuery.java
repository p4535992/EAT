package com.github.p4535992.database.datasource.sql.query;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 02/02/2016.
 * @author 4535992.
 */
public interface MyQuery<T> {

    /////////
    //JDBC///
    /////////
    String[] getColumnsInsertTable(String nameOfTable);

    void create(String SQL) throws Exception;
    void create(String SQL, boolean erase) ;

    void deleteAll();
    void deleteDuplicateRecords(String[] columns, String nameKeyColumn);

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
    //void tryInsert(Object object);

    void update(String[] columns, Object[] values, String[] columns_where,Object[] values_where);
    void update(String[] columns,Object[] values,String columns_where,String values_where);
    void update(String queryString);

    /////////////
    //MANAGER////
    /////////////
    void insert(T object);
    void delete(final Object id);

    //@Transactional
    //Object update(Object t);

    void delete(String whereColumn, String whereValue);
    T find(final Object id);
    T update(final T t);

    /////////////
    //OTHER////
    /////////////
    void trim(String column);
    long getCount();
    boolean verifyDuplicate(String columnWhereName, String valueWhereName) throws MySQLSyntaxErrorException;
    int getCount(String nameOfTable);

}
