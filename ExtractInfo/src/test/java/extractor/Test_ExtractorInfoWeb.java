package extractor;

import com.github.p4535992.extractor.estrattori.ExtractInfoWeb;
import com.github.p4535992.extractor.estrattori.ExtractorInfoGate8;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.gatebasic.gate.gate8.GateSupport;
import gate.CorpusController;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marco on 25/06/2015.
 */
public class Test_ExtractorInfoWeb {

    public static void main(String[] args) throws
            MalformedURLException, InterruptedException, SQLException, InvocationTargetException {
        ExtractInfoWeb web = new ExtractInfoWeb(
                "com.sql.jdbc.Driver",
                "jdbc:sql",
                "localhost",
                "3306",
                "siimobility",
                "siimobility",
                "geolocationddb");

        CorpusController controller = (CorpusController) web.getController();
        //Class for process a document with GATE and get a result with only the st ring value
        //name Document -> name AnnotationSet -> name Annotation -> string content.
        ExtractorInfoGate8 eig8 = ExtractorInfoGate8.getInstance();
        //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
        String[] anntotations = new String[]{"MyRegione","MyPhone","MyFax","MyEmail","MyPartitaIVA",
                "MyLocalita","MyIndirizzo","MyEdificio","MyProvincia"};
        List<String> listAnn = Arrays.asList(anntotations);
        //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
        String[] anntotationsSet = new String[]{"MyFOOTER","MyHEAD","MySpecialID","MyAnnSet"};
        List<String> listAnnSet = Arrays.asList(anntotationsSet);
        //Store the result on of the extraction on a GateSupport Object
        GateSupport support = GateSupport.getInstance(
                eig8.extractorGATE(
                        new URL("http://www.unifi.it"),controller,"corpus_test_1",listAnn,listAnnSet,true)
        );
        //Now you can get the content from a specific document, specific AnnotationSet, specific Annotation.
        String contnet0 = support.getContent("doc1", "MyAnnSet", "MyIndirizzo");
        String content1 = support.getContent(0,"MyAnnSet", "MyIndirizzo");
        String content2 = support.getContent(0,0,"MyIndirizzo");
        String content3 = support.getContent(0,0,0);

        GeoDocument geoDoc = web.convertGateSupportToGeoDocument(support,"http://www.unifi.it",0);

    }
}
