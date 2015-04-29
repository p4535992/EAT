package object.impl.jdbc;

import object.dao.generic.IGenericDao;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import spring.bean.BeansKit;
import p4535992.util.log.SystemLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 16/04/2015.
 */
public abstract class GenericDaoImpl<T> implements IGenericDao<T> {
    /** {@code org.slf4j.Logger} */
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenericDaoImpl.class);
    protected DriverManagerDataSource driverManag;
    protected JdbcTemplate jdbcTemplate;
    protected String myInsertTable,mySelectTable,myUpdateTable;
    protected DataSource dataSource;
    protected SessionFactory sessionFactory;
    @PersistenceContext
    protected EntityManager em;
    protected Class<T> cl;
    protected String clName;
    protected String query;
    protected ApplicationContext context;

    public GenericDaoImpl() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    @Override
    public void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database) {
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.sql.jdbc.Driver"
        driverManag.setUrl("" + typeDb + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        driverManag.setUsername(user);
        driverManag.setPassword(pass);
        this.dataSource = driverManag;
        setNewJdbcTemplate();
    }

    @Override
    public void setNewJdbcTemplate() {
        this.jdbcTemplate = new JdbcTemplate();
        this.jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate();
        this.jdbcTemplate.setDataSource(dataSource);
    }

    @Override
    public void loadSpringConfig(String filePathXml) throws IOException {
        context = BeansKit.tryGetContextSpring(filePathXml);
    }

    @Override
    public void loadSpringConfig(String[] filesPathsXml) throws IOException {
       context = BeansKit.tryGetContextSpring(filesPathsXml);
    }



    @Override
    public DataSource getDataSource() {
        return dataSource;
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
    public void setTableUpdate(String nameOfTable) {
        this.myUpdateTable = nameOfTable;
    }


    @Override
    public void create(String SQL) throws Exception {
        //Copy the geodocument table
        try {
            query = SQL;
            SystemLog.message(query);

        }catch(Exception e){
            if(!e.getMessage().contains("already exists")){
                SystemLog.logStackTrace(e, logger);
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
        boolean b = false;
        try {
            query = "SELECT count(*) FROM " + myInsertTable + " WHERE " + columnWhereName + "='" + valueWhereName.replace("'", "''") + "'";
            int c = this.jdbcTemplate.queryForObject(query, Integer.class);
            if (c > 0) {
                b = true;
            }
            SystemLog.query(query+" -> "+b);
        }catch(org.springframework.jdbc.BadSqlGrammarException e){
            SystemLog.error(query);
            b = true;
        }

        return b;
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from " + myInsertTable + "", Integer.class);
    }


    //ENTITY MANAGER METHOD

    @Override
    public void insert(final T object) {this.em.persist(object); }

    @Override
    public void delete(final Object id) {
        this.em.remove(this.em.getReference(cl, id));
    }

    @Override
    public void delete(String whereColumn, String whereValue) {
        jdbcTemplate.update("DELETE from " + mySelectTable + " where " + whereColumn + "= ? ",
                new Object[]{whereValue});
    }

    @Override
    public T find(final Object id) {
        return (T) this.em.find(cl, id);
    }

    @Override
    public T update(final T t) {
        return this.em.merge(t);
    }

    @Override
    public void update(String[] columns,Object[] values,String column_where,String value_where){
        try {
            Object[] newValues = new Object[values.length+1];
            query = "UPDATE " + myUpdateTable + " SET ";
            int f = 0;
            for (int k = 0; k < columns.length; k++) {
                query += columns[k] + "=? ";
                if(values[k] == null) {
                    newValues[f] = "NULL"; f++;
                }
                else{
                    newValues[f] = values[k];f++;
                }
                if (k < columns.length - 1) {
                    query += ", ";
                }
            }
            query += " WHERE " + column_where + "=?";
            newValues[f] = column_where;
            jdbcTemplate.update(query, newValues);
            SystemLog.query(query);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            SystemLog.warning(e.getMessage());
        }
    }


    @Override
    public long countAll(final Map<String, Object> params) {
        final StringBuffer queryString = new StringBuffer(
                "SELECT count(o) from ");
        queryString.append(clName).append(" o ");
        //queryString.append(this.getQueryClauses(params, null));
        final javax.persistence.Query query = this.em.createQuery(queryString.toString());
        return (Long) query.getSingleResult();
    }


    @Override
    public void deleteAll() {
            jdbcTemplate.update("DELETE from "+myInsertTable+"");
    }

    @Override
    public Object select(String column, String column_where, String value_where,Class<? extends Object> aClass){
        Object result = null;
        try {
            query = "SELECT " + column + " from " + mySelectTable + " WHERE " + column_where + " = ? LIMIT 1";
            result =  jdbcTemplate.queryForObject(query, new Object[]{value_where},aClass);
        }catch(org.springframework.dao.EmptyResultDataAccessException e){
            SystemLog.error(query + " ->" + e.getMessage());
            return null;
        }catch(java.lang.NullPointerException ex){
            SystemLog.error(query + " ->" + ex.getMessage());
            return null;
        }
        SystemLog.query(query + " -> " + result);
        //String name = (String)getJdbcTemplate().queryForObject(query, new Object[] { custId }, String.class);
        return result;
    }

    @Override
    public void insertAndTrim(String[] columns,Object[] params,int[] types) {
        query =  "INSERT INTO "+myInsertTable+"  (";
        for(int i = 0; i <  columns.length; i++){
            query += columns[i];
            if(i < columns.length-1){
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
                SystemLog.message("SQL:" + query);
                jdbcTemplate.execute(query);
            }
        }catch(Exception e){}
    }

    @Override
    public void insert(String[] columns,Object[] params,int[] types) {
        query =  "INSERT INTO "+myInsertTable+"  (";
        for(int i = 0; i <  columns.length; i++){
            query += columns[i];
            if(i < columns.length-1){
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

        jdbcTemplate.update(query, params, types);
    }


    @Override
    public void tryInsert(T object) {

    }

    @Override
      public List select(int limit,int offset){
        List<Object> list = new ArrayList<>();
        query = "SELECT * FROM "+mySelectTable+" LIMIT 1 OFFSET 0";
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
        query += " FROM "+mySelectTable+" LIMIT "+limit+" OFFSET "+offset+"";
        return list;
    }

    @Override
    public List select(String[] columns_where,Object[] values_where,int limit,int offset,String condition){
        List<Object> list = new ArrayList<>();
        query = "SELECT * FROM "+mySelectTable+" WHERE ";
        for(int k=0; k < columns_where.length; k++ ){
            query+= columns_where[k] +" = "+ values_where[k];
            if(k < columns_where.length -1){ query += " "+condition.toUpperCase()+" ";}
        }
        query += " LIMIT "+limit+" OFFSET "+offset+"";
        return list;
    }

    @Override
    public List select(String query) {
        return this.jdbcTemplate.query(query,
            new RowMapper<T>() {
                @Override
                public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                    final T w = null;
                    ResultSetExtractor extractor = new ResultSetExtractor() {
                        @Override
                        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
//                                w.setId(rs.getString("id"));
//                                w.setCity(rs.getString("city"));
//                                w.setUrl(rs.getString("url"));
                            return w;
                        }
                    };
                    return w;
                }
            }
        );
    }

    @Override
    public List select(final String column, int limit,int offset) {
        List <Object> list = new ArrayList<>();
        query = "select " + column + " from " + mySelectTable + " LIMIT " + limit + " OFFSET " + offset + "";
        try {
            list = this.jdbcTemplate.query(query,
                    new RowMapper<Object>() {
                        @Override
                        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getObject(column);
                        }
                    }
            );
            if(list.isEmpty()){SystemLog.warning("The result list of:"+query+" is empty!!");}
            else{SystemLog.query(query + " -> retur a list with size:"+list.size());}
        }catch(Exception e){
           SystemLog.exception(e);
        }
        return list;
    }

    @Override
    public List select(String column, String datatype, int limit, int offset) {

        return null;
    }

    @Override
    public String[] getColumnsInsertTable(){
        query = "SELECT * FROM "+myInsertTable+" LIMIT 1";
        String[] columns =  new String[]{};
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement p = connection.prepareStatement(query);
            ResultSet rs = p.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            // get the column names; column indexes start from 1
            for (int i = 1; i < numberOfColumns + 1; i++) {
                columns[i] = rsMetaData.getColumnName(i);
            }
        }catch(Exception e){}
        return columns;
    }







}

//    class GenericResultSetExtractor<T> implements ResultSetExtractor {
//
//        @Override
//        public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
//            T t =null;
//            //SETTTER OF T
//            return t;
//        }
//    }
//
//
//    class GenericRowMapper implements RowMapper {
//        @Override
//        public Object mapRow(ResultSet rs, int line) throws SQLException {
//            GenericResultSetExtractor extractor = new GenericResultSetExtractor();
//            return extractor.extractData(rs);
//        }
//    }

//    public void insertBatchNamedParameter2(final List<Customer> customers){
//
//        SqlParameterSource[] params =
//                SqlParameterSourceUtils.createBatch(customers.toArray());
//        jdbcTemplate.update(
//                "INSERT INTO CUSTOMER (CUST_ID, NAME, AGE) VALUES (:custId, :name, :age)",
//                params);
//
//    }


