package object.dao;

import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 16/04/2015.
 */
public interface IGenericDao<T> {

    /////////////////
    //BASE - SPRING//
    /////////////////
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setNewJdbcTemplate();
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml);

    ///////////////////
    //BASE -HIBERNATE//
    //////////////////
    void setHibernateTemplate(HibernateTemplate hibernateTemplate);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void loadHibernateConfig(String filePathXml);

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

    String selectValueForSpecificColumn(String column, String column_where, String value_where);

    void insertAndTrim(Object[] params,int[] types);
    List<T> selectAll(Class<T> aClass,String column, String limit, String offset);
    List<T> selectAll(String query);
    List<T> select(final String column, String limit, String offset);
    List<T> select(String column, String datatype,String limit, String offset);

    /////////////
    //MANAGER////
    /////////////
    void insert(T object);
    void delete(final Object id);
    T find(final Object id);
    T update(final T t);
    long countAll(Map<String, Object> params);
    /////////////
    //HIBERNATE//
    /////////////

    void saveSimpleH(T object);  //method to save
    void updateSimpleH(T object);//method to update
    void deleteSimpleH(T object);//method to delete
    T getHByColumn(String column);//method to return one of given id

    List<T> selectAllSimpleH();//method to return all
    List<T> selectAllSimpleH(final String limit, final String offset);
    List<Map<String,Object>> getAll();
    Number getAutoGeneratedKey(String idColumn);





}
