package object.impl.jdbc.generic;

import object.dao.jdbc.generic.IGenericDao;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import p4535992.util.reflection.ReflectionKit;
import p4535992.util.sql.SQLKit;
import p4535992.util.sql.SQLSupport;
import p4535992.util.string.StringKit;
import bean.BeansKit;
import p4535992.util.log.SystemLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 16/04/2015.
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
    public void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database) {
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.sql.jdbc.Driver"
        driverManag.setUrl("" + dialectDB + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
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
        try {
            SQLSupport support = SQLKit.insertSupport(object);
            String[] columns = support.getCOLUMNS();
            Object[] params = support.getVALUES();
            int[] types = support.getTYPES();
            insert(columns,params,types);
        }catch(IllegalAccessException | NoSuchMethodException |InvocationTargetException | NoSuchFieldException ne){
            SystemLog.exception(ne);
        }
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
    public String prepareSelectQuery(String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition){
        //PREPARE THE QUERY STRING
        query = "SELECT * FROM "+mySelectTable+" WHERE ";
        for(int k=0; k < columns_where.length; k++ ){
            query+= columns_where[k] +" ";
            if(values_where[k]== null){ query += " IS NULL ";}
            else{query += " = '" + values_where[k]+"'";}
            if(condition!=null && k < columns_where.length -1){ query += " "+condition.toUpperCase()+" ";}
            else{query += " ";}
        }
        if(limit != null && offset!= null) {
            query += " LIMIT " + limit + " OFFSET " + offset + "";
        }
        return query;
    }

    @Override
    public List trySelect(String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition) {
        List<T> list = new ArrayList<>();
        //PREPARE THE QUERY STRING
        query = prepareSelectQuery(columns_where,values_where,limit,offset,condition);
        List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
        SystemLog.query(query);
        try {
            int i = 0;
            Class[] classes = ReflectionKit.getClassesByFieldsByAnnotation(cl,javax.persistence.Column.class);
            for (Map<String, Object> geoDoc : map) {
                //INVOKE NEW DEFAULT CONSTRUCTOR
                T iClass =  ReflectionKit.invokeConstructor(cl);
                for (Iterator<Map.Entry<String, Object>> it = geoDoc.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    Object value = entry.getValue();
                    if (value == null ||value.toString()==""){ value = null;}
                    else {
                        if (classes[i].getName() == String.class.getName()) {
                            value = value.toString();
                        } else if (classes[i].getName() == URL.class.getName()) {
                            if (value.toString().contains("://")) {
                                value = new URL(value.toString());
                            } else {
                                value = new URL("http://" + value.toString());
                            }
                        } else if (classes[i].getName() == Double.class.getName()) {
                            value = Double.parseDouble(value.toString());
                        } else if (classes[i].getName() == Integer.class.getName()) {
                            value = Integer.parseInt(value.toString());
                        } else if (classes[i].getName() == Float.class.getName()){
                            value = Float.parseFloat(value.toString());
                        }
                        iClass = (T) SQLKit.invokeSetterSupport(iClass, entry.getKey(), value);
                    }
                    i++;
                }
                list.add(iClass);
            }
        }catch(Exception e){
            SystemLog.exception(e);
        }
        return list;
    }

    private T supportObject2;
    @Override
    public List trySelect(String query, final T MyObject) {
        List<T> list = new ArrayList<>();
        try {
            //T MyObject = ReflectionKit.invokeConstructor(cl);
            list = this.jdbcTemplate.query(query,
                    new RowMapper<T>() {
                        @Override
                        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                            T MyObject =ReflectionKit.invokeConstructor(cl);
                            ResultSetExtractor extractor = new ResultSetExtractor() {
                                @Override
                                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                                    //supportObject2 = ReflectionKit.invokeConstructor(cl);
                                    //supportObject2 = ReflectionKit.invokeSetterMethod(supportObject2, rs);
                                    //list.add(supportObject2);
                                    T MyObject2 = ReflectionKit.invokeConstructor(cl);
                                    while (rs.next()) {
                                        int size = rs.getFetchSize();
                                        for (Field field : cl.getDeclaredFields()){
                                            for (Method method : ReflectionKit.getSettersClass(cl)){
                                                //for (Method method : MyObject.getClass().getMethods())
                                                //if (ReflectionKit.isSetter(method)) {
//                                                    if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())
//                                                            && method.getName().toLowerCase().startsWith("set"))
//                                                    {

                                                // MZ: Method found, run it
                                                try
                                                {
                                                    method.setAccessible(true);
                                                    if(field.getType().getSimpleName().toLowerCase().endsWith("integer"))
                                                        method.invoke(MyObject2,rs.getInt(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("long"))
                                                        method.invoke(MyObject2,rs.getLong(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("string"))
                                                        method.invoke(MyObject2,rs.getString(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("boolean"))
                                                        method.invoke(MyObject2,rs.getBoolean(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("timestamp"))
                                                        method.invoke(MyObject2,rs.getTimestamp(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("date"))
                                                        method.invoke(MyObject2,rs.getDate(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("double"))
                                                        method.invoke(MyObject2,rs.getDouble(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("float"))
                                                        method.invoke(MyObject2,rs.getFloat(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("time"))
                                                        method.invoke(MyObject2,rs.getTime(field.getName().toLowerCase()));
                                                    else if(field.getType().getSimpleName().toLowerCase().endsWith("url"))
                                                        method.invoke(MyObject2,rs.getURL(field.getName().toLowerCase()));
                                                    else
                                                        method.invoke(MyObject2,rs.getObject(field.getName().toLowerCase()));
                                                }
                                                catch (IllegalAccessException | InvocationTargetException | SQLException e)
                                                {
                                                    System.err.println(e.getMessage());
                                                }
                                                    //}
                                                //}
                                            }
                                        }
                                    }
                                    return MyObject2;
                                }
                            };
                            return MyObject;
                        }

                    }
            );
//            T MyObject = ReflectionKit.invokeConstructor(cl);
//            GenericRowMapper<T> rowMapper = new GenericRowMapper(MyObject);
//            this.jdbcTemplate.query(query,rowMapper);



        }catch (Exception e){
            SystemLog.exception(e);
        }finally{
            if(list.isEmpty()){SystemLog.warning("The result list of:"+query+" is empty!!");}
            else{SystemLog.query(query + " -> return a list with size:"+list.size());}
        }
        return list;
    }

    @Override
    public List trySelect(final String column, int limit, int offset) {
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

    class GenericResultSetExtractor<T> implements ResultSetExtractor {

        public T MyObject;
        GenericResultSetExtractor(T MyObject){
            this.MyObject=MyObject;
        }

        @Override
        public T extractData(ResultSet rs) throws SQLException, DataAccessException {
            //SETTTER OF T
            return ReflectionKit.invokeSetterMethod(MyObject, rs);
        }
    }

    class GenericRowMapper<T> implements RowMapper {

        public T MyObject;
        GenericRowMapper(T MyObject){
            this.MyObject = MyObject;
        }

        @Override
        public Object mapRow(ResultSet rs, int line) throws SQLException {
            GenericResultSetExtractor extractor = new GenericResultSetExtractor(MyObject);
            return extractor.extractData(rs);
        }
    }

//    public void insertBatchNamedParameter2(final List<Customer> customers){
//
//        SqlParameterSource[] params =
//                SqlParameterSourceUtils.createBatch(customers.toArray());
//        jdbcTemplate.update(
//                "INSERT INTO CUSTOMER (CUST_ID, NAME, AGE) VALUES (:custId, :name, :age)",
//                params);
//
//    }


