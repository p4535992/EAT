package object.dao.hibernate;

import object.dao.hibernate.generic.IGenericHibernateDao;
import object.model.GeoDocument;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 02/04/2015.
 */
public interface IGeoDocumentHibernateDao extends IGenericHibernateDao<GeoDocument> {
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    //void setNewJdbcTemplate();
    //void setHibernateTemplate(HibernateTemplate ht);
    //void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);

    void setContext(ApplicationContext context);
    ApplicationContext getContext();
    void setBeanIdSessionFactory(String beanIdSessionFactory);
    String getBeanIdSessionFactory();
    //method to save
    //void saveH(GeoDocument g);

    //method to update
    void update(GeoDocument g);

    //method to delete
    //void deleteH(GeoDocument g);

    //method to return one of given id
    //GeoDocument  getHByColumn(String column);

    //method to return all
    List<GeoDocument> selectAll(Serializable serial);
    GeoDocument select(Serializable serial);
    List<GeoDocument> findByName(String name);
    Iterator<GeoDocument> iterateByWeight(int weight);
    IGeoDocumentHibernateDao getDao(String beanIdName);
}
