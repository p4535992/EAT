package com.github.p4535992.database.home;

import com.github.p4535992.database.datasource.DataSourceFactory;
import com.github.p4535992.database.datasource.database.DatabaseBasic;
import com.github.p4535992.database.datasource.database.MySqlDatabase;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by 4535992 on 01/02/2016.
 * @author 4535992.
 */
public class TestMain {

    public static void main(String[] args) throws IOException, SQLException, URISyntaxException {

        //test1 jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true
        //test2 jdbc:postgresql://host:port/database?user=userName&password=pass
        //LogBackUtil.console(Paths.get("logback.xml"));

        DatabaseBasic database = new MySqlDatabase("localhost", "3306", "siimobility", "siimobility", "geodb");

        //MyDataSource dataSource = (MyDataSource) new MySqlDatabase("lovalhost","3306","siimobility","siimobility","geodb");

        //WORK
        //Connection conn = getMySqlConnection("localhost","3306","geodb","siimobility","siimobility");
        //String url = "jdbc:postgresql://host:port/database?user=userName&password=pass";


        //WORK
      /* DataSource conn2 = getLocalConnection(
                "ds1","jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true",
                "com.mysql.jdbc.Driver","siimobility","siimobility");*/

        DataSource conn2 = DataSourceFactory.createDataSource(
                "ds1","jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true",
                "com.mysql.jdbc.Driver","siimobility","siimobility");

        Map<String,DataSource> map =  DataSourceFactory.getDataSourceAlreadySetted();

        //executeSQL(LogBackUtil.getMySQLScript(),conn);
        //WORK
        DataSource conn3 = DataSourceFactory.getDataSource("ds1");

        String test = "";

    }
}
