package com.github.p4535992.extractor.estrattori;
import com.github.p4535992.extractor.object.dao.jdbc.IWebsiteDao;
import com.github.p4535992.extractor.object.impl.jdbc.WebsiteDaoImpl;
import com.github.p4535992.gatebasic.gate.gate8.ExtractorInfoGate8;
import com.github.p4535992.gatebasic.gate.gate8.Gate8Kit;
import com.github.p4535992.gatebasic.gate.gate8.GateSupport;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.extractor.karma.GenerationOfTriple;
import com.github.p4535992.util.string.StringKit;
import gate.Controller;
import gate.Corpus;
import gate.CorpusController;
import com.github.p4535992.extractor.object.dao.jdbc.IGeoDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.GeoDocumentDaoImpl;
import com.github.p4535992.extractor.object.model.GeoDocument;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import com.github.p4535992.extractor.object.dao.jdbc.IInfoDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.InfoDocumentDaoImpl;
import gate.Document;
import gate.util.DocumentProcessor;

/**
 * Created by 4535992 on 15/06/2015.
 * @author 4535992
 * @version  2015-06-25
 */
@SuppressWarnings("unused")
public class ExtractInfoWeb {

    private static int indGDoc;
    private Controller controller;
    private DocumentProcessor procDoc;

    public DocumentProcessor getProcDoc() {
        return procDoc;
    }

    public Controller getController() {
        return controller;
    }

    private String TABLE_INPUT,TABLE_OUTPUT,DRIVER_DATABASE,DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE,
            USER, PASS, DB_OUTPUT;

    private static ExtractInfoWeb instance = null;
    protected ExtractInfoWeb(){}
    protected ExtractInfoWeb(String DRIVER_DATABASE,String DIALECT_DATABASE,String HOST_DATABASE,
                          String PORT_DATABASE,String USER,String PASS,String DB_OUTPUT){
        this.DRIVER_DATABASE = DRIVER_DATABASE;
        this.DIALECT_DATABASE = DIALECT_DATABASE;
        this.HOST_DATABASE = HOST_DATABASE;
        this.PORT_DATABASE = PORT_DATABASE;
        this.USER = USER;
        this.PASS = PASS;
        this.DB_OUTPUT=DB_OUTPUT;
        Gate8Kit gate8 = Gate8Kit.getInstance();
        this.controller = gate8.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
                "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
        //gate8.loadGapp("custom/gapp", "geoLocationPipeline06102014v7_fastMode.xgapp");
        //procDoc = gate8.setUpGateEmbeddedWithSpring("gate/gate-beans.xml",this.getClass(),"documentProcessor");
    }

    public static ExtractInfoWeb getInstance(){
        if(instance == null) {
            instance = new ExtractInfoWeb();
        }
        return instance;
    }

    public static ExtractInfoWeb getInstance(String DRIVER_DATABASE,String DIALECT_DATABASE,String HOST_DATABASE,
                                             String PORT_DATABASE,String USER,String PASS,String DB_OUTPUT){
        if(instance == null) {
            instance = new ExtractInfoWeb(DRIVER_DATABASE,DIALECT_DATABASE,HOST_DATABASE,
                    PORT_DATABASE,USER,PASS,DB_OUTPUT);
        }
        return instance;
    }

    public GeoDocument ExtractGeoDocumentFromString(
            String url, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable){
        GeoDocument geoDoc = new GeoDocument();
        try {
            if (!url.matches("^(https?|ftp)://.*$")) {
                url = "http://" + url;
            }
            geoDoc = ExtractGeoDocumentFromUrl(new URL(url),TABLE_INPUT,TABLE_OUTPUT,createNewTable);
        }catch(MalformedURLException e){
            SystemLog.error("You have insert a not valid url for the extraction of information:"+ url+" is not valid!");
        }
        return geoDoc;
    }

    public List<GeoDocument> ExtractGeoDocumentFromListStrings(List<String> urls,String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable){
        List<URL> listUrls = new ArrayList<>();
        List<GeoDocument> listGeo;
        for(int i = 0; i < urls.size(); i++) {
            try{
                if (!urls.get(i).matches("^(https?|ftp)://.*$")) {
                    listUrls.set(i,new URL("http://" + urls.get(i)));
                }
            }catch(MalformedURLException e){
                SystemLog.error("You have insert a not valid url for the extraction of information:"+ urls.get(i)+" is not valid!");
            }
        }
        listGeo = ExtractGeoDocumentFromListUrls(listUrls,TABLE_INPUT,TABLE_OUTPUT,createNewTable);
        return listGeo;
    }

