package com.github.p4535992.extractor.estrattori;

import com.github.p4535992.extractor.object.dao.jdbc.*;
import com.github.p4535992.extractor.object.impl.jdbc.*;
import com.github.p4535992.gatebasic.gate.gate8.GateDataStore8Kit;
import com.github.p4535992.util.file.SimpleParameters;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Classe con la quale da una lista di url realizziamo una lista di GeoDocument
 * da inserire come InfoDocument nel database mySQL.
 * @author 4535992.
 * @version 2015-07-06.
 */
@SuppressWarnings("unused")
public class ExtractInfoSpring {

     //private boolean SAVE_DATASTORE = false;
     //private String TYPE_EXTRACTION;
    private Integer PROCESS_PROGAMM,LIMIT,OFFSET;
    private boolean CREATE_NEW_GEODOCUMENT_TABLE,ERASE;
    private boolean ONTOLOGY_PROGRAMM,GENERATION_TRIPLE_KARMA_PROGRAMM,GEODOMAIN_PROGRAMM,SILK_LINKING_TRIPLE_PROGRAMM;
    private String USER,PASS,DB_INPUT, DB_OUTPUT, TABLE_INPUT,TABLE_OUTPUT,COLUMN_TABLE_INPUT,
             TABLE_KEYWORD_DOCUMENT,DB_KEYWORD,DRIVER_DATABASE,DIALECT_DATABASE,HOST_DATABASE;
    private Integer PORT_DATABASE;
    private String TABLE_OUTPUT_ONTOLOGY,TABLE_INPUT_ONTOLOGY;

    /*private boolean FILTER,CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY,ERASE_ONTOLOGY;
    private String API_KEY_GM,TYPE_DATABASE_KARMA,FILE_MAP_TURTLE_KARMA,FILE_OUTPUT_TRIPLE_KARMA,ID_DATABASE_KARMA,
             TABLE_INPUT_KARMA,OUTPUT_FORMAT_KARMA,KARMA_HOME;*/
    private boolean CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY,ERASE_ONTOLOGY;
    private String FILE_MAP_TURTLE_KARMA,FILE_OUTPUT_TRIPLE_KARMA,OUTPUT_FORMAT_KARMA;

    private Integer LIMIT_GEODOMAIN, OFFSET_GEODOMAIN,FREQUENZA_URL_GEODOMAIN;
    private String TABLE_INPUT_GEODOMAIN,TABLE_OUTPUT_GEODOMAIN,DB_OUTPUT_GEODOMAIN;
    private boolean CREA_NUOVA_TABELLA_GEODOMAIN,ERASE_GEODOMAIN;

    private String DIRECTORY_FILES,SILK_SLS_FILE;

    IDocumentDao Docdao = new DocumentDaoImpl();
    IWebsiteDao websiteDao = new WebsiteDaoImpl();
    public static IGeoDocumentDao getGeoDocumentDao() {
        return geoDocumentDao;
    }
    private static IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();

    private static ExtractInfoSpring instance = null;

    public static ExtractInfoSpring getInstance(SimpleParameters par){
        if(instance == null) {
            instance = new ExtractInfoSpring(par);
        }
        return instance;
    }

