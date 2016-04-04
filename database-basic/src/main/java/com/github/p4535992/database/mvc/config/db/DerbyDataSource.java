package com.github.p4535992.database.mvc.config.db;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Profile("derby")
@Configuration
public class DerbyDataSource{

	//jdbc:derby:memory:testdb
	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.DERBY)
				.addScript("db/sql/create-db.sql").addScript("db/sql/insert-data.sql").build();

	}

}