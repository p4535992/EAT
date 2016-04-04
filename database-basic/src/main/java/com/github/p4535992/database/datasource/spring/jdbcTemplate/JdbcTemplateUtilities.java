package com.github.p4535992.database.datasource.spring.jdbcTemplate;

import com.github.p4535992.database.datasource.jooq.JOOQUtilities;
import com.github.p4535992.database.datasource.sql.SQLSupport;
import com.github.p4535992.database.datasource.sql.query.SQLQuery;

import com.github.p4535992.database.util.ArrayUtilities;
import com.github.p4535992.database.util.CollectionUtilities;
import com.github.p4535992.database.util.ReflectionUtilities;
import com.github.p4535992.database.util.StringUtilities;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.Condition;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.*;

/**
 * Created by 4535992 on 02/02/2016.
 * @author 4535992.
 */
public class JdbcTemplateUtilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(JdbcTemplateUtilities.class);


    /**
     * Method to create a table SQL.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param query the {@link String} SQL query to invoke.
     * @param myInsertTable the {@link String} name of the Insert Table.
     */
    public static void create(JdbcTemplate jdbcTemplate,String myInsertTable,String query){
        try {
            jdbcTemplate.execute(query);
            logger.info("Execute SQL Query:"+query);
        }catch(Exception e){
            if(e.getMessage().contains("Table '"+myInsertTable+"' already exists")){
                logger.warn( "Table '"+myInsertTable+"' already exists");
            }else {
                logger.error( e.getMessage(),e);
            }
        }
    }

    /**
     * Method to create a Table on the database.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param query the {@link String} SQL query to invoke.
     * @param myInsertTable the {@link String} name of the Insert Table.
     * @param erase the {@link Boolean} if true drop a table with the same name if already exists.
     */
    public static void create(JdbcTemplate jdbcTemplate,String myInsertTable,String query, boolean erase) {
        if(myInsertTable.isEmpty()) {
            logger.error( "Name of the table is empty!!!");
        }
        if(erase) {
            jdbcTemplate.execute("DROP TABLE IF EXISTS "+myInsertTable+";");
            logger.info("Execute SQL Query:"+"DROP TABLE IF EXISTS "+myInsertTable+";");
        }
        create(jdbcTemplate,query,myInsertTable);
    }

    /**
     * Method to check if a Record with the same value on the specific columns is already present on the table.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myInsertTable the {@link String} name of the Insert Table.
     * @param column_where the Array String of the Columns to check.
     * @param value_where the Array String of the values to check on the Columns.
     * @return if true the record with the specific parameter is already present on the table.
     * @throws MySQLSyntaxErrorException throw when the table not exists.
     */
    public static boolean verifyDuplicate(JdbcTemplate jdbcTemplate,String myInsertTable,
                                   String column_where, String value_where) throws MySQLSyntaxErrorException {
        boolean b = false;
        try {
            String query = "SELECT count(*) FROM " + myInsertTable + " WHERE " + column_where + "='" + value_where.replace("'", "''") + "'";
            int c = jdbcTemplate.queryForObject(query, Integer.class);
            if (c > 0) { b = true;}
            logger.info("Execute SQL Query:"+query+" -> "+b);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            //noinspection ThrowableResultOfMethodCallIgnored
            if(ExceptionUtils.getRootCause(e) instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException){
                logger.warn("Table :"+myInsertTable+" doesn't exist return false");
                throw new com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException("Table :"+myInsertTable+" doesn't exist");
            }else {
                logger.error( e.getMessage(), e);
            }
        }
        return b;
    }

    /**
     * Method to count the number of row of the table.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the Insert Table.
     * @return the {@link Integer} count of the row of the table.
     */
    public static int getCount(JdbcTemplate jdbcTemplate,String mySelectTable) {
        int count = jdbcTemplate.queryForObject("select count(*) from " + mySelectTable + "", Integer.class);
        logger.info("Execute SQL Query:"+"select count(*) from " + mySelectTable + ""+" -> "+count);
        return count;
    }


    //ENTITY MANAGER METHOD
    @Transactional
    public static <T> void insert(EntityManager entityManager,final T object) {entityManager.persist(object); }

    @Transactional
    public static <T> void  delete(EntityManager entityManager,final T id) {
        entityManager.remove(entityManager.getReference(id.getClass(), id));
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public static <T> T find(EntityManager entityManager,final Object id,Class<?> classToRefer) {
        //noinspection unchecked
        return (T) entityManager.find(classToRefer, id);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public static <T> T update(EntityManager entityManager,final T t) {
        return entityManager.merge(t);
    }

    public static long getCount(EntityManager entityManager,String mySelectTable) {
        String query = "SELECT count(*) from "+mySelectTable;
        //queryString.append(this.getQueryClauses(values, null));
        final javax.persistence.Query jpquery = entityManager.createQuery(query);
        return (Long) jpquery.getSingleResult();
    }

    //////////////////////////

    /**
     * Method to delete a record with specific value on specific column.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myDeleteTable the {@link String} name of the Insert Table.
     * @param whereColumn the {@link String} name of the column.
     * @param whereValue the {@link String} name of the value.
     */
    public static void delete(JdbcTemplate jdbcTemplate,String myDeleteTable,String whereColumn, String whereValue) {
        logger.info("Execute SQL Query:"+"DELETE FROM " + myDeleteTable + " WHERE " + whereColumn + "= " + whereValue);
        jdbcTemplate.update("DELETE FROM " + myDeleteTable + " WHERE " + whereColumn + "= ? ", whereValue);
    }

    /**
     * Method to update record with specific values on specific columns.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myUpdateTable the {@link String} name of the update table.
     * @param columns the {@link String[]} of the columns to update.
     * @param values the {@link Object[]} of the values to update.
     * @param columns_where the {@link String[]} of the columns where.
     * @param values_where he {@link Object[]} of the values where.
     */
    public static void update(JdbcTemplate jdbcTemplate,String myUpdateTable,
                       String[] columns, Object[] values, String[] columns_where, Object[] values_where){
        try {
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareUpdateQuery(myUpdateTable,columns, null, columns_where, null, "AND");
            /** if you don't want to use JOOQ */
            String query = JOOQUtilities.update(myUpdateTable, columns, values, true,
                    JOOQUtilities.convertToListConditionEqualsWithAND(columns_where, values_where));

            Object[] vals = ArrayUtilities.concatenateArrays(values, values_where);
            if(vals!=null) {
                jdbcTemplate.update(query, vals);
            }else{
                jdbcTemplate.update(query);
            }
            logger.info("Execute SQL Query:"+query);
        }catch(BadSqlGrammarException e) {
            logger.error( e.getMessage(), e);
        }
    }

    /**
     * Method to update record with specific values on specific columns.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myUpdateTable the {@link String} name of the update table.
     * @param columns the {@link String[]} of the columns to update.
     * @param values the {@link Object[]} of the values to update.
     * @param columns_where the {@link String} of the columns where.
     * @param values_where he {@link Object} of the values where.
     */
    public static void update(JdbcTemplate jdbcTemplate,String myUpdateTable,
                       String[] columns,Object[] values,String columns_where,Object values_where){
        try {
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareUpdateQuery(myUpdateTable,columns,
            //        values, new String[]{columns_where}, new String[]{values_where},null);
            /** if you want to use JOOQ */
            String query = JOOQUtilities.update(myUpdateTable, columns, values, true,
                    JOOQUtilities.convertToListConditionEqualsWithAND(new String[]{columns_where}, new Object[]{values_where}));


            if(values_where!=null && !ArrayUtilities.isEmpty(values)) {
                jdbcTemplate.update(query, values);
            }else{
                jdbcTemplate.update(query);
            }
            logger.info("Execute SQL Query:"+query);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            logger.error( e.getMessage(), e);
        }
    }

    /**
     * Method to update record with specific values on specific columns.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param query the {@link String} of the SQL query.
     */
    public static void update(JdbcTemplate jdbcTemplate,String query){
        try{
            jdbcTemplate.update(query);
            logger.info("Execute SQL Query:"+query);
        }catch(org.springframework.jdbc.BadSqlGrammarException e) {
            logger.error( e.getMessage(), e);
        }
    }



    /**
     * Method to delete all the record duplicate on the table.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myDeleteTable the {@link String} name of the delete table.
     */
    public static void deleteAll(JdbcTemplate jdbcTemplate,String  myDeleteTable) {
        jdbcTemplate.update("DELETE FROM" + myDeleteTable + "");
        logger.info("Execute SQL Query:"+"DELETE FROM" + myDeleteTable + "");
    }

    /**
     * Method to delete all the record duplicate on the table except one.
     * href: https://social.msdn.microsoft.com/Forums/sqlserver/en-US/5364a7dd-b0c5-463a-9d4c-3ba057f99285/
     *  remove-duplicate-records-but-keep-at-least-1-record?forum=transactsql
     * href: http://stackoverflow.com/questions/6025367/t-sql-deleting-all-duplicate-rows-but-keeping-one
     * href: http://stackoverflow.com/questions/4685173/delete-all-duplicate-rows-except-for-one-in-mysql
     * href: http://stackoverflow.com/questions/18390574/how-to-delete-duplicate-rows-in-sql-server
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myDeleteTable the {@link String} name of the delete table.
     * @param columns the {@link String[]} Array of columns name of the record.
     * @param nameKeyColumn the {@link String} of the ID column.
     */
    public static void deleteDuplicateRecords(JdbcTemplate jdbcTemplate,String  myDeleteTable,String[] columns,String nameKeyColumn){
        //String cols = ArrayUtilities.toString(columns);
        String query = SQLQuery.deleteDuplicateRecord(myDeleteTable,nameKeyColumn,columns);
        jdbcTemplate.update(query);
        logger.info("Execute SQL Query:"+query);
    }

    /**
     * Method to select a Object from a table.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the delete table.
     * @param column the {@link String}  of column name of the record.
     * @param value_where the {@link String} of the value on the where filter.
     * @return the {@link Object} record select.
     */
    public static Object select(JdbcTemplate jdbcTemplate,String  mySelectTable,String column, Object value_where){
        Object result;
        String query ="";
        try {
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareSelectQuery(mySelectTable,new String[]{column}, new String[]{column_where}, null, null, null, null);
            /** if you  want to use JOOQ */
            query = JOOQUtilities.select(mySelectTable, new String[]{column}, true);
            result =  jdbcTemplate.queryForObject(query, new Object[]{value_where},value_where.getClass());
        }catch(org.springframework.dao.EmptyResultDataAccessException e){
            logger.warn( "Attention probably the SQL result is empty!", e);
            logger.error( query + " ->" + e.getMessage(), e);
            return null;
        }catch (java.lang.NullPointerException e) {
            logger.error( query + " ->" + e.getMessage(), e);
            return null;
        }catch(org.springframework.jdbc.CannotGetJdbcConnectionException e){
            logger.warn( "Attention probably the database not exists!", e);
            logger.error( query + " ->" + e.getMessage(),e);
            return null;
        }
        logger.info("Execute SQL Query:"+query + " -> " + result);
        return result;
    }


    /**
     * Method for insert multiple record.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myInsertTable the {@link String} name of the insert table.
     * @param columns the {@link String[]} array of columns.
     * @param values the {@link String[]} array of values.
     * @param types the {@link Integer[]} array of columns.
     */
    public static void insertAndTrim(JdbcTemplate jdbcTemplate,String myInsertTable,String[] columns,Object[] values,int[] types) {
        insert(jdbcTemplate,myInsertTable,columns, values, types);
        for (String column : columns) {
            trim(jdbcTemplate,myInsertTable,column);
        }
    }

    /**
     * Method for insert multiple record.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myInsertTable the {@link String} name of the insert table.
     * @param columns the {@link String[]} array of columns.
     * @param values the {@link String[]} array of values.
     * @param types the {@link Integer[]} array of columns.
     */
    public static void insertAndTrim(JdbcTemplate jdbcTemplate,String myInsertTable,String[] columns, Object[] values, Integer[] types) {
        int[] iTypes = CollectionUtilities.toPrimitive(types);
        insertAndTrim(jdbcTemplate,myInsertTable,columns, values, iTypes);
    }

    /**
     * Method for make the trim of the value on a specific column.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myInsertTable the {@link String} name of the select table.
     * @param column  the {@link String} of column.
     */
    public static void trim(JdbcTemplate jdbcTemplate,String myInsertTable,String column){
        try {
            String query = "UPDATE " + myInsertTable + " SET " + column + " = LTRIM(RTRIM(" + column + "));";
            jdbcTemplate.execute(query);
            logger.info("Execute SQL Query:"+query);
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * Method to insert.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myInsertTable the {@link String} name of the insert table.
     * @param columns the {@link String[]} array of columns.
     * @param values the {@link String[]} array of values.
     * @param types the {@link Integer[]} array of columns.
     */
    public static void insert(JdbcTemplate jdbcTemplate,String myInsertTable,String[] columns,Object[] values,int[] types) {
        String query;
        try {
            /** if you don't want to use JOOQ */
            //query = SQLQuery.prepareInsertIntoQuery(myInsertTable, columns, null);
            /** if you want to use JOOQ */
            query = JOOQUtilities.insert(myInsertTable, columns, values, types, true);
            jdbcTemplate.update(query, values, types);
            logger.info("Execute SQL Query:"+query);
        }catch(org.springframework.dao.TransientDataAccessResourceException e) {
            logger.error("Attention: probably there is some java.sql.Type not supported from your database:"+e.getMessage(), e);
        }catch(org.springframework.dao.DataIntegrityViolationException e){
            logger.error("Attention: probably you have some value to long for that schema:"+e.getMessage());
            values = StringUtilities.abbreviateOnlyStringableObject(values);
            insert(jdbcTemplate,myInsertTable,columns,values,types);
        }catch(org.springframework.jdbc.BadSqlGrammarException e){
            logger.error( "Attention: probably you try to use a Integer[] instead a int[]:"+e.getMessage(),e);
            try {
                /** if you don't want to use JOOQ */
                //query = SQLQuery.prepareInsertIntoQuery(myInsertTable,columns, values,types);
                /** if you want to use JOOQ */
                query = JOOQUtilities.insert(myInsertTable, columns, values, types, false);
                //jdbcTemplate.update(query, values);
                jdbcTemplate.update(query);
                logger.info("Execute SQL Query:"+query);
            }catch(org.springframework.jdbc.UncategorizedSQLException|org.springframework.jdbc.BadSqlGrammarException ex){
                logger.error( ex.getMessage(), ex);
            }
        }
    }

    /**
     * Method to insert.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myInsertTable the {@link String} name of the insert table.
     * @param columns the {@link String[]} array of columns.
     * @param values the {@link String[]} array of values.
     * @param types the {@link Integer[]} array of columns.
     */
    public static void insert(JdbcTemplate jdbcTemplate,String myInsertTable,String[] columns, Object[] values, Integer[] types) {
        int[] iTypes = CollectionUtilities.toPrimitive(types);
        insert(jdbcTemplate,myInsertTable,columns,values,iTypes);
    }

    /**
     * Method to insert.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param myInsertTable the {@link String} name of the insert table.
     * @param object the {@link Object} to convert to [@link SQLSupport].
     * @param <T> the generic type.
     */
    @SuppressWarnings("unchecked")
    public static <T> void tryInsert(JdbcTemplate jdbcTemplate,String myInsertTable,T object) {
        SQLSupport<T> support = SQLSupport.getInstance(object);
        String[] columns = support.getCOLUMNS();
        Object[] params = support.getVALUES();
        int[] types = support.getTYPES();
        insert(jdbcTemplate,myInsertTable,columns,params,types);
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

    /**
     * Method to try a select with object class.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param columns the {@link String[]} array of columns.
     * @param columns_where the {@link String} of the columns where.
     * @param values_where he {@link Object} of the values where.
     * @param limit the {@link Integer} limit of the SQL query.
     * @param offset the {@link Integer} offset of the SQL query.
     * @param conditions the {@link List} of {@link org.jooq.Condition}.
     * @param classObjectToInsert the {@link Class} of the object to insert.
     * @param <T> the generic type.
     * @return the {@link List} of elements.
     */
    public static <T> List<T> trySelect(JdbcTemplate jdbcTemplate,String mySelectTable,final String[] columns,
                                 String[] columns_where, Object[] values_where, Integer limit, Integer offset,
                                 List<org.jooq.Condition> conditions,Class<T> classObjectToInsert) {
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable, columns, columns_where, values_where, limit, offset, condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = JOOQUtilities.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        String query = JOOQUtilities.select(mySelectTable, columns, false, conditions, limit, offset);
        List<T> list = new ArrayList<>();
        List<Map<String, Object>> map = jdbcTemplate.queryForList(query);
        try {
            int i = 0;
            Class<?>[] classes = ReflectionUtilities.getClassesByFieldsByAnnotation(classObjectToInsert,
                    javax.persistence.Column.class);
            for (Map<String, Object> geoDoc : map) {
                T iClass =  ReflectionUtilities.invokeConstructor(classObjectToInsert);
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
            logger.error( e.getMessage(),e);
        }
        logger.info("Execute SQL Query:"+query+" -> A list of " + list.size()+ "elements.");
        return list;
    }

    /**
     * Method to try a select with object class.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param columns the {@link String[]} array of columns.
     * @param columns_where the {@link String} of the columns where.
     * @param values_where he {@link Object} of the values where.
     * @param limit the {@link Integer} limit of the SQL query.
     * @param offset the {@link Integer} offset of the SQL query.
     * @param conditions the {@link List} of {@link org.jooq.Condition}.
     * @param classObjectToInsert the {@link Class} of the object to insert.
     * @param <T> the generic type.
     * @return the {@link List} of elements.
     */
    public static <T> List<T> trySelectWithRowMap(JdbcTemplate jdbcTemplate, String mySelectTable,
                                           String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset,
                                           List<org.jooq.Condition> conditions, final Class<T> classObjectToInsert) {
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,columns, columns_where, values_where, limit, offset,condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = JOOQUtilities.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        String query = JOOQUtilities.select(mySelectTable, columns, false, conditions, limit, offset);
        List<T> list = new ArrayList<>();
        try {
            final String[] columns2;
            if(columns.length==1 && Arrays.asList(columns).contains("*")) {
                columns2 = SQLSupport.getArrayColumns(classObjectToInsert, javax.persistence.Column.class, "name");
//            final Integer[] types =
//                    SQLKit.getArrayTypes(cl, javax.persistence.Column.class);
            }else{
                columns2 = ArrayUtilities.copy(columns);
            }
            final Class<?>[] classes = SQLSupport.getArrayClassesTypes(classObjectToInsert, javax.persistence.Column.class);
            final List<Method> setters = (List<Method>) ReflectionUtilities.findSetters(classObjectToInsert, true);
            list = jdbcTemplate.query(
                    query, new RowMapper<T>() {
                        @Override
                        public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                            T MyObject2 = ReflectionUtilities.invokeConstructor(classObjectToInsert);
                            try {
                                for (int i = 0; i < columns2.length; i++) {
                                    System.out.println(i+")Class:"+classes[i].getName()+",Column:"+columns2[i]);
                                    if (classes[i].getName()
                                            .equalsIgnoreCase(String.class.getName())) {
                                        MyObject2 = ReflectionUtilities.invokeSetter(
                                                MyObject2, setters.get(i), rs.getString(columns2[i]));
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
                                                MyObject2, setters.get(i), url);

                                    }
                                    else if (classes[i].getName()
                                            .equalsIgnoreCase(Double.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",",".").replace(" ",".");
                                        Double num = Double.parseDouble(sup);
                                        MyObject2 = ReflectionUtilities.invokeSetter(
                                                MyObject2, setters.get(i), num);

                                    }else if (classes[i].getName()
                                            .equalsIgnoreCase(Integer.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        Integer num = Integer.parseInt(sup);
                                        MyObject2 = ReflectionUtilities.invokeSetter(
                                                MyObject2, setters.get(i), num);
                                    }

                                }
                            }catch(MalformedURLException e){
                                logger.error( e.getMessage(), e);
                            }
                            return MyObject2;
                        }
                    }
            );
        }catch (Exception e){
            logger.error( e.getMessage(), e);
        }
        if(list.isEmpty()){logger.warn( "The result list of:" + query + " is empty!!");}
        else{ logger.info("Execute SQL Query:"+query+" -> A list of " + list.size()+ "elements.");}

        return list;
    }


    /**
     * Method to try a select with object class.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param columns the {@link String[]} array of columns.
     * @param columns_where the {@link String} of the columns where.
     * @param values_where he {@link Object} of the values where.
     * @param limit the {@link Integer} limit of the SQL query.
     * @param offset the {@link Integer} offset of the SQL query.
     * @param conditions the {@link List} of {@link org.jooq.Condition}.
     * @param classObjectToInsert the {@link Class} of the object to insert.
     * @param <T> the generic type.
     * @return the {@link List} of elements.
     */
    public static <T> List<T> trySelectWithResultSetExtractor(JdbcTemplate jdbcTemplate, String mySelectTable,
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,
                                                       List<Condition> conditions,final Class<T> classObjectToInsert){
        List<T> list = new ArrayList<>();
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,columns, columns_where, values_where, limit, offset,condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = JOOQUtilities.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        String query = JOOQUtilities.select(mySelectTable, columns, false, conditions, limit, offset);
        try {
            //T MyObject = ReflectionKit.invokeConstructor(cl);
            final String[] columns2;
            if(columns.length==1 && Arrays.asList(columns).contains("*")) {
                columns2 = SQLSupport.getArrayColumns(classObjectToInsert, javax.persistence.Column.class, "name");
//            final Integer[] types =
//                    SQLKit.getArrayTypes(cl, javax.persistence.Column.class);
            }else{
                columns2 = ArrayUtilities.copy(columns);
            }
            final Class<?>[] classes =SQLSupport.getArrayClassesTypes(classObjectToInsert, javax.persistence.Column.class);
            final List<Method> setters = (List<Method>) ReflectionUtilities.findSetters(classObjectToInsert, true);

            list = jdbcTemplate.query(query,new ResultSetExtractor<List<T>>() {
                @Override
                public List<T> extractData(ResultSet rs) throws SQLException {
                    List<T> list = new ArrayList<>();
                    while(rs.next()){
                        T MyObject = ReflectionUtilities.invokeConstructor(classObjectToInsert);
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
                                                MyObject, setters.get(i), rs.getString(columns2[i]));
                                        //map.put(columns[i], rs.getString(columns[i]));
                                    } else if (classes[i].getName().equalsIgnoreCase(URL.class.getName())) {
                                        URL url;
                                        if (rs.getString(columns2[i]).matches("^(https?|ftp)://.*$")) {
                                            url = new URL(rs.getString(columns2[i]));
                                        } else {
                                            url = new URL("http://" + rs.getString(columns2[i]));
                                        }
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), url);
                                        //map.put(columns[i], url);
                                    } else if (classes[i].getName().equalsIgnoreCase(Double.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", ".").replace(" ", ".");
                                        Double num = Double.parseDouble(sup);
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), num);
                                        //map.put(columns[i], num);
                                    } else if (classes[i].getName().equalsIgnoreCase(Integer.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        Integer num = Integer.parseInt(sup);
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), num);
                                        //map.put(columns[i], num);
                                    } else if (classes[i].getName().equalsIgnoreCase(int.class.getName())) {
                                        String sup = rs.getString(columns2[i]).replace(",", "").replace(".", "").replace(" ", "");
                                        int num = Integer.parseInt(sup);
                                        MyObject = ReflectionUtilities.invokeSetter(
                                                MyObject, setters.get(i), num);
                                        //map.put(columns[i], num);
                                    }

                                } catch (UncategorizedSQLException e) {
                                    logger.warn("... try and failed to get a value of a column not specify  in the query");
                                    MyObject = ReflectionUtilities.invokeSetter(MyObject, method, new Object[]{null});
                                }
                            }
                        } catch (MalformedURLException e) {
                            logger.error( e.getMessage(), e);
                        }
                        list.add(MyObject);
                    }
                    return list;
                }
            });
        }catch (Exception e){
            logger.error( e.getMessage(), e);
        }
        if(list.isEmpty()){logger.warn( "The result list of:" + query + " is empty!!");}
        else{ logger.info("Execute SQL Query:"+query+" -> A list of " + list.size()+ "elements.");}
        return list;
    }


    /**
     * Method to try a select with object class.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param column the {@link String} array of columns.
     * @param column_where the {@link String} of the columns where.
     * @param value_where he {@link Object} of the values where.
     * @param conditions the {@link List} of {@link org.jooq.Condition}.
     * @return  the {@link List} of elements.
     */
    public static List<Object> select(JdbcTemplate jdbcTemplate, String mySelectTable,String column,String column_where,Object value_where,
                               List<org.jooq.Condition> conditions) {
        return select(jdbcTemplate,mySelectTable,column,column_where,value_where,null,null,conditions);
    }

    /**
     * Method to try a select with object class.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param column the {@link String} array of columns.
     * @param column_where the {@link String} of the columns where.
     * @param value_where he {@link Object} of the values where.
     * @param limit the {@link Integer} limit of the SQL query.
     * @param offset the {@link Integer} offset of the SQL query.
     * @param conditions the {@link List} of {@link org.jooq.Condition}.
     * @return  the {@link List} of elements.
     */
    public static List<Object> select(JdbcTemplate jdbcTemplate, String mySelectTable,
            String column,String column_where,Object value_where,Integer limit,Integer offset,List<org.jooq.Condition> conditions){
        List<Object> listObj = new ArrayList<>();
        String query;
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,new String[]{column},new String[]{column_where},null,limit,offset,condition);
        List<Map<String, Object>> list;
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = JOOQUtilities.convertToListConditionEqualsWithAND(new String[]{column_where}, new Object[]{value_where});
        }
        if(value_where != null) {
            query = JOOQUtilities.select(mySelectTable, new String[]{column}, true, conditions, limit, offset);
            list = jdbcTemplate.queryForList(query,new Object[]{value_where},new Class<?>[]{value_where.getClass()});
            logger.info("Execute SQL Query:"+query+" -> A list of " + list.size()+ "elements.");
        }else{
            query = JOOQUtilities.select(mySelectTable, new String[]{column}, false, conditions, limit, offset);
            list = jdbcTemplate.queryForList(query);
            logger.info("Execute SQL Query:"+query+" -> A list of " + list.size()+ "elements.");
        }
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    listObj.add(value);
                }
            }
        }catch(Exception e){
            logger.error( e.getMessage(), e);
        }
        return listObj;
    }

    /**
     * Method to select.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param columns the {@link String[]} array of columns.
     * @param columns_where the {@link String} of the columns where.
     * @param values_where he {@link Object} of the values where.
     * @param conditions the {@link List} of {@link org.jooq.Condition}.
     * @return the {@link List} of {@link List} of {@link Object[]}.
     */
    public static List<List<Object[]>> select(JdbcTemplate jdbcTemplate, String mySelectTable,
            String[] columns, String[] columns_where, Object[] values_where,List<org.jooq.Condition> conditions){
        return  select(jdbcTemplate, mySelectTable,columns, columns_where, values_where, null, null,conditions);
    }

    /**
     * Method to select.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param columns the {@link String[]} array of columns.
     * @param columns_where the {@link String} of the columns where.
     * @param values_where he {@link Object} of the values where.
     * @param limit the {@link Integer} limit of the SQL query.
     * @param offset the {@link Integer} offset of the SQL query.
     * @param conditions the {@link List} of {@link org.jooq.Condition}.
     * @return the {@link List} of {@link List} of {@link Object[]}.
     */
    public static List<List<Object[]>> select(JdbcTemplate jdbcTemplate, String mySelectTable,
            String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset,
                                       List<org.jooq.Condition> conditions){
        List<List<Object[]>> listOfList = new ArrayList<>();
        /** if you don't want to use JOOQ */
        //query = SQLQuery.prepareSelectQuery(mySelectTable,columns, columns_where, null, limit, offset,condition);
        /** if you want to use JOOQ */
        if(conditions == null || conditions.isEmpty()) {
            conditions = JOOQUtilities.convertToListConditionEqualsWithAND(columns_where, values_where);
        }
        String query = JOOQUtilities.select(mySelectTable, columns, true, conditions, limit, offset);


        List<Map<String, Object>> list;
        if(values_where != null) {
            Class<?>[] classes = new Class<?>[]{values_where.getClass()};
            list = jdbcTemplate.queryForList(query, new Object[]{values_where}, classes);
            logger.info("Execute SQL Query:"+query + " -> Return a list of " + list.size() + " elements!");
        }else{
            list = jdbcTemplate.queryForList(query);
            logger.info("Execute SQL Query:"+query + " -> Return a list of " + list.size() + " elements!");
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
        }catch(Exception e){ logger.error( e.getMessage(), e);}
        return listOfList;
    }

    /**
     * Method to select.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param column the {@link String} array of columns.
     * @param limit the {@link Integer} limit of the SQL query.
     * @param offset the {@link Integer} offset of the SQL query.
     * @param clazz the {@link Class} of the object to select.
     * @return the {@link List} of {@link Object}.
     */
    public static List<Object> select(JdbcTemplate jdbcTemplate, String mySelectTable,
                               String column, Integer limit, Integer offset, Class<?> clazz){
        List<Object> listObj = new ArrayList<>();
        /** if you don't want to use JOOQ */
        //query =SQLQuery.prepareSelectQuery(mySelectTable, new String[]{column}, null, null, limit,offset,null);
        /** if you want to use JOOQ */
        String query = JOOQUtilities.select(mySelectTable, new String[]{column}, false, null, limit, offset);

        List<Map<String, Object>> list;
        list = jdbcTemplate.queryForList(query);
        logger.info("Execute SQL Query:"+query + " -> Return a list of " + list.size() + " elements!");
        try {
            for (Map<String, Object> map : list) { //...column already filter from the query
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = StringUtilities.toInstanceOfObject(entry.getValue(), clazz);
                    listObj.add(value);
                }
            }
        }catch(Exception e){ logger.error( e.getMessage(), e);}
        return listObj;
    }

    /**
     * Method to execute a query with ? element on the value.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param sql the {@link String} sql with ? element on .
     * @param values the {@link Object} array value of the ? elements.
     * @param clazzBean the {@link Class} of the Bean used.
     * @param <T> the generic type.
     * @return the T Bean founded from the String sql.
     */
    public static <T> T select(JdbcTemplate jdbcTemplate,String sql,Object[] values,Class<T> clazzBean){
        if(ReflectionUtilities.isPrimitiveType(clazzBean)) {
            return jdbcTemplate.queryForObject(sql, clazzBean,values);
        }else{
            return jdbcTemplate.queryForObject(sql, values, new BeanPropertyRowMapper<>(clazzBean));
        }
    }

    /**
     * Method to execute a query with ? element on the value.
     * @param <T> the generic type.
     * @param jdbcTemplate the {@link JdbcTemplate} to use.
     * @param mySelectTable the {@link String} name of the select table.
     * @param clazzBean the {@link Class} of the Bean used.
     * @return the T Bean founded from the String sql.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static <T> List selectAll(JdbcTemplate jdbcTemplate, String mySelectTable, Class<T> clazzBean){
        String sql = "SELECT * FROM "+mySelectTable+"";
        //noinspection unchecked
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(clazzBean));
    }



}
