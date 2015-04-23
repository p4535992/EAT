package extractor.estrattori;
import extractor.SystemLog;
import extractor.gate.GateAnnotationKit;
import extractor.gate.GateCorpusKit;
import extractor.gate.GateDataStoreKit;
import gate.Corpus;
import gate.CorpusController;
import gate.creole.ExecutionException;
import gate.persist.PersistenceException;
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
public class EstrazioneDatiWithGATEAndDataStore {	   

        private static GateAnnotationKit maac=new GateAnnotationKit();

      /** Tell GATE's spring.mvc.controller about the corpus you want to run on.
       *  @param corpus il corpus da settare
       */
      public void setCorpus(Corpus corpus,CorpusController controller) {controller.setCorpus(corpus);} // setCorpus

      /** Run GATE. */
      private int exeTentative = 0;
      public void execute(CorpusController controller) throws GateException {         
          try{
            controller.execute(); // execute()            
          }catch(ExecutionException ee){  
            if(exeTentative < 3){
                exeTentative ++;
                execute(controller);
            }else{
                SystemLog.ticket("No sentences or tokens to process in some gate documents", "OUT");
            }            
          }catch(java.lang.OutOfMemoryError e){
              if(exeTentative < 3){
              exeTentative ++;
              execute(controller);
              }else{
                  SystemLog.ticket("Exception in thread \"AWT-EventQueue-0\" ava.lang.OutOfMemoryError: Java heap space", "WARNING");
              }           
          }
      }
      
