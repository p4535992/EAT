package util;

import extractor.FileUtil;
import gate.CorpusController;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;
import object.model.GeoDocument;
import extractor.estrattori.ExtractorInfoGATE;
import extractor.gate.GateKit;

import java.io.File;
import java.util.List;

public class Test_FileTest extends TestCase {
    //TEST

    /** Create the test case
     *  @param testName name of the test case
     */
    public Test_FileTest( String testName ) { super( testName ); }

    /** @return the suite of tests being tested */
    public static Test suite() {
        return new TestSuite(
                Test_FileTest.class
        );
    }

    /**  Rigourous Test :-) */
    public void testApp() { assertTrue( true ); }


    public static void main(String[] args) throws Exception {
        List<File> files = files = FileUtil.readDirectory("C:\\Users\\Marco\\Downloads\\parseWebUrls");
        GateKit.setUpGateEmbedded("gate_files", "plugins", "gate.xml", "user-gate.xml", "gate.session");
        GateKit.loadGapp("custom/gapp", "geoLocationPipeline06102014v7_fastMode.xgapp");
        CorpusController controller = GateKit.getController();
        Integer i = 0;
        ExtractorInfoGATE gate = new ExtractorInfoGATE();
        List<GeoDocument> geoList = gate.extractMicrodataWithGATEMultipleFiles(files, controller);
//            GeoDocument g =
//                    gate.extractorGATE(FileUtil.convertFileToUri(file).toURL(), controller, i);

    }



}