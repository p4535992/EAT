package com.github.p4535992.extractor.object.impl.jdbc.generic;

import com.github.p4535992.extractor.object.dao.jdbc.generic.IGenericDao;
import com.github.p4535992.util.bean.BeansKit;
import com.github.p4535992.util.collection.ArrayUtilities;
import com.github.p4535992.util.collection.CollectionUtilities;
import com.github.p4535992.util.database.jooq.SQLJooqKit2;
import com.github.p4535992.util.database.sql.SQLQuery;
import com.github.p4535992.util.reflection.ReflectionUtilities;
import com.github.p4535992.util.string.StringUtilities;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.SessionFactory;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.github.p4535992.util.database.sql.SQLSupport;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.IOException;
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

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GenericDaoImpl.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

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
    protected DSLContext dslContext;

    @SuppressWarnings("unchecked")
    public GenericDaoImpl() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    @Override
    public void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database) {
        logger.info("DRIVER[:" + driver + "] ,URL[" + dialectDB + "://" + host + ":" + port + "/" + database + "]");
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.mysql.jdbc.Driver"
        driverManag.setUrl("" + dialectDB + "://" + host + ":" + port + "/" + database); //"jdbc:mysql://localhost:3306/jdbctest"
        driverManag.setUsername(user);
        driverManag.setPassword(pass);
        this.dataSource = driverManag;
        setNewJdbcTemplate();
        //NEW TRY WITH JOOQ
        try {
            dslContext = DSL.using(driverManag.getConnection(), SQLJooqKit2.convertDialectDBToSQLDialectJOOQ(dialectDB));
            SQLJooqKit2.setDslContext(dslContext);
            SQLJooqKit2.setSqlDialect(SQLJooqKit2.convertDialectDBToSQLDialectJOOQ(dialectDB));
        }catch(SQLException e){
            //e.printStackTrace();
            logger.error(gm() +"Can't set the driver manager for JOOQ, maybe some inout name (database,table, ecc. is wrong");
            logger.error(gm() + e.getMessage(),e);
        }
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
    public void setTableInsert(String nameOfTable) { this.myInsertTable = nameOfTable;}

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
    public String getMyInsertTable() {return myInsertTable;}

    @Override
    public String getMySelectTable() {return mySelectTable;}

    @Override
    public String getMyUpdateTable() { return myUpdateTable;}

    @Override
    public String getMyDeleteTable() {return myDeleteTable;}

    @Override
    public void create(String SQL){
        try {
            query = SQL;
            jdbcTemplate.execute(query);
            logger.info(query);
        }catch(Exception e){
            if(e.getMessage().contains("Table '"+myInsertTable+"' already exists")){
                logger.warn(gm() + "Table '"+myInsertTable+"' already exists");
            }else {
                logger.error(gm() + e.getMessage(),e);
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
            logger.error(gm() + "Name of the table is empty!!!");
        }
        if(erase) {
            query = "DROP TABLE IF EXISTS "+myInsertTable+";";
            jdbcTemplate.execute(query);
            logger.info(query);
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
            //SystemLog.query(query);
            int c = this.jdbcTemplate.queryForObject(query, Integer.class);
            //int c = this.jdbcTemplate.queryForInt(query);
            if (c > 0) {
                b = true;
            }
            logger.info(query+" -> "+b);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            if(ExceptionUtils.getRootCause(e) instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException){
                logger.warn(gm() +"Table :"+myInsertTable+" doesn't exist return false");
                throw new com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException("Table :"+myInsertTable+" doesn't exist");
            }else {
                logger.error(gm() + e.getMessage(), e);
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
        logger.info("DELETE FROM " + myDeleteTable + " WHERE " + whereColumn + "= " + whereValue);
        jdbcTemplate.update("DELETE FROM " + myDeleteTable + " WHERE " + whereColumn + "= ? ", whereValue);
    }

    @Override
    public void update(String[] columns, Object[] values, String[] columns_where, Object[] values_where){
        try {
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareUpdateQuery(myUpdateTable,columns, null, columns_where, null, "AND");
            /** if you don't want to use JOOQ */
            query = SQLJooqKit2.update(myUpdateTable, columns, values, true,
                    SQLJooqKit2.convertToListConditionEqualsWithAND(columns_where, values_where));

            Object[] vals = ArrayUtilities.concatenateArrays(values, values_where);
            if(values_where!=null) {
                jdbcTemplate.update(query, vals);
            }else{
                jdbcTemplate.update(query);
            }
            logger.info(query);
        }catch(BadSqlGrammarException e) {
            logger.error(gm() + e.getMessage(), e);
        }
    }

    @Override
    public void update(String[] columns,Object[] values,String columns_where,String values_where){
        try {
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareUpdateQuery(myUpdateTable,columns,
            //        values, new String[]{columns_where}, new String[]{values_where},null);
            /** if you want to use JOOQ */
            query = SQLJooqKit2.update(myUpdateTable, columns, values, true,
                    SQLJooqKit2.convertToListConditionEqualsWithAND(new String[]{columns_where}, new Object[]{values_where}));

            logger.info(query);
            if(values_where!=null && !ArrayUtilities.isEmpty(values)) {
                jdbcTemplate.update(query, values);
            }else{
                jdbcTemplate.update(query);
            }
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            logger.error(gm() + e.getMessage(), e);
        }
    }

    @Override
    public void update(String queryString){
        try{
            query = queryString;
            jdbcTemplate.update(query);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            logger.error(gm() + e.getMessage(), e);
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
        String cols = ArrayUtilities.toString(columns);
        query = SQLQuery.deleteDuplicateRecord(myDeleteTable,nameKeyColumn,columns);
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
        String cols = ArrayUtilities.toString(columns);
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
        String cols = ArrayUtilities.toString(columns);
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
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareSelectQuery(mySelectTable,new String[]{column}, new String[]{column_where}, null, null, null, null);
            /** if you  want to use JOOQ */
            query = SQLJooqKit2.select(mySelectTable, new String[]{column}, true);
            result =  jdbcTemplate.queryForObject(query, new Object[]{value_where},value_where.getClass());
            logger.info(query + " -> " + result);
        }catch(org.springframework.dao.EmptyResultDataAccessException e){
            logger.warn(gm() + "Attention probably the SQL result is empty!", e);
            logger.error(gm() + query + " ->" + e.getMessage(), e);
            return null;
        }catch (java.lang.NullPointerException e) {
            logger.error(gm() + query + " ->" + e.getMessage(), e);
            return null;
        }catch(org.springframework.jdbc.CannotGetJdbcConnectionException e){
            logger.warn(gm() + "Attention probably the database not exists!", e);
            logger.error(gm() + query + " ->" + e.getMessage(),e);
            return null;
        }finally{
            query = "";
        }
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
        int[] iTypes = CollectionUtilities.toPrimitive(types);
        insertAndTrim(columns, values, iTypes);
    }

    @Override
    public void trim(String column){
        try {
            query = "UPDATE " + myInsertTable + " SET " + column + " = LTRIM(RTRIM(" + column + "));";
            //SystemLog.message("SQL:" + query);
            jdbcTemplate.execute(query);
        }finally{
            query ="";
        }
    }

    @Override
    public void insert(String[] columns,Object[] values,int[] types) {
        try {
            query ="";
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareInsertIntoQuery(myInsertTable, columns, null);
            /** if you want to use JOOQ */
            query = SQLJooqKit2.insert(myInsertTable, columns, values, types, true);

            logger.info(query);
            jdbcTemplate.update(query, values, types);
            //SystemLog.query(prepareInsertIntoQuery(columns, values, types));
            query ="";
        }catch(org.springframework.dao.TransientDataAccessResourceException e) {
            logger.warn(gm() + "Attention: probably there is some java.sql.Type not supported from your database",e);
            logger.error(gm() + e.getMessage(),e);
        }catch(org.springframework.jdbc.BadSqlGrammarException e){
            logger.warn(gm() + "Attention: probably you try to use a Integer[] instead a int[]",e);
            logger.error(gm() + e.getMessage(),e);
            try {
                /** if you don't want to use JOOQ */
                //query = SQLQuery.prepareInsertIntoQuery(myInsertTable,columns, values,types);
                /** if you want to use JOOQ */
                query = SQLJooqKit2.insert(myInsertTable, columns, values, types, false);
                logger.info(query);
                //jdbcTemplate.update(query, values);
                jdbcTemplate.update(query);
                logger.info(query);
                query ="";
            }catch(org.springframework.jdbc.UncategorizedSQLException|org.springframework.jdbc.BadSqlGrammarException ex){
                logger.error(gm() + ex.getMessage(), ex);
            }
        }finally{
            query ="";
        }
    }

    @Override
    public void insert(String[] columns, Object[] values, Integer[] types) {
        int[] iTypes = CollectionUtilities.toPrimitive(types);
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
    public List<T> trySelect(final String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,List<org.jooq.Condition> conditions) {
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable, columns, columns_where, values_where, limit, offset, condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = SQLJooqKit2.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        query = SQLJooqKit2.select(mySelectTable, columns, false, conditions, limit, offset);
        List<T> list = new ArrayList<>();
        List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
        logger.info(query);
        try {
            int i = 0;
            Class<?>[] classes = ReflectionUtilities.getClassesByFieldsByAnnotation(cl, javax.persistence.Column.class);
            for (Map<String, Object> geoDoc : map) {
                T iClass =  ReflectionUtilities.invokeConstructor(cl);
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
            logger.error(gm() + e.getMessage(),e);
        }finally{
            query ="";
        }
        return list;
    }

    @Override
    public List<T> trySelectWithRowMap(String[] columns,String[] columns_where,Object[] values_where,Integer limit, Integer offset,List<org.jooq.Condition> conditions) {
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,columns, columns_where, values_where, limit, offset,condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
           conditions = SQLJooqKit2.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        query = SQLJooqKit2.select(mySelectTable, columns, false, conditions, limit, offset);
        List<T> list = new ArrayList<>();
        try {
            final String[] columns2;
            if(columns.length==1 && Arrays.asList(columns).contains("*")) {
                columns2 = SQLSupport.getArrayColumns(cl, javax.persistence.Column.class, "name");
//            final Integer[] types =
//                    SQLKit.getArrayTypes(cl, javax.persistence.Column.class);
            }else{
                columns2 = ArrayUtilities.copy(columns);
            }
            final Class<?>[] classes = SQLSupport.getArrayClassesTypes(cl, javax.persistence.Column.class);
            final List<Method> setters = (List<Method>) ReflectionUtilities.findSetters(cl, true);
            list = this.jdbcTemplate.query(
                    query, new RowMapper<T>() {
                        @Override
                        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                            T MyObject2 = ReflectionUtilities.invokeConstructor(cl);
                            try {
                                for (int i = 0; i < columns2.length; i++) {
                                    System.out.println(i+")Class:"+classes[i].getName()+",Column:"+columns2[i]);
                                    if (classes[i].getName()
                                            .equalsIgnoreCase(String.class.getName())) {
                                        MyObject2 = ReflectionUtilities.invokeSetter(
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
                                        MyObject2 = ReflectionUtilities.invokeSetter(
                                                MyObject2, setters.get(i), new Object[]{url});

                                    }
                                    else if (classes[i].getName()
                                            .equalsIgnoreCase(Double.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",",".").replace(" ",".");
                                        Double num = Double.parseDouble(sup);
                                        MyObject2 = ReflectionUtilities.invokeSetter(
                                                MyObject2, setters.get(i), new Object[]{num});

                                    }else if (classes[i].getName()
                                            .equalsIgnoreCase(Integer.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        Integer num = Integer.parseInt(sup);
                                        MyObject2 = ReflectionUtilities.invokeSetter(
                                                MyObject2, setters.get(i), new Object[]{num});
                                    }

                                }
                            }catch(MalformedURLException e){
                                logger.error(gm() + e.getMessage(), e);
                            }
                            return MyObject2;
                        }
                    }
            );
        }catch (Exception e){
            logger.error(gm() + e.getMessage(), e);
        }finally{
            if(list.isEmpty()){logger.warn(gm() + "The result list of:" + query + " is empty!!");}
            else{logger.info(query + " -> return a list with size:" + list.size());}
            query ="";
        }
        return list;
    }

    @Override
    public List<T> trySelectWithResultSetExtractor(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,List<Condition> conditions){
        List<T> list = new ArrayList<>();
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,columns, columns_where, values_where, limit, offset,condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = SQLJooqKit2.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        query = SQLJooqKit2.select(mySelectTable, columns, false, conditions, limit, offset);
        try {
            //T MyObject = ReflectionKit.invokeConstructor(cl);
            final String[] columns2;
            if(columns.length==1 && Arrays.asList(columns).contains("*")) {
              columns2 = SQLSupport.getArrayColumns(cl, Column.class, "name");
//            final Integer[] types =
//                    SQLKit.getArrayTypes(cl, javax.persistence.Column.class);
            }else{
                columns2 = ArrayUtilities.copy(columns);
            }
            final Class<?>[] classes =SQLSupport.getArrayClassesTypes(cl, Column.class);
            final List<Method> setters = (List<Method>) ReflectionUtilities.findSetters(cl, true);

            list = jdbcTemplate.query(query,new ResultSetExtractor<List<T>>() {
                @Override
                public List<T> extractData(ResultSet rs) throws SQLException {
                    List<T> list = new ArrayList<>();
                    while(rs.next()){
                        T MyObject = ReflectionUtilities.invokeConstructor(cl);
                        Method method;
                        try {
                            for (int i = 0; i < columns2.length; i++) {
                                method = setters.get(i); //..support for exception
                                logger.info("(" + i + ")Class:" + classes[i].getName() + ",Column:" + columns2[i]);
                                //String[] column2 = new String[rs.getMetaData().getColumnCount()];
                                //for(int j = 0; j < rs.getMetaData().getColumnCount(); j++){column2[j] = rs.getMetaData().getColumnName(j);}
                                try {
                                    if (classes[i].getName().equalsIgnoreCase(String.class.getName())) {
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), new Object[]{rs.getString(columns2[i])});
                                        //map.put(columns[i], rs.getString(columns[i]));
                                    } else if (classes[i].getName().equalsIgnoreCase(URL.class.getName())) {
                                        URL url;
                                        if (rs.getString(columns2[i]).matches("^(https?|ftp)://.*$")) {
                                            url = new URL(rs.getString(columns2[i]));
                                        } else {
                                            url = new URL("http://" + rs.getString(columns2[i]));
                                        }
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), new Object[]{url});
                                        //map.put(columns[i], url);
                                    } else if (classes[i].getName().equalsIgnoreCase(Double.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", ".").replace(" ", ".");
                                        Double num = Double.parseDouble(sup);
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    } else if (classes[i].getName().equalsIgnoreCase(Integer.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        Integer num = Integer.parseInt(sup);
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    } else if (classes[i].getName().equalsIgnoreCase(int.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        int num = Integer.parseInt(sup);
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), new Object[]{num});
                                        //map.put(columns[i], num);
                                    }

                                } catch (UncategorizedSQLException e) {
                                    logger.warn("... try and failed to get a value of a column not specify  in the query");
                                    MyObject = ReflectionUtilities.invokeSetter(MyObject, method, new Object[]{null});
                                }
                            }
                        } catch (MalformedURLException e) {
                            logger.error(gm() + e.getMessage(), e);
                        }
                        list.add(MyObject);
                    }
                    return list;
                }
            });
        }catch (Exception e){
            logger.error(gm() + e.getMessage(), e);
        }finally{
            if(list.isEmpty()){logger.warn(gm() + "The result list of:" + query + " is empty!!");}
            else{logger.info(query + " -> return a list with size:" + list.size());}
            query ="";
        }
        return list;
    }

    @Override
    public List<Object> select(String column,String column_where,Object value_where,List<org.jooq.Condition> conditions) {
        return select(column,column_where,value_where,null,null,conditions);
    }

    @Override
    public List<Object> select(
            String column,String column_where,Object value_where,Integer limit,Integer offset,List<org.jooq.Condition> conditions){
        List<Object> listObj = new ArrayList<>();
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,new String[]{column},new String[]{column_where},null,limit,offset,condition);
        List<Map<String, Object>> list;
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = SQLJooqKit2.convertToListConditionEqualsWithAND(new String[]{column_where}, new Object[]{value_where});
        }
        if(value_where != null) {
            query = SQLJooqKit2.select(mySelectTable, new String[]{column}, true, conditions, limit, offset);
            list = jdbcTemplate.queryForList(query,new Object[]{value_where},new Class<?>[]{value_where.getClass()});
            logger.info(query + " -> Return a list of " + list.size() + " elements!");
        }else{
            query = SQLJooqKit2.select(mySelectTable, new String[]{column}, false, conditions, limit, offset);
            list = jdbcTemplate.queryForList(query);
            logger.info(query + " -> Return a list of " + list.size() + " elements!");
        }
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    listObj.add(value);
                }
            }
        }catch(Exception e){
            logger.error(gm() + e.getMessage(), e);
        }finally{
            query ="";
        }
        return listObj;
    }

    @Override
    public List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where,List<org.jooq.Condition> conditions){
        return  select(columns, columns_where, values_where, null, null,conditions);
    }

    @Override
    public List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset,List<org.jooq.Condition> conditions){
        List<List<Object[]>> listOfList = new ArrayList<>();
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,columns, columns_where, null, limit, offset,condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = SQLJooqKit2.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        query = SQLJooqKit2.select(mySelectTable, columns, true, conditions, limit, offset);


        List<Map<String, Object>> list;
        if(values_where != null) {
            Class<?>[] classes = new Class<?>[]{values_where.getClass()};
            list = jdbcTemplate.queryForList(query, new Object[]{values_where}, classes);
            logger.info(query + " -> Return a list of " + list.size() + " elements!");
        }else{
            list = jdbcTemplate.queryForList(query);
            logger.info(query + " -> Return a list of " + list.size() + " elements!");
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
        }catch(Exception e){ logger.error(gm() + e.getMessage(), e);}
        return listOfList;
    }

    @Override
    public List<Object> select(String column, Integer limit, Integer offset, Class<?> clazz){
        List<Object> listObj = new ArrayList<>();
        /** if you don't want to use JOOQ */
        //query =SQLQuery.prepareSelectQuery(mySelectTable, new String[]{column}, null, null, limit,offset,null);
        /** if you want to use JOOQ */
        query = SQLJooqKit2.select(mySelectTable, new String[]{column}, false, null, limit, offset);

        List<Map<String, Object>> list;
        list = jdbcTemplate.queryForList(query);
        logger.info(query + " -> Return a list of " + list.size() + " elements!");
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = StringUtilities.toInstanceOfObject(entry.getValue(), clazz);
                    listObj.add(value);
                }
            }
        }catch(Exception e){ logger.error(gm() + e.getMessage(), e);}
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
            logger.error(gm() + e.getMessage(),e);
        }
        return columns;
    }





}