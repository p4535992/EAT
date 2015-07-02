package com.github.p4535992.extractor.object.dao.jdbc;

import com.github.p4535992.extractor.object.dao.jdbc.generic.IGenericDao;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.dao.jdbc.generic.IGenericDao;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Created by Marco on 02/04/2015.
 */
public interface IGeoDocumentDao extends IGenericDao<GeoDocument> {
    void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database);
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    void setNewJdbcTemplate();
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml) throws IOException;
    void create(boolean erase) throws Exception;
    boolean verifyDuplicate(String columnWhereName,String valueWhereName);
    void insertAndTrim(GeoDocument obj);
}
