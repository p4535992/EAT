package object.dao;
import object.model.Document;
import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;

/**
 * Created by 4535992 on 01/04/2015.
 */
public interface IDocumentDao extends IGenericDao< Document> {

    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setTableSelect(String nameOfTable);
    void setNewJdbcTemplate();
    void setHibernateTemplate(HibernateTemplate ht);
    void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml);
    void loadHibernateConfig(String filePathXml);

    String selectValueForSpecificColumn(String column, String column_where, String value_where);

    //method to update
    void updateH(Document g);

    //method to return one of given id
    Document  getHByColumn(String column);

}
