package util.estrattori;

import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.GateException;
import object.model.GeoDocument;
import object.support.AnnotationInfo;
import util.FileUtil;
import util.SystemLog;
import util.gate.GateAnnotationKit;
import util.gate.GateCorpusKit;
import util.gate.GateDataStoreKit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * EstrazioneDatiWithGATE.java
 * Classe che utilizza GATE per l'estrazione di informazione da und eterminato testo
 * @author Utente
 */
public class ExtractorInfoGATE {


    private GeoDocument geoDoc;
    private Corpus corpus;
    private AnnotationInfo annInfo;

    public ExtractorInfoGATE(){}

    /** Tell GATE's spring.mvc.controller about the corpus you want to run on.
     * *  @param corpus il corpus da settare
     */
    public void setCorpus(Corpus corpus,CorpusController controller) {controller.setCorpus(corpus);} // setCorpus

    /** Run GATE. */
    public void execute(CorpusController controller) throws GateException {controller.execute();} // execute()

    /** Run GATE. */
    private int exeTentative = 0;
    public void executeWithTentatives(CorpusController controller) throws GateException {
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
        //for each url we create a corpus
        try{
          if(url!=null){
            corpus = GateCorpusKit.createCorpusByUrl(url, "GeoDocuments Corpus", i);
           }//if url!=null
          else if(content!=null || url ==null){
              corpus = Factory.newCorpus("GeoDocuments Corpus");
              Document doc =(Document) Factory.newDocument(content);
              corpus.add(doc);

          }
          if(corpus == null){return null;}
          else{
              //settiamo il corpus
              setCorpus(corpus, controller);
              //carichiamo i documenti del corpus sulla nostra pipeline
              //su cui è caricato il file .gapp/.xgapp
              SystemLog.message("Execute of GATE in process for the url "+url+"...");
              execute(controller);
              SystemLog.message(".. GATE is been processed");
            //andiamo a trasformare il contenuto matchato dalle nostre annotazioni
            //in keyword che andremo a inserire nel nostro apposito GeoDocument.
              annInfo =  new AnnotationInfo(null,null, null, null, null, null, null, null, null, null, null);;
              annInfo = GateAnnotationKit.getSingleAnnotationInfo(corpus,null);
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
              SystemLog.ticket(ex.getMessage(), "ERROR");
              ex.printStackTrace();
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
          ArrayList<GeoDocument> listGeo = new ArrayList<GeoDocument>();
          for(URL url : listUrl){
              GeoDocument g = new GeoDocument();
              g = extractMicrodataWithGATESingleUrl(url,content,controller,i);
              listGeo.add(g);
          }
      return listGeo;                 
     }//extractMicrodataWithGATEMultipleUrls

    public ArrayList<GeoDocument> extractMicrodataWithGATEMultipleFiles(ArrayList<File> listFile,String content,CorpusController controller,Integer i) throws MalformedURLException, InterruptedException, SQLException, InvocationTargetException {
        ArrayList<GeoDocument> listGeo = new ArrayList<GeoDocument>();
        for(File file : listFile){
            GeoDocument g = new GeoDocument();
            g = extractMicrodataWithGATESingleUrl(FileUtil.convertFileToUri(file).toURL(),content,controller,i);
            listGeo.add(g);
        }
        return listGeo;
    }//extractMicrodataWithGATEMultipleFiles

    public void loadCorpusOnADataStore(Corpus corpus) throws gate.security.SecurityException, PersistenceException, ResourceInstantiationException {
         try {
                //System.out.println("1");
                GateDataStoreKit.openDataStore();
                GateDataStoreKit.setDataStoreWithACorpus(corpus);
                GateDataStoreKit.saveACorpusOnTheDataStore(corpus, GateDataStoreKit.getDS_DIR());
                //datastore.closeDataStore();
            } catch (Exception e) {
                //System.out.println("2");
                GateDataStoreKit.openDataStore();
                GateDataStoreKit.saveACorpusOnTheDataStore(corpus, GateDataStoreKit.getDS_DIR());
                //MERGE DEI CORPUS
//               ArrayList<Corpus> listCorpus = GateDataStoreKit.loadAllCorpusOnTheDataStore();
//               if(listCorpus.size()>1){
//                    String nome_corpus_2 ="GeoDocuments_Corpus";
//                    corpus = GateDataStoreKit.mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(nome_corpus_2, listCorpus);
//               }
//                GateDataStoreKit.closeDataStore();

            } finally {
             //CODICE CHE FA IL MERGE DI TUTTI I CORPUS PRESENTI NEL DATASTORE
             GateDataStoreKit.openDataStore();
             ArrayList<Corpus> listCorpus = GateDataStoreKit.loadAllCorpusOnTheDataStore();
             if (listCorpus.size() > 1) {
                 String nome_corpus_2 = "GeoDocuments_Corpus_" + "[]";
                 corpus = GateDataStoreKit.mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(nome_corpus_2, listCorpus);
                 GateDataStoreKit.saveACorpusOnTheDataStore(corpus, GateDataStoreKit.getDS_DIR());
             }
             GateDataStoreKit.closeDataStore();

         }
    }//loadCorpusOnADataStore


	  
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
     }//pulisciAnnotionInfo
      
}