     /**
       * Metodo che estrae le informazioni da una lista di documenti web
       * @param listUrl lista degli indirizzi web del documento
       * @param content serve a identificare i fogli "in bianco" i testi senza 
       * contenuto come capita con l'estrazione con SearhMonkey
       * @param controller il CorpusController di GATE settato precedentemente      
       * @param indice identifica l'indice del documento in esame
       * @param datastore il datastore su cui slavare via via i corpus
       * @return un GeoDocument con i campi riempiti dalle annotazioni di GATE
       * @throws InterruptedException
       * @throws InvocationTargetException
       * @throws SQLException 
       */
      public ArrayList<GeoDocument> extractMicrodataWithGATEMultipleUrls(ArrayList<URL> listUrl,String content,CorpusController controller,Integer indice,GateDataStoreKit datastore) throws InterruptedException, InvocationTargetException, SQLException, PersistenceException{
        GeoDocument geoDoc = null;
        GeoDocument geoDoc2 = new GeoDocument(null,null, null, null, null, null, null, null, null, null, null, null, null, null,null,null,null);
        Corpus corpus = null;  
        ArrayList<GeoDocument> listGeo = new ArrayList<GeoDocument>();      
        //for(URL url: listUrl){System.out.println("("+i+")"+url.toString());}
        EstrazioneDatiWithGATEAndDataStore  gate = new EstrazioneDatiWithGATEAndDataStore ();
        // Con questa linea di codice vediamo tutto l'output sulla interfaccia GATE
        //*****************************************************************************
        /*MainFrame.getInstance().setVisible(true);*/
        //*************************************************************************
        //Per ogni singolo url viene creato un corpus con un singolo documento    
        try{                                  
          //AnnotationInfo annInfo = new AnnotationInfo(null, null, null, null, null, null, null, null, null, null);                   
          ArrayList<AnnotationInfo> listAnnInfo = new ArrayList<AnnotationInfo>();
          if(!(listUrl.isEmpty())&& listUrl.size()>0){   
            //int LO = m.getOFFSET()+m.getLIMIT();
            //String nome_corpus ="["+m.getOFFSET()+"-"+LO+"]("+m.getLIMIT()+")_GeoDocuments_Corpus";
            String nome_corpus ="[]_GeoDocuments_Corpus";
            corpus = GateCorpusKit.createCorpusByListOfUrls(listUrl, nome_corpus);
            //for(Document doc: corpus){System.out.println(doc.getName());}
            
            try{
                //System.out.println("1");
                GateDataStoreKit.openDataStore();
                GateDataStoreKit.setDataStoreWithACorpus(corpus);
                GateDataStoreKit.saveACorpusOnTheDataStore(corpus, datastore.getDS_DIR());
               //datastore.closeDataStore();
            }catch(Exception e){
               //System.out.println("2");
                GateDataStoreKit.openDataStore();
                GateDataStoreKit.saveACorpusOnTheDataStore(corpus, datastore.getDS_DIR());
//               ArrayList<Corpus> listCorpus = datastore.loadAllCorpusOnTheDataStore();
//               if(listCorpus.size()>1){
//                    String nome_corpus_2 ="GeoDocuments_Corpus";
//                    corpus = datastore.mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(nome_corpus_2, listCorpus);
//               }
                GateDataStoreKit.closeDataStore();
               
            }finally{ 
               //CODICE CHE FA IL MERGE DI TUTTI I CORPUS PRESENTI NEL DATASTORE
                /*
               datastore.openDataStore();
               ArrayList<Corpus> listCorpus = datastore.loadAllCorpusOnTheDataStore();
               if(listCorpus.size()>1){
                    String nome_corpus_2 ="GeoDocuments_Corpus_"+"["+m.getOFFSET().toString()+"-"+m.getLIMIT().toString()+"]";
                    corpus = datastore.mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(nome_corpus_2, listCorpus);                   
                    datastore.saveACorpusOnTheDataStore(corpus,datastore.getDS_DIR()); 
               }
               datastore.closeDataStore();
                */            
            }
            
          }//if url!=null
          else if(content!=null || (listUrl.isEmpty()&& listUrl.size()==0)){
//              corpus = Factory.newCorpus("GeoDocuments Corpus");
//              Document doc =(Document) Factory.newDocument(content);
//              corpus.add(doc);
                corpus = null;
          }  
          if(corpus == null){return null;}
          else{
            //System.out.println("5");
            //settiamo il corpus 
            gate.setCorpus(corpus,controller);    
            //carichiamo i documenti del corpus sulla nostra pipeline
            //su cui è caricato il file .gapp/.xgapp
            gate.execute(controller);           
            //andiamo a trasformare il contenuto matchato dalle nostre annotazioni
            //in keyword che andremo a inserire nel nostro apposito GeoDocument.  
            //for(Document doc: corpus){System.out.println(doc.getName());}
            listAnnInfo = GateAnnotationKit.getMultipleAnnotationInfo(corpus,null);
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
                //System.out.println("GATE:"+geoDoc.toString());
                //*****************************************************************
                //RUN APACHE TIKA -> LIBRERIA TIKA VA IN CONTRASTO CON HIBERNATE
                //******************************************************************
                /*
                util.log.write("*********************************************","OUT");
                util.log.write("Run TIKA","OUT");
                //System.out.println("************************************************");
                EstrazioneDatiWithTika etika = new EstrazioneDatiWithTika();
                geoDoc2 = etika.extractMicrodataWithTika(annInfo.getUrl(), geoDoc2);
                */
                if(geoDoc2.getEdificio()==null){
                    SystemLog.ticket("*********************************************", "OUT");
                    SystemLog.ticket("Run JSOUP", "OUT");
                    ExtractorJSOUP j = new ExtractorJSOUP();
                    geoDoc2 = j.GetTitleAndHeadingTags(annInfo.getUrl().toString(),geoDoc2);
                }   
                //*********************************************************************
                geoDoc = gate.compareInfo3(geoDoc, geoDoc2);             
                listGeo.add(geoDoc);
            }//for each AnnotationInfo
          }//else
        }//try
        catch(GateException ex){              
              SystemLog.ticket(ex.getMessage(), "ERROR");
              ex.printStackTrace();
              //continue;                  
      } catch (RuntimeException ex) {
              SystemLog.ticket(ex.getMessage(), "ERROR");
              ex.printStackTrace();                        
      } catch (IOException ex) {
              SystemLog.ticket(ex.getMessage(), "ERROR");
              ex.printStackTrace(); 
      }
       finally{
             //ripuliamo (opzionale)
            if(corpus!=null){
             corpus.cleanup();
             corpus.clear();
            }
             //datastore.closeDataStore();
             //datastore.closeDataStore();
      }    
      //System.out.println("GATE:"+geoDoc.toString());
      return listGeo;                 
    }//extractorGATE
	  
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
