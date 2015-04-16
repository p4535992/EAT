/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.TransformerConfigurationException;

import object.model.Website;
import org.xml.sax.SAXException;
import util.hibernate.GeoDocumentDAO;
import util.hibernate.HibernateUtil;

/**
 *
 * @author Marco
 */
public class Test_Hibernate {
    
    
     public static void main(String args[]) throws NullPointerException, InterruptedException, InvocationTargetException, SAXException, IOException,TransformerConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException{  
         GeoDocumentDAO dao = new GeoDocumentDAO();

         //String path= System.getProperty("user.dir")+"\\src\\main\\java\\util\\hibernate\\cfg\\urldb\\hibernate.cfg.xml";
        // String path = "C:\\Users\\Marco\\IdeaProjects\\ExtractInfo\\src\\main\\java\\util\\hibernate\\cfg\\urldb\\hibernate.cfg.xml";
         //String path = "C:\\Users\\Marco\\IdeaProjects\\ExtractInfo\\src\\main\\resources\\hibernate\\cfg\\urldb\\hibernate.cfg.xml";
         String path = "resources/hibernate.cfg.xml";
         File file = new File(path);

         //XMLUtil.readXMLFileAndPrint(file);

         //XMLUtil.updateValueOfAttribute(file, "class", "table", "xxx");
         
         //XMLUtil.updateValueOfattributeSAX(path, "class", "table", "XXX");
         
         //String sUrl = "http://wifi.unipi.it/via-santa-maria/";
         //dao.SelectCityFromKeywordDB("document","url", sUrl,"city","siimobility","siimobility","keyword");
         //HibernateUtil.buildSessionFactory("urldb","website","Website","root","",
         //        "com.mysql.jdbc.Driver","jdbc:mysql","org.hibernate.dialect.MySQLDialect","localhost","3306");
         
         /*
         path = System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\urldb\\hibernate.cfg.xml";
         HibernateUtil.buildSessionFactory(path);
         HibernateUtil.getSessionFactory();
         List<String> listStringUrl  = dao.SelectUrlDAO("website",0,100,"url");
         HibernateUtil.shutdown();
         */
         List<Class> listCls = new ArrayList<>();
         //listCls.add(home.object_h.Document.class);
         //listCls.add(home.object_h.GeoDocument.class);
         //listCls.add(home.object_h.GeoDomainDocument.class);
         //listCls.add(home.object_h.InfoDocument.class);
         listCls.add(Website.class);
         
         String USER="root"; //string
         String PASS = "";  //string 
         
         String DB_OUTPUT="urldb_2015"; //string       
         
         String DRIVER_DATABASE="com.mysql.jdbc.Driver"; //string 
         String DIALECT_DATABASE="jdbc:mysql"; //string
         String HOST_DATABASE="localhost"; //string
         String DIALECT_DATABASE_HIBERNATE = "org.hibernate.dialect.MySQLDialect";
         String PORT_DATABASE ="3306";
         
         HibernateUtil.buildSessionFactory(
                 DB_OUTPUT,
                 USER,
                 PASS,
                 DRIVER_DATABASE,
                 DIALECT_DATABASE,
                 DIALECT_DATABASE_HIBERNATE,
                 HOST_DATABASE,
                 PORT_DATABASE,
                 listCls
         );
         Object website = new Object();
        //List<home.object_h.Website> list = dao.readWebsiteAnnotated(
         List list = dao.readObjectAnnotated(
                 website, 
                 "home.object.model.Website",
                 3, 
                 0,
                 null); //url
         /*
         HibernateUtil.shutdown();
         HibernateUtil.buildSessionFactory(
                 DB_OUTPUT, 
                 USER, 
                 PASS, 
                 DRIVER_DATABASE,                
                 DIALECT_DATABASE, 
                 DIALECT_DATABASE_HIBERNATE,
                 HOST_DATABASE, 
                 PORT_DATABASE, 
                 listCls
         );
         */
         for(Object web : list){    
             System.out.println(web.toString());
             //System.out.println(web.toString());
             //dao.addWebsiteAnnotated(web, "home.object_h.Website");
             dao.addObjectAnnotated(web, "home.object_h.Website");
         }
         HibernateUtil.shutdown();
           
     }
}
