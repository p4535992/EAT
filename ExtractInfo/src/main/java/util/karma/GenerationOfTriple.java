/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.karma;

import util.EncodingUtil;
import util.StringKit;
import util.SystemLog;
import util.FileUtil;
import util.cmd.ExecuteCmdAndPrintOnOutput;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static util.JenaKit.readQueryAndCleanTripleInfoDocument;

/**
 *
 * @author Marco
 */
public class GenerationOfTriple {

    private String MODEL_TURTLE_KARMA,SOURCETYPE_KARMA,TRIPLE_OUTPUT_KARMA,
            DBTYPE_KARMA,HOSTNAME_KARMA,USERNAME_KARMA,PASSWORD_KARMA,
            PORTNUMBER_KARMA,DBNAME_KARMA,TABLENAME_KARMA,OUTPUT_FORMAT_KARMA,KARMA_HOME;
    
    private ExecuteCmdAndPrintOnOutput rte = new ExecuteCmdAndPrintOnOutput();
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
    
    public String setCommandKarma(){
        //mvn exec:java -Dexec.mainClass=\"edu.isi.karma.rdf.OfflineRdfGenerator\" -Dexec.args=\" --sourcetype DB --modelfilepath \\\"%~dp0/karma_files/model/R2RML_infodocument_nadia3_ontology-model_2014-12-22.ttl\\\" --outputfile \\\"%~dp0/karma_files/output/tripla-model-java-2015-01-13.n3\\\" --dbtype MySQL --hostname localhost --username siimobility --password siimobility --portnumber 3306 --dbname geolocationdb --tablename infodocument_u4_link_test_ontology\""                     
        String cmdKarma = 
                
                "mvn exec:java -Dexec.mainClass=\"edu.isi.karma.rdf.OfflineRdfGenerator\" "
                
                + "-Dexec.args=\" "
                + "--sourcetype "+SOURCETYPE_KARMA+" " //DB
                //+ "--modelfilepath \\\"%~dp0/karma_files/model/"+MODEL_TURTLE_KARMA+"\\\" "
                //+ "--outputfile \\\"%~dp0/karma_files/output/"+TRIPLE_OUTPUT_KARMA+"\\\" "
                + "--modelfilepath \\\"%~dp0/karma_files/model/"+MODEL_TURTLE_KARMA+"\\\" "
                + "--outputfile \\\"%~dp0/karma_files/output/"+TRIPLE_OUTPUT_KARMA+"\\\" "
                //+ "--modelfilepath \\\""+System.getProperty("user.dir")+"\\karma_files\\model\\"+MODEL_TURTLE_KARMA+"\\\" "
                //+ "--outputfile \\\""+System.getProperty("user.dir")+"\\karma_files\\output\\"+TRIPLE_OUTPUT_KARMA+"\\\" "
                + "--dbtype "+DBTYPE_KARMA+" "
                + "--hostname "+HOSTNAME_KARMA+" "
                + "--username "+USERNAME_KARMA+" "
                + "--password "+PASSWORD_KARMA+" "
                + "--portnumber "+PORTNUMBER_KARMA+" "
                + "--dbname "+DBNAME_KARMA+" "
                + "--tablename "+TABLENAME_KARMA+""
                + "\"";  
        SystemLog.write("KARMA:" + cmdKarma + "", "OUT");
        return cmdKarma;
    }
    
