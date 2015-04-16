package util.estrattori;
import util.SystemLog;
import util.gate.CreateCorpus;
import util.gate.ManageAnnotationAndContent;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.util.*;
import object.support.AnnotationInfo;
import object.model.GeoDocument;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.sql.SQLException;
import java.util.*;
/**
 * EstrazioneDatiWithGATE.java
 * Classe che utilizza GATE per l'estrazione di informazione da und eterminato testo
 * @author Utente
 */
public class EstrazioneDatiWithGATE {	   
//        //ASSICURATI CHE I PARAMETRI DEL TUO DATABASE HOST SERVER CORRISPONDANO
//        private static ConnectToDatabaseMySQL c = new ConnectToDatabaseMySQL(
//           "com.mysql.jdbc.Driver",         /*DRIVER*/
//           "jdbc:mysql","localhost",3306   /*HOST_URL*/
//           );
//        //NOME USER PER L'HOST MYSQL
//        private static final String USER ="root";
//        //PASSWORD PER L'HOST MYSQL
//        private static final String PASS="";
        //**********************************************************************************
        private static CreateCorpus cc = new CreateCorpus();
        private static ManageAnnotationAndContent maac=new ManageAnnotationAndContent();
        //private static ManageJsonWithGoogleMaps j = new ManageJsonWithGoogleMaps(API_KEY_GM);

      /** The Corpus Pipeline application to contain ANNE,Lingpipe,Tools,ecc. */
      //private static CorpusController controller;

      /**
       * Initialise the GATE system. This creates a "corpus pipeline"
       * application that can be used to run sets of documents through
       * the extraction system.
       */
//      public void init() throws GateException, IOException {
//        //Out.prln("Loading file .gapp...");
//        //Carica tutte le applicazioni della GATE_HOME attraverso il file .gapp
//        File home = Gate.getGateHome();
//        //Con il controllo della lingua
//        File gapp = new File(home, "custom/gapp/geoLocationPipeline19092014.xgapp");
//        //Senza il controllo della lingua
//        //File gapp = new File(home, "custom/gapp/geoLocationPipelineFast.xgapp");
//        controller = (CorpusController) PersistenceManager.loadObjectFromFile(gapp);
//        //Out.prln("...file .gapp loaded ");
//      } // initAnnie()

      /** Tell GATE's controller about the corpus you want to run on.
       *  @param corpus il corpus da settare
       */
      public void setCorpus(Corpus corpus,CorpusController controller) {controller.setCorpus(corpus);} // setCorpus

      /** Run GATE. */
      public void execute(CorpusController controller) throws GateException {controller.execute();} // execute()

      /**
       * Metodo che estrae le informazioni dal singolo documento web
       * @param url indirizzo web del documento
       * @param content serve a identificare i fogli "in bianco" i testi senza 
       * contenuto come capita con l'estrazione con SearhMonkey
       * @param controller il CorpusController di GATE settato precedentemente
       * @param i identifica l'indice del documento in esame
       * @return un GeoDocument con i campi riempiti dalle annotazioni di GATE
       * @throws InterruptedException
       * @throws InvocationTargetException
       * @throws SQLException 
       */
      public GeoDocument extractMicrodataWithGATESingleUrl(URL url,String content,CorpusController controller,Integer i) throws InterruptedException, InvocationTargetException, SQLException{	      
        GeoDocument geoDoc = null;
        Corpus corpus = null;       	         
        EstrazioneDatiWithGATE gate = new EstrazioneDatiWithGATE();
        // Con questa linea di codice vediamo tutto l'output sulla interfaccia GATE
        //*****************************************************************************
        /*MainFrame.getInstance().setVisible(true);*/
        //*************************************************************************
        //Per ogni singolo url viene creato un corpus con un singolo documento    
        try{                                  
          AnnotationInfo annInfo = new AnnotationInfo(null,null, null, null, null, null, null, null, null, null, null);                   
          if(url!=null){                     
            corpus = cc.createCorpusByUrl(url,"GeoDocuments Corpus",i);
           }//if url!=null
          else if(content!=null || url ==null){
              corpus = Factory.newCorpus("GeoDocuments Corpus");
              Document doc =(Document) Factory.newDocument(content);
              corpus.add(doc);

          }  
          if(corpus == null){return null;}
          else{           
            //settiamo il corpus 
            gate.setCorpus(corpus,controller);    
            //carichiamo i documenti del corpus sulla nostra pipeline
            //su cui è caricato il file .gapp/.xgapp
            gate.execute(controller);           
            //andiamo a trasformare il contenuto matchato dalle nostre annotazioni
            //in keyword che andremo a inserire nel nostro apposito GeoDocument.            
            annInfo = maac.getSingleAnnotationInfo(corpus,null);
            //*************************************************************************          
            annInfo = pulisciAnnotionInfo(annInfo);           
           
            geoDoc = new GeoDocument(                                                       
              annInfo.getUrl(),       /*url*/
              annInfo.getRegione(),   /*regione*/
              annInfo.getProvincia(), /*provincia*/
              annInfo.getLocalita(),  /*city*/                               
              annInfo.getIndirizzo(), /*indirizzo*/
              annInfo.getIva(),       /*Partita IVA*/
              annInfo.getEmail(),     /*email*/
              annInfo.getTelefono(),  /*telefono*/
              annInfo.getFax(),       /*fax*/
              annInfo.getEdificio(),  /*edificio*/ 
              null,                   /*latitude*/
              null,                   /*longitude*/ 
              annInfo.getNazione(),   /*nazione*/
              null,                   /*description*/
              null,                   /*indirizzoNoCAP*/
              null,                   /*postalCode*/
              null                    /*indirizzoHasNumber*/
            );                                                                          
          }//else
        }//try
        catch(GateException ex){
              SystemLog.write(ex.getMessage(), "ERROR");
              ex.printStackTrace();                 
      } catch (RuntimeException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              ex.printStackTrace();                        
      } catch (IOException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              ex.printStackTrace(); 
      }
       finally{
             //ripuliamo (opzionale)
            if(corpus!=null){
             corpus.cleanup();
             corpus.clear();
            }            
      }    
      //System.out.println("GATE:"+geoDoc.toString());
      return geoDoc;                 
    }//extractMicrodataWithGATESingleUrl
      
