package com.github.p4535992.database.datasource.sql.query;

import com.github.p4535992.database.datasource.database.AbstractDatabaseBasic;
import com.github.p4535992.database.datasource.spring.jdbcTemplate.JdbcTemplateUtilities;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.jooq.Condition;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.*;

/**
 * Created by 4535992 on 02/02/2016.
 * @author 4535992.
 */
public abstract class AbstractQuery<T> extends AbstractDatabaseBasic<T> implements MyQuery<T>{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(AbstractQuery.class);

    @Override
    public void create(String SQL){
        JdbcTemplateUtilities.create(jdbcTemplate,myInsertTable,SQL);
    }

    /**
     * Method to create a Table on the database.
     * @param SQL string query for create the table.
     * @param erase if true drop a table with the same name if already exists.
     */
    @Override
    public void create(String SQL, boolean erase) {
        JdbcTemplateUtilities.create(jdbcTemplate,myInsertTable,SQL,erase);
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
        return JdbcTemplateUtilities.verifyDuplicate(jdbcTemplate,myInsertTable,column_where,value_where);
    }

    /**
     * Method to count hte number of row of the table.
     * @param nameOfTable string name of the table.
     * @return the count of the row of the table.
     */
    @Override
    public int getCount(String nameOfTable) { return JdbcTemplateUtilities.getCount(jdbcTemplate,nameOfTable);}


    //ENTITY MANAGER METHOD
    @Transactional
    @Override
    public void insert(final T object) {JdbcTemplateUtilities.insert(entityManager,object); }

    @Transactional
    @Override
    public void delete(final Object id) {JdbcTemplateUtilities.delete(entityManager,id);}

    @Transactional
    @Override
    @SuppressWarnings("unchecked")
    public T find(final Object id) {
        //noinspection unchecked
        return (T) JdbcTemplateUtilities.find(entityManager,id,cl);
    }

    @Transactional
    @Override
    public T update(final T t) { return JdbcTemplateUtilities.update(entityManager,t);}

    //////////////////////////

    @Override
    public void delete(String whereColumn, String whereValue) {
        JdbcTemplateUtilities.delete(jdbcTemplate,myDeleteTable,whereColumn,whereValue);
    }

    @Override
    public void update(String[] columns, Object[] values, String[] columns_where, Object[] values_where){
        JdbcTemplateUtilities.update(jdbcTemplate,myUpdateTable,columns,values,columns_where,values_where);
    }

    @Override
    public void update(String[] columns,Object[] values,String columns_where,String values_where){
        JdbcTemplateUtilities.update(jdbcTemplate,myUpdateTable,columns,values,columns_where,values_where);
    }

    @Override
    public void update(String queryString){
        JdbcTemplateUtilities.update(jdbcTemplate,queryString);
    }

    @Override
    public long getCount() {
        return JdbcTemplateUtilities.getCount(entityManager,mySelectTable);
    }

    /**
     * Method to delete all the record duplicate on the table.
     */
    @Override
    public void deleteAll() {
        JdbcTemplateUtilities.deleteAll(jdbcTemplate,myDeleteTable);
    }

    /**
     * Method to delete all the record duplicate on the table except one.
     * href: https://social.msdn.microsoft.com/Forums/sqlserver/en-US/5364a7dd-b0c5-463a-9d4c-3ba057f99285/
     *  remove-duplicate-records-but-keep-at-least-1-record?forum=transactsql
     * href: http://stackoverflow.com/questions/6025367/t-sql-deleting-all-duplicate-rows-but-keeping-one
     * href: http://stackoverflow.com/questions/4685173/delete-all-duplicate-rows-except-for-one-in-mysql
     * href: http://stackoverflow.com/questions/18390574/how-to-delete-duplicate-rows-in-sql-server
     * @param columns the {@link String[]} Array of columns name of the record..
     * @param nameKeyColumn the {@link String} of the ID column.
     */
    @Override
    public void deleteDuplicateRecords(String[] columns,String nameKeyColumn){
        JdbcTemplateUtilities.deleteDuplicateRecords(jdbcTemplate,myDeleteTable,columns,nameKeyColumn);
    }

