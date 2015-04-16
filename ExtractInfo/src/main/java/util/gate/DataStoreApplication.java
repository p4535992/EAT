/**
 * DatastoreApplication.java 
 * @author Tenti Marco Elaborato Sistemi Distribuiti
 * Classe opzionale da usare solo se vi sono problemi di memoria RAM.
 * (sono 6.000.00 di documenti web),prevede il salvataggio del Corpus di GATE
 * in un Serial Lucene Datastore e tutti i metodi necessari per svolgere le
 * operazioni di CRUD sia per il corpus che per i singoli documenti direttamente 
 * dal DataStore.
 * NON E STATA INTEGRATA IN QUESTO PROGETTO MA PER INVOCARLA CI VUOLE POCO
 */
package util.gate;
import gate.persist.SerialDataStore;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.security.SecurityException;
import gate.security.SecurityInfo;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import gate.*;
import util.SystemLog;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataStoreApplication {
    
  //the directory must EXIST and be EMPTY
  //private static final String dsDir = "/var/tmp/gate001";
  private static String DS_DIR = null;
  private static String NOME_DATASTORE = null;
  private static SerialDataStore sds = null;
  
  public DataStoreApplication(){}

  public DataStoreApplication(String DS_DIR,String NOME_DATASTORE) {
      //Path rep = Paths.get(DS_DIR).toAbsolutePath();
      Path rep = Paths.get(DS_DIR+"/"+NOME_DATASTORE).toAbsolutePath();
      DS_DIR = "file:///"+rep.toString();
      //DS_DIR = rep.toString();
      SystemLog.write("Datastore directory:" + DS_DIR, "OUT");
      //System.out.println(NOME_DATASTORE);     
      this.DS_DIR = DS_DIR;
      this.NOME_DATASTORE = NOME_DATASTORE;    
  }
  
  /**
   * Operazioni CRUD nel Datastore
   * @param corp il Corpus di GATE  da salvare nel DataStore
   * @throws IOException
   * @throws PersistenceException 
   */
  public void setDataStoreWithACorpus(Corpus corp) throws IOException, PersistenceException, SecurityException {
    //Inizializzazione di GATE avvenuta 
    //gate.init() --- Gate.init()
    //Creazione e settaggio del Corpus avvenuta
    //gate.setCorpus(corpus)  
    SystemLog.write("Datastore directory:" + DS_DIR, "OUT");
    SystemLog.write("Nome DataStore:" + NOME_DATASTORE, "OUT");
    try {
      //create&open a new Serial Data Store
      //pass the datastore class and path as parameteres
      sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
      sds.setName(NOME_DATASTORE);      
      SystemLog.write("Serial datastore created...", "OUT");
      //create test corpus     
      // SecurityInfo is ingored for SerialDataStore - just pass null
      // a new persisent corpus is returned     
      Corpus persistCorp = null;
      persistCorp = (Corpus)sds.adopt(corp,null);
      sds.sync(persistCorp);
      SystemLog.write("Corpus saved in datastore...", "OUT");
      Object corpusID  = persistCorp.getLRPersistenceId();
      SystemLog.write(corpusID.toString(), "OUT");
    }
    catch(gate.persist.PersistenceException ex) {
      //ex.printStackTrace();    
      SystemLog.write("Il datastore esiste già....", "WARNING");
    }finally{   
    }
  }
   /**change name to the corpus and sync it with the datastore. */
   public void changeNameCorpus(Corpus persistCorp,String nameCorpus) throws PersistenceException, SecurityException{
      persistCorp.setName(nameCorpus);      
      persistCorp.sync();    
      SystemLog.write("Cambiato nome al Corpus:" + persistCorp.getName() + " dal DataStore con " + nameCorpus, "OUT");
   }
   /**Load corpus from datastore using its persistent ID. */
   public Corpus loadCorpusDataStoreById(Object corpusID) throws ResourceInstantiationException, PersistenceException {            
      openDataStore(DS_DIR);     
      Corpus persistCorp = null;
      FeatureMap corpFeatures = Factory.newFeatureMap();
      corpFeatures.put(DataStore.LR_ID_FEATURE_NAME, corpusID);
      corpFeatures.put(DataStore.DATASTORE_FEATURE_NAME, sds);
      //tell the factory to load the Serial Corpus with the specified ID from the specified  datastore
      persistCorp = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", corpFeatures);
      SystemLog.write("corpus " + persistCorp.getName() + " caricato dal datastore...", "OUT");
      return persistCorp;
      
   }
   /**Remove corpus from datastore. */
   public void removeCorpusDataStoreById(Corpus persistCorp,Object corpusID) throws PersistenceException {
      sds.delete("gate.corpora.SerialCorpusImpl", corpusID);
      SystemLog.write("corpus " + persistCorp.getName() + " cancellato dal datastore " + sds.getName() + "!!!", "OUT");
      persistCorp = null;
   }
   
   /**Close the DataStore. */
   public void closeDataStore() throws PersistenceException{
      sds.close();
      //sds = null;
      SystemLog.write("Datastore " + sds.getName() + " chiuso!!!", "OUT");
   }
   
   /**Delete the DataStore. */
   public void deleteDataStore() throws PersistenceException{
      sds.delete();    
      SystemLog.write("Datastore " + sds.getName() + " cancellato!!!", "OUT");
   }
   
   /** open-reopen DataStore. */
   public void openDataStore(String absolutePathDirectory) {
      try{
       if((absolutePathDirectory!=null) || (sds==null)){
          sds = new SerialDataStore(absolutePathDirectory);
          sds.setName(NOME_DATASTORE);
          //oppure
          //DataStore ds = Factory.openDataStore("gate.persist.SerialDataStore","file://"+absolutePathDirectory);
       }
       sds.open();
       SystemLog.write("Datastore " + sds.getName() + " aperto!!!", "OUT");
      }catch(gate.persist.PersistenceException e){
          try {
              sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
              sds.setName(NOME_DATASTORE);
              sds.open();
              SystemLog.write("Datastore " + sds.getName() + " aperto!!!", "OUT");
          } catch (PersistenceException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              Logger.getLogger(DataStoreApplication.class.getName()).log(Level.SEVERE, null, ex);
          } catch (UnsupportedOperationException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              Logger.getLogger(DataStoreApplication.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
   }
   public void openDataStore() {
      try{
       if((DS_DIR!=null) || (sds==null)){
          sds = new SerialDataStore(DS_DIR);
          sds.setName(NOME_DATASTORE);
       }
            SystemLog.write("Datastore directory:" + DS_DIR, "OUT");
            //sds =(SerialDataStore) Factory.openDataStore("gate.persist.SerialDataStore","file://"+DS_DIR);
            sds.open();
            SystemLog.write("Datastore " + sds.getName() + " aperto!!!", "OUT");
    } catch(gate.persist.PersistenceException e){
          try {
              sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
              sds.setName(NOME_DATASTORE);
              sds.open();
              SystemLog.write("Datastore directory:" + DS_DIR, "OUT");
          } catch (PersistenceException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              Logger.getLogger(DataStoreApplication.class.getName()).log(Level.SEVERE, null, ex);
          } catch (UnsupportedOperationException ex) {
              SystemLog.write(ex.getMessage(), "ERROR");
              Logger.getLogger(DataStoreApplication.class.getName()).log(Level.SEVERE, null, ex);
          }
        
    }
   }

   /**Save Document on the DataStore. */

   public Document saveDocumentOnDataStore(Document persistDoc,Document doc,SecurityInfo securityInfo) throws PersistenceException, SecurityException{
      //SecurityInfo is ingored for SerialDataStore - just pass null
      persistDoc = (Document)sds.adopt(doc,securityInfo);
      sds.sync(persistDoc);
      SystemLog.write("Documento " + doc.getName() + " salvato nel DataStore come Documento " + persistDoc.getName(), "OUT");
      return persistDoc;
   }

   /**Change the name of the Document doc on the DataStore. */
   public void changeDocumentNameWithTheDataStore(Document persistDoc,String newName) throws PersistenceException, SecurityException{
       persistDoc.setName(newName);
       persistDoc.sync();
       SystemLog.write("Documento del DataStore ha un nuovo nome " + persistDoc.getName(), "OUT");
   }
   
   /** Load document from datastore. */
   public Document loadDocumentFromDataStore(Document persistDoc,Object docID) throws ResourceInstantiationException{
      //persistDoc = (Document)sds.adopt(doc,securityInfo);
      //sds.sync(persistDoc);
      persistDoc = null;

      FeatureMap docFeatures = Factory.newFeatureMap();
      docFeatures.put(DataStore.LR_ID_FEATURE_NAME, docID);
      docFeatures.put(DataStore.DATASTORE_FEATURE_NAME, sds);

      persistDoc = (Document)Factory.createResource("gate.corpora.DocumentImpl", docFeatures);
      SystemLog.write("Il documento" + persistDoc.getName() + " è stato caricato dal DataStore!!!", "OUT");
      return persistDoc;
   }
   
   /**Delete document from the DataStore. */
   public void deleteDocumentFromDataStore(Document persistDoc) throws PersistenceException{
      Object docID  = persistDoc.getLRPersistenceId();
      sds.delete("gate.corpora.DocumentImpl", docID);
      SystemLog.write("Il documento" + persistDoc.getName() + " è stato cancellato dal DataStore!!!", "OUT");
      persistDoc = null;
   }
   
   /**Load all the corpus on the DataStore. */
   public ArrayList<Corpus> loadAllCorpusOnTheDataStore() throws PersistenceException, ResourceInstantiationException{
       // list the corpora/documents in the DS       
       openDataStore(DS_DIR);
       List corpusIds = sds.getLrIds("gate.corpora.SerialCorpusImpl");
       ArrayList<Corpus> listCorpus = new ArrayList<Corpus>();
//       FeatureMap fm = Factory.newFeatureMap();
//       fm.put(DataStore.DATASTORE_FEATURE_NAME, sds);
//       fm.put(DataStore.LR_ID_FEATURE_NAME, corpusIds.get(0));
       //*********************************************************
       //corpusIds.set(0, corpusIds.get(0));
       //fm[DataStore.DATASTORE_FEATURE_NAME] = ds;
       //fm[DataStore.LR_ID_FEATURE_NAME] = corpusIds[0];
       //List docIds = ds.getLrIds("gate.corpora.DocumentImpl");
       
       for(int i =0; i < corpusIds.size(); i++){
           System.out.println("("+i+")"+"ID CORPUS:"+corpusIds.get(i).toString());    
           FeatureMap fm = Factory.newFeatureMap();
           fm.put(DataStore.DATASTORE_FEATURE_NAME, sds);
           fm.put(DataStore.LR_ID_FEATURE_NAME, corpusIds.get(i));
           Corpus c = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", fm);
           SystemLog.write("(" + i + ")" + "CORPUS NAME:" + c.getName(), "OUT");
           SystemLog.write("(" + i + ")" + "ID NAME:" + c.getLRPersistenceId(), "OUT");
           listCorpus.add(c);
       }
       //Corpus c = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", fm);     
       return listCorpus;
       // similarly for documents, just use gate.corpora.DocumentImpl as the class name
   }       
   
   /**Copy all document on a corpus to another corpus. */
   public Corpus mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(String nome_merge_corpus,ArrayList<Corpus> listCorpus) throws PersistenceException, ResourceInstantiationException{
       Corpus finalCorpus = Factory.newCorpus(nome_merge_corpus);
       //listCorpus = loadAllCorpusOnTheDataStore();
       for(Corpus corpus: listCorpus){
           String name_corpus = corpus.getName();
           SystemLog.write("************" + name_corpus.toUpperCase() + "**************", "OUT");
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               SystemLog.write("Document nel Corpus:" + new_name_doc, "OUT");
               doc.setName(new_name_doc);
               finalCorpus.add(doc);
           }
       }
       return finalCorpus;
   }
   
   /**Copy all document on a corpus to another corpus. */
   public Corpus mergeDocumentsOfMultipleCorpusWithoutChooseOfCorpusOnTheDataStore(String nome_merge_corpus) throws PersistenceException, ResourceInstantiationException{
       Corpus finalCorpus = Factory.newCorpus(nome_merge_corpus);
       ArrayList<Corpus> listCorpus = loadAllCorpusOnTheDataStore();
       for(Corpus corpus: listCorpus){
           String name_corpus = corpus.getName();
           SystemLog.write("************" + name_corpus.toUpperCase() + "**************", "OUT");
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               SystemLog.write("Document nel Corpus:" + new_name_doc, "OUT");
               doc.setName(new_name_doc);
               finalCorpus.add(doc);
           }
       }
       return finalCorpus;
   }
   
   public void saveACorpusOnTheDataStore(Corpus corp,String pathToDataStore) throws PersistenceException, SecurityException{
      //sds.open();
       //create test corpus
      //Corpus corp = dsApp.createTestCorpus();
      //openDataStore(pathToDataStore);     
      Corpus persistCorp = null;
      //save corpus in datastore
      // SecurityInfo is ingored for SerialDataStore - just pass null
      // a new persisent corpus is returned
      persistCorp = (Corpus)sds.adopt(corp,null);
      sds.sync(persistCorp);    
      SystemLog.write("corpus saved in datastore...", "OUT");
      Object corpusID  = persistCorp.getLRPersistenceId();
      SystemLog.write("ID del Corpus salvato nel datastore:" + corpusID.toString(), "OUT");
      //closeDataStore();
      //sds.close();
   }

    //SETTER AND GETTER
    public static String getDS_DIR() {
        return DS_DIR;
    }

    public static void setDS_DIR(String DS_DIR) {
        DataStoreApplication.DS_DIR = DS_DIR;
    }

    public static String getNOME_DATASTORE() {
        return NOME_DATASTORE;
    }

    public static void setNOME_DATASTORE(String NOME_DATASTORE) {
        DataStoreApplication.NOME_DATASTORE = NOME_DATASTORE;
    }
   
}
    

