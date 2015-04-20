package object.dao;

import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Marco on 02/04/2015.
 */
public interface IGeoDomainDocumentDao extends IGenericDao<GeoDomainDocument> {
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

    boolean verifyDuplicate(String columnWhereName, String valueWhereName);

    void insertAndTrim(GeoDocument g);

    //method to save
    void saveH(GeoDomainDocument g);

    //method to update
    void updateH(GeoDomainDocument g);

    //method to delete
    void deleteH(GeoDomainDocument g);

    //method to return one of given id
    GeoDomainDocument getHByColumn(String column);

    //method to return all
    List<GeoDomainDocument> getAllH();

    List<GeoDocument> getAllH(final String limit, final String offset);
}
