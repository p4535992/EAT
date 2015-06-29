/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.p4535992.extractor.karma;
import com.github.p4535992.extractor.JenaInfoDocument;
import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.encoding.EncodingUtil;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.cmd.PrepareCommand;
import com.github.p4535992.util.string.StringKit;

import java.io.*;
import java.util.List;
/**
 * Class for calll the Karma method from the module maven
 * @author 4535992
 */
public class GenerationOfTriple {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenerationOfTriple.class);
    private String MODEL_TURTLE_KARMA,SOURCETYPE_KARMA,TRIPLE_OUTPUT_KARMA,
            DBTYPE_KARMA,HOSTNAME_KARMA,USERNAME_KARMA,PASSWORD_KARMA,
            PORTNUMBER_KARMA,DBNAME_KARMA,TABLENAME_KARMA,OUTPUT_FORMAT_KARMA,KARMA_HOME;
 
    private PrepareCommand rte = new PrepareCommand();
    public GenerationOfTriple(
            String SOURCETYPE_KARMA,
            String MODEL_TURTLE_KARMA,
            String TRIPLE_OUTPUT_KARMA,
            String DBTYPE_KARMA,
            String HOSTNAME_KARMA,
            String USERNAME_KARMA,
            String PASSWORD_KARMA,
            String PORTNUMBER_KARMA,
            String DBNAME_KARMA,
            String TABLENAME_KARMA,
            String OUTPUT_FORMAT_KARMA,
            String KARMA_HOME
            ){
        this.SOURCETYPE_KARMA = SOURCETYPE_KARMA;
        this.MODEL_TURTLE_KARMA = MODEL_TURTLE_KARMA;
        this.TRIPLE_OUTPUT_KARMA = TRIPLE_OUTPUT_KARMA;
        this.DBTYPE_KARMA = DBTYPE_KARMA;
        this.HOSTNAME_KARMA = HOSTNAME_KARMA;
        this.USERNAME_KARMA = USERNAME_KARMA;
        this.PASSWORD_KARMA = PASSWORD_KARMA;
        this.PORTNUMBER_KARMA = PORTNUMBER_KARMA;
        this.DBNAME_KARMA = DBNAME_KARMA;
        this.TABLENAME_KARMA =TABLENAME_KARMA;
        this.OUTPUT_FORMAT_KARMA = OUTPUT_FORMAT_KARMA;      
        this.KARMA_HOME = KARMA_HOME;
    }  
    public GenerationOfTriple(){}

    public void GenerationOfTripleWithKarmaAPIFromDataBase() throws IOException {

        String pathModel = MODEL_TURTLE_KARMA+"";
        String pathOut = TRIPLE_OUTPUT_KARMA+"";
        String[] value = new String[]{
                SOURCETYPE_KARMA,pathModel,pathOut,DBTYPE_KARMA,HOSTNAME_KARMA,USERNAME_KARMA,PASSWORD_KARMA,
                PORTNUMBER_KARMA,DBNAME_KARMA,TABLENAME_KARMA
                //"UTF-8"

        };
        String[] param = new String[]{
                "--sourcetype", "--modelfilepath","--outputfile", "--dbtype","--hostname",
                "--username","--password","--portnumber","--dbname","--tablename"
                //"--encoding"
        };

        //Other param options
        //--modelurl --jsonoutputfile --baseuri --selection --root --killtriplemap --stoptriplemap --pomtokill
        //Other param database options
        //--encoding --queryfile
        //Other param file options
        //--filepath --maxNumLines --sourcename

        String[] args2;
        try {
            args2 = CollectionKit.mergeArraysForCommandInput(param, value);
            String msg ="";
            for(int i = 0; i < args2.length; i++){ msg+=args2[i]+" ";}
            SystemLog.message("PARAM KARMA:"+ msg);
            SystemLog.message("try to create a file of triples from a relational table with karma...");
            edu.isi.karma.rdf.OfflineRdfGenerator.main(args2);

            SystemLog.message("...file of triples created in the path:" + pathOut);

            String output = System.getProperty("user.dir")+File.separator+"karma_files"+File.separator+"output"+File.separator+FileUtil.filename(TRIPLE_OUTPUT_KARMA)
                    .replace("."+FileUtil.extension(TRIPLE_OUTPUT_KARMA), "-UTF8."+FileUtil.extension(TRIPLE_OUTPUT_KARMA));


            File filePathTriple = new File(pathOut);
            List<String> lines = EncodingUtil.convertUnicodeEscapeToUTF8(filePathTriple);
            EncodingUtil.writeLargerTextFileWithReplace2(output, lines);


            //TEST
            //String output = "C:\\Users\\Marco\\Documents\\GitHub\\EAT\\karma_files\\output\\\\output_karma_2015-04-UTF8.n3";

            File f = new File(output);
            //RIPULIAMO LETRIPLE DALLE LOCATION SENZA COORDINATE CON JENA
            SystemLog.message("Re-clean infodocument triples from the Location information  without coordinates from the file:" + output);
            SystemLog.message(FileUtil.filenameNoExt(f)+","+FileUtil.path(f)+","+FileUtil.filenameNoExt(f) + "-c" + "," + FileUtil.extension(f));
           
            JenaInfoDocument.readQueryAndCleanTripleInfoDocument(
                    FileUtil.filenameNoExt(f), //filenameInput
                    FileUtil.path(f), //filepath
                    FileUtil.filenameNoExt(f) + "-c", //fileNameOutput
                    FileUtil.extension(f), //inputFormat n3
                    OUTPUT_FORMAT_KARMA //outputFormat "ttl"
            );
            //delete not filter file of triples
            f.delete();
            filePathTriple.delete();


        }catch(Exception ex){
            SystemLog.exception(ex);
        }



    }
}
