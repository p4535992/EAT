package object.dao.generic;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 23/04/2015.
 */
public interface IGenericHibernateDao<T> {

    void setHibernateTemplate(HibernateTemplate hibernateTemplate);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setDataSource(DataSource ds);
    void loadHibernateConfig(String filePathXml);

    DataSource getDataSource();
    void setSessionFactory(DataSource dataSource);
    void setSessionFactory();
    SessionFactory getSessionFactory();
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);

    void saveSimpleH(T object);  //method to save
    void updateSimpleH(T object);//method to update
    void deleteSimpleH(T object);//method to delete
    T getHByColumn(String column);//method to return one of given id

    List<T> selectAllH();
    List<T> selectAllSimpleH();//method to return all
    List<T> selectAllSimpleH(final String limit, final String offset);
    //List<Map<String,Object>> getAll();
    List<T> getAllH();
}
