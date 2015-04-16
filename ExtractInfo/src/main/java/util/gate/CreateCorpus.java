package util.gate;
/**
 *  CreateCorpus.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti
 * Crea un GATE Document per ogni URL della tabella website del database UrlDB
 * ogni volta che viene creato un documento lo inserisce nel Corpus di GATE
 * quando per tutti gli URL che ci interessano sono stati elaborati il Corpus
 * è completo e lo andiamo a restituire alla main class MainGate.java 
 * per poterci girare sopra
 * il file .gapp/.xgapp
*/
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.security.SecurityException;
import gate.security.SecurityInfo;
import java.util.*;
import java.io.*;
import java.net.*;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.*;
import util.SystemLog;

public class CreateCorpus { 
  
  public CreateCorpus(){}
  private static int xx=0;
  /**
    * Crea un Corpus di Documenti Gate
    * @param  url l'url relativo alla singola pagina web da convertire in GATE Document
    * @param  nomeCorpus il nome assegnato al Corpus in esame
    * @return il corpus "riempito" di GATE
    */
  //public Corpus createCorpusByUrl(ArrayList<URL> listUrl,String nomeCorpus) throws IOException, ResourceInstantiationException{
    public Corpus createCorpusByUrl(URL url,String nomeCorpus,Integer indice) throws IOException, ResourceInstantiationException{
   // int j=0;
    Corpus corpus = Factory.newCorpus(nomeCorpus);   
    try {
        //for(int i = 0; i < listUrl.size(); i++) {         
          try{ 
                //Qui si crea un documento per ogni URL con le sue feature e annotazioni
                //document features create with GATE
                FeatureMap params = Factory.newFeatureMap();
                //URL url = listUrl.get(i); 
                xx++;
                params.put("sourceUrl", url);                            
                params.put("preserveOriginalContent", new Boolean(true));
                params.put("collectRepositioningInfo", new Boolean(true));
                params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME,"UTF-8");
                //*********************************************************
                //Out.prln("("+xx+")Creating doc for " + listUrl.get(i));
                //********************************************************
                
                //document features create by me
                FeatureMap feats =Factory.newFeatureMap();
                feats.put("date",new Date());
                //creazione del documento        
                Document doc = 
                        (Document)Factory.createResource("gate.corpora.DocumentImpl", 
                                /*params,feats,"doc_"+i+"_"+listUrl.get(i));*/
                                params,feats,"doc_"+indice+"_"+url);
                //Out.prln(doc.getFeatures().get("city").toString());
                corpus.add(doc);//add a document to the corpus               
                //j++;
                //try del singolo documento
        
          }catch (GateException gex) {
        //      gex.printStackTrace();
                //System.err.println("Documento "+listUrl.get(i)+" non più disponibile o raggiungibile.");
                SystemLog.write("Documento " + url + " non più disponibile o raggiungibile.", "ERROR");
                return corpus = null;
                //**************************+
                //IL SEGUENTE TRY E CATCH ERA UTILE QUANDO SI ANALIZZAVA TOT DOCUMENTI INSIEME
//                try{
//                    if(j < listUrl.size()){
//                        //soluzione forzata
//                       continue;
//                    }//if
//                }//try 
//                catch(Exception e){
//                     System.err.println("...fine della lista di documenti");
//                }//catch          
            }catch(ArrayIndexOutOfBoundsException ax){
                //System.err.println("Documento "+listUrl.get(i)+" non più disponibile o raggiungibile.");
                SystemLog.write("Documento " + url + " non più disponibile o raggiungibile.", "ERROR");
                return corpus = null;
//                try{
//                    if(j < listUrl.size()){
//                        //soluzione forzata
//                       continue;
//                    }//if
//                }//try 
//                catch(Exception e){
//                     System.err.println("...fine della lista di documenti");
//                }//catch
            }
        //} // for each corpus
        //****************************************************************************
        //System.err.println("Contenuto del Corpus costituito da:"+j+" indirizzi url.");
        //***************************************************************************
        
        //returnPipeline.setCorpus(corpus);  
    }//try per il singolo corpus
    catch (NullPointerException ne){
        SystemLog.write("Errore 'non fatale' in fase di RUN delle regole JAPE dovuta a uno dei seguenti motivi:", "ERROR");
        SystemLog.write("1)Collisione delle fasi", "ERROR");
        SystemLog.write("2)Una delle regole è entrata in collisione con un altra", "ERROR");
        SystemLog.write("3)Nel testo preso in esame non viene individuato alcun contenuto interessante dalle nostre regole JAPE", "ERROR");
      ne.printStackTrace();
    }
    xx=0;
    return corpus;
  } // createCorpus
    
  /**
    * Crea un Corpus di Documenti Gate
    * @param  listUrl la lista degli url prelevati dalla tabella website
    * @param  nomeCorpus il nome assegnato al Corpus in esame
    * @return il corpus "riempito" di GATE
    */  
  public Corpus createCorpusByListOfUrls(ArrayList<URL> listUrl,String nomeCorpus,Integer indice,DataStoreApplication datastore) throws IOException, ResourceInstantiationException, PersistenceException, SecurityException{   
    Corpus corpus = Factory.newCorpus(nomeCorpus);
    try {
        for(int i = 0; i < listUrl.size(); i++) {
        //for(URL url: listUrl) { 
          URL url = listUrl.get(i); 
          try{ 
                //Qui si crea un documento per ogni URL con le sue feature e annotazioni
                //document features create with GATE
                FeatureMap params = Factory.newFeatureMap();
                params.put("sourceUrl", url);                            
                params.put("preserveOriginalContent", new Boolean(true));
                params.put("collectRepositioningInfo", new Boolean(true));
                params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME,"UTF-8");
                //*********************************************************
                //Out.prln("("+indice+")Creating doc for " + url);
                //********************************************************
                
                //document features create by me
                FeatureMap feats =Factory.newFeatureMap();
                feats.put("date",new Date());
                //creazione del documento        
                Document doc = 
                        (Document)Factory.createResource("gate.corpora.DocumentImpl", 
                                /*params,feats,"doc_"+i+"_"+listUrl.get(i));*/
                                params,feats,"doc_"+indice+"_"+url);
                //Out.prln(doc.getFeatures().get("city").toString());
                corpus.add(doc);//add a document to the corpus               
                //try del singolo documento
                //*********************************************************
                System.out.println("("+indice+")Creating doc for " + url);
                //********************************************************
                indice++;
          }catch (GateException gex) {
        //      gex.printStackTrace();
                //System.err.println("Documento "+listUrl.get(i)+" non più disponibile o raggiungibile.");
                System.err.println("Documento "+url+" non più disponibile o raggiungibile.");
                //return corpus = null;
                // continue;
                //**************************+
                //IL SEGUENTE TRY E CATCH ERA UTILE QUANDO SI ANALIZZAVA TOT DOCUMENTI INSIEME
                try{
                    if(i < listUrl.size()){
                        //soluzione forzata
                       continue;
                    }//if
                }//try 
                catch(Exception e){
                     System.err.println("...fine della lista di documenti");
                }//catch
            }catch(ArrayIndexOutOfBoundsException ax){
                //System.err.println("Documento "+listUrl.get(i)+" non più disponibile o raggiungibile.");
                System.err.println("Documento "+url+" non più disponibile o raggiungibile.");
                //return corpus = null;
                //continue;
                try{
                    if(i < listUrl.size()){
                        //soluzione forzata
                       continue;
                    }//if
                }//try 
                catch(Exception e){
                     System.err.println("...fine della lista di documenti");
                }//catch
            } catch(java.lang.OutOfMemoryError e){
                //salva il corpus
                System.err.println("java.lang.OutOfMemoryError");
                System.err.println("Contenuto del Corpus costituito da:"+indice+" indirizzi url.");
                saveCorpusOnDataStoreForOutOfMemory(corpus,datastore);
                return corpus;               
            }
        } // for each corpus
        //****************************************************************************
        System.err.println("Contenuto del Corpus costituito da:"+indice+" indirizzi url.");
        //***************************************************************************
        //returnPipeline.setCorpus(corpus);  
    }//try per il singolo corpus
    catch (NullPointerException ne){
      System.err.println("Errore 'non fatale' in fase di RUN delle regole JAPE dovuta a uno dei seguenti motivi:");
      System.err.println("1)Collisione delle fasi");
      System.err.println("2)Una delle regole è entrata in collisione con un altra");
      System.err.println("3)Nel testo preso in esame non viene individuato alcun contenuto interessante dalle nostre regole JAPE");
      ne.printStackTrace();
    }
    return corpus;
  } // createCorpus
  
  /**
     * Metodo che salva un Corpus nel datastore 
     * @param corpus il corpus da asalvare
     * @throws IOException
     * @throws PersistenceException 
     */
    private void saveCorpusOnDataStoreForOutOfMemory(Corpus corpus,DataStoreApplication datastore) throws IOException, PersistenceException, SecurityException{
        //String NOME_DATASTORE = datastore.getNOME_DATASTORE();
        datastore.setDataStoreWithACorpus(corpus);
    }
  
}//class pipeline
