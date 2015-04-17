package object.impl;

import object.dao.IGeoDomainDocumentDao;
import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import util.SystemLog;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 01/04/2015.
 */
@org.springframework.stereotype.Component("GeoDomainDocumentDao")
public class GeoDomainDocumentDaoImpl extends GenericDaoImpl<GeoDomainDocument> implements IGeoDomainDocumentDao {
    private DriverManagerDataSource driverManag;
    private JdbcTemplate jdbcTemplate;
    private String myTable;
    private String myTableSelect;
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
        GeoDomainDocumentDaoImpl g = contextClassPath.getBean(GeoDomainDocumentDaoImpl.class);
    }

    @Override
    public void loadHibernateConfig(String filePathXml) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        GeoDomainDocumentDaoImpl g = contextClassPath.getBean(GeoDomainDocumentDaoImpl.class);
    }

    @Override
    public void setTableSelect(String nameOfTable){
        this.myTableSelect = nameOfTable;
    }

    @Override
    public void setTableInsert(String nameOfTable){

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
        String query ="CREATE TABLE IF NOT EXISTS `"+myTable+"` (\n" +
                "  `doc_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `url` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `regione` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `provincia` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `city` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzo` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `iva` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `email` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `telefono` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `fax` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `edificio` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `latitude` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `longitude` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `nazione` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `description` varchar(5000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `postalCode` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzoNoCAP` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzoHasNumber` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  PRIMARY KEY (`doc_id`),\n" +
                "  KEY `url` (`url`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;";
        jdbcTemplate.execute(query);
    }

    @Override
    public void create(boolean erase) throws Exception {
        String query;
        if(erase==true) {
            query = "DROP TABLE IF EXISTS "+myTable+";";
            jdbcTemplate.execute(query);
        }
        create();
    }

    @Override
    public boolean verifyDuplicate(String columnWhereName, String valueWhereName) {
        int c = this.jdbcTemplate.queryForObject("select count(*) from "+myTable+" where "+columnWhereName+"='"+valueWhereName+"'", Integer.class);
        boolean b = false;
        if(c > 0){
            b = true;
        }
        return b;
    }



    @Override
    public List<String> selectAllString(final String column,String limit, String offset) {
        return this.jdbcTemplate.query("select "+column+" from " + myTable + " LIMIT " + limit + " OFFSET " + offset + "",
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(column).toString();
                    }
                }
        );
    }

    @Override
    public void insertAndTrim(GeoDocument g) {
        String query =
                "INSERT INTO "+myTable+" "
                        + "(url, regione, provincia, city, indirizzo, iva, email, telefono, fax," +
                        " edificio, latitude,longitude,nazione,description,postalCode,indirizzoNoCAP," +
                        "indirizzoHasNumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        // define query arguments
        Object[] params = new Object[] {
                g.getUrl(), g.getRegione(), g.getProvincia(), g.getCity(),
                g.getIndirizzo(),g.getIva(),g.getEmail(),g.getTelefono(),g.getFax(),g.getEdificio(),g.getLat(),g.getLng(),
                g.getNazione(),g.getDescription(),g.getPostalCode(),g.getIndirizzoNoCAP(),g.getIndirizzoHasNumber()};
        // define SQL types of the arguments
        int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR,};
        // execute insert query to insert the data
        // return number of row / rows processed by the executed query
        jdbcTemplate.update(query, params, types);

        //Method 1 ROWMAP
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
    public void saveH(GeoDomainDocument g ){
        hibernateTemplate.save(g);
    }
    //method to update
    @Override
    public void updateH(GeoDomainDocument g){
        hibernateTemplate.update(g);
    }
    //method to delete
    @Override
    public void deleteH(GeoDomainDocument g){
        hibernateTemplate.delete(g);
    }
    //method to return one of given id
    @Override
    public GeoDomainDocument  getHByColumn(String column){
        GeoDomainDocument g = hibernateTemplate.get(GeoDomainDocument.class,column);
        return g;
    }
    //method to return all
    @Override
    public List<GeoDomainDocument> getAllH(){
        List<GeoDomainDocument> list = new ArrayList<GeoDomainDocument>();
        list = hibernateTemplate.loadAll(GeoDomainDocument.class);
        return list;
    }

    @Override
    public List<GeoDocument> getAllH(final String limit, final String offset){
        List<GeoDocument> docs = new ArrayList<GeoDocument>();
        //METHOD 1 (Boring)
        /*
        Query q = getHibernateTemplate().getSession().createQuery("from User");
        q.setFirstResult(0); // modify this to adjust paging
        q.setMaxResults(limit);
        return (List<User>) q.list();
        */
        if(limit != null && offset != null) {
            //METHOD 2 (Probably the best)
            docs =
             (List<GeoDocument>) hibernateTemplate.execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    Query query = session.createQuery("from " + myTableSelect + "");
                    query.setFirstResult(Integer.parseInt(offset));
                    query.setMaxResults(Integer.parseInt(limit));
                    return (List<GeoDocument>) query.list();
                }
            });
        }
        return docs;
    }



   // If you'd like to use HibernateTemplate you can do something like this:
/*
    @SuppressWarnings("unchecked")
    public List<User> getUsers(final int limit) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<User>>() {
            @Override
            public List<User> doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(User.class).setMaxResults(limit).list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<User> getUsers(final int limit) {
        return getHibernateTemplate().executeFind(new HibernateCallback<List<User>>() {
            @Override
            public List<User> doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from User u").setMaxResults(limit).list();
            }
        });
    }
    */
}
