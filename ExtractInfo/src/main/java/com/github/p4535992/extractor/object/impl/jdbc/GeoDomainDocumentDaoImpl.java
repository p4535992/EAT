package com.github.p4535992.extractor.object.impl.jdbc;

import com.github.p4535992.extractor.object.dao.jdbc.IGeoDomainDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.generic.GenericDaoImpl;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.extractor.object.dao.jdbc.IGeoDomainDocumentDao;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.model.GeoDomainDocument;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import com.github.p4535992.util.string.StringKit;

import javax.sql.DataSource;
import java.net.MalformedURLException;
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
    public void setDriverManager(String driver, String dialectDB, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver, dialectDB, host, port,user,  pass, database);
    }

    @Override
    public void setDataSource(DataSource ds) { super.dataSource = ds;}

    @Override
    public void loadSpringConfig(String filePathXml) {
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

    @Override
    public void setTableUpdate(String nameOfTable){

        myUpdateTable = nameOfTable;
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
    public void create(boolean erase) throws Exception {
        if (myInsertTable.isEmpty()) {
            throw new Exception("Name of the table is empty!!!");
        }
        String query = "CREATE TABLE IF NOT EXISTS `" + myInsertTable + "` (\n" +
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
        super.create(query, erase);
    }

    @Override
    public boolean verifyDuplicate(String column_where, String value_where) {
       return super.verifyDuplicate(column_where, value_where);
    }


    @SuppressWarnings("rawtypes")
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

//        query = "trySelectWithRowMap "+column+" from " + mySelectTable + " LIMIT " + limit + " OFFSET " + offset + "";
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

    public List<GeoDocument> selectGeoDocuments(String column,int limit,int offset){
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
        Object[] params;
        int[] types;
        // define query arguments
        if(g.getDoc_id()==null) {
            query =
                    "INSERT INTO "+myInsertTable+" "
                            + "(url, regione, provincia, city, indirizzo, iva, email, telefono, fax," +
                            " edificio, latitude,longitude,nazione,description,postalCode,indirizzoNoCAP," +
                            "indirizzoHasNumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
           params = new Object[]{
                    g.getUrl().toString().replace("http://", ""), g.getRegione(), g.getProvincia(), g.getCity(),
                    g.getIndirizzo(), g.getIva(), g.getEmail(), g.getTelefono(), g.getFax(), g.getEdificio(), g.getLat(), g.getLng(),
                    g.getNazione(), g.getDescription(), g.getPostalCode(), g.getIndirizzoNoCAP(), g.getIndirizzoHasNumber()};
            // define SQL types of the arguments
            types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,};
        }else{
            query =
                    "INSERT INTO "+myInsertTable+" "
                            + "(doc_id, url, regione, provincia, city, indirizzo, iva, email, telefono, fax," +
                            " edificio, latitude,longitude,nazione,description,postalCode,indirizzoNoCAP," +
                            "indirizzoHasNumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            params = new Object[]{
                    g.getDoc_id(),g.getUrl().toString().replace("http://", ""), g.getRegione(), g.getProvincia(), g.getCity(),
                    g.getIndirizzo(), g.getIva(), g.getEmail(), g.getTelefono(), g.getFax(), g.getEdificio(), g.getLat(), g.getLng(),
                    g.getNazione(), g.getDescription(), g.getPostalCode(), g.getIndirizzoNoCAP(), g.getIndirizzoHasNumber()};
            // define SQL types of the arguments
            types = new int[]{Types.INTEGER,Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,};
        }
        // execute insert query to insert the data
        // return number of row / rows processed by the executed query
        SystemLog.query(query);
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
                SystemLog.query(query);
                jdbcTemplate.execute(query);
            }
        }catch(Exception e){}
    }


    public List<GeoDocument> selectGeoDomainWihNoCoords(String[] columns_where,Object[] values_where,int limit,int offset,String condition) throws MalformedURLException {
        List<GeoDocument> listGeoDoc = new ArrayList<GeoDocument>();
        query = "SELECT * FROM "+mySelectTable+" WHERE ";
        for(int k=0; k < columns_where.length; k++ ){
            query+= columns_where[k] +" ";
            if(values_where[k]== null){ query += " IS NULL ";}
            else{query += " = '" + values_where[k]+"'";}
            if(condition!=null && k < columns_where.length -1){ query += " "+condition.toUpperCase()+" ";}
            else{query += " ";}
        }
        query += " LIMIT "+limit+" OFFSET "+offset+"";
        List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
        SystemLog.query(query);
        for (Map<String, Object> geoDoc : map) {
            GeoDocument g = new GeoDocument();
            for (Iterator<Map.Entry<String, Object>> it = geoDoc.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                String value;
                if (entry.getValue() == null) value = "";
                else value = entry.getValue().toString();
                switch (entry.getKey()) {
                    case "url":
                        value = StringKit.setNullForEmptyString(value);
                        if(!(value.contains("http://"))){
                            value = "http://"+value;
                        }
                        g.setUrl(new URL(value));
                        break;
                    case "doc_id":
                        if (StringKit.setNullForEmptyString(value) == null) g.setDoc_id(null);
                        else g.setDoc_id(Integer.parseInt(value));
                        break;
                    case "city":
                        g.setCity(StringKit.setNullForEmptyString(value));
                        break;
                    case "description":
                        g.setDescription(StringKit.setNullForEmptyString(value));
                        break;
                    case "edificio":
                        g.setEdificio(StringKit.setNullForEmptyString(value));
                        break;
                    case "email":
                        g.setEmail(StringKit.setNullForEmptyString(value));
                        break;
                    case "fax":
                        g.setFax(StringKit.setNullForEmptyString(value));
                        break;
                    case "indirizzo":
                        g.setIndirizzo(StringKit.setNullForEmptyString(value));
                        break;
                    case "indirizzoHasNumber":
                        g.setIndirizzoHasNumber(StringKit.setNullForEmptyString(value));
                        break;
                    case "indirizzoNoCAP":
                        g.setIndirizzoNoCAP(StringKit.setNullForEmptyString(value));
                        break;
                    case "iva":
                        g.setIva(StringKit.setNullForEmptyString(value));
                        break;
                    case "latitude":
                        if (StringKit.setNullForEmptyString(value) == null) g.setLat(null);
                        else g.setLat(Double.parseDouble(value));
                        break;
                    case "longitude":
                        if (StringKit.setNullForEmptyString(value) == null) g.setLng(null);
                        else g.setLng(Double.parseDouble(value));
                        break;
                    case "nazione":
                        g.setNazione(StringKit.setNullForEmptyString(value));
                        break;
                    case "postalCode":
                        g.setPostalCode(StringKit.setNullForEmptyString(value));
                        break;
                    case "regione":
                        g.setRegione(StringKit.setNullForEmptyString(value));
                        break;
                    case "provincia":
                        g.setProvincia(StringKit.setNullForEmptyString(value));
                        break;
                }
            }
            listGeoDoc.add(g);
        /*return jdbcTemplate.query(query,
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
        );*/
        }
        return listGeoDoc;
    }

    public void update(String[] columns,Object[] values,String column_where,String value_where){
        try {
            super.update(columns,values,column_where,value_where);
        }catch(org.springframework.jdbc.BadSqlGrammarException e){
            SystemLog.warning(e.getMessage());
            if(values[0]==null && values[1]==null) {
                query = "UPDATE " + myUpdateTable + " SET latitude=NULL, longitude=NULL WHERE url='" + value_where + "'";
            }else{
                query = "UPDATE " + myUpdateTable + " SET latitude='" + values[0] + "', longitude='" + values[1] + "' WHERE url='" + value_where + "'";
            }
            jdbcTemplate.execute(query);
            SystemLog.query(query);
        }
    }




}
