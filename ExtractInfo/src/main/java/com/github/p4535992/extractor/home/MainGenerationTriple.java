package com.github.p4535992.extractor.home;

import com.github.p4535992.extractor.estrattori.JenaInfoDocument;

import com.github.p4535992.extractor.estrattori.karma.GenerationRDFSupport;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.log.logback.LogBackUtil;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Marco
 */
public class MainGenerationTriple {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(MainGenerationTriple.class);

    public static void main(String[] args) throws IOException{

        LogBackUtil.console();
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
                "C:\\Users\\tenti\\Documents\\GitHub\\EAT\\karma_files\\model\\R2RML_infodocument-model_2015-07-08.ttl",
                "C:\\Users\\tenti\\Desktop\\tripla-model-2015-09-19.n3",
                "MySQL",
                "localhost",
                "siimobility",
                "siimobility",
                "3306",
                "geodb",
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

        try {
            //String[] args2 = CollectionKit.mergeArraysForCommandInput(param, value);
            //edu.isi.karma.rdf.OfflineRdfGenerator.main(args2);
            /*GenerationOfTriple got = GenerationOfTriple.getInstance();
            File f= got.GenerationOfTripleWithKarmaAPIFromDataBase(
                    value[0],value[1],value[2],value[3],value[4],value[5],value[6],value[7],value[8],value[9]
            );*/

            File f = GenerationRDFSupport.getInstance()
                    .generateRDF(value[0],value[1],value[2],value[3],value[4],value[5],value[6],value[7],value[8],value[9]

                    );
            try {
                //code for clean up the triple file generate from karma.
                JenaInfoDocument.readQueryAndCleanTripleInfoDocument(
                        FileUtilities.getFilenameWithoutExt(f), //filenameInput
                        FileUtilities.getPath(f), //filepath
                        FileUtilities.getFilenameWithoutExt(f) + "-c", //fileNameOutput
                        FileUtilities.getExtension(f), //inputFormat n3
                        "ttl" //outputFormat "ttl"
                );
                //delete not filter file of triples
                f.delete();
            } catch (IOException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }catch(Exception ex ){
            logger.error(ex.getMessage(), ex);
        }
    }//main

}
