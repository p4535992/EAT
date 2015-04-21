package object.impl;

import object.dao.IGeoDomainDocumentDao;
import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate4.HibernateCallback;
import util.SystemLog;
import util.string.StringKit;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 01/04/2015.
 */
@org.springframework.stereotype.Component("GeoDomainDocumentDao")
public class GeoDomainDocumentDaoImpl extends GenericDaoImpl<GeoDomainDocument> implements IGeoDomainDocumentDao {

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver,typeDb, host, port,user,  pass, database);
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void loadSpringConfig(String filePathXml) {
        context = new ClassPathXmlApplicationContext(filePathXml);
        GeoDomainDocumentDaoImpl g = context.getBean(GeoDomainDocumentDaoImpl.class);
    }

    @Override
    public void loadHibernateConfig(String filePathXml) {
        context = new ClassPathXmlApplicationContext(filePathXml);
        GeoDomainDocumentDaoImpl g = context.getBean(GeoDomainDocumentDaoImpl.class);
    }

    @Override
    public void setTableSelect(String nameOfTable){
        mySelectTable = nameOfTable;
    }

    @Override
    public void setTableInsert(String nameOfTable){

        myInsertTable = nameOfTable;
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
        if(myInsertTable.isEmpty()) {
            throw new Exception("Name of the table is empty!!!");
        }
        String query ="CREATE TABLE IF NOT EXISTS `"+myInsertTable+"` (\n" +
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
            query = "DROP TABLE IF EXISTS "+myInsertTable+";";
            jdbcTemplate.execute(query);
        }
        create();
    }

    @Override
    public boolean verifyDuplicate(String columnWhereName, String valueWhereName) {
        int c = this.jdbcTemplate.queryForObject("select count(*) from "+myInsertTable+" where "+columnWhereName+"='"+valueWhereName+"'", Integer.class);
        boolean b = false;
        if(c > 0){
            b = true;
        }
        return b;
    }


    public List<GeoDocument> selectAllGeoDocument(final String column,String limit, String offset) {
        query = "SELECT * FROM "+mySelectTable+" LIMIT 1 OFFSET 0";
        if(column == "*"){
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                PreparedStatement p = connection.prepareStatement(query);
                ResultSet rs = p.executeQuery();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int numberOfColumns = rsMetaData.getColumnCount();
                query = "SELECT ";
                // get the column names; column indexes start from 1
                for (int i = 1; i < numberOfColumns + 1; i++) {
                    query += rsMetaData.getColumnName(i);
                    if(i < numberOfColumns){query += " ,";}
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        query += " FROM "+mySelectTable+" LIMIT "+limit+" OFFSET "+offset+"";
        SystemLog.message(query);
        return jdbcTemplate.query(query,
                new RowMapper<GeoDocument>() {
                    @Override
                    public GeoDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final GeoDocument w = new GeoDocument();
                        ResultSetExtractor extractor = new ResultSetExtractor() {
                            @Override
                            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                                w.setDoc_id(rs.getInt("doc_id"));
                                w.setCity(rs.getString("city"));
                                w.setUrl(rs.getURL("url"));
                                w.setDescription(rs.getString("description"));
                                w.setEdificio(rs.getString("edificio"));
                                w.setEmail(rs.getString("email"));
                                w.setFax(rs.getString("fax"));
                                w.setIndirizzo(rs.getString("indirizzo"));
                                w.setIndirizzoHasNumber(rs.getString("indirizzoHasNumber"));
                                w.setIndirizzoNoCAP(rs.getString("indirizzoNoCAP"));
                                w.setIva(rs.getString("iva"));
                                w.setLat(rs.getDouble("latitude"));
                                w.setLng(rs.getDouble("longitude"));
                                w.setNazione(rs.getString("nazione"));
                                w.setPostalCode(rs.getString("postalCode"));
                                w.setRegione(rs.getString("regione"));
                                w.setProvincia(rs.getString("provincia"));
                                return w;
                            }
                        };
                        return w;
                    }
                }
        );
        //Class aClass = GeoDocument.class;
        //return super.selectAll(aClass,column, limit, offset);

//        query = "select "+column+" from " + mySelectTable + " LIMIT " + limit + " OFFSET " + offset + "";
//        List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
//        for (Map<String, Object> geoDoc : map) {
//            for (Iterator<Map.Entry<String, Object>> it = geoDoc.entrySet().iterator(); it.hasNext();) {
//                Map.Entry<String, Object> entry = it.next();
//                String key = entry.getKey();
//                Object value = entry.getValue();
//                //System.out.println(key + " = " + value);
//            }
//
//            System.out.println();
//
//        }
    }

    public List<GeoDocument> selectGeoDocuments(String column,String limit,String offset){
        List<GeoDocument> ges = new ArrayList<>();

        query = "select "+column+" from " + mySelectTable + " LIMIT " + limit + " OFFSET " + offset + "";
        try {
            List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
            for (Map<String, Object> geoDoc : map) {
                GeoDocument g = new GeoDocument();
                for (Iterator<Map.Entry<String, Object>> it = geoDoc.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    String value;
                    if(entry.getValue()==null) value  = "";
                    else value = entry.getValue().toString();
                    switch (entry.getKey()) {
                        case "url": g.setUrl(new URL(StringKit.setNullForEmptyString(value)));break;
                        case"doc_id":
                            if(StringKit.setNullForEmptyString(value) == null)  g.setDoc_id(null);
                            else  g.setDoc_id(Integer.parseInt(value));
                            break;
                        case"city": g.setCity(StringKit.setNullForEmptyString(value));break;
                        case"description": g.setDescription(StringKit.setNullForEmptyString(value));break;
                        case"edificio": g.setEdificio(StringKit.setNullForEmptyString(value));break;
                        case"email": g.setEmail(StringKit.setNullForEmptyString(value));break;
                        case"fax": g.setFax(StringKit.setNullForEmptyString(value));break;
                        case"indirizzo": g.setIndirizzo(StringKit.setNullForEmptyString(value));break;
                        case"indirizzoHasNumber": g.setIndirizzoHasNumber(StringKit.setNullForEmptyString(value));break;
                        case"indirizzoNoCAP": g.setIndirizzoNoCAP(StringKit.setNullForEmptyString(value));break;
                        case"iva": g.setIva(StringKit.setNullForEmptyString(value));break;
                        case"latitude":
                            if(StringKit.setNullForEmptyString(value) == null)  g.setLat(null);
                            else g.setLat(Double.parseDouble(value));
                            break;
                        case"longitude":
                            if(StringKit.setNullForEmptyString(value) == null)  g.setLng(null);
                            else g.setLng(Double.parseDouble(value));
                            break;
                        case"nazione": g.setNazione(StringKit.setNullForEmptyString(value));break;
                        case"postalCode": g.setPostalCode(StringKit.setNullForEmptyString(value));break;
                        case"regione": g.setRegione(StringKit.setNullForEmptyString(value));break;
                        case"provincia": g.setProvincia(StringKit.setNullForEmptyString(value));break;
                    }
                }
                ges.add(g);
            }
        }catch(Exception e){
             e.printStackTrace();
        }
        return ges;

    }

    @Override
    public void insertAndTrim(GeoDocument g) {
        String query =
                "INSERT INTO "+myInsertTable+" "
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
        query = "SELECT * FROM "+myInsertTable+" LIMIT 1";

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet rs = p.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            // get the column names; column indexes start from 1
            for (int i = 1; i < numberOfColumns + 1; i++) {
                query = "UPDATE `" + myInsertTable + "` SET `" + rsMetaData.getColumnName(i) + "` = LTRIM(RTRIM(`" + rsMetaData.getColumnName(i) + "`));";
                SystemLog.message(query);
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
                    Query query = session.createQuery("from " + mySelectTable + "");
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
