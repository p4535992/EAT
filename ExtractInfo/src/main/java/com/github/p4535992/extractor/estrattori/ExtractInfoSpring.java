package com.github.p4535992.extractor.estrattori;

import com.github.p4535992.extractor.estrattori.gtfs.GTFSUtilities;
import com.github.p4535992.extractor.estrattori.silk.SilkUtilities;
import com.github.p4535992.extractor.object.dao.jdbc.*;
import com.github.p4535992.extractor.object.impl.jdbc.*;
import com.github.p4535992.gatebasic.gate.gate8.GateDataStore8Kit;
import com.github.p4535992.util.database.sql.SQLUtilities;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.file.PropertiesUtilities;
import com.github.p4535992.util.file.SimpleParameters;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.util.http.HttpUtilities;
import com.github.p4535992.util.repositoryRDF.sesame.Sesame2Utilities;
import com.github.p4535992.util.string.StringUtilities;

import org.openrdf.repository.Repository;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Classe con la quale da una lista di url realizziamo una lista di GeoDocument
 * da inserire come InfoDocument nel database mySQL.
 *
 * @author 4535992.
 * @version 2015-07-06.
 */
@SuppressWarnings("unused")
@Configuration
public class ExtractInfoSpring {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ExtractInfoSpring.class);

    //private boolean SAVE_DATASTORE = false;
    //private String TYPE_EXTRACTION;
    private Integer PROCESS_PROGAMM, LIMIT, OFFSET;
    private boolean CREATE_NEW_GEODOCUMENT_TABLE, ERASE;
    private boolean ONTOLOGY_PROGRAMM, GENERATION_TRIPLE_KARMA_PROGRAMM, GEODOMAIN_PROGRAMM, SILK_LINKING_TRIPLE_PROGRAMM;
    private String USER, PASS, DB_INPUT, DB_OUTPUT, TABLE_INPUT, TABLE_OUTPUT, COLUMN_TABLE_INPUT,
            TABLE_KEYWORD_DOCUMENT, DB_KEYWORD, DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE;
    private Integer PORT_DATABASE;
    private String TABLE_OUTPUT_ONTOLOGY, TABLE_INPUT_ONTOLOGY;

    /*private boolean FILTER,CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY,ERASE_ONTOLOGY;
    private String API_KEY_GM,TYPE_DATABASE_KARMA,FILE_MAP_TURTLE_KARMA,FILE_OUTPUT_TRIPLE_KARMA,ID_DATABASE_KARMA,
             TABLE_INPUT_KARMA,OUTPUT_FORMAT_KARMA,KARMA_HOME;*/
    private boolean CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY, ERASE_ONTOLOGY;
    private String FILE_MAP_TURTLE_KARMA, FILE_OUTPUT_TRIPLE_KARMA, OUTPUT_FORMAT_KARMA;

    private Integer LIMIT_GEODOMAIN, OFFSET_GEODOMAIN, FREQUENZA_URL_GEODOMAIN;
    private String TABLE_INPUT_GEODOMAIN, TABLE_OUTPUT_GEODOMAIN, DB_OUTPUT_GEODOMAIN;
    private boolean CREA_NUOVA_TABELLA_GEODOMAIN, ERASE_GEODOMAIN;

    private String DIRECTORY_FILES, SILK_SLS_FILE,SILK_INTERLINK_ID;
    private Integer SILK_NUM_THREADS;
    private boolean SILK_LOGQUERIES,SILK_RELOAD;

    private String TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL ,GATE_CORPUS_NAME;
    private boolean CREA_NUOVA_TABELLA_OFFLINE,ERASE_OFFLINE;

    /*http://stackoverflow.com/questions/6212898/spring-properties-file-get-element-as-an-array*/
    /*http://stackoverflow.com/questions/12576156/reading-a-list-from-properties-file-and-load-with-spring-annotation-value*/
    private String[] GATE_ANN_LIST,GATE_ANNSET_LIST;
    private String GATE_PLUGIN_PATH,GATE_SITE_CONFIG,GATE_USER_CONFIG,GATE_SESSION_CONFIG,GATE_GAPP_FILE,GATE_HOME_PATH;
    private String SESAME_URL_REPOSITORY,SESAME_FILE_TO_IMPORT;
    private String GTFS_FILE_RDF_GENERATE;

    private static IDocumentDao Docdao = new DocumentDaoImpl();
    private static IWebsiteDao websiteDao = new WebsiteDaoImpl();
    private static IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();

    private static ExtractInfoSpring instance = null;

    public static ExtractInfoSpring getInstance(SimpleParameters par) {
        if (instance == null) {
            instance = new ExtractInfoSpring(par);
        }
        return instance;
    }

    public static ExtractInfoSpring getInstance(Properties env) {
        if (instance == null) {
            instance = new ExtractInfoSpring(env);
        }
        return instance;
    }

    public static ExtractInfoSpring getInstance() {
        if (instance == null) {
            instance = new ExtractInfoSpring();
        }
        return instance;
    }

    public static ExtractInfoSpring getNewInstance(Properties env) {
        instance = new ExtractInfoSpring(env);
        return instance;
    }

    public static ExtractInfoSpring getNewInstance() {
        instance = new ExtractInfoSpring();
        return instance;
    }

    protected ExtractInfoSpring() {
    }

    protected ExtractInfoSpring(Properties env) {
        try {
            env = PropertiesUtilities.prepareProperties(env);

            //this.TYPE_EXTRACTION = par.getValue("PARAM_TYPE_EXTRACTION");
            this.PROCESS_PROGAMM = Integer.parseInt(env.getProperty("PARAM_PROCESS_PROGAMM"));

            this.CREATE_NEW_GEODOCUMENT_TABLE = Boolean.parseBoolean(env.getProperty("PARAM_CREA_NUOVA_TABELLA_GEODOCUMENT").toLowerCase());
            this.ERASE = Boolean.parseBoolean(env.getProperty("PARAM_ERASE").toLowerCase());
            this.LIMIT = Integer.parseInt(env.getProperty("PARAM_LIMIT"));
            this.OFFSET = Integer.parseInt(env.getProperty("PARAM_OFFSET"));

            this.ONTOLOGY_PROGRAMM = Boolean.parseBoolean(env.getProperty("PARAM_ONTOLOGY_PROGRAMM").toLowerCase());
            this.GENERATION_TRIPLE_KARMA_PROGRAMM = Boolean.parseBoolean(env.getProperty("PARAM_GENERATION_TRIPLE_KARMA_PROGRAMM").toLowerCase());
            this.GEODOMAIN_PROGRAMM = Boolean.parseBoolean(env.getProperty("PARAM_GEODOMAIN_PROGRAMM").toLowerCase());

            //this.FILTER = Boolean.parseBoolean(par.getValue("PARAM_FILTER").toLowerCase());
            //this.API_KEY_GM = par.getValue("PARAM_API_KEY_GM");

            this.USER = env.getProperty("PARAM_USER");
            this.PASS = env.getProperty("PARAM_PASS");
            this.DB_INPUT = env.getProperty("PARAM_DB_INPUT");
            this.DB_OUTPUT = env.getProperty("PARAM_DB_OUTPUT");
            this.TABLE_INPUT = env.getProperty("PARAM_TABLE_INPUT");
            this.TABLE_OUTPUT = env.getProperty("PARAM_TABLE_OUTPUT");
            this.COLUMN_TABLE_INPUT = env.getProperty("PARAM_COLUMN_TABLE_INPUT");

            this.TABLE_KEYWORD_DOCUMENT = env.getProperty("PARAM_TABLE_KEYWORD_DOCUMENT");
            this.DB_KEYWORD = env.getProperty("PARAM_DB_KEYWORD");

            this.DRIVER_DATABASE = env.getProperty("PARAM_DRIVER_DATABASE");
            this.DIALECT_DATABASE = env.getProperty("PARAM_DIALECT_DATABASE");
            this.HOST_DATABASE = env.getProperty("PARAM_HOST_DATABASE");
            this.PORT_DATABASE = Integer.parseInt(env.getProperty("PARAM_PORT_DATABASE"));

            //this.SAVE_DATASTORE = Boolean.parseBoolean(par.getValue("PARAM_SAVE_DATASTORE").toLowerCase());

            if (PROCESS_PROGAMM == 3) {
                this.DIRECTORY_FILES = env.getProperty("PARAM_DIRECTORY_FILES");
            }

            if (Boolean.parseBoolean(env.getProperty("PARAM_SAVE_DATASTORE").toLowerCase())) {
                String NOME_DATASTORE = env.getProperty("PARAM_NOME_DATASTORE");
                String DS_DIR = env.getProperty("PARAM_DS_DIR");
                //this.RANGE = Integer.parseInt(par.getValue("PARAM_RANGE"));
                //this.tentativiOutMemory = Integer.parseInt(par.getValue("PARAM_TENTATIVI_OUT_OF_MEMORY"));
                GateDataStore8Kit gds8 = GateDataStore8Kit.getInstance(DS_DIR, NOME_DATASTORE);
            }

            if (GENERATION_TRIPLE_KARMA_PROGRAMM) {
                //this.TYPE_DATABASE_KARMA = par.getValue("PARAM_TYPE_DATABASE_KARMA");//MySQL
                this.FILE_MAP_TURTLE_KARMA = env.getProperty("PARAM_FILE_MAP_TURTLE_KARMA"); //PATH: karma_files/model/
                this.FILE_OUTPUT_TRIPLE_KARMA = env.getProperty("PARAM_FILE_OUTPUT_TRIPLE_KARMA");//PATH: karma_files/output/
                //this.ID_DATABASE_KARMA = par.getValue("PARAM_ID_DATABASE_KARMA");//DB
                //this.TABLE_INPUT_KARMA = par.getValue("PARAM_TABLE_INPUT_KARMA");
                this.OUTPUT_FORMAT_KARMA = env.getProperty("PARAM_OUTPUT_FORMAT_KARMA");
                //this.KARMA_HOME = par.getValue("PARAM_KARMA_HOME");
            }


            this.CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY = Boolean.parseBoolean(env.getProperty("PARAM_CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY").toLowerCase());
            this.ERASE_ONTOLOGY = Boolean.parseBoolean(env.getProperty("PARAM_ERASE_ONTOLOGY").toLowerCase());
            this.TABLE_OUTPUT_ONTOLOGY = env.getProperty("PARAM_TABLE_OUTPUT_ONTOLOGY");
            this.TABLE_INPUT_ONTOLOGY = env.getProperty("PARAM_TABLE_INPUT_ONTOLOGY");


            this.LIMIT_GEODOMAIN = Integer.parseInt(env.getProperty("PARAM_LIMIT_GEODOMAIN"));
            this.OFFSET_GEODOMAIN = Integer.parseInt(env.getProperty("PARAM_OFFSET_GEODOMAIN"));
            this.FREQUENZA_URL_GEODOMAIN = Integer.parseInt(env.getProperty("PARAM_FREQUENZA_URL_GEODOMAIN"));
            this.TABLE_INPUT_GEODOMAIN = env.getProperty("PARAM_TABLE_INPUT_GEODOMAIN");
            this.TABLE_OUTPUT_GEODOMAIN = env.getProperty("PARAM_TABLE_OUTPUT_GEODOMAIN");
            //this.DB_INPUT_GEODOMAIN = par.getValue("PARAM_DB_OUTPUT");
            this.DB_OUTPUT_GEODOMAIN = env.getProperty("PARAM_DB_OUTPUT");
            this.CREA_NUOVA_TABELLA_GEODOMAIN = Boolean.parseBoolean(env.getProperty("PARAM_CREA_NUOVA_TABELLA_GEODOMAIN").toLowerCase());
            this.ERASE_GEODOMAIN = Boolean.parseBoolean(env.getProperty("PARAM_ERASE_GEODOMAIN").toLowerCase());

            this.SILK_LINKING_TRIPLE_PROGRAMM = Boolean.parseBoolean(env.getProperty("PARAM_SILK_LINKING_TRIPLE_PROGRAMM").toLowerCase());
            this.SILK_SLS_FILE = env.getProperty("PARAM_SILK_SLS_FILE");
            this.SILK_INTERLINK_ID = env.getProperty("PARAM_SILK_INTERLINK_ID");
            this.SILK_NUM_THREADS = Integer.parseInt(env.getProperty("PARAM_SILK_NUM_THREADS"));
            this.SILK_LOGQUERIES = Boolean.parseBoolean(env.getProperty("PARAM_SILK_LOGQUERIES").toLowerCase());
            this.SILK_RELOAD = Boolean.parseBoolean(env.getProperty("PARAM_SILK_RELOAD").toLowerCase());

            this.TABLE_REF_OFFLINE = env.getProperty("PARAM_TABLE_REF_OFFLINE");
            this.COLUMN_OFFLINE_REF_URL = env.getProperty("PARAM_COLUMN_OFFLINE_REF_URL");
            this.ERASE_OFFLINE = Boolean.parseBoolean(env.getProperty("PARAM_ERASE_OFFLINE").toLowerCase());
            this.CREA_NUOVA_TABELLA_OFFLINE = Boolean.parseBoolean(env.getProperty("PARAM_CREA_NUOVA_TABELLA_OFFLINE").toLowerCase());

            if(CREA_NUOVA_TABELLA_OFFLINE){
                Connection conn = SQLUtilities.getMySqlConnection(HOST_DATABASE,PORT_DATABASE.toString(),USER,PASS);
                if(ERASE_OFFLINE){
                    SQLUtilities.executeSQL("DROP TABLE "+DB_OUTPUT+"."+TABLE_REF_OFFLINE,conn);
                }
                SQLUtilities.executeSQL(
                        "CREATE TABLE `"+TABLE_REF_OFFLINE+"` (\n" +
                        "  `id` int(10) DEFAULT NULL,\n" +
                        "  `url` varchar(1000) DEFAULT NULL\n" +
                        ")",conn);
                SQLUtilities.closeConnection();
                conn.close();
            }

            this.GATE_ANN_LIST = env.getProperty("PARAM_GATE_ANN_LIST").split(",");
            this.GATE_ANNSET_LIST = env.getProperty("PARAM_GATE_ANNSET_LIST").split(",");
            this.GATE_CORPUS_NAME = env.getProperty("PARAM_GATE_CORPUS_NAME");


            this.GATE_HOME_PATH=env.getProperty("PARAM_GATE_HOME_PATH");
            this.GATE_PLUGIN_PATH=env.getProperty("PARAM_GATE_PLUGIN_PATH");
            this.GATE_SITE_CONFIG=env.getProperty("PARAM_GATE_SITE_CONFIG");
            this.GATE_USER_CONFIG=env.getProperty("PARAM_GATE_USER_CONFIG");
            this.GATE_SESSION_CONFIG=env.getProperty("PARAM_GATE_SESSION_CONFIG");
            this.GATE_GAPP_FILE=env.getProperty("PARAM_GATE_GAPP_FILE");

            this.SESAME_URL_REPOSITORY = env.getProperty("PARAM_SESAME_URL_REPOSITORY");
            this.SESAME_FILE_TO_IMPORT = env.getProperty("PARAM_SESAME_FILE_TO_IMPORT");
            this.GTFS_FILE_RDF_GENERATE = env.getProperty("PARAM_GTFS_FILE_RDF_GENERATE");

        } catch (NullPointerException | SQLException ne) {
            logger.warn("Attention: make sure all the parameter on the input.properties file are setted correctly");
            logger.error(ne.getMessage(), ne);
            System.exit(0);
        }
        //Set the onbjects so we not call them again after each url
        Docdao.setTableSelect(TABLE_KEYWORD_DOCUMENT);
        Docdao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_KEYWORD);
        geoDocumentDao.setTableInsert(TABLE_OUTPUT);
        geoDocumentDao.setTableSelect(TABLE_OUTPUT);
        geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
        //websiteDao.setTableSelect(TABLE_INPUT);
        //websiteDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_INPUT);

    }

    protected ExtractInfoSpring(SimpleParameters par) {
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

            if (PROCESS_PROGAMM == 3) {
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
            this.SILK_INTERLINK_ID = par.getValue("PARAM_SILK_INTERLINK_ID");
            this.SILK_NUM_THREADS = Integer.parseInt(par.getValue("PARAM_SILK_NUM_THREADS"));
            this.SILK_LOGQUERIES = Boolean.parseBoolean(par.getValue("PARAM_SILK_LOGQUERIES").toLowerCase());
            this.SILK_RELOAD = Boolean.parseBoolean(par.getValue("PARAM_SILK_RELOAD").toLowerCase());

            this.TABLE_REF_OFFLINE = par.getValue("PARAM_TABLE_REF_OFFLINE");
            this.COLUMN_OFFLINE_REF_URL = par.getValue("PARAM_COLUMN_OFFLINE_REF_URL");
            this.ERASE_OFFLINE = Boolean.parseBoolean(par.getValue("PARAM_ERASE_OFFLINE").toLowerCase());
            this.CREA_NUOVA_TABELLA_OFFLINE = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_OFFLINE").toLowerCase());

            if(CREA_NUOVA_TABELLA_OFFLINE){
                Connection conn = SQLUtilities.getMySqlConnection(HOST_DATABASE,PORT_DATABASE.toString(),USER,PASS);
                if(ERASE_OFFLINE){
                    SQLUtilities.executeSQL("DROP TABLE "+DB_OUTPUT+"."+TABLE_REF_OFFLINE,conn);
                }
                SQLUtilities.executeSQL(
                        "CREATE TABLE `"+TABLE_REF_OFFLINE+"` (\n" +
                                "  `id` int(10) DEFAULT NULL,\n" +
                                "  `url` varchar(1000) DEFAULT NULL\n" +
                                ")",conn);
                SQLUtilities.closeConnection();
                conn.close();
            }

            this.GATE_ANN_LIST = par.getValue("PARAM_GATE_ANN_LIST").split(",");
            this.GATE_ANNSET_LIST = par.getValue("PARAM_GATE_ANNSET_LIST").split(",");
            this.GATE_CORPUS_NAME = par.getValue("PARAM_GATE_CORPUS_NAME");

            this.GATE_HOME_PATH=par.getValue("PARAM_GATE_HOME_PATH");
            this.GATE_PLUGIN_PATH=par.getValue("PARAM_GATE_PLUGIN_PATH");
            this.GATE_SITE_CONFIG=par.getValue("PARAM_GATE_SITE_CONFIG");
            this.GATE_USER_CONFIG=par.getValue("PARAM_GATE_USER_CONFIG");
            this.GATE_SESSION_CONFIG=par.getValue("PARAM_GATE_SESSION_CONFIG");
            this.GATE_GAPP_FILE=par.getValue("PARAM_GATE_GAPP_FILE");

            this.SESAME_URL_REPOSITORY = par.getValue("PARAM_SESAME_URL_REPOSITORY");
            this.SESAME_FILE_TO_IMPORT = par.getValue("PARAM_SESAME_FILE_TO_IMPORT");
            this.GTFS_FILE_RDF_GENERATE = par.getValue("PARAM_GTFS_FILE_RDF_GENERATE");
        } catch (java.lang.NullPointerException|SQLException ne) {
            logger.warn("Attention: make sure all the parameter on the input.properties file are setted correctly");
            logger.error(ne.getMessage(), ne);
            System.exit(0);
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

    private List<URL> _listUrl = new ArrayList<>();
    private List<File> _listFile = new ArrayList<>();

    public static IGeoDocumentDao getGeoDocumentDao() {
        return geoDocumentDao;
    }

    public static void setGeoDocumentDao(IGeoDocumentDao geoDocumentDao) {
        ExtractInfoSpring.geoDocumentDao = geoDocumentDao;
    }

    /**
     * Method to extract company information by analizing a single url at the time.
     *
     * @param directoryFiles            the {@link String} path to the directory with the files to analyze.
     * @param driverDatabase            the {@link String} to the driver jdbc you using.
     * @param dialectDatabase           the {@link String} the jdbc dialect used for the connection.
     * @param hostDatabase              the {@link String} the address web of the host database.
     * @param portDatabase              the {@link String} the number of the port for communicate with the database-.
     * @param user                      the {@link String} user .
     * @param pass                      the {@link String} password.
     * @param dbOutput                  the {@link String} name of the database where is present the table for insert the data.
     * @param tableOutput               the {@link String} name of the  table for insert the data.
     * @param limit                     the {@link String} number of limit for the sql query.
     * @param offset                    the {@link String} number of offset for the sql query.
     * @param createNewGeodocumentTable the {@link Boolean} if true drop the old GeoDocument table with the same name.
     * @param erase                     the {@link Boolean} if true clear the data on the GeoDocument table with the same name.
     * @return the {@link List} of {@link} GeoDocument.
     */
    public List<GeoDocument> extractInfoFile(
            String directoryFiles, String driverDatabase, String dialectDatabase, String hostDatabase, String portDatabase, String user,
            String pass, String dbOutput, String tableOutput, String offset, String limit, boolean createNewGeodocumentTable, boolean erase) {

        List<GeoDocument> listGeo = new ArrayList<>();
        try {
            logger.info("RUN PROCESS 3: Abilitate for single file or for a directory");
            //String DIRECTORY_FILE = "C:\\Users\\Marco\\Downloads\\parseWebUrls";
            ExtractInfoWeb web = prepareListAndGATE(directoryFiles, driverDatabase, dialectDatabase, hostDatabase, portDatabase, user,
                    pass, dbOutput, tableOutput, offset, limit);
            if (_listFile.isEmpty()) {
                logger.warn("The list of files you get is empty!!");
            } else {
                logger.info("Loaded a list of: " + _listFile.size() + " files");
                web.ExtractGeoDocumentFromListFiles(
                        _listFile, tableOutput, tableOutput, createNewGeodocumentTable, erase);
            }
            logger.info("Obtained a list of: " + listGeo.size() + " GeoDocument");
        } catch (OutOfMemoryError e) {
            logger.error("java.lang.OutOfMemoryError, Reload the programm please");
        }
        return listGeo;
    }


    /**
     * Method to extract company information by analizing a block of urls at the time.
     *
     * @param driverDatabase            the {@link String} to the driver jdbc you using.
     * @param dialectDatabase           the {@link String} the jdbc dialect used for the connection.
     * @param hostDatabase              the {@link String} the address web of the host database.
     * @param portDatabase              the {@link String} the number of the port for communicate with the database-.
     * @param user                      the {@link String} user .
     * @param pass                      the {@link String} password.
     * @param dbOutput                  the {@link String} name of the database where is present the table for insert the data.
     * @param dbInput                   the {@link String} name of the database where is present the table for select the data.
     * @param tableOutput               the {@link String} name of the  table for insert the data.
     * @param tableInput                the {@link String} name of the table for select the data.
     * @param columnTableInput          the {@link String} name of the field on table for select the data where ares stored the urls to analyze.
     * @param limit                     the {@link String} number of limit for the sql query.
     * @param offset                    the {@link String} number of offset for the sql query.
     * @param createNewGeodocumentTable the {@link Boolean} if true drop the old GeoDocument table with the same name.
     * @param erase                     the {@link Boolean} if true clear the data on the GeoDocument table with the same name.
     * @return the {@link List} of {@link} GeoDocument.
     */
    public List<GeoDocument> extractInfoMultipleURL(
            String driverDatabase, String dialectDatabase, String hostDatabase, String portDatabase, String user,
            String pass, String dbOutput, String dbInput, String tableOutput, String tableInput,
            String columnTableInput,String offset, String limit, boolean createNewGeodocumentTable, boolean erase) {
        List<GeoDocument> listGeo = new ArrayList<>();
        try {
            ExtractInfoWeb web = prepareListAndGATE(
                    driverDatabase, dialectDatabase, hostDatabase, portDatabase, user,
                    pass, dbOutput, dbInput, tableOutput, tableInput,
                    columnTableInput, limit, offset);
            logger.info("RUN PROCESS 2: Abilitate for multiple url");
            if (_listUrl.isEmpty()) {
                logger.info("The list of urls you get from the table:" + tableInput +
                        " from the columns " + columnTableInput + " empty!!!");
            } else {
                logger.info("Loaded a list of: " + _listUrl.size() + " files");
                listGeo = web.ExtractGeoDocumentFromListUrls(
                        _listUrl, tableInput, tableOutput, createNewGeodocumentTable, erase);
            }
        } catch (OutOfMemoryError e) {
            logger.error("java.lang.OutOfMemoryError, Reload the programm please");
        }
        return listGeo;
    }


    private ExtractInfoWeb prepareListAndGATE(
            String directoryFiles, String driverDatabase, String dialectDatabase, String hostDatabase, String portDatabase, String user,
            String pass, String dbOutput, String tableOutput, String offset, String limit) {

        IGeoDocumentDao geoDocumentDao2 = new GeoDocumentDaoImpl();
        geoDocumentDao2.setTableInsert(tableOutput);
        geoDocumentDao2.setTableSelect(tableOutput);
        geoDocumentDao2.setDriverManager(driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput);
        setGeoDocumentDao(geoDocumentDao2);

        ExtractInfoWeb web;
        //Check out if the user setted manually support table offline and manually setted the gate annotations
        if(StringUtilities.isCollectionStringsNullOrEmpty(TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME) &&
                GATE_ANN_LIST.length > 0 && GATE_ANNSET_LIST.length > 0){
            web = ExtractInfoWeb.getNewInstance(
                    driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput,
                    TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME,GATE_ANN_LIST,GATE_ANNSET_LIST);
        }else {
            web = ExtractInfoWeb.getNewInstance(
                    driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput);
        }

        Integer iOffset = StringUtilities.toInteger(offset);
        Integer iLimit = StringUtilities.toInteger(limit);

        List<File> files = new ArrayList<>();
        //code for make some verify before go to the extraction...
        if(!directoryFiles.endsWith(File.separator))directoryFiles = directoryFiles + File.separator;
        if (FileUtilities.isDirectoryExists(new File(directoryFiles))) {
            //We use the index of the list like  bookmark for all the files in the directory...
            files = FileUtilities.getFilesFromDirectory(
                     directoryFiles,StringUtilities.toInteger(offset),StringUtilities.toInteger(limit));
        } else {
            //subFiles.add(new File(directoryFiles));
            files.add(new File(directoryFiles));
        }
        //avoid already present on the database url of the file...
        for (File file : files) {
            String urlFile;
            //If you have a table of inpout with a reference to a address web for the file.
            /*String urlFile =(String) websiteDao.select("url", "file_path", file.getName());
            if(!(urlFile == null || (geoDocumentDao.verifyDuplicate("url", urlFile))
            )){
                files.add(file);
                mapFile.put(file,urlFile);
            }*/

            urlFile = FileUtilities.toURL(file).toString();
            if(urlFile == null)urlFile = "";

            if (urlFile.isEmpty() || geoDocumentDao2.verifyDuplicate(COLUMN_TABLE_INPUT, urlFile)) {
                files.remove(file);
                //mapFile.put(file,urlFile);
            }
        }
        _listFile.addAll(files);
        files.clear();
        //Initialize GATE...
        if(StringUtilities.isNullOrEmpty(GATE_SESSION_CONFIG)) {
            web.setGate("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
                    "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
        }else{
            web.setGate(GATE_HOME_PATH, GATE_PLUGIN_PATH, GATE_SITE_CONFIG, GATE_USER_CONFIG, GATE_SESSION_CONFIG,
                    GATE_GAPP_FILE);
        }
        return web;
    }

    private ExtractInfoWeb prepareListAndGATE(
            String driverDatabase, String dialectDatabase, String hostDatabase, String portDatabase, String user,
            String pass, String dbOutput, String dbInput, String tableOutput, String tableInput,
            String columnTableInput, String limit, String offset) {

        IGeoDocumentDao geoDocumentDao2 = new GeoDocumentDaoImpl();
        geoDocumentDao2.setTableInsert(tableOutput);
        geoDocumentDao2.setTableSelect(tableOutput);
        geoDocumentDao2.setDriverManager(driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput);

        setGeoDocumentDao(geoDocumentDao2);
        ExtractInfoWeb web;
        //Check out if the user setted manually support table offline and manually setted the gate annotations
        if(StringUtilities.isCollectionStringsNullOrEmpty(TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME) &&
                GATE_ANN_LIST.length > 0 && GATE_ANNSET_LIST.length > 0){
            web = ExtractInfoWeb.getNewInstance(
                    driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput,
                    TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME,GATE_ANN_LIST,GATE_ANNSET_LIST);
        }else {
            web = ExtractInfoWeb.getNewInstance(
                    driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput);
        }

        logger.info("Run the extraction method.");
        _listUrl =  web.getListURLFromDatabase(
                driverDatabase, dialectDatabase, hostDatabase,
                portDatabase, user, pass, dbInput, tableInput, columnTableInput,
                StringUtilities.toInteger(limit), StringUtilities.toInteger(offset));

        //Filter and remove all unreachable or errate address web...
        geoDocumentDao2.setTableInsert(TABLE_REF_OFFLINE);
        List<URL> supportList = new ArrayList<>();
        for (URL url : _listUrl) {
            if (geoDocumentDao2.verifyDuplicate(COLUMN_TABLE_INPUT, url.toString())) {
                supportList.add(url);
            }
        }
        for (URL aSupportList : supportList) {
            logger.warn("The site " + aSupportList + " can't be reach...");
            for (int j = 0; j < _listUrl.size(); j++) {
                if (_listUrl.get(j) == aSupportList) {
                    _listUrl.remove(j);
                    break;
                }
            }
        }
        geoDocumentDao2.setTableInsert(tableInput);
        supportList.clear();
        //Initialize GATE...
        if(StringUtilities.isNullOrEmpty(GATE_SESSION_CONFIG)) {
            web.setGate("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
                    "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
        }else{
            web.setGate(GATE_HOME_PATH, GATE_PLUGIN_PATH, GATE_SITE_CONFIG, GATE_USER_CONFIG, GATE_SESSION_CONFIG,
                    GATE_GAPP_FILE);
        }
        return web;
    }

    /**
     * Method to extract company information by analizing a single url at the time.
     *
     * @param driverDatabase            the {@link String} to the driver jdbc you using.
     * @param dialectDatabase           the {@link String} the jdbc dialect used for the connection.
     * @param hostDatabase              the {@link String} the address web of the host database.
     * @param portDatabase              the {@link String} the number of the port for communicate with the database-.
     * @param user                      the {@link String} user .
     * @param pass                      the {@link String} password.
     * @param dbOutput                  the {@link String} name of the database where is present the table for insert the data.
     * @param dbInput                   the {@link String} name of the database where is present the table for select the data.
     * @param tableOutput               the {@link String} name of the  table for insert the data.
     * @param tableInput                the {@link String} name of the table for select the data.
     * @param columnTableInput          the {@link String} name of the field on table for select the data where ares stored the urls to analyze.
     * @param limit                     the {@link String} number of limit for the sql query.
     * @param offset                    the {@link String} number of offset for the sql query.
     * @param createNewGeodocumentTable the {@link Boolean} if true drop the old GeoDocument table with the same name.
     * @param erase                     the {@link Boolean} if true clear the data on the GeoDocument table with the same name.
     * @return the {@link List} of {@link} GeoDocument.
     */
    public List<GeoDocument> extractInfoSingleURL(
            String driverDatabase, String dialectDatabase, String hostDatabase, String portDatabase, String user,
            String pass, String dbOutput, String dbInput, String tableOutput, String tableInput,
            String columnTableInput,String offset, String limit, boolean createNewGeodocumentTable, boolean erase) {

        List<GeoDocument> listGeo = new ArrayList<>();
        ExtractInfoWeb web = prepareListAndGATE(
                driverDatabase, dialectDatabase, hostDatabase, portDatabase, user,
                pass, dbOutput, dbInput, tableOutput, tableInput,
                columnTableInput, limit, offset);
        try {
            logger.info("RUN PROCESS 1: Abilitate for each single url");
            if (_listUrl.isEmpty()) {
                logger.info("The list of urls you get from the table:" + tableInput +
                        " from the columns " + columnTableInput + " empty!!!");
            } else {
                logger.info("Loaded a list of: " + _listUrl.size() + " files");
                GeoDocument geoDoc;
                for (URL url : _listUrl) {
                    geoDoc = web.ExtractGeoDocumentFromUrl(
                            url, tableOutput, tableOutput, createNewGeodocumentTable, erase);
                    if (geoDoc != null) listGeo.add(geoDoc);
                }
            }
        } catch (OutOfMemoryError e) {
            logger.error("java.lang.OutOfMemoryError, Reload the programm please");
        }
        return listGeo;
    }

    /**
     * Method to convert a SQL Table to a File of triple.
     *
     * @param driverDatabase                       the {@link String} to the driver jdbc you using.
     * @param dialectDatabase                      the {@link String} the jdbc dialect used for the connection.
     * @param hostDatabase                         the {@link String} the address web of the host database.
     * @param portDatabase                         the {@link String} the number of the port for communicate with the database-.
     * @param user                                 the {@link String} user .
     * @param pass                                 the {@link String} password.
     * @param dbOutput                             the {@link String} name of the database where is present the table for insert the data.
     * @param tableInputOntology                   the {@link String} name of the table for select the data.
     * @param tableOutputOntology                  the {@link String} name of the table for insert the data.
     * @param outputFormatKarma                    the {@link String}  output RDFFormat
     * @param fileMapTurtleKarma                   the {@link String}   path to the file model of Web-Karma .
     * @param fileOutputTripleKarma                the {@link String}  path to the file of triple generate.
     * @param creaNuovaTabellaInfodocumentOntology the {@link Boolean} if true drop the old InfoDocument table with the same name.
     * @param eraseOntology                        the {@link Boolean} if true clear the data on the InfoDocument table with the same name.
     * @return the {@link Boolean} is true if all the operation are done.
     */
    public Boolean convertTableToTriple(String driverDatabase, String dialectDatabase,
                                     String hostDatabase, String portDatabase, String user, String pass, String dbOutput,
                                     String tableInputOntology, String tableOutputOntology,
                                     String outputFormatKarma, String fileMapTurtleKarma, String fileOutputTripleKarma,
                                     boolean creaNuovaTabellaInfodocumentOntology, boolean eraseOntology) {
        logger.info("RUN ONTOLOGY PROGRAMM: Create Table of infodocument from a geodocument/geodomaindocument table!");
        logger.info("RUN KARMA PROGRAMM: Generation of triple with Web-karma!!");

        ExtractInfoWeb web;
        //Check out if the user setted manually support table offline and manually setted the gate annotations
        if(StringUtilities.isCollectionStringsNullOrEmpty(TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME) &&
                GATE_ANN_LIST.length > 0 && GATE_ANNSET_LIST.length > 0){
            web = ExtractInfoWeb.getNewInstance(
                    driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput,
                    TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME,GATE_ANN_LIST,GATE_ANNSET_LIST);
        }else {
            web = ExtractInfoWeb.getNewInstance(
                    driverDatabase, dialectDatabase, hostDatabase, portDatabase, user, pass, dbOutput);
        }

        return web.triplifyGeoDocumentFromDatabase(
                tableInputOntology, tableOutputOntology,
                outputFormatKarma, fileMapTurtleKarma, fileOutputTripleKarma,
                creaNuovaTabellaInfodocumentOntology, eraseOntology);
    }

    /**
     * Method to convert a SQL Table to a File of triple.
     *
     * @param driverDatabase            the {@link String} to the driver jdbc you using.
     * @param dialectDatabase           the {@link String} the jdbc dialect used for the connection.
     * @param hostDatabase              the {@link String} the address web of the host database.
     * @param portDatabase              the {@link String} the number of the port for communicate with the database-.
     * @param user                      the {@link String} user .
     * @param pass                      the {@link String} password.
     * @param dbOutput                  the {@link String} name of the database where is present the table for insert the data.
     * @param tableOutput               the {@link String} name of the table for insert the data.
     * @param tableInput                the {@link String} name of the table for select the data.
     * @param creaNuovaTabellaGeodomain the {@link Boolean} if true drop the old InfoDocument table with the same name.
     * @param erase                     the {@link Boolean} if true clear the data on the InfoDocument table with the same name.
     * @param limit                     the {@link Integer} offset index.
     * @param offset               the {@link Integer} limit index.
     * @param frequenzaUrl         the {@link Integer} range for check the common field.
     * @return                     the {@link Boolean} is true if all operations are done.
     */
    public Boolean createGeoDomainTable(String driverDatabase, String dialectDatabase, String hostDatabase,
                                     String portDatabase, String user, String pass, String dbOutput,
                                     String tableOutput, String tableInput,
                                     boolean creaNuovaTabellaGeodomain, boolean erase,
                                     Integer limit, Integer offset, Integer frequenzaUrl) {
        logger.info("RUN GEODOMAIN PROGRAMM: Create a geodomaindocument table from a geodocument table!");
        try{
        IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
        geoDomainDocumentDao.setTableInsert(tableOutput);
        geoDomainDocumentDao.setTableSelect(tableInput);
        geoDomainDocumentDao.setDriverManager(
                driverDatabase, dialectDatabase, hostDatabase, portDatabase,
                user, pass, dbOutput);
        if (creaNuovaTabellaGeodomain) {
            geoDomainDocumentDao.create(erase);
        }
        ExtractorDomain egd = new ExtractorDomain(
                (GeoDomainDocumentDaoImpl) geoDomainDocumentDao,
                limit, offset, frequenzaUrl);
        egd.CreateTableOfGeoDomainDocument("sql");
        return true;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return false;
        }
    }



    /**
     * Run all the functions of the project by use a properties file.
     */
    public void Extraction() {
        //logger.info(env.toString());
        try {
            ExtractInfoWeb web;
            //Check out if the user setted manually support table offline and manually setted the gate annotations
            if(StringUtilities.isCollectionStringsNullOrEmpty(TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME) &&
                    GATE_ANN_LIST.length > 0 && GATE_ANNSET_LIST.length > 0){
                web = ExtractInfoWeb.getNewInstance(
                        DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT,
                        TABLE_REF_OFFLINE,COLUMN_OFFLINE_REF_URL,GATE_CORPUS_NAME,GATE_ANN_LIST,GATE_ANNSET_LIST);
            }else {
                web = ExtractInfoWeb.getNewInstance(
                        DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
            }
            //OLd method
          /*  ExtractInfoWeb web = ExtractInfoWeb.getInstance(
                    DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);*/
            if (PROCESS_PROGAMM < 4) {
                logger.info("Run the extraction method.");
                List<URL> listUrl = ExtractInfoWeb.getInstance().getListURLFromDatabase(
                        DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE,
                        PORT_DATABASE.toString(), USER, PASS, DB_INPUT, TABLE_INPUT, COLUMN_TABLE_INPUT, LIMIT, OFFSET);

                //Filter and remove all unreachable or errate address web...
                geoDocumentDao.setTableInsert(TABLE_REF_OFFLINE);
                List<URL> supportList = new ArrayList<>();
                for (URL url : listUrl) {
                    if (geoDocumentDao.verifyDuplicate(COLUMN_TABLE_INPUT, url.toString())) {
                        supportList.add(url);
                    }
                }
                for (URL aSupportList : supportList) {
                    logger.warn("The site " + aSupportList + " can't be reach...");
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
                if(StringUtilities.isNullOrEmpty(GATE_SESSION_CONFIG)) {
                    web.setGate("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session",
                            "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
                }else{
                    web.setGate(GATE_HOME_PATH, GATE_PLUGIN_PATH, GATE_SITE_CONFIG, GATE_USER_CONFIG, GATE_SESSION_CONFIG,
                            GATE_GAPP_FILE);
                }
                if (null != PROCESS_PROGAMM) switch (PROCESS_PROGAMM) {
                    case 1:
                        logger.info("RUN PROCESS 1: Abilitate for each single url");
                        if (listUrl.isEmpty()) {
                            logger.info("The list of urls you get from the table:" + TABLE_INPUT +
                                    " from the columns " + COLUMN_TABLE_INPUT + " empty!!!");
                        } else {
                            logger.info("Loaded a list of: " + listUrl.size() + " files");
                            GeoDocument geoDoc;
                            for (URL url : listUrl) {
                                geoDoc = web.ExtractGeoDocumentFromUrl(
                                        url, TABLE_OUTPUT, TABLE_OUTPUT, CREATE_NEW_GEODOCUMENT_TABLE, ERASE);
                                if (geoDoc != null) listGeo.add(geoDoc);
                            }
                        }   break;
                    case 2:
                        logger.info("RUN PROCESS 2: Abilitate for multiple url");
                        if (listUrl.isEmpty()) {
                            logger.info("The list of urls you get from the table:" + TABLE_INPUT +
                                    " from the columns " + COLUMN_TABLE_INPUT + " empty!!!");
                        } else {
                            logger.info("Loaded a list of: " + listUrl.size() + " files");
                            web.ExtractGeoDocumentFromListUrls(
                                    listUrl, TABLE_INPUT, TABLE_OUTPUT, CREATE_NEW_GEODOCUMENT_TABLE, ERASE);
                        }   break;
                //else lista url non vuota
                    case 3:
                        logger.info("RUN PROCESS 3: Abilitate for single file or for a directory");
                        //String DIRECTORY_FILE = "C:\\Users\\Marco\\Downloads\\parseWebUrls";
                        List<File> files = new ArrayList<>();
                        List<File> subFiles = new ArrayList<>(); //suppport list...
                        Map<File, String> mapFile = new HashMap<>();
                        //code for make some verify before go to the extraction...
                        if (FileUtilities.isDirectoryExists(new File(DIRECTORY_FILES))) {
                            files = FileUtilities.getFilesFromDirectory(DIRECTORY_FILES);
                            //We use the index of the list like  bookmark for all the files in the directory...
                            if (OFFSET + LIMIT < files.size()) subFiles.addAll(files.subList(OFFSET, OFFSET + LIMIT));
                            else subFiles.addAll(files.subList(OFFSET, files.size()));
                            files.clear();
                        } else {
                            subFiles.add(new File(DIRECTORY_FILES));
                        }   //avoid already present on the database url of the file...
                        for (File file : subFiles) {
                            String urlFile = (String) websiteDao.select(COLUMN_TABLE_INPUT, "file_path", file.getName());
                            if (!(urlFile == null || (geoDocumentDao.verifyDuplicate(COLUMN_TABLE_INPUT, urlFile))
                                    )) {
                                files.add(file);
                                mapFile.put(file, urlFile);
                            }
                        }   subFiles.clear();
                        subFiles.addAll(files);
                        files.clear();
                        if (subFiles.isEmpty()) {
                            logger.info("The list of urls you get from the table:" + TABLE_INPUT +
                                    " from the columns " + COLUMN_TABLE_INPUT + " empty!!!");
                        } else {
                            logger.info("Loaded a list of: " + subFiles.size() + " files");
                            web.ExtractGeoDocumentFromListFiles(
                                    subFiles, TABLE_INPUT, TABLE_OUTPUT, CREATE_NEW_GEODOCUMENT_TABLE, ERASE);
                        }   logger.info("Obtained a list of: " + listGeo.size() + " GeoDocument");
                        break;
                    default:
                        logger.warn("You are jump the extraction process! (use 1,2 or 3 on the Proces Porgamma Parameter)");
                        break;
                }
            }
            //Other process after the extraction of the information....
            if (PROCESS_PROGAMM == 4) {
                logger.info("RUN PROCESS 4");
                //CREAZIONE DI UNA TABELLA DI GEODOMAINDOCUMENT
                if (GEODOMAIN_PROGRAMM) {
                    logger.info("RUN GEODOMAIN PROGRAMM: Create a geodomaindocument table from a geodocument table!");
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
                    logger.info("RUN ONTOLOGY PROGRAMM: Create Table of infodocument from a geodocument/geodomaindocument table!");
                    logger.info("RUN KARMA PROGRAMM: Generation of triple with Web-karma!!");
                    web.triplifyGeoDocumentFromDatabase(
                            TABLE_INPUT_ONTOLOGY, TABLE_OUTPUT_ONTOLOGY,
                            OUTPUT_FORMAT_KARMA, FILE_MAP_TURTLE_KARMA, FILE_OUTPUT_TRIPLE_KARMA,
                            CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY, ERASE_ONTOLOGY);
                }
                if (SILK_LINKING_TRIPLE_PROGRAMM) {
                    if (new File(SILK_SLS_FILE).exists()) {
                        try {
                               /* de.fuberlin.wiwiss.silk.Silk.executeFile(
                                    new File(SILK_SLS_FILE), SILK_INTERLINK_ID, SILK_NUM_THREADS, SILK_LOGQUERIES);*/
                            SilkUtilities.getNewInstance().generateRDF(
                                    new File(SILK_SLS_FILE), SILK_INTERLINK_ID, SILK_NUM_THREADS, SILK_LOGQUERIES);
                        }catch(Exception e){
                            logger.error(e.getMessage(),e);
                        }
                    } else {
                        logger.error("The " + new File(SILK_SLS_FILE).getAbsolutePath() + " not exists!!");
                    }
                }
            }
            if(PROCESS_PROGAMM == 5){
                logger.info("RUN PROCESS PROGRAMM 5: UPDATE COORDINATES ON GEODOMAIN TABLE");
                IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
                geoDomainDocumentDao.setTableInsert(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableSelect(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableUpdate(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT_GEODOMAIN);
                if (CREA_NUOVA_TABELLA_GEODOMAIN) {
                    geoDomainDocumentDao.create(ERASE_GEODOMAIN);
                }
                ExtractorDomain egd = new ExtractorDomain((GeoDomainDocumentDaoImpl) geoDomainDocumentDao,
                        LIMIT_GEODOMAIN, OFFSET_GEODOMAIN, FREQUENZA_URL_GEODOMAIN);
                egd.reloadNullCoordinates();
            }
            if(PROCESS_PROGAMM == 6){
                logger.info("RUN PROCESS PROGRAMM 6:"+
                "DELETE OVERRRIDE RECORD GEODOMAINDOCUMENT TABLE WITH SIIMOBILITY COORDINATES");
                IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
                geoDomainDocumentDao.setTableInsert(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableSelect(TABLE_INPUT_GEODOMAIN);
                geoDomainDocumentDao.setTableUpdate(TABLE_OUTPUT_GEODOMAIN);
                geoDomainDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT_GEODOMAIN);
                if (CREA_NUOVA_TABELLA_GEODOMAIN) {
                    geoDomainDocumentDao.create(ERASE_GEODOMAIN);
                }
                ExtractorDomain egd = new ExtractorDomain((GeoDomainDocumentDaoImpl) geoDomainDocumentDao, LIMIT_GEODOMAIN, OFFSET_GEODOMAIN, FREQUENZA_URL_GEODOMAIN);
                List<Object> listUrlGM = geoDomainDocumentDao.select(COLUMN_TABLE_INPUT, 500, 0,String.class);//url
                Map<String,String> map = new HashMap<>();
                for(Object url: listUrlGM){
                    String ids = (String)geoDomainDocumentDao.select("doc_id", COLUMN_TABLE_INPUT, url);//url
                    map.put(ids,String.valueOf(url));
                }
                egd.deleteOverrideRecord(map,TABLE_INPUT_GEODOMAIN,COLUMN_TABLE_INPUT,TABLE_OUTPUT_GEODOMAIN);
            }
            if(PROCESS_PROGAMM == 7){
                logger.info("RUN PROCESS PROGRAMM 7: Import data to Sesame Repository");
                //Repository repo = Sesame2Utilities.getInstance().connectToHTTPRepository(SESAME_URL_REPOSITORY);
                Sesame2Utilities sesame = Sesame2Utilities.getInstance();
                Repository rep;
                try {
                    String repoID = HttpUtilities.getLastBitFromUrl(SESAME_URL_REPOSITORY);
                    rep = sesame.connectToHTTPRepository(SESAME_URL_REPOSITORY);
                }catch(Exception e){
                    String repoID = HttpUtilities.getLastBitFromUrl(SESAME_URL_REPOSITORY);
                    rep = sesame.connectToHTTPRepositoryWithDefaultServer(repoID);
                }
                //Repository rep = sesame.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");
                //Repository rep = sesame.connectToHTTPRepositoryWithDefaultServer(repositoryID);
                sesame.setPrefixes(rep);
                try{
                    sesame.importIntoRepositoryFileChunked(new File(SESAME_FILE_TO_IMPORT),rep);
                }catch(Exception e){
                    try{
                        sesame.importIntoRepository(new File(SESAME_FILE_TO_IMPORT), rep);
                    }catch(Exception e2){
                        logger.error(e2.getMessage(),e2);
                    }
                }
            }
            if(PROCESS_PROGAMM == 8){
                logger.info("RUN PROCESS PROGRAMM 8: Convert a GTFS database to the RDF Triple.");
                Connection conn =
                        SQLUtilities.getMySqlConnection(
                                HOST_DATABASE,PORT_DATABASE.toString(),DB_INPUT,USER,PASS,false,false,false);
                File file = new File(GTFS_FILE_RDF_GENERATE);
                GTFSUtilities.getInstance().exportGTFSDatabaseToRDF(conn,file);
            }
        } catch (OutOfMemoryError e) {
            logger.error("java.lang.OutOfMemoryError, Reload the programm please");
        }
    }//main
}