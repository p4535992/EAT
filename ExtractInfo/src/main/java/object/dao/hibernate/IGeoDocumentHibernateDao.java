package object.dao.hibernate;

import object.dao.generic.IGenericDao;
import object.dao.generic.IGenericHibernateDao;
import object.model.GeoDocument;

import javax.sql.DataSource;
import java.io.IOException;
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


    //method to save
    void saveH(GeoDocument g);

    //method to update
    //void updateH(GeoDocument g);

    //method to delete
    //void deleteH(GeoDocument g);

    //method to return one of given id
    //GeoDocument  getHByColumn(String column);

    //method to return all
    List<GeoDocument> getAllH();
}
