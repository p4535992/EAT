package com.github.p4535992.extractor.object.impl.jdbc;

import com.github.p4535992.extractor.object.impl.jdbc.generic.GenericDaoImpl;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.dao.jdbc.IInfoDocumentDao;
import com.github.p4535992.extractor.object.model.InfoDocument;
import com.github.p4535992.util.database.sql.query.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import java.sql.*;

/**
 * Created by 4535992 on 01/04/2015.
 * @author 4535992.
 * @version 2015-12-18.
 */
@org.springframework.stereotype.Component("InfoDocumentDao")
public class InfoDocumentDaoImpl extends GenericDaoImpl<InfoDocument> implements IInfoDocumentDao {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( InfoDocumentDaoImpl.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    @Override
    public void setDriverManager(String driver, String dialectDB, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver, dialectDB, host, port,user,  pass, database);
    }


    @Override
    public void loadSpringConfig(String filePathXml) {
        context = new ClassPathXmlApplicationContext(filePathXml);
        InfoDocumentDaoImpl g = context.getBean(InfoDocumentDaoImpl.class);
    }

    @Override
    public void setTableSelect(String nameOfTable){
        super.mySelectTable = nameOfTable;
    }


    public void setSessionFactory(SessionFactory sessionFactory) {
        super.sessionFactory = sessionFactory;
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    ////////////////
    //SPRING METHOD
    ////////////////


    @Override
    public void create() {
        String query;
        //Copy the geodocument table
        try {
            query = "CREATE TABLE " + myInsertTable + " LIKE " + mySelectTable + ";";
            logger.info(query);
            jdbcTemplate.execute(query);
            query = "INSERT " + myInsertTable + " SELECT * FROM " + mySelectTable + ";";
            logger.info(query);
            jdbcTemplate.execute(query);

            //Add identifier
            query = "ALTER TABLE " + myInsertTable + " ADD identifier varchar(1000);";
            logger.info(query);
            jdbcTemplate.execute(query);
            query = "UPDATE " + myInsertTable + " SET identifier = doc_id;";
            jdbcTemplate.execute(query);

            //Add name location
            query = " ALTER TABLE " +myInsertTable + " ADD name_location varchar(1000);";
            logger.info(query);
            jdbcTemplate.execute(query);
            query = "SELECT identifier FROM " + myInsertTable + ";";
            logger.info(query);
            jdbcTemplate.execute(query);
            query = "UPDATE " + myInsertTable + " SET name_location = identifier;";
            logger.info(query);
            jdbcTemplate.execute(query);
            query = SQLQuery.updateColumnToMD5Hash(myInsertTable, "url", "identifier",false);
            logger.info(query);
            jdbcTemplate.execute(query);
            query = "UPDATE " + myInsertTable + " SET name_location=CONCAT('Location_',identifier);";
            logger.info(query);
            jdbcTemplate.execute(query);


        }catch(org.springframework.jdbc.BadSqlGrammarException e){
            logger.warn(gm() + "Make sure the tables you try to use exists!!!");
            if(e.getMessage().contains("Table '"+myInsertTable+"' already exists")){
                logger.warn(gm() + "Table '"+myInsertTable+"' already exists",e);
            }else {
                logger.error(gm() +e.getMessage(),e);
            }
        }

    }

    @Override
    public void create(boolean erase) {
        if(erase==true){
            query ="DROP TABLE IF EXISTS "+myInsertTable+";";
            jdbcTemplate.execute(query);
        }
        create();
    }


    @Override
    public boolean verifyDuplicate(String column_where,String value_where){
        int c = this.jdbcTemplate.queryForObject("select count(*) from "+myInsertTable+" where "+ column_where +"='"+ value_where +"'", Integer.class);
        boolean b = false;
        if(c > 0){
            b = true;
        }
        return b;
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from " + myInsertTable + "", Integer.class);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE from "+myInsertTable+"");
    }


    @Override
    public void insertAndTrim(GeoDocument g){
        String query =
                "INSERT INTO "+myInsertTable+" "
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
                logger.info(query);
                jdbcTemplate.execute(query);
            }
        }catch(Exception e){}
    }

    ///////////////////////
    //HIBERNATE
    //////////////////////


}
