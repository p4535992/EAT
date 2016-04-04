package com.github.p4535992.database.mvc.config;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@ComponentScan({ "com.github.p4535992.database.mvc" })
@Configuration
public class SpringRootConfig {

	/*http://stackoverflow.com/questions/2231669/when-using-autowire-where-do-you-set-the-bean-you-want-wired-up*/
	@Autowired
	DataSource dataSource;

	@Bean
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return new NamedParameterJdbcTemplate(dataSource);
	}
	
	@PostConstruct
	public void startDBManager() {
		
		//hsqldb
		DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", "" });

		//derby
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:derby:memory:testdb", "--user", "", "--password", "" });

		//h2
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:h2:mem:testdb", "--user", "sa", "--password", "" });

	}
	
	//MethodInvokingBean example
	/*
	@PostConstruct
	public void startDBM() {
		MethodInvokingBean mBean = new MethodInvokingBean();

		mBean.setTargetClass(DatabaseManagerSwing.class);
		mBean.setTargetMethod("main");
		String[] args = new String[] { "--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", "" };
		mBean.setArguments(args);
		try {
			mBean.prepare();
			mBean.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/

}