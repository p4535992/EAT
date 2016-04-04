package com.github.p4535992.database.datasource.sql;


import com.github.p4535992.database.util.StringUtilities;
import org.jooq.SQLDialect;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 4535992 on 02/02/2016.
 * @author 4535992.
 */
public class SQLConverter {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SQLConverter.class);

    /**
     * Method for get a mapt with all SQL java types.
     * href: http://www.java2s.com/Code/Java/Database-SQL-JDBC/convertingajavasqlTypesintegervalueintoaprintablename.htm.
     * @param jdbcType code the {@link Integer} of the type sql.
     * @return the {@link Map} of SQL Types with name
     */
    public static Map<Integer,String> toJdbcTypeName(int jdbcType) {
        Map<Integer,String> map = new HashMap<>();
        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                Integer value = (Integer) field.get(null);
                map.put(value, name);
            } catch (IllegalAccessException e) {
                logger.warn(e.getMessage(),e);
            }
        }
        return map;
    }

    /**
     * Method to convert a String to a SQL Type.
     * @param value the {@link String} value to convert.
     * @return the {@link Integer} SQL Type of the value.
     */
    public static int toSQLTypes(String value){
        if(value == null) return Types.NULL;
        if(StringUtilities.isFloat(value)) return Types.FLOAT;
        if(StringUtilities.isDouble(value)) return Types.DOUBLE;
        if(StringUtilities.isDecimal(value)) return Types.DECIMAL;
        if(StringUtilities.isInt(value)) return Types.INTEGER;
        if(StringUtilities.isURL(value)) return Types.VARCHAR;
        if(StringUtilities.isNumeric(value)) return Types.NUMERIC;
        else  return Types.VARCHAR;
    }

    /**
     * Method for convert a SQLTypes to a java class.
     * @param sqlTypes the {@link Integer} identificator for the SQL java types.
     * @return the {@link Class} relative java class.
     */
    public static Class<?> toClassTypes(int sqlTypes) {
        switch (sqlTypes) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return  String.class;
            case Types.NUMERIC:
            case Types.DECIMAL:
                return  java.math.BigDecimal.class;
            case Types.BIT:
                return  Boolean.class;
            case Types.TINYINT:
                return  Byte.class;
            case Types.SMALLINT:
                return  Short.class;
            case Types.INTEGER:
                return  Integer.class;
            case Types.BIGINT:
                return  Long.class;
            case Types.REAL:
            case Types.FLOAT:
                return  Float.class;
            case Types.DOUBLE:
                return  Double.class;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return  Byte[].class;
            case Types.DATE:
                return  java.sql.Date.class;
            case Types.TIME:
                return  java.sql.Time.class;
            case Types.TIMESTAMP:
                return  java.sql.Timestamp.class;
            case Types.NULL:
                return  Object.class.getSuperclass();
            default:
                return Object.class;
        }
    }

    /**
     * Method for convert a java class to a SQLTypes.
     * @param aClass the {@link Class} correspondent java class.
     * @return the {@link Integer} identificator for the SQL java types.
     */
    public static int toSQLTypes(Class<?> aClass) {
        if(aClass.getName().equals(String.class.getName()))return  Types.VARCHAR;
        else if(aClass.getName().equals(java.math.BigDecimal.class.getName()))return  Types.NUMERIC;
        else if(aClass.getName().equals(Boolean.class.getName()))return  Types.BIT;
        else if(aClass.getName().equals(int.class.getName()))return  Types.INTEGER;
        else if(aClass.getName().equals(Byte.class.getName()))return  Types.TINYINT;
        else if(aClass.getName().equals(Short.class.getName()))return  Types.SMALLINT;
        else if(aClass.getName().equals(Integer.class.getName()))return  Types.INTEGER;
        else if(aClass.getName().equals(Long.class.getName())) return  Types.BIGINT;
        else if(aClass.getName().equals(Float.class.getName()))return  Types.REAL;
        else if(aClass.getName().equals(Double.class.getName()))return  Types.DOUBLE;
        else if(aClass.getName().equals(Byte[].class.getName()))return  Types.VARBINARY;
        else if(aClass.getName().equals(java.sql.Date.class.getName())) return  Types.DATE;
        else if(aClass.getName().equals(java.sql.Time.class.getName()))return  Types.TIME;
        else if(aClass.getName().equals(java.sql.Timestamp.class.getName()))return  Types.TIMESTAMP;
        else if(aClass.getName().equals(java.net.URL.class.getName()))return  Types.VARCHAR;
        return Types.NULL;
    }

    /**
     * Method for convert a SQLTypes to a Stirng name of the types.
     * @param sqlTypes the {@link Integer} SQL types.
     * @return the {@link String} name of the SQL types.
     */
    public static String toStringValue(int sqlTypes) {
        switch (sqlTypes) {
            case Types.BIT: return "BIT";
            case Types.TINYINT: return "TINYINT";
            case Types.SMALLINT: return "SMALLINT";
            case Types.INTEGER: return "INTEGER";
            case Types.BIGINT: return "BIGINT";
            case Types.FLOAT: return "FLOAT";
            case Types.REAL:return "REAL";
            case Types.DOUBLE:return "DOUBLE";
            case Types.NUMERIC:return "NUMERIC";
            case Types.DECIMAL:return "DECIMAL";
            case Types.CHAR:return "CHAR";
            case Types.VARCHAR:return "VARCHAR";
            case Types.LONGVARCHAR:return "LONGVARCHAR";
            case Types.DATE:return "DATE";
            case Types.TIME: return "TIME";
            case Types.TIMESTAMP:return "TIMESTAMP";
            case Types.BINARY:return "BINARY";
            case Types.VARBINARY:return "VARBINARY";
            case Types.LONGVARBINARY:return "LONGVARBINARY";
            case Types.NULL:return "NULL";
            case Types.OTHER:return "OTHER";
            case Types.JAVA_OBJECT:return "JAVA_OBJECT";
            case Types.DISTINCT:return "DISTINCT";
            case Types.STRUCT:return "STRUCT";
            case Types.ARRAY:return "ARRAY";
            case Types.BLOB:return "BLOB";
            case Types.CLOB:return "CLOB";
            case Types.REF:return "REF";
            case Types.DATALINK:return "DATALINK";
            case Types.BOOLEAN:return "BOOLEAN";
            case Types.ROWID:return "ROWID";
            case Types.NCHAR:return "NCHAR";
            case Types.NVARCHAR:return "NVARCHAR";
            case Types.LONGNVARCHAR:return "LONGNVARCHAR";
            case Types.NCLOB:return "NCLOB";
            case Types.SQLXML:return "SQLXML";
            default: return "";
        }
    }

    public static String toNameID(SQLDialect dialectDb){
        return toNameID(dialectDb.name());
    }

    /**
     * Method to convert a DialectDatabase of JOOQ to a String name for a correct cast.
     * @param dialectDb the {@link String} to cast to a correct format.
     * @return the {@link String} with correct format.
     */
    public static String toNameID(String dialectDb){
        if(dialectDb.toLowerCase().contains("mysql"))return "mysql";
        if(dialectDb.toLowerCase().contains("cubrid"))return "cubrid";
        if(dialectDb.toLowerCase().contains("derby"))return "derby";
        if(dialectDb.toLowerCase().contains("firebird"))return "firebird";
        if(dialectDb.toLowerCase().contains("h2"))return "h2";
        if(dialectDb.toLowerCase().contains("hsqldb"))return "hsqldb";
        if(dialectDb.toLowerCase().contains("hsql"))return "hsqldb";
        if(dialectDb.toLowerCase().contains("mariadb"))return "mariadb";
        if(dialectDb.toLowerCase().contains("postgres"))return "postgres";
        if(dialectDb.toLowerCase().contains("postgresql"))return "postgres";
        if(dialectDb.toLowerCase().contains("postgres93"))return "postgres93";
        if(dialectDb.toLowerCase().contains("postgres94"))return "postgres94";
        if(dialectDb.toLowerCase().contains("sqlite"))return "sqlite";
        logger.warn("There is not database type for the specific database dialect used:"+dialectDb);
        return "";
    }

    /*public static XSDDatatype toXDDTypes(int type){
        switch (type) {
            case Types.BIT: return XSDDatatype.XSDbyte;
            case Types.TINYINT: return XSDDatatype.XSDint;
            case Types.SMALLINT: return XSDDatatype.XSDint;
            case Types.INTEGER: return XSDDatatype.XSDinteger;
            case Types.BIGINT: return XSDDatatype.XSDint;
            case Types.FLOAT: return XSDDatatype.XSDfloat;
            //case Types.REAL:return ;
            case Types.DOUBLE:return XSDDatatype.XSDdouble;
            case Types.NUMERIC:return XSDDatatype.XSDinteger;
            case Types.DECIMAL:return XSDDatatype.XSDdecimal;
            case Types.CHAR:return XSDDatatype.XSDstring;
            case Types.VARCHAR:return  XSDDatatype.XSDstring;
            case Types.LONGVARCHAR:return  XSDDatatype.XSDstring;
            case Types.DATE:return  XSDDatatype.XSDdate;
            case Types.TIME: return  XSDDatatype.XSDtime;
            case Types.TIMESTAMP:return  XSDDatatype.XSDdateTime;
            case Types.BINARY:return  XSDDatatype.XSDbase64Binary;
            case Types.VARBINARY:return XSDDatatype.XSDbase64Binary;
            case Types.LONGVARBINARY:return XSDDatatype.XSDbase64Binary;
            case Types.NULL:return XSDDatatype.XSDstring;
            //case Types.OTHER:return "";
            //case Types.JAVA_OBJECT:return "JAVA_OBJECT";
            //case Types.DISTINCT:return "DISTINCT";
            //case Types.STRUCT:return "STRUCT";
            //case Types.ARRAY:return "ARRAY";
            //case Types.BLOB:return "BLOB";
            //case Types.CLOB:return "CLOB";
            //case Types.REF:return "REF";
            //case Types.DATALINK:return "DATALINK";
            case Types.BOOLEAN: return XSDDatatype.XSDboolean;
            //case Types.ROWID:return "ROWID";
            case Types.NCHAR:return XSDDatatype.XSDstring;
            case Types.NVARCHAR:return XSDDatatype.XSDstring;
            case Types.LONGNVARCHAR:return XSDDatatype.XSDstring;
            //case Types.NCLOB:return "NCLOB";
            //case Types.SQLXML:return "SQLXML";
            default: return XSDDatatype.XSDstring;
        }
    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////

    public static SQLEnum.DBDialect toDBDialect(Class<?> driverClass){
        return toDBDialect(driverClass.getCanonicalName());
    }

   /* public static SQLEnum.DBDialect toDBDialect(Class<?> driverClass){
        switch(toDBDialect(driverClass.getCanonicalName())){
            case MYSQL: return SQLEnum.DBDialect.MYSQL;
            default: {
                logger.error("Can't convert the DBDriver:"+driverClass.getCanonicalName()+" to a DBConnector");
                return SQLEnum.DBDialect.NULL;
            }
        }
    }*/

    public static SQLEnum.DBDialect toDBDialect(URL url) {
        return toDBDialect(toDriverClassName(url.toString()));
    }

    /**
     * @param  url the {@link String} url connection to the database.
     * @return "Guess" the JDBC driver from a connection URL.
     */
    public static String toDriverClassName(String url){
        switch(toDBDialect(url)){
            case CUBRID:return SQLEnum.DBDialect.CUBRID.getDriverClassName();
            case DERBY: return SQLEnum.DBDialect.DERBY.getDriverClassName();
            case FIREBIRD:return SQLEnum.DBDialect.FIREBIRD.getDriverClassName();
            case H2:return SQLEnum.DBDialect.H2.getDriverClassName();
            case HSQLDB:return SQLEnum.DBDialect.HSQLDB.getDriverClassName();
            case MARIADB:return SQLEnum.DBDialect.MARIADB.getDriverClassName();
            case MYSQL:return SQLEnum.DBDialect.MYSQL.getDriverClassName();
            case POSTGRES:return SQLEnum.DBDialect.POSTGRES.getDriverClassName();
            case SQLITE:return SQLEnum.DBDialect.SQLITE.getDriverClassName();
            case ORACLE: return SQLEnum.DBDialect.ORACLE.getDriverClassName();
            case SQLSERVER: return  SQLEnum.DBDialect.SQLSERVER.getDriverClassName();
            default:{
                logger.error("Can't convert the URL:"+url+" to a DriverClassNAme");
                return "java.sql.Driver";
            }
        }
    }

    /*public static SQLEnum.DBDialect toDBDialect(String canonicalNameDriverClass){
        if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.MYSQL.getDriverClassName())) return SQLEnum.DBDialect.MYSQL;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.CUBRID.getDriverClassName())) return SQLEnum.DBDialect.CUBRID;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.DERBY.getDriverClassName())) return SQLEnum.DBDialect.DERBY;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.FIREBIRD.getDriverClassName())) return SQLEnum.DBDialect.FIREBIRD;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.H2.getDriverClassName())) return SQLEnum.DBDialect.H2;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.HSQLDB.getDriverClassName())) return SQLEnum.DBDialect.HSQLDB;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.MARIADB.getDriverClassName())) return SQLEnum.DBDialect.MARIADB;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.POSTGRES.getDriverClassName())) return SQLEnum.DBDialect.POSTGRES;
        else if(canonicalNameDriverClass.contains(SQLEnum.DBDialect.SQLITE.getDriverClassName())) return SQLEnum.DBDialect.SQLITE;
        else {
            logger.error("Can't convert the canonicalNameDriverClass:"+canonicalNameDriverClass+" to a DBType");
            return null;
        }
    }*/

    public static SQLEnum.DBDialect toDBDialect(String url){
        if (url == null) {
            logger.error("Can't convert the URL:NULL to a SqlDialect");
            return SQLEnum.DBDialect.NULL;
        }
        // The below list might not be accurate or complete. Feel free to
        // contribute fixes related to new / different JDBC driver configurations
        else if (url.startsWith("jdbc:cubrid:") || url.toLowerCase().contains("cubrid")) {
            return SQLEnum.DBDialect.CUBRID;
        }
        else if (url.startsWith("jdbc:derby:") || url.toLowerCase().contains("derby")) {
            return SQLEnum.DBDialect.DERBY;
        }
        else if (url.startsWith("jdbc:firebirdsql:") || url.toLowerCase().contains("firebird")) {
            return SQLEnum.DBDialect.FIREBIRD;
        }
        else if (url.startsWith("jdbc:h2:") || url.toLowerCase().contains("h2")) {
            return SQLEnum.DBDialect.H2;
        }
        else if (url.startsWith("jdbc:hsqldb:") || url.toLowerCase().contains("hsqldb")) {
            return SQLEnum.DBDialect.HSQLDB;
        }
        else if (url.startsWith("jdbc:mariadb:") || url.toLowerCase().contains("mariadb")) {
            return SQLEnum.DBDialect.MARIADB;
        }
        else if (url.startsWith("jdbc:mysql:")
                || url.startsWith("jdbc:google:") || url.toLowerCase().contains("mysql")) {
            return SQLEnum.DBDialect.MYSQL;
        }
        else if (url.startsWith("jdbc:postgresql:") || url.toLowerCase().contains("postgres")) {
            return SQLEnum.DBDialect.POSTGRES;
        }
        else if (url.startsWith("jdbc:sqlite:")
                || url.startsWith("jdbc:sqldroid:") || url.toLowerCase().contains("sqlite")) {
            return SQLEnum.DBDialect.SQLITE;
        }
        //Added.....
        else if(url.startsWith("jdbc:oracle") || url.toLowerCase().contains("oracle") ){
            return SQLEnum.DBDialect.ORACLE;
        }
        else {
            logger.error("Can't convert the URL:" + url + " to a SqlDialect");
            return SQLEnum.DBDialect.NULL;
        }
    }

    /**
     * @param  url the {@link String} url connection to the database.
     * @return "Guess" the {@link SQLDialect} from a connection URL.
     */
    public static SQLDialect toSQLDialect(String url) {
       // return JDBCUtils.dialect(url);
        if (url == null) {
            return SQLDialect.DEFAULT;
        }
        // The below list might not be accurate or complete. Feel free to
        // contribute fixes related to new / different JDBC driver configurations
        else if (url.startsWith("jdbc:cubrid:") || url.toLowerCase().contains("cubrid")) {
            return SQLDialect.CUBRID;
        }
        else if (url.startsWith("jdbc:derby:") || url.toLowerCase().contains("derby")) {
            return SQLDialect.DERBY;
        }
        else if (url.startsWith("jdbc:firebirdsql:") || url.toLowerCase().contains("firebird")) {
            return SQLDialect.FIREBIRD;
        }
        else if (url.startsWith("jdbc:h2:") || url.toLowerCase().contains("h2")) {
            return SQLDialect.H2;
        }
        else if (url.startsWith("jdbc:hsqldb:") || url.toLowerCase().contains("hsqldb")) {
            return SQLDialect.HSQLDB;
        }
        else if (url.startsWith("jdbc:mariadb:") || url.toLowerCase().contains("mariadb")) {
            return SQLDialect.MARIADB;
        }
        else if (url.startsWith("jdbc:mysql:")
                || url.startsWith("jdbc:google:") || url.toLowerCase().contains("mysql")) {
            return SQLDialect.MYSQL;
        }
        else if (url.startsWith("jdbc:postgresql:") || url.toLowerCase().contains("postgres")) {
            return SQLDialect.POSTGRES;
        }
        else if (url.startsWith("jdbc:sqlite:")
                || url.startsWith("jdbc:sqldroid:") || url.toLowerCase().contains("sqlite")) {
            return SQLDialect.SQLITE;
        }
        //Added.....
       /* else if(url.startsWith("jdbc:oracle")){
            return SQLDialect.
        }
        */
        else {
            logger.error("Can't convert the URL:" + url + " to a SqlDialect");
            return SQLDialect.DEFAULT;
        }
    }

    /**
     * @param connection the {@link Connection} to the database.
     * @return "Guess" the {@link SQLDialect} from a connection URL.
     */
    public static SQLDialect toSQLDialect(Connection connection) {
       // return JDBCUtils.dialect(conn);
        SQLDialect result = SQLDialect.DEFAULT;
        if (connection != null) {
            try {
                DatabaseMetaData m = connection.getMetaData();
                String url = m.getURL();
                result = toSQLDialect(url);
            }
            catch (SQLException ignore) {}
        }
        if (result == SQLDialect.DEFAULT) {
            // If the dialect cannot be guessed from the URL, take some other
            // measures, e.g. by querying DatabaseMetaData.getDatabaseProductName()
        }
        logger.error("Can't convert the Connection to a SQLDialect");
        return result;
    }

    public static SQLEnum.DBDialect toDBDialect(SQLDialect sqlDialect){
        switch(sqlDialect){
            case CUBRID: return SQLEnum.DBDialect.CUBRID;
            case DERBY: return SQLEnum.DBDialect.DERBY;
            case FIREBIRD: return SQLEnum.DBDialect.FIREBIRD;
            case H2: return SQLEnum.DBDialect.H2;
            case HSQLDB: return SQLEnum.DBDialect.HSQLDB;
            case MARIADB: return SQLEnum.DBDialect.MARIADB;
            case MYSQL: return SQLEnum.DBDialect.MYSQL;
            case POSTGRES: return SQLEnum.DBDialect.POSTGRES;
            case POSTGRES_9_3: return SQLEnum.DBDialect.POSTGRES;
            case POSTGRES_9_4: return SQLEnum.DBDialect.POSTGRES;
            //case POSTGRES_9_5: return SQLEnum.DBDialect.POSTGRES;
            case SQLITE: return SQLEnum.DBDialect.SQLITE;
            default:{
                logger.error("Can't convert the SqlDialect:"+sqlDialect.name()+" to a DBType");
                return SQLEnum.DBDialect.NULL;
            }
        }
    }







}
