/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.hibernate;
import object.model.GeoDomainDocument;
import object.model.GeoDocument;
import object.model.InfoDocument;
import util.SystemLog;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author Marco
 */
public class GeoDocumentDAO {
   
    public GeoDocumentDAO(){}
    
    public void addListOfGeoDocument(List<GeoDocument> geoDocs){
        for(GeoDocument geo : geoDocs){
            
            Transaction trns = null;
            Session session = HibernateUtil.getSessionFactory().openSession();
            try {
                trns = session.beginTransaction();
                session.save(geo);
                session.getTransaction().commit();
            } catch (RuntimeException e) {
                if (trns != null) {trns.rollback();}
                SystemLog.write(e.getMessage(), "ERROR");
                e.printStackTrace();
            } finally {
                session.flush();
                session.close();
            }
        }
    }
    
    public void addGeoDocument(GeoDocument geo) {
        Transaction trns = null;
        //String path= System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\geolocationdb\\hibernate.cfg.xml";
        //File cfgFile = new File(path);
        //SessionFactory factory = new Configuration().configure().buildSessionFactory(); 
        //HibernateUtil.buildSessionFactory(cfgFile, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        try {
            trns = session.beginTransaction();
            SystemLog.write("INSERIMENTO", "OUT");
            //System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(geo.toString());
            //System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");                                       
            session.save(geo);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }
    
    public void addGeoDomainDocument(GeoDomainDocument geo) {
        Transaction trns = null;
        //String path= System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\geolocationdb\\hibernate.cfg.xml";
        //File cfgFile = new File(path);
        //SessionFactory factory = new Configuration().configure().buildSessionFactory(); 
        //HibernateUtil.buildSessionFactory(cfgFile, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        try {
            trns = session.beginTransaction();
            SystemLog.write("INSERIMENTO", "OUT");
            //System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(geo.toString());
            //System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");                                       
            session.save(geo);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) { trns.rollback();}
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }
    
    public void addInfoDocument(InfoDocument info) {
        Transaction trns = null;
        //String path= System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\geolocationdb\\hibernate.cfg.xml";
        //File cfgFile = new File(path);
        //SessionFactory factory = new Configuration().configure().buildSessionFactory(); 
        //HibernateUtil.buildSessionFactory(cfgFile, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        try {
            trns = session.beginTransaction();
            SystemLog.write("INSERIMENTO", "OUT");
            //System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(info.toString());
            //System.out.println("---------------------------------------------------------------------------------------------------------------------------------------");                                      
            session.save(info);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {trns.rollback();}
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }

    public void deleteGeoDocument(int geoid) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            GeoDocument geo = (GeoDocument) session.load(GeoDocument.class, new Integer(geoid));
            session.delete(geo);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {trns.rollback(); }
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }

    public void updateGeoDocument(GeoDocument geo) {
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.update(geo);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {trns.rollback();}
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }

    public List<GeoDocument> getAllGeoDocuments(String nomeTabella,Integer LIMIT,Integer OFFSET) {
        List<GeoDocument> geoDocs = new ArrayList<GeoDocument>();
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            //String sql = "SELECT * FROM "+nomeTabella+""; 
            String sql = "SELECT " 
                 + "doc_id,url,regione,provincia,city,indirizzo,iva,email,telefono,fax," 
                 + "edificio,latitude,longitude,nazione,description,postalCode,"
                 + "indirizzoNoCAP,indirizzoHasNumber "
                 + "FROM "+nomeTabella+"";
            SystemLog.write("HIBERNATE:" + sql + " LIMIT " + LIMIT + " OFFSET " + OFFSET + "", "AVOID");
            SQLQuery query = session.createSQLQuery(sql).addEntity(GeoDocument.class);  
            if(LIMIT !=null && OFFSET !=null){
                query.setFirstResult(OFFSET); 
                query.setMaxResults(LIMIT);
            }
            geoDocs = query.list();
            tx = session.beginTransaction();
            //geoDocs = session.createQuery("FROM "+nomeTabella+"").list();
            //org.hibernate.hql.internal.ast.QuerySyntaxException: infodocument_gianni_update4_h is not mapped [FROM infodocument_gianni_update4_h]
            tx.commit();
        } catch (RuntimeException e) {
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return geoDocs;
    }

    public GeoDocument getGeoDocumentById(String nomeTabella,String nameColumnID,String valueID) {
        GeoDocument geo = null;
        Transaction trns = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            String queryString = "FROM "+nomeTabella+" WHERE "+nameColumnID+" = :"+nameColumnID+"";
            SystemLog.write("HIBERNATE:" + queryString + "", "AVOID");
            Query query = session.createQuery(queryString);
            query.setString(nameColumnID, valueID);
            geo = (GeoDocument) query.uniqueResult();
        } catch (RuntimeException e) {
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return geo;
    }
    
    public ArrayList<String> SelectUrlDAO(String nameTable,Integer LIMIT,Integer OFFSET,String nameColumn){
        ArrayList<String> list = new ArrayList<String>();
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        Transaction tx = null;
        try{ 
            //String hql = "SELECT "+nomeColonna+" FROM "+nomeTabella+" "; 
            //Query query = session.createQuery(hql); 
            String sql = "SELECT "+nameColumn+" FROM "+nameTable+"";
            SystemLog.write("HIBERNATE:" + sql + " LIMIT " + LIMIT + " OFFSET " + OFFSET + "", "AVOID");
            SQLQuery query = session.createSQLQuery(sql);            
            query.setFirstResult(OFFSET); 
            query.setMaxResults(LIMIT);
            tx = session.beginTransaction();
            //Criteria cr = session.createCriteria(GeoDocument.class); 
            // Add restriction. cr.add(Restrictions.gt("salary", 2000)); 
            List results = query.list(); 
            for (Iterator iterator = results.iterator(); iterator.hasNext();){
                String url = (String) iterator.next(); 
                //if(verifyDuplicate(nameOutputTable,nameColumn,url)==false){
                    //if(ExtractInfoHibernatePOJO.isFILTER()==true)
                    //{
                        if(url.toString().endsWith(".rtf")||url.toString().endsWith(".pdf")||
                                        url.toString().contains("image")||
                                        url.toString().contains("dbms")){
                            continue;
                        }else{
                            list.add(url);
                        }                                                                
                    //}else{
                    //    list.add(url);
                    //}
                    //System.out.print("First Name: " + employee.getFirstName()); 
                    //System.out.print(" Last Name: " + employee.getLastName()); 
                    //System.out.println(" Salary: " + employee.getSalary()); } 
                //}//verify
            }
             tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null) {tx.rollback();}
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }
        finally { 
            session.close(); 
        } 
        return list;
    }
      
    public void CreateTableGeoDocumentDAO(String nomeTabella,Boolean erase){
        //creating seession factory object
        //SessionFactory factory = new Configuration().configure().buildSessionFactory(); 
        //File cfg = new File(System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\geolocationdb\\hibernate.cfg.xml");
        //HibernateUtil.buildSessionFactory(cfg, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        //creating session object
        Session session = factory.openSession();
        //creating transaction object
        Transaction tx = null;
        try{ 
            //String hql = "SELECT "+nomeColonna+" FROM "+nomeTabella+" "; 
            //Query query = session.createQuery(hql);                                       
            String sql;
             if(erase==true){
                 sql ="DROP TABLE IF EXISTS "+nomeTabella+";";
                 //SQLQuery query = session.createSQLQuery(sql); 
                 SystemLog.write("HIBERNATE:" + sql + "", "AVOID");
                 session.createSQLQuery(sql).executeUpdate();
                 tx = session.beginTransaction();
                 tx.commit();
             }            
             //query ="CREATE TABLE "+nomeTabellaPerOntology+" LIKE "+nomeTabella+";"; 
             tx = null;
             sql ="CREATE TABLE IF NOT EXISTS `"+nomeTabella+"` (\n" +
                "  `doc_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `url` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `regione` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `provincia` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `city` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzo` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `iva` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `email` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `telefono` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `fax` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `edificio` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `latitude` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `longitude` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `nazione` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `description` varchar(5000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `postalCode` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzoNoCAP` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzoHasNumber` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  PRIMARY KEY (`doc_id`),\n" +
                "  KEY `url` (`url`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;";
            //SQLQuery query = session.createSQLQuery(sql);
            SystemLog.write("HIBERNATE:" + sql + "", "AVOID");
            session.createSQLQuery(sql).executeUpdate();
            tx = session.beginTransaction();
            //Criteria cr = session.createCriteria(GeoDocument.class); 
        // Add restriction. cr.add(Restrictions.gt("salary", 2000));                
            tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null) {tx.rollback(); }
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }
        finally { 
            session.close(); 
        } 
    }
    
    public void CreateTableInfoDocumentDAO(String nomeTabella,Boolean erase){
        //creating seession factory object
        //SessionFactory factory = new Configuration().configure().buildSessionFactory(); 
        //File cfg = new File(System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\geolocationdb\\hibernate.cfg.xml");
        //HibernateUtil.buildSessionFactory(cfg, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        //creating session object
        Session session = factory.openSession();
        //creating transaction object
        Transaction tx = null;
        try{ 
            //String hql = "SELECT "+nomeColonna+" FROM "+nomeTabella+" "; 
            //Query query = session.createQuery(hql);                                       
            String sql;
             if(erase==true){
                 sql ="DROP TABLE IF EXISTS "+nomeTabella+";";
                 //SQLQuery query = session.createSQLQuery(sql); 
                 SystemLog.write("HIBERNATE:" + sql + "", "AVOID");
                 session.createSQLQuery(sql).executeUpdate();
                 tx = session.beginTransaction();
                 tx.commit();
             }            
             //query ="CREATE TABLE "+nomeTabellaPerOntology+" LIKE "+nomeTabella+";"; 
             tx = null;
             sql ="CREATE TABLE IF NOT EXISTS `"+nomeTabella+"` (\n" +
                "  `doc_id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `url` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `regione` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `provincia` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `city` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzo` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `iva` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `email` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `telefono` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `fax` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `edificio` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `latitude` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `longitude` varchar(255) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `nazione` varchar(50) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `description` varchar(5000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `postalCode` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzoNoCAP` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `indirizzoHasNumber` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `identifier` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  `name_location` varchar(1000) COLLATE utf8_bin DEFAULT NULL,\n" +
                "  PRIMARY KEY (`doc_id`),\n" +
                "  KEY `url` (`url`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;";
            //SQLQuery query = session.createSQLQuery(sql);
            SystemLog.write("HIBERNATE:" + sql + "", "AVOID");
            session.createSQLQuery(sql).executeUpdate();
            tx = session.beginTransaction();
            //Criteria cr = session.createCriteria(GeoDocument.class); 
        // Add restriction. cr.add(Restrictions.gt("salary", 2000));                
            tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null){tx.rollback();}
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }
        finally { 
            session.close(); 
        } 
    }
    
    public String SelectCityFromKeywordDB(String nomeTabella,String nameWhereColumn,String valueWhereColumn,String nameSelectColumn){
        String city ="";
        //String path= System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\keyworddb\\hibernate.cfg.xml";
        //File cfgFile = new File(path);
        //HibernateUtil.buildSessionFactory(cfgFile, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();      
        Transaction tx = null;
        try{
           
            String sql ="SELECT "+nameSelectColumn+" FROM "+nomeTabella+" WHERE "+nameWhereColumn+"='"+valueWhereColumn+"'";
            SystemLog.write("HIBERNATE:" + sql, "AVOID");
            SQLQuery query = session.createSQLQuery(sql);
            
            List results = query.list();
            for (Iterator iterator = results.iterator(); iterator.hasNext();){
               //String url = (String) iterator.next(); 
               city = (String) iterator.next(); 
               break;
            }
            tx = session.beginTransaction();
            tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null) {tx.rollback(); }
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }
        finally { 
            session.close(); 
        } 
            
        return city;
    }
    
    public void CreateTableOfInfoDocumentFromGeoDocumentTable(String nomeTabellaGeoDocument,String nomeTabellaInfoDocument,Integer LIMIT,Integer OFFSET){
        //CreateTableInfoDocumentDAO(nomeTabellaInfoDocument, false);    
        //String path= System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\geolocationdb\\hibernate.cfg.xml";
        //File cfgFile = new File(path);
        //HibernateUtil.buildSessionFactory(cfgFile, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();   
        //Transaction tx = null;
        List<GeoDocument> listGeo = getAllGeoDocuments(nomeTabellaGeoDocument,LIMIT,OFFSET);           
        InfoDocument info; 
        GeoDocument geo;
        Session session = factory.openSession();          
        try{            
            //for (Iterator iterator = listGeo.iterator(); iterator.hasNext();){
            for(int i=0; i < listGeo.size(); i++){             
                geo = (GeoDocument) listGeo.get(i); 
                info = ConvertoGeoDocumentToInfoDocument(geo);
                addInfoDocument(info);
            }    
        }catch (HibernateException e) {
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }catch (Exception e) {
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }
        finally { 
            session.close(); 
        } 
    }
    
      public void CreateTableOfInfoDocumentFromGeoDocumentTableJIRA(String nomeTabellaGeoDocument,String nomeTabellaInfoDocument,Integer LIMIT,Integer OFFSET){
        //CreateTableInfoDocumentDAO(nomeTabellaInfoDocument, false);    
        //String path= System.getProperty("user.dir")+"\\src\\home\\utils\\hibernate\\cfg\\geolocationdb\\hibernate.cfg.xml";
        //File cfgFile = new File(path);
        //HibernateUtil.buildSessionFactory(cfgFile, null);
        SessionFactory factory = HibernateUtil.getSessionFactory();   
        //Transaction tx = null;
        List<GeoDocument> listGeo = getAllGeoDocuments(nomeTabellaGeoDocument,LIMIT,OFFSET);           
        InfoDocument info; 
        GeoDocument geo;
        Session session = factory.openSession();          
        try{            
            //for (Iterator iterator = listGeo.iterator(); iterator.hasNext();){
            for(int i=0; i < listGeo.size(); i++){             
                geo = (GeoDocument) listGeo.get(i); 
                info = ConvertoGeoDocumentToInfoDocument(geo);
                //addInfoDocument(info);
                addObjectAnnotated(geo,"home.object_h.InfoDocument");  
            }    
        }catch (HibernateException e) {
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }catch (Exception e) {
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }
        finally { 
            session.close(); 
        } 
    }
    
    /**
     * Metodo che converte unìoggetto GeoDocument in un InfoDocument
     * @param geo
     * @return 
     */
    public InfoDocument ConvertoGeoDocumentToInfoDocument(GeoDocument geo){
        InfoDocument info = new InfoDocument(
                geo.getUrl(), 
                geo.getRegione(),
                geo.getProvincia(), 
                geo.getCity(), 
                geo.getIndirizzo(), 
                geo.getIva(), 
                geo.getEmail(), 
                geo.getTelefono(),
                geo.getFax(), 
                geo.getEdificio(), 
                geo.getLat(), 
                geo.getLng(),
                geo.getNazione(),
                geo.getDescription(),
                geo.getIndirizzoNoCAP(),
                geo.getPostalCode(), 
                geo.getIndirizzoHasNumber(),
                null,//identifier
                null);//name_location
        
        String identifier = geo.getEdificio().replaceAll("[\\^\\|\\;\\:]"," ").replaceAll("\\s+"," ").replaceAll("\\s","_");
        //identifier = identifier.replaceAll("[\\u00c0-\\u00f6\\u00f8-\\u00FF]","");
        //identifier = identifier.replaceAll("[^a-zA-Z0-9]", "");
        String name_location = "Location_" + identifier;
        info.setIdentifier(identifier);
        info.setName_location(name_location);
        return info;
    }
    
    public void GeoDocumentRemoveBlankSpaceBeforeAndAfterTheString(
            String nomeTabella,Integer LIMIT,Integer OFFSET){
         Transaction trns = null;
         Session session = HibernateUtil.getSessionFactory().openSession();
         String sql="";
         SQLQuery query;
         try {                    
            if(LIMIT == null && OFFSET ==null){
                sql = "SELECT * FROM "+nomeTabella+" ";
                SystemLog.write(sql, "AVOID");
                query = session.createSQLQuery(sql).addEntity(GeoDocument.class);
                SystemLog.write(query.getQueryString(), "AVOID");
                
            }else{
                sql = "SELECT * FROM "+nomeTabella+" LIMIT "+LIMIT+" OFFSET "+OFFSET+" ";
                SystemLog.write(sql, "AVOID");
                query = session.createSQLQuery(sql).addEntity(GeoDocument.class);
                SystemLog.write(query.getQueryString(), "AVOID");
                
            }  
            //List results = query.list();
            trns = session.beginTransaction();        
            session.getTransaction().commit();
            
            //List results = query.list();
            List<String> columns = Arrays.asList(
            "url","regione","provincia","city","indirizzo","iva","email",
            "telefono","edificio","nazione","latitude","longitude","description",
            "indirizzoNoCAP","postalCode","fax","indirizzoHasNumber"        
            );
            //"doc_id","identifier","name_location;
   
            //Query q;
            for (String s : columns){
              //String url = (String) iterator.next();             
              trns = null;
              sql = "UPDATE `"+nomeTabella+"` SET `"+s+"` = LTRIM(RTRIM(`"+s+"`));";
              //home.utils.log.write(sql,"OUT");
              query = session.createSQLQuery(sql);
                SystemLog.write(query.getQueryString(), "AVOID");
             // session.getTransaction().commit();
              session.createSQLQuery(sql).executeUpdate();
              trns = session.beginTransaction();
              trns.commit();                     
            }              
        } catch (RuntimeException e) {
            if (trns != null) {trns.rollback();}
             SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }   
    }
    
    public boolean verifyDuplicate(String nomeTabella,String nameWhereColumn,String valueWhereColumn){
        boolean b = false;
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();      
        Transaction tx = null;
        try{
           
            String sql ="SELECT * FROM "+nomeTabella+" WHERE "+nameWhereColumn+"='"+valueWhereColumn+"' LIMIT 1";
            SystemLog.write("HIBERNATE:" + sql, "AVOID");
            SQLQuery query = session.createSQLQuery(sql);
            
            List results = query.list();           
            if(results!=null && results.size() > 0){
                SystemLog.write("Attenzione! Il record con " + nameWhereColumn.toString() + "='" + valueWhereColumn.toString() + "' è già presente nel database", "WAR");
                b=true;
            }
            tx =session.beginTransaction();
            tx.commit();
        }catch (HibernateException e) { 
            if (tx!=null) {tx.rollback(); }
            SystemLog.write(e.getMessage(), "ERROR");
            e.printStackTrace(); 
        }
        finally { 
            session.close(); 
        } 
        
        return b;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////
    //METODI AGGIUNTIVI CHE UTILIZZANO LE ANNOTAZIONI
    //////////////////////////////////////////////////////////////////////////////////
   /**
    * Method to CREATE an employee in the database 
    * @param obj 
    * @param pathPackageToObjectAnnotated
    */
   public void addObjectAnnotated(Object obj,String pathPackageToObjectAnnotated){
      SessionFactory factory = HibernateUtil.getSessionFactory();
      Session session = factory.openSession();      
      Transaction tx = null;
      try{
         tx = session.beginTransaction();
          SystemLog.write("INSERIMENTO", "OUT");
         //session.save(obj);
         //Class clsObjectAnnotated = HibernateUtil.createNewClass(pathPackageToObjectAnnotated);
         //session.save(clsObjectAnnotated.cast(obj));
         session.save(obj);
         //session.getTransaction().commit();
         tx.commit();
          SystemLog.write("E' stato creato un GeoDocument:" + obj.toString() + " nel database", "AVOID");
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace();  
      }catch(Exception e){
         e.printStackTrace();
       }finally {
         session.close(); 
      }      
   }
   /* Method to UPDATE salary for an employee */
   public void updateCityGeoDocumentAnnotated(Object obj,String valueWhere,String pathPackageToObjectAnnotated){
      SessionFactory factory = HibernateUtil.getSessionFactory();
      Session session = factory.openSession();      
      Transaction tx = null;
      try{
         //Class clsObjectAnnotated = HibernateUtil.createNewClass(pathPackageToObjectAnnotated);
         tx = session.beginTransaction();
         //Object geoDoc = (Object)session.get(clsObjectAnnotated, valueWhere); 
         //geoDoc.setCity(newCityValue);
         session.update(obj); 
         session.getTransaction().commit();
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } catch (Exception ex) {
            Logger.getLogger(GeoDocumentDAO.class.getName()).log(Level.SEVERE, null, ex);     
      }finally {
         session.close(); 
      }
   }
   /* Method to DELETE an employee from the records */
   public void deleteGeoDocumentAnnotated(Object obj,String pathPackageToObjectAnnotated){
      SessionFactory factory = HibernateUtil.getSessionFactory();
      Session session = factory.openSession();      
      Transaction tx = null;
      try{
         //Class clsObjectAnnotated = HibernateUtil.createNewClass(pathPackageToObjectAnnotated);
         tx = session.beginTransaction();
         //Object geoDoc = (Object)session.get(clsObjectAnnotated , valueWhere); 
         session.delete(obj);
         session.getTransaction().commit();
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      } catch (Exception ex) {
            Logger.getLogger(GeoDocumentDAO.class.getName()).log(Level.SEVERE, null, ex);     
      }finally {
         session.close(); 
      }
   }
    
   /* Method to  READ all the employees */
   public List<Object> readObjectAnnotated(Object nameObjectAnnotated,String pathPackageToObjectAnnotated,Integer LIMIT,Integer OFFSET,String nameColumn) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
      List<Object> list = new ArrayList<>();   
      SessionFactory factory = HibernateUtil.getSessionFactory();
      Session session = factory.openSession();      
      Transaction tx = null;
      try{
         //Class clsObjectAnnotated = HibernateUtil.createNewClass(nameObjectAnnotated,pathPackageToObjectAnnotated);
         Class clsObjectAnnotated = HibernateUtil.createNewClass(pathPackageToObjectAnnotated);
         String sql ="";
         if(nameColumn != null){
            sql = "SELECT "+nameColumn+" FROM "+clsObjectAnnotated.getSimpleName().toLowerCase()+""; 
         }else{
            sql = "SELECT * FROM "+clsObjectAnnotated.getSimpleName().toLowerCase()+"";  
         }
          SystemLog.write("HIBERNATE:" + sql + " LIMIT " + LIMIT + " OFFSET " + OFFSET + "", "AVOID");
         SQLQuery query = session.createSQLQuery(sql);            
         query.addEntity(clsObjectAnnotated);
         query.setFirstResult(OFFSET); 
         query.setMaxResults(LIMIT);       
         tx = session.beginTransaction();      
          if(nameColumn!=null){
               List<String> results = query.list();
               tx.commit();
                for (Iterator iterator = results.iterator(); iterator.hasNext();){
                    String url = (String) iterator.next();          
                    //if(ExtractInfoHibernatePOJO.isFILTER()==true)
                    //{
                        if(url.toString().endsWith(".rtf")||url.toString().endsWith(".pdf")||
                           url.toString().contains("image")||url.toString().contains("dbms")){
                            continue;
                        }else{                        
                            list.add(url);
                        }                                                                
                    //}else{
                    //    list.add(url);
                    //}
             }//for
             results.clear();
        }else{//if columnName==null
              List<Object> results2 = query.list();          
              for (Iterator iterator = results2.iterator(); iterator.hasNext();){
                  list.add(clsObjectAnnotated.cast(iterator.next()));
               }
              results2.clear();
              tx.commit();       
        }
     }catch (HibernateException ex) {
       if (tx!=null) {tx.rollback();}
       ex.printStackTrace();
          SystemLog.write("", "");
     }finally {
        session.close();        
     }
     return list;
   }
   
   
   
   
   
   /* 
   public void addWebsiteAnnotated(home.object_h.Website obj,String pathPackageToObjectAnnotated){
      SessionFactory factory = HibernateUtil.getSessionFactory();
      Session session = factory.openSession();      
      Transaction tx = null;
      try{
         tx = session.beginTransaction();        
         home.utils.log.write("INSERIMENTO","OUT");                                          
         session.save(obj);
         //session.save(clsObjectAnnotated.cast(geo));
         session.getTransaction().commit();
         //tx.commit();
         home.utils.log.write("E' stato creato un GeoDocument:"+obj.toString()+" nel database", "OUT");
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace();      
        }finally {
         session.close(); 
      }      
   }
   */
   
   /*
   public List<home.object_h.Website> readWebsiteAnnotated(Object nameObjectAnnotated,String pathPackageToObjectAnnotated,Integer LIMIT,Integer OFFSET,String nameColumn) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException{
      List<home.object_h.Website> list = new ArrayList<>();
      home.object_h.Website web = new home.object_h.Website();
      SessionFactory factory = HibernateUtil.getSessionFactory();
      Session session = factory.openSession();      
      Transaction tx = null;
      try{
         //Class clsObjectAnnotated = HibernateUtil.createNewClass(nameObjectAnnotated,pathPackageToObjectAnnotated);
         Class clsObjectAnnotated = HibernateUtil.createNewClass(pathPackageToObjectAnnotated);
         String sql ="";
         if(nameColumn != null){
            sql = "SELECT "+nameColumn+" FROM "+clsObjectAnnotated.getSimpleName()+""; 
         }else{
            sql = "SELECT * FROM "+clsObjectAnnotated.getSimpleName()+"";  
         }
         home.utils.log.write("HIBERNATE:"+sql+" LIMIT "+LIMIT+" OFFSET "+OFFSET+"","AVOID");
         SQLQuery query = session.createSQLQuery(sql);            
         query.addEntity(home.object_h.Website.class);
         query.setFirstResult(OFFSET); 
         query.setMaxResults(LIMIT);    
         tx = session.beginTransaction();    
          if(nameColumn!=null){
               List<String> results = query.list();
               tx.commit();
            for (Iterator iterator = results.iterator(); iterator.hasNext();){
                   String url = (String) iterator.next();          
                   if(ExtractInfoHibernate.isFILTER()==true)
                   {                    
                       if(url.toString().endsWith(".rtf")||url.toString().endsWith(".pdf")||
                                       url.toString().contains("image")||
                                       url.toString().contains("dbms")){
                           continue;
                       }else{
                           web.setUrl(url);
                           list.add(web);
                       }                                                                
                   }else{
                       web.setUrl(url);
                       list.add(web);
                   }   
            }//for
      }else{
            List<home.object_h.Website> results2 = query.list();
            //List<home.object_h.Website> results2 = session.createQuery("SELECT web FROM "+clsObjectAnnotated.getSimpleName()+" web").list(); 
            for (Iterator iterator = results2.iterator(); iterator.hasNext();){
                //Object obj = HibernateUtil.castObjectToSpecificClass(clsObjectAnnotated, iterator.next());
                //web = iterator.next();
                list.add((Website) iterator.next());
             }
            tx.commit();         
      }           
     }catch (HibernateException ex) {
       if (tx!=null) {tx.rollback();}
       ex.printStackTrace(); 
       home.utils.log.write("","");    
     }finally {
        session.close(); 
     }
     return list;
   }
   */
   
    
}
    
