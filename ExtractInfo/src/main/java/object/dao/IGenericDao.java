package object.dao;

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
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
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

    /////////
    //JDBC///
    /////////

    void create(String SQL) throws Exception;
    void create(String SQL, boolean erase) throws Exception;
    boolean verifyDuplicate(String columnWhereName, String valueWhereName);
    int getCount();
    void deleteAll();
    Object select(String column, String column_where, String value_where,Class<? extends Object> aClass);
    //Object select(String column, String column_where, String value_where);
    //List select(String column, String limit, String offset,Class<T> aClass);
    List select(final String column, int limit,int offset);
    List<T> select(String query);
    List<T> select(int limit, int offset);
    List<T> select(String column, String datatype,int limit, int offset);
    void insertAndTrim(Object[] params,int[] types);
    /////////////
    //MANAGER////
    /////////////
    void insert(T object);
    void delete(final Object id);
    void delete(String whereColumn, String whereValue);
    T find(final Object id);
    T update(final T t);
    long countAll(Map<String, Object> params);






}
