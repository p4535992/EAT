package extractor.hibernate;
import extractor.hibernate.finder.FinderArgumentTypeFactory;
import extractor.hibernate.finder.FinderExecutor;
import extractor.hibernate.finder.FinderNamingStrategy;
import extractor.hibernate.finder.impl.SimpleFinderArgumentTypeFactory;
import extractor.hibernate.finder.impl.SimpleFinderNamingStrategy;
import object.dao.hibernate.generic.IGenericHibernateDao;
import object.model.GeoDocument;
import org.hibernate.criterion.Criterion;
import p4535992.util.log.SystemLog;
import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import p4535992.util.reflection.ReflectionKit;

/**
 * LIttle Class for help with first steps to Hibernate
 * @author 4535992
 */
public class Hibernate4Kit<T> implements IGenericHibernateDao<T>{

    private static Hibernate4Kit instance;
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Hibernate4Kit.class);
    protected  String myInsertTable,mySelectTable,myUpdateTable;
    protected  org.hibernate.SessionFactory sessionFactory;
    protected  org.hibernate.Session session;
    protected  org.hibernate.service.ServiceRegistry serviceRegistry;
    protected  org.hibernate.cfg.Configuration configuration;
    protected  File PATH_CFG_HIBERNATE;
    protected  boolean cfgXML;
    protected  org.hibernate.Criteria criteria,specificCriteria;
    protected  org.hibernate.Transaction trns;
    protected  org.hibernate.SQLQuery SQLQuery;
    protected  org.hibernate.Query query;
    protected  Class<T> cl;
    protected  String clName,sql;


    //CONSTRUCTOR

    public static Hibernate4Kit newInstance() {
        if (instance == null) {
            instance = new Hibernate4Kit();
        }
        return instance;
    }

    public static Hibernate4Kit newInstance(
            org.hibernate.Session session,org.hibernate.SessionFactory sessionFactory) {
        if (instance == null) {
            instance = new Hibernate4Kit(session,sessionFactory);
        }
        return instance;
    }

    public Hibernate4Kit(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    public Hibernate4Kit(Class<T> cl){
        this.cl = cl;
        this.clName = cl.getSimpleName();
    }

    public Hibernate4Kit(
            org.hibernate.Session session,org.hibernate.SessionFactory sessionFactory){
        this.session = session;
        this.sessionFactory = sessionFactory;
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    /***
     * Method for try into o many ways to set a configuration objct from a file
     */
    private void setNewConfiguration(){

        //configuration = new Configuration();
        //URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(PATH_CFG_HIBERNATE.getAbsolutePath());
        //configuration.configure(urlStatic);
        SystemLog.hibernate("Try to set a new configuration...");
        if(cfgXML) {
            try {
                //You can put the configuration file where you want but you must pay attention
                //where you put the java class with jpa annotation
                //Web-project -> WEB-INF/pojo.hbm.xml
                //Maven-project -> resources/pojo.hbm.xml ->
                //THis piece of code can be better
                configuration=  new org.hibernate.cfg.Configuration().configure(PATH_CFG_HIBERNATE);
            }catch(Exception ex6){
                try {
                    configuration = new org.hibernate.cfg.Configuration().configure(PATH_CFG_HIBERNATE.getAbsolutePath()); //work on Netbeans
                } catch (Exception ex) {
                    try {
                        configuration = new org.hibernate.cfg.Configuration().configure(PATH_CFG_HIBERNATE.getCanonicalPath());
                    } catch (Exception ex3) {
                        try {
                            configuration = new org.hibernate.cfg.Configuration().configure(PATH_CFG_HIBERNATE.getPath());
                        } catch (Exception ex4) {
                            try {
                                configuration = new org.hibernate.cfg.Configuration().configure(PATH_CFG_HIBERNATE.getAbsoluteFile());
                            }catch(Exception ex9){
                                SystemLog.warning("...failed to load the configuration file to the path:"+PATH_CFG_HIBERNATE.getAbsolutePath());
                                SystemLog.exception(ex9);
                            }
                        }
                    }
                }
            }
        }else{
            try {
                /**deprecated in Hibernate 4.3*/
                //configuration = new AnnotationConfiguration();
                configuration = new org.hibernate.cfg.Configuration().configure();
            }catch(org.hibernate.HibernateException e){
                SystemLog.exception(e);
            }
        }
    }

    /**Set the Service Registry*/
    @Override
    public void setNewServiceRegistry() {
        /**deprecated in Hibernate 4.3*/
        //serviceRegistry = new ServiceRegistryBuilder().applySettings(
        //        configuration.getProperties()). buildServiceRegistry();
        if(configuration != null) {
            serviceRegistry = new org.hibernate.boot.registry.StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
        }else{
           SystemLog.warning("Try to set a ServiceRegistry without have configurate the Configuration");
        }
    }

    /**
     * Get the ServiceRegistry
     * @return serviceRegistry
     */
    public org.hibernate.service.ServiceRegistry getServiceRegistry(){
        return serviceRegistry;
    }

    /**
     * Get the mapping of the selected class
     * @param entityClass
     * @return
     */
    public org.hibernate.mapping.PersistentClass getClassMapping(Class entityClass){
        return configuration.getClassMapping(entityClass.getName());
    }

    /**
     * Get the  SessionFactory
     * @return sessionFactory
     */
    @Override
    public org.hibernate.SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void setSessionFactory(org.hibernate.SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Get the Session
     * @return session
     */
    @Override
    public org.hibernate.Session getSession(){
        return session;
    }

    /**
     * Set the Session
     * @param session
     */
    @Override
    public void setSession(org.hibernate.Session session){
        this.session = session;
    }

    /**Close caches and connection pool*/
    @Override
    public void shutdown() {
        SystemLog.hibernate("try to closing session ... ");
        if (getCurrentSession() != null) {
            getCurrentSession().flush();
            session.flush();
            if (session.isOpen()) {
                session.close();
                getCurrentSession().close();
            }
        }
        SystemLog.hibernate("...session is closed! try to close the SessionFactory ... ");
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        SystemLog.hibernate("... the SessionFactory is closed!");
        this.configuration = null;
        this.sessionFactory = null;
        this.session = null;
    }

    /**
     * Method for suppor the reset of specific parameter of the class
     */
    private void reset(){
       /* if (getCurrentSession() != null) {
            getCurrentSession().flush();
            if (getCurrentSession().isOpen()) {
                getCurrentSession().close();
            }
        }*/
        if (session != null) {
            session.flush();
            if(session.isOpen()) {
                session.close();
            }
        }
        criteria = null;
        specificCriteria = null;
    }

    /**
     * Opens a session and will not bind it to a session context
     * @return
     */
    @Override
    public void openSession() {
        session = sessionFactory.openSession();
    }

    /**
     * Close a Session
     */
    @Override
    public void closeSession() {
        session.close();
    }

    /**
     * Close and Open a Session
     */
    @Override
    public void restartSession() {
        openSession();
        closeSession();
    }

    /**
     * Returns a session from the session context. If there is no session in the context it opens a session,
     * stores it in the context and returns it.
     * This context is intended to be used with a hibernate.cfg.xml including the following property
     * <property name="current_session_context_class">thread</property>
     * This would return the current open session or if this does not exist, will insert a new session
     * @return the session
     */
    @Override
    public org.hibernate.Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Method to build the sessionFactory for Hibernate froma configuration file or from
     * a java code.
     */
    private void buildSessionFactory() {
        try {
            if(PATH_CFG_HIBERNATE !=null) {
                cfgXML = true;
                setNewConfiguration();//new Configuration
                setNewServiceRegistry(); //new ServiceRegistry
                /**deprecated Hibernate 4.0, 4.1, 4.2*/
                //sessionFactory = configuration.configure().buildSessionFactory();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            }else{
                setNewConfiguration();//new Configuration
                setNewServiceRegistry(); //new ServiceRegistry
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            }
        } catch (Throwable ex) {
            SystemLog.warning("Initial SessionFactory creation failed.");
            SystemLog.throwException(ex);
        }
    }

    /**
     * Method to build a Session Factory on a remote configuration file XML
     * @param uri
     * @note NOT WORK NEED UPDATE
     */
    public void buildSessionFactory(URL uri) {
        try {
            if(uri != null){
                //URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(cfgFile.getAbsolutePath());
                PATH_CFG_HIBERNATE = new File(uri.toString());
                buildSessionFactory();
            }
        } catch (Throwable ex) {
           SystemLog.throwException(ex);
        }
    }

    /**
     * Method to build a Session Factory on a local configuration file XML
     * @param filePath
     */
     public void buildSessionFactory(String filePath) {
           try {
               File cfgFile = new File(filePath);                   
               if(cfgFile != null && cfgFile.exists()){
                   PATH_CFG_HIBERNATE = cfgFile;
                   buildSessionFactory();
               }                    
           } catch (Throwable ex) {
                   SystemLog.throwException(ex);
           }
    }

    /**
     * Method to build a Session Factory on a local configuration file XML
     * @param cfgFile
     */
    public void buildSessionFactory(File cfgFile) {
        try {
            if(cfgFile != null && cfgFile.exists()){
                PATH_CFG_HIBERNATE = cfgFile;
                buildSessionFactory();
            }
        } catch (Throwable ex) {
            SystemLog.throwException(ex);
        }
    }

    /**
     * Method to build a Session Factory with code and the annotation class
     * @param DB_OUTPUT_HIBERNATE name database
     * @param USER_HIBERNATE user name database
     * @param PASS_HIBERNATE password database
     * @param DRIVER_DATABASE driver for database
     * @param DIALECT_DATABASE dialect for database
     * @param DIALECT_DATABASE_HIBERNATE specific HSQL dialect for database
     * @param HOST_DATABASE host database
     * @param PORT_DATABASE port database
     * @param LIST_ANNOTATED_CLASS list of all annotated classes
     */
    public void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,String USER_HIBERNATE,String PASS_HIBERNATE,String DRIVER_DATABASE,
            String DIALECT_DATABASE,String DIALECT_DATABASE_HIBERNATE,String HOST_DATABASE,String PORT_DATABASE,
            List<Class> LIST_ANNOTATED_CLASS
    ) {
        cfgXML = false;
        setNewConfiguration();
        try {
            configuration
            //DATABASE PARAMETER
            .setProperty("hibernate.dialect", DIALECT_DATABASE_HIBERNATE)
            .setProperty("hibernate.connection.driver_class", DRIVER_DATABASE)
            .setProperty("hibernate.connection.url", "" + DIALECT_DATABASE + "://" + HOST_DATABASE + ":" + PORT_DATABASE + "/" + DB_OUTPUT_HIBERNATE + "")
            .setProperty("hibernate.connection.username", USER_HIBERNATE)
            .setProperty("hibernate.connection.password", PASS_HIBERNATE)

            //DEFAULT PARAMETER
            //.setProperty("connection.pool_size", "1")
            //.setProperty("current_session_context_class", "thread")
            //.setProperty("cache.provider_class", "org.hibernate.cache.NoCacheProvider")
            .setProperty("hibernate.show_sql", "true")

            //.setProperty("hibernate.hbm2ddl.auto","update")
            //.setProperty("hibernate.format_sql","true")
            //.setProperty("hibernate.hbm2ddl.auto","insert-drop")

            //OTHER PROPERTIES
            /*
            .setProperty("hibernate.c3p0.acquire_increment","1")
            .setProperty("hibernate.c3p0.idle_test_period","100")
            .setProperty("hibernate.c3p0.maxIdleTime","300")
            .setProperty("hibernate.c3p0.max_size","75")
            .setProperty("hibernate.c3p0.max_statements","0")
            .setProperty("hibernate.c3p0.min_size","20")
            .setProperty("hibernate.c3p0.timeout","180")
            .setProperty("hibernate.cache.user_query_cache","true")
            */
            ;
            //ADD ANNOTATED CLASS
            for (Class cls : LIST_ANNOTATED_CLASS) {
                configuration.addAnnotatedClass(cls);
            }
            buildSessionFactory();
        } catch (Throwable ex) {
            SystemLog.throwException(ex);
        }
    }//buildSessionFactory


    /**
     * Method to build a Session Factory with code and the annotation class
     * @param DB_OUTPUT_HIBERNATE name database
     * @param USER_HIBERNATE user name database
     * @param PASS_HIBERNATE password database
     * @param DRIVER_DATABASE driver for database
     * @param DIALECT_DATABASE dialect for database
     * @param DIALECT_DATABASE_HIBERNATE specific HSQL dialect for database
     * @param HOST_DATABASE host database
     * @param PORT_DATABASE port database
     * @param LIST_ANNOTATED_CLASS list of all annotated classes
     * @param LIST_RESOURCE_XML list of the XML file resource
     */
    public void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,String USER_HIBERNATE,String PASS_HIBERNATE,String DRIVER_DATABASE,
            String DIALECT_DATABASE,String DIALECT_DATABASE_HIBERNATE, String HOST_DATABASE,String PORT_DATABASE,
            List<Class> LIST_ANNOTATED_CLASS,List<File> LIST_RESOURCE_XML
    ){
        cfgXML = false;
        setNewConfiguration();
        try {
            configuration
            //DATABASE PARAMETER
            .setProperty("hibernate.dialect", DIALECT_DATABASE_HIBERNATE)
            .setProperty("hibernate.connection.driver_class", DRIVER_DATABASE)
            .setProperty("hibernate.connection.url", "" + DIALECT_DATABASE + "://" + HOST_DATABASE + ":" + PORT_DATABASE + "/" + DB_OUTPUT_HIBERNATE + "")
            .setProperty("hibernate.connection.username", USER_HIBERNATE)
            .setProperty("hibernate.connection.password", PASS_HIBERNATE)

            //DEFAULT PARAMETER
            //.setProperty("connection.pool_size", "1")
            //.setProperty("current_session_context_class", "thread")
            //.setProperty("cache.provider_class", "org.hibernate.cache.NoCacheProvider")
            .setProperty("hibernate.show_sql", "true")

            //.setProperty("hibernate.hbm2ddl.auto","update")
            //.setProperty("hibernate.format_sql","true")
            //.setProperty("hibernate.hbm2ddl.auto","insert-drop")

            //OTHER PROPERTIES
            /*
            .setProperty("hibernate.c3p0.acquire_increment","1")
            .setProperty("hibernate.c3p0.idle_test_period","100")
            .setProperty("hibernate.c3p0.maxIdleTime","300")
            .setProperty("hibernate.c3p0.max_size","75")
            .setProperty("hibernate.c3p0.max_statements","0")
            .setProperty("hibernate.c3p0.min_size","20")
            .setProperty("hibernate.c3p0.timeout","180")
            .setProperty("hibernate.cache.user_query_cache","true")
            */
            ;
            //ADD ANNOTATED CLASS
            for(Class cls : LIST_ANNOTATED_CLASS){
                configuration.addAnnotatedClass(cls);
            }
            //Specifying the mapping files directly
            for(File resource : LIST_RESOURCE_XML){
                configuration.addResource(resource.getAbsolutePath());
            }
            buildSessionFactory();
        }catch (Throwable ex) {
            SystemLog.throwException(ex);
        }
    }//buildSessionFactory


    //CRUD OPERATION HIBERNATE

    @Override
    @javax.transaction.Transactional
    public Serializable insertRow(T object) {
        Serializable id = null;
        try {
            openSession();
            trns = session.beginTransaction();
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Insert the item:" + object);
            id = session.getIdentifier(object);
            SystemLog.message("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return id;
    }

    @Override
    public T selectRow(Serializable id){
        T object = null;
        try {
            openSession();
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            //WORK
            criteria.add(org.hibernate.criterion.Restrictions.eq("doc_id",id));
            List<T> results = criteria.list();
            SystemLog.message("[HIBERNATE] Select the item:" + results.get(0));
            //NOT WORK
            //object = (T) criteria.setFirstResult((Integer) id);
            //SystemLog.message("[HIBERNATE] Select the item:" + object.toString());
            object = (T) session.load(cl, id);
            SystemLog.message("[HIBERNATE] Select the item:" + object.toString());
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return object;
    }

    @Override
    @javax.transaction.Transactional
    public List<T> selectRows() {
        List<T> listT = new ArrayList<>();
        try {
            openSession();
            trns = session.beginTransaction();
            if(specificCriteria==null){
                criteria = session.createCriteria(cl);
            }else{
                criteria =specificCriteria;
            }
            listT = criteria.list();
            if(listT.size() == 0){
                SystemLog.warning("[HIBERNATE] The returned list is empty!1");
            }
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return listT;
    }

    @Override
    @javax.transaction.Transactional
    public List<T> selectRows(String nameColumn,int limit,int offset) {
        List<T> listT = new ArrayList<>();
        try {
            openSession();
            sql = "SELECT "+nameColumn+" FROM "+mySelectTable+"";
            SQLQuery = session.createSQLQuery(sql);
            query.setFirstResult(offset);
            query.setMaxResults(limit);
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            listT = query.list();
            if(listT.size() == 0){
                SystemLog.warning("[HIBERNATE] The returned list is empty!1");
            }
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return listT;
    }

    @Override
    @javax.transaction.Transactional
    public int getCount() {
        Object result = null;
        try {
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.setProjection(org.hibernate.criterion.Projections.rowCount());
            result = criteria.uniqueResult();
            SystemLog.message("[HIBERNATE] The count of employees is :" + result);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return (int)result;
    }

    @Override
    @javax.transaction.Transactional
    public Serializable updateRow(String whereColumn, Object whereValue) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            //t.setName("Abigale");
            //t = object;
            session.saveOrUpdate(t);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Update the item:" + t.toString());
            id = session.getIdentifier(t);
            SystemLog.message("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return id;
    }


    @Override
    @javax.transaction.Transactional
    public Serializable updateRow(T object) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            session.saveOrUpdate(object);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Update the item:" + object.toString());
            id = session.getIdentifier(object);
            SystemLog.message("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return id;
    }

    @Override
    @javax.transaction.Transactional
    public Serializable deleteRow(String whereColumn, Object whereValue) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            session.delete(t);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Delete the item:" + t);
            id = session.getIdentifier(t);
            SystemLog.message("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return id;
    }

    @Override
    @javax.transaction.Transactional
    public Serializable deleteRow(T object) {
        Serializable id = null;
        try{
            openSession();
            trns = session.beginTransaction();
            //session.beginTransaction();
            session.delete(object);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Delete the item:" + object);
            id = session.getIdentifier(object);
            SystemLog.message("[HIBERNATE] Get the identifier:" + id);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            reset();
        }
        return id;
    }

    //METHOD FOR MODIFY THE ANNOTATION OF HIBERNATE IN RUNTIME

    @Override
    public void updateAnnotationEntity(String nameOfAttribute, String newValueAttribute) {
        ReflectionKit.updateAnnotationClassValue(cl, javax.persistence.Entity.class, nameOfAttribute, newValueAttribute);
    }

    @Override
    public void updateAnnotationTable(String nameOfAttribute, String newValueAttribute){
        ReflectionKit.updateAnnotationClassValue(cl, javax.persistence.Table.class, nameOfAttribute, newValueAttribute);
    }
    @Override
    public void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.Column.class, nameField, nameOfAttribute, newValueAttribute);
    }
    @Override
    public void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.JoinColumn.class, nameField, nameOfAttribute, newValueAttribute);
    }

    @Override
    public List<Object[]> getAnnotationTable() {
        Annotation ann = GeoDocument.class.getAnnotation(javax.persistence.Table.class);
        return ReflectionKit.getAnnotationClass(ann);
    }

    //SUPPORT METHOD FOR THE CRITERIA
    @Override
    public void setNewCriteria(Criterion criterion){
        specificCriteria = session.createCriteria(cl);
        this.specificCriteria.add(criterion);
    }







}//end of the class

