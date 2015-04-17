package object.impl;

import object.dao.IInfoDocumentDao;
import object.model.GeoDocument;
import object.model.InfoDocument;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTemplate;
import util.SystemLog;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 01/04/2015.
 */
@org.springframework.stereotype.Component("InfoDocumentDao")
public class InfoDocumentDaoImpl extends GenericDaoImpl<InfoDocument> implements IInfoDocumentDao {

    private String mySecondTable;

    @Override
    public void setSecondTable(String nameOfTable){
        mySecondTable = nameOfTable;
    }

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
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new HibernateTemplate(getSessionFactory());
    }


    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void loadSpringConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        InfoDocumentDaoImpl g = contextClassPath.getBean(InfoDocumentDaoImpl.class);
    }

    @Override
    public void loadHibernateConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        InfoDocumentDaoImpl g = contextClassPath.getBean(InfoDocumentDaoImpl.class);
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
        if(myTable.isEmpty()) {
            throw new Exception("Name of the table is empty!!!");
        }
        String query;

        //Copy the geodocument table
        try {
            query = "CREATE TABLE " + myTable + " LIKE " + mySecondTable + ";";
            SystemLog.write(query, "OUT");
            jdbcTemplate.execute(query);
            query = "INSERT " + myTable + " SELECT * FROM " + mySecondTable + ";";
            SystemLog.write(query, "OUT");
            jdbcTemplate.execute(query);

            //Add identifier
            query = "ALTER TABLE " + myTable + " ADD identifier varchar(1000);";
            SystemLog.write(query, "OUT");
            jdbcTemplate.execute(query);
            query = "UPDATE " + myTable + " SET identifier = edificio;";
            jdbcTemplate.execute(query);

            //Add name location
            query = " ALTER TABLE " + myTable + " ADD name_location varchar(1000);";
            SystemLog.write(query, "OUT");
            jdbcTemplate.execute(query);
            query = "SELECT identifier FROM " + myTable + ";";
            SystemLog.write(query, "OUT");
            jdbcTemplate.execute(query);
            query = "UPDATE " + myTable + " SET name_location = identifier;";
            SystemLog.write(query, "OUT");
            jdbcTemplate.execute(query);
            query = "UPDATE " + myTable + " SET name_location=CONCAT('Location_',name_location);";
            SystemLog.write(query, "OUT");
        }catch(Exception e){
            if(!e.getMessage().contains("already exists")){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void create(boolean erase) throws Exception {
        String query;
        if(erase==true){
            query ="DROP TABLE IF EXISTS "+myTable+";";
            jdbcTemplate.execute(query);
        }
        create();
    }


    @Override
    public boolean verifyDuplicate(String columnWhereName,String valueWhereName){
        int c = this.jdbcTemplate.queryForObject("select count(*) from "+myTable+" where "+columnWhereName+"='"+valueWhereName+"'", Integer.class);
        boolean b = false;
        if(c > 0){
            b = true;
        }
        return b;
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from " + myTable + "", Integer.class);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE from "+myTable+"");
    }

    @Override
    public void insertAndTrim(GeoDocument g){
        String query =
                "INSERT INTO "+myTable+" "
                        + "(url, regione, provincia, city, indirizzo, iva, email, telefono, fax," +
                        " edificio, latitude,longitude,nazione,description,postalCode,indirizzoNoCAP," +
                        "indirizzoHasNumber,identifier,name_location) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        String identifier = g.getEdificio().replace("\""," ").replace("^"," ").replace("|","")
                .replace(":", "").replace("http","").replaceAll("\\s+","\\s").replaceAll("\\s","_");
        String name_location = "Location_"+identifier;
        // define query arguments
        Object[] params = new Object[] {
                g.getUrl(), g.getRegione(), g.getProvincia(), g.getCity(),
                g.getIndirizzo(),g.getIva(),g.getEmail(),g.getTelefono(),g.getFax(),g.getEdificio(),g.getLat(),g.getLng(),
                g.getNazione(),g.getDescription(),g.getPostalCode(),g.getIndirizzoNoCAP(),g.getIndirizzoHasNumber(),
        identifier,name_location};
        // define SQL types of the arguments
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR
                ,Types.VARCHAR, Types.VARCHAR};
        // execute insert query to insert the data
        // return number of row / rows processed by the executed query
        jdbcTemplate.update(query, params, types);

        query = "SELECT * FROM "+myTable+" LIMIT 1";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet rs = p.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            // get the column names; column indexes start from 1
            for (int i = 1; i < numberOfColumns + 1; i++) {
                query = "UPDATE `" + myTable + "` SET `" + rsMetaData.getColumnName(i) + "` = LTRIM(RTRIM(`" + rsMetaData.getColumnName(i) + "`));";
                SystemLog.write(query, "OUT");
                jdbcTemplate.execute(query);
            }
        }catch(Exception e){}
    }

    ///////////////////////
    //HIBERNATE
    //////////////////////

    //method to save
    @Override
    public void saveH(InfoDocument g ){
        hibernateTemplate.save(g);
    }
    //method to update
    @Override
    public void updateH(InfoDocument g){
        hibernateTemplate.update(g);
    }
    //method to delete
    @Override
    public void deleteH(InfoDocument g){
        hibernateTemplate.delete(g);
    }
    //method to return one of given id
    @Override
    public InfoDocument  getHByColumn(String column){
        InfoDocument g = hibernateTemplate.get(InfoDocument.class,column);
        return g;
    }
    //method to return all
    @Override
    public List<InfoDocument> getAllH(){
        List<InfoDocument> list = new ArrayList<InfoDocument>();
        list = hibernateTemplate.loadAll(InfoDocument.class);
        return list;
    }
}
