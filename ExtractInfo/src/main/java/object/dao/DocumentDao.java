package object.dao;

import object.impl.DocumentDaoImpl;
import object.model.Document;
import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import object.model.Website;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 01/04/2015.
 */
public interface DocumentDao {

    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setTable(String nameOfTable);
    void setJdbcTemplate(JdbcTemplate jdbcTemplate);
    void setHibernateTemplate(HibernateTemplate ht);
    void setHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);
    DocumentDaoImpl loadSpringConfig(String filePathXml);
    DocumentDaoImpl loadHibernateConfig(String filePathXml);

    String selectValueForSpecificColumn(String column, String column_where, String value_where);

    void create() throws Exception;
    void create(boolean erase) throws Exception;
    boolean verifyDuplicate(String columnWhereName,String valueWhereName);

    List<Map<String, Object>> getAll();

    List<String> selectAllString(String column, String limit, String offset);

    void insertAndTrim(Document obj);

    //method to save
    void saveH(Document g);

    //method to update
    void updateH(Document g);

    //method to delete
    void deleteH(Document g);

    //method to return one of given id
    Document  getHByColumn(String column);

    //method to return all
    List<Document> getAllH();
}
