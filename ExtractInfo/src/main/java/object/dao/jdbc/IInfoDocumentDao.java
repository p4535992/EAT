package object.dao.jdbc;

import object.dao.jdbc.generic.IGenericDao;
import object.model.GeoDocument;
import object.model.InfoDocument;

import javax.sql.DataSource;

/**
 * Created by Marco on 02/04/2015.
 */
public interface IInfoDocumentDao extends IGenericDao<InfoDocument> {

    void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database);
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    void setNewJdbcTemplate();
    //void setHibernateTemplate(HibernateTemplate ht);
    //void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml);
    //void loadHibernateConfig(String filePathXml);

    void create() throws Exception;

    void create(boolean erase) throws Exception;

    boolean verifyDuplicate(String columnWhereName, String valueWhereName);

    int getCount();

    void deleteAll();

    void insertAndTrim(GeoDocument info);

    //method to save
    //void saveH(InfoDocument g);

    //method to update
    //void updateH(InfoDocument g);

    //method to delete
    //void deleteH(InfoDocument g);

    //method to return one of given id
    //InfoDocument getHByColumn(String column);

    //method to return all
    //List<InfoDocument> getAllH();
}
