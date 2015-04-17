package util.estrattori;

import object.dao.*;
import object.impl.*;
import util.SystemLog;
import util.cmd.SimpleParameters;
import gate.CorpusController;
import util.setInfoParameterIta.SetRegioneEProvincia;
import gate.Gate;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import object.model.GeoDocument;
import util.gate.DataStoreApplication;
import util.http.HttpUtil;
import util.karma.GenerationOfTriple;
import util.setInfoParameterIta.SetCodicePostale;
import util.setInfoParameterIta.SetNazioneELanguage;
import util.ManageJsonWithGoogleMaps;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * MainEstrazioneInfoDocument.java
 * Classe con la quale da una lista di url realizziamo una lista di GeoDocument
 * da inserire come InfoDocument nel database mySQL
 * @author 4535992
 */
public class ExtractInfoMySQL {
       
     private Map<String,String> parameters = new HashMap<String,String>();
     private String TYPE_EXTRACTION;
     private Integer PROCESS_PROGAMM ;    
     private boolean CreaNuovaTabellaGeoDocumenti;
     private boolean ERASE;
     private Integer LIMIT ;
     private Integer OFFSET;  
     private boolean ONTOLOGY_PROGRAMM;
     private boolean GENERATION_TRIPLE_KARMA_PROGRAMM;   
     private boolean GEODOMAIN_PROGRAMM;    
     private boolean FILTER;  
     private String API_KEY_GM;
        
     private String USER; 
     private String PASS; 
     private String DB_INPUT;
     private String DB_OUTPUT;
     private String TABLE_INPUT;
     private String TABLE_OUTPUT;
     private String COLUMN_TABLE_INPUT;
     
     private String TABLE_KEYWORD_DOCUMENT;
     private String DB_KEYWORD;        
     
     private String DRIVER_DATABASE;
     private String DIALECT_DATABASE;
     private String HOST_DATABASE;
     private Integer PORT_DATABASE;

     private String NOME_DATASTORE;
     private String DS_DIR;
     private Integer RANGE;
     private int indGDoc;
     private int tentativiOutMemory;
     
     private String TABLE_OUTPUT_ONTOLOGY;
     private boolean CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY;
     private boolean ERASE_ONTOLOGY;
     
     private String TYPE_DATABASE_KARMA; //MySQL
     private String FILE_MAP_TURTLE_KARMA; //PATH: karma_files/model/
     private String FILE_OUTPUT_TRIPLE_KARMA; //PATH: karma_files/output/
     private String ID_DATABASE_KARMA; //DB  
     //private String USER_KARMA;
     //private String PASS_KARMA;
     private String TABLE_INPUT_KARMA;
     private String OUTPUT_FORMAT_KARMA; //ttl
     //private String DISK_STORE_KARMA; //c:
     private String KARMA_HOME;
     
     //private String USER_GEODOMAIN;
     //private String PASS_GEODOMAIN;
     private Integer LIMIT_GEODOMAIN;
     private Integer OFFSET_GEODOMAIN;
     private Integer FREQUENZA_URL_GEODOMAIN;
     private String TABLE_INPUT_GEODOMAIN;
     private String TABLE_OUTPUT_GEODOMAIN;   
     private String DB_INPUT_GEODOMAIN;
     private String DB_OUTPUT_GEODOMAIN;
     private boolean CREA_NUOVA_TABELLA_GEODOMAIN;
     private boolean ERASE_GEODOMAIN;
      
     //COSTRUTTORI
     private DataStoreApplication datastore = new DataStoreApplication(); 
     private GenerationOfTriple got = new GenerationOfTriple();
     private ManageJsonWithGoogleMaps j = new ManageJsonWithGoogleMaps(); 
     private EstrazioneGeoDomainDocument egd = new EstrazioneGeoDomainDocument(); 
     //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
     private EstrazioneDatiWithGATE egate = new EstrazioneDatiWithGATE();
     private EstrazioneDatiWithGATEAndDataStore egateDataStore = new EstrazioneDatiWithGATEAndDataStore();
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     public ExtractInfoMySQL(){}
        
