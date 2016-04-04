package com.github.p4535992.database.datasource.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class LocalContext extends InitialContext implements InitialContextFactoryBuilder, InitialContextFactory {

    Map<Object,Object> dataSources;

    LocalContext() throws NamingException {
        super();
        dataSources = new HashMap<>();
    }

    public void addDataSource(String name, String connectionString, String username, String password) {
        this.dataSources.put(name, new LocalDataSource(connectionString,username,password));
    }

    public void addDataSource(String name,Connection connection) {
        this.dataSources.put(name, new LocalDataSource(connection));
    }

    public InitialContextFactory createInitialContextFactory(
            Hashtable<?, ?> hsh) throws NamingException {
        dataSources.putAll(hsh);
        return this;
    }

    public Context getInitialContext(Hashtable<?, ?> arg0)
            throws NamingException {
        return this;
    }

    @Override
    public Object lookup(String name) throws NamingException {
        Object ret = dataSources.get(name);
        return (ret != null) ? ret : super.lookup(name);
    }
}
