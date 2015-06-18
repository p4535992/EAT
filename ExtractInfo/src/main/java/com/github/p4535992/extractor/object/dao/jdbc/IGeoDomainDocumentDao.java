package com.github.p4535992.extractor.object.dao.jdbc;

import com.github.p4535992.extractor.object.dao.jdbc.generic.IGenericDao;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.model.GeoDomainDocument;

import javax.sql.DataSource;

/**
 * Created by Marco on 02/04/2015.
 */
public interface IGeoDomainDocumentDao extends IGenericDao<GeoDomainDocument> {
    void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database);
    void setTableInsert(String nameOfTable);
    void setTableSelect(String nameOfTable);
    void setTableUpdate(String nameOfTable);
    void setNewJdbcTemplate();
    //void setHibernateTemplate(HibernateTemplate ht);
    //void setNewHibernateTemplate(SessionFactory sessionFactory);
    void setDataSource(DataSource ds);
    void loadSpringConfig(String filePathXml);
    //void loadHibernateConfig(String filePathXml) throws IOException;

    void create(boolean erase) throws Exception;

    boolean verifyDuplicate(String columnWhereName, String valueWhereName);

    void insertAndTrim(GeoDocument g);

    void update(String[] columns,Object[] values,String column_where,String value_where);
    //method to return one of given id
    //GeoDomainDocument getHByColumn(String column);
    //List<GeoDocument> getAllH(final String limit, final String offset);

    //String prepareSelectQuery(String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition);
}