    public List<GeoDocument> ExtractGeoDocumentFromListUrls(
            List<URL> listUrls, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable){
        IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();
        geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE,
                PORT_DATABASE, USER, PASS, DB_OUTPUT);
        geoDocumentDao.setTableInsert(TABLE_OUTPUT);
        geoDocumentDao.setTableSelect(TABLE_INPUT);
        ExtractorInfoGate8 egate = ExtractorInfoGate8.getInstance();
        ExtractorGeoDocumentSupport egs  = new ExtractorGeoDocumentSupport();
        ExtractorJSOUP j = new ExtractorJSOUP();
        if(createNewTable){
            try {
                geoDocumentDao.create(true);
            } catch (Exception e) {
                SystemLog.exception(e);
            }
        }
        indGDoc = 0;
        GeoDocument geo2 = new GeoDocument();
        GeoDocument geoDoc = new GeoDocument();
        List<GeoDocument> listGeo = new ArrayList<>();
        SystemLog.message("*******************Run GATE**************************");
        //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
        String[] anntotations = new String[]{"MyRegione", "MyPhone", "MyFax", "MyEmail", "MyPartitaIVA",
                "MyLocalita", "MyIndirizzo", "MyEdificio", "MyProvincia"};
        List<String> listAnn = Arrays.asList(anntotations);
        //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
        String[] anntotationsSet = new String[]{"MyFOOTER", "MyHEAD", "MySpecialID", "MyAnnSet"};
        List<String> listAnnSet = Arrays.asList(anntotationsSet);
        //Store the result on of the extraction on a GateSupport Object
        GateSupport support = GateSupport.getInstance(
                egate.extractorGATE(listUrls, (CorpusController) controller, "corpus_test_1", listAnn, listAnnSet, true));
        Corpus corpus = egate.getCorpus();
        for (Document doc : corpus) {
            try {
                SystemLog.message("(" + indGDoc + ")URL:" + doc.getSourceUrl());
                GeoDocument geo = convertGateSupportToGeoDocument(support, doc.getSourceUrl(), indGDoc);
                indGDoc++;
                SystemLog.message("*******************Run JSOUP**************************");
                geo2 = j.GetTitleAndHeadingTags(doc.getSourceUrl().toString(), geo2);
                SystemLog.message("*******************Run Support GeoDocument**************************");
                geoDoc = ExtractorGeoDocumentSupport.compareInfo3(geoDoc, geo2);
                //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
                geoDoc = egs.UpgradeTheDocumentWithOtherInfo(geoDoc);
                geoDoc = egs.pulisciDiNuovoGeoDocument(geoDoc);
                listGeo.add(geoDoc);
            }catch(IOException|InterruptedException|URISyntaxException e){
                SystemLog.exception(e);
            }
        }
        for(GeoDocument geoDoc3: listGeo) {
            if (geoDoc3.getUrl() != null) {
                //SystemLog.message("INSERIMENTO");
                SystemLog.message(geoDoc3.toString());
                geoDocumentDao.insertAndTrim(geoDoc3);
            }//if
        }
        return listGeo;
    }

    public GeoDocument ExtractGeoDocumentFromUrl(URL url, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable){
        IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();
        geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE,
                PORT_DATABASE, USER, PASS, DB_OUTPUT);
        geoDocumentDao.setTableInsert(TABLE_OUTPUT);
        geoDocumentDao.setTableSelect(TABLE_INPUT);
        ExtractorInfoGate8 egate = ExtractorInfoGate8.getInstance();
        ExtractorGeoDocumentSupport egs  = new ExtractorGeoDocumentSupport();
        ExtractorJSOUP j = new ExtractorJSOUP();
        if(createNewTable){
            try {
                geoDocumentDao.create(true);
            } catch (Exception e) {
                SystemLog.exception(e);
            }
        }
        indGDoc = 0;
        GeoDocument geo2 = new GeoDocument();
        GeoDocument geoDoc = new GeoDocument();
        SystemLog.message("(" + indGDoc + ")URL:" + url);
        indGDoc++;
        SystemLog.message("*******************Run JSOUP**************************");
        try {
            geo2 = j.GetTitleAndHeadingTags(url.toString(), geo2);
            if (ExtractorJSOUP.isEXIST_WEBPAGE()) {
                try {
                    SystemLog.message("*******************Run GATE**************************");
                    //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
                    String[] anntotations = new String[]{"MyRegione","MyPhone","MyFax","MyEmail","MyPartitaIVA",
                            "MyLocalita","MyIndirizzo","MyEdificio","MyProvincia"};
                    List<String> listAnn = Arrays.asList(anntotations);
                    //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
                    String[] anntotationsSet = new String[]{"MyFOOTER","MyHEAD","MySpecialID","MyAnnSet"};
                    List<String> listAnnSet = Arrays.asList(anntotationsSet);
                    //Store the result on of the extraction on a GateSupport Object
                    GateSupport support = GateSupport.getInstance(
                            egate.extractorGATE(url,(CorpusController) controller, "corpus_test_1", listAnn, listAnnSet, true));
                    geoDoc = convertGateSupportToGeoDocument(support,url,0); //0 because is just a unique document...
                    SystemLog.message("*******************Run Support GeoDocument**************************");
                    geoDoc = ExtractorGeoDocumentSupport.compareInfo3(geoDoc, geo2);
                    //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
                    geoDoc = egs.UpgradeTheDocumentWithOtherInfo(geoDoc);
                    //geoDoc = egs.pulisciDiNuovoGeoDocument(geoDoc);
                } catch (URISyntaxException e) {
                    SystemLog.exception(e);
                }
                if (geoDoc.getUrl() != null) {
                    //SystemLog.message("INSERIMENTO");
                    SystemLog.message(geoDoc.toString());
                    geoDocumentDao.insertAndTrim(geoDoc);
                }//if
            }
        }catch(IOException|InterruptedException e ){
            SystemLog.exception(e);
        }
        return geoDoc;
    }

    public void triplifyGeoDocument(String TABLE_INPUT,String TABLE_OUTPUT,String OUTPUT_FORMAT,boolean createNewTable){
        SystemLog.message("RUN ONTOLOGY PROGRAMM: Create Table of infodocument from a geodocument/geodomaindocuemnt table!");
        IInfoDocumentDao infoDocumentDao = new InfoDocumentDaoImpl();
        infoDocumentDao.setTableInsert(TABLE_OUTPUT);
        infoDocumentDao.setTableSelect(TABLE_INPUT);
        infoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE, USER, PASS, DB_OUTPUT);
        if(createNewTable){
            try {
                infoDocumentDao.create(true);
            } catch (Exception e) {
                SystemLog.exception(e);
            }
        }
        //GENERIAMO IL FILE DI TRIPLE CORRISPONDENTE ALLE INFORMAZIONI ESTRATTE CON KARMA
        SystemLog.message("RUN KARMA PROGRAMM: Generation of triple with org.p4535992.mvc.webapp-karma!!");
        GenerationOfTriple got = new GenerationOfTriple(
                "DB", //DB
                "karma_files/model/", //PATH: karma_files/model/
                "karma_files/output/", //PATH: karma_files/output/
                "MySQL",//MySQL
                HOST_DATABASE,
                USER,//USER_KARMA
                PASS,//PASS_KARMA
                PORT_DATABASE,
                DB_OUTPUT,
                TABLE_OUTPUT,
                OUTPUT_FORMAT,
                null
        );
        try {
            got.GenerationOfTripleWithKarmaAPIFromDataBase();
        } catch (IOException ex) {
            SystemLog.exception(ex);
        }
    }

    public List<GeoDocument> ExtractGeoDocumentFromFile(File fileOrDirectory, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable) {
        List<GeoDocument> listGeo = new ArrayList<>();
        if(FileUtil.isDirectory(fileOrDirectory)){
            List<File> listFiles = FileUtil.readDirectory(fileOrDirectory);
            ExtractGeoDocumentFromListFiles(listFiles,TABLE_INPUT,TABLE_OUTPUT,createNewTable);
        }else{
            //..is a single file
            URL url;
            try {
                url = FileUtil.convertFileToURL(fileOrDirectory);
                listGeo.add(ExtractGeoDocumentFromUrl(url,TABLE_INPUT,TABLE_OUTPUT,createNewTable));
            } catch (MalformedURLException e) {
                SystemLog.warning(e.getMessage());
                return null;
            }
        }
        return listGeo;
    }

    public List<GeoDocument> ExtractGeoDocumentFromListFiles(List<File> listFiles, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable) {
        List<URL> listUrls = new ArrayList<>();
        for(File file: listFiles) {
            try {
                URL url = FileUtil.convertFileToURL(file);
                listUrls.add(url);
            } catch (MalformedURLException e) {
                SystemLog.warning(e.getMessage());
            }
        }
        return ExtractGeoDocumentFromListUrls(listUrls,TABLE_INPUT,TABLE_OUTPUT,createNewTable);
    }



    public GeoDocument convertGateSupportToGeoDocument(GateSupport support,URL url,Integer index){
        GeoDocument geoDoc = new GeoDocument();
        String[] anntotations = new String[]{"MyRegione","MyPhone","MyFax","MyEmail","MyPartitaIVA",
                "MyLocalita","MyIndirizzo","MyEdificio","MyProvincia"};
        try {
            geoDoc.setUrl(url);
            for(String nameAnnotation: anntotations ){
                //get list of all annotation set...
                List<Map<String,Map<String,String>>> list = new ArrayList<>(support.getMapDocs().values());
                //for each annotation set....
                //boolean flag1 = false;
                //for(int i=0; i< list.size(); i++){
                    boolean flag2 = false;
                    for(int j=0; j < list.get(index).size(); j++){
                        String content = support.getContent(index, j, nameAnnotation);
                        switch (nameAnnotation){
                            case "MyRegione": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getRegione())) {
                                    geoDoc.setRegione(content);
                                    flag2 = true;
                                }
                                break;
                            }
                            case "MyPhone": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getTelefono())) {
                                    geoDoc.setTelefono(content);
                                    flag2 = true;
                                }
                                break;
                            }
                            case "MyFax": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getFax())) {
                                    geoDoc.setFax(content);
                                    flag2 = true;
                                }
                                break;
                            }
                            case "MyEmail": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getEmail())) {
                                    geoDoc.setEmail(content);
                                    flag2 = true;
                                }
                                break;
                            }
                            case "MyPartitaIVA": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getIva())) {
                                    geoDoc.setIva(content);
                                    flag2 = true;
                                }
                                break;
                            }
                            case "MyLocalita": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getCity())) {
                                    geoDoc.setCity(content);
                                    flag2 = true;
                                }
                                break;
                            }
                            case "MyIndirizzo": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getIndirizzo())) {
                                    geoDoc.setIndirizzo(content);
                                    flag2 = true;
                                }
                                break;
                            }
                            case "MyEdificio": {
                                if (!StringKit.isNullOrEmpty(content) && StringKit.isNullOrEmpty(geoDoc.getEdificio())) {
                                    geoDoc.setEdificio(content);
                                    flag2 = true;
                                }
                                break;
                            }
                        }//switch
                        if(flag2){
                            //flag1 = true;
                            break;
                        }
                    }
                //    if(flag1)break;
                // }
            }
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return geoDoc;
    }

    public List<URL> getListURLFromDatabase(String DRIVER_DATABASE,String DIALECT_DATABASE,String HOST_DATABASE,
           String PORT_DATABASE,String USER,String PASS,String DB_INPUT, String TABLE_INPUT,
                                            String COLUMN_TABLE_INPUT,Integer LIMIT,Integer OFFSET) {
        List<URL> listUrl = new ArrayList<>();
        try {
            IWebsiteDao websiteDao = new WebsiteDaoImpl();
            websiteDao.setTableSelect(TABLE_INPUT);
            websiteDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE, USER, PASS, DB_INPUT);
            listUrl = websiteDao.selectAllUrl(COLUMN_TABLE_INPUT, LIMIT, OFFSET);
        } catch (MalformedURLException e) {
           SystemLog.exception(e);
        }
        return listUrl;
    }

}
