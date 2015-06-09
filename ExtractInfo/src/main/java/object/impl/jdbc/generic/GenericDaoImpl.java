package object.impl.jdbc.generic;

import extractor.hibernate.interceptor.GeoDocumentInterceptor;
import object.dao.jdbc.generic.IGenericDao;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import p4535992.util.reflection.ReflectionKit;
import p4535992.util.sql.SQLSupport;
import bean.BeansKit;
import p4535992.util.log.SystemLog;
import p4535992.util.string.StringKit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;

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
    public void create(String SQL){
        try {
            query = SQL;
            jdbcTemplate.execute(query);
            SystemLog.message(query);
        }catch(Exception e){
            if(!e.getMessage().contains("Table already exists")){
                SystemLog.exception(e);
            }
        }
    }

    @Override
    public void create(String SQL, boolean erase) throws Exception {
        if(myInsertTable.isEmpty()) {
            throw new Exception("Name of the table is empty!!!");
        }
        String query;
        if(erase) {
            query = "DROP TABLE IF EXISTS "+myInsertTable+";";
            jdbcTemplate.execute(query);
            SystemLog.query(query);
        }
        create(SQL);
    }

    @Override
    public boolean verifyDuplicate(String column_where, String value_where) {
        boolean b = false;
        try {
            query = "SELECT count(*) FROM " + myInsertTable + " WHERE " + column_where + "='" + value_where.replace("'", "''") + "'";
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
        //queryString.append(this.getQueryClauses(values, null));
        final javax.persistence.Query query = this.em.createQuery(queryString.toString());
        return (Long) query.getSingleResult();
    }


    @Override
    public void deleteAll() {
            jdbcTemplate.update("DELETE from " + myInsertTable + "");
    }

    @Override
    public Object select(String column, String column_where, Object value_where){
        Object result;
        try {
            query = prepareSelectQuery(new String[]{column},new String[]{column_where},null,null,null,null);
            //query = "SELECT " + column + " FROM " + mySelectTable + " WHERE " + column_where + " = ? LIMIT 1";
            result =  jdbcTemplate.queryForObject(query, new Object[]{value_where},value_where.getClass());
        }catch(org.springframework.dao.EmptyResultDataAccessException e){
            SystemLog.warning(query + " ->" + e.getMessage());
            SystemLog.warning("Attention probably the SQL result is empty!");
            return null;
        }catch(java.lang.NullPointerException e){
            SystemLog.warning(query + " ->" + e.getMessage());
            return null;
        }catch(org.springframework.jdbc.CannotGetJdbcConnectionException e){
            SystemLog.warning(query + " ->" + e.getMessage());
            SystemLog.warning("Attention probably the database not exists!");
            return null;
        }
        SystemLog.query(query + " -> " + result);
        return result;
    }

    @Override
    public void insertAndTrim(String[] columns,Object[] values,int[] types) {
        insert(columns, values, types);
        for(int i= 0; i < columns.length; i++){
            trim(columns[i]);
        }
    }

    @Override
    public void trim(String column){
        query = "UPDATE `" + myInsertTable + "` SET `" + column + "` = LTRIM(RTRIM(`" + column + "`));";
        SystemLog.message("SQL:" + query);
        jdbcTemplate.execute(query);
    }

    @Override
    public void insert(String[] columns,Object[] values,int[] types) {
        try {
            query = prepareInsertIntoQuery(columns, null);
            jdbcTemplate.update(query, values, types);
            //query = prepareInsertIntoQuery(columns, values);
            //jdbcTemplate.update(query);
            SystemLog.query(prepareInsertIntoQuery(columns, values));
        }catch(org.springframework.dao.TransientDataAccessResourceException e){
            SystemLog.warning("Attention: probably there is some java.sql.Type not supported" +
                    " from your database");
            SystemLog.exception(e);
        }
    }

    @Override
    public void tryInsert(T object) {
        SQLSupport support = new SQLSupport(object);
        String[] columns = support.getCOLUMNS();
        Object[] params = support.getVALUES();
        int[] types = support.getTYPES();
        insert(columns,params,types);
    }

//    @Override
//      public List trySelect(int limit, int offset){
//        List<Object> list = new ArrayList<>();
//        query = "SELECT * FROM "+mySelectTable+" LIMIT 1 OFFSET 0";
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//            PreparedStatement p = connection.prepareStatement(query);
//            ResultSet rs = p.executeQuery();
//            ResultSetMetaData rsMetaData = rs.getMetaData();
//            int numberOfColumns = rsMetaData.getColumnCount();
//            query = "SELECT ";
//            // get the column names; column indexes start from 1
//            for (int i = 1; i < numberOfColumns + 1; i++) {
//                query += rsMetaData.getColumnName(i);
//                if(i < numberOfColumns){query += " ,";}
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        query += " FROM "+mySelectTable+" LIMIT "+limit+" OFFSET "+offset+"";
//        return list;
//    }

    @Override
    public String prepareInsertIntoQuery(String[] columns,Object[] values){
        try {
            boolean statement = false;
            query = "INSERT INTO " + myInsertTable + "  (";
            for (int i = 0; i < columns.length; i++) {
                query += columns[i];
                if (i < columns.length - 1) {
                    query += ",";
                }
            }
            query += " ) VALUES ( ";
            if (values == null) {
                statement = true;
            }
            for (int i = 0; i < columns.length; i++) {
                if (statement) {
                    query += "?";
                } else {
                    if(values[i]== null){
                        values[i]= " NULL ";
                    }else if (values[i]!=null && values[i] instanceof String) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else if (values[i]!=null && values[i] instanceof java.net.URL) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else{
                        values[i] = " " + values[i] + " ";
                    }
                    query += "" + values[i] + "";
                }
                if (i < columns.length - 1) {
                    query += ",";
                }
            }
            query += ");";
        }catch (NullPointerException e){
            SystemLog.warning("Attention: you problably have forgotten  to put some column for the SQL query");
            SystemLog.exception(e);
        }
        return query;
    }

    @Override
    public String prepareSelectQuery(String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition){
        boolean statement = false;
        //PREPARE THE QUERY STRING
        query = "SELECT ";
        if(columns.length==0 || (columns.length==1 && columns[0]=="*")){
             query += " * ";
        }else{
            for(int i = 0; i < columns.length; i++){
                query += " "+columns[i]+"";
                if(i < columns.length-1){
                    query += ", ";
                }
            }
        }
        query +=" FROM "+mySelectTable+" ";
        if(!StringKit.isArrayEmpty(columns_where)) {
            if(values_where==null){
                statement = true;
                //values_where = new Object[columns_where.length];
                //for(int i = 0; i < columns_where.length; i++){values_where[i]="?";}
            }
            query += " WHERE ";
            for (int k = 0; k < columns_where.length; k++) {
                query += columns_where[k] + " ";
                if(statement){
                    query += " = ? ";
                }else {
                    if (values_where[k] == null) {
                        query += " IS NULL ";
                    } else {
                        query += " = '" + values_where[k] + "'";
                    }
                }
                if (condition != null && k < columns_where.length - 1) {
                    query += " " + condition.toUpperCase() + " ";
                } else {
                    query += " ";
                }
            }
        }
        if(limit != null && offset!= null) {
            query += " LIMIT " + limit + " OFFSET " + offset + "";
        }
        return query;
    }

    @Override
    public List<T> trySelect(final String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition) {
        List<T> list = new ArrayList<>();
        query = prepareSelectQuery(columns,columns_where,values_where,limit,offset,condition);
        List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
        SystemLog.query(query);
        try {
            int i = 0;
            Class[] classes = ReflectionKit.getClassesByFieldsByAnnotation(cl,javax.persistence.Column.class);
            for (Map<String, Object> geoDoc : map) {
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
                        iClass = SQLSupport.invokeSetterSupport(iClass, entry.getKey(), value);
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

    @Override
    public List<T> trySelectWithRowMap(String[] columns,String[] columns_where,Object[] values_where,Integer limit, Integer offset,String condition) {
        query = prepareSelectQuery(columns,columns_where,values_where,limit,offset,condition);
        List<T> list = new ArrayList<>();
        try {
            final String[] columns2;
            if(columns.length==1 && Arrays.asList(columns).contains("*")) {
                columns2 = SQLSupport.getArrayColumns(cl, javax.persistence.Column.class, "name");
//            final Integer[] types =
//                    SQLKit.getArrayTypes(cl, javax.persistence.Column.class);
            }else{
                columns2 = StringKit.copyContentArray(columns);
            }

            final Class[] classes =
                    SQLSupport.getArrayClassesTypes(cl, javax.persistence.Column.class);

            final List<Method> setters = ReflectionKit.getSettersClassOrder(cl);

            list = this.jdbcTemplate.query(
                    query, new RowMapper<T>() {
                        @Override
                        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                            T MyObject2 = ReflectionKit.invokeConstructor(cl);
                            try {
                                for (int i = 0; i < columns2.length; i++) {
                                    System.out.println(i+")Class:"+classes[i].getName()+",Column:"+columns2[i]);
                                    if (classes[i].getName()
                                            .equalsIgnoreCase(String.class.getName())) {
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{rs.getString(columns2[i])});
                                    }
                                    else if (classes[i].getName()
                                            .equalsIgnoreCase(URL.class.getName())) {
                                        URL url;
                                        if(rs.getString(columns2[i]).contains("://")){
                                            url = new URL(rs.getString(columns2[i]));
                                        }else{
                                            url = new URL("http://"+rs.getString(columns2[i]));
                                        }
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{url});

                                    }
                                    else if (classes[i].getName()
                                            .equalsIgnoreCase(Double.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",",".").replace(" ",".");
                                        Double num = Double.parseDouble(sup);
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{num});

                                    }else if (classes[i].getName()
                                            .equalsIgnoreCase(Integer.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        Integer num = Integer.parseInt(sup);
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{num});
                                    }

                                }
                            }catch(IllegalAccessException|InvocationTargetException|NoSuchMethodException e){
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            return MyObject2;
                        }
                    }
            );
        }catch (Exception e){
            SystemLog.exception(e);
        }finally{
            if(list.isEmpty()){SystemLog.warning("The result list of:"+query+" is empty!!");}
            else{SystemLog.query(query + " -> return a list with size:"+list.size());}
        }
        return list;
    }

    @Override
    public List<T> trySelectWithResultSetExtractor(String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition){
        List<T> list = new ArrayList<>();
        query = prepareSelectQuery(columns,columns_where,values_where,limit,offset,condition);
        try {
            //T MyObject = ReflectionKit.invokeConstructor(cl);
            final String[] columns2;
            if(columns.length==1 && Arrays.asList(columns).contains("*")) {
              columns2 = SQLSupport.getArrayColumns(cl, javax.persistence.Column.class, "name");
//            final Integer[] types =
//                    SQLKit.getArrayTypes(cl, javax.persistence.Column.class);
            }else{
                columns2 = StringKit.copyContentArray(columns);
            }

            final Class[] classes =
                    SQLSupport.getArrayClassesTypes(cl, javax.persistence.Column.class);

            final List<Method> setters = ReflectionKit.getSettersClassOrder(cl);

            list = (List<T>) jdbcTemplate.query(query,new ResultSetExtractor() {
                @Override
                public List<T> extractData(ResultSet rs) throws SQLException {
                    List<T> list = new ArrayList<>();
                    while(rs.next()){
                        T MyObject2 = ReflectionKit.invokeConstructor(cl);
                        Method method = null;
                        try {
                            for (int i = 0; i < columns2.length; i++) {
                                method = setters.get(i); //..support for exception

                                System.out.println(i + ")Class:" + classes[i].getName() + ",Column:" + columns2[i]);
                                //String[] column2 = new String[rs.getMetaData().getColumnCount()];
                                //for(int j = 0; j < rs.getMetaData().getColumnCount(); j++){column2[j] = rs.getMetaData().getColumnName(j);}
                                try {
                                    if (classes[i].getName()
                                            .equalsIgnoreCase(String.class.getName())) {
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{rs.getString(columns2[i])});
                                        //map.put(columns[i], rs.getString(columns[i]));
                                    } else if (classes[i].getName()
                                            .equalsIgnoreCase(URL.class.getName())) {
                                        URL url;
                                        if (rs.getString(columns2[i]).contains("://")) {
                                            url = new URL(rs.getString(columns2[i]));
                                        } else {
                                            url = new URL("http://" + rs.getString(columns2[i]));
                                        }
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{url});
                                        //map.put(columns[i], url);
                                    } else if (classes[i].getName()
                                            .equalsIgnoreCase(Double.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", ".").replace(" ", ".");
                                        Double num = Double.parseDouble(sup);
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    }else if (classes[i].getName()
                                            .equalsIgnoreCase(Integer.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        Integer num = Integer.parseInt(sup);
                                        MyObject2 = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject2, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    }
                                } catch (org.springframework.jdbc.UncategorizedSQLException e) {
                                    SystemLog.warning("... try and failed to get a value of a column not specify  in the query");
                                    MyObject2 = ReflectionKit.invokeSetterMethodForObject(MyObject2, method, new Object[]{null});
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        list.add(MyObject2);
                    }
                    return list;
                }

            });
        }catch (Exception e){
            SystemLog.exception(e);
        }finally{
            if(list.isEmpty()){SystemLog.warning("The result list of:"+query+" is empty!!");}
            else{SystemLog.query(query + " -> return a list with size:"+list.size());}
        }
        return list;
    }

    @Override
    public List<Object> select(String column,String column_where,Object value_where,Integer limit,Integer offset,String condition){
        List<Object> listObj = new ArrayList<>();
        query = prepareSelectQuery(new String[]{column},new String[]{column_where},null,limit,offset,condition);
        List<Map<String, Object>> list;
        if(value_where != null) {
            Class[] classes = new Class[]{value_where.getClass()};
            list = jdbcTemplate.queryForList(query, new Object[]{value_where}, classes);
            SystemLog.query(query +" -> Return a list of "+list.size()+" elements!");
        }else{
            list = jdbcTemplate.queryForList(query);
            SystemLog.query(query +" -> Return a list of "+list.size()+" elements!");
        }
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                for (Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    Object value = entry.getValue();
                    listObj.add(value);
                }
            }
        }catch(Exception e){SystemLog.exception(e);}
        return listObj;
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