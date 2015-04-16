package object.dao;

import object.impl.InfoDocumentDaoImpl;
import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import object.model.InfoDocument;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by Marco on 02/04/2015.
 */
public interface InfoDocumentDao {
    void setSecondTable(String nameOfTable);

    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setTable(String nameOfTable);
    void setJdbcTemplate(JdbcTemplate jdbcTemplate);
    void setHibernateTemplate(HibernateTemplate ht);
    void setHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);
    InfoDocumentDaoImpl loadSpringConfig(String filePathXml);
    InfoDocumentDaoImpl loadHibernateConfig(String filePathXml);

    void create() throws Exception;

    void create(boolean erase) throws Exception;

    boolean verifyDuplicate(String columnWhereName, String valueWhereName);

    int getCount();

    void deleteAll();

    void insertAndTrim(GeoDocument info);

    //method to save
    void saveH(InfoDocument g);

    //method to update
    void updateH(InfoDocument g);

    //method to delete
    void deleteH(InfoDocument g);

    //method to return one of given id
    InfoDocument getHByColumn(String column);

    //method to return all
    List<InfoDocument> getAllH();
}
