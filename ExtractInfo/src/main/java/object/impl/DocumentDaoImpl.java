package object.impl;

import object.model.Document;
import object.dao.IDocumentDao;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;

/**
 * Created by Marco on 01/04/2015.
 */
@org.springframework.stereotype.Component("DocumentDao")
public class DocumentDaoImpl extends GenericDaoImpl<Document> implements IDocumentDao {

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver,typeDb, host, port,user,  pass, database);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void loadSpringConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        DocumentDaoImpl g = contextClassPath.getBean(DocumentDaoImpl.class);
    }

    @Override
    public void loadHibernateConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        DocumentDaoImpl g = contextClassPath.getBean(DocumentDaoImpl.class);
    }

    @Override
    public void setTableSelect(String nameOfTable){
        this.mySelectTable = nameOfTable;
    }


    @Override
    public String selectValueForSpecificColumn(String column, String column_where, String value_where){
        String city ="";
        try {
            String query = "SELECT " + column + " from " + mySelectTable + " WHERE " + column_where + " = ?";
            city = (String) jdbcTemplate.queryForObject(query, new Object[]{value_where}, String.class);
        }catch(org.springframework.dao.EmptyResultDataAccessException e){
            city = "";
        }
        //String name = (String)getJdbcTemplate().queryForObject(query, new Object[] { custId }, String.class);
        return city;
    }


    ///////////////////////
    //HIBERNATE
    //////////////////////

    @Override
    public void updateH(Document g){
        hibernateTemplate.update(g);
    }

    //method to return one of given id
    @Override
    public Document  getHByColumn(String column){
        Document g = hibernateTemplate.get(Document.class,column);
        return g;
    }





}
