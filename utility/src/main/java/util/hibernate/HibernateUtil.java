/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.hibernate;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import util.SystemLogUtility;

/**
 *
 * @author Marco
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    public static void buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            //METODO 1
            //Configuration cfg = new Configuration();
	    //cfg.configure("hibernate.cfg.xml");
            //METODO 2
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you SystemLog the exception, as it might be swallowed
            SystemLogUtility.message("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static void buildSessionFactory(File cfgFile,URL url) {
		try {		
                    if(cfgFile != null){
                        /*
                        SessionFactory sessionFactory = new Configuration().configure(cfgFile).buildSessionFactory();
                        Configuration cfg = HibernateUtil.ChangeDatabase("localhost",database,username,password);                       
                        URL url = Thread.currentThread().getContextClassLoader().getResource("/home/utils/hibernate/cfg/keyworddb/hibernate.cfg.xml");                    
                        SessionFactory factory = cfg.buildSessionFactory();    
                        Configuration  configuration = new Configuration().configure(cfgFile.getAbsoluteFile()).addResource("/home/utils/hibernate/cfg/keyworddb/Document.hbm.xml");
                        Configuration  configuration = new Configuration().configure(cfgFile.getAbsoluteFile());
                        SessionFactory factory = configuration.buildSessionFactory();
                        sessionFactory = configuration.buildSessionFactory();
                        */
                    }
                    if(url != null){
                         URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(cfgFile.getAbsolutePath());                    
                         sessionFactory =  new Configuration().configure(urlStatic).buildSessionFactory();
                    }
			//return sessionFactory;
 
		} catch (Throwable ex) {
			// Make sure you SystemLog the exception, as it might be swallowed
            SystemLogUtility.error("ERROR: Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
    
    public static void buildSessionFactory(String filePath,URL url) {
           try {
               File cfgFile = new File(filePath);                   
               if(cfgFile != null){
                   
                   Configuration  configuration = new Configuration().configure(cfgFile.getAbsoluteFile());
                   //SessionFactory factory = configuration.buildSessionFactory();
                   sessionFactory = configuration.buildSessionFactory();
               }
               if(url != null){
                    URL urlStatic = Thread.currentThread().getContextClassLoader().getResource(cfgFile.getAbsolutePath());                    
                    sessionFactory =  new Configuration().configure(urlStatic).buildSessionFactory();
               }                  
           } catch (Throwable ex) {
                   // Make sure you SystemLog the exception, as it might be swallowed
                   SystemLogUtility.error("ERROR: Initial SessionFactory creation failed." + ex);
                   throw new ExceptionInInitializerError(ex);
           }
    }
    
     public static void buildSessionFactory(String filePath) {
           try {
               File cfgFile = new File(filePath);                   
               if(cfgFile != null){
                   
                   Configuration  configuration = new Configuration().configure(cfgFile.getAbsoluteFile());
                   //SessionFactory factory = configuration.buildSessionFactory();
                   sessionFactory = configuration.buildSessionFactory();
               }                    
           } catch (Throwable ex) {
                   // Make sure you SystemLog the exception, as it might be swallowed
                   SystemLogUtility.error("ERROR: Initial SessionFactory creation failed." + ex);
                   throw new ExceptionInInitializerError(ex);
           }
    }
     
    public static void buildSessionFactory(File cfgFile) {
           try {
               //File cfgFile = new File(filePath);                   
               if(cfgFile != null){                 
                   Configuration  configuration = new Configuration().configure(cfgFile.getAbsoluteFile());
                   //SessionFactory factory = configuration.buildSessionFactory();
                   sessionFactory = configuration.buildSessionFactory();
               }                    
           } catch (Throwable ex) {
                   // Make sure you SystemLog the exception, as it might be swallowed
                   SystemLogUtility.error("ERROR: Initial SessionFactory creation failed." + ex);
                   throw new ExceptionInInitializerError(ex);
           }
    }
     
    public static void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,
            String USER_HIBERNATE,
            String PASS_HIBERNATE,
            String DRIVER_DATABASE,
            String DIALECT_DATABASE,
            String DIALECT_DATABASE_HIBERNATE,
            String HOST_DATABASE,
            String PORT_DATABASE,
            List<Class> LIST_ANNOTATED_CLASS
        ){
            // Create the SessionFactory from hibernate.cfg.xml
            //ATTENTO LOAD FOR DEFAULT LOAD THE FILE hibernate.cfg.xml 
           Configuration configuration = new Configuration(); 
          try {
            configuration=new AnnotationConfiguration()
            //DATABASE PARAMETER
            .setProperty("hibernate.dialect",DIALECT_DATABASE_HIBERNATE)
            .setProperty("hibernate.connection.driver_class",DRIVER_DATABASE)
            .setProperty("hibernate.connection.url",""+DIALECT_DATABASE+"://"+HOST_DATABASE+":"+PORT_DATABASE+"/"+DB_OUTPUT_HIBERNATE+"")
            .setProperty("hibernate.connection.username",USER_HIBERNATE)
            .setProperty("hibernate.connection.password",PASS_HIBERNATE)

            //DEFAULT PARAMETER
            //.setProperty("connection.pool_size", "1")
            //.setProperty("current_session_context_class", "thread")
            //.setProperty("cache.provider_class", "org.hibernate.cache.NoCacheProvider")
            .setProperty("hibernate.show_sql","true")

            //.setProperty("hibernate.hbm2ddl.auto","update")
            //.setProperty("hibernate.format_sql","true")
            //.setProperty("hibernate.hbm2ddl.auto","insert-drop")

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
            //<property name="hibernate.c3p0.testConnectionOnCheckout" value="true" />
            //.addClass(createNewClass(POJO_OUTPUT_HIBERNATE,"home.object_h"))
            //.addResource("home/object/utils/hibernate/cfg/urldb/Website.hbm.xml")
            /*
            .addAnnotatedClass(home.object_h.Document.class)
            .addAnnotatedClass(home.object_h.GeoDocument.class)
            .addAnnotatedClass(home.object_h.GeoDomainDocument.class)
            .addAnnotatedClass(home.object_h.InfoDocument.class)
            .addAnnotatedClass(home.object_h.Website.class);
                */
             ;
            for(Class cls : LIST_ANNOTATED_CLASS){
                configuration.addAnnotatedClass(cls);
            }
            // 
            //sessionFactory= new Configuration().configuration.buildSessionFactory();

            sessionFactory=configuration.buildSessionFactory();
          }catch (Throwable ex) {
              SystemLogUtility.error("ERROR: Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
          }
    }
    
   
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    /**
     * Close caches and connection pools
     */
    public static void shutdown() {
        getSessionFactory().close();
        sessionFactory.close();
        sessionFactory = null;
    }
    
   /**
    * Opens a session and will not bind it to a session context
    * @return the session
    */
    public Session openSession() {
        return sessionFactory.openSession();
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
    
    public static Class createNewClass(String annotatedClassName,String pathPackageToAnnotatedClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
        /*Then use the Class.newInstance() method if you never need to handle constructors with arguments, 
         or use Class.getConstructor(...) or Class.getConstructors() otherwise. (
         Call Constructor.newInstance() to invoke the constructor.)      
        String path= pathClass+"."+name;
	Object clso = Class.forName(path).getConstructor().newInstance(); //Se il construct non ha argomenti
        Class cls = clso.getClass();
        */
        //e.g. oracle.jdbc.driver.OracleDriver#sthash.4rtwgiWJ.dpuf
        Class cls = Class.forName(pathPackageToAnnotatedClass+"."+annotatedClassName).getConstructor().newInstance().getClass();
        /*
        //That will only work for a single string parameter of course, but you can modify it pretty easily.
        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor(String.class);
        Object object = ctor.newInstance(new Object[] { ctorArgument });//You can then cast this to your object.
        //Object object = cons.newInstance("MyAttributeValue");
	*/
	/*
        Class myClass = Class.forName("MyClass");
        Class[] types = {Double.TYPE, this.getClass()};
        Constructor constructor = myClass.getConstructor(types);
        //Object[] parameters = {new Double(0), this};
        //Object instanceOfMyClass = constructor.newInstance(parameters);
        Object instanceOfMyClass = constructor.newInstance(new Double(0), this);
        */
        //You can use reflection : Class.forName(className).getConstructor(String.class).newInstance(arg);
        SystemLogUtility.message("Create new Class Object con Name: " + cls.getName());
       return cls;
  }
    
   public static Class createNewClass(String pathPackageToAnnotatedClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{     
        //e.g. oracle.jdbc.driver.OracleDriver#sthash.4rtwgiWJ.dpuf
        Class cls = Class.forName(pathPackageToAnnotatedClass).getConstructor().newInstance().getClass();       
        //You can use reflection : Class.forName(className).getConstructor(String.class).newInstance(arg);
       SystemLogUtility.message("Create new Class Object con Name: " + cls.getName());
       //System.out.println(cls.getName());
       //System.out.println(cls.getSimpleName());
       return cls;
  }
   
  public static Constructor castObjectToSpecificConstructor(Class newClass){
      //get constructor that takes a String as argument
      //Constructor constructor = MyObject.class.getConstructor(String.class);
      //MyObject myObject = (MyObject)constructor.newInstance("constructor-arg1");
     
      Constructor constructor = null;
      try{
        //constructor constructor = newClass.getConstructor();
        //constructor constructor = newClass.getConstructor(new Class[]{});
        //Constructor constructor = aClass.getConstructor(new Class[]{String.class});
        constructor = newClass.getConstructor(new Class[]{});
      }catch(Exception e){
      }
      return constructor;
  }
  
  public static Object castObjectToSpecificClass(Class newClass,Object obj){
      //Object newObj = Class.forName(classname).cast(obj);
      //MyClass mobj = MyClass.class.cast(obj);
      Object obj2 = new Object();
      //Constructor constructor = null;
      try{
        obj2 = newClass.cast(obj);
        //constructor = newClass.getConstructor(new Class[]{});
      }catch(Exception e){
      }
      return obj2;
  }
  
  public static Object castObjectToSpecificObject(Object obj,String pathPackageToObjectAnnotated) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
      Class clsObjectAnnotated = HibernateUtil.createNewClass(pathPackageToObjectAnnotated);
      Constructor cons = HibernateUtil.castObjectToSpecificConstructor(clsObjectAnnotated);
      //System.out.println(cons.newInstance().toString());
      //System.out.println(cons.newInstance().getClass().cast(obj));
     
      obj = (Object)cons.newInstance();
     
      return obj;
  }
    
  
  //METODI DI SUPPORTO CON XMLUTIL
//  public static void setNewTable(File xmlFile,String newNameTable) throws TransformerException, IOException, SAXException {
//      //XMLUtil.updateValueOfAttribute(file, "class", "table", "xxx");
//      XMLKit.updateValueOfAttribute(xmlFile, "class", "table", newNameTable);
//      SystemLog.message("Settata una nuova tabella "+newNameTable+" nel file "+xmlFile.getAbsolutePath()+"");
//  }
  
//  public static void setNewTable(String xmlFilePath,String newNameTable) throws TransformerException, IOException, SAXException {
//      //XMLUtil.updateValueOfAttribute(file, "class", "table", "xxx");
//      File xmlFile = new File(xmlFilePath);
//      XMLKit.updateValueOfAttribute(xmlFile, "class", "table", newNameTable);
//      SystemLog.message("Settata una nuova tabella "+newNameTable+" nel file "+xmlFilePath+"");
//  }

  /*
  public static void setNewDatabase(
          String xmlFilePath,
          String DB_OUTPUT,String USER,String PASS,String DRIVER_DATABASE,String DIALECT_DATABASE,
          String DIALECT_DATABASE_HIBERNATE,String HOST_DATABASE,String PORT_DATABASE.toString(),String DB_OUTPUT
  ){
      File xmlFile = new File(xmlFilePath);
      //<property name="connection.driver_class">com.mysql.jdbc.Driver</property>
      XMLUtil.updateValueOfAttribute(xmlFile, "class", "table", newNameTable);
      //<property name="connection.url">jdbc:mysql://localhost/geolocationdb</property>
      XMLUtil.updateValueOfAttribute(xmlFile, "class", "table", newNameTable);
      //<property name="connection.username">siimobility</property>
      XMLUtil.updateValueOfAttribute(xmlFile, "class", "table", newNameTable);
      //<property name="connection.password">siimobility</property>
      XMLUtil.updateValueOfAttribute(xmlFile, "class", "table", newNameTable);
      //<property name="dialect">org.hibernate.dialect.MySQLDialect</property>
      XMLUtil.updateValueOfAttribute(xmlFile, "class", "table", newNameTable);
  }
  */
}