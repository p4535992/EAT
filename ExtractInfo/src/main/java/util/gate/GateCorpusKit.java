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
import gate.*;
import gate.corpora.DocumentImpl;
import gate.security.SecurityException;

import java.util.*;
import java.io.*;
import java.net.*;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.*;
import util.FileUtil;
import util.SystemLog;

public class GateCorpusKit {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GateCorpusKit.class);
    private static Document doc;
  
  public GateCorpusKit(){}
    /**
    * Crea un Corpus di Documenti Gate
    * @param  url l'url relativo alla singola pagina web da convertire in GATE Document
            * @param  nomeCorpus il nome assegnato al Corpus in esame
    * @return il corpus "riempito" di GATE
    */
    public static Corpus createCorpusByUrl(URL url,String nomeCorpus,Integer indice)
            throws IOException, ResourceInstantiationException{
        Corpus corpus = Factory.newCorpus(nomeCorpus);
        doc = createDocByUrl(url);
        if(doc != null) {
            corpus.add(doc);//add a document to the corpus
        }
        return corpus;
    } // createCorpus

    /**
     * Crea un Corpus di Documenti Gate
     * @param  url l'url relativo alla singola pagina web da convertire in GATE Document
     * @param  nomeCorpus il nome assegnato al Corpus in esame
     * @return il corpus "riempito" di GATE
     */
    public static Corpus createCorpusByUrl(URL url,String nomeCorpus)
            throws IOException, ResourceInstantiationException{
        Corpus corpus = Factory.newCorpus(nomeCorpus);
        doc = createDocByUrl(url);
        if(doc != null) {
            corpus.add(doc);//add a document to the corpus
        }
        return corpus;
    } // createCorpus
    
  /**
    * Crea un Corpus di Documenti Gate
    * @param  listUrl la lista degli url prelevati dalla tabella website
    * @param  nomeCorpus il nome assegnato al Corpus in esame
    * @return il corpus "riempito" di GATE
    */  
  public static Corpus createCorpusByListOfUrls(ArrayList<URL> listUrl,String nomeCorpus,Integer indice,GateDataStoreKit datastore)
          throws IOException, ResourceInstantiationException, PersistenceException, SecurityException{
    Corpus corpus = Factory.newCorpus(nomeCorpus);
    for(int i = 0; i < listUrl.size(); i++) {
        URL url = listUrl.get(i);
        doc = createDocByUrl(url);
        if(doc != null) {
            corpus.add(doc);//add a document to the corpus
        }
        indice = i;
    } // for each corpus
    SystemLog.message("Contenuto del Corpus costituito da:" + indice + " indirizzi url.");
    return corpus;
  } // createCorpus


    public static Corpus createCoprusByFile(String nomeCorpus,String filePath) throws ResourceInstantiationException, IOException {
            Corpus corpus = Factory.newCorpus(nomeCorpus);
            doc = createDocByUrl(FileUtil.convertFileToUri(filePath).toURL());
            if(doc != null) {
                corpus.add(doc);//add a document to the corpus
            }
            return corpus;
    }
  
  /**
     * Metodo che salva un Corpus nel datastore 
     * @param corpus il corpus da asalvare
     * @throws IOException
     * @throws PersistenceException 
     */
    public static  void saveCorpusOnDataStoreForOutOfMemory(Corpus corpus,GateDataStoreKit datastore) throws IOException, PersistenceException, SecurityException{
        //String NOME_DATASTORE = datastore.getNOME_DATASTORE();
        datastore.setDataStoreWithACorpus(corpus);
    }

    public static Document createDocByUrl(URL url)
            throws IOException, ResourceInstantiationException {
        Document doc = new DocumentImpl();
        try {
            //Qui si crea un documento per ogni URL con le sue feature e annotazioni
            //document features insert with GATE
            FeatureMap params = Factory.newFeatureMap();
            params.put("sourceUrl", url);
            params.put("preserveOriginalContent", new Boolean(true));
            params.put("collectRepositioningInfo", new Boolean(true));
            params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
            //document features insert by me
            FeatureMap feats = Factory.newFeatureMap();
            feats.put("date", new Date());
            //creazione del documento
            doc = (Document) Factory.createResource("gate.corpora.DocumentImpl",
                            /*params,feats,"doc_"+i+"_"+listUrl.get(i));*/
                    params, feats, "doc_" + url);
            //Out.prln(doc.getFeatures().get("city").toString());
        } catch (GateException gex) {
            SystemLog.ticket("Documento " + url + " non più disponibile o raggiungibile.", "ERROR");
            //**************************+
            //IL SEGUENTE TRY E CATCH ERA UTILE QUANDO SI ANALIZZAVA TOT DOCUMENTI INSIEME
        } catch (ArrayIndexOutOfBoundsException ax) {
            SystemLog.ticket("Documento " + url + " non più disponibile o raggiungibile.", "ERROR");
        }
        catch (NullPointerException ne){
            SystemLog.ticket("ERROR:" + ne.getMessage(),"ERROR");
            //ne.printStackTrace();
            doc = null;
        }
        return doc;
    }

    public static List<Object> getValueOfAnnotationFromDoc(Document doc,String annotatioName){
        List<Object> list = new ArrayList<>();
        // obtain the Original markups annotation set
        AnnotationSet origMarkupsSet = doc.getAnnotations("Original markups");
        // obtain annotations of type ’a’
        AnnotationSet anchorSet = origMarkupsSet.get("a");
        // iterate over each annotation
        // obtain its features and print the value of href feature
        System.out.println("Tutti gli url a cui quest a pagina fa o fornisce riferimento...");
        for (Annotation anchor : anchorSet) {
                //String href = (String) anchor.getFeatures().get("href");
                String valueAnn = (String) anchor.getFeatures().get(annotatioName);
                if(valueAnn != null) {
                    //URL uHref=new URL(doc.getSourceUrl(), href);
                    // resolving href value against the document’s url
                    if(!(list.contains(valueAnn)))list.add(valueAnn);

                }//if
        }//for anchor
        return list;
    }
  
}//class pipeline
