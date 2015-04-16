package object.impl;

import object.model.Document;
import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import object.model.Website;
import object.dao.DocumentDao;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 01/04/2015.
 */
public class DocumentDaoImpl implements DocumentDao {
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
    public DocumentDaoImpl loadSpringConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        DocumentDaoImpl g = contextClassPath.getBean(DocumentDaoImpl.class);
        return g;
    }

    @Override
    public DocumentDaoImpl loadHibernateConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        DocumentDaoImpl g = contextClassPath.getBean(DocumentDaoImpl.class);
        return g;
    }

    @Override
    public void setTable(String nameOfTable){
        this.myTable = nameOfTable;
    }


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    ////////////////
    //SPRING METHOD
    ////////////////


    @Override
    public void create() throws Exception {

    }

    @Override
    public void create(boolean erase) throws Exception {

    }

    @Override
    public boolean verifyDuplicate(String columnWhereName, String valueWhereName) {
        return false;
    }

    @Override
    public List<Map<String, Object>> getAll() {
        return null;
    }

    @Override
    public List<String> selectAllString(String column,String limit, String offset) {
        return null;
    }

    @Override
    public void insertAndTrim(Document obj) {

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

    //method to save
    @Override
    public void saveH(Document g ){
        hibernateTemplate.save(g);
    }
    //method to update
    @Override
    public void updateH(Document g){
        hibernateTemplate.update(g);
    }
    //method to delete
    @Override
    public void deleteH(Document g){
        hibernateTemplate.delete(g);
    }
    //method to return one of given id
    @Override
    public Document  getHByColumn(String column){
        Document g = hibernateTemplate.get(Document.class,column);
        return g;
    }
    //method to return all
    @Override
    public List<Document> getAllH(){
        List<Document> list = new ArrayList<Document>();
        list = hibernateTemplate.loadAll(Document.class);
        return list;
    }



}
