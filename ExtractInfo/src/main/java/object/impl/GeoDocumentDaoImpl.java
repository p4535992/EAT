package object.impl;

import object.dao.IGeoDocumentDao;
import object.model.GeoDocument;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import util.ResourcesKit;
import util.SystemLog;

import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.List;

/**
 * Created by Marco on 01/04/2015.
 */
@org.springframework.stereotype.Component("GeoDocumentDao")
public class GeoDocumentDaoImpl extends GenericDaoImpl<GeoDocument> implements IGeoDocumentDao {

    public GeoDocumentDaoImpl(){}

    public GeoDocumentDaoImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

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
        File file = ResourcesKit.getResourceAsFile(filePathXml);
        if(file.exists()){
            try {
                context = new ClassPathXmlApplicationContext(filePathXml);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        GeoDocumentDaoImpl g = context.getBean(GeoDocumentDaoImpl.class);
    }

    @Override
    public void loadHibernateConfig(String filePathXml) {
        //ApplicationContext context = new ClassPathXmlApplicationContext(filePathXml);
        File file = ResourcesKit.getResourceAsFile(filePathXml);
        if(file.exists()){
            try {
                context = new ClassPathXmlApplicationContext(filePathXml);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        GeoDocumentDaoImpl g = (GeoDocumentDaoImpl) context.getBean("GeoDocumentDao");
    }

    @Override
    public void setTableSelect(String nameOfTable){this.mySelectTable = nameOfTable;}

    @Override
    public void setTableInsert(String nameOfTable){
        this.myInsertTable = nameOfTable;
    }
/*
    public void setSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addAnnotatedClasses(GeoDocument.class);
        this.sessionFactory = sessionBuilder.buildSessionFactory();
    }
*/
    /*
    public void setSessionFactory() {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addAnnotatedClasses(GeoDocument.class);
        this.sessionFactory = sessionBuilder.buildSessionFactory();
    }
    */
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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
        if(erase==true){
            query ="DROP TABLE IF EXISTS "+myInsertTable+";";
            jdbcTemplate.execute(query);
        }
        create();
    }

    @Override
    public boolean verifyDuplicate(String columnWhereName, String valueWhereName) {
        return super.verifyDuplicate(columnWhereName,valueWhereName);
    }


    @Override
    public void insertAndTrim(GeoDocument g) {
        String query =
                "INSERT INTO "+myInsertTable+" "
                        + "(url, regione, provincia, city, indirizzo, iva, email, telefono, fax," +
                        " edificio, latitude,longitude,nazione,description,postalCode,indirizzoNoCAP," +
                        "indirizzoHasNumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        SystemLog.message(query);
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
                jdbcTemplate.execute(query);
            }
        }catch(Exception e){}
    }
    /*
    private List<String> getListColumnSDAO(String builder) {
        String query;
        //Method 1
        List<String> columns = new ArrayList<>();
        List rsmdList = (List) jdbcTemplate.query(builder.toString(), new ResultSetExtractor() {
            @Override
            public ResultSetMetaData extractData(ResultSet rs) throws SQLException, DataAccessException {
                ResultSetMetaData rsmd = rs.getMetaData();
                return rsmd;
            }
        });
        ResultSetMetaData rsMetaData = (ResultSetMetaData) rsmdList.get(0);

        int numberOfColumns = 0;
        try {
            numberOfColumns = rsMetaData.getColumnCount();
            // get the column names; column indexes start from 1
            for (int i = 1; i < numberOfColumns + 1; i++) {
                columns.add(rsMetaData.getColumnName(i));
                query = "UPDATE `" + myTable + "` SET `" + rsMetaData.getColumnName(i) + "` = LTRIM(RTRIM(`" + rsMetaData.getColumnName(i) + "`));";
                jdbcTemplate.execute(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }
    */


    ///////////////////////
    //HIBERNATE
    //////////////////////

    //method to save
    @Override
    public void saveH(GeoDocument g ){
        hibernateTemplate.save(g);
    }
    //method to update
    @Override
    public void updateH(GeoDocument g){
        hibernateTemplate.update(g);
    }
    //method to delete
    @Override
    public void deleteH(GeoDocument g){
        hibernateTemplate.delete(g);
    }
    //method to return one of given id
    @Override
    public GeoDocument  getHByColumn(String column){
        GeoDocument g = hibernateTemplate.get(GeoDocument.class,column);
        return g;
    }
    //method to return all
    @Override
    @Transactional
    public List<GeoDocument> getAllH(){
        @SuppressWarnings("unchecked")
        /*
        List<GeoDocument> list = new ArrayList<GeoDocument>();
        list = hibernateTemplate.loadAll(GeoDocument.class);
        */
        List<GeoDocument> list = (List<GeoDocument>) sessionFactory.getCurrentSession()
        .createCriteria(GeoDocument.class)
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

        return list;
    }


        /*
        List columns = new ArrayList();
        jdbcTemplate.query(builder.toString(),new ResultSetExtractor<Object>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for(int i = 1 ; i <= columnCount ; i++){

                    SQLColumn column = new SQLColumn();
                    column.setName(rsmd.getColumnName(i));
                    column.setAutoIncrement(rsmd.isAutoIncrement(i));
                    column.setType(rsmd.getColumnTypeName(i));
                    column.setTypeCode(rsmd.getColumnType(i));
                    column.setTableName(sqlTable.getName().toUpperCase());
                    columns.add(column);

                }
                return columnCount;
            }
        });
        return columns;
                */

}
