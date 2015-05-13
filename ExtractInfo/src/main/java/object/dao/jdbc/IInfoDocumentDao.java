package object.dao.jdbc;

import object.dao.jdbc.generic.IGenericDao;
import object.model.GeoDocument;
import object.model.InfoDocument;

import javax.sql.DataSource;

/**
 * Created by Marco on 02/04/2015.
 */
public interface IInfoDocumentDao extends IGenericDao<InfoDocument> {

    void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database);
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    void setNewJdbcTemplate();
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml);
    void create() throws Exception;
    void create(boolean erase) throws Exception;
    boolean verifyDuplicate(String columnWhereName, String valueWhereName);
    int getCount();
    void deleteAll();
    void insertAndTrim(GeoDocument info);
}
