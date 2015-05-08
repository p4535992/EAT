package extractor.hibernate;
import extractor.hibernate.finder.FinderArgumentTypeFactory;
import extractor.hibernate.finder.FinderExecutor;
import extractor.hibernate.finder.FinderNamingStrategy;
import extractor.hibernate.finder.impl.SimpleFinderArgumentTypeFactory;
import extractor.hibernate.finder.impl.SimpleFinderNamingStrategy;
import org.hibernate.SessionFactory;
import p4535992.util.reflection.ReflectionKit;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 04/05/2015.
 */
public class Hibernate4SpringKit<T> implements IHibernateKit<T>,FinderExecutor{

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Hibernate4Kit.class);
    protected String myInsertTable,mySelectTable,myUpdateTable;
    protected org.hibernate.SessionFactory sessionFactory;
    protected org.hibernate.Session session;
    protected org.hibernate.Query query;
    protected Class<T> cl;
    protected String clName,sql;
    protected org.springframework.context.ApplicationContext context;
    public String beanIdSessionFactory;
    private String contextFile = "test-applicationContext.xml";

    public Hibernate4SpringKit(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    public Hibernate4SpringKit(Class<T> cl){
        this.cl = cl;
    }

    public void setContext(){
        context = new org.springframework.context.support.ClassPathXmlApplicationContext(contextFile);
    }

    //NOTE THE FOLLOW METHOD NEED THE SPRINGFRAMEWORK LIBRARY
    // Hibernate implementation of GenericDao
    // A typesafe implementation of CRUD and finder methods based on Hibernate and Spring AOP
    // The finders are implemented through the executeFinder method. Normally called by the FinderIntroductionInterceptor
    private FinderNamingStrategy namingStrategy = new SimpleFinderNamingStrategy(); // Default. Can override in config
    private FinderArgumentTypeFactory argumentTypeFactory = new SimpleFinderArgumentTypeFactory(); // Default. Can override in config


    public javax.sql.DataSource getDataSource() {
        return org.springframework.orm.hibernate4.SessionFactoryUtils.getDataSource(sessionFactory);
    }


    @Override
    public void shutdown()
    {
        closeSession();
    }

    @Override
    public void openSession()
    {
        org.hibernate.SessionFactory sessionFactory = getSessionFactory();
        org.hibernate.Session session = getSessionFactory().openSession();
        org.springframework.transaction.support.TransactionSynchronizationManager.bindResource(sessionFactory,
                new org.springframework.orm.hibernate4.SessionHolder(session));
    }

    @Override
    public void closeSession()
    {
        org.hibernate.SessionFactory sessionFactory = getSessionFactory();
        org.springframework.orm.hibernate4.SessionHolder sessionHolder =
                (org.springframework.orm.hibernate4.SessionHolder)
                        org.springframework.transaction.support.TransactionSynchronizationManager.unbindResource(sessionFactory);
        sessionHolder.getSession().flush();
        sessionHolder.getSession().close();
        //SessionFactoryUtils.releaseSession(sessionHolder.getSession(), sessionFactory);
        org.springframework.orm.hibernate4.SessionFactoryUtils.closeSession(sessionHolder.getSession());
    }

    @Override
    public  org.hibernate.Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }


    @Override
    public void insert(T newInstance) {

    }


    private void restartSession()
    {
        closeSession();
        openSession();
    }

    @Override
    public  org.hibernate.SessionFactory getSessionFactory()
    {
        //return (SessionFactory) context.getBean("sessionFactory");
        return ( org.hibernate.SessionFactory) context.getBean(beanIdSessionFactory);
    }

    @Override
    public  org.hibernate.Session getSession() {
        return null;
    }

    private T getDao(String beanId)
    {
        //T personDao = (T) context.getBean("personDao");
        T t = (T) context.getBean(beanId);
        return t;
    }


    @Override
    public Object insertAndReturn(T object) {
        return (Object) session.save(object);
    }

    @Override
    public T Select(Object id)
    {
        return (T) session.get(cl,(Long)id);
    }

    @Override
    public List<T> select() {
        return null;
    }

    @Override
    public List<T> select(String nameColumn, int limit, int offset) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void update(T object, String whereColumn, Object whereValue) {

    }

    @Override
    public void delete(String whereColumn, Object whereValue) {

    }


    public List<T> executeFinder(java.lang.reflect.Method method, final Object[] queryArgs)
    {
        final  org.hibernate.Query namedQuery = prepareQuery(method, queryArgs);
        return (List<T>) namedQuery.list();
    }

    public Iterator<T> iterateFinder(java.lang.reflect.Method method, final Object[] queryArgs)
    {
        final  org.hibernate.Query namedQuery = prepareQuery(method, queryArgs);
        return (Iterator<T>) namedQuery.iterate();
    }

//    public ScrollableResults scrollFinder(Method method, final Object[] queryArgs)
//    {
//        final Query namedQuery = prepareQuery(method, queryArgs);
//        return (ScrollableResults) namedQuery.scroll();
//    }

    private  org.hibernate.Query prepareQuery(java.lang.reflect.Method method, Object[] queryArgs)
    {
        final String queryName = getNamingStrategy().queryNameFromMethod(cl, method);
        final  org.hibernate.Query namedQuery = getSession().getNamedQuery(queryName);
        String[] namedParameters = namedQuery.getNamedParameters();
        if(namedParameters.length==0)
        {
            setPositionalParams(queryArgs, namedQuery);
        } else {
            setNamedParams(namedParameters, queryArgs, namedQuery);
        }
        return namedQuery;
    }

    private void setPositionalParams(Object[] queryArgs,  org.hibernate.Query namedQuery)
    {
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

    private void setNamedParams(String[] namedParameters, Object[] queryArgs,  org.hibernate.Query namedQuery)
    {
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

    public void setArgumentTypeFactory(FinderArgumentTypeFactory argumentTypeFactory)
    {
        this.argumentTypeFactory = argumentTypeFactory;
    }


    @Override
    public void updateAnnotationTable(String nameOfAttribute, String newValueAttribute){
        ReflectionKit.updateAnnotationClassValue(cl, javax.persistence.Entity.class, nameOfAttribute, newValueAttribute);
    }
    @Override
    public void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.Column.class,nameField, nameOfAttribute, newValueAttribute);
    }
    @Override
    public void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.JoinColumn.class,nameField, nameOfAttribute, newValueAttribute);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


}
