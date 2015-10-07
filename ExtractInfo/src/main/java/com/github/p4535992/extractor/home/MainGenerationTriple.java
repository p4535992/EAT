package com.github.p4535992.extractor.home;

import com.github.p4535992.extractor.JenaInfoDocument;
import com.github.p4535992.extractor.karma.GenerationOfTriple;
import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Marco
 */
public class MainGenerationTriple {

    public static void main(String[] args) throws IOException{
        SystemLog.setIsPRINT(false);
        SystemLog.setIsLogOff(true);
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

        try {
            //String[] args2 = CollectionKit.mergeArraysForCommandInput(param, value);
            //edu.isi.karma.rdf.OfflineRdfGenerator.main(args2);
            GenerationOfTriple got = GenerationOfTriple.getInstance();
            File f= got.GenerationOfTripleWithKarmaAPIFromDataBase(
                    value[0],value[1],value[2],value[3],value[4],value[5],value[6],value[7],value[8],value[9]
            );
            try {
                //code for clean up the triple file generate from karma.
                JenaInfoDocument.readQueryAndCleanTripleInfoDocument(
                        FileUtil.filenameNoExt(f), //filenameInput
                        FileUtil.path(f), //filepath
                        FileUtil.filenameNoExt(f) + "-c", //fileNameOutput
                        FileUtil.extension(f), //inputFormat n3
                        "ttl" //outputFormat "ttl"
                );
                //delete not filter file of triples
                f.delete();
            } catch (IOException ex) {
                SystemLog.exception(ex);
            }
        }catch(Exception ex ){
            SystemLog.exception(ex);
        }
    }//main

}
