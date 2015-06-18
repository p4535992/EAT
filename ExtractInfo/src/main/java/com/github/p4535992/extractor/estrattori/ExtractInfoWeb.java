package com.github.p4535992.extractor.estrattori;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.extractor.gate.GateKit;
import com.github.p4535992.extractor.karma.GenerationOfTriple;
import gate.CorpusController;
import com.github.p4535992.extractor.object.dao.jdbc.IGeoDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.GeoDocumentDaoImpl;
import com.github.p4535992.extractor.object.model.GeoDocument;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.github.p4535992.extractor.object.dao.jdbc.IInfoDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.InfoDocumentDaoImpl;

/**
 * Created by 4535992 on 15/06/2015.
 */
public class ExtractInfoWeb {

    private static int indGDoc;

    private static CorpusController controller;

    public ExtractInfoWeb(){}
    private String TABLE_INPUT,TABLE_OUTPUT,DRIVER_DATABASE,DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE, USER, PASS, DB_OUTPUT;

    public ExtractInfoWeb(String DRIVER_DATABASE,String DIALECT_DATABASE,String HOST_DATABASE,
                          String PORT_DATABASE,String USER,String PASS,String DB_OUTPUT){       
        this.DRIVER_DATABASE = DRIVER_DATABASE;
        this.DIALECT_DATABASE = DIALECT_DATABASE;
        this.HOST_DATABASE = HOST_DATABASE;
        this.PORT_DATABASE = PORT_DATABASE;
        this.USER = USER;
        this.PASS = PASS;
        this.DB_OUTPUT=DB_OUTPUT;
        GateKit.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session");
        GateKit.loadGapp("custom/gapp", "geoLocationPipeline06102014v7_fastMode.xgapp");
        controller = GateKit.getController();
    }

    private GeoDocument ExtractGeoDocumentFromWeb(String url,String TABLE_INPUT,String TABLE_OUTPUT){
        GeoDocument geoDoc = new GeoDocument();
        try {
            if (!url.matches("^(https?|ftp)://.*$")) {
                url = "http://" + url;
            }
            geoDoc = ExtractGeoDocumentFromWeb(new URL(url),TABLE_INPUT,TABLE_OUTPUT);
        }catch(MalformedURLException e){
            SystemLog.error("You have isert a nota valid url for the extraction of information:"+url);
        }
        return geoDoc;
    }

    private List<GeoDocument> ExtractMultiGeoDocumentFromListString(List<String> urls,String TABLE_INPUT,String TABLE_OUTPUT){
        List<URL> urls2 = new ArrayList<>();
        List<GeoDocument> listGeo;
        for(int i = 0; i < urls.size(); i++) {
            try{
                if (!urls.get(i).matches("^(https?|ftp)://.*$")) {
                    urls2.set(i,new URL("http://" + urls.get(i)));
                }
            }catch(MalformedURLException e){
                SystemLog.error("You have insert a not valid url for the extraction of information:"+"http://" + urls.get(i));              
            }
        }
        listGeo = ExtractMultiGeoDocumentFromListURL(urls2,TABLE_INPUT,TABLE_OUTPUT);
        return listGeo;
    }

    private List<GeoDocument> ExtractMultiGeoDocumentFromListURL(List<URL> urls,String TABLE_INPUT,String TABLE_OUTPUT){
        List<GeoDocument> listGeo = new ArrayList<>();
        for (URL url : urls) {
            GeoDocument geoDoc = ExtractGeoDocumentFromWeb(url,TABLE_INPUT,TABLE_OUTPUT);
            listGeo.add(geoDoc);
        }
        return listGeo;
    }

    private GeoDocument ExtractGeoDocumentFromWeb(URL url,String TABLE_INPUT,String TABLE_OUTPUT){
        ExtractorInfoGATE egate = new ExtractorInfoGATE();
        ExtractorGeoDocumentSupport egs = new ExtractorGeoDocumentSupport();
        indGDoc = 0;
        IGeoDocumentDao geoDocumentDao = new GeoDocumentDaoImpl();
        geoDocumentDao.setTableInsert(TABLE_OUTPUT);
        geoDocumentDao.setTableSelect(TABLE_OUTPUT);
        geoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE, USER, PASS, DB_OUTPUT);
        GeoDocument geo2 = new GeoDocument();
        GeoDocument geoDoc = new GeoDocument();
        SystemLog.message("(" + indGDoc + ")URL:" + url);
        indGDoc++;
        SystemLog.message("*******************Run JSOUP**************************");
        ExtractorJSOUP j = new ExtractorJSOUP();
        try {
            geo2 = j.GetTitleAndHeadingTags(url.toString(), geo2);
            if (ExtractorJSOUP.isEXIST_WEBPAGE()) {
                try {
                    SystemLog.message("*******************Run GATE**************************");
                    geoDoc = egate.extractorGATE(url, controller);
                    geoDoc = ExtractorGeoDocumentSupport.compareInfo3(geoDoc, geo2);
                    //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
                    geoDoc = egs.UpgradeTheDocumentWithOtherInfo(geoDoc);
                    geoDoc = egs.pulisciDiNuovoGeoDocument(geoDoc);
                } catch (InterruptedException | InvocationTargetException | SQLException | URISyntaxException e) {
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
    
    private void triplifyGeoDocument(String TABLE_INPUT,String TABLE_OUTPUT,String OUTPUT_FORMAT){
        
        SystemLog.message("RUN ONTOLOGY PROGRAMM: Create Table of infodocument from a geodocument/geodomaindocuemnt table!");
        IInfoDocumentDao infoDocumentDao = new InfoDocumentDaoImpl();
        infoDocumentDao.setTableInsert(TABLE_OUTPUT);
        infoDocumentDao.setTableSelect(TABLE_INPUT);
        infoDocumentDao.setDriverManager(DRIVER_DATABASE, DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE, USER, PASS, DB_OUTPUT);
        if (true) {
            try {
                infoDocumentDao.create(true);
            } catch (Exception ex) {
                SystemLog.exception(ex);
            }
        }              
            
        //GENERIAMO IL FILE DI TRIPLE CORRISPONDENTE ALLE INFORMAZIONI ESTRATTE CON KARMA
        SystemLog.message("RUN KARMA PROGRAMM: Generation of triple with org.p4535992.mvc.webapp-karma!!");
        GenerationOfTriple got = new GenerationOfTriple(
                "DB",//DB
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
}
