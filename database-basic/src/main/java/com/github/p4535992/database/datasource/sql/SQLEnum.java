package com.github.p4535992.database.datasource.sql;

import java.sql.Driver;

/**
 * Created by 4535992 on 01/02/2016.
 * @author 4535992.
 */
public class SQLEnum {

    public enum DBDialect{
        CUBRID,DERBY,FIREBIRD,H2,HSQLDB,MARIADB,MYSQL,POSTGRES_9_3,POSTGRES_9_4,POSTGRES_9_5,
        POSTGRES,SQLITE,ORACLE,SQLSERVER,MYSQL_GJT,NULL;

        public String getHibernateDialect(){
            switch(this) {
                case CUBRID:
                    return "org.hibernate.dialect.CUBRIDDialect";
                case DERBY:
                    return "org.hibernate.dialect.DerbyTenSevenDialect";
                case FIREBIRD:
                    return "org.hibernate.dialect.FirebirdDialect";
                case H2:
                    return "org.hibernate.dialect.H2Dialect";
                case HSQLDB:
                    return "org.hibernate.dialect.HSQLDialect";
                case MARIADB:
                case MYSQL:
                    return "org.hibernate.dialect.MySQL5Dialect";
                case POSTGRES_9_3:
                    return "org.hibernate.dialect.PostgreSQL92Dialect";
                case POSTGRES_9_4:
                case POSTGRES_9_5:
                case POSTGRES:
                    return "org.hibernate.dialect.PostgreSQL94Dialect";
                case SQLITE:
                    return "";
                case NULL: return null;
                default:
                    return "";
            }
        }

        public String getDriverClassName(){
            return getJDBCDialect();
        }

        public Class<? extends Driver> getDriverClass(){
            switch(this){
                case MYSQL: return com.mysql.jdbc.Driver.class;
                case HSQLDB: return org.hsqldb.jdbcDriver.class;
                case H2: return org.h2.Driver.class;
                default: return null;
            }
        }

        public String getJDBCDialect(){
            switch (this) {
                case MYSQL: return "com.mysql.jdbc.Driver";
                case MYSQL_GJT: return "org.gjt.mm.mysql.Driver";
                case ORACLE: return "oracle.jdbc.driver.OracleDriver";
                case H2: return "org.h2.Driver";
                case HSQLDB: return "org.hsqldb.jdbcDriver";
                case CUBRID: return "cubrid.jdbc.driver.CUBRIDDriver";
                case DERBY: return "org.apache.derby.jdbc.ClientDriver";
                case FIREBIRD: return "org.firebirdsql.jdbc.FBDriver";
                case MARIADB: return "org.mariadb.jdbc.Driver";
                case POSTGRES: return "org.postgresql.Driver";
                case SQLITE: return "org.sqlite.JDBC";
                case SQLSERVER: return "com.microsoft.sqlserver.jdbc.SQLServerDriver";  /*https://msdn.microsoft.com/it-it/library/ms378526%28v=sql.110%29.aspx*/
                case NULL: return null;
                default: return "java.sql.Driver";
            }
        }

        public String getJDBCConnector(){
            switch (this) {
                case MYSQL: return "jdbc:mysql://";
                case HSQLDB: return"jdbc:hsqldb:hsql://";
                case H2: return "jdbc:h2:tcp://";
                case ORACLE: return "jdbc:oracle:thin:@";
                case SQLSERVER: return "jdbc:sqlserver://"; /*https://msdn.microsoft.com/it-it/library/ms378526%28v=sql.110%29.aspx*/
                case CUBRID: return "";
                case DERBY: return "";
                case FIREBIRD: return "";
                case MARIADB: return "";
                case POSTGRES: return "";
                case POSTGRES_9_3:return "";
                case POSTGRES_9_4:return "";
                case POSTGRES_9_5:return "";
                case SQLITE: return "";
                case NULL: return null;
                default: return "";
            }
        }



    }
}
