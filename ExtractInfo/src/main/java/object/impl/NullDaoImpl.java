package object.impl;

import object.dao.NullDao;
import object.model.GeoDocument;
import object.model.Website;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import util.SystemLog;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 16/04/2015.
 */
public class NullDaoImpl<T> implements NullDao {
    /** {@code org.slf4j.Logger} */
    //private static org.slf4j.Logger logger;
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NullDaoImpl.class);
    private DriverManagerDataSource driverManag;
    private JdbcTemplate jdbcTemplate;
    private String myInsertTable;
    private String mySelectTable;
    private DataSource dataSource;
    private HibernateTemplate hibernateTemplate;
    private SessionFactory sessionFactory;
    private ApplicationContext contextClassPath;


    @Override
    public void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database) {
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
    public void setTable(String nameOfTable) {
        this.myInsertTable = nameOfTable;
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate();
        this.jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public void loadSpringConfig(String filePathXml, Class cl) {
        contextClassPath = new ClassPathXmlApplicationContext(filePathXml);
        Object classObject = contextClassPath.getBean(cl);
    }

    @Override
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override
    public void setHibernateTemplate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new HibernateTemplate(getSessionFactory());
    }

    @Override
    public void loadHibernateConfig(String filePathXml,Class cl) {

    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void setTableInsert(String nameOfTable) {
        this.myInsertTable = nameOfTable;
    }

    @Override
    public void setTableSelect(String nameOfTable) {
        this.mySelectTable = nameOfTable;
    }

    @Override
    public void create(String SQL) throws Exception {
        //Copy the geodocument table
        String query;
        try {
            query = SQL;
            SystemLog.message(query);

        }catch(Exception e){
            if(!e.getMessage().contains("already exists")){
                SystemLog.logStackTrace(e,logger);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void create(String SQL, boolean erase) throws Exception {
        if(myInsertTable.isEmpty()) {
            throw new Exception("Name of the table is empty!!!");
        }
        String query;
        create(SQL);
    }

    @Override
    public boolean verifyDuplicate(String columnWhereName, String valueWhereName) {
        int c = this.jdbcTemplate.queryForObject(
                "select count(*) from "+myInsertTable+" where "+columnWhereName+"='"+valueWhereName+"'", Integer.class);
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
    public Object selectValueForSpecificColumn(String column, String column_where, String value_where) {
        return null;
    }

    @Override
    public void insertAndTrim(Object[] params,int[] types) {
        String query =  "INSERT INTO "+myInsertTable+"  (";
        for(int i = 0; i <  params.length; i++){
            query += params[i];
            if(i < params.length-1){
                query+= ",";
            }
        }
        query +=" ) VALUES ( ";
        for(int i = 0; i <  params.length; i++){
            query += "?";
            if(i < params.length-1){
                query+= ",";
            }
        }
        query += ");";
        // execute insert query to insert the data
        // return number of row / rows processed by the executed query
        jdbcTemplate.update(query, params, types);
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet rs = p.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            // get the column names; column indexes start from 1
            for (int i = 1; i < numberOfColumns + 1; i++) {
                query = "UPDATE `" + myInsertTable + "` SET `" + rsMetaData.getColumnName(i) + "` = LTRIM(RTRIM(`" + rsMetaData.getColumnName(i) + "`));";
                SystemLog.message("SQL:"+query);
                jdbcTemplate.execute(query);
            }
        }catch(Exception e){}
    }

    @Override
    public List selectAll(String column) {
        return this.jdbcTemplate.query("select FIRSTNAME, LASTNAME from PERSON",
                new RowMapper<Website>() {
                    @Override
                    public Website mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final Website w = new Website();
                        ResultSetExtractor extractor = new ResultSetExtractor() {

                            @Override
                            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                                w.setId(rs.getString("id"));
                                w.setCity(rs.getString("city"));
                                w.setUrl(rs.getString("url"));
                                return w;
                            }
                        };
                        return w;
                    }
                }
        );
    }

    @Override
    public List select(final String column, String limit, String offset) {
        return this.jdbcTemplate.query("select "+column+" from " + myInsertTable + " LIMIT " + limit + " OFFSET " + offset + "",
                new RowMapper<Object>() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getObject(column);
                    }
                }
        );
    }


    @Override
    public List select(String column, String datatype, String limit, String offset) {
        return null;
    }

    @Override
    public void saveSimpleH(Object object) {
        hibernateTemplate.save(object);
    }

    @Override
    public void updateSimpleH(Object object) {
        hibernateTemplate.update(object);
    }

    @Override
    public void deleteSimpleH(Object object) {
        hibernateTemplate.delete(object);
    }

    @Override
    public Object getHByColumn(String column,Class cl) {
        Object g = hibernateTemplate.get(cl,column);
        return g;
    }

    @Override
    public List selectAllSimpleH(Class cl) {
        List<Object> list = new ArrayList<>();
        list = hibernateTemplate.loadAll(cl);
        return list;
    }

    @Override
    public List selectAllSimpleH(final String limit,final String offset) {
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

    @Override
    public List<Map> getAll() {
        return null;
    }

    @Override
    public Number getAutoGeneratedKey(String idColumn) {
        return null;
    }
}
