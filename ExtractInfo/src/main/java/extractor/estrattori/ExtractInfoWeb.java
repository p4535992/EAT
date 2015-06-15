package extractor.estrattori;

import com.p4535992.util.log.SystemLog;
import extractor.ManageJsonWithGoogleMaps;
import extractor.gate.GateKit;
import extractor.karma.GenerationOfTriple;
import gate.CorpusController;
import object.dao.jdbc.IGeoDocumentDao;
import object.impl.jdbc.GeoDocumentDaoImpl;
import object.model.GeoDocument;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 15/06/2015.
 */
public class ExtractInfoWeb {

    private static int indGDoc;

    private static CorpusController controller;

    public ExtractInfoWeb(){}
    private String TABLE_OUTPUT,DRIVER_DATABASE,DIALECT_DATABASE, HOST_DATABASE, PORT_DATABASE, USER, PASS, DB_OUTPUT;

    public ExtractInfoWeb(String TABLE_OUTPUT,String DRIVER_DATABASE,String DIALECT_DATABASE,String HOST_DATABASE,
                          String PORT_DATABASE,String USER,String PASS,String DB_OUTPUT){
        this.TABLE_OUTPUT = TABLE_OUTPUT;
        this.DRIVER_DATABASE = DRIVER_DATABASE;
        this.DIALECT_DATABASE = DIALECT_DATABASE;
        this. HOST_DATABASE = HOST_DATABASE;
        this.PORT_DATABASE = PORT_DATABASE;
        this.USER = USER;
        this.PASS = PASS;
        this.DB_OUTPUT=DB_OUTPUT;
        GateKit.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session");
        GateKit.loadGapp("custom/gapp", "geoLocationPipeline06102014v7_fastMode.xgapp");
        controller = GateKit.getController();
        GeoDocument geo3;
        GeoDocument geo2 = new GeoDocument();
    }

    private GeoDocument ExtractGeoDocumentFromWeb(String url){
        GeoDocument geoDoc = new GeoDocument();
        try {
            if (!url.matches("^(https?|ftp)://.*$")) {
                url = "http://" + url;
            }
            geoDoc = ExtractGeoDocumentFromWeb(new URL(url));
        }catch(MalformedURLException e){
            SystemLog.error("You have isert a nota valid url for the extraction of information:"+url);
        }
        return geoDoc;
    }

    private List<GeoDocument> ExtractMultiGeoDocumentFromWeb(List<String> urls){
        List<URL> urls2 = new ArrayList<>();
        List<GeoDocument> listGeo;
        for(int i = 0; i < urls.size(); i++) {
            try{
                if (!urls.get(i).matches("^(https?|ftp)://.*$")) {
                    urls2.set(i,new URL("http://" + urls.get(i)));
                }
            }catch(MalformedURLException e){
                SystemLog.error("You have insert a not valid url for the extraction of information:"+"http://" + urls.get(i));
                continue;
            }
        }
        listGeo = ExtractGeoDocumentFromWeb(urls2);
        return listGeo;
    }

    private List<GeoDocument> ExtractGeoDocumentFromWeb(List<URL> urls){
        List<GeoDocument> listGeo = new ArrayList<>();
        for(int i = 0; i < urls.size(); i++) {
            GeoDocument geoDoc = ExtractGeoDocumentFromWeb(urls.get(i));
            listGeo.add(geoDoc);
        }
        return listGeo;
    }

    private GeoDocument ExtractGeoDocumentFromWeb(URL url){
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
                    geoDoc = egs.compareInfo3(geoDoc, geo2);
                    //AGGIUNGIAMO ALTRE INFORMAZIONI AL GEODOCUMENT
                    geoDoc = egs.UpgradeTheDocumentWithOtherInfo(geoDoc);
                    geoDoc = egs.pulisciDiNuovoGeoDocument(geoDoc);
                } catch (Exception e) {
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

}
