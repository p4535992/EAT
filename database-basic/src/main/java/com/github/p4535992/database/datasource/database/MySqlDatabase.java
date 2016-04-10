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
 *
 */
public class MySqlDatabase extends AbstractDatabaseBasic<MySqlDatabase> {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(MySqlDatabase.class);

    public MySqlDatabase(Connection connection) {
        super.dataSource = DataSourceFactory.createDataSource(connection);
        super.connection = connection;
        setNewJdbcTemplate();
        setNewDSLContext(super.connection, SQLDialect.MYSQL);
        JOOQUtilities.setDslContext(dslContext);
        JOOQUtilities.setSqlDialect(sqlDialect);
    }

    public MySqlDatabase(DataSource dataSource) {
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

    public MySqlDatabase(String host, String port, String user, String pass, String database) {
        try {
            SQLUtility.invokeClassDriverForDbType(SQLEnum.DBDialect.MYSQL);
            super.dataSource = DataSourceFactory.createDataSource(
                    DriverManager.getConnection(prepareURL(host,port,database),user,pass),user,pass);
            super.connection = super.dataSource.getConnection();
            setNewJdbcTemplate();
            setNewDSLContext(super.connection, SQLDialect.MYSQL);
            JOOQUtilities.setDslContext(dslContext);
            JOOQUtilities.setSqlDialect(sqlDialect);
            //new MySqlDatabase(DriverManager.getConnection(prepareURL(host, port, database), user, pass));
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
            logger.error("You forgot to turn on your MySQL Server:" + e.getMessage(), e);
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
        String url = SQLEnum.DBDialect.MYSQL.getJDBCConnector() + host;
        try {
            if (port != null && StringUtilities.isNumeric(port)) {
                url += ":" + port;
            }
            url += "/" + schema + "?noDatetimeStringSync=true"; //"jdbc:sql://localhost:3306/jdbctest"
            return url;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return "";
        }
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
    public MySQLDatabase getJOOQDatabase(Connection conn) {
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

    /**
     * Method to get a MySQL connection.
     *
     * @param host     host where the server is.
     * @param port     number of the port of the server.
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public Connection getMySqlConnection(
            String host, String port, String database, String username, String password) {
        // The newInstance() call is a work around for some broken Java implementations
        try {
            SQLUtility.invokeClassDriverForDbType(SQLEnum.DBDialect.MYSQL);
            String url = prepareURL(host, port, database);
            try {
                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
                super.connection = DriverManager.getConnection(url, username, password);
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.error("You forgot to turn on your MySQL Server:" + e.getMessage(), e);
            } catch (SQLException e) {
                logger.error("The URL is not correct:" + e.getMessage(), e);
            }
        } catch (InstantiationException e) {
            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("Access problem while loading!:" + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load driver class!:" + e.getMessage(), e);
        }
        return super.connection;
    }

    /**
     * Method to get a MySQL connection.
     *
     * @param host     string name of the host where is it the database
     * @param database string name of the database.
     * @param username string username.
     * @param password string password.
     * @return the connection.
     */
    public Connection getMySqlConnection(
            String host, String database, String username, String password) {
        return getMySqlConnection(host, null, database, username, password);
    }

    /**
     * Method to get a MySQL connection.
     *
     * @param hostAndDatabase string name of the host where is it the database
     * @param username        string username.
     * @param password        string password.
     * @return the connection.
     */
    public Connection getMySqlConnection(String hostAndDatabase, String username, String password) {
        String[] split = hostAndDatabase.split("/");
        if (hostAndDatabase.startsWith("/")) hostAndDatabase = split[1];
        else hostAndDatabase = split[0];
        return getMySqlConnection(hostAndDatabase, null, split[split.length - 1], username, password);
    }

    public Connection getMySqlConnection(String fullUrl) {
        //e.g. "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true&user=siimobility&password=siimobility"
        try {
            SQLUtility.invokeClassDriverForDbType(SQLEnum.DBDialect.MYSQL);
            try {
                //DriverManager.getConnection("jdbc:mysql://localhost/test?" +"user=minty&password=greatsqldb");
                super.connection = DriverManager.getConnection(fullUrl);
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.error("You forgot to turn on your MySQL Server:" + e.getMessage(), e);
            } catch (SQLException e) {
                logger.error("The URL is not correct" + e.getMessage(), e);
            }
        } catch (InstantiationException e) {
            logger.error("Unable to instantiate driver!:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error("Access problem while loading!:" + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load driver class!:" + e.getMessage(), e);
        }
        return super.connection;
    }

    /* private static Connection getMySqlConnection2(String fullUrl){
        //jdbc:mysql://localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
        //localhost:3306/geodb?user=minty&password=greatsqldb&noDatetimeStringSync=true
        if(fullUrl.toLowerCase().contains("jdbc:mysql://")) fullUrl = fullUrl.replace("jdbc:mysql://","");
        String[] split = fullUrl.split("\\?");
        String hostAndDatabase = split[0];//localhost:3306/geodb
        Pattern pat = Pattern.compile("(\\&|\\?)?(user|username)(\\=)(.*?)(\\&|\\?)?", Pattern.CASE_INSENSITIVE);
        String username = StringUtilities.findWithRegex(fullUrl, pat);
        if(Objects.equals(username, "?")) username = "root";
        pat = Pattern.compile("(\\&|\\?)?(pass|password)(\\=)(.*?)(\\&|\\?)?", Pattern.CASE_INSENSITIVE);
        String password = StringUtilities.findWithRegex(fullUrl, pat);
        if(Objects.equals(password, "?")) password ="";
        split = hostAndDatabase.split("/");
        String database = split[split.length-1];
        hostAndDatabase = hostAndDatabase.replace(database,"");
        pat = Pattern.compile("([0-9])+", Pattern.CASE_INSENSITIVE);
        String port = StringUtilities.findWithRegex(hostAndDatabase, pat);
        if(Objects.equals(port, "?")) port = null;
        else  hostAndDatabase = hostAndDatabase.replace(port, "").replace(":","").replace("/","");
        return getMySqlConnection(hostAndDatabase,port,database,username,password);
    }*/

}