    @Override
    public Object select(String column, String column_where, Object value_where){
        //return JdbcTemplateUtilities.select(jdbcTemplate,mySelectTable,column,column_where,value_where);
        return JdbcTemplateUtilities.select(jdbcTemplate,mySelectTable,column,value_where);
    }

    @Override
    public void insertAndTrim(String[] columns,Object[] values,int[] types) {
        JdbcTemplateUtilities.insertAndTrim(jdbcTemplate,myInsertTable,columns,values,types);
    }

    @Override
    public void insertAndTrim(String[] columns, Object[] values, Integer[] types) {
        JdbcTemplateUtilities.insertAndTrim(jdbcTemplate,myInsertTable,columns,values,types);
    }

    @Override
    public void trim(String column){
        JdbcTemplateUtilities.trim(jdbcTemplate,myInsertTable,column);
    }

    @Override
    public void insert(String[] columns,Object[] values,int[] types) {
        JdbcTemplateUtilities.insert(jdbcTemplate,myInsertTable,columns,values,types);
    }

    @Override
    public void insert(String[] columns, Object[] values, Integer[] types) {
        JdbcTemplateUtilities.insert(jdbcTemplate,myInsertTable,columns,values,types);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void tryInsert(T object) {
        JdbcTemplateUtilities.tryInsert(jdbcTemplate,myInsertTable,object);
    }

    @Override
    public List<T> trySelect(final String[] columns, String[] columns_where, Object[] values_where,
                             Integer limit, Integer offset, List<org.jooq.Condition> conditions) {
        return JdbcTemplateUtilities.trySelect(jdbcTemplate,mySelectTable,
                columns,columns_where,values_where,limit, offset,conditions,cl);
    }

    @Override
    public List<T> trySelectWithRowMap(String[] columns,String[] columns_where,Object[] values_where,
                                       Integer limit, Integer offset,List<org.jooq.Condition> conditions) {
        return JdbcTemplateUtilities.trySelectWithRowMap(jdbcTemplate,mySelectTable,
                columns,columns_where,values_where,limit, offset,conditions,cl);
    }

    @Override
    public List<T> trySelectWithResultSetExtractor(
            String[] columns,String[] columns_where,Object[] values_where,
            Integer limit,Integer offset,List<Condition> conditions){
        return JdbcTemplateUtilities.trySelectWithResultSetExtractor(jdbcTemplate,mySelectTable,
                columns,columns_where,values_where,limit, offset,conditions,cl);
    }

    @Override
    public List<Object> select(String column,String column_where,Object value_where,List<org.jooq.Condition> conditions) {
        return JdbcTemplateUtilities.select(jdbcTemplate,mySelectTable,column,column_where,value_where,null,null,conditions);
    }

    @Override
    public List<Object> select(
            String column,String column_where,Object value_where,Integer limit,Integer offset,List<org.jooq.Condition> conditions){
        return JdbcTemplateUtilities.select(jdbcTemplate,mySelectTable,column,column_where,value_where,limit,offset,conditions);
    }

    @Override
    public List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where,List<org.jooq.Condition> conditions){
        return JdbcTemplateUtilities.select(jdbcTemplate,mySelectTable,columns, columns_where, values_where, null, null,conditions);
    }

    @Override
    public List<List<Object[]>> select(
            String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset,
            List<org.jooq.Condition> conditions){
        return JdbcTemplateUtilities.select(jdbcTemplate,mySelectTable,columns, columns_where, values_where, limit, offset,conditions);
    }

    @Override
    public List<Object> select(String column, Integer limit, Integer offset, Class<?> clazz){
        return JdbcTemplateUtilities.select(jdbcTemplate,mySelectTable,column,limit,offset,clazz);
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
            logger.error( e.getMessage(),e);
        }
        return columns;
    }
}