     /**
       * Metodo che estrae le informazioni da una lista di documenti web
       * @param listUrl lista degli indirizzi web del documento
       * @param content serve a identificare i fogli "in bianco" i testi senza 
       * contenuto come capita con l'estrazione con SearhMonkey
       * @param controller il CorpusController di GATE settato precedentemente
       * @param i identifica l'indice del documento in esame
       * @return un GeoDocument con i campi riempiti dalle annotazioni di GATE
       * @throws InterruptedException
       * @throws InvocationTargetException
       * @throws SQLException 
       */
      public ArrayList<GeoDocument> extractMicrodataWithGATEMultipleUrls(ArrayList<URL> listUrl,String content,CorpusController controller,Integer i) throws InterruptedException, InvocationTargetException, SQLException{	      
        GeoDocument geoDoc = null;
        GeoDocument geoDoc2 = new GeoDocument(null,null, null, null, null, null, null, null, null, null, null, null, null, null,null,null,null);
        Corpus corpus = null;  
        ArrayList<GeoDocument> listGeo = new ArrayList<GeoDocument>();      
        //for(URL url: listUrl){System.out.println("("+i+")"+url.toString());}  
        EstrazioneDatiWithGATE gate = new EstrazioneDatiWithGATE();
        // Con questa linea di codice vediamo tutto l'output sulla interfaccia GATE
        //*****************************************************************************
        /*MainFrame.getInstance().setVisible(true);*/
        //*************************************************************************
        //Per ogni singolo url viene creato un corpus con un singolo documento    
        try{                                  
          //AnnotationInfo annInfo = new AnnotationInfo(null, null, null, null, null, null, null, null, null, null);                   
          ArrayList<AnnotationInfo> listAnnInfo = new ArrayList<AnnotationInfo>();
          if(!(listUrl.isEmpty())&& listUrl.size()>0){                     
            corpus = cc.createCorpusByListOfUrls(listUrl,"GeoDocuments Corpus",i,null);
           }//if url!=null
          else if(content!=null || (listUrl.isEmpty()&& listUrl.size()==0)){
//              corpus = Factory.newCorpus("GeoDocuments Corpus");
//              Document doc =(Document) Factory.newDocument(content);
//              corpus.add(doc);
                corpus = null;
          }  
          if(corpus == null){return null;}
          else{           
            //settiamo il corpus 
            gate.setCorpus(corpus,controller);    
            //carichiamo i documenti del corpus sulla nostra pipeline
            //su cui è caricato il file .gapp/.xgapp
            gate.execute(controller);           
            //andiamo a trasformare il contenuto matchato dalle nostre annotazioni
            //in keyword che andremo a inserire nel nostro apposito GeoDocument.            
            listAnnInfo = maac.getMultipleAnnotationInfo(corpus,null);
            //*************************************************************************
            for(AnnotationInfo annInfo:listAnnInfo){
                annInfo = pulisciAnnotionInfo(annInfo);           
           
                geoDoc = new GeoDocument(                                                       
                  annInfo.getUrl(),       /*url*/
                  annInfo.getRegione(),   /*regione*/
                  annInfo.getProvincia(), /*provincia*/
                  annInfo.getLocalita(),  /*city*/                               
                  annInfo.getIndirizzo(), /*indirizzo*/
                  annInfo.getIva(),       /*Partita IVA*/
                  annInfo.getEmail(),     /*email*/
                  annInfo.getTelefono(),  /*telefono*/
                  annInfo.getFax(),       /*fax*/
                  annInfo.getEdificio(),  /*edificio*/ 
                  null,                   /*latitude*/
                  null,                   /*longitude*/ 
                  annInfo.getNazione(),   /*nazione*/
                  null,                   /*description*/
                  null,                   /*indirizzoNoCAP*/
                  null,                   /*postalCode*/
                  null                    /*indirizzoHasNumber*/
                );   
                SystemLog.write("GATE document:" + geoDoc.toString(), "OUT");
                //*****************************************************************
                //RUN APACHE TIKA -> LIBRERIA TIKA VA IN CONTRASTO CON HIBERNATE
                //******************************************************************
                /*
                util.log.write("*********************************************","OUT");
                util.log.write("Run TIKA","OUT");
                EstrazioneDatiWithTika etika = new EstrazioneDatiWithTika();
                geoDoc2 = etika.extractMicrodataWithTika(annInfo.getUrl(), geoDoc2);
                */
                if(geoDoc2.getEdificio()==null){
                    SystemLog.write("*********************************************", "OUT");
                    SystemLog.write("Run JSOUP", "OUT");
                    util.estrattori.EstrazioneDatiWithJSOUP j = new util.estrattori.EstrazioneDatiWithJSOUP();                   
                    geoDoc2 = j.GetTitleAndHeadingTags(annInfo.getUrl().toString(),geoDoc2);
                }   
                //if(geoDoc2.getEdificio()==null){System.err.println("Documento "+geoDoc2.getUrl()+" non più disponibile o raggiungibile."); continue;}
                //System.out.println("TIKA:"+geoDoc2.toString());
                //*********************************************************************
                geoDoc = gate.compareInfo3(geoDoc, geoDoc2);
                //System.out.println("*********************************************************************");
                //System.out.println("TIKA+GATE:"+geoDoc.toString());
                listGeo.add(geoDoc);
            }//for each AnnotationInfo
          }//else
        }//try
        catch(GateException ex){
              SystemLog.write(ex.getMessage(), "ERROR");
              ex.printStackTrace();
              //continue;                  
      } catch (RuntimeException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              ex.printStackTrace();                        
      } catch (IOException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              ex.printStackTrace(); 
      }
       finally{
             //ripuliamo (opzionale)
            if(corpus!=null){
             corpus.cleanup();
             corpus.clear();
            }
      }    
      //System.out.println("GATE:"+geoDoc.toString());
      return listGeo;                 
    }//extractMicrodataWithGATESingleUrl
	  
