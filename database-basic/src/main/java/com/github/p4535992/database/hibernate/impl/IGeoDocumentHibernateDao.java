package com.github.p4535992.database.hibernate.impl;

import com.github.p4535992.database.hibernate.IGenericHibernateDao;
import com.github.p4535992.database.model.GeoDocument;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;



import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;


/**
 * Created by 4535992 on 02/04/2015.
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
    void setHibernateTemplate(org.springframework.orm.hibernate4.HibernateTemplate hibernateTemplate);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void loadSpringContext(String filePathXml);
    DataSource getDataSource();
    void setSessionFactory(DataSource dataSource);
    List<GeoDocument> selectRows();
    GeoDocument selectRow(Serializable serial);

    GeoDocument getDao(String beanIdNAme);
}
