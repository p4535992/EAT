package com.github.p4535992.extractor.estrattori;

import com.github.p4535992.extractor.gate.GateAnnotationKit;
import com.github.p4535992.extractor.gate.GateCorpusKit;
import gate.Corpus;
import gate.CorpusController;
import gate.creole.ExecutionException;
import gate.util.GateException;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.support.AnnotationInfo;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * EstrazioneDatiWithGATE.java
 * Classe che utilizza GATE per lt'estrazione di informazione da und eterminato testo
 * @author 4535992
 * @version 2015-06-25
 */
public class ExtractorInfoGATE {

    private Corpus corpus;
    public ExtractorInfoGATE(){

    }

    /** Tell GATE's spring.mvc.home.home.initializer.org.p4535992.mvc.webapp.controller about the corpus you want to run on.
     *  @param corpus corpus gate to set.
     *  @param controller corpus controller gate to set.
     */
    public void setCorpus(Corpus corpus,CorpusController controller) {controller.setCorpus(corpus);} // setCorpus

    /** 
     * Run GATE. 
     * @param controller corpus controller gate to set.
     * @throws GateException gate exception.
     */
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
                SystemLog.warning("No sentences or tokens to process in some gate documents");
            }
        }catch(java.lang.OutOfMemoryError e){
            if(exeTentative < 3){
                exeTentative ++;
                execute(controller);
            }else{
                SystemLog.warning("Exception in thread \"AWT-EventQueue-0\" ava.lang.OutOfMemoryError: Java heap space");
            }
        }
    }


    /**
     * Metodo che estrae le informazioni dal singolo documento org.p4535992.mvc.webapp
     * @param url url to the web document.
     * @param controller corpus controller gate to set.
     * @return GeoDocument with field set.
     * @throws InterruptedException error.
     * @throws InvocationTargetException error.
     * @throws SQLException error.
     */
    public GeoDocument extractorGATE(URL url, CorpusController controller)
            throws InterruptedException, InvocationTargetException, SQLException{
        //for each url we create a corpus
        GeoDocument geoDoc = new GeoDocument();
        try{
            if(url!=null){
             corpus = GateCorpusKit.createCorpusByUrl(url, "GeoDocuments Corpus");
            }//if url!=null
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                SystemLog.message("Execute of GATE in process for the url " + url + "...");
                execute(controller);
                SystemLog.message(".. GATE is been processed");
                AnnotationInfo annInfo = GateAnnotationKit.getSingleAnnotationInfo(corpus, null);
                geoDoc = convertAnnotationInfoToGeoDocument(annInfo);
            }//else
        }//try
        catch(GateException|RuntimeException|IOException ex){
              SystemLog.exception(ex);
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
    }//extractorGATE

     /**
      * Metodo che estrae le informazioni da una lista di documenti org.p4535992.mvc.webapp
      * @param listUrl list of url referenced to wed documents where you want extract the information.
      * @param controller  CorpusController of GATE already setting.
      * @return un GeoDocument con i campi riempiti dalle annotazioni di GATE.
      * @throws GateException error.
      * @throws IOException error.
      */
     public List<GeoDocument> extractorGATE(List<URL> listUrl, CorpusController controller)
             throws GateException,IOException{
         List<GeoDocument> listGeo = new ArrayList<>();
         corpus = GateCorpusKit.createCorpusByListOfUrls(listUrl,"Nome COrpus");
         if(corpus == null){return null;}
         else {
              //settiamo il corpus
              setCorpus(corpus, controller);
              //carichiamo i documenti del corpus sulla nostra pipeline
              //su cui è caricato il file .gapp/.xgapp
              SystemLog.message("Execute of GATE in process for the list of urls of size: " + listUrl.size()  + " ...");
              execute(controller);
              SystemLog.message(".. GATE is been processed");
              //andiamo a trasformare il contenuto matchato dalle nostre annotazioni
              //in keyword che andremo a inserire nel nostro apposito GeoDocument.
              List<AnnotationInfo> listAnn = GateAnnotationKit.getMultipleAnnotationInfo(corpus,null);
              for(AnnotationInfo annInfo : listAnn){
                  listGeo.add(convertAnnotationInfoToGeoDocument(annInfo));
              }
         }
      return listGeo;                 
     }//extractorGATE

    public List<GeoDocument> extractMicrodataWithGATEMultipleFiles(List<File> listFile,CorpusController controller)
            throws GateException,MalformedURLException, InterruptedException, SQLException, InvocationTargetException {
        List<GeoDocument> listGeo = new ArrayList<GeoDocument>();
        for(File file : listFile){
            GeoDocument g = new GeoDocument();
            g = extractorGATE(file.toURI().toURL(), controller);
            listGeo.add(g);
        }
        return listGeo;
    }//extractMicrodataWithGATEMultipleFiles

    public GeoDocument extractMicrodataWithGATESingleFile(File file,CorpusController controller)
            throws GateException,MalformedURLException, InterruptedException, SQLException, InvocationTargetException {
        GeoDocument g = new GeoDocument();
        //SystemLog.debug(FileUtil.convertFileToUri(file).toURL().toString());
        g = extractorGATE(FileUtil.convertFileToUri(file).toURL(), controller);
        //home = extractorGATE(file.toURL(), home.home.initializer.org.p4535992.mvc.webapp.controller);
        return g;
    }//extractMicrodataWithGATESingleFile


     /**
      * Method for re-cean AnnotationInfo.
      * @param annInfo annotation info object, temporary file for memorize annotation on the gate document.
      * @return annotationInfo "filtered".
      */
     private AnnotationInfo pulisciAnnotionInfo(AnnotationInfo annInfo){
         //if(annInfo.getUrl().toString()!=null){}
         if(StringKit.setNullForEmptyString(annInfo.getRegione())!=null){
             annInfo.setRegione(annInfo.getRegione().toString().trim());
         }
         
         if(StringKit.setNullForEmptyString(annInfo.getProvincia())!=null){
             annInfo.setProvincia(annInfo.getProvincia().toString().trim());
         }
         
         if(StringKit.setNullForEmptyString(annInfo.getLocalita())!=null){
             annInfo.setLocalita(annInfo.getLocalita().toString().trim());
         }
         else{
            if(StringKit.setNullForEmptyString(annInfo.getLocalita())!=null){
                     annInfo.setLocalita(annInfo.getLocalita().trim());
            }else if(StringKit.setNullForEmptyString(annInfo.getProvincia())!=null){
                     annInfo.setLocalita(annInfo.getProvincia().trim());
            }
         }
         if(StringKit.setNullForEmptyString(annInfo.getIndirizzo())!=null){
             annInfo.setIndirizzo(annInfo.getIndirizzo().toString().trim());
         }
         if(StringKit.setNullForEmptyString(annInfo.getIva())!=null){
             annInfo.setIva(annInfo.getIva().toString().trim());
         }
         if(StringKit.setNullForEmptyString(annInfo.getEmail())!=null){
             annInfo.setEmail(annInfo.getEmail().toString().trim());
         }
         if(StringKit.setNullForEmptyString(annInfo.getTelefono())!=null){
             annInfo.setTelefono(annInfo.getTelefono().toString().trim());
         }
         if(StringKit.setNullForEmptyString(annInfo.getFax())!=null){
             annInfo.setFax(annInfo.getFax().toString().trim());
         }
         if(StringKit.setNullForEmptyString(annInfo.getEdificio())!=null){
             annInfo.setEdificio(annInfo.getEdificio().toString().trim());
         }
         if(StringKit.setNullForEmptyString(annInfo.getNazione())!=null){
             annInfo.setNazione(annInfo.getNazione().toString().trim());
         }
         return annInfo;
     }//pulisciAnnotionInfo


    private GeoDocument convertAnnotationInfoToGeoDocument(AnnotationInfo annInfo){
        annInfo = pulisciAnnotionInfo(annInfo);
        GeoDocument geoDoc = new GeoDocument(
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
        return geoDoc;
    }
      
}
