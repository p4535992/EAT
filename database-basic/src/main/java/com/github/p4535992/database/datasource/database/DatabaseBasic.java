package com.github.p4535992.database.datasource.database;

import com.github.p4535992.database.datasource.sql.query.MyQuery;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@SuppressWarnings("unchecked")
public interface DatabaseBasic {

	JdbcTemplate getJdbcTemplate();
	DataSource getDataSource();
    Connection getConnection();
    DSLContext getDSLContext();
    SQLDialect getSQLDialect();
	String prepareURL(String host, String port, String schema);

    //SessionFactory getSessionFactory();

    /////////////////
	//BASE - SPRING//
	/////////////////
	void setNewJdbcTemplate();
    void setNewDSLContext(Connection connection,SQLDialect sqlDialect);
	//void setDriverManager(String driver, String dialectDB, String host, String port, String user, String pass, String database);
	void setDataSource(DataSource ds);
	void loadSpringConfig(String filePathXml) throws IOException;
	void loadSpringConfig(String[] filesPathsXml) throws IOException;

	/////////
	//OTHER//
	/////////
	void setTableInsert(String nameOfTable);
	void setTableSelect(String nameOfTable);
	void setTableUpdate(String nameOfTable);
	void setTableDelete(String nameOfTable);
	String getMyInsertTable();
	String getMySelectTable();
	String getMyUpdateTable();
	String getMyDeleteTable();

	/////////
	//JDBC///
	/////////
	String[] getColumnsInsertTable(String nameOfTable);

	void create(String SQL) throws Exception;
	void create(String SQL, boolean erase) ;

	void deleteAll();
	void deleteDuplicateRecords(String[] columns, String nameKeyColumn);

	Object select(String column, String column_where, Object value_where);
	List<Object> select(
			String column, String column_where, Object value_where,List<org.jooq.Condition> condition);
	List<Object> select(
			String column, String column_where, Object value_where,Integer limit,Integer offset,List<org.jooq.Condition> condition);
	List<List<Object[]>> select(
			String[] columns, String[] columns_where, Object[] values_where,List<org.jooq.Condition> conditions);
	List<List<Object[]>> select(
			String[] columns, String[] columns_where, Object[] values_where, Integer limit, Integer offset,List<org.jooq.Condition> condition);
	List<Object> select(
			String column, Integer limit, Integer offset, Class<?> clazz);

	void insertAndTrim(String[] columns,Object[] params,int[] types);
	void insertAndTrim(String[] columns, Object[] values, Integer[] types);

	void insert(String[] columns,Object[] params,int[] types);
	void insert(String[] columns, Object[] values, Integer[] types);
	//void tryInsert(Object object);

	void update(String[] columns, Object[] values, String[] columns_where,Object[] values_where);
	void update(String[] columns,Object[] values,String columns_where,String values_where);
	void update(String queryString);

	//void delete(final Object id); //????????????????????????????

	//@Transactional
	//Object update(Object t);

	void delete(String whereColumn, String whereValue);

	/////////////
	//OTHER////
	/////////////
	void trim(String column);
	long getCount();
	boolean verifyDuplicate(String columnWhereName, String valueWhereName) throws MySQLSyntaxErrorException;
	int getCount(String nameOfTable);

    @SuppressWarnings("rawtypes")
	MyQuery getQuery(Class<?> classObject);
	@SuppressWarnings("rawtypes")
	MyQuery getQuery();


	////////////////////////////
	//PREPARED STRING QUERY ////
	////////////////////////////

    /*String prepareInsertIntoQuery(String[] columns,Object[] values);

    String prepareInsertIntoQuery(String[] columns, Object[] values, Integer[] types);

    String prepareInsertIntoQuery(String[] columns, Object[] values, int[] types);

    String prepareSelectQuery(
            String[] columns,String[] columns_where,Object[] values_where,Integer limit,Integer offset,String condition);

    String prepareUpdateQuery(
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition);

    String prepareDeleteQuery(
            String[] columns, Object[] values, String[] columns_where, Object[] values_where, String condition);*/


}
