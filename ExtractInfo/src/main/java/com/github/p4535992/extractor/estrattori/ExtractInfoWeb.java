package com.github.p4535992.extractor.estrattori;
import com.github.p4535992.extractor.JenaInfoDocument;
import com.github.p4535992.extractor.object.dao.jdbc.IWebsiteDao;
import com.github.p4535992.extractor.object.impl.jdbc.WebsiteDaoImpl;
import com.github.p4535992.gatebasic.gate.gate8.ExtractorInfoGate8;
import com.github.p4535992.gatebasic.gate.gate8.Gate8Kit;
import com.github.p4535992.gatebasic.gate.gate8.GateSupport;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.html.JSoupKit;
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
import java.sql.Types;
import java.util.*;

import com.github.p4535992.extractor.object.dao.jdbc.IInfoDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.InfoDocumentDaoImpl;
import gate.Document;
import gate.util.DocumentProcessor;

/**
 * Created by 4535992 on 15/06/2015.
 * @author 4535992.
 * @version  2015-09-26.
 */
@SuppressWarnings("unused")
public class ExtractInfoWeb {

    private static int  indGDoc = 0;
    private Controller controller;
    private DocumentProcessor procDoc;

    public DocumentProcessor getProcDoc() {
        return procDoc;
    }

    public Controller getController() {
        return controller;
    }

    private String TABLE_INPUT,TABLE_OUTPUT,DRIVER_DATABASE,DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE,
            USER, PASS, DB_INPUT, DB_OUTPUT;

    private static ExtractInfoWeb instance = null;
    protected ExtractInfoWeb(){}

    private boolean tableAlreadyCreated = true; //la tabella di outpu di default esiste gi�.
    private boolean gateAlreadySetted = true; //il settaggio di gate di default � compelto.
    private boolean connectionToADatabase = true; //la connessione a un database di default � true.

    public boolean isConnectionToADatabase() {
        return connectionToADatabase;
    }

    public void setConnectionToADatabase(boolean connectionToADatabase) {
        this.connectionToADatabase = connectionToADatabase;
    }



