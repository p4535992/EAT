package com.github.p4535992.extractor.object.impl.jdbc.generic;

import com.github.p4535992.extractor.object.dao.jdbc.generic.IGenericDao;
import com.github.p4535992.util.bean.BeansKit;
import com.github.p4535992.util.collection.CollectionKit;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.github.p4535992.util.reflection.ReflectionKit;
import com.github.p4535992.util.sql.SQLSupport;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import org.springframework.transaction.annotation.Transactional;

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
 * @author 4535992
 * @version 2015-06-26
 */
@SuppressWarnings("unused")
public abstract class GenericDaoImpl<T> implements IGenericDao<T> {
    /** {@code org.slf4j.Logger} */
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenericDaoImpl.class);
    protected DriverManagerDataSource driverManag;
    protected JdbcTemplate jdbcTemplate;
    protected String myInsertTable,mySelectTable,myUpdateTable,myDeleteTable;
    protected DataSource dataSource;
    protected SessionFactory sessionFactory;
    @PersistenceContext
    protected EntityManager em;
    protected Class<T> cl;
    protected String clName;
    protected String query;
    protected ApplicationContext context;

    @SuppressWarnings("unchecked")
    public GenericDaoImpl() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    @Override
    public void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database) {
        SystemLog.message("DRIVER[:"+driver+"] ,URL[" + dialectDB + "://" + host + ":" + port + "/" + database+"]");
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.mysql.jdbc.Driver"
        driverManag.setUrl("" + dialectDB + "://" + host + ":" + port + "/" + database); //"jdbc:mysql://localhost:3306/jdbctest"
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
        context = BeansKit.tryGetContextSpring(filePathXml,GenericDaoImpl.class);
    }

    @Override
    public void loadSpringConfig(String[] filesPathsXml) throws IOException {
       context = BeansKit.tryGetContextSpring(filesPathsXml,GenericDaoImpl.class);
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
    public void setTableDelete(String nameOfTable) {
        this.myDeleteTable = nameOfTable;
    }

    @Override
    public void create(String SQL){
        try {
            query = SQL;
            jdbcTemplate.execute(query);
            SystemLog.message(query);
        }catch(Exception e){
            if(e.getMessage().contains("Table '"+myInsertTable+"' already exists")){
                SystemLog.warning("Table '"+myInsertTable+"' already exists");
            }else {
                SystemLog.exception(e);
            }
        }
    }

    /**
     * Method to create a Table on the database.
     * @param SQL string query for create the table.
     * @param erase if true drop a table with the same name if already exists.
     */
    @Override
    public void create(String SQL, boolean erase) {
        if(myInsertTable.isEmpty()) {
            SystemLog.error("Name of the table is empty!!!");
        }
        if(erase) {
            query = "DROP TABLE IF EXISTS "+myInsertTable+";";
            jdbcTemplate.execute(query);
            SystemLog.query(query);
        }
        query = SQL;
        create(SQL);
    }

    /**
     * Method to check if a Record with the same value on the specific columns is already present on the table.
     * @param column_where the Array String of the Columns to check.
     * @param value_where the Array String of the values to check on the Columns.
     * @return if true the record with the specific parameter is already present on the table.
     * @throws MySQLSyntaxErrorException throw when the table not exists.
     */
    @Override
    public boolean verifyDuplicate(String column_where, String value_where) throws MySQLSyntaxErrorException {
        boolean b = false;
        try {
            query = "SELECT count(*) FROM " + myInsertTable + " WHERE " + column_where + "='" + value_where.replace("'", "''") + "'";
            SystemLog.query(query);
            int c = this.jdbcTemplate.queryForObject(query, Integer.class);
            if (c > 0) {
                b = true;
            }
            SystemLog.query(query+" -> "+b);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            if(ExceptionUtils.getRootCause(e) instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException){
                SystemLog.warning("Table :"+myInsertTable+" doesn't exist return false");
                throw new com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException("Table :"+myInsertTable+" doesn't exist");
            }else {
                SystemLog.exception(e);
            }
        }
        return b;
    }

    /**
     * Method to count hte number of row of the table.
     * @param nameOfTable string name of the table.
     * @return the count of the row of the table.
     */
    @Override
    public int getCount(String nameOfTable) {
        return this.jdbcTemplate.queryForObject("select count(*) from " + nameOfTable + "", Integer.class);
    }


    //ENTITY MANAGER METHOD
    @Transactional
    @Override
    public void insert(final T object) {this.em.persist(object); }

    @Transactional
    @Override
    public void delete(final Object id) {
        this.em.remove(this.em.getReference(cl, id));
    }

    @Transactional
    @Override
    public T find(final Object id) {
        return this.em.find(cl, id);
    }

    @Transactional
    @Override
    public T update(final T t) {
        return this.em.merge(t);
    }

    //////////////////////////

    @Override
    public void delete(String whereColumn, String whereValue) {
        jdbcTemplate.update("DELETE FROM " + myDeleteTable + " WHERE " + whereColumn + "= ? ", whereValue);
    }

    @Override
    public void update(String[] columns, Object[] values, String[] columns_where, Object[] values_where){
        try {
            query = prepareUpdateQuery(columns,null,columns_where,null,"AND");
            Object[] vals = CollectionKit.concatenateArrays(values, values_where);
            if(values_where!=null) {
                jdbcTemplate.update(query, vals);
            }else{
                jdbcTemplate.update(query);
            }
            SystemLog.query(query);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            SystemLog.warning(e.getMessage());
        }
    }

    @Override
    public void update(String[] columns,Object[] values,String columns_where,String values_where){
        try {
            prepareUpdateQuery(columns,values,new String[]{columns_where},new String[]{values_where},null);
            SystemLog.query(query);
            if(values_where!=null && !CollectionKit.isArrayEmpty(values)) {
                jdbcTemplate.update(query, values);
            }else{
                jdbcTemplate.update(query);
            }
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            SystemLog.warning(e.getMessage());
        }
    }

    @Override
    public void update(String queryString){
        try{
            query = queryString;
            jdbcTemplate.update(query);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            SystemLog.warning(e.getMessage());
        }
    }

    @Override
    public long countAll(final Map<String, Object> params) {
        query = "SELECT count(*) from "+mySelectTable;
        //queryString.append(this.getQueryClauses(values, null));
        final javax.persistence.Query jpquery = this.em.createQuery(query);
        return (Long) jpquery.getSingleResult();
    }

    /**
     * Method to delete all the record duplicate on the table.
     */
    @Override
    public void deleteAll() {
            jdbcTemplate.update("DELETE FROM" + myDeleteTable + "");
    }

    /**
     * Method to delete all the record duplicate on the table except one.
     * https://social.msdn.microsoft.com/Forums/sqlserver/en-US/5364a7dd-b0c5-463a-9d4c-3ba057f99285/
     *  remove-duplicate-records-but-keep-at-least-1-record?forum=transactsql
     * @param columns string Array of columns name.
     * @param nameKeyColumn string name of the column with primary key.
     */
    @Override
    public void deleteDuplicateRecords(String[] columns,String nameKeyColumn){
        String cols = StringKit.convertArrayContentToSingleString(columns);
        query = "WHILE EXISTS (SELECT COUNT(*) FROM "+myDeleteTable+" GROUP BY "+cols+" HAVING COUNT(*) > 1)\n" +
                "BEGIN\n" +
                "    DELETE FROM "+myDeleteTable+" WHERE "+nameKeyColumn+" IN \n" +
                "    (\n" +
                "        SELECT MIN("+nameKeyColumn+") as [DeleteID]\n" +
                "        FROM "+myDeleteTable+"\n" +
                "        GROUP BY "+cols+"\n" +
                "        HAVING COUNT(*) > 1\n" +
                "    )\n" +
                "END";
        jdbcTemplate.update(query);
    }

    /**
     * Method to delete all the record duplicate on the table except one..
     * https://social.msdn.microsoft.com/Forums/sqlserver/en-US/5364a7dd-b0c5-463a-9d4c-3ba057f99285/
     *  remove-duplicate-records-but-keep-at-least-1-record?forum=transactsql
     * http://stackoverflow.com/questions/6025367/t-sql-deleting-all-duplicate-rows-but-keeping-one
     * @param columns string Array of columns name.
     */
    @Override
    public void deleteDuplicateRecords(String[] columns){
        String cols = StringKit.convertArrayContentToSingleString(columns);
        query = "WITH "+myDeleteTable+" AS ( " +
                "SELECT ROW_NUMBER() OVER(PARTITION BY "+cols+" ORDER BY "+cols+") AS ROW " +
                "FROM "+myDeleteTable+") " +
                "DELETE FROM "+myDeleteTable+" " +
                "WHERE ROW > 1;";
        jdbcTemplate.update(query);
    }

    /**
     * Method to delete all the record duplicate on the table except one.
     * http://stackoverflow.com/questions/4685173/delete-all-duplicate-rows-except-for-one-in-mysql
     * @param columns string Array of columns name of the record..
     * @param values string Array of value of the record.
     * @param high if true.....
     */
    @Override
    public void deleteDuplicateRecords(String[] columns,Object[] values,boolean high){
        String cols = StringKit.convertArrayContentToSingleString(columns);
        if(high) {
            //if you want to keep the row with the lowest id value
            query = "";
        }else{
            //if you want to keep the row with the highest id value
            query = "";
        }
        jdbcTemplate.update(query);
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
        for (String column : columns) {
            trim(column);
        }
    }

    @Override
    public void insertAndTrim(String[] columns, Object[] values, Integer[] types) {
        int[] iTypes = CollectionKit.convertIntegersToInt(types);
        insertAndTrim(columns, values, iTypes);
    }

    @Override
    public void trim(String column){
        query = "UPDATE " + myInsertTable + " SET " + column + " = LTRIM(RTRIM(" + column + "));";
        SystemLog.message("SQL:" + query);
        jdbcTemplate.execute(query);
    }

    @Override
    public void insert(String[] columns,Object[] values,int[] types) {
        try {
            query = prepareInsertIntoQuery(columns, null);
            SystemLog.query(query);
            jdbcTemplate.update(query, values, types);
        }catch(org.springframework.dao.TransientDataAccessResourceException e){
            SystemLog.warning("Attention: probably there is some java.sql.Type not supported from your database");
            SystemLog.exception(e);
        }catch(org.springframework.jdbc.BadSqlGrammarException e2){
            SystemLog.error(e2.getMessage());
            SystemLog.warning("Attention: probably you try to use a Integer[] instead a int[]");
            try {
                query = prepareInsertIntoQuery(columns, values,types);
                SystemLog.query(query);
                //jdbcTemplate.update(query, values);
                jdbcTemplate.update(query);
            }catch(org.springframework.jdbc.UncategorizedSQLException|org.springframework.jdbc.BadSqlGrammarException e){
                SystemLog.exception(e);
            }
        }
        SystemLog.query(prepareInsertIntoQuery(columns, values));
    }

    @Override
    public void insert(String[] columns, Object[] values, Integer[] types) {
        int[] iTypes = CollectionKit.convertIntegersToInt(types);
        insert(columns,values,iTypes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void tryInsert(T object) {
        SQLSupport<T> support = SQLSupport.getInstance(object);
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
        StringBuilder bQuery = new StringBuilder();
        try {
            boolean statement = false;
            bQuery.append("INSERT INTO ").append(myInsertTable).append(" (");
            for (int i = 0; i < columns.length; i++) {
                bQuery.append(columns[i]);
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(") VALUES (");
            if (values == null) {
                statement = true;
            }
            for (int i = 0; i < columns.length; i++) {
                if (statement) {
                    bQuery.append("?");
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
                    bQuery.append(values[i]);
                }
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(");");
        }catch (NullPointerException e){
            SystemLog.warning("Attention: you problably have forgotten to put some column for the SQL query");
            SystemLog.exception(e);
        }
        query = bQuery.toString();
        return query;
    }

    @Override
    public String prepareInsertIntoQuery(String[] columns, Object[] values, Integer[] types) {
        return prepareInsertIntoQuery(columns,values,CollectionKit.convertIntegersToInt(types));
    }

    @Override
    public String prepareInsertIntoQuery(String[] columns, Object[] values, int[] types){
        StringBuilder bQuery = new StringBuilder();
        try {
            boolean statement = false;
            bQuery.append("INSERT INTO ").append(myInsertTable).append("  (");
            for (int i = 0; i < columns.length; i++) {
                bQuery.append(columns[i]);
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(" ) VALUES ( ");
            if (values == null) {
                statement = true;
            }
            for (int i = 0; i < columns.length; i++) {
                if (statement) {
                    bQuery.append("?");
                } else {
                    if(values[i]== null || Objects.equals(values[i].toString(), "NULL")){
                        //if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(Integer.class.getName())){
                        values[i] = "NULL";
                    }else if (values[i]!=null && values[i] instanceof String) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else if (values[i]!=null && values[i] instanceof java.net.URL) {
                        values[i] = "'" + values[i].toString().replace("'", "''") + "'";
                    }else{
                        values[i] = " " + values[i] + " ";
                    }
                    bQuery.append(values[i]);
                }
                if (i < columns.length - 1) {
                    bQuery.append(",");
                }
            }
            bQuery.append(");");
        }catch (NullPointerException e){
            SystemLog.warning("Attention: you problably have forgotten  to put some column for the SQL query");
            SystemLog.exception(e);
        }
        query = bQuery.toString();
        return query;
    }


//   public Object[] prepareValues(Object[] values,int[] types){
//        for(int i = 0; i < values.length; i++){
//            if(values[i]== null){
//                if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(Integer.class.getName())){
//                    values[i] = "NULL";
//                    types[i] = Types.NULL;
//                }
//                else if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(int.class.getName())){
//                    values[i] = "NULL" ;
//                    types[i] = Types.NULL;
//                }
//                else if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(String.class.getName())){
//                    values[i] = "NULL";
//                    types[i] = Types.NULL;
//                }
//                else {
//                    values[i] = "NULL";
//                    types[i] = Types.NULL;
//                }
//            }else{
//                if(SQLHelper.convertSQLTypes2JavaClass(types[i]).getName().equals(String.class.getName())){
//                    values[i] = values[i].toString();
//                    types[i] = Types.VARCHAR;
//                }
//            }
//        }
//        return values;
//    }

    @Override
    public String prepareSelectQuery(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition){
        StringBuilder bQuery = new StringBuilder();
        boolean statement = false;
        //PREPARE THE QUERY STRING
        bQuery.append("SELECT ");
        if(CollectionKit.isArrayEmpty(columns) || (columns.length==1 && columns[0].equals("*"))){
            bQuery.append(" * ");
        }else{
            for(int i = 0; i < columns.length; i++){
                bQuery.append(" ").append(columns[i]).append("");
                if(i < columns.length-1){
                    bQuery.append(", ");
                }
            }
        }
        bQuery.append(" FROM ").append(mySelectTable).append(" ");
        if(!CollectionKit.isArrayEmpty(columns_where)) {
            if(values_where==null){
                statement = true;
                //values_where = new Object[columns_where.length];
                //for(int i = 0; i < columns_where.length; i++){values_where[i]="?";}
            }
            bQuery.append(" WHERE ");
            for (int k = 0; k < columns_where.length; k++) {
                bQuery.append(columns_where[k]).append(" ");
                if(statement){
                    bQuery.append(" = ? ");
                }else {
                    if (values_where[k] == null) {
                        bQuery.append(" IS NULL ");
                    } else {
                        bQuery.append(" = '").append(values_where[k]).append("'");
                    }
                }
                if (condition != null && k < columns_where.length - 1) {
                    bQuery.append(" ").append(condition.toUpperCase()).append(" ");
                } else {
                    bQuery.append(" ");
                }
            }
        }
        if(limit != null && offset!= null) {
            bQuery.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
        }
        query = bQuery.toString();
        return query;
    }

    @Override
    public String prepareUpdateQuery(
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition){
        StringBuilder bQuery = new StringBuilder();
        boolean statement = false;
        bQuery.append("UPDATE ").append(myUpdateTable).append(" SET ");
        int f = 0;
        for (int k = 0; k < columns.length; k++) {
            bQuery.append(columns[k]).append("=? ");
            if(CollectionKit.isArrayEmpty(values)) {
                if (values[k] == null) {
                    values[f] = "NULL";
                    f++;
                } else {
                    values[f] = values[k];
                    f++;
                }
            }
            if (k < columns.length - 1) {
                bQuery.append(", ");
            }
        }
        if(!CollectionKit.isArrayEmpty(columns_where)) {
            if(values_where==null){
                statement = true;
            }
            bQuery.append(" WHERE ");
            for (int k = 0; k < columns_where.length; k++) {
                bQuery.append(columns_where[k]).append(" ");
                if(statement){
                    bQuery.append(" = ? ");
                }else {
                    if (values_where[k] == null) {
                        bQuery.append(" IS NULL ");
                    } else {
                        bQuery.append(" = '").append(values_where[k]).append("'");
                    }
                }
                if (condition != null && k < columns_where.length - 1) {
                    bQuery.append(" ").append(condition.toUpperCase()).append(" ");
                }
            }
        }
        query = bQuery.toString();
        return query;
    }

    /**
     * Method to create a query string for the delete operation.
     * http://stackoverflow.com/questions/3311903/remove-duplicate-rows-in-mysql
     * @param columns string Array of columns name of the record.
     * @param values string Array of value of the record.
     * @param columns_where string Array of columns name for where condition of the record.
     * @param values_where string Array of value for where condition of the record.
     * @param condition string condition AND,OR.
     * @return the string for delete a query.
     */
    @Override
    public String prepareDeleteQuery(
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition){
       /* query ="ALTER IGNORE TABLE "+mySelectTable+" ADD UNIQUE INDEX idx_name ("+
                StringKit.convertArrayContentToSingleString(columns) +" );";*/
        StringBuilder bQuery = new StringBuilder();
        bQuery.append("DELETE ");
        bQuery.append("table1 FROM ").append(myDeleteTable).append(" table1,").append(myDeleteTable).append(" table2");
        bQuery.append(" WHERE ");
        for(int i=0; i< columns.length; i++){
            if(Arrays.asList(columns_where).contains(columns[i])){
                bQuery.append("table1.").append(columns[i]).append("= table2.").append(columns[i]);
                if (condition != null && i < columns.length - 1) {
                    bQuery.append(" ").append(condition.toUpperCase()).append(" ");
                }
            }
        }
        query = bQuery.toString();
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
            Class<?>[] classes = ReflectionKit.getClassesByFieldsByAnnotation(cl,javax.persistence.Column.class);
            for (Map<String, Object> geoDoc : map) {
                T iClass =  ReflectionKit.invokeConstructor(cl);
                for (Map.Entry<String, Object> entry : geoDoc.entrySet()) {
                    //for (Iterator<Map.Entry<String, Object>> it = geoDoc.entrySet().iterator(); it.hasNext(); ) {
                    //    Map.Entry<String, Object> entry = it.next();
                    Object value = entry.getValue();
                    if (!(value == null || Objects.equals(value.toString(), ""))) {
                        if (Objects.equals(classes[i].getName(), String.class.getName())) {
                            value = value.toString();
                        } else if (Objects.equals(classes[i].getName(), URL.class.getName())) {
                            if (!value.toString().matches("^(https?|ftp)://.*$")) {
                                value = new URL("http://" + value.toString());
                            }
                        } else if (Objects.equals(classes[i].getName(), Double.class.getName())) {
                            value = Double.parseDouble(value.toString());
                        } else if (Objects.equals(classes[i].getName(), Integer.class.getName())) {
                            value = Integer.parseInt(value.toString());
                        } else if (Objects.equals(classes[i].getName(), Float.class.getName())) {
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
                columns2 = CollectionKit.copyContentArray(columns);
            }
            final Class<?>[] classes = SQLSupport.getArrayClassesTypes(cl, javax.persistence.Column.class);
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
                            }catch(IllegalAccessException | InvocationTargetException
                                    | NoSuchMethodException | MalformedURLException e){
                               SystemLog.exception(e);
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
                columns2 = CollectionKit.copyContentArray(columns);
            }
            final Class<?>[] classes =SQLSupport.getArrayClassesTypes(cl, javax.persistence.Column.class);
            final List<Method> setters = ReflectionKit.getSettersClassOrder(cl);

            list = jdbcTemplate.query(query,new ResultSetExtractor<List<T>>() {
                @Override
                public List<T> extractData(ResultSet rs) throws SQLException {
                    List<T> list = new ArrayList<>();
                    while(rs.next()){
                        T MyObject = ReflectionKit.invokeConstructor(cl);
                        Method method;
                        try {
                            for (int i = 0; i < columns2.length; i++) {
                                method = setters.get(i); //..support for exception
                                SystemLog.message("(" + i + ")Class:" + classes[i].getName() + ",Column:" + columns2[i]);
                                //String[] column2 = new String[rs.getMetaData().getColumnCount()];
                                //for(int j = 0; j < rs.getMetaData().getColumnCount(); j++){column2[j] = rs.getMetaData().getColumnName(j);}
                                try {
                                    if (classes[i].getName().equalsIgnoreCase(String.class.getName())) {
                                        MyObject = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject, setters.get(i), new Object[]{rs.getString(columns2[i])});
                                        //map.put(columns[i], rs.getString(columns[i]));
                                    } else if (classes[i].getName().equalsIgnoreCase(URL.class.getName())) {
                                        URL url;
                                        if (rs.getString(columns2[i]).matches("^(https?|ftp)://.*$")) {
                                            url = new URL(rs.getString(columns2[i]));
                                        } else {
                                            url = new URL("http://" + rs.getString(columns2[i]));
                                        }
                                        MyObject = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject, setters.get(i), new Object[]{url});
                                        //map.put(columns[i], url);
                                    } else if (classes[i].getName().equalsIgnoreCase(Double.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", ".").replace(" ", ".");
                                        Double num = Double.parseDouble(sup);
                                        MyObject = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    } else if (classes[i].getName().equalsIgnoreCase(Integer.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        Integer num = Integer.parseInt(sup);
                                        MyObject = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    } else if (classes[i].getName().equalsIgnoreCase(int.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        int num = Integer.parseInt(sup);
                                        MyObject = ReflectionKit.invokeSetterMethodForObject(
                                                MyObject, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    }

                                } catch (org.springframework.jdbc.UncategorizedSQLException e) {
                                    SystemLog.warning("... try and failed to get a value of a column not specify  in the query");
                                    MyObject = ReflectionKit.invokeSetterMethodForObject(MyObject, method, new Object[]{null});
                                }
                            }
                        } catch (IllegalAccessException | InvocationTargetException
                                | NoSuchMethodException | MalformedURLException e) {
                            SystemLog.exception(e);
                        }
                        list.add(MyObject);
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
            Class<?>[] classes = new Class<?>[]{value_where.getClass()};
            list = jdbcTemplate.queryForList(query, new Object[]{value_where}, classes);
            SystemLog.query(query +" -> Return a list of "+list.size()+" elements!");
        }else{
            list = jdbcTemplate.queryForList(query);
            SystemLog.query(query +" -> Return a list of "+list.size()+" elements!");
        }
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    listObj.add(value);
                }
            }
        }catch(Exception e){
            SystemLog.exception(e);
        }
        return listObj;
    }

    @Override
    public List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset, String condition){
        List<List<Object[]>> listOfList = new ArrayList<>();
        query = prepareSelectQuery(columns,columns_where,null,limit,offset,condition);
        List<Map<String, Object>> list;
        if(values_where != null) {
            Class<?>[] classes = new Class<?>[]{values_where.getClass()};
            list = jdbcTemplate.queryForList(query, new Object[]{values_where}, classes);
            SystemLog.query(query +" -> Return a list of "+list.size()+" elements!");
        }else{
            list = jdbcTemplate.queryForList(query);
            SystemLog.query(query +" -> Return a list of "+list.size()+" elements!");
        }
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                List<Object[]> listObj = new ArrayList<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    for (String column : columns) {
                        if (entry.getKey().contains(column)) {
                            Object value = entry.getValue();
                            if (value == null) {
                                value = "";
                            }
                            Object[] obj = new Object[]{entry.getKey(), value};
                            listObj.add(obj);
                        }
                    }
                }
                listOfList.add(listObj);
            }
        }catch(Exception e){SystemLog.exception(e);}
        return listOfList;
    }

    @Override
    public List<Object> select(String column, Integer limit, Integer offset, Class<?> clazz){
        List<Object> listObj = new ArrayList<>();
        query = prepareSelectQuery(new String[]{column},null,null,limit,offset,null);
        List<Map<String, Object>> list;
        list = jdbcTemplate.queryForList(query);
        SystemLog.query(query +" -> Return a list of "+list.size()+" elements!");
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = StringKit.convertInstanceOfObject(entry.getValue(), clazz);
                    listObj.add(value);
                }
            }
        }catch(Exception e){SystemLog.exception(e);}
        return listObj;
    }


    /**
     * Method to get a Array of COlumns of The table.
     * @param nameOfTable string name of the table.
     * @return a Array Collection filled with the name of the columns of the table.
     */
    @Override
    public String[] getColumnsInsertTable(String nameOfTable){
        query = "SELECT * FROM "+nameOfTable+" LIMIT 1";
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
        }catch(SQLException e){
            SystemLog.exception(e);
        }
        return columns;
    }





}