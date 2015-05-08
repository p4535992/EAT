package object.impl.hibernate.generic;

import extractor.hibernate.finder.FinderArgumentTypeFactory;
import extractor.hibernate.finder.FinderExecutor;
import extractor.hibernate.finder.FinderNamingStrategy;
import extractor.hibernate.finder.impl.SimpleFinderArgumentTypeFactory;
import extractor.hibernate.finder.impl.SimpleFinderNamingStrategy;
import object.dao.hibernate.generic.IGenericHibernateDao;
import object.model.GeoDocument;
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import p4535992.util.file.FileUtil;
import p4535992.util.log.SystemLog;
import p4535992.util.reflection.ReflectionKit;
import p4535992.util.string.StringKit;
import spring.bean.BeansKit;

import java.io.File;
import java.io.FileNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Marco on 23/04/2015.
 */
public class GenericHibernateDaoImpl<T> implements IGenericHibernateDao<T>,FinderExecutor {

    //BASIC FIELD
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenericHibernateDaoImpl.class);

    protected Class<T> cl;
    protected String clName;
    protected String query;
    //HIBERNATE FIELD

    protected String myInsertTable,mySelectTable;
    protected DataSource dataSource;
    @Autowired
    protected org.hibernate.SessionFactory sessionFactory;
    protected org.hibernate.Session session;


    //SPRING FIELD
    protected org.springframework.orm.hibernate4.HibernateTemplate hibernateTemplate;
    protected org.springframework.orm.hibernate4.LocalSessionFactoryBuilder sessionBuilder;
    protected org.springframework.orm.hibernate4.SessionHolder sessionHolder;
    protected DriverManagerDataSource driverManag;
    @PersistenceContext
    protected EntityManager em;
    protected org.springframework.context.ApplicationContext context;
    protected String beanIdSessionFactory;
    protected String beanIdSpringContext;


    protected File contextFile;

    //CONSTRUCTOR
    public GenericHibernateDaoImpl() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

   public GenericHibernateDaoImpl(Object s) throws FileNotFoundException {
        //super(s); //extend test case????
       if(context==null){
           loadSpringContext(contextFile.getAbsolutePath());
       }else{
           //..do nothing
       }

    }

    //GETTER AND SETTRE BASE
    public String getBeanIdSpringContext() {
        return beanIdSpringContext;
    }

    public void setBeanIdSpringContext(String beanIdSpringContext) {
        this.beanIdSpringContext = beanIdSpringContext;
    }

    public String getBeanIdSessionFactory() {
        return beanIdSessionFactory;
    }

    public void setBeanIdSessionFactory(String beanIdSessionFactory) {
        this.beanIdSessionFactory = beanIdSessionFactory;
    }

    public ApplicationContext getContextFile() {
        return context;
    }

    public void setContextFile(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void setTableInsert(String nameOfTable) {this.myInsertTable= nameOfTable;}

    @Override
    public void setTableSelect(String nameOfTable) {this.mySelectTable= nameOfTable;}

    @Override
    public Session getSession() {
        session =  sessionFactory.getCurrentSession();
        return session;
    }


    @Override
    public void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database) {
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.sql.jdbc.Driver"
        driverManag.setUrl("" + typeDb + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        driverManag.setUsername(user);
        driverManag.setPassword(pass);
        this.dataSource = driverManag;
    }

    @Override
    public void setDataSource(DataSource ds) { this.dataSource= ds;}

    @Override
    public void setHibernateTemplate(org.springframework.orm.hibernate4.HibernateTemplate hibernateTemplate) {this.hibernateTemplate = hibernateTemplate;}

    @Override
    public void setNewHibernateTemplate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new org.springframework.orm.hibernate4.HibernateTemplate(getSessionFactory());
    }

   @Override
   public void loadSpringContext(String filePathXml){
        try {
            context = BeansKit.tryGetContextSpring(filePathXml);
        }catch (Exception e){
            SystemLog.exception(e);
        }
   }

    @Override
    public org.hibernate.SessionFactory getSessionFactory() {
        if(context==null && StringKit.setNullForEmptyString(beanIdSessionFactory) == null){
            return this.sessionFactory;
        }else {
            //return (SessionFactory) context.getBean("sessionFactory");
            return (org.hibernate.SessionFactory) context.getBean(beanIdSessionFactory);
        }
    }
    /**
     * Method needed for the configuration bean file (context file)
     */
    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
            this.sessionFactory = sessionFactory;
    }

    @Override
    public void setSessionFactory(DataSource dataSource) {
        sessionBuilder = new org.springframework.orm.hibernate4.LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addAnnotatedClasses(GeoDocument.class);
        this.sessionFactory = sessionBuilder.buildSessionFactory();
    }

    @Override
    public void setSessionFactory() {
        sessionBuilder = new org.springframework.orm.hibernate4.LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addAnnotatedClasses(GeoDocument.class);
        this.sessionFactory = sessionBuilder.buildSessionFactory();
    }


    @Override
    public void updateAnnotationTable(String nameOfAttribute, String newValueAttribute){
        ReflectionKit.updateAnnotationClassValue(cl, javax.persistence.Entity.class, nameOfAttribute, newValueAttribute);
    }
    @Override
    public void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute){
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.Column.class, nameField, nameOfAttribute, newValueAttribute);
    }
    @Override
    public void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.JoinColumn.class,nameField, nameOfAttribute, newValueAttribute);
    }

    @Override
    public javax.sql.DataSource getDataSource() {
        if(dataSource==null) {
            return org.springframework.orm.hibernate4.SessionFactoryUtils.getDataSource(sessionFactory);
        }else {
            return this.dataSource;
        }
    }


    @Override
    public void shutdown()
    {
        closeSession();
    }

    @Override
    public void openSession()
    {
        sessionFactory = getSessionFactory();
        session = getSessionFactory().openSession();
        org.springframework.transaction.support.TransactionSynchronizationManager.bindResource(sessionFactory,
                new org.springframework.orm.hibernate4.SessionHolder(session));
    }

    @Override
    public void closeSession()
    {
        sessionFactory = getSessionFactory();
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
    public void restartSession()
    {
        closeSession();
        openSession();
    }

    public Object create(T o)
    {
        return getSession().save(o);
    }


    //NOTE THE FOLLOW METHOD NEED THE SPRINGFRAMEWORK LIBRARY
    // Hibernate implementation of GenericDao
    // A typesafe implementation of CRUD and finder methods based on Hibernate and Spring AOP
    // The finders are implemented through the executeFinder method. Normally called by the FinderIntroductionInterceptor
    private FinderNamingStrategy namingStrategy = new SimpleFinderNamingStrategy(); // Default. Can override in config
    private FinderArgumentTypeFactory argumentTypeFactory = new SimpleFinderArgumentTypeFactory(); // Default. Can override in config
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

    protected  org.hibernate.Query prepareQuery(java.lang.reflect.Method method, Object[] queryArgs)
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

    protected  void setPositionalParams(Object[] queryArgs,  org.hibernate.Query namedQuery)
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

    protected  void setNamedParams(String[] namedParameters, Object[] queryArgs,  org.hibernate.Query namedQuery)
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

    //METHOD FOR CRUD OPERATION WITH HIBERNATE
    @Override
    public void insert(T object) {
        if(hibernateTemplate!=null) {
            hibernateTemplate.save(object);
        }else if(session !=null){
            session.save(object);
        }
    }

    @Override
    public Object insertAndReturn(T newInstance) {
        return null;
    }

    @Override
    public List<T> select(String nameColumn, int limit, int offset) {
        return null;
    }

    @Override
     public void update(T object) {
        if(hibernateTemplate!=null) {
           hibernateTemplate.update(object);
        }else if(session !=null){
          session.update(object);
        }
     }

    @Override
    public void delete(T object) {
        hibernateTemplate.delete(object);
    }

    @Override
    public List<T> selectAll(Serializable serial) {
        List<T> list = new ArrayList<>();
        if(hibernateTemplate!=null) {
            list = hibernateTemplate.loadAll(cl);
        }else if(session != null && session!=null){
            session.load(cl,serial);
        }
        return list;
    }

    @Override
    public T select(Serializable serial){
        session = getCurrentSession();

        return (T)session.load(cl,serial);
    }

    @Override
    public int getCount() {
        return 0;
    }


//    @Override
//    public T getHByColumn(String column) {
//        // GeoDocument g = hibernateTemplate.get(GeoDocument.class,column);
//        final Class cla = cl;
//        T g = (T) hibernateTemplate.get(cla,column);
//        return g;
//    }

    //method to return one of given id
//    @Override
//    public InfoDocument  getHByColumn(String column){
//        InfoDocument g = hibernateTemplate.get(InfoDocument.class,column);
//        return g;
//    }



//    @Override
//    public List selectAllSimpleH(final String limit,final String offset) {
//        List<GeoDocument> docs = new ArrayList<GeoDocument>();
//        //METHOD 1 (Boring)
//        /*
//        Query q = getHibernateTemplate().getSession().createQuery("from User");
//        q.setFirstResult(0); // modify this to adjust paging
//        q.setMaxResults(limit);
//        return (List<User>) q.list();
//        */
//        if(limit != null && offset != null) {
//            //METHOD 2 (Probably the best)
//            docs =
//                    (List<GeoDocument>) hibernateTemplate.execute(new HibernateCallback() {
//                        public Object doInHibernate(Session session) throws HibernateException {
//                            Query query = session.createQuery("from " + mySelectTable + "");
//                            query.setFirstResult(Integer.parseInt(offset));
//                            query.setMaxResults(Integer.parseInt(limit));
//                            return (List<GeoDocument>) query.list();
//                        }
//                    });
//        }
//        return docs;
//    }

//    @Override
//    public List<Map<String, Object>>  getAll() {
//        return null;
//    }

    //method to return all
//    @Override
//    public List<T> getAllH(){return hibernateTemplate.loadAll(cl);}
//
//    //method to return all
//    @Override
//    @Transactional
//    public List<T>  selectAllH(){
//        @SuppressWarnings("unchecked")
//        List<T> list = (List<T>) sessionFactory.getCurrentSession()
//        .createCriteria(GeoDocument.class)
//        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
//        return list;
//    }







    // If you'd like to use HibernateTemplate you can do something like this:

    /*
    @SuppressWarnings("unchecked")
    public List<T> get(final int limit) {
        return hibernateTemplate.executeFind(new HibernateCallback<List<T>>() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                return session.createCriteria(cl).setMaxResults(limit).list();
            }
        });
    }
    */

    /*
    @SuppressWarnings("unchecked")
    public List<T> find(final int limit) {
        return hibernateTemplate.executeFind(new HibernateCallback<List<T>>() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                return session.createQuery("FROM "+mySelectTable+"").setMaxResults(limit).list();
            }
        });
    }
    */
    @Override
    public <T> T getDao(String beanIdNAme)
    {
        T dao = (T) context.getBean(beanIdNAme);
        return dao;
    }

}
