package object.dao;

import object.impl.GeoDocumentDaoImpl;
import object.model.Document;
import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 02/04/2015.
 */
public interface GeoDocumentDao {
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setTable(String nameOfTable);
    void setJdbcTemplate(JdbcTemplate jdbcTemplate);
    void setHibernateTemplate(HibernateTemplate ht);
    void setHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);
    GeoDocumentDaoImpl loadSpringConfig(String filePathXml);
    GeoDocumentDaoImpl loadHibernateConfig(String filePathXml);

    void create() throws Exception;
    void create(boolean erase) throws Exception;
    boolean verifyDuplicate(String columnWhereName,String valueWhereName);

    void insertAndTrim(GeoDocument obj);

    //method to save
    void saveH(GeoDocument g);

    //method to update
    void updateH(GeoDocument g);

    //method to delete
    void deleteH(GeoDocument g);

    //method to return one of given id
    GeoDocument  getHByColumn(String column);

    //method to return all
    List<GeoDocument> getAllH();
}