     public ExtractInfoMySQL(SimpleParameters par){
         
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
            this.indGDoc = Integer.parseInt(par.getValue("PARAM_INDGDOC"));
            
            if(PROCESS_PROGAMM == 3){
                this.NOME_DATASTORE = par.getValue("PARAM_NOME_DATASTORE");
                this.DS_DIR = par.getValue("PARAM_DS_DIR");
                this.RANGE = Integer.parseInt(par.getValue("PARAM_RANGE"));          
                this.tentativiOutMemory = Integer.parseInt(par.getValue("PARAM_TENTATIVI_OUT_OF_MEMORY"));
                datastore = new DataStoreApplication(DS_DIR,NOME_DATASTORE);
            }
            
            if(GENERATION_TRIPLE_KARMA_PROGRAMM==true) {
                this.TYPE_DATABASE_KARMA = par.getValue("PARAM_TYPE_DATABASE_KARMA");//MySQL
                this.FILE_MAP_TURTLE_KARMA = par.getValue("PARAM_FILE_MAP_TURTLE_KARMA"); //PATH: karma_files/model/
                this.FILE_OUTPUT_TRIPLE_KARMA = par.getValue("PARAM_FILE_OUTPUT_TRIPLE_KARMA");//PATH: karma_files/output/
                this.ID_DATABASE_KARMA = par.getValue("PARAM_ID_DATABASE_KARMA");//DB
                //this.USER_KARMA = par.getValue("PARAM_USER_KARMA");
                //this.PASS_KARMA = par.getValue("PARAM_PASS_KARMA");
                this.TABLE_INPUT_KARMA = par.getValue("PARAM_TABLE_INPUT_KARMA");
                this.OUTPUT_FORMAT_KARMA = par.getValue("PARAM_OUTPUT_FORMAT_KARMA");
                //this.DISK_STORE_KARMA =par.getValue("PARAM_DISK_STORE_KARMA");
                this.KARMA_HOME = par.getValue("PARAM_KARMA_HOME");
            }
            
            if(ONTOLOGY_PROGRAMM==true){
                this.CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY = Boolean.parseBoolean(par.getValue("PARAM_CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY").toLowerCase());
                this.ERASE_ONTOLOGY = Boolean.parseBoolean(par.getValue("PARAM_ERASE_ONTOLOGY").toLowerCase());
                this.TABLE_OUTPUT_ONTOLOGY = par.getValue("PARAM_TABLE_OUTPUT_ONTOLOGY");
            }
            
            j = new ManageJsonWithGoogleMaps(API_KEY_GM);

            par.getParameters().clear();
     }
    /**
     * Metodo Main del programma per la creazione di InfoDocument    
     */
    public void Extraction() throws Exception {
        try{
            SystemLog.write("Run the extraction method.", "OUT");
            ArrayList<URL> listUrl = new ArrayList<>();        
            ArrayList<GeoDocument> listGeo = new ArrayList<>();   
            ArrayList<String> listStringUrl = new ArrayList<String>();
            try{             
                 if(PROCESS_PROGAMM != 4){
                     IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();
                     geoDocumentDao.setTable(TABLE_OUTPUT);
                     geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
                     IWebsiteDao websiteDao = new WebsiteDaoImpl();
                     websiteDao.setTable(TABLE_INPUT);
                     websiteDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_INPUT);
                     if(CreaNuovaTabellaGeoDocumenti ==true){
                         geoDocumentDao.create();
                    }
                     //Estraiamo dal database la nostra lista di url
                     listStringUrl =(ArrayList<String>) websiteDao.selectAllString(COLUMN_TABLE_INPUT,LIMIT.toString(),OFFSET.toString());
                    for(String sUrl : listStringUrl){
                        URL u;
                        if(!(sUrl.contains("http://")) && !(sUrl.contains("www"))){
                            u = new URL("http://www."+sUrl);
                        }
                        else if(!(sUrl.contains("http://"))){
                            u = new URL("http://"+sUrl);
                        }else{
                            u = new URL(sUrl);
                        }
                        listUrl.add(u);
                    }
                    listStringUrl.clear();

                      for(URL url : listUrl){
                        if(geoDocumentDao.verifyDuplicate(COLUMN_TABLE_INPUT,url.toString())==true){
                            listStringUrl.add(url.toString());
                        }
                    }
                    for(int i = 0; i < listStringUrl.size(); i++){                    
                        listUrl.remove(new URL(listStringUrl.get(i)));
                    }
                    listStringUrl.clear();
                    SystemLog.write("Lista di URL con " + listUrl.size() + " elements", "OUT");
                    //SETTIAMO GATE PER IL PROGRAMMA
                    SystemLog.write("Inizializzazione GATE...", "OUT");
                    Gate.setGateHome(new File("gate_files"));
                    Gate.setPluginsHome(new File("gate_files/plugins"));
                    Gate.setSiteConfigFile(new File("gate_files/gate.xml"));
                    Gate.setUserConfigFile(new File("gate_files/user-gate.xml"));
                     Gate.setUserSessionFile(new File("gate_files/gate.session"));
                    Gate.init();
                    //INIZIALIZZIAMO IL PROGRAMMA CON IL NOSTRO FILE GAPP
                    init();
                    SystemLog.write("...GATE initialised", "OUT");
                    //**********************************************************************
                    //Lavorare con l'interfaccia grafica di GATE
                    /*MainFrame.getInstance().setVisible(true);*/
                    //*************************************************************************
                    //QUIT THE PROGRAMM SE LA LISTA DEGLI URL E' VUOTA
                    if(listUrl.isEmpty()){
                        SystemLog.write("Lista degli url vuota uscita forzata dal programma di estrazione", "ERROR");}
                    else{
                        //*****************************************************************************************************
                        if(PROCESS_PROGAMM == 1){
                        // <editor-fold defaultstate="collapsed" desc="PROCESSO 1">
                        SystemLog.write("RUN PROCESS 1", "OUT");
                        //METODOLOGIE PER SINGOLO URL        
                        workWithSingleUrl(listUrl);
                        // </editor-fold>                       
                        //********************************************************************************************************
                        }else if(PROCESS_PROGAMM == 2){
                        // <editor-fold defaultstate="collapsed" desc="PROCESSO 2">
                        SystemLog.write("RUN PROCESS 2", "OUT");
                        //METODOLOGIE PER MULTIPLI URL 
                        listGeo = workWithMultipleUrls(listUrl);
                        if(listGeo==null || listGeo.isEmpty()){
                            SystemLog.write("Lista degli URL vuota o di elementi tutti irraggiungibili", "ERROR");}
                        else{
                            //Richiama il metodo per l'inserimento dei GeoDocument nellla tabella MySQL
                             insertGeoDocumentToMySQLTableMainMethod(listGeo);
                        }
                        // </editor-fold>                          
                        //*********************************************************************************************************
                        }else if(PROCESS_PROGAMM == 3){
                        // <editor-fold defaultstate="collapsed" desc="PROCESSO 3">
                        //METODOLOGIE PER MULTIPLI URL CON UTILIZZO DEL DATASTORE PER GESTIONE E RICHIEDE PIU MEMORIA RAM
                        //(4G COME MINIMO)MA A IL VANTAGGIO CHE SE LANCIA L'ERRORE java.lang.OutOfMemoryError 
                        //SI PUO' IMPOSTARE LA MACCHINA PER RILANCIARE IL PROGRAMMA DAL DOCUMENTO X DEL CORPUS Y
                        //PER MANCANZE DI TEMPO CI LIMITIAMO A RILANCIARE IL PROGRAMMA CON UN DIVERSO VALORE DI OFFSET
                        //FINO AL RAGGIUNGIMENTO DEL LIMIT IMPOSTATO (NON E' DETTO CHE FUNZIONI AL 100%)            
                        SystemLog.write("RUN PROCESS 3", "OUT");
                        listGeo = workWithMultipleUrlsAndDataStore(listUrl);
                        if(listGeo==null || listGeo.isEmpty()){
                            SystemLog.write("Lista degli URL vuota o di elementi tutti irraggiungibili", "ERROR");}
                        else{
                            //Richiama il metodo per l'inserimento dei GeoDocument nellla tabella MySQL
                             insertGeoDocumentToMySQLTableMainMethod(listGeo);
                        }
                        // </editor-fold>     
                        //*********************************************************************************************************   
                        }else{
                            SystemLog.write("ERRORE NELLA SELEZIONE DEL PROCESSO DA UTILIZZARE 1,2,3,4 SONO I VALORI POSSIBILI", "ERROR");
                        }
                    }//else lista url non vuota
                 } //SE PROCESS_PROGRAMM == 4 
                 SystemLog.write("RUN PROCESS 4", "OUT");
            } catch (RuntimeException e2) {
                //Err.prln("ECCEZIONE DI QUALCHE TIPO CAUSATA IN FASE DI RUN");
                SystemLog.write(e2.getMessage(), "ERROR");
                e2.printStackTrace();
                Logger.getLogger(ExtractInfoMySQL.class.getName()).log(Level.SEVERE, null, e2);
            } catch (Exception e2){
                SystemLog.write(e2.getMessage(), "ERROR");
                e2.printStackTrace();
                Logger.getLogger(ExtractInfoMySQL.class.getName()).log(Level.SEVERE, null, e2);
            } 
            //************************************************************************************************************
            //************************************************************************************************************
            finally {
                //CREAZIONE DI UNA TABELLA DI GEODOMAINDOCUMENT       
                if(GEODOMAIN_PROGRAMM == true){
                    IGeoDomainDocumentDao geoDomainDocumentDao = new GeoDomainDocumentDaoImpl();
                    geoDomainDocumentDao.setTableInsert(TABLE_OUTPUT_GEODOMAIN);
                    geoDomainDocumentDao.setTableSelect(TABLE_INPUT_GEODOMAIN);
                    geoDomainDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT_GEODOMAIN);
                    SystemLog.write("RUN GEODOMAIN PROGRAMM", "OUT");
                    if(CREA_NUOVA_TABELLA_GEODOMAIN==true){
                          geoDomainDocumentDao.create(ERASE_GEODOMAIN);
                    }
                    egd = new EstrazioneGeoDomainDocument((GeoDomainDocumentDaoImpl)geoDomainDocumentDao,LIMIT,OFFSET,FREQUENZA_URL_GEODOMAIN);
                    egd.CreateTableOfGeoDomainDocument("mysql");
                }
                //INTEGRIAMO LA TABELLA INFODOCUMENT PER LAVORARE CON UN'ONTOLOGIA        
                if(ONTOLOGY_PROGRAMM == true){ 
                    SystemLog.write("RUN ONTOLOGY PROGRAMM", "OUT");
                    IInfoDocumentDao infoDocumentDao = new InfoDocumentDaoImpl();
                    infoDocumentDao.setTable(TABLE_OUTPUT_ONTOLOGY);
                    infoDocumentDao.setSecondTable(TABLE_OUTPUT);
                    infoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
                    if(CREA_NUOVA_TABELLA_INFODOCUMENT_ONTOLOGY == true){
                       infoDocumentDao.create(ERASE_ONTOLOGY);
                    }else{
                        for(GeoDocument g : listGeo){
                            infoDocumentDao.insertAndTrim(g);
                        }
                    }
                    //integrationTableWithAOntology(DB_OUTPUT,USER,PASS,TABLE_OUTPUT,TABLE_OUTPUT_ONTOLOGY);
                }
                //GENERIAMO IL FILE DI TRIPLE CORRISPONDENTE ALLE INFORMAZIONI ESTRATTE CON KARMA
                if(GENERATION_TRIPLE_KARMA_PROGRAMM == true)
                {
                    SystemLog.write("RUN GENERATION TRIPLE WITH KARMA PROGRAMM", "OUT");
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
            }//finally
    }catch(java.lang.OutOfMemoryError e){
        SystemLog.write("java.lang.OutOfMemoryError, Reload the programm please", "ERROR");
    }
}//main	

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Metodo che fa lavorare GATE sui singoli url.
     * @param listUrl la lista di url fonrita come input
     */
    private void workWithSingleUrl(ArrayList<URL> listUrl){
        ArrayList<GeoDocument> listGeo = new ArrayList<> ();
        for (URL url : listUrl) {
             GeoDocument geo = new GeoDocument(null,null,null, null, null, null, null,null, null, null,null,null,null,null,null,null,null);
             GeoDocument geo2 = new GeoDocument(null, null, null, null, null, null, null, null, null, null, null, null,null,null,null,null,null);
              try{
                SystemLog.write("(" + indGDoc + ")URL:" + url, "OUT");
                indGDoc++;
                if(geo2.getEdificio()==null){
                    SystemLog.write("*********************************************", "OUT");
                    SystemLog.write("Run JSOUP", "OUT");
                    util.estrattori.EstrazioneDatiWithJSOUP j = new util.estrattori.EstrazioneDatiWithJSOUP();                   
                    geo2 = j.GetTitleAndHeadingTags(url.toString(),geo2);
                }   
               if(geo2==null || geo2.getEdificio()==null){continue;}
               else{
                    SystemLog.write("*********************************************", "OUT");
                    SystemLog.write("Run GATE", "OUT");
                    geo = egate.extractMicrodataWithGATESingleUrl(url,null,controller,indGDoc);                        

                    if(geo!=null){
                       geo = compareInfo3(geo, geo2);
                    // </editor-fold>
                       //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
                       geo = UpgradeTheDocumentWithOtherInfo(geo); 
                       geo = pulisciDiNuovoGeoDocument(geo);
                    }
                }//else della "esistenza" dell'indirizzo url nel web  
                                    
              if(geo.getUrl()!=null && geo.getEdificio()!=null){
                 listGeo.add(geo);                                            
                 insertGeoDocumentToMySQLTableMainMethod(listGeo);
                 listGeo.clear();   
              }
             }//try for
             catch(NullPointerException ne){continue;}
             catch(Exception e){e.printStackTrace();}
          }//for each url of the listUrl               
        //mei.integrationTableWithAOntology(DB_OUTPUT, USER, PASS, TABLE_OUTPUT, TABLE_OUTPUT_ONTOLOGY);
    }//workWithSingleUrl
    
    /**
     * Metodo che fa lavorare GATE sui più url.
     * @param listUrl la lista di url fonrita come input
     * @return la lista di GeoDocument relativa alla lista degli url
     */
    private ArrayList<GeoDocument> workWithMultipleUrls(ArrayList<URL> listUrl){
        ArrayList<GeoDocument> listGeo = new ArrayList<> ();
        ArrayList<GeoDocument> listGeoFinal = new ArrayList<> ();
        try{                                                                                        
          SystemLog.write("*********************************************", "OUT");
          SystemLog.write("Run GATE", "OUT");
          listGeo = egate.extractMicrodataWithGATEMultipleUrls(listUrl,null,controller,indGDoc);                        
          listUrl.clear();          
          for(GeoDocument geo: listGeo){
              //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
               geo = UpgradeTheDocumentWithOtherInfo(geo);
               geo = pulisciDiNuovoGeoDocument(geo);
              //AGGIUNGIAMO ALLA LISTA DI GEODOCUMENT
              listGeoFinal.add(geo);              
          }//for each GeoDocument     
       }//try for
       catch(Exception e){
           SystemLog.write(e.getMessage(), "ERROR");e.printStackTrace();}
       return listGeoFinal;              
    }
    /**
     * Metodo che fa lavorare GATE sui più url con in più l'ausilio del datastore.
     * @param listUrl la lista di url fonrita come input
     * @return la lista di GeoDocument relativa alla lista degli url
     */
    private ArrayList<GeoDocument> workWithMultipleUrlsAndDataStore(ArrayList<URL> listUrl){
        ArrayList<GeoDocument> listGeo = new ArrayList<> ();
        ArrayList<GeoDocument> listGeoFinal = new ArrayList<> ();
        //QUESTA LINEA DI CODICE E' INUTILE SE SPECIFICATA ALL'INIZIO
        //DataStoreApplication datastore = new DataStoreApplication(DS_DIR,NOME_DATASTORE);
        try{       
          SystemLog.write("*********************************************", "OUT");
          SystemLog.write("Run GATE", "OUT");
          //System.out.println("*********************************************");
          listGeo = egateDataStore.extractMicrodataWithGATEMultipleUrls(listUrl,null,controller,indGDoc,datastore);                        
          //***************************************************************************************************  
          listUrl.clear();          
          for(GeoDocument geo: listGeo){
              //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
               geo = UpgradeTheDocumentWithOtherInfo(geo);
               geo = pulisciDiNuovoGeoDocument(geo);
              //AGGIUNGIAMO ALLA LISTA DI GEODOCUMENT
              listGeoFinal.add(geo);              
          }//for each GeoDocument
        //*************************************************************************************

        //************************************************************************* 
       }//try for
       catch(Exception e){
           SystemLog.write(e.getMessage(), "ERROR");e.printStackTrace();}
       return listGeoFinal;              
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** The Corpus Pipeline application to contain ANNE,Lingpipe,Tools,ecc. */
    private static CorpusController controller;

    /**
     * Initialise the GATE system. This creates a "corpus pipeline"
     * application that can be used to run sets of documents through
     * the extraction system.
     */
    public void init() throws GateException, IOException {
      //Out.prln("Loading file .gapp...");
      //Carica tutte le applicazioni della GATE_HOME attraverso il file .gapp
      File home = Gate.getGateHome();
      //Con il controllo della lingua
      File gapp = new File(home, "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
      //Senza il controllo della lingua
      //File gapp = new File(home, "custom/gapp/geoLocationPipelineFast.xgapp");
      controller = (CorpusController) PersistenceManager.loadObjectFromFile(gapp);
        CorpusController  con = (CorpusController) PersistenceManager.loadObjectFromFile(gapp);
    } // initAnnie()
    
    private GeoDocument UpgradeTheDocumentWithOtherInfo(GeoDocument geo) throws URISyntaxException{
       try{
        SystemLog.write("**************DOCUMENT*********************", "OUT");
        //*************************************************************************************  
        //INTEGRAZIONE FINALE CON IL DATABASE KEYWORDDB
        if(setNullForEmptyString(geo.getCity())==null){

           SystemLog.write("Integrazione Keyworddb", "OUT");
            IDocumentDao dao = new DocumentDaoImpl();
            dao.setTable(TABLE_KEYWORD_DOCUMENT);
            dao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS ,DB_KEYWORD);
            geo.setCity(dao.selectValueForSpecificColumn("city","url",geo.getUrl().toString()));

        }
        //************************************************************************************
        //INTEGRAZIONE DEI CAMPI CITY-PROVINCIA-REGIONE DEI GEDOCUMENT           
        if(setNullForEmptyString(geo.getCity())!=null){
             SetRegioneEProvincia set = new SetRegioneEProvincia();
             set.checkString(geo.getCity());                  
             geo.setRegione(set.getRegione());
             geo.setProvincia(set.getProvincia());
        }  
         //************************************************************************************             
         //INTEGRAZIONE DEL CAMPO LANGUAGE -> NAZIONE
         SetNazioneELanguage set = new SetNazioneELanguage();
         String language = geo.getNazione();
         String domain = getDomainName(geo.getUrl().toString());
         String nazione = set.checkNazioneByDomain(domain);
         geo.setNazione(nazione);
         //con il linguaggio identificato da Tika se fallisce il controllo del
         //dominio per esempio con estensioni .com,.edu,ecc.
         if(geo.getNazione() == null && language != null){
              nazione = set.checkNazioneByLanguageIdentificatorByTika(language);
         }
         geo.setNazione(nazione);
         //*************************************************************************************
         //INTEGRAZIONE DEI CAMPI DELLE COORDINATE CON GOOGLE MAPS             
         geo =j.connection(geo);    
         SystemLog.write("COORD[LAT:" + geo.getLat() + ",LNG:" + geo.getLng() + "]", "OUT");
         //PULIAMO NUOVAMENTE LA STRINGA EDIFICIO E INDIRIZZO (UTILE NEL CASO DI SearchMonkey e Tika)
         geo = pulisciDiNuovoLaStringaEdificio(geo);
         geo = pulisciDiNuovoLaStringaIndirizzo(geo);
         //************************************************************************************* 
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
         ////////////////////////////////////////////////
       if(indirizzoNoCAP != null){          
             //indirizzoNoCAP = indirizzo.replaceAll("\\d{5,6}", "").replace("-", ""); 
             indirizzoHasNumber = setCap.GetNumberByIndirizzo(indirizzoNoCAP);                       
        }//if indrizzo not null 
        if(indirizzoHasNumber!=null && setNullForEmptyString(indirizzoHasNumber)!= null){
              geo.setIndirizzoHasNumber(indirizzoHasNumber);
              indirizzoNoCAP = indirizzoNoCAP.replace(indirizzoHasNumber,"").replaceAll("[\\^\\|\\;\\:\\,]","");
        ////////////////////////////////////////////////
       }
       geo.setIndirizzoNoCAP(indirizzoNoCAP);
        ////////////////////////////////////////////////
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
             if(geo.getEdificio()!=null || setNullForEmptyString(geo.getEdificio())!=null ){ 
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
            if(setNullForEmptyString(address)!=null){
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
     * Metodo che assite l'inserimento dei GeoDocument nella tabella MySQL
     * @param listGeo la lista di GeoDocument fornita come input
     */
    private void insertGeoDocumentToMySQLTableMainMethod(ArrayList<GeoDocument> listGeo){
         //INSERIMENTO DEL GEODOCUMENT NELLA TABELLA INFODOCUMENT NEL 
         //DATABASE GEOLOCATIONDB MYSQL
        try{ 
            for(GeoDocument geo: listGeo){
                if(geo.getUrl()!=null && geo.getEdificio()!=null){
                    SystemLog.write("INSERIMENTO", "OUT");
                    IGeoDocumentDao dao = new GeoDocumentDaoImpl();
                    dao.setTable(TABLE_OUTPUT);
                    dao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE.toString(), USER, PASS, DB_OUTPUT);
                    dao.insertAndTrim(geo);
                   //*********************************************************************************************************
                }//if
         }//for
        }catch(Exception e){
                SystemLog.write("Errore durante l'inserimento del record nella tabella MySQL:" + e.getMessage(), "ERROR");
                e.printStackTrace();
                Logger.getLogger(ExtractInfoMySQL.class.getName()).log(Level.SEVERE,null,e);
        }finally{
                //c.closeConnection(); 
               // uttm.closeConnection();
        }           
    }

    /**
     * Metodo che matcha e sostituisce determinati parti di una stringa attraverso le regular expression
     * @param input stringa di input
     * @param expression regular expression da applicare
     * @param replace setta la stringa con cui sostituire il risultato del match
     * @return il risultato in formato stringa della regular expression
     */
    public String RegexAndReplace(String input,String expression,String replace){
        String result ="";
        //String expression = "\\d{5,7}";
        ///[^a-z0-9\s]+/ig
        if(replace==null){
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(input);
            while(matcher.find()){
                 result = matcher.group().toString();   
                 if(result != null && result != ""){break;}
            }
        }else{
            input.replaceAll(expression, replace);
        }
        return result;
    }
     
    /**
     * Semplice metodo che estare il domino web di appartenenza dell'url analizzato
     * @param u url di ingresso in fromato stringa
     * @return il dominio web dell'url in formato stringa
     * @throws URISyntaxException 
     */
    private String getDomainName(String u) {     
            String domain ="";
            try{
            URI uri = new URI(u);
            domain = uri.getHost();           
            //return domain.startsWith("www.") ? domain.substring(4) : domain;
            }catch(URISyntaxException ue){
                String[] ss = u.split("/");
                domain = ss[0]+"/";
            }
            return domain;
    }//getDomainName
    
    /**
    * Setta a null se verifica che la stringa non è
    * nulla, non è vuota e non è composta da soli spaceToken (white space)
    * @param s stringa di input
    * @return  il valore della stringa se null o come è arrivata
    */
    private String setNullForEmptyString(String s){     
        if(s!=null && !s.isEmpty() && !s.trim().isEmpty()){return s;}
        else{return null;}
    } //setNullforEmptyString
   
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
    private GeoDocument compareInfo3(GeoDocument geo,GeoDocument geo2){			            	  
        if(setNullForEmptyString(geo2.getEdificio())!=null){geo.setEdificio(geo2.getEdificio());}
        else if(setNullForEmptyString(geo2.getEdificio())==null){geo.setEdificio(geo.getUrl().toString());}
        
        if(setNullForEmptyString(geo2.getDescription())!=null){geo.setDescription(geo2.getDescription());} 
        if(setNullForEmptyString(geo2.getNazione())!=null){geo.setNazione(geo2.getNazione());} 
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