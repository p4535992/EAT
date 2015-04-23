package extractor.estrattori;

import extractor.ManageJsonWithGoogleMaps;
import extractor.cmd.SimpleParameters;
import extractor.gate.GateKit;
import extractor.http.HttpUtil;
import extractor.setInfoParameterIta.SetNazioneELanguage;
import extractor.setInfoParameterIta.SetProvinciaECity;
import extractor.setInfoParameterIta.SetRegioneEProvincia;
import gate.CorpusController;
import object.dao.*;
import object.impl.*;
import object.model.GeoDocument;
import extractor.FileUtil;
import extractor.SystemLog;
import extractor.gate.GateDataStoreKit;
import extractor.karma.GenerationOfTriple;
import extractor.setInfoParameterIta.SetCodicePostale;
import util.string.StringKit;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * MainEstrazioneInfoDocument.java
 * Classe con la quale da una lista di url realizziamo una lista di GeoDocument
 * da inserire come InfoDocument nel database mySQL
 * @author 4535992
 */
public class ExtractInfoSpring {

     private boolean DATASTORE = false;
     private String TYPE_EXTRACTION;
     private Integer PROCESS_PROGAMM,indGDoc ;
     private boolean CreaNuovaTabellaGeoDocumenti,ERASE;
     private Integer LIMIT,OFFSET;
     private boolean ONTOLOGY_PROGRAMM,GENERATION_TRIPLE_KARMA_PROGRAMM,GEODOMAIN_PROGRAMM,FILTER;
     private String API_KEY_GM,USER,PASS,DB_INPUT, DB_OUTPUT, TABLE_INPUT,TABLE_OUTPUT,COLUMN_TABLE_INPUT,
             TABLE_KEYWORD_DOCUMENT,DB_KEYWORD,DRIVER_DATABASE,DIALECT_DATABASE,HOST_DATABASE;
     private Integer PORT_DATABASE;
     private String NOME_DATASTORE,DS_DIR;
     private String TABLE_OUTPUT_ONTOLOGY;
     private boolean CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY,ERASE_ONTOLOGY;

     private String TYPE_DATABASE_KARMA,FILE_MAP_TURTLE_KARMA,FILE_OUTPUT_TRIPLE_KARMA,ID_DATABASE_KARMA,
             TABLE_INPUT_KARMA,OUTPUT_FORMAT_KARMA,KARMA_HOME;

     private Integer LIMIT_GEODOMAIN, OFFSET_GEODOMAIN,FREQUENZA_URL_GEODOMAIN;
     private String TABLE_INPUT_GEODOMAIN,TABLE_OUTPUT_GEODOMAIN,DB_INPUT_GEODOMAIN,DB_OUTPUT_GEODOMAIN;
     private boolean CREA_NUOVA_TABELLA_GEODOMAIN,ERASE_GEODOMAIN;

     //COSTRUTTORI
     private GenerationOfTriple got = new GenerationOfTriple();
     private ManageJsonWithGoogleMaps j = new ManageJsonWithGoogleMaps();
     private EstrazioneGeoDomainDocument egd = new EstrazioneGeoDomainDocument();
     private ExtractorInfoGATE egate = new ExtractorInfoGATE();

    //OBJECTS
    //private static IGenericDao gDao;
    private static CorpusController controller;
    IDocumentDao Docdao = new DocumentDaoImpl();
    IWebsiteDao websiteDao = new WebsiteDaoImpl();
    public static IGeoDocumentDao getGeoDocumentDao() {
        return geoDocumentDao;
    }

    public static IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();

     public ExtractInfoSpring(SimpleParameters par){

            this.TYPE_EXTRACTION = par.getValue("PARAM_TYPE_EXTRACTION");
            this.PROCESS_PROGAMM = Integer.parseInt(par.getValue("PARAM_PROCESS_PROGAMM"));

            this.CreaNuovaTabellaGeoDocumenti = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_GEODOCUMENT").toLowerCase());
            this.ERASE = Boolean.parseBoolean(par.getValue("PARAM_ERASE").toLowerCase());
            this.LIMIT = Integer.parseInt(par.getValue("PARAM_LIMIT"));
            this.OFFSET = Integer.parseInt(par.getValue("PARAM_OFFSET"));

            this.ONTOLOGY_PROGRAMM = Boolean.parseBoolean(par.getValue("PARAM_ONTOLOGY_PROGRAMM").toLowerCase());
            this.GENERATION_TRIPLE_KARMA_PROGRAMM = Boolean.parseBoolean(par.getValue("PARAM_GENERATION_TRIPLE_KARMA_PROGRAMM").toLowerCase());
            this.GEODOMAIN_PROGRAMM = Boolean.parseBoolean(par.getValue("PARAM_GEODOMAIN_PROGRAMM").toLowerCase());

            this.FILTER = Boolean.parseBoolean(par.getValue("PARAM_FILTER").toLowerCase());
            this.API_KEY_GM = par.getValue("PARAM_API_KEY_GM");

            this.USER = par.getValue("PARAM_USER");
            this.PASS = par.getValue("PARAM_PASS");
            this.DB_INPUT = par.getValue("PARAM_DB_INPUT");
            this.DB_OUTPUT = par.getValue("PARAM_DB_OUTPUT");
            this.TABLE_INPUT = par.getValue("PARAM_TABLE_INPUT");
            this.TABLE_OUTPUT = par.getValue("PARAM_TABLE_OUTPUT");
            this.COLUMN_TABLE_INPUT = par.getValue("PARAM_COLUMN_TABLE_INPUT");

            this.TABLE_KEYWORD_DOCUMENT =par.getValue("PARAM_TABLE_KEYWORD_DOCUMENT");
            this.DB_KEYWORD =par.getValue("PARAM_DB_KEYWORD");

            this.DRIVER_DATABASE = par.getValue("PARAM_DRIVER_DATABASE");
            this.DIALECT_DATABASE = par.getValue("PARAM_DIALECT_DATABASE");
            this.HOST_DATABASE = par.getValue("PARAM_HOST_DATABASE");
            this.PORT_DATABASE = Integer.parseInt(par.getValue("PARAM_PORT_DATABASE"));

            if(DATASTORE){
                this.NOME_DATASTORE = par.getValue("PARAM_NOME_DATASTORE");
                this.DS_DIR = par.getValue("PARAM_DS_DIR");
                //this.RANGE = Integer.parseInt(par.getValue("PARAM_RANGE"));
                //this.tentativiOutMemory = Integer.parseInt(par.getValue("PARAM_TENTATIVI_OUT_OF_MEMORY"));
                GateDataStoreKit.setDataStore(DS_DIR,NOME_DATASTORE);
            }

            if(GENERATION_TRIPLE_KARMA_PROGRAMM) {
                this.TYPE_DATABASE_KARMA = par.getValue("PARAM_TYPE_DATABASE_KARMA");//MySQL
                this.FILE_MAP_TURTLE_KARMA = par.getValue("PARAM_FILE_MAP_TURTLE_KARMA"); //PATH: karma_files/model/
                this.FILE_OUTPUT_TRIPLE_KARMA = par.getValue("PARAM_FILE_OUTPUT_TRIPLE_KARMA");//PATH: karma_files/output/
                this.ID_DATABASE_KARMA = par.getValue("PARAM_ID_DATABASE_KARMA");//DB
                this.TABLE_INPUT_KARMA = par.getValue("PARAM_TABLE_INPUT_KARMA");
                this.OUTPUT_FORMAT_KARMA = par.getValue("PARAM_OUTPUT_FORMAT_KARMA");
                this.KARMA_HOME = par.getValue("PARAM_KARMA_HOME");
            }

            if(ONTOLOGY_PROGRAMM){
                this.CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY").toLowerCase());
                this.ERASE_ONTOLOGY = Boolean.parseBoolean(par.getValue("PARAM_ERASE_ONTOLOGY").toLowerCase());
                this.TABLE_OUTPUT_ONTOLOGY = par.getValue("PARAM_TABLE_OUTPUT_ONTOLOGY");
            }

            if(GEODOMAIN_PROGRAMM){
                this.LIMIT_GEODOMAIN = Integer.parseInt(par.getValue("PARAM_LIMIT_GEODOMAIN"));
                this.OFFSET_GEODOMAIN = Integer.parseInt(par.getValue("PARAM_OFFSET_GEODOMAIN"));
                this.FREQUENZA_URL_GEODOMAIN = Integer.parseInt(par.getValue("PARAM_FREQUENZA_URL_GEODOMAIN"));
                this.TABLE_INPUT_GEODOMAIN = par.getValue("PARAM_TABLE_INPUT_GEODOMAIN");
                this.TABLE_OUTPUT_GEODOMAIN = par.getValue("PARAM_TABLE_OUTPUT_GEODOMAIN");
                this.DB_INPUT_GEODOMAIN = par.getValue("PARAM_DB_OUTPUT");
                this.DB_OUTPUT_GEODOMAIN = par.getValue("PARAM_DB_OUTPUT");
                this.CREA_NUOVA_TABELLA_GEODOMAIN = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_GEODOMAIN").toLowerCase());
                this.ERASE_GEODOMAIN = Boolean.parseBoolean(par.getValue("PARAM_ERASE_GEODOMAIN").toLowerCase());
            }

            j = new ManageJsonWithGoogleMaps(API_KEY_GM);

            //Set the onbjects so we not call them again after each url
             Docdao.setTableSelect(TABLE_KEYWORD_DOCUMENT);
             Docdao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_KEYWORD);
             geoDocumentDao.setTableInsert(TABLE_OUTPUT);
             geoDocumentDao.setTableSelect(TABLE_OUTPUT);
             geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
             websiteDao.setTableSelect(TABLE_INPUT);
             websiteDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_INPUT);

         par.getParameters().clear();
     }


    /**
     * Metodo Main del programma per la creazione di InfoDocument
     */
    public void Extraction() throws Exception {
        try{
             SystemLog.ticket("Run the extraction method.", "OUT");
             List<URL> listUrl = new ArrayList<>();
             indGDoc = 0;
             List<GeoDocument> listGeo = new ArrayList<>();
             if(PROCESS_PROGAMM != 4){
                 if(CreaNuovaTabellaGeoDocumenti ==true){
                     geoDocumentDao.create();
                 }
                 //SET GATE FOR THE PROGRAMM
                 GateKit.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session");
                 GateKit.loadGapp("custom/gapp", "geoLocationPipeline06102014v7_fastMode.xgapp");
                 controller = GateKit.getController();
                 //QUIT THE PROGRAMM SE LA LISTA DEGLI URL E' VUOTA
                 GeoDocument geo3 = new GeoDocument(null,null,null, null, null, null, null,null, null, null,null,null,null,null,null,null,null);
                 GeoDocument geo2 = new GeoDocument(null, null, null, null, null, null, null, null, null, null, null, null,null,null,null,null,null);
                 if(PROCESS_PROGAMM == 1){
                     try{
                         SystemLog.message("RUN PROCESS 1: METODOLOGIA PER SINGOLO URL");
                         listUrl = websiteDao.selectAllUrl(COLUMN_TABLE_INPUT, LIMIT, OFFSET);
                         SystemLog.message("Loaded list of URL's with " + listUrl.size() + " elements");
                         if(listUrl.isEmpty()){SystemLog.abort(0,"Forced Exit from the programm hte list of url was empty!");}
                         //METODOLOGIE PER SINGOLO URL
                         for (URL url : listUrl) {
                             SystemLog.message("(" + indGDoc + ")URL:" + url);
                             indGDoc++;
                             SystemLog.message("*******************Run JSOUP**************************");
                             ExtractorJSOUP j = new ExtractorJSOUP();
                             geo2 = j.GetTitleAndHeadingTags(url.toString(),geo2);
                             if(ExtractorJSOUP.isEXIST_WEBPAGE()){
                                 SystemLog.message("*******************Run GATE**************************");
                                 geo3 = egate.extractorGATE(url, controller);
                                 geo3 = compareInfo3(geo3, geo2);
                                 //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
                                 geo3 = UpgradeTheDocumentWithOtherInfo(geo3);
                                 geo3 = pulisciDiNuovoGeoDocument(geo3);
                                 listGeo.add(geo3);
                                 for(GeoDocument geo: listGeo) {
                                     if (geo.getUrl() != null) {
                                         SystemLog.message("INSERIMENTO");
                                         SystemLog.message(geo.toString());
                                         geoDocumentDao.insertAndTrim(geo);
                                     }//if
                                 }
                                 listGeo.clear();
                             }
                         }//for each url of the listUrl
                     }catch(Exception e){
                         SystemLog.error("ERROR:" + e.getMessage());e.printStackTrace();
                     }
                 }else if(PROCESS_PROGAMM == 2){
                     //METODOLOGIE PER MULTIPLI URL
                     SystemLog.message("RUN PROCESS 2: METODOLOGIA PER MULTIPLI URL");
                     listUrl = websiteDao.selectAllUrl(COLUMN_TABLE_INPUT, LIMIT, OFFSET);
                     SystemLog.message("Loaded list of URL's with " + listUrl.size() + " elements");
                     if(listUrl.isEmpty()){SystemLog.abort(0,"Forced Exit from the programm hte list of url was empty!");}
                     List<GeoDocument> listGeoFinal = new ArrayList<>();
                     try{
                        SystemLog.message("*******************Run GATE**************************");
                        listGeo = egate.extractorGATE(listUrl, controller);
                        listUrl.clear();
                        for(GeoDocument geo: listGeo){
                            SystemLog.message("(" + indGDoc + ")URL:" + geo.getUrl());
                            indGDoc++;
                            SystemLog.message("*******************Run JSOUP**************************");
                            ExtractorJSOUP j = new ExtractorJSOUP();
                            geo3 = j.GetTitleAndHeadingTags(geo.getUrl().toString(),geo);
                            geo3 = compareInfo3(geo3, geo);
                            geo3 = UpgradeTheDocumentWithOtherInfo(geo3);
                            geo3 = pulisciDiNuovoGeoDocument(geo3);
                            SystemLog.message("INSERIMENTO");
                            SystemLog.message(geo3.toString());
                            geoDocumentDao.insertAndTrim(geo3);
                        }//for each GeoDocument
                     }//try for
                     catch(Exception e){
                        SystemLog.error("ERROR:" + e.getMessage());e.printStackTrace();
                     }
//                        }else if(PROCESS_PROGAMM == 3){
//                           //METODOLOGIE PER MULTIPLI URL CON UTILIZZO DEL DATASTORE
//                            SystemLog.ticket("RUN PROCESS 3", "OUT");
//                            listGeo = workWithMultipleUrlsAndDataStore(listUrl);
//                            if (listGeo == null || listGeo.isEmpty()) {
//                                SystemLog.ticket("Lista degli URL vuota o di elementi tutti irraggiungibili", "ERROR");
//                            } else {
//                                //Richiama il metodo per l'inserimento dei GeoDocument nellla tabella MySQL
//                                insertGeoDocumentToMySQLTableMainMethod(listGeo);
//                            }
                }else if(PROCESS_PROGAMM == 3){
                     SystemLog.message("RUN PROCESS 3");
                     String DIRECTORY_FILE = "C:\\Users\\Marco\\Downloads\\parseWebUrls";
                     List<File> files = FileUtil.readDirectory(DIRECTORY_FILE);
                     List<File> subFiles = new ArrayList<File>();
                     if(OFFSET+LIMIT < files.size())subFiles.addAll(files.subList(OFFSET,OFFSET+LIMIT));
                     else subFiles.addAll(files.subList(OFFSET, files.size()));
                     files.clear();
                     for(File file : subFiles){
                        String urlFile =(String) websiteDao.select("url","file_path",file.getName(),String.class);
                        if(urlFile == null ||(urlFile != null && geoDocumentDao.verifyDuplicate("url", urlFile.toString()))){
                            files.add(file);
                        }
                     }
                     for(File file :files) {subFiles.remove(file);}
                     SystemLog.attention("Loaded a list of: "+subFiles.size()+" files");
                     List<GeoDocument> geoDocs = new ArrayList<>();
                     //geoDocs = egate.extractMicrodataWithGATEMultipleFiles(subFiles, controller);
                     for(File file : subFiles){
                         //SystemLog.debug("File:"+file.getAbsolutePath());
                         GeoDocument g = egate.extractMicrodataWithGATESingleFile(file,controller);
                         g.setUrl(new URL(websiteDao.select("url","file_path",file.getName(),String.class).toString()));
                         geoDocs.add(g);
                     }
                     SystemLog.debug("Loaded a list of: "+subFiles.size()+" files");
                     if(subFiles==null || subFiles.isEmpty()){
                        SystemLog.error("The url of the list are all all unreachable or unexisting");}
                     else{
                        //Richiama il metodo per l'inserimento dei GeoDocument nellla tabella MySQL
                        for(GeoDocument g : geoDocs){
                            geoDocumentDao.insertAndTrim(g);
                        }
                     }
                 }//else lista url non vuota
                 else SystemLog.warning("You are jump the extraction process! (use 1,2 or 3 on the Proces Porgamma Parameter)");
             } //SE PROCESS_PROGRAMM == 4

             SystemLog.message("RUN PROCESS 4");
             if(GEODOMAIN_PROGRAMM || ONTOLOGY_PROGRAMM ||GENERATION_TRIPLE_KARMA_PROGRAMM ) {
               //CREAZIONE DI UNA TABELLA DI GEODOMAINDOCUMENT
               if (GEODOMAIN_PROGRAMM == true) {
                   SystemLog.ticket("RUN GEODOMAIN PROGRAMM", "OUT");
                   IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
                   geoDomainDocumentDao.setTableInsert(TABLE_OUTPUT_GEODOMAIN);
                   geoDomainDocumentDao.setTableSelect(TABLE_INPUT_GEODOMAIN);
                   geoDomainDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT_GEODOMAIN);
                   if (CREA_NUOVA_TABELLA_GEODOMAIN == true) {
                       geoDomainDocumentDao.create(ERASE_GEODOMAIN);
                   }
                   egd = new EstrazioneGeoDomainDocument((GeoDomainDocumentDaoImpl) geoDomainDocumentDao, LIMIT_GEODOMAIN, OFFSET_GEODOMAIN, FREQUENZA_URL_GEODOMAIN);
                   egd.CreateTableOfGeoDomainDocument("mysql");
               }
               //INTEGRIAMO LA TABELLA INFODOCUMENT PER LAVORARE CON UN'ONTOLOGIA
               if (ONTOLOGY_PROGRAMM == true) {
                   SystemLog.ticket("RUN ONTOLOGY PROGRAMM", "OUT");
                   IInfoDocumentDao infoDocumentDao = new InfoDocumentDaoImpl();
                   infoDocumentDao.setTableInsert(TABLE_OUTPUT_ONTOLOGY);
                   infoDocumentDao.setTableSelect(TABLE_OUTPUT);
                   infoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
                   if (CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY == true) {
                       infoDocumentDao.create(ERASE_ONTOLOGY);
                   } else {
                       for (GeoDocument g : listGeo) {
                           infoDocumentDao.insertAndTrim(g);
                       }
                   }
                   //integrationTableWithAOntology(DB_OUTPUT,USER,PASS,TABLE_OUTPUT,TABLE_OUTPUT_ONTOLOGY);
               }
               //GENERIAMO IL FILE DI TRIPLE CORRISPONDENTE ALLE INFORMAZIONI ESTRATTE CON KARMA
               if (GENERATION_TRIPLE_KARMA_PROGRAMM == true) {
                   SystemLog.message("RUN GENERATION TRIPLE WITH KARMA PROGRAMM");
                   got = new GenerationOfTriple(
                           ID_DATABASE_KARMA,//DB
                           FILE_MAP_TURTLE_KARMA, //PATH: karma_files/model/
                           FILE_OUTPUT_TRIPLE_KARMA, //PATH: karma_files/output/
                           TYPE_DATABASE_KARMA,//MySQL
                           HOST_DATABASE,
                           USER,//USER_KARMA
                           PASS,//PASS_KARMA
                           PORT_DATABASE.toString(),
                           DB_OUTPUT,
                           TABLE_INPUT_KARMA,
                           OUTPUT_FORMAT_KARMA,
                           KARMA_HOME
                   );
                   //got.GenerationOfTripleWithKarma(); //OLD METHOD
                   got.GenerationOfTripleWithKarmaAPIFromDataBase();
               }
             }
        }catch(OutOfMemoryError e) {
            SystemLog.error("java.lang.OutOfMemoryError, Reload the programm please");
        }
    }//main

    
    private GeoDocument UpgradeTheDocumentWithOtherInfo(GeoDocument geo) throws URISyntaxException{
       try{
            SystemLog.message("**************DOCUMENT*********************");
            //*************************************************************************************
            //INTEGRAZIONE FINALE CON IL DATABASE KEYWORDDB
            //SET CITY IF YOU DON'T HAVE
            if(StringKit.setNullForEmptyString(geo.getCity())==null){
                //SystemLog.message("Integrazione Keyworddb");
                geo.setCity(Docdao.selectValueForSpecificColumn("city", "url", geo.getUrl().toString()));
            }
            //SET PROVINCIA
            if(StringKit.setNullForEmptyString(geo.getCity())!=null){
               geo.setProvincia(SetProvinciaECity.checkProvincia(geo.getCity()));
            }
            //INTEGRAZIONE DEI CAMPI CITY-PROVINCIA-REGIONE DEI GEDOCUMENT
            if(StringKit.setNullForEmptyString(geo.getCity())!=null){
                 SetRegioneEProvincia set = new SetRegioneEProvincia();
                 set.checkString(geo.getCity());
                 geo.setRegione(set.getRegione());
                 geo.setProvincia(set.getProvincia());
            }
           //INTEGRAZIONE DEL CAMPO LANGUAGE -> NAZIONE
           SetNazioneELanguage set = new SetNazioneELanguage();
           String language = geo.getNazione();
           String domain = HttpUtil.getDomainName(geo.getUrl().toString());
           String nazione = set.checkNazioneByDomain(domain);
           geo.setNazione(nazione);
           //con il linguaggio identificato da Tika se fallisce il controllo del
           //dominio per esempio con estensioni .com,.edu,ecc.
           if(geo.getNazione() == null && language != null){
               nazione = set.checkNazioneByLanguageIdentificatorByTika(language);
           }
           geo.setNazione(nazione);
           //INTEGRAZIONE DEI CAMPI DELLE COORDINATE CON GOOGLE MAPS
           geo =j.connection(geo);
           SystemLog.message("COORD[LAT:" + geo.getLat() + ",LNG:" + geo.getLng() + "]");
           //PULIAMO NUOVAMENTE LA STRINGA EDIFICIO E INDIRIZZO (UTILE NEL CASO DI SearchMonkey e Tika)
           geo = pulisciDiNuovoLaStringaEdificio(geo);
           geo = pulisciDiNuovoLaStringaIndirizzo(geo);
           //AGGIUNGIAMO POSTALCODE E INDIRIZZONOCAP
           geo = new GeoDocument(geo.getUrl(), geo.getRegione(), geo.getProvincia(), geo.getCity(), geo.getIndirizzo(), geo.getIva(), geo.getEmail(), geo.getTelefono(),geo.getFax(), geo.getEdificio(),
                     geo.getLat(), geo.getLng(),geo.getNazione(),geo.getDescription(),null, null,null);
           String indirizzo = geo.getIndirizzo();
           String indirizzoNoCAP = null;
           String postalCode =null;
           String indirizzoHasNumber = null;
           SetCodicePostale  setCap = new SetCodicePostale();
           if(indirizzo != null){
               indirizzoNoCAP = indirizzo.replaceAll("\\d{5,6}", "").replace("-", "");
               postalCode = setCap.GetPostalCodeByIndirizzo(indirizzo);
           }//if indrizzo not null
           if((postalCode == null || postalCode == "")){
                 postalCode = setCap.checkPostalCodeByCitta(geo.getCity());//work
           }
           geo.setPostalCode(postalCode);
           if(indirizzoNoCAP != null){
               //indirizzoNoCAP = indirizzo.replaceAll("\\d{5,6}", "").replace("-", "");
               indirizzoHasNumber = setCap.GetNumberByIndirizzo(indirizzoNoCAP);
           }//if indrizzo not null
           if(indirizzoHasNumber!=null && StringKit.setNullForEmptyString(indirizzoHasNumber)!= null){
               geo.setIndirizzoHasNumber(indirizzoHasNumber);
               indirizzoNoCAP = indirizzoNoCAP.replace(indirizzoHasNumber,"").replaceAll("[\\^\\|\\;\\:\\,]","");
           }
           geo.setIndirizzoNoCAP(indirizzoNoCAP);
       }catch(NullPointerException ne){ne.printStackTrace();}
        return geo;
    }
       
    
    /**
     * Metodo che ripulisce il nome edificio da usare come URI da caratteri non
     * non voluti
     * @param geo GeoDocument fornito come input
     * @return il GeoDocument con il campo Edificio settato in un nuovo modo
     */
    private GeoDocument pulisciDiNuovoLaStringaEdificio(GeoDocument geo){
        //Le seguenti righe di codice aiutano ad evitare un'errore di sintassi
        //in fase di inserimento dei record nel database.
        try{       
            String set; 
             if(geo.getEdificio()!=null || StringKit.setNullForEmptyString(geo.getEdificio())!=null ){
                set=geo.getEdificio(); 
                //set = geo.getEdificio().replaceAll("[^a-zA-Z\\d\\s:]","");
                //Accetta le lettere accentuate
                if(set.toLowerCase().contains("http://")==true){
                    set = HttpUtil.getAuthorityName(set);
                    set = set.replaceAll("(https?|ftp)://", "");
                    set = set.replaceAll("(www(\\d)?)", "");
                    set = set.replace("."," ");
                    set = set.replace("/"," ");
                }            
                set = set.replaceAll("[^a-zA-Z\\u00c0-\\u00f6\\u00f8-\\u00FF\\d\\s:]","");
                set = set.replaceAll("\\s+", " ");
                //set = set.replaceAll("http", "");
                set = set.replace(":", "");
                if(set!= null){
                    geo.setEdificio(set.toUpperCase());
                }
            }
           }catch(Exception e){}
        return geo;
    }//pulisciDiNuovoLaStringaEdificio
    
    /**
     * Metodo che ripulisce il nome indirizzo da usare come URI da caratteri non
     * non voluti
     * @param geo GeoDocument fornito come input
     * @return il GeoDocument con il campo indirizzo settato in un nuovo modo
     */
     private GeoDocument pulisciDiNuovoLaStringaIndirizzo(GeoDocument geo){        
        try{
            String address = geo.getIndirizzo();
           // address = "’Orario di Lavoro - Piazza San Marco, 4 - 50121";
        if(address != null){   
            if(StringKit.setNullForEmptyString(address)!=null){
        //Rimuovi gli SpaceToken
        //set = geo.getIndirizzo().replaceAll("[^a-zA-Z\\d\\s:]",""); 
        address = address.replaceAll("\\s+", " ");
        //set = set.replaceAll("...", "");    
      
        List<String> addressWords = Arrays.asList(
                "VIA","via","Via","VIALE","viale","Viale","STRADA","strada","Strada","ROAD","road","Road","Piazza","PIAZZA",
                "piazza","P.zza","p.zza","Piazzale","piazzale","iazza","Corso","Loc.","loc.","loc","Loc","località",
                "Località","V.","v."
        );
        boolean b = false;
        for(String s : addressWords){                    
            //With Split
            String[] listSplit = address.split(" "+s+" ");
            if(listSplit.length > 0){            
                for(int i =0; i < listSplit.length; i++){
                    if(i > 0){
                        address ="";
                        address = address + listSplit[i]; 
                        address = s+" "+address;
                        b=true; break;
                    }
                    //if(i == listSplit.length-1){indirizzo = s+" "+indirizzo;}
                }            
            }
            if(b==true){ 
                //"[^a-z0-9\\s]+"
                address = address.replaceAll("[^A-Za-z0-9\\s]+","");
                geo.setIndirizzo(address);
            }         
        }//FOR       
        }//IF indirizzo is not null
        }//IF NOT NULL
        }catch(NullPointerException ne){ne.printStackTrace();}
        
      
        return geo;
    }
    

    /**
     * Return the value of FILTER
     * @return value
     */
    public boolean isFILTER() {
        return FILTER;
    }  
       
    /**
     * Metodo di comparazione dei risultati attraverso GATE e Apache Tika per la modalità 1
     * @param geo GeoDocument ricavato da DOM HTML
     * @param geo2 GeoDocument ricavato da anlisi del GATE
     * @return un geodocument con il meglio dei risultati di entrambe le ricerche
     */
    public static GeoDocument compareInfo3(GeoDocument geo,GeoDocument geo2){
        if(StringKit.setNullForEmptyString(geo2.getEdificio())!=null){geo.setEdificio(geo2.getEdificio());}
        else if(StringKit.setNullForEmptyString(geo2.getEdificio())==null){geo.setEdificio(StringKit.setNullForEmptyString(geo.getUrl().toString()));}
        if(StringKit.setNullForEmptyString(geo2.getDescription())!=null){geo.setDescription(geo2.getDescription());}
        if(StringKit.setNullForEmptyString(geo2.getNazione())!=null){geo.setNazione(geo2.getNazione());}
        return geo;
    }//compareInfo3
    
    private GeoDocument pulisciDiNuovoGeoDocument(GeoDocument geo){
        if(geo.getRegione()!=null){geo.setRegione(geo.getRegione().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getProvincia()!=null){geo.setProvincia(geo.getProvincia().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getCity()!=null){geo.setCity(geo.getCity().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getIndirizzo()!=null){geo.setIndirizzo(geo.getIndirizzo().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getIva()!=null){geo.setIva(geo.getIva().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getEmail()!=null){geo.setEmail(geo.getEmail().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getTelefono()!=null){geo.setTelefono(geo.getTelefono().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getEdificio()!=null){geo.setEdificio(geo.getEdificio().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getNazione()!=null){geo.setNazione(geo.getNazione().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        //geo.setLat(geo.getLat().replaceAll("\\r\\n|\\r|\\n","").trim());
        //geo.setLng(geo.getLng().replaceAll("\\r\\n|\\r|\\n","").trim());
        if(geo.getDescription()!=null){geo.setDescription(geo.getDescription().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getIndirizzoNoCAP()!=null){geo.setIndirizzoNoCAP(geo.getIndirizzoNoCAP().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getPostalCode()!=null){geo.setPostalCode(geo.getPostalCode().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getFax()!=null){geo.setFax(geo.getFax().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        if(geo.getIndirizzoHasNumber()!=null){geo.setIndirizzoHasNumber(geo.getIndirizzoHasNumber().replaceAll("\\r\\n|\\r|\\n","").replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim()); }
        return geo;
    
    }

}