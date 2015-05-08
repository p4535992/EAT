package object.dao.hibernate.generic;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 4535992 on 23/04/2015.
 */
public interface IGenericHibernateDao<T> {

    void setHibernateTemplate(HibernateTemplate hibernateTemplate);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setDataSource(DataSource ds);
    void loadSpringContext(String filePathXml) throws FileNotFoundException;

    DataSource getDataSource();
    void setSessionFactory(DataSource dataSource);
    void setSessionFactory(SessionFactory sessionFactory);
    void setSessionFactory();
    SessionFactory getSessionFactory();
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);

    /*void saveSimpleH(T object);  //method to save
    void updateSimpleH(T object);//method to update
    void deleteSimpleH(T object);//method to delete
    T getHByColumn(String column);//method to return one of given id

    List<T> selectAllH();
    List<T> selectAllSimpleH();//method to return all
    List<T> selectAllSimpleH(final String limit, final String offset);
    //List<Map<String,Object>> getAll();
    List<T> getAllH();*/

    Session getSession();
    void shutdown();
    void openSession();
    void closeSession();
    void restartSession();
    Session getCurrentSession();
    void insert(T newInstance);
    Object insertAndReturn(T newInstance);
    //List<T> select(Serializable serial);
    List<T> select(String nameColumn, int limit, int offset);
    List<T> selectAll(Serializable serial);
    T select(Serializable serial);
    int getCount();
    void update(T object);
    //void update(T object, String whereColumn, Object whereValue);
    void delete(T object);
    //void delete(String whereColumn, Object whereValue);

    void updateAnnotationTable(String nameOfAttribute, String newValueAttribute);
    void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
    void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
    <T> T getDao(String beanIdNAme);
}


