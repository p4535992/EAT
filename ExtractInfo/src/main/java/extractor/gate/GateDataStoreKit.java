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
import extractor.SystemLog;
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
      SystemLog.ticket("Datastore directory:" + DS_DIR, "OUT");
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
    SystemLog.ticket("Datastore directory:" + DS_DIR, "OUT");
    SystemLog.ticket("Nome DataStore:" + NOME_DATASTORE, "OUT");
    try {
      //insert&open a new Serial Data Store
      //pass the datastore class and path as parameteres
      sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
      sds.setName(NOME_DATASTORE);      
      SystemLog.ticket("Serial datastore created...", "OUT");
      //insert test corpus
      // SecurityInfo is ingored for SerialDataStore - just pass null
      // a new persisent corpus is returned     
      Corpus persistCorp = null;
      persistCorp = (Corpus)sds.adopt(corp,null);
      sds.sync(persistCorp);
      SystemLog.ticket("Corpus saved in datastore...", "OUT");
      Object corpusID  = persistCorp.getLRPersistenceId();
      SystemLog.ticket(corpusID.toString(), "OUT");
    }
    catch(gate.persist.PersistenceException ex) {
      //ex.printStackTrace();    
      SystemLog.ticket("Il datastore esiste già....", "WARNING");
    }finally{   
    }
  }
   /**change name to the corpus and sync it with the datastore. */
   public static void changeNameCorpus(Corpus persistCorp,String nameCorpus) throws PersistenceException, SecurityException{
      persistCorp.setName(nameCorpus);      
      persistCorp.sync();    
      SystemLog.ticket("Cambiato nome al Corpus:" + persistCorp.getName() + " dal DataStore con " + nameCorpus, "OUT");
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
      SystemLog.ticket("corpus " + persistCorp.getName() + " caricato dal datastore...", "OUT");
      return persistCorp;
      
   }
   /**Remove corpus from datastore. */
   public static void removeCorpusDataStoreById(Corpus persistCorp,Object corpusID) throws PersistenceException {
      sds.delete("gate.corpora.SerialCorpusImpl", corpusID);
      SystemLog.ticket("corpus " + persistCorp.getName() + " cancellato dal datastore " + sds.getName() + "!!!", "OUT");
      persistCorp = null;
   }
   
   /**Close the DataStore. */
   public static void closeDataStore() throws PersistenceException{
      sds.close();
      //sds = null;
      SystemLog.ticket("Datastore " + sds.getName() + " chiuso!!!", "OUT");
   }
   
   /**Delete the DataStore. */
   public static void deleteDataStore() throws PersistenceException{
      sds.delete();    
      SystemLog.ticket("Datastore " + sds.getName() + " cancellato!!!", "OUT");
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
       SystemLog.ticket("Datastore " + sds.getName() + " aperto!!!", "OUT");
      }catch(gate.persist.PersistenceException e){
          try {
              sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
              sds.setName(NOME_DATASTORE);
              sds.open();
              SystemLog.ticket("Datastore " + sds.getName() + " aperto!!!", "OUT");
          } catch (PersistenceException ex) {
              SystemLog.ticket(ex.getMessage(), "ERROR");
              Logger.getLogger(GateDataStoreKit.class.getName()).log(Level.SEVERE, null, ex);
          } catch (UnsupportedOperationException ex) {
              SystemLog.ticket(ex.getMessage(), "ERROR");
              Logger.getLogger(GateDataStoreKit.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
   }
   public static void openDataStore() {
      try{
       if((DS_DIR!=null) || (sds==null)){
          sds = new SerialDataStore(DS_DIR);
          sds.setName(NOME_DATASTORE);
       }
            SystemLog.ticket("Datastore directory:" + DS_DIR, "OUT");
            //sds =(SerialDataStore) Factory.openDataStore("gate.persist.SerialDataStore","file://"+DS_DIR);
            sds.open();
            SystemLog.ticket("Datastore " + sds.getName() + " aperto!!!", "OUT");
    } catch(gate.persist.PersistenceException e){
          try {
              sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
              sds.setName(NOME_DATASTORE);
              sds.open();
              SystemLog.ticket("Datastore directory:" + DS_DIR, "OUT");
          } catch (PersistenceException ex) {
              SystemLog.ticket(ex.getMessage(), "ERROR");
              Logger.getLogger(GateDataStoreKit.class.getName()).log(Level.SEVERE, null, ex);
          } catch (UnsupportedOperationException ex) {
              SystemLog.ticket(ex.getMessage(), "ERROR");
              Logger.getLogger(GateDataStoreKit.class.getName()).log(Level.SEVERE, null, ex);
          }
        
    }
   }

   /**Save Document on the DataStore. */
   public static Document saveDocumentOnDataStore(Document persistDoc,Document doc,SecurityInfo securityInfo) throws PersistenceException, SecurityException{
      //SecurityInfo is ingored for SerialDataStore - just pass null
      persistDoc = (Document)sds.adopt(doc,securityInfo);
      sds.sync(persistDoc);
      SystemLog.ticket("Document " + doc.getName() + " save to Datastore with the name " + persistDoc.getName(), "OUT");
      return persistDoc;
   }

   /**Change the name of the Document doc on the DataStore. */
   public static void changeDocumentNameWithTheDataStore(Document persistDoc,String newName) throws PersistenceException, SecurityException{
       String oldName = persistDoc.getName();
       persistDoc.setName(newName);
       persistDoc.sync();
       SystemLog.ticket("Document: " + oldName + " on the Datastore has a new name: " + persistDoc.getName(), "OUT");
   }
   
   /** Load document from datastore. */
   public static Document loadDocumentFromDataStore(Document persistDoc,Object docID) throws ResourceInstantiationException{
      //persistDoc = (Document)sds.adopt(doc,securityInfo);
      //sds.sync(persistDoc);
      FeatureMap docFeatures = Factory.newFeatureMap();
      docFeatures.put(DataStore.LR_ID_FEATURE_NAME, docID);
      docFeatures.put(DataStore.DATASTORE_FEATURE_NAME, sds);

      persistDoc = (Document)Factory.createResource("gate.corpora.DocumentImpl", docFeatures);
      SystemLog.ticket("Document" + persistDoc.getName() + " is loaded from the DataStore!!!", "OUT");
      return persistDoc;
   }
   
   /**Delete document from the DataStore. */
   public static void deleteDocumentFromDataStore(Document persistDoc) throws PersistenceException{
      Object docID  = persistDoc.getLRPersistenceId();
      sds.delete("gate.corpora.DocumentImpl", docID);
      SystemLog.ticket("Il documento" + persistDoc.getName() + " è stato cancellato dal DataStore!!!", "OUT");
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
           SystemLog.ticket("(" + i + ")" + "CORPUS NAME:" + c.getName(), "OUT");
           SystemLog.ticket("(" + i + ")" + "ID NAME:" + c.getLRPersistenceId(), "OUT");
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
           SystemLog.ticket("************" + name_corpus.toUpperCase() + "**************", "OUT");
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               SystemLog.ticket("Document nel Corpus:" + new_name_doc, "OUT");
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
           SystemLog.ticket("************" + name_corpus.toUpperCase() + "**************", "OUT");
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               SystemLog.ticket("Document nel Corpus:" + new_name_doc, "OUT");
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
      SystemLog.ticket("corpus saved in datastore...", "OUT");
      Object corpusID  = persistCorp.getLRPersistenceId();
      SystemLog.ticket("ID del Corpus salvato nel datastore:" + corpusID.toString(), "OUT");
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
    

