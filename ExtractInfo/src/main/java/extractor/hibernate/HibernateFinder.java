package extractor.hibernate;

import extractor.hibernate.finder.FinderArgumentTypeFactory;
import extractor.hibernate.finder.FinderExecutor;
import extractor.hibernate.finder.FinderNamingStrategy;
import extractor.hibernate.finder.impl.SimpleFinderArgumentTypeFactory;
import extractor.hibernate.finder.impl.SimpleFinderNamingStrategy;
import org.hibernate.Session;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by monica on 11/05/2015.
 */
public class HibernateFinder<T> implements FinderExecutor {

    //SUPPORT METHOD FOR SPECIFIC FUNCTION OF HIBERNATE STILL IN PROGRESS...
    private FinderNamingStrategy namingStrategy = new SimpleFinderNamingStrategy(); // Default. Can override in config
    private FinderArgumentTypeFactory argumentTypeFactory = new SimpleFinderArgumentTypeFactory(); // Default. Can override in config
    private Session session;

    /**
     * Method for execute a query on the configuration file of a class
     * @Note: still in progress....
     * @param method
     * @param queryArgs
     * @return
     */
    public List<T> executeFinder(java.lang.reflect.Method method, final Object[] queryArgs) {
        final  org.hibernate.Query namedQuery = prepareQuery(method, queryArgs);
        return (List<T>) namedQuery.list();
    }

    /**
     * Method for execute a query on the configuration file of a class
     * @Note: still in progress....
     * @param method
     * @param queryArgs
     * @return
     */
    public Iterator<T> iterateFinder(java.lang.reflect.Method method, final Object[] queryArgs) {
        final  org.hibernate.Query namedQuery = prepareQuery(method, queryArgs);
        return (Iterator<T>) namedQuery.iterate();
    }

   /* public ScrollableResults scrollFinder(Method method, final Object[] queryArgs)
    {
        final Query namedQuery = prepareQuery(method, queryArgs);
        return (ScrollableResults) namedQuery.scroll();
    }*/

    private  org.hibernate.Query prepareQuery(java.lang.reflect.Method method, Object[] queryArgs) {
        final String queryName = getNamingStrategy().queryNameFromMethod(this.getClass(), method);
        final  org.hibernate.Query namedQuery = session.getNamedQuery(queryName);
        String[] namedParameters = namedQuery.getNamedParameters();
        if(namedParameters.length==0)
        {
            setPositionalParams(queryArgs, namedQuery);
        } else {
            setNamedParams(namedParameters, queryArgs, namedQuery);
        }
        return namedQuery;
    }

    private void setPositionalParams(Object[] queryArgs,  org.hibernate.Query namedQuery) {
        // Set parameter. Use custom Hibernate Type if necessary
        if(queryArgs!=null)
        {
            for(int i = 0; i < queryArgs.length; i++)
            {
                Object arg = queryArgs[i];
                org.hibernate.type.Type argType = getArgumentTypeFactory().getArgumentType(arg);
                if(argType != null)
                {
                    namedQuery.setParameter(i, arg, argType);
                }
                else
                {
                    namedQuery.setParameter(i, arg);
                }
            }
        }
    }

    private void setNamedParams(String[] namedParameters, Object[] queryArgs,  org.hibernate.Query namedQuery) {
        // Set parameter. Use custom Hibernate Type if necessary
        if(queryArgs!=null)
        {
            for(int i = 0; i < queryArgs.length; i++)
            {
                Object arg = queryArgs[i];
                org.hibernate.type.Type argType = getArgumentTypeFactory().getArgumentType(arg);
                if(argType != null)
                {
                    namedQuery.setParameter(namedParameters[i], arg, argType);
                }
                else
                {
                    if(arg instanceof Collection) {
                        namedQuery.setParameterList(namedParameters[i], (Collection) arg);
                    }
                    else
                    {
                        namedQuery.setParameter(namedParameters[i], arg);
                    }
                }
            }
        }
    }

    public FinderNamingStrategy getNamingStrategy()
    {
        return namingStrategy;
    }

    public void setNamingStrategy(FinderNamingStrategy namingStrategy)
    {
        this.namingStrategy = namingStrategy;
    }

    public FinderArgumentTypeFactory getArgumentTypeFactory()
    {
        return argumentTypeFactory;
    }

    public void setArgumentTypeFactory(FinderArgumentTypeFactory argumentTypeFactory) {
        this.argumentTypeFactory = argumentTypeFactory;
    }
}
