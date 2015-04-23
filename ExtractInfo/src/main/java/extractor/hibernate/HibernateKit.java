package extractor.hibernate;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import java.io.File;
import java.net.URL;
import java.util.List;
import org.hibernate.cfg.Configuration;

/**
 * LIttle Class for help with first steps to Hibernate
 * @author 4535992
 */
public class HibernateKit {

    public HibernateKit(){}
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HibernateKit.class);
    private SessionFactory sessionFactory;
    private Session session;
    private ServiceRegistry serviceRegistry;
    private Configuration configuration;

    private File PATH_CFG_HIBERNATE;

    private boolean cfgXML;

    public void setNewConfiguration(){
        //configuration = new Configuration();
        //URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(PATH_CFG_HIBERNATE.getAbsolutePath());
        //configuration.configure(urlStatic);
        if(cfgXML) {
            try {
                //You cna put the configuration file where you want bu you must pay attention
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
            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                    configuration.getProperties()).build();
        }else{
            throw new ExceptionInInitializerError("Try to set a ServiceRegistry without have configurate the Configuration");
        }
    }

    /**
     * Get the ServiceRegistry
     * @return serviceRegistry
     */
    protected ServiceRegistry getServiceRegistry(){
        return serviceRegistry;
    }

    /**
     * Get the  SessionFactory
     * @return sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Get the Session
     * @return session
     */
    public Session getSession(){
        return session;
    }

    /**Close caches and connection pool*/
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
    public void openSession() {
        session = sessionFactory.openSession();
    }

    /**
     * Returns a session from the session context. If there is no session in the context it opens a session,
     * stores it in the context and returns it.
     * This factory is intended to be used with a hibernate.cfg.xml
     * including the following property <property
     * name="current_session_context_class">thread</property> This would return
     * the current open session or if this does not exist, will insert a new session
     * @return the session
     */
    public Session getCurrentSession() {
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
            String DB_OUTPUT_HIBERNATE,
            String USER_HIBERNATE,
            String PASS_HIBERNATE,
            String DRIVER_DATABASE,
            String DIALECT_DATABASE,
            String DIALECT_DATABASE_HIBERNATE,
            String HOST_DATABASE,
            String PORT_DATABASE,
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
     * @param LIST_RESOURCE_XML list of the XML file resource
     */
    public void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,
            String USER_HIBERNATE,
            String PASS_HIBERNATE,
            String DRIVER_DATABASE,
            String DIALECT_DATABASE,
            String DIALECT_DATABASE_HIBERNATE,
            String HOST_DATABASE,
            String PORT_DATABASE,
            List<Class> LIST_ANNOTATED_CLASS,
            List<String> LIST_RESOURCE_XML
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
            for(String resource : LIST_RESOURCE_XML){
                configuration.addResource(resource);
            }
            buildSessionFactory();
        }catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }



    






}//end of the class



