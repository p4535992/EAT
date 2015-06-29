package extractor;

import com.github.p4535992.extractor.estrattori.ExtractInfoWeb;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.gatebasic.gate.gate8.ExtractorInfoGate8;
import com.github.p4535992.gatebasic.gate.gate8.GateSupport;
import gate.CorpusController;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 4535992 on 25/06/2015.
 */
public class Test_ExtractorInfoWeb {

    public static void main(String[] args) throws
            MalformedURLException, InterruptedException, SQLException, InvocationTargetException {
        //Set the Driver Manager for a MySQL Database...
        ExtractInfoWeb web = ExtractInfoWeb.getInstance(
                "com.mysql.jdbc.Driver","jdbc:mysql","localhost","3306","username","password","nameDatabase");
        CorpusController controller = (CorpusController) web.getController();
        //Class for process a document with GATE and get a result with only the st ring value
        //name Document -> name AnnotationSet -> name Annotation -> string content.
        ExtractorInfoGate8 eig8 = ExtractorInfoGate8.getInstance();
        //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
        List<String> listAnn =  new ArrayList<>(Arrays.asList("MyRegione","MyPhone","MyFax","MyEmail","MyPartitaIVA",
                "MyLocalita","MyIndirizzo","MyEdificio","MyProvincia"));
        //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
        List<String> listAnnSet = new ArrayList<>(Arrays.asList("MyFOOTER","MyHEAD","MySpecialID","MyAnnSet"));

        //METHOD 1: extract all the information given from a URL in a specific object GeoDocument,
        // there are method for works with java.io.File File or Directory
        //Store the result on of the extraction on a GateSupport Object
        GateSupport support = GateSupport.getInstance(
                eig8.extractorGATE(
                        new URL("http://www.unifi.it"),controller,"corpus_test_1",listAnn,listAnnSet,true)
        );

        //Now you can get the content from a specific document, specific AnnotationSet, specific Annotation.
        String contnet0 = support.getContent("doc1", "MyAnnSet", "MyIndirizzo"); // "P.azza Guido Monaco"
        String content1 = support.getContent(0,"MyAnnSet", "MyIndirizzo"); // "P.azza Guido Monaco"
        String content2 = support.getContent(0,0,"MyIndirizzo"); // "P.azza Guido Monaco"
        String content3 = support.getContent(0,0,0); // "P.azza Guido Monaco"
        GeoDocument geoDoc = web.convertGateSupportToGeoDocument(support,new URL("http://www.unifi.it"),0);

        //METHOD 2: and we want to save the geoDocument in a table on the database.

        String table_where_insert ="test_55";
        String table_where_select= "test_55";
        // in this case the "select" table is use for avoid the insert of duplicate
        GeoDocument geoDoc2 =
                web.ExtractGeoDocumentFromUrl(new URL("http://www.unifi.it"),table_where_select,table_where_insert,true);

        //METHOD 3: Convert a relational table in a file of triple with a Web-Karma Model:
        String output_format ="ttl";
        String output_karma_file ="c:\\path\\fileOfTriple.n3"; //always given the input in n3 format.
        String karma_model ="c:\\path\\model_R2RML.ttl";
        //This piece of code save a file of triple in a file...
        web.triplifyGeoDocument(table_where_select,table_where_select,output_format, karma_model,output_karma_file,true);

    }
}
