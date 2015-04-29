package extractor.gate;
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
import p4535992.util.file.FileUtil;
import p4535992.util.log.SystemLog;

public class GateCorpusKit {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GateCorpusKit.class);
    private static Document doc;

    public static Corpus getCorpus() {
        return corpus;
    }

    public static void setCorpus(Corpus corpus) {
        GateCorpusKit.corpus = corpus;
    }

    public static Corpus corpus;


  public GateCorpusKit(){}

    /**
     * Crea un Corpus di Documenti Gate
     * @param  url lt'url relativo alla singola pagina web da convertire in GATE Document
     * @param  nomeCorpus il nome assegnato al Corpus in esame
     * @return il corpus "riempito" di GATE
     */
    public static Corpus createCorpusByUrl(URL url,String nomeCorpus)
            throws IOException, ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        doc = createDocByUrl(url);
        if(doc != null) {
            corpus.add(doc);//add a document to the corpus
        }
        SystemLog.message("Loaded a corpus of: "+corpus.size()+" files");
        return corpus;
    } // createCorpus
    
  /**
    * Crea un Corpus di Documenti Gate
    * @param  listUrl la lista degli url prelevati dalla tabella website
    * @param  nomeCorpus il nome assegnato al Corpus in esame
    * @return il corpus "riempito" di GATE
    */  
  public static Corpus createCorpusByListOfUrls(List<URL> listUrl,String nomeCorpus)
          throws IOException, ResourceInstantiationException, PersistenceException, SecurityException{
        corpus = Factory.newCorpus(nomeCorpus);
        Integer indice = 0;
        for(int i = 0; i < listUrl.size(); i++) {
            URL url = listUrl.get(i);
            doc = createDocByUrl(url,indice);
            if(doc != null) {
                corpus.add(doc);//add a document to the corpus
                indice++;
            }
            indice = i;
        } // for each corpus
        SystemLog.message("Contenuto del Corpus costituito da:" + indice + " indirizzi url.");
        return corpus;
  } // createCorpus


    public static Corpus createCorpusByFile(File file,String nomeCorpus)
            throws ResourceInstantiationException, IOException {
            corpus = Factory.newCorpus(nomeCorpus);
            doc = createDocByUrl(FileUtil.convertFileToUri(file.getAbsolutePath()).toURL());
            if(doc != null) {
                corpus.add(doc);//add a document to the corpus
            }
            return corpus;
    }

    public static Corpus createCorpusByFile(List<File> files,String nomeCorpus)
            throws ResourceInstantiationException, IOException {
        corpus = Factory.newCorpus(nomeCorpus);
        Integer indice = 0;
        for(File file : files) {
            doc = createDocByUrl(FileUtil.convertFileToUri(file.getAbsolutePath()).toURL(),indice);
            //Document doc = Factory.newDocument(docFile.toURL(), "utf-8");
            if (doc != null) {
                corpus.add(doc);//add a document to the corpus
                indice++;
            }
        }
        return corpus;
    }
  
  /**
     * Metodo che salva un Corpus nel datastore 
     * @param corpus il corpus da asalvare
     * @throws IOException
     * @throws PersistenceException 
     */
    public static  void saveCorpusOnDataStoreForOutOfMemory(Corpus corpus,GateDataStoreKit datastore)
            throws IOException, PersistenceException, SecurityException{
        datastore.openDataStore();
        datastore.setDataStoreWithACorpus(corpus);
        datastore.saveACorpusOnTheDataStore(corpus, GateDataStoreKit.getDS_DIR());
        datastore.closeDataStore();
    }

    public static void loadCorpusOnADataStore(Corpus corpus)
            throws SecurityException, PersistenceException, ResourceInstantiationException {
        try {
            GateDataStoreKit.openDataStore();
            GateDataStoreKit.setDataStoreWithACorpus(corpus);
            GateDataStoreKit.saveACorpusOnTheDataStore(corpus, GateDataStoreKit.getDS_DIR());
            //datastore.closeDataStore();
        } catch (Exception e) {
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

    public static Document createDocByUrl(URL url,Integer i)
            throws IOException, ResourceInstantiationException {
        doc = new DocumentImpl();
        try {
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
                    params,feats,"doc_"+i+"_"+url);

            //doc = Factory.newDocument(url, "utf-8");

        } catch (GateException gex) {
            SystemLog.warning("Documento " + url + " non più disponibile o raggiungibile.");
        } catch (ArrayIndexOutOfBoundsException ax) {
            SystemLog.warning("Documento " + url + " non più disponibile o raggiungibile.");
        }
        catch (NullPointerException ne){
            SystemLog.exception(ne);
            doc = null;
        }
        return doc;
    }

    public static Document createDocByUrl(URL url)
            throws IOException, ResourceInstantiationException {
        doc = new DocumentImpl();
        try {
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
                    params, feats, "doc_" + url);
        } catch (GateException gex) {
            SystemLog.warning("Documento " + url + " non più disponibile o raggiungibile.");
        } catch (ArrayIndexOutOfBoundsException ax) {
            SystemLog.warning("Documento " + url + " non più disponibile o raggiungibile.");
        }
        catch (NullPointerException ne){
            SystemLog.exception(ne);
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

    public static void printXML(File docFile,Document doc)
            throws IOException,FileNotFoundException,UnsupportedEncodingException{
        String docXMLString = null;
        docXMLString = doc.toXml();
        String outputFileName = doc.getName() + ".out.xml";
        File outputFile = new File(docFile.getParentFile(), outputFileName);
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter out;
        out = new OutputStreamWriter(bos, "utf-8");
        out.write(docXMLString);
        out.close();
    }
  
}//class pipeline