    /**
     * Constructor.
     * @param DRIVER_DATABASE driver of the database eg: "com.mysql.jdbc.Driver".
     * @param DIALECT_DATABASE dialect of the databse eg: "jdbc:mysql".
     * @param HOST_DATABASE host of the database eg: "localhost".
     * @param PORT_DATABASE port of the databse eg: "3306".
     * @param USER username of the database eg: "username".
     * @param PASS passwrod of the database eg: "password".
     * @param DB_OUTPUT string name of the database eg: "database".
     */
    protected ExtractInfoWeb(String DRIVER_DATABASE,String DIALECT_DATABASE,String HOST_DATABASE,
                          String PORT_DATABASE,String USER,String PASS,String DB_OUTPUT){
        this.DRIVER_DATABASE = DRIVER_DATABASE;
        this.DIALECT_DATABASE = DIALECT_DATABASE;
        this.HOST_DATABASE = HOST_DATABASE;
        this.PORT_DATABASE = PORT_DATABASE;
        this.USER = USER;
        this.PASS = PASS;
        this.DB_OUTPUT=DB_OUTPUT;
        //Gate8Kit gate8 = Gate8Kit.getInstance();
        //this.controller = gate8.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
        //        "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
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

    /**
     * Method to set the GATE Embedded API.
     * @param directoryFolderHome the root dircetory where all files of gate are stored eg:"gate_files".
     * @param directoryFolderPlugin  the root directory of all plugin of gate under the directoryFolderHome eg: "plugins".
     * @param configFileGate the path to the config file of gate under the directoryFolderHome eg:"gate.xml".
     * @param configFileUser the path to the config file user of gate under the directoryFolderHome eg:"user-gate.xml".
     * @param configFileSession the path to the config file session of gate under the directoryFolderHome eg:"gate.session".
     * @param gappFile the path to the gapp file user of gate under the directoryFolderHome eg:"custom/gapp/test.xgapp".
     * @return the GATE Controller.
     */
    public Controller setGate(String directoryFolderHome,String directoryFolderPlugin,
                        String configFileGate,String configFileUser,String configFileSession,String gappFile){
        if(controller==null) {
            if(gateAlreadySetted) {
                Gate8Kit gate8 = Gate8Kit.getInstance();
                this.controller = gate8.setUpGateEmbedded(directoryFolderHome, directoryFolderPlugin,
                        configFileGate, configFileUser, configFileSession, gappFile);
                gateAlreadySetted = false;
                return controller;
            }else{
                SystemLog.warning("The GATE embedded API is already set with Spring Framework and ProcessorDocument!!!");
                return null;
            }
        }else{
            SystemLog.warning("The GATE embedded API is already set with Corpus Controller!!!");
            return controller;
        }
    }

    /**
     * Method to set the GATE Embedded API with Spring framework.
     * @param pathToTheGateContextFile path to the GATE Context File eg: "gate/gate-beans.xml".
     * @param beanNameOfTheProcessorDocument string name of the bean for the DocumentProcessor class on the
     *                                       gate context file eg:"documentProcessor".
     * @param thisClass class here you want invokem this method necessary for avoid exception with spring.
     * @return the GATE DocumentProcessor.
     */
    public DocumentProcessor setGateWithSpring(
            String pathToTheGateContextFile,String beanNameOfTheProcessorDocument,Class<?> thisClass){
        if(procDoc ==null) {
            if(gateAlreadySetted) {
                Gate8Kit gate8 = Gate8Kit.getInstance();
                this.procDoc = gate8.setUpGateEmbeddedWithSpring(pathToTheGateContextFile, thisClass, beanNameOfTheProcessorDocument);
                gateAlreadySetted = false;
                return procDoc;
            }else{
                SystemLog.warning("The GATE embedded API is already set with Corpus Controller!!!");
                return null;
            }
        }else {
            SystemLog.warning("The GATE embedded API is already set with Spring Framework and ProcessorDocument!!!");
            return procDoc;
        }
    }

    /**
     * Method to Extract GeoDocuments from a string as url.
     * @param url string of a url address.
     * @param TABLE_INPUT String name of the table where do the operation of Select.
     * @param TABLE_OUTPUT String name of the table where insert the geodoucment.
     * @param createNewTable if true Create a new TABLE_OUTPUT with the same name.
     * @param dropOldTable if true Drop the old TABLE_OUTPUT with the same name.
     * @return a List Collection of GeoDocuments, but the geoDocuments are already put in the database.
     */
    public GeoDocument ExtractGeoDocumentFromString(
            String url, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable,boolean dropOldTable){
        GeoDocument geoDoc = new GeoDocument();
        try {
            if (!url.matches("^(https?|ftp)://.*$")) {
                url = "http://" + url;
            }
            geoDoc = ExtractGeoDocumentFromUrl(new URL(url),TABLE_INPUT,TABLE_OUTPUT,createNewTable,dropOldTable);
        }catch(MalformedURLException e){
            SystemLog.error("You have insert a not valid url for the extraction of information:"+ url+" is not valid!");
        }
        return geoDoc;
    }


    /**
     * Method to Extract GeoDocuments from a List of string as url.
     * @param urls List Collection of string as url address.
     * @param TABLE_INPUT String name of the table where do the operation of Select.
     * @param TABLE_OUTPUT String name of the table where insert the geodoucment.
     * @param createNewTable if true Create a new TABLE_OUTPUT with the same name.
     * @param dropOldTable if true Drop the old TABLE_OUTPUT with the same name.
     * @return a List Collection of GeoDocuments, but the geoDocuments are already put in the database.
     */
    public List<GeoDocument> ExtractGeoDocumentFromListStrings(
            List<String> urls,String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable,boolean dropOldTable){
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
        listGeo = ExtractGeoDocumentFromListUrls(listUrls,TABLE_INPUT,TABLE_OUTPUT,createNewTable,dropOldTable);
        return listGeo;
    }

    /**
     * Method to Extract GeoDocuments from a List of urls.
     * @param listUrls List Collection of URLs address.
     * @param TABLE_INPUT String name of the table where do the operation of Select.
     * @param TABLE_OUTPUT String name of the table where insert the geodoucment.
     * @param createNewTable if true Create a new TABLE_OUTPUT with the same name.
     * @param dropOldTable if true Drop the old TABLE_OUTPUT with the same name.
     * @return a List Collection of GeoDocuments, but the geoDocuments are already put in the database.
     */
    public List<GeoDocument> ExtractGeoDocumentFromListUrls(
            List<URL> listUrls, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable,boolean dropOldTable){
        List<GeoDocument> listGeo = new ArrayList<>();
        try {
            IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();
            if(connectionToADatabase) {
                geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE,
                        PORT_DATABASE, USER, PASS, DB_OUTPUT);
                geoDocumentDao.setTableInsert(TABLE_OUTPUT);
                geoDocumentDao.setTableSelect(TABLE_INPUT);
                if (tableAlreadyCreated) {
                    if (createNewTable) {
                        try {
                            geoDocumentDao.create(dropOldTable);
                        } catch (Exception e) {
                            SystemLog.exception(e);
                        }finally{
                            tableAlreadyCreated = false;
                        }
                    }
                }
            }
            ExtractorInfoGate8 egate = ExtractorInfoGate8.getInstance();
            ExtractorGeoDocumentSupport egs = new ExtractorGeoDocumentSupport();
            ExtractorJSOUP j = new ExtractorJSOUP();

            GeoDocument geo2 = new GeoDocument();
            GeoDocument geoDoc = new GeoDocument();
            //check if the site is already present like offline or unreachable
            /*geoDocumentDao.setTableInsert("offlinesite");
            List<URL> supportList = new ArrayList<>();
            for(URL url : listUrls) {if (geoDocumentDao.verifyDuplicate("url", url.toString())) {supportList.add(url);} }
            for(URL url: supportList){SystemLog.warning("The site "+url+" can't be reach..."); listUrls.remove(url);}
            geoDocumentDao.setTableInsert(TABLE_INPUT);
            supportList.clear();*/

            SystemLog.message("*******************Run GATE**************************");
            //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
            List<String> listAnn = new ArrayList<>(Arrays.asList("MyRegione", "MyPhone", "MyFax", "MyEmail", "MyPartitaIVA",
                    "MyLocalita", "MyIndirizzo", "MyEdificio", "MyProvincia"));
            //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
            List<String> listAnnSet = new ArrayList<>(Arrays.asList("MyFOOTER", "MyHEAD", "MySpecialID", "MyAnnSet"));
            //Store the result on of the extraction on a GateSupport Object
            GateSupport support = null;
            if(controller!=null) {
                support = GateSupport.getInstance(
                        egate.extractorGATE(listUrls, (CorpusController) controller, "corpus_test_1", listAnn, listAnnSet, true));
            }
            if(procDoc!=null){
                support = GateSupport.getInstance(
                        egate.extractorGATE(listUrls, procDoc, "corpus_test_1", listAnn, listAnnSet, true));
            }
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
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    SystemLog.exception(e);
                }
            }
            if(connectionToADatabase) {
                for (GeoDocument geoDoc3 : listGeo) {
                    if (geoDoc3.getUrl() != null) {
                        SystemLog.message(geoDoc3.toString());
                        geoDocumentDao.insertAndTrim(geoDoc3);
                    }//if
                }
            }
        }finally{
            tableAlreadyCreated = true;
            //indGDoc = 0;
        }
        return listGeo;
    }

    /**
     * Method to Extract GeoDocuments from a single url. 2015-09-26
     * @param url url address to a web document.
     * @param TABLE_INPUT String name of the table where do the operation of Select.
     * @param TABLE_OUTPUT String name of the table where insert the geodoucment.
     * @param createNewTable if true Create a new TABLE_OUTPUT with the same name.
     * @param dropOldTable if true Drop the old TABLE_OUTPUT with the same name.
     * @return a List Collection of GeoDocuments, but the geoDocuments are already put in the database.
     */
    public GeoDocument ExtractGeoDocumentFromUrl(
            URL url, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable,boolean dropOldTable){
        IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();
        if(connectionToADatabase) {
            geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE,
                    PORT_DATABASE, USER, PASS, DB_OUTPUT);
            geoDocumentDao.setTableInsert(TABLE_OUTPUT);
            geoDocumentDao.setTableSelect(TABLE_INPUT);
            if(tableAlreadyCreated) {
                if (createNewTable) {
                    try {
                        geoDocumentDao.create(dropOldTable);
                    } catch (Exception e) {
                        SystemLog.exception(e);
                    }finally{
                        tableAlreadyCreated = false;
                    }
                }
            }
        }
        ExtractorInfoGate8 egate = ExtractorInfoGate8.getInstance();
        ExtractorGeoDocumentSupport egs  = new ExtractorGeoDocumentSupport();
        ExtractorJSOUP j = new ExtractorJSOUP();
        GeoDocument geo2 = new GeoDocument();
        GeoDocument geoDoc = new GeoDocument();
        SystemLog.message("(" + indGDoc + ")URL:" + url);
        indGDoc++;
        //check if the site is already present like offline or unreachable
        geoDocumentDao.setTableInsert("offlinesite");
        if(!geoDocumentDao.verifyDuplicate("url",url.toString())){
            geoDocumentDao.setTableInsert(TABLE_OUTPUT);
            SystemLog.message("*******************Run JSOUP**************************");
            try {
                geo2 = j.GetTitleAndHeadingTags(url.toString(), geo2);
                if (ExtractorJSOUP.isEXIST_WEBPAGE()) {
                    try {
                        SystemLog.message("*******************Run GATE**************************");
                        //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
                        String[] anntotations = new String[]{"MyRegione", "MyPhone", "MyFax", "MyEmail", "MyPartitaIVA",
                                "MyLocalita", "MyIndirizzo", "MyEdificio", "MyProvincia"};
                        List<String> listAnn = Arrays.asList(anntotations);
                        //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
                        String[] anntotationsSet = new String[]{"MyFOOTER", "MyHEAD", "MySpecialID", "MyAnnSet"};
                        List<String> listAnnSet = Arrays.asList(anntotationsSet);
                        //Store the result on of the extraction on a GateSupport Object
                        GateSupport support = null;
                        String content = JSoupKit.convertUrlToStringHTML(url.toString());
                        if (controller != null) {
                            if(content!=null){
                                support = GateSupport.getInstance(
                                        egate.extractorGATE(content, (CorpusController) controller, "corpus_test_1", listAnn, listAnnSet, true), true);
                            }else{
                                support = GateSupport.getInstance(
                                        egate.extractorGATE(url, (CorpusController) controller, "corpus_test_1", listAnn, listAnnSet, true), true);
                            }
                            //geoDoc = convertGateSupportToGeoDocument(support, url, 0); //0 because is just a unique document...
                        }
                        if (procDoc != null) {
                            if(content!=null) {
                                support = GateSupport.getInstance(
                                        egate.extractorGATE(content, procDoc, "corpus_test_1", listAnn, listAnnSet, true), true);
                            }else{
                                support = GateSupport.getInstance(
                                        egate.extractorGATE(url, procDoc, "corpus_test_1", listAnn, listAnnSet, true), true);
                            }
                            //geoDoc = convertGateSupportToGeoDocument(support,url,0); //0 because is just a unique document...
                        }
                        geoDoc = convertGateSupportToGeoDocument(support, url, 0); //0 because is just a unique document...
                        SystemLog.message("*******************Run Support GeoDocument**************************");
                        geoDoc = ExtractorGeoDocumentSupport.compareInfo3(geoDoc, geo2);
                        //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
                        geoDoc = egs.UpgradeTheDocumentWithOtherInfo(geoDoc);
                    } catch (URISyntaxException e) {
                        SystemLog.exception(e);
                    }
                    if (connectionToADatabase) {
                        if (geoDoc.getUrl() != null) {
                            SystemLog.message(geoDoc.toString());
                            geoDocumentDao.insertAndTrim(geoDoc);
                        }
                    }//if
                } else {
                    //Try to insert in the offline table
                    SystemLog.warning("The site "+url+" can't be reach...");
                    geoDocumentDao.setTableInsert("offlinesite");
                    geoDocumentDao.insertAndTrim(new String[]{"url"}, new Object[]{url}, new int[]{Types.VARCHAR});
                    return null;
                }
            }catch(IOException|InterruptedException e ){
                SystemLog.exception(e);
            }finally{
                tableAlreadyCreated = true;
                //indGDoc = 0;
            }
            return geoDoc;
        }else{
            //is already offline
            return null;
        }
    }

    /**
     * Method to Extract GeoDocuments from a single url.
     * @param url url address to a web document.
     * @param listAnn list of string Annotations of GATE you want to extract.
     * @param listAnnSet list of string annotationSets of GATE you want to extract.
     * @return a List Collection of GeoDocuments, but the geoDocuments are already put in the database.
     */
    public GateSupport ExtractSupportGateFromUrl(URL url,List<String> listAnn, List<String> listAnnSet){
        ExtractorInfoGate8 egate = ExtractorInfoGate8.getInstance();
        try {
            //Store the result on of the extraction on a GateSupport Object
            GateSupport support = null;
            if(controller!=null) {
                support = GateSupport.getInstance(
                        egate.extractorGATE(url, (CorpusController) controller, "corpus_test_1", listAnn, listAnnSet, true),true);
                return support;
            }
            if(procDoc!=null){
                support = GateSupport.getInstance(
                        egate.extractorGATE(url, procDoc, "corpus_test_1", listAnn, listAnnSet, true),true);
                return support;
            }
        }catch(Exception e ){
            SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Method to Triplify a SQL file from a local file
     * @param karmaModel file of the Karma model you want to use.
     * @param fileToTriplify file SQL you want to triplify.
     * @return new triple file on the same location of the old sql file.
     */
    public File triplifyGeoDocumentFromLocalFile(File karmaModel,File fileToTriplify){
        GenerationOfTriple got = GenerationOfTriple.getInstance();
        return got.GenerationOfTripleWithKarmaFromAFile(karmaModel,fileToTriplify);
    }

    /**
     * Method to Triplify a Relation Table of GeoDocument.
     * @param TABLE_INPUT the already existent Relation Table of GeoDocument.
     * @param TABLE_OUTPUT the new Relation Table of InfoDocument create by TABLE_INPUT.
     * @param OUTPUT_FORMAT the output format of the triple file eg: "ttl","csv","n3","nt",eec.
     * @param MODEL_KARMA the string path to the file Model of Karma R2RML.
     * @param OUTPUT_KARMA_FILE the string path to the folder where save the output triple file generated.
     * @param createNewTable if true create a new table TABLE_OUTPUT.
     * @param dropOldTable if true drop the already existent table TABLE_OUTPUT, only if createNewTable is true.
     */
    public void triplifyGeoDocumentFromDatabase(
            String TABLE_INPUT, String TABLE_OUTPUT, String OUTPUT_FORMAT,
            String MODEL_KARMA, String OUTPUT_KARMA_FILE, boolean createNewTable, boolean dropOldTable){
        SystemLog.message("RUN ONTOLOGY PROGRAMM: Create Table of infoDocument from a geodocument/geodomaindocuemnt table!");
        IInfoDocumentDao infoDocumentDao = new InfoDocumentDaoImpl();
        infoDocumentDao.setTableInsert(TABLE_OUTPUT);
        infoDocumentDao.setTableSelect(TABLE_INPUT);
        infoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE, USER, PASS, DB_OUTPUT);
        if(tableAlreadyCreated) {
            if (createNewTable) {
                try {
                    infoDocumentDao.create(dropOldTable);
                } catch (Exception e) {
                    SystemLog.exception(e);
                }finally{
                    tableAlreadyCreated = false;
                }
            }
        }
        //Choose the Karma Driver:
        String KARMA_DRIVER;
        if(DIALECT_DATABASE.toLowerCase().contains("oracle")) KARMA_DRIVER ="Oracle";
        else if(DIALECT_DATABASE.toLowerCase().contains("mysql")) KARMA_DRIVER ="MySQL";
        else if(DIALECT_DATABASE.toLowerCase().contains("sql")) KARMA_DRIVER ="SQLServer";
        else if(DIALECT_DATABASE.toLowerCase().contains("post")) KARMA_DRIVER ="PostGIS";
        else KARMA_DRIVER ="Sybase";

        //GENERIAMO IL FILE DI TRIPLE CORRISPONDENTE ALLE INFORMAZIONI ESTRATTE CON KARMA
        SystemLog.message("RUN KARMA PROGRAMM: Generation of triple with karma!!");
        GenerationOfTriple got = new GenerationOfTriple(
                "DB", //DB -> A database
                MODEL_KARMA, //PATH: karma_files/model/
                OUTPUT_KARMA_FILE, //PATH: karma_files/output/
                KARMA_DRIVER,//MySQL
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
            File f = got.GenerationOfTripleWithKarmaAPIFromDataBase();
            //code for clean up the triple file generate from karma.
            JenaInfoDocument.readQueryAndCleanTripleInfoDocument(
                    FileUtil.filenameNoExt(f), //filenameInput
                    FileUtil.path(f), //filepath
                    FileUtil.filenameNoExt(f) + "-c", //fileNameOutput
                    FileUtil.extension(f), //inputFormat n3
                    OUTPUT_FORMAT //outputFormat "ttl"
            );
            //delete not filter file of triples
            f.delete();
        } catch (IOException ex) {
            SystemLog.exception(ex);
        }
    }


    /**
     * Method to Extract GeoDocuments from a File or Directory.
     * @param fileOrDirectory file or Directory of Files.
     * @param TABLE_INPUT String name of the table where do the operation of Select.
     * @param TABLE_OUTPUT String name of the table where insert the geodoucment.
     * @param createNewTable if true Create a new TABLE_OUTPUT with the same name.
     * @param dropOldTable if true Drop the old TABLE_OUTPUT with the same name.
     * @return a List Collection of GeoDocuments, but the geoDocuments are already put in the database.
     */
    public List<GeoDocument> ExtractGeoDocumentFromFile(
            File fileOrDirectory, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable,boolean dropOldTable) {
        List<GeoDocument> listGeo = new ArrayList<>();
        if(FileUtil.isDirectory(fileOrDirectory)){
            List<File> listFiles = FileUtil.readDirectory(fileOrDirectory);
            ExtractGeoDocumentFromListFiles(listFiles,TABLE_INPUT,TABLE_OUTPUT,createNewTable,dropOldTable);
        }else{
            //..is a single file
            URL url;
            try {
                url = FileUtil.convertFileToURL(fileOrDirectory);
                listGeo.add(ExtractGeoDocumentFromUrl(url,TABLE_INPUT,TABLE_OUTPUT,createNewTable,dropOldTable));
            } catch (MalformedURLException e) {
                SystemLog.warning(e.getMessage());
                return null;
            }
        }
        return listGeo;
    }

    /**
     * Method to Extract GeoDocuments from a list of Files.
     * @param listFiles List Collection of file , all the Direcotry file are ignored with this method.
     * @param TABLE_INPUT String name of the table where do the operation of Select.
     * @param TABLE_OUTPUT String name of the table where insert the geodoucment.
     * @param createNewTable if true Create a new TABLE_OUTPUT with the same name.
     * @param dropOldTable if true Drop the old TABLE_OUTPUT with the same name.
     * @return a List Collection of GeoDocuments, but the geoDocuments are already put in the database.
     */
    public List<GeoDocument> ExtractGeoDocumentFromListFiles(
            List<File> listFiles, String TABLE_INPUT,String TABLE_OUTPUT,boolean createNewTable,boolean dropOldTable) {
        List<URL> listUrls = new ArrayList<>();
        for(File file: listFiles) {
            try {
                if(!FileUtil.isDirectory(file)) {
                    URL url = FileUtil.convertFileToURL(file);
                    listUrls.add(url);
                }else{
                    SystemLog.warning("The file:" + FileUtil.convertFileToURL(file) + " so is ignored!!");
                }
            } catch (MalformedURLException e) {
                SystemLog.warning(e.getMessage());
            }
        }
        return ExtractGeoDocumentFromListUrls(listUrls,TABLE_INPUT,TABLE_OUTPUT,createNewTable,dropOldTable);
    }


    /**
     * Method to Convert the Object Gate Support to a GeoDocument object.
     * @param support the Gate Support Object.
     * @param url the URL address to the resource.
     * @param index we use the index of the Document Gate on the corpus because we can't know the exact nam of
     *              the document given from the user.
     * @return a GeoDocument Object.
     */
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
                if(!list.isEmpty() && list.get(index).size()>0) {
                    try {
                        for (int j = 0; j < list.get(index).size(); j++) {
                            String content = support.getContent(index, j, nameAnnotation);
                            switch (nameAnnotation) {
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
                            if (flag2) {
                                //flag1 = true;
                                break;
                            }
                        }
                        //    if(flag1)break;
                        // }
                    } catch (java.lang.IndexOutOfBoundsException e) {
                        SystemLog.exception(e);
                    }
                }//if !isEmpty
            }
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return geoDoc;
    }

    /**
     * Method to support the Extraction of a List of URLS from a Table of the database.
     * @param DRIVER_DATABASE driver of the database eg: "com.mysql.jdbc.Driver".
     * @param DIALECT_DATABASE dialect of the databse eg: "jdbc:mysql".
     * @param HOST_DATABASE host of the database eg: "localhost".
     * @param PORT_DATABASE port of the databse eg: "3306".
     * @param USER username of the database eg: "username".
     * @param PASS passwrod of the database eg: "password".
     * @param DB_INPUT strig name of the database eg: "database".
     * @param TABLE_INPUT string name of the table where make the select  eg: "table".
     * @param COLUMN_TABLE_INPUT string name of the column of the table where make th select eg:"url".
     * @param LIMIT LIMIT of the select query SQL.
     * @param OFFSET OFFSET of the select query SQL.
     * @return a List Collection of java.net.URL.
     */
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

    /**
     * Method for update all record with the Coordinates null or empty.
     * @param geoDoc GeoDocument to update.
     * @param columns_where the columns on the where clause of the SQL query.
     * @param LIMIT limit of the SQL query.
     * @param OFFSET offset of the SQL query.
     * @param isNumeric if true the value null can be a number.
     * @return list of all geodocument to update on the database
     */
    public  List<GeoDocument> reloadGeoDocumentNullColumns(
            GeoDocument geoDoc,String[] columns_where,Integer LIMIT,Integer OFFSET,boolean isNumeric){
        IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();
        if(connectionToADatabase) {
            geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE,
                    PORT_DATABASE, USER, PASS, DB_OUTPUT);
            geoDocumentDao.setTableInsert(TABLE_OUTPUT);
            geoDocumentDao.setTableSelect(TABLE_INPUT);
        }
        //ManageJsonWithGoogleMaps j = ManageJsonWithGoogleMaps.getInstance();
        //String[] columns_where = new String[]{"latitude","longitude"};
        Object[] values_where = new Object[]{null,null};
        List<GeoDocument> list;

        list = geoDocumentDao.trySelect(
                new String[]{"*"}, columns_where, values_where, LIMIT, OFFSET, null);

        values_where = new Object[]{"",""};
        List<GeoDocument> list2 =
                geoDocumentDao.trySelect(
                        new String[]{"*"}, columns_where, values_where, LIMIT, OFFSET, null);
        list.addAll(list2);
        if(isNumeric) {
            values_where = new Object[]{0, 0};
            list2 = geoDocumentDao.trySelect(
                    new String[]{"*"}, columns_where, values_where, LIMIT, OFFSET, null);
            list.addAll(list2);
        }
      /*  for (GeoDocument geo : list) {
            LatLng coord = j.getCoords(geo);
                    if (coord.getLat() == 0 && coord.getLng() == 0) {
                        values_where = new Object[]{null, null};
                    } else {
                        values_where = new Object[]{coord.getLat(), coord.getLng()};

                    }
            geoDocumentDao.update(columns_where, values_where, "url", geo.getUrl().toString().replace("http://", ""));
        }*/
        return list;
    }

}
