package extractor.hibernate;
import p4535992.util.log.SystemLog;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.cfg.Configuration;
import p4535992.util.reflection.ReflectionKit;

/**
 * LIttle Class for help with first steps to Hibernate
 * @author 4535992
 */
public abstract class Hibernate4Kit<T> implements IHibernateKit<T> {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Hibernate4Kit.class);
    protected  String myInsertTable,mySelectTable,myUpdateTable;
    protected  org.hibernate.SessionFactory sessionFactory;
    protected  org.hibernate.Session session;
    protected  org.hibernate.service.ServiceRegistry serviceRegistry;
    protected  Configuration configuration;
    protected  File PATH_CFG_HIBERNATE;
    protected  boolean cfgXML;
    protected  org.hibernate.Criteria criteria;
    protected  org.hibernate.Transaction trns;
    protected  org.hibernate.SQLQuery SQLQuery;
    protected  org.hibernate.Query query;
    protected  Class<T> cl;
    protected  String clName,sql;

    public Hibernate4Kit(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    public Hibernate4Kit(Class<T> cl){
        this.cl = cl;
    }


    public void setNewConfiguration(){
        //configuration = new Configuration();
        //URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(PATH_CFG_HIBERNATE.getAbsolutePath());
        //configuration.configure(urlStatic);
        if(cfgXML) {
            try {
                //You can put the configuration file where you want but you must pay attention
                //where you put the java class with jpa annotation
                //Web-project -> WEB-INF/pojo.hbm.xml
                //Maven-project -> resources/pojo.hbm.xml ->
                //THis piece of code can be better
                configuration=  new Configuration().configure(PATH_CFG_HIBERNATE);
            }catch(Exception ex6){
                try {
                    configuration = new Configuration().configure(PATH_CFG_HIBERNATE.getAbsolutePath()); //work on Netbeans
                } catch (Exception ex) {
                    try {
                        configuration = new Configuration().configure(PATH_CFG_HIBERNATE.getCanonicalPath());
                    } catch (Exception ex3) {
                        try {
                            configuration = new Configuration().configure(PATH_CFG_HIBERNATE.getPath());
                        } catch (Exception ex4) {
                            try {
                                configuration = new Configuration().configure(PATH_CFG_HIBERNATE.getAbsoluteFile());
                            }catch(Exception ex9){
                                throw new ExceptionInInitializerError("Failed to load the configurationfile");
                            }
                        }
                    }
                }
            }
        }else{
            /**deprecated in Hibernate 4.3*/
            //configuration = new AnnotationConfiguration();
            configuration = new Configuration().configure();
        }
    }

    /**
     * Get the configuration parameter
     * @return configuration
     */
    public Configuration getConfiguration(){
        return configuration;
    }

    /**Set the Service Registry*/
    public void setNewServiceRegistry() {
        /**deprecated in Hibernate 4.3*/
        //serviceRegistry = new ServiceRegistryBuilder().applySettings(
        //        configuration.getProperties()). buildServiceRegistry();
        if(configuration != null) {
            serviceRegistry = new org.hibernate.boot.registry.StandardServiceRegistryBuilder().applySettings(
                    configuration.getProperties()).build();
        }else{
            throw new ExceptionInInitializerError("Try to set a ServiceRegistry without have configurate the Configuration");
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
     * Get the  SessionFactory
     * @return sessionFactory
     */
    @Override
    public org.hibernate.SessionFactory getSessionFactory() {
        return sessionFactory;
    }

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

    /**Close caches and connection pool*/
    @Override
    public void shutdown() {
        session.close();
        sessionFactory.close();
        sessionFactory = null;
        session = null;
    }

    /**
     * Opens a session and will not bind it to a session context
     * @return
     */
    @Override
    public void openSession() {
        session = sessionFactory.openSession();
    }

    @Override
    public void closeSession() {
        session.close();
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


    public void buildSessionFactory() {
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
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed." + ex);
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
            throw new ExceptionInInitializerError(ex);
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
                   throw new ExceptionInInitializerError(ex);
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
            throw new ExceptionInInitializerError(ex);
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
            throw new ExceptionInInitializerError(ex);
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
            throw new ExceptionInInitializerError(ex);
        }
    }//buildSessionFactory

    @Override
    public void insert(T object) {
        try {
            openSession();
            trns = session.beginTransaction();
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Insert the item:" + object);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            session.flush();
            session.close();
        }
    }

    @Override
    public List<T> select() {
        List<T> listT = new ArrayList<>();
        try {
            openSession();
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            listT = criteria.list();
            if(listT.size() == 0){SystemLog.warning("[HIBERNATE] The returned list is empty!1");}
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            session.flush();
            session.close();
        }
        return listT;
    }

    @Override
    public List<T> select(String nameColumn,int limit,int offset) {
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
            if(listT.size() == 0){SystemLog.warning("[HIBERNATE] The returned list is empty!1");}
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            session.flush();
            session.close();
        }
        return listT;
    }

    @Override
    public int getCount() {
        Object result = null;
        try {
            openSession();
            trns = session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.setProjection(org.hibernate.criterion.Projections.rowCount());
            result = criteria.uniqueResult();
            SystemLog.message("[HIBERNATE] The count of employees is :" + result);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            session.flush();
            session.close();
        }
        return (int)result;
    }

    @Override
    public void update(T object, String whereColumn, Object whereValue) {
        try{
            openSession();
            trns = session.beginTransaction();
            session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            //t.setName("Abigale");
            t = object;
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Update the item:"+ object);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            session.flush();
            session.close();
        }
    }

    @Override
    public void delete(String whereColumn, Object whereValue) {
        try{
            openSession();
            trns = session.beginTransaction();
            session.beginTransaction();
            criteria = session.createCriteria(cl);
            criteria.add(org.hibernate.criterion.Restrictions.eq(whereColumn, whereValue));
            T t = (T)criteria.uniqueResult();
            session.delete(t);
            session.getTransaction().commit();
            SystemLog.message("[HIBERNATE] Delete the item:"+t);
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.exception(e);
        } finally {
            session.flush();
            session.close();
        }
    }

    @Override
    public void updateAnnotationTable(String nameOfAttribute, String newValueAttribute){
        ReflectionKit.updateAnnotationClassValue(cl,javax.persistence.Entity.class,nameOfAttribute,newValueAttribute);
    }
    @Override
    public void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.Column.class,nameField, nameOfAttribute, newValueAttribute);
    }
    @Override
    public void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.JoinColumn.class,nameField, nameOfAttribute, newValueAttribute);
    }






    //OTHER METHODS
    /*public static Class createNewClass(String annotatedClassName,String pathPackageToAnnotatedClass)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        Class cls = Class.forName(pathPackageToAnnotatedClass+"."+annotatedClassName).getConstructor().newInstance().getClass();
        //You can use reflection : Class.forName(className).getConstructor(String.class).newInstance(arg);
        SystemLog.message("Create new Class Object con Name: " + cls.getName());
        return cls;
    }

    public static Class createNewClass(String pathPackageToAnnotatedClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
        //e.g. oracle.jdbc.driver.OracleDriver#sthash.4rtwgiWJ.dpuf
        Class cls = Class.forName(pathPackageToAnnotatedClass).getConstructor().newInstance().getClass();
        SystemLog.message("Create new Class Object con Name: " + cls.getName());
        return cls;
    }

    public static Constructor castObjectToSpecificConstructor(Class newClass){
        Constructor constructor = null;
        try{
            constructor = newClass.getConstructor(new Class[]{});
        }catch(Exception e){
        }
        return constructor;
    }

    public static Object castObjectToSpecificClass(Class newClass,Object obj){
        Object obj2 = new Object();
        try{
            obj2 = newClass.cast(obj);
        }catch(Exception e){
        }
        return obj2;
    }

    public static Object castObjectToSpecificObject(Object obj,String pathPackageToObjectAnnotated)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
        Class clsObjectAnnotated = createNewClass(pathPackageToObjectAnnotated);
        Constructor cons = castObjectToSpecificConstructor(clsObjectAnnotated);
        obj = (Object)cons.newInstance();
        return obj;
    }*/
}//end of the class



