package com.github.p4535992.extractor.object.impl.jdbc;

import com.github.p4535992.extractor.object.dao.jdbc.IGeoDomainDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.generic.GenericDaoImpl;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.model.GeoDomainDocument;
import com.github.p4535992.util.database.sql.SQLSupport;
import com.github.p4535992.util.string.StringUtilities;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by 4535992 on 01/04/2015.
 * @author 4535992.
 * @version 2015-06-30.
 */
@SuppressWarnings("unused")
@org.springframework.stereotype.Component("GeoDomainDocumentDao")
public class GeoDomainDocumentDaoImpl extends GenericDaoImpl<GeoDomainDocument> implements IGeoDomainDocumentDao {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( GeoDomainDocumentDaoImpl.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

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
    public void create(boolean erase) {
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
        try {
            return super.verifyDuplicate(column_where, value_where);
        } catch (MySQLSyntaxErrorException e) {
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void insertAndTrim(GeoDomainDocument g) {
        SQLSupport<GeoDomainDocument> support = SQLSupport.getInstance(g);
        super.insertAndTrim(support.getCOLUMNS(),support.getVALUES(),support.getTYPES());
    }

    @SuppressWarnings("rawtypes")
    public List<GeoDocument> selectAllGeoDocument(final String column,String limit, String offset) {
        query = "SELECT * FROM "+mySelectTable+" LIMIT 1 OFFSET 0";
        if(Objects.equals(column, "*")){
            Connection connection;
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
        logger.info(query);
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
    }

    public List<GeoDocument> selectGeoDocuments(String column,int limit,int offset){
        List<GeoDocument> ges = new ArrayList<>();

        query = "select "+column+" from " + mySelectTable + " LIMIT " + limit + " OFFSET " + offset + "";
        try {
            List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
            for (Map<String, Object> geoDoc : map) {
                GeoDocument g = new GeoDocument();
                for (Map.Entry<String, Object> entry : geoDoc.entrySet()) {
                    String value = "";
                    if (entry.getValue() != null) value = entry.getValue().toString();
                    switch (entry.getKey()) {
                        case "url":
                            if(!StringUtilities.isNullOrEmpty(value)){
                                if(StringUtilities.isURLWithProtocol(value)) {
                                    g.setUrl(new URL(value));
                                }else{
                                    g.setUrl(new URL("http://"+value));
                                }
                            }
                            else g.setUrl(null);
                            break;
                        case "doc_id":
                            if (StringUtilities.isNullOrEmpty(value)) g.setDoc_id(null);
                            else g.setDoc_id(Integer.parseInt(value));
                            break;
                        case "city":
                            g.setCity(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "description":
                            g.setDescription(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "edificio":
                            g.setEdificio(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "email":
                            g.setEmail(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "fax":
                            g.setFax(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "indirizzo":
                            g.setIndirizzo(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "indirizzoHasNumber":
                            g.setIndirizzoHasNumber(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "indirizzoNoCAP":
                            g.setIndirizzoNoCAP(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "iva":
                            g.setIva(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "latitude":
                            if (StringUtilities.setNullForEmptyString(value) == null) g.setLat(null);
                            else g.setLat(Double.parseDouble(value));
                            break;
                        case "longitude":
                            if (StringUtilities.setNullForEmptyString(value) == null) g.setLng(null);
                            else g.setLng(Double.parseDouble(value));
                            break;
                        case "nazione":
                            g.setNazione(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "postalCode":
                            g.setPostalCode(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "regione":
                            g.setRegione(StringUtilities.setNullForEmptyString(value));
                            break;
                        case "provincia":
                            g.setProvincia(StringUtilities.setNullForEmptyString(value));
                            break;
                    }
                }
                ges.add(g);
            }
        }catch(Exception e){
             e.printStackTrace();
        }
        return ges;

    }

    public void update(String[] columns,Object[] values,String column_where,String value_where){
        try {
            super.update(columns,values,column_where,value_where);
        }catch(org.springframework.jdbc.BadSqlGrammarException e){
            logger.warn(e.getMessage());
            if(values[0]==null && values[1]==null) {
                query = "UPDATE " + myUpdateTable + " SET latitude=NULL, longitude=NULL WHERE url='" + value_where + "'";
            }else{
                query = "UPDATE " + myUpdateTable + " SET latitude='" + values[0] + "', longitude='" + values[1] + "' WHERE url='" + value_where + "'";
            }
            jdbcTemplate.execute(query);
            logger.info(query);
        }
    }


    public List<GeoDomainDocument> selectGeoDomainWihNoCoords(String[] columns,String[] columns_where,
                                    Object[] values_where, Integer limit, Integer offset, List<org.jooq.Condition> conditions) {
        return super.trySelect(columns, columns_where, values_where, limit, offset, conditions);
    }
}