   /**
    * Setta a null se verifica che la stringa non è
    * nulla, non è vuota e non è composta da soli spaceToken (white space)
    * @param s stringa di input
    * @return  il valore della stringa se null o come è arrivata
    */
   private String setNullForEmptyString(String s){     
        if(s!=null && !s.isEmpty() && !s.trim().isEmpty()){return s;}
        else{return null;}
    }  
   
    /**
     * Metodo di comparazione dei risultati attraverso GATE e Apache Tika per la modalità 1
     * @param geo GeoDocument ricavato da DOM HTML
     * @param geo2 GeoDocument ricavato da anlisi del GATE
     * @return un geodocument con il meglio dei risultati di entrambe le ricerche
     */
    private GeoDocument compareInfo3(GeoDocument geo,GeoDocument geo2){			            	  
        if(geo2.getEdificio()!=null){geo.setEdificio(geo2.getEdificio());}  
        if(geo2.getDescription()!=null){geo.setDescription(geo2.getDescription());} 
        if(geo2.getNazione()!=null){geo.setNazione(geo2.getNazione());} 
        return geo;
    }//compareInfo3

     /**
      * Metodo che pulisce i campi dell'oggetto AnnotationInfo
      * @param annInfo
      * @return annotationInfo "ripulito"
      */
     private AnnotationInfo pulisciAnnotionInfo(AnnotationInfo annInfo){
         //if(annInfo.getUrl().toString()!=null){}
         if(setNullForEmptyString(annInfo.getRegione())!=null){
             annInfo.setRegione(annInfo.getRegione().toString().trim());
         }
         
         if(setNullForEmptyString(annInfo.getProvincia())!=null){
             annInfo.setProvincia(annInfo.getProvincia().toString().trim());
         }
         
         if(setNullForEmptyString(annInfo.getLocalita())!=null){
             annInfo.setLocalita(annInfo.getLocalita().toString().trim());
         }
         else{
            if(setNullForEmptyString(annInfo.getLocalita())!=null){
                     annInfo.setLocalita(annInfo.getLocalita().trim());
            }else if(setNullForEmptyString(annInfo.getProvincia())!=null){
                     annInfo.setLocalita(annInfo.getProvincia().trim());
            }
         }
         if(setNullForEmptyString(annInfo.getIndirizzo())!=null){
             annInfo.setIndirizzo(annInfo.getIndirizzo().toString().trim());
         }
         if(setNullForEmptyString(annInfo.getIva())!=null){
             annInfo.setIva(annInfo.getIva().toString().trim());
         }
         if(setNullForEmptyString(annInfo.getEmail())!=null){
             annInfo.setEmail(annInfo.getEmail().toString().trim());
         }
         if(setNullForEmptyString(annInfo.getTelefono())!=null){
             annInfo.setTelefono(annInfo.getTelefono().toString().trim());
         }
         if(setNullForEmptyString(annInfo.getFax())!=null){
             annInfo.setFax(annInfo.getFax().toString().trim());
         }
         if(setNullForEmptyString(annInfo.getEdificio())!=null){
             annInfo.setEdificio(annInfo.getEdificio().toString().trim());
         }
         if(setNullForEmptyString(annInfo.getNazione())!=null){
             annInfo.setNazione(annInfo.getNazione().toString().trim());
         }
         return annInfo;
     }
      
}
