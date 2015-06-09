package spring;

import object.dao.jdbc.IGeoDocumentDao;
import object.impl.jdbc.GeoDocumentDaoImpl;
import object.model.GeoDocument;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;


//1. File system
//
//Resource resource = appContext.getResource("file:c:\\testing.txt");
//
//2. URL path
//
//Resource resource =
//appContext.getResource("url:http://www.yourdomain.com/testing.txt");
//
//3. Class path
//
//Resource resource =
//appContext.getResource("classpath:com/mkyong/common/testing.txt");

/**
 * Created by 4535992 on 03/04/2015.
 */
public class Test_Spring_Hibernate {
    public Test_Spring_Hibernate(){}
    public static void main(String args[]) throws NullPointerException, InterruptedException, IOException, URISyntaxException {
        Test_Spring_Hibernate test  = new Test_Spring_Hibernate();
        GeoDocument geo = new GeoDocument(
                new URL("http://www.url.com"), "regione", "provincia", "city",
                "indirizzo", "iva", "email", "telefono", "fax",
                "edificio", (Double) 0.0, (Double) 0.0, "nazione", "description",
                "postalCode", "indirizzoNoCAP", "indirizzoHasNumber"
        );

        String filePathXml ="spring_hibernate_files/spring-hibernate4v2.xml";
//        File file = Resources.getResourceAsFile(filePathXml);
//        if(file.exists()){
//            try {
//        ApplicationContext contextClassPath2 =
//                new ClassPathXmlApplicationContext("spring_hibernate_files/spring-hibernate4v2.xml");
//            }catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
        IGeoDocumentDao dao = new GeoDocumentDaoImpl();
        dao.setTableInsert("test_34");
        //dao.setDriverManager("com.sql.jdbc.Driver","jdbc:sql","localhost","3306","siimobility","siimobility","geolocationddb")
        dao.loadSpringConfig(filePathXml);
    }

//    public void loadXml() throws IOException, URISyntaxException {
//        String filePathXml ="spring_hibernate_files/spring-hibernate4v2.xml";
//        File file = Resources.getResourceAsFile(filePathXml);
//        if(file.exists()){
//            try {
//
////                ApplicationContext appContext =
////                        new ClassPathXmlApplicationContext(new String[] {"Spring-Customer.xml"});
//
////              CustomerService cust = (CustomerService)appContext.getBean("dataSource");
////              Resource resource = cust.getResource(file.getAbsolutePath());
//
//                //ApplicationContext context = new ClassPathXmlApplicationContext(file.getAbsolutePath());
//
//                ApplicationContext contextClassPath2 =
//                 new ClassPathXmlApplicationContext("spring_hibernate_files/spring-hibernate4v2.xml");
//
//                //ApplicationContext context = new FileSystemXmlApplicationContext("spring_hibernate_files/spring-hibernate4v2.xml");
//
//
//                //PRINT
////                try{
////                    InputStream is = resource.getInputStream();
////                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
////
////                    String line;
////                    while ((line = br.readLine()) != null) {
////                        System.out.println(line);
////                    }
////                    br.close();
////
////                }catch(IOException e){
////                    e.printStackTrace();
////                }
//            }catch(Exception e) {
//            }
//        }
//    }

    public class WebsiteService implements ResourceLoaderAware
    {
        private ResourceLoader resourceLoader;

        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        public Resource getResource(String location){
            return resourceLoader.getResource(location);
        }
    }


}

