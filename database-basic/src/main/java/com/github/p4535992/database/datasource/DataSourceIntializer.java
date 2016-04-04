package com.github.p4535992.database.datasource;

import com.github.p4535992.database.datasource.DataSourceFactory;
import com.github.p4535992.database.datasource.sql.SQLEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Created by 4535992 on 04/02/2016.
 * href: http://stackoverflow.com/questions/16038360/initialize-database-without-xml-configuration-but-using-configuration
 * @author 4535992.
 */
public class DataSourceIntializer {

   /* @Value("classpath:com/foo/sql/db-schema.sql")
    private Resource schemaScript;

    @Value("classpath:com/foo/sql/db-test-data.sql")
    private Resource dataScript;*/

    public DataSource setUpDataSource(SQLEnum.DBDialect dbDialect,
                                      String url,String database,String username,String password,String... scripts){
        DataSource dataSource = DataSourceFactory.createDataSourceWithSpring(dbDialect,url,database,username,password);
        DatabasePopulatorUtils.execute(createDatabasePopulator(scripts),dataSource);
        return dataSource;
    }

    @Bean
    public DataSource setUpEmbeddedDataSource(EmbeddedDatabaseType embeddedDatabaseType,String... scripts) {
        // no need shutdown, EmbeddedDatabaseFactoryBean will take care of this
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        builder.setName(""); //if not set spring use  testdb
        builder.setType(embeddedDatabaseType); //.H2 or .DERBY
        for(String script : scripts) {
            builder.addScript(script);
          /*  builder.addScript("db/sql/create-db.sql")
                    .addScript("db/sql/insert-data.sql")*/
        }
        return  builder.build();
    }

    private DatabasePopulator createDatabasePopulator(String[] sqlInitializationScripts) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(true);
        for (String sqlScript : sqlInitializationScripts) {
            //Resource sqlScriptResource = RESOURCE_LOADER.getResource(sqlScript);
            Resource sqlScriptResource = new ClassPathResource(sqlScript);
            populator.addScript(sqlScriptResource);
        }
        return populator;
    }


}
