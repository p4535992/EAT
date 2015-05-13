package object.dao.jdbc;

import object.dao.jdbc.generic.IGenericDao;
import object.model.Website;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by 4535992 on 01/04/2015.
 */
public interface IWebsiteDao extends IGenericDao<Website> {

    void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database);
    void setTableSelect(String nameOfTable);
    void setNewJdbcTemplate();
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml);
    boolean verifyDuplicate(String columnWhereName, String valueWhereName);
    List<URL> selectAllUrl(String column_table_input, Integer limit, Integer offset) throws MalformedURLException;
    URL selectURL(String column, String column_where, String value_where);
}