    public String[] CreateFileOfTriple(String cmdKarma){
        ExecuteCmdAndPrintOnOutput eca = new ExecuteCmdAndPrintOnOutput();
        String CD="";
        if(KARMA_HOME == null || KARMA_HOME.toString()=="null"){
            CD="cd %~dp0\\Web-Karma-master v2.031";
        }else{
            CD="cd %"+KARMA_HOME+"%";
        }
         String[] listString = new String[]{
                FileUtil.GetCurrentDisk(), //c: 
                CD,
                //"cd "+System.getProperty("user.dir")+"\\Web-Karma-master v2.031",
                "PUSHD \".\\karma-offline\"",
                cmdKarma
         };
        //eca.RunBatchWithProcessAndRuntime("Generator_Triple_Karma.bat", listString);
        return listString;
    }
    
    
    public void GenerationOfTripleWithKarma(){     
        String cmdKarma = setCommandKarma();
        String[] listStringCmd = CreateFileOfTriple(cmdKarma);
        try {           
            rte.RunBatchWithProcessAndRuntime("karma"+GetTimeAndDate()+".bat", listStringCmd );
            
            String pathTriple = System.getProperty("user.dir")+"\\karma_files\\output\\"+TRIPLE_OUTPUT_KARMA+"";
            
            //CODIFICHIAMO IL FILE DI TRIPLE DA ASCII A UNICODE (UTF8)
            List<String> lines = new ArrayList<>();		           
            //treat as a large file - use some buffering
            SystemLog.write("Codifica Unicode per il file di triple...", "OUT");
            //**************************************************************************
            /*
            util.encoding.ConvertASCIIToUnicode text =
                    new util.encoding.ConvertASCIIToUnicode(
                            pathTriple,pathTriple+".UTF8",StandardCharsets.UTF_8);
            util.log.write("Lettura del file di triple "+pathTriple+"...","OUT");
            lines = text.readLargerTextFileWithReturn(pathTriple);
            
            String output = System.getProperty("user.dir")+"\\karma_files\\output\\"+TRIPLE_OUTPUT_KARMA.replace(".n3","-UTF8.n3");
            util.log.write("Scrittura del file di triple "+output+"...","OUT");
            text.writeLargerTextFileWithReplace2(output, lines); 
            */
            //******************************************************************
            String output =FileUtil.path(pathTriple)+"\\"+ FileUtil.filenameNoExt(pathTriple)+".UTF8."+FileUtil.extension(pathTriple);
            EncodingUtil text = new EncodingUtil();
            lines = text.UnicodeEscape2UTF8(new File(pathTriple));
            text.writeLargerTextFileWithReplace2(output,lines);
            //******************************************************************
            
            SystemLog.write("...File di triple " + output + " codificato", "OUT");
            File filePathTriple = new File(pathTriple);
            filePathTriple.delete();
            
            // filePathTriple.delete();
            File f = new File(output);
            //RIPULIAMO LETRIPLE DALLE LOCATION SENZA COORDINATE CON JENA
            SystemLog.write("Ripuliamo le triple infodocument dalle Location senza coordinate nel file:" + output, "OUT");

            readQueryAndCleanTripleInfoDocument(
                    FileUtil.filenameNoExt(f), //filenameInput
                    FileUtil.path(f), //filepath
                    FileUtil.filenameNoExt(f) + "-c", //fileNameOutput
                    FileUtil.extension(f), //inputFormat
                    OUTPUT_FORMAT_KARMA //outputFormat "ttl"
            );
            f.delete();
            
        } catch (FileNotFoundException ex) {
            SystemLog.write("KARMA:" + ex.getMessage() + "", "ERRROR");
            Logger.getLogger(GenerationOfTriple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            SystemLog.write("KARMA:" + ex.getMessage() + "", "ERRROR");
            Logger.getLogger(GenerationOfTriple.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IOException ioe){
            SystemLog.write("KARMA:" + ioe.getMessage() + "", "ERRROR");
            Logger.getLogger(GenerationOfTriple.class.getName()).log(Level.SEVERE, null,ioe);
        }
    }

    /*
    public void GenerationOfTripleWithKarmaAPIFromFile(String nameModel,String nameOutput) throws IOException, edu.isi.karma.webserver.KarmaException {
        edu.isi.karma.rdf.GenericRDFGenerator rdfGenerator = new  edu.isi.karma.rdf.GenericRDFGenerator();

        String pathTriple = System.getProperty("user.dir")+"\\karma_files\\model\\"+nameModel+"";
        String pathOut = System.getProperty("user.dir")+"\\karma_files\\"+nameOutput+"";
        //Construct a R2RMLMappingIdentifier that provides the location of
        //the model and a name for the model and add the model to the
        //JSONRDFGenerator. You can add multiple models using this API.
        edu.isi.karma.kr2rml.mapping.R2RMLMappingIdentifier modelIdentifier =
                new edu.isi.karma.kr2rml.mapping.R2RMLMappingIdentifier("model", new File(pathTriple).toURI().toURL());
        rdfGenerator.addModel(modelIdentifier);

        String filename = "files/data/people.json";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        edu.isi.karma.kr2rml.writer.N3KR2RMLRDFWriter writer = new edu.isi.karma.kr2rml.writer.N3KR2RMLRDFWriter(new edu.isi.karma.kr2rml.URIFormatter(),pw);
        edu.isi.karma.rdf.RDFGeneratorRequest request = new edu.isi.karma.rdf.RDFGeneratorRequest("model",filename);

        request.setInputFile(new File(filename));
        request.setAddProvenance(true);
        request.setDataType(GenericRDFGenerator.InputType.JSON);
        request.addWriter(writer);

        rdfGenerator.generateRDF(request);
        String rdf = sw.toString();
        System.out.println("Generated RDF: " + rdf);

        SystemLog.write("...File di triple " + output + " codificato", "OUT");
        File filePathTriple = new File(pathTriple);
        filePathTriple.delete();

        // filePathTriple.delete();
        File f = new File(output);
        //RIPULIAMO LETRIPLE DALLE LOCATION SENZA COORDINATE CON JENA
        SystemLog.write("Ripuliamo le triple infodocument dalle Location senza coordinate nel file:" + output, "OUT");

        readQueryAndCleanTripleInfoDocument(
                FileUtil.filenameNoExt(f), //filenameInput
                FileUtil.path(f), //filepath
                FileUtil.filenameNoExt(f) + "-c", //fileNameOutput
                FileUtil.extension(f), //inputFormat
                OUTPUT_FORMAT_KARMA //outputFormat "ttl"
        );
        f.delete();

    }
    */

    public void GenerationOfTripleWithKarmaAPIFromDataBase() throws IOException, edu.isi.karma.webserver.KarmaException {

        String pathModel = MODEL_TURTLE_KARMA+"";
        String pathOut = TRIPLE_OUTPUT_KARMA+"";
        String[] value = new String[]{
                SOURCETYPE_KARMA,
                pathModel,
                //"C:\\Users\\Marco\\IdeaProjects\\ExtractInfo\\karma_files\\model\\R2RML_infodocument_nadia_10_h-model_2015-03-02.ttl",
                pathOut,
                //"C:\\Users\\Marco\\Desktop\\tripla-model-2015-03-30.n3",
                DBTYPE_KARMA,
                HOSTNAME_KARMA,
                USERNAME_KARMA,
                PASSWORD_KARMA,
                PORTNUMBER_KARMA,
                DBNAME_KARMA,
                TABLENAME_KARMA,
                //"UTF-8"

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
                "--tablename",
                //"--encoding"
        };

        //Other param options
        //--modelurl --jsonoutputfile --baseuri --selection --root --killtriplemap --stoptriplemap --pomtokill
        //Other param database options
        //--encoding --queryfile
        //Other param file options
        //--filepath --maxNumLines --sourcename

        String[] args2 = new String[22];
        try {
            args2 = StringKit.mergeArraysString(param, value);
            edu.isi.karma.rdf.OfflineRdfGenerator.main(args2);
        }catch(Exception ex ){}

    }


        /**
       * Meto che scriva data e ora come un'unico numero yyyyMMdd_HHmmss
       * @return formato stringa della data e ora attuali
       */
   public static String GetTimeAndDate(){
       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
       return timeStamp;
   }
}
