package com.github.p4535992.extractor.object.impl.jdbc;

import com.github.p4535992.extractor.object.impl.jdbc.generic.GenericDaoImpl;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.dao.jdbc.IGeoDocumentDao;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.hibernate.SessionFactory;
import com.github.p4535992.util.database.sql.SQLSupport;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

/**
 * Created by 4535992 on 01/04/2015.
 * @author 4535992.
 * @version 2015-06-30.
 */
@org.springframework.stereotype.Component("GeoDocumentDao")
public class GeoDocumentDaoImpl extends GenericDaoImpl<GeoDocument> implements IGeoDocumentDao {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( GeoDocumentDaoImpl.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    public GeoDocumentDaoImpl(){}

    @Override
    public void setDriverManager(String driver, String dialectDB, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver, dialectDB, host, port,user,  pass, database);
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void loadSpringConfig(String filePathXml) throws IOException {
        super.loadSpringConfig(filePathXml);
        GeoDocumentDaoImpl g = context.getBean(GeoDocumentDaoImpl.class);
    }

    @Override
    public void setTableSelect(String nameOfTable){super.mySelectTable = nameOfTable;}

    @Override
    public void setTableInsert(String nameOfTable){
        super.myInsertTable = nameOfTable;
    }

    //@Autowired
    //@Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }


    ////////////////
    //SPRING METHOD
    ////////////////

    @Override
    public void create(boolean erase) throws Exception {
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
       super.create(query,erase);


    }

    @Override
    public boolean verifyDuplicate(String column_where, String value_where) {
        try {
            return super.verifyDuplicate(column_where, value_where);
        }catch(MySQLSyntaxErrorException e){
            return false;
        }
    }


    @Override
    @SuppressWarnings("rawtypes")
    public void insertAndTrim(GeoDocument g) {
       SQLSupport support = SQLSupport.getInstance(g,true);
       try {
           //support.getInstance(g);
           //super.insertAndTrim(columns,values,types);
           super.insertAndTrim(support.getCOLUMNS(),support.getVALUES(),support.getTYPES());
        }catch(NullPointerException e){
            logger.error("Null pointer on the query:"+query+":"+e.getCause(),e);
        }
    }

    @Override
    public List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where,List<org.jooq.Condition> conditions){
        return  super.select(columns, columns_where, values_where, null, null,conditions);
    }
}
