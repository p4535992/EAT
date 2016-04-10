package com.github.p4535992.database.datasource.database;

import com.github.p4535992.database.datasource.DataSourceFactory;
import com.github.p4535992.database.datasource.jooq.JOOQUtilities;
import com.github.p4535992.database.datasource.sql.SQLEnum;
import com.github.p4535992.database.datasource.sql.SQLUtility;

import com.github.p4535992.database.util.StringUtilities;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.util.mysql.MySQLDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by 4535992 on 03/02/2016.
 * @author 4535992.
 */
public class OracleXEDatabase extends AbstractDatabaseBasic<MySqlDatabase> {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(OracleXEDatabase.class);

    public OracleXEDatabase(Connection connection){
        super.dataSource = DataSourceFactory.createDataSource(connection);
        super.connection = connection;
        setNewJdbcTemplate();
        setNewDSLContext(super.connection, SQLDialect.MYSQL);
        JOOQUtilities.setDslContext(dslContext);
        JOOQUtilities.setSqlDialect(sqlDialect);
    }

    public OracleXEDatabase(DataSource dataSource){
        this.dataSource = dataSource;
        try {
            super.connection = dataSource.getConnection();
        } catch (SQLException e) {
            super.connection = null;
        }
        setNewJdbcTemplate();
        setNewDSLContext(connection, SQLDialect.MYSQL);
        JOOQUtilities.setDslContext(dslContext);
        JOOQUtilities.setSqlDialect(sqlDialect);
    }

    public OracleXEDatabase(String host, String port, String user, String pass, String database) {
        try {
            SQLUtility.invokeClassDriverForDbType(SQLEnum.DBDialect.ORACLE);
            super.dataSource = DataSourceFactory.createDataSource(
                    DriverManager.getConnection(prepareURL(host,port,database),user,pass),user,pass);
            super.connection = super.dataSource.getConnection();
            setNewJdbcTemplate();
            setNewDSLContext(super.connection, SQLDialect.MYSQL);
            JOOQUtilities.setDslContext(dslContext);
            JOOQUtilities.setSqlDialect(sqlDialect);
        } catch (SQLException e) {
            logger.error("The URL is not correct:" + e.getMessage(), e);
        }catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            logger.error("The Class is not correct:" + e.getMessage(), e);
        }

    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String prepareURL(String host, String port, String schema) {
        String url = SQLEnum.DBDialect.MYSQL.getJDBCConnector() + host;
        if (port != null && StringUtilities.isNumeric(port)) {
            url += ":" + port;
        }
        url += "/"  + schema + "?noDatetimeStringSync=true"; //"jdbc:sql://localhost:3306/jdbctest"
        return url;
    }

    @Override
    public String getDatabaseName() {
        return SQLEnum.DBDialect.HSQLDB.name();
    }

    @Override
    public String getDriverClassName() {
        return SQLEnum.DBDialect.MYSQL.toString();
    }

    @Override
    public MySQLDatabase getJOOQDatabase(Connection conn){
        MySQLDatabase mySQLDatabase = new MySQLDatabase();
        mySQLDatabase.setConnection(conn);
        mySQLDatabase.create();
        return mySQLDatabase;
    }

    @Override
    public DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    public SQLDialect getSQLDialect() {
        return SQLDialect.MYSQL;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return super.jdbcTemplate;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
