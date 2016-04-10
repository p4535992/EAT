package com.github.p4535992.database.datasource.database;

import com.github.p4535992.database.datasource.DataSourceFactory;
import com.github.p4535992.database.datasource.jooq.JOOQUtilities;
import com.github.p4535992.database.datasource.sql.SQLEnum;
import com.github.p4535992.database.datasource.sql.SQLUtility;

import com.github.p4535992.database.util.StringUtilities;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.util.hsqldb.HSQLDBDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 */
public class HsqlDatabase extends AbstractDatabaseBasic<HsqlDatabase> {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(HsqlDatabase.class);

	public HsqlDatabase(Connection connection){
        super.dataSource = DataSourceFactory.createDataSource(connection);
        super.connection = connection;
        setNewJdbcTemplate();
        setNewDSLContext(super.connection,SQLDialect.HSQLDB);
        JOOQUtilities.setDslContext(dslContext);
        JOOQUtilities.setSqlDialect(sqlDialect);
	}

    public HsqlDatabase(DataSource dataSource){
        super.dataSource = dataSource;
        try {
            super.connection = dataSource.getConnection();
        } catch (SQLException e) {
            super.connection = null;
        }
        setNewJdbcTemplate();
        setNewDSLContext(connection,SQLDialect.HSQLDB);
        JOOQUtilities.setDslContext(dslContext);
        JOOQUtilities.setSqlDialect(sqlDialect);
    }

    public HsqlDatabase(String host, String port, String user, String pass, String database) {
        try {
            SQLUtility.invokeClassDriverForDbType(SQLEnum.DBDialect.HSQLDB);
            new HsqlDatabase(DriverManager.getConnection(prepareURL(host,port,database), user, pass));
        } catch (SQLException e) {
            logger.error("The URL is not correct:" + e.getMessage(), e);
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            logger.error("The Class is not correct:" + e.getMessage(), e);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String prepareURL(String host, String port, String schema) {
        String url = SQLEnum.DBDialect.HSQLDB.getJDBCConnector() + host;
        if (port != null && StringUtilities.isNumeric(port)) {
            url += ":" + port; //jdbc:hsqldb:data/database
        }
        url += "/" + schema; //"jdbc:sql://localhost:3306/jdbctest"
        return url;
    }

    @Override
    public String getDatabaseName() {
        return SQLEnum.DBDialect.HSQLDB.name();
    }

    @Override
    public String getDriverClassName() {
        return SQLEnum.DBDialect.HSQLDB.toString();
    }

    @Override
    public HSQLDBDatabase getJOOQDatabase(Connection conn) {
        HSQLDBDatabase hsqldbDatabase = new HSQLDBDatabase();
        hsqldbDatabase.setConnection(super.connection);
        hsqldbDatabase.create();
        return hsqldbDatabase;
    }

    @Override
    public DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    public SQLDialect getSQLDialect() {
        return SQLDialect.HSQLDB;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return super.jdbcTemplate;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Method to get a HSQL connection.
     * @param host String name of the host where is the server.
     * @param port String number of the port where the server communicate.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public Connection getHSQLDBConnection(String host, String port, String database, String username, String password) {
        // The newInstance() call is a work around for some broken Java implementations
        try {
            SQLUtility.invokeClassDriverForDbType(SQLEnum.DBDialect.HSQLDB);
            super.connection = DriverManager.getConnection(prepareURL(host,port,database), username, password);
        }catch (InstantiationException e) {
            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
        }catch(IllegalAccessException e){
            logger.error("Access problem while loading!:"+e.getMessage(),e);
        } catch(ClassNotFoundException e){
            logger.error("Unable to load driver class!:"+e.getMessage(),e);
        }catch (SQLException e) {
            logger.error("The URL is not correct:" + e.getMessage(), e);
        }
        return super.connection;
    }








}