     protected ExtractInfoSpring(SimpleParameters par){
        try {
            //this.TYPE_EXTRACTION = par.getValue("PARAM_TYPE_EXTRACTION");
            this.PROCESS_PROGAMM = Integer.parseInt(par.getValue("PARAM_PROCESS_PROGAMM"));

            this.CREATE_NEW_GEODOCUMENT_TABLE = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_GEODOCUMENT").toLowerCase());
            this.ERASE = Boolean.parseBoolean(par.getValue("PARAM_ERASE").toLowerCase());
            this.LIMIT = Integer.parseInt(par.getValue("PARAM_LIMIT"));
            this.OFFSET = Integer.parseInt(par.getValue("PARAM_OFFSET"));

            this.ONTOLOGY_PROGRAMM = Boolean.parseBoolean(par.getValue("PARAM_ONTOLOGY_PROGRAMM").toLowerCase());
            this.GENERATION_TRIPLE_KARMA_PROGRAMM = Boolean.parseBoolean(par.getValue("PARAM_GENERATION_TRIPLE_KARMA_PROGRAMM").toLowerCase());
            this.GEODOMAIN_PROGRAMM = Boolean.parseBoolean(par.getValue("PARAM_GEODOMAIN_PROGRAMM").toLowerCase());

            //this.FILTER = Boolean.parseBoolean(par.getValue("PARAM_FILTER").toLowerCase());
            //this.API_KEY_GM = par.getValue("PARAM_API_KEY_GM");

            this.USER = par.getValue("PARAM_USER");
            this.PASS = par.getValue("PARAM_PASS");
            this.DB_INPUT = par.getValue("PARAM_DB_INPUT");
            this.DB_OUTPUT = par.getValue("PARAM_DB_OUTPUT");
            this.TABLE_INPUT = par.getValue("PARAM_TABLE_INPUT");
            this.TABLE_OUTPUT = par.getValue("PARAM_TABLE_OUTPUT");
            this.COLUMN_TABLE_INPUT = par.getValue("PARAM_COLUMN_TABLE_INPUT");

            this.TABLE_KEYWORD_DOCUMENT = par.getValue("PARAM_TABLE_KEYWORD_DOCUMENT");
            this.DB_KEYWORD = par.getValue("PARAM_DB_KEYWORD");

            this.DRIVER_DATABASE = par.getValue("PARAM_DRIVER_DATABASE");
            this.DIALECT_DATABASE = par.getValue("PARAM_DIALECT_DATABASE");
            this.HOST_DATABASE = par.getValue("PARAM_HOST_DATABASE");
            this.PORT_DATABASE = Integer.parseInt(par.getValue("PARAM_PORT_DATABASE"));

            //this.SAVE_DATASTORE = Boolean.parseBoolean(par.getValue("PARAM_SAVE_DATASTORE").toLowerCase());

            if(PROCESS_PROGAMM==3){
                this.DIRECTORY_FILES = par.getValue("PARAM_DIRECTORY_FILES");
            }

            if (Boolean.parseBoolean(par.getValue("PARAM_SAVE_DATASTORE").toLowerCase())) {
                String NOME_DATASTORE = par.getValue("PARAM_NOME_DATASTORE");
                String DS_DIR = par.getValue("PARAM_DS_DIR");
                //this.RANGE = Integer.parseInt(par.getValue("PARAM_RANGE"));
                //this.tentativiOutMemory = Integer.parseInt(par.getValue("PARAM_TENTATIVI_OUT_OF_MEMORY"));
                GateDataStore8Kit gds8 = GateDataStore8Kit.getInstance(DS_DIR, NOME_DATASTORE);

            }

            if (GENERATION_TRIPLE_KARMA_PROGRAMM) {
                //this.TYPE_DATABASE_KARMA = par.getValue("PARAM_TYPE_DATABASE_KARMA");//MySQL
                this.FILE_MAP_TURTLE_KARMA = par.getValue("PARAM_FILE_MAP_TURTLE_KARMA"); //PATH: karma_files/model/
                this.FILE_OUTPUT_TRIPLE_KARMA = par.getValue("PARAM_FILE_OUTPUT_TRIPLE_KARMA");//PATH: karma_files/output/
                //this.ID_DATABASE_KARMA = par.getValue("PARAM_ID_DATABASE_KARMA");//DB
                //this.TABLE_INPUT_KARMA = par.getValue("PARAM_TABLE_INPUT_KARMA");
                this.OUTPUT_FORMAT_KARMA = par.getValue("PARAM_OUTPUT_FORMAT_KARMA");
                //this.KARMA_HOME = par.getValue("PARAM_KARMA_HOME");
            }


            this.CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY").toLowerCase());
            this.ERASE_ONTOLOGY = Boolean.parseBoolean(par.getValue("PARAM_ERASE_ONTOLOGY").toLowerCase());
            this.TABLE_OUTPUT_ONTOLOGY = par.getValue("PARAM_TABLE_OUTPUT_ONTOLOGY");
            this.TABLE_INPUT_ONTOLOGY = par.getValue("PARAM_TABLE_INPUT_ONTOLOGY");


            this.LIMIT_GEODOMAIN = Integer.parseInt(par.getValue("PARAM_LIMIT_GEODOMAIN"));
            this.OFFSET_GEODOMAIN = Integer.parseInt(par.getValue("PARAM_OFFSET_GEODOMAIN"));
            this.FREQUENZA_URL_GEODOMAIN = Integer.parseInt(par.getValue("PARAM_FREQUENZA_URL_GEODOMAIN"));
            this.TABLE_INPUT_GEODOMAIN = par.getValue("PARAM_TABLE_INPUT_GEODOMAIN");
            this.TABLE_OUTPUT_GEODOMAIN = par.getValue("PARAM_TABLE_OUTPUT_GEODOMAIN");
            //this.DB_INPUT_GEODOMAIN = par.getValue("PARAM_DB_OUTPUT");
            this.DB_OUTPUT_GEODOMAIN = par.getValue("PARAM_DB_OUTPUT");
            this.CREA_NUOVA_TABELLA_GEODOMAIN = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_GEODOMAIN").toLowerCase());
            this.ERASE_GEODOMAIN = Boolean.parseBoolean(par.getValue("PARAM_ERASE_GEODOMAIN").toLowerCase());

            this.SILK_LINKING_TRIPLE_PROGRAMM = Boolean.parseBoolean(par.getValue("PARAM_SILK_LINKING_TRIPLE_PROGRAMM").toLowerCase());
            this.SILK_SLS_FILE = par.getValue("PARAM_SILK_SLS_FILE");
        }catch(java.lang.NullPointerException ne){
            SystemLog.warning("Attention: make sure all the parameter on the input.properties file are setted correctly");
            SystemLog.exception(ne);
            SystemLog.abort(0,"EXIT PROGRAMM");
        }
         par.getParameters().clear();
         //Set the onbjects so we not call them again after each url
         Docdao.setTableSelect(TABLE_KEYWORD_DOCUMENT);
         Docdao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_KEYWORD);
         geoDocumentDao.setTableInsert(TABLE_OUTPUT);
         geoDocumentDao.setTableSelect(TABLE_OUTPUT);
         geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
         //websiteDao.setTableSelect(TABLE_INPUT);
         //websiteDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_INPUT);


     }


    /**
     * Metodo Main del programma per la creazione di InfoDocument.
     */
    public void Extraction(){
        try{
             ExtractInfoWeb web = ExtractInfoWeb.getInstance(
                     DRIVER_DATABASE,DIALECT_DATABASE,HOST_DATABASE,PORT_DATABASE.toString(),USER,PASS,DB_OUTPUT);
             if(PROCESS_PROGAMM < 4){
                 SystemLog.message("Run the extraction method.");
                 List<URL> listUrl = ExtractInfoWeb.getInstance().getListURLFromDatabase(
                         DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE,
                         PORT_DATABASE.toString(), USER, PASS, DB_INPUT, TABLE_INPUT, COLUMN_TABLE_INPUT, LIMIT, OFFSET);

                 //Filter and remove all unreachable or errate address web...
                 geoDocumentDao.setTableInsert("offlinesite");
                 List<URL> supportList = new ArrayList<>();
                 for(URL url : listUrl) {
                     if (geoDocumentDao.verifyDuplicate("url", url.toString())) {
                         supportList.add(url);
                     }
                 }
                 for (URL aSupportList : supportList) {
                     SystemLog.warning("The site " + aSupportList + " can't be reach...");
                     for (int j = 0; j < listUrl.size(); j++) {
                         if (listUrl.get(j) == aSupportList) {
                             listUrl.remove(j);
                             break;
                         }
                     }
                 }
                 geoDocumentDao.setTableInsert(TABLE_INPUT);
                 supportList.clear();

                 //Initialize GATE...
                 List<GeoDocument> listGeo = new ArrayList<>();
                 web.setGate("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
                         "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
                 if(PROCESS_PROGAMM == 1){
                     SystemLog.message("RUN PROCESS 1: Abilitate for each single url");
                     if(listUrl.isEmpty()){
                         SystemLog.message("The list of urls you get from the table:"+TABLE_INPUT+
                                 " from the columns "+COLUMN_TABLE_INPUT+" empty!!!");
                     }else {
                         SystemLog.attention("Loaded a list of: "+listUrl.size()+" files");
                         for (URL url : listUrl) {
                             GeoDocument geoDoc = web.ExtractGeoDocumentFromUrl(
                                     url, TABLE_OUTPUT, TABLE_OUTPUT, CREATE_NEW_GEODOCUMENT_TABLE,ERASE);
                             if(geoDoc!=null)listGeo.add(geoDoc);
                         }
                     }
                 }else if(PROCESS_PROGAMM == 2){
                     SystemLog.message("RUN PROCESS 2: Abilitate for multiple url");
                     if(listUrl.isEmpty()){
                         SystemLog.message("The list of urls you get from the table:"+TABLE_INPUT+
                                 " from the columns "+COLUMN_TABLE_INPUT+" empty!!!");
                     }else {
                         SystemLog.attention("Loaded a list of: "+listUrl.size()+" files");
                         web.ExtractGeoDocumentFromListUrls(
                                 listUrl, TABLE_INPUT, TABLE_OUTPUT,CREATE_NEW_GEODOCUMENT_TABLE,ERASE);
                     }
                }else if(PROCESS_PROGAMM == 3){
                     SystemLog.message("RUN PROCESS 3: Abilitate for single file or for a directory");
                     //String DIRECTORY_FILE = "C:\\Users\\Marco\\Downloads\\parseWebUrls";
                     List<File> files = new ArrayList<>();
                     List<File> subFiles = new ArrayList<>(); //suppport list...
                     Map<File,String> mapFile = new HashMap<>();
                     //code for make some verify before go to the extraction...
                     if(FileUtil.isDirectory(new File(DIRECTORY_FILES))) {
                         files = FileUtil.readDirectory(DIRECTORY_FILES);
                         //We use the index of the list like  bookmark for all the files in the directory...
                         if(OFFSET+LIMIT < files.size())subFiles.addAll(files.subList(OFFSET,OFFSET+LIMIT));
                         else subFiles.addAll(files.subList(OFFSET, files.size()));
                         files.clear();
                     }else{
                         subFiles.add(new File(DIRECTORY_FILES));
                     }
                     //avoid already present on the database url of the file...
                     for(File file : subFiles){
                        String urlFile =(String) websiteDao.select("url", "file_path", file.getName());
                        if(!(urlFile == null || (geoDocumentDao.verifyDuplicate("url", urlFile))
                        )){
                            files.add(file);
                            mapFile.put(file,urlFile);
                        }
                     }
                     subFiles.clear();
                     subFiles.addAll(files);
                     files.clear();

                     if(subFiles.isEmpty()){
                         SystemLog.message("The list of urls you get from the table:" + TABLE_INPUT +
                                 " from the columns " + COLUMN_TABLE_INPUT + " empty!!!");
                     }else {
                         SystemLog.attention("Loaded a list of: "+subFiles.size()+" files");
                         web.ExtractGeoDocumentFromListFiles(
                                 subFiles, TABLE_INPUT, TABLE_OUTPUT, CREATE_NEW_GEODOCUMENT_TABLE,ERASE);
                     }
                     SystemLog.message("Obtained a list of: " + listGeo.size() + " GeoDocument");
                 }//else lista url non vuota
                 else SystemLog.warning("You are jump the extraction process! (use 1,2 or 3 on the Proces Porgamma Parameter)");
             }
            //Other process after the extraction of the information....
            if(PROCESS_PROGAMM == 4) {
                SystemLog.message("RUN PROCESS 4");
                //CREAZIONE DI UNA TABELLA DI GEODOMAINDOCUMENT
                if (GEODOMAIN_PROGRAMM) {
                    SystemLog.message("RUN GEODOMAIN PROGRAMM: Create a geodomaindocument table from a geodocument table!");
                    IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
                    geoDomainDocumentDao.setTableInsert(TABLE_OUTPUT_GEODOMAIN);
                    geoDomainDocumentDao.setTableSelect(TABLE_INPUT_GEODOMAIN);
                    geoDomainDocumentDao.setDriverManager(
                            DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(),
                            USER, PASS, DB_OUTPUT_GEODOMAIN);
                    if (CREA_NUOVA_TABELLA_GEODOMAIN) {
                        geoDomainDocumentDao.create(ERASE_GEODOMAIN);
                    }
                    ExtractorDomain egd = new ExtractorDomain(
                            (GeoDomainDocumentDaoImpl) geoDomainDocumentDao,
                            LIMIT_GEODOMAIN, OFFSET_GEODOMAIN, FREQUENZA_URL_GEODOMAIN);
                    egd.CreateTableOfGeoDomainDocument("sql");
                }
                //INTEGRIAMO LA TABELLA INFODOCUMENT PER LAVORARE CON UN'ONTOLOGIA
                if (ONTOLOGY_PROGRAMM || GENERATION_TRIPLE_KARMA_PROGRAMM) {
                    SystemLog.message("RUN ONTOLOGY PROGRAMM: Create Table of infodocument from a geodocument/geodomaindocument table!");
                    SystemLog.message("RUN KARMA PROGRAMM: Generation of triple with Web-karma!!");
                    web.triplifyGeoDocumentFromDatabase(
                            TABLE_INPUT_ONTOLOGY, TABLE_OUTPUT_ONTOLOGY,
                            OUTPUT_FORMAT_KARMA, FILE_MAP_TURTLE_KARMA, FILE_OUTPUT_TRIPLE_KARMA,
                            CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY, ERASE_ONTOLOGY);
                }
                if(SILK_LINKING_TRIPLE_PROGRAMM){
                    if(new File(SILK_SLS_FILE).exists()){
                        de.fuberlin.wiwiss.silk.Silk.executeFile(new File(SILK_SLS_FILE), "interlink_location", 2, true);
                    }else{
                        SystemLog.error("The "+new File(SILK_SLS_FILE).getAbsolutePath()+" not exists!!");
                    }
                }
            }
           /* if(PROCESS_PROGAMM == 5){
                SystemLog.message("RUN PROCESS PROGRAMM 5: UPDATE COORDINATES ON GEODOMAIN TABLE");
                IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
                geoDomainDocumentDao.setTableInsert(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableSelect(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableUpdate(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT_GEODOMAIN);
                if (CREA_NUOVA_TABELLA_GEODOMAIN) {
                    geoDomainDocumentDao.create(ERASE_GEODOMAIN);
                }
                egd = new ExtractorDomain((GeoDomainDocumentDaoImpl) geoDomainDocumentDao, LIMIT_GEODOMAIN, OFFSET_GEODOMAIN, FREQUENZA_URL_GEODOMAIN);
                egd.reloadNullCoordinates();
            }*/
            /*if(PROCESS_PROGAMM == 6){
                SystemLog.message("RUN PROCESS PROGRAMM 6:
                DELETE OVERRRIDE RECORD GEODOMAINDOCUMENT TABLE WITH SIIMOBILITY COORDINATES");
                IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
                geoDomainDocumentDao.setTableInsert(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableSelect(TABLE_INPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableUpdate(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT_GEODOMAIN);
                if (CREA_NUOVA_TABELLA_GEODOMAIN) {
                    geoDomainDocumentDao.create(ERASE_GEODOMAIN);
                }
                egd = new ExtractorDomain((GeoDomainDocumentDaoImpl) geoDomainDocumentDao, LIMIT_GEODOMAIN, OFFSET_GEODOMAIN, FREQUENZA_URL_GEODOMAIN);
                List<String> listUrlGM = geoDomainDocumentDao.trySelect("url", 500, 0);
                Map<String,String> map = new HashMap<>();
                for(String url: listUrlGM){
                    String ids = (String)geoDomainDocumentDao.select("doc_id", "url", url);
                    map.put(ids,url);
                }

                egd.deleteOverrideRecord(map);
            }*/
        }catch(OutOfMemoryError e) {
            SystemLog.error("java.lang.OutOfMemoryError, Reload the programm please");
        }
    }//main
}