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
    private DriverManagerDataSource driverManag;
    private JdbcTemplate jdbcTemplate;
    private String myTable;
    private DataSource dataSource;
    private HibernateTemplate hibernateTemplate;
    private SessionFactory sessionFactory;
    private ClassPathXmlApplicationContext contextClassPath;

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.mysql.jdbc.Driver"
        driverManag.setUrl("" + typeDb + "://" + host + ":" + port + "/" + database); //"jdbc:mysql://localhost:3306/jdbctest"
        driverManag.setUsername(user);
        driverManag.setPassword(pass);
        this.jdbcTemplate = new JdbcTemplate();
        this.dataSource = driverManag;
        this.jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new JdbcTemplate();
        this.jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public void setHibernateTemplate(HibernateTemplate ht) {
        this.hibernateTemplate = ht;
    }

    @Override
    public void setHibernateTemplate(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
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
    public void setTable(String nameOfTable){
        this.myTable = nameOfTable;
    }


    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public String selectValueForSpecificColumn(String column, String column_where, String value_where){
        String query = "SELECT "+ column + " from " + myTable + " WHERE " + column_where + "= ?";
        String city =(String) jdbcTemplate.queryForObject(query ,new Object[]{value_where},String.class);
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
