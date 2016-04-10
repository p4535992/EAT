package com.github.p4535992.database.datasource.database;

import java.util.*;

/**

 */
public class DatabaseFactory {
    @SuppressWarnings("rawtypes")
	private final Map<String, Class<? extends AbstractDatabaseBasic>> databases;
	private final Properties properties = new Properties();

	private static final DatabaseFactory instance = new DatabaseFactory();

	private DatabaseFactory() {
		databases = new TreeMap<>();
		registerDatabase(MySqlDatabase.class);
		registerDatabase(HsqlDatabase.class);
	}

	/**
	 * Returns the {@code DatabaseFactory} instance
	 * @return the {@code DatabaseFactory} instance
	 */
	public static DatabaseFactory getInstance() {
		return instance;
	}

	/**
	 * Creates a new instance of a given implementation of {@link AbstractDatabaseBasic}.
	 *
	 * @param databaseClass a class that extends {@link AbstractDatabaseBasic}.
	 * @return the {@link AbstractDatabaseBasic} instance.
	 */
	@SuppressWarnings("rawtypes")
	private static AbstractDatabaseBasic newInstance(Class<? extends AbstractDatabaseBasic> databaseClass) {
		try {
			return databaseClass.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException("Cannot create instance of database: " + databaseClass);
		}
	}

	/**
	 * Adds a new {@link AbstractDatabaseBasic} implementation to the factory.
	 * @param databaseClass a class that extends {@link AbstractDatabaseBasic}.
	 */
	@SuppressWarnings("rawtypes")
	public void registerDatabase(Class<? extends AbstractDatabaseBasic> databaseClass) {
		databases.put(newInstance(databaseClass).getDatabaseName().toLowerCase(), databaseClass);
	}

	/**
	 * Returns the names of the available databases you can connect to using this factory class.
	 * @return a set of database names available for use.
	 */
	public Set<String> getAvailableDatabases() {
		return Collections.unmodifiableSet(databases.keySet());
	}


}
