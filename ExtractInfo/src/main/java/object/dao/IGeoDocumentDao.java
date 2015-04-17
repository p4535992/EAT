package object.dao;

import object.model.GeoDocument;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Marco on 02/04/2015.
 */
public interface IGeoDocumentDao extends IGenericDao<GeoDocument> {
    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    void setNewJdbcTemplate();
    void setHibernateTemplate(HibernateTemplate ht);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml);
    void loadHibernateConfig(String filePathXml);

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
