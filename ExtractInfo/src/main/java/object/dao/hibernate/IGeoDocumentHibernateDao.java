package object.dao.hibernate;

import object.dao.hibernate.generic.IGenericHibernateDao;
import object.model.GeoDocument;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate4.HibernateTemplate;

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
    void setDataSource(DataSource ds);
    void setContext(ApplicationContext context);
    ApplicationContext getContext();
    void setBeanIdSessionFactory(String beanIdSessionFactory);
    String getBeanIdSessionFactory();

    void setHibernateTemplate(HibernateTemplate hibernateTemplate);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void loadSpringContext(String filePathXml);

    DataSource getDataSource();
    void setSessionFactory(DataSource dataSource);


    //method to return all
    List<GeoDocument> selectRows();
    GeoDocument selectRow(Serializable serial);

    GeoDocument getDao(String beanIdNAme);
}
