import util.string.StringKit;

import java.io.IOException;

/**
 *
 * @author Marco
 */
public class Test_Karma_EAT {


    //TEST

    public static void main(String[] args) throws IOException
    // , edu.isi.karma.webserver.KarmaException
    {
        //ExecuteCmdAndPrintOnOutput rte = new ExecuteCmdAndPrintOnOutput();
        /*
        GenerationOfTriple got = new GenerationOfTriple(
                "DB",
                "R2RML_infodocument_nadia3_ontology-model_2014-12-22.ttl",
                "triple_output_"+"test"+".n3",
                "MySQL",
                "localhost",
                "siimobility",
                "siimobility",
                "3306",
                "geolocationdb", 
                "infodocument_u4_link_test_ontology",               
                "ttl",
                //"c:",
                null
        );
        
        String cmdKarma = got.setCommandKarma();
        String[] listStringCmd = got.CreateFileOfTriple(cmdKarma);
        rte.RunBatchWithProcessAndRuntime("test.bat", listStringCmd );
        */

        //GenerationOfTriple got = new GenerationOfTriple();

        String[] value = new String[]{
                "DB",
                "C:\\Users\\Marco\\IdeaProjects\\ExtractInfo\\karma_files\\model\\R2RML_infodocument_nadia_10_h-model_2015-03-02.ttl",
                "C:\\Users\\Marco\\Desktop\\tripla-model-2015-03-30.n3",
                "MySQL",
                "localhost",
                "siimobility",
                "siimobility",
                "3306",
                "geolocationdb",
                "infodocument_nadia_10_h"

        };
        String[] param = new String[]{
                "--sourcetype",
                "--modelfilepath",
                "--outputfile",
                "--dbtype",
                "--hostname",
                "--username",
                "--password",
                "--portnumber",
                "--dbname",
                "--tablename"
        };

        //Other param options
        //--modelurl
        //--jsonoutputfile
        //--baseuri
        //--selection
        //--root
        //--killtriplemap
        //--stoptriplemap
        //--pomtokill

        //Other param database options
        //--encoding
        //--queryfile

        //Other param file options
        //--filepath
        //--maxNumLines
        //--sourcename


        String[] args2 = new String[20];
        try {
            args2 = StringKit.mergeArraysString(param,value);
            /*
            int j = 0;

            for (int i = 0; i < 10; i++) {
                if(i == 0)
                    j = j + i;
                else
                    j = j + 1;

                args2[j] = param[i];
                j = j + 1;
                args2[j] = value[i];
            }
            */
            //geolocationdb.infodocument_nadia_10_h
            //edu.isi.karma.rdf.OfflineRdfGenerator

            edu.isi.karma.rdf.OfflineRdfGenerator.main(args2);
        }catch(Exception ex ){}
    }//main

}
