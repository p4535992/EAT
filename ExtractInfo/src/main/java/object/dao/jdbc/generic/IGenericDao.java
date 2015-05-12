package object.dao.jdbc.generic;

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
    Object select(String column, String column_where, String value_where,Class<? extends Object> aClass);

    //private T supportObject =  ReflectionKit.invokeConstructor(cl);
    List trySelect(String query, T supportObject);

    //Object trySelect(String column, String column_where, String value_where);
    //List trySelect(String column, String limit, String offset,Class<T> aClass);
    List trySelect(final String column, int limit, int offset);
    //List<T> trySelect(String query);
    List<T> select(int limit, int offset);
    List<T> select(String[] columns_where,Object[] values_where,int limit, int offset,String condition);
    List<T> trySelect(String[] columns_where,Object[] values_where,Integer limit, Integer offset,String condition);
    List<T> select(String column, String datatype,int limit, int offset);
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
    void update(String[] columns,Object[] values,String column_where,String value_where);
    long countAll(Map<String, Object> params);

    String prepareSelectQuery(String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition);




}
