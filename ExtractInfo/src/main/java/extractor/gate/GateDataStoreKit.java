/**
 * DatastoreApplication.java 
 * @author 4535992
 * @description
 * Class prevede il salvataggio del Corpus di GATE
 * in un Serial Lucene Datastore e tutti i metodi necessari per svolgere le
 * operazioni di CRUD sia per il corpus che per i singoli documenti direttamente 
 * dal DataStore.
 * NON E STATA INTEGRATA IN QUESTO PROGETTO MA PER INVOCARLA CI VUOLE POCO
 */
package extractor.gate;
import p4535992.util.log.SystemLog;
import gate.persist.SerialDataStore;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.security.SecurityException;
import gate.security.SecurityInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import gate.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GateDataStoreKit {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GateDataStoreKit.class);
  //the directory must EXIST and be EMPTY
  //private static final String dsDir = "/var/tmp/gate001";
  private static String DS_DIR = null;
  private static String NOME_DATASTORE = null;
  private static SerialDataStore sds = null;
  
  public GateDataStoreKit(){}

  public GateDataStoreKit(String DS_DIR, String NOME_DATASTORE) {
      //Path rep = Paths.get(DS_DIR).toAbsolutePath();
      Path rep = Paths.get(DS_DIR+"/"+NOME_DATASTORE).toAbsolutePath();
      DS_DIR = "file:///"+rep.toString();
      //DS_DIR = rep.toString();
      SystemLog.message("Datastore directory:" + DS_DIR);
      //System.out.println(NOME_DATASTORE);     
      this.DS_DIR = DS_DIR;
      this.NOME_DATASTORE = NOME_DATASTORE;    
  }

    public static void setDataStore(String DS_DIR,String NOME_DATASTORE){
        //Path rep = Paths.get(DS_DIR).toAbsolutePath();
        Path rep = Paths.get(DS_DIR + File.separator + NOME_DATASTORE).toAbsolutePath();
        DS_DIR = rep.toString();
        //DS_DIR = rep.toString();
        SystemLog.message("Datastore directory is located on :" + DS_DIR);
        //System.out.println(NOME_DATASTORE);
        DS_DIR = DS_DIR;
        NOME_DATASTORE = NOME_DATASTORE;
    }

    public static GateDataStoreKit getNewDataStore(){
        GateDataStoreKit datastore = new GateDataStoreKit(DS_DIR,NOME_DATASTORE);
        return datastore;
    }
  
  /**
   * Operazioni CRUD nel Datastore
   * @param corp il Corpus di GATE  da salvare nel DataStore
   * @throws IOException
   * @throws PersistenceException 
   */
  public static void setDataStoreWithACorpus(Corpus corp) throws IOException, PersistenceException, SecurityException {
    //Inizializzazione di GATE avvenuta 
    //gate.init() --- Gate.init()
    //Creazione e settaggio del Corpus avvenuta
    //gate.setCorpus(corpus)  
    SystemLog.message("Datastore directory:" + DS_DIR + "\nNome DataStore:" + NOME_DATASTORE);
    try {
      //insert&open a new Serial Data Store
      //pass the datastore class and path as parameteres
      sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
      sds.setName(NOME_DATASTORE);      
      SystemLog.message("Serial datastore created...");
      //insert test corpus
      // SecurityInfo is ingored for SerialDataStore - just pass null
      // a new persisent corpus is returned     
      Corpus persistCorp = null;
      persistCorp = (Corpus)sds.adopt(corp,null);
      sds.sync(persistCorp);
      SystemLog.message("Corpus saved in datastore...");
      Object corpusID  = persistCorp.getLRPersistenceId();
      SystemLog.message(corpusID.toString());
    }
    catch(gate.persist.PersistenceException ex) {
      //ex.printStackTrace();    
      SystemLog.message("The datastore already exists....");
    }finally{   
    }
  }
   /**change name to the corpus and sync it with the datastore. */
   public static void changeNameCorpus(Corpus persistCorp,String nameCorpus) throws PersistenceException, SecurityException{
      persistCorp.setName(nameCorpus);      
      persistCorp.sync();    
      SystemLog.message("Change name of the Corpus:" + persistCorp.getName() + " on the datastoe with name " + nameCorpus);
   }
   /**Load corpus from datastore using its persistent ID. */
   public static Corpus loadCorpusDataStoreById(Object corpusID) throws ResourceInstantiationException, PersistenceException {
      openDataStore(DS_DIR);     
      Corpus persistCorp = null;
      FeatureMap corpFeatures = Factory.newFeatureMap();
      corpFeatures.put(DataStore.LR_ID_FEATURE_NAME, corpusID);
      corpFeatures.put(DataStore.DATASTORE_FEATURE_NAME, sds);
      //tell the factory to load the Serial Corpus with the specified ID from the specified  datastore
      persistCorp = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", corpFeatures);
      SystemLog.message("Corpus " + persistCorp.getName() + " loaded on the datastore...");
      return persistCorp;
      
   }
   /**Remove corpus from datastore. */
   public static void removeCorpusDataStoreById(Corpus persistCorp,Object corpusID) throws PersistenceException {
      sds.delete("gate.corpora.SerialCorpusImpl", corpusID);
      SystemLog.message("Corpus " + persistCorp.getName() + " deleted from the datastore " + sds.getName() + "!!!");
      persistCorp = null;
   }
   
   /**Close the DataStore. */
   public static void closeDataStore() throws PersistenceException{
      sds.close();
      //sds = null;
      SystemLog.message("Datastore " + sds.getName() + " closed!!!");
   }
   
   /**Delete the DataStore. */
   public static void deleteDataStore() throws PersistenceException{
      sds.delete();    
      SystemLog.message("Datastore " + sds.getName() + " deleted!!!");
   }
   
   /** open-reopen DataStore. */
   public static void openDataStore(String absolutePathDirectory) {
      try{
       if((absolutePathDirectory!=null) || (sds==null)){
          sds = new SerialDataStore(absolutePathDirectory);
          sds.setName(NOME_DATASTORE);
          //oppure
          //DataStore ds = Factory.openDataStore("gate.persist.SerialDataStore","file://"+absolutePathDirectory);
       }
       sds.open();
       SystemLog.message("Datastore " + sds.getName() + " opened!!!");
      }catch(gate.persist.PersistenceException e){
          try {
              sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
              sds.setName(NOME_DATASTORE);
              sds.open();
              SystemLog.message("Datastore " + sds.getName() + " opened!!!");
          } catch (PersistenceException ex) {
              SystemLog.exception(ex);
          } catch (UnsupportedOperationException ex) {
              SystemLog.exception(ex);
          }
      }
   }
   public static void openDataStore() {
      try{
           if((DS_DIR!=null) || (sds==null)){
              sds = new SerialDataStore(DS_DIR);
              sds.setName(NOME_DATASTORE);
           }
           SystemLog.message("Try to open the Datastore on directory:" + DS_DIR+"...");
           //sds =(SerialDataStore) Factory.openDataStore("gate.persist.SerialDataStore","file://"+DS_DIR);
           sds.open();
           SystemLog.message("...opened Datastore " + sds.getName() + "!!!");
    } catch(gate.persist.PersistenceException e){
          try {
              sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
              sds.setName(NOME_DATASTORE);
              sds.open();
              SystemLog.message("Try to opne the Datastore directory:" + DS_DIR+"...");
          } catch (PersistenceException ex) {
              SystemLog.exception(ex);
          } catch (UnsupportedOperationException ex) {
              SystemLog.exception(ex);
          }
        
    }
   }

   /**Save Document on the DataStore. */
   public static Document saveDocumentOnDataStore(Document persistDoc,Document doc,SecurityInfo securityInfo) throws PersistenceException, SecurityException{
      //SecurityInfo is ingored for SerialDataStore - just pass null
      persistDoc = (Document)sds.adopt(doc,securityInfo);
      sds.sync(persistDoc);
      SystemLog.message("Document " + doc.getName() + " save to Datastore with the name " + persistDoc.getName());
      return persistDoc;
   }

   /**Change the name of the Document doc on the DataStore. */
   public static void changeDocumentNameWithTheDataStore(Document persistDoc,String newName) throws PersistenceException, SecurityException{
       String oldName = persistDoc.getName();
       persistDoc.setName(newName);
       persistDoc.sync();
       SystemLog.message("Document: " + oldName + " on the Datastore has a new name: " + persistDoc.getName());
   }
   
   /** Load document from datastore. */
   public static Document loadDocumentFromDataStore(Document persistDoc,Object docID) throws ResourceInstantiationException{
      //persistDoc = (Document)sds.adopt(doc,securityInfo);
      //sds.sync(persistDoc);
      FeatureMap docFeatures = Factory.newFeatureMap();
      docFeatures.put(DataStore.LR_ID_FEATURE_NAME, docID);
      docFeatures.put(DataStore.DATASTORE_FEATURE_NAME, sds);
      persistDoc = (Document)Factory.createResource("gate.corpora.DocumentImpl", docFeatures);
      SystemLog.message("Document" + persistDoc.getName() + " is loaded from the DataStore!!!");
      return persistDoc;
   }
   
   /**Delete document from the DataStore. */
   public static void deleteDocumentFromDataStore(Document persistDoc) throws PersistenceException{
      Object docID  = persistDoc.getLRPersistenceId();
      sds.delete("gate.corpora.DocumentImpl", docID);
      SystemLog.message("The document " + persistDoc.getName() + " is deleted from the DataStore!!!");
      persistDoc = null;
   }
   
   /**Load all the corpus on the DataStore. */
   public static ArrayList<Corpus> loadAllCorpusOnTheDataStore() throws PersistenceException, ResourceInstantiationException{
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
           SystemLog.message("(" + i + ")" + "CORPUS NAME:" + c.getName());
           SystemLog.message("(" + i + ")" + "ID NAME:" + c.getLRPersistenceId());
           listCorpus.add(c);
       }
       //Corpus c = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", fm);     
       return listCorpus;
       // similarly for documents, just use gate.corpora.DocumentImpl as the class name
   }       
   
   /**Copy all document on a corpus to another corpus. */
   public static Corpus mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(String nome_merge_corpus,ArrayList<Corpus> listCorpus) throws PersistenceException, ResourceInstantiationException{
       Corpus finalCorpus = Factory.newCorpus(nome_merge_corpus);
       //listCorpus = loadAllCorpusOnTheDataStore();
       for(Corpus corpus: listCorpus){
           String name_corpus = corpus.getName();
           SystemLog.message(name_corpus.toUpperCase());
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               SystemLog.message("Document of Corpus:" + new_name_doc);
               doc.setName(new_name_doc);
               finalCorpus.add(doc);
           }
       }
       return finalCorpus;
   }
   
   /**Copy all document on a corpus to another corpus. */
   public static Corpus mergeDocumentsOfMultipleCorpusWithoutChooseOfCorpusOnTheDataStore(String nome_merge_corpus) throws PersistenceException, ResourceInstantiationException{
       Corpus finalCorpus = Factory.newCorpus(nome_merge_corpus);
       ArrayList<Corpus> listCorpus = loadAllCorpusOnTheDataStore();
       for(Corpus corpus: listCorpus){
           String name_corpus = corpus.getName();
           SystemLog.message(name_corpus.toUpperCase());
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               SystemLog.message("Document of the Corpus:" + new_name_doc);
               doc.setName(new_name_doc);
               finalCorpus.add(doc);
           }
       }
       return finalCorpus;
   }
   
   public static void saveACorpusOnTheDataStore(Corpus corp,String pathToDataStore) throws PersistenceException, SecurityException{
      //insert test corpus
      Corpus persistCorp = null;
      //save corpus in datastore,SecurityInfo is ingored for SerialDataStore - just pass null
      // a new persisent corpus is returned
      persistCorp = (Corpus)sds.adopt(corp,null);
      sds.sync(persistCorp);    
      SystemLog.message("corpus saved in datastore...");
      Object corpusID  = persistCorp.getLRPersistenceId();
      SystemLog.message("ID del Corpus salvato nel datastore:" + corpusID.toString());
   }

    //SETTER AND GETTER
    public static String getDS_DIR() {
        return DS_DIR;
    }

    public static void setDS_DIR(String DS_DIR) {
        GateDataStoreKit.DS_DIR = DS_DIR;
    }

    public static String getNOME_DATASTORE() {
        return NOME_DATASTORE;
    }

    public static void setNOME_DATASTORE(String NOME_DATASTORE) {
        GateDataStoreKit.NOME_DATASTORE = NOME_DATASTORE;
    }
   
}
    

