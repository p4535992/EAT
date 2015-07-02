package com.github.p4535992.extractor.karma;
import com.github.p4535992.util.collection.CollectionKit;
import com.github.p4535992.util.encoding.EncodingUtil;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import edu.isi.karma.kr2rml.URIFormatter;
import edu.isi.karma.kr2rml.mapping.R2RMLMappingIdentifier;
import edu.isi.karma.kr2rml.writer.N3KR2RMLRDFWriter;
import edu.isi.karma.rdf.GenericRDFGenerator;
import edu.isi.karma.rdf.RDFGeneratorRequest;
import edu.isi.karma.webserver.KarmaException;

import java.io.*;
import java.util.List;

import static edu.isi.karma.rdf.GenericRDFGenerator.*;

/**
 * Class for call the Karma method from the module maven.
 * @author 4535992.
 * @version 2015-07-01.
 */
@SuppressWarnings("unused")
public class GenerationOfTriple {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenerationOfTriple.class);
    private String MODEL_TURTLE_KARMA, SOURCETYPE_KARMA,TRIPLE_OUTPUT_KARMA,DBTYPE_KARMA,HOSTNAME_KARMA
                    ,USERNAME_KARMA,PASSWORD_KARMA,PORTNUMBER_KARMA,DBNAME_KARMA,TABLENAME_KARMA;
    //private String OUTPUT_FORMAT_KARMA;
    //private String KARMA_HOME;

    public GenerationOfTriple(
            String SOURCETYPE_KARMA,String MODEL_TURTLE_KARMA,String TRIPLE_OUTPUT_KARMA,
            String DBTYPE_KARMA,String HOSTNAME_KARMA,String USERNAME_KARMA,String PASSWORD_KARMA,
            String PORTNUMBER_KARMA,String DBNAME_KARMA,String TABLENAME_KARMA,String OUTPUT_FORMAT_KARMA,
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
        //this.OUTPUT_FORMAT_KARMA = OUTPUT_FORMAT_KARMA;
        //this.KARMA_HOME = KARMA_HOME;
    }

    private static GenerationOfTriple instance = null;
    protected GenerationOfTriple(){}
    public static GenerationOfTriple getInstance(){
        if(instance == null) {
            instance = new GenerationOfTriple();
        }
        return instance;
    }

    public File GenerationOfTripleWithKarmaAPIFromDataBase() throws IOException {

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
            for (String anArgs2 : args2) {
                msg += anArgs2 + " ";
            }
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

            /*JenaInfoDocument.readQueryAndCleanTripleInfoDocument(
                    FileUtil.filenameNoExt(f), //filenameInput
                    FileUtil.path(f), //filepath
                    FileUtil.filenameNoExt(f) + "-c", //fileNameOutput
                    FileUtil.extension(f), //inputFormat n3
                    OUTPUT_FORMAT_KARMA //outputFormat "ttl"
            );*/
            filePathTriple.delete();
            return f;
        }catch(Exception ex){
            SystemLog.exception(ex);
        }
        return null;
    }

    /**
     * Method to generate triple file with Web-KArma API from a local file:JSON,CSV,XML,AVRO.
     * @param karmaModel file Model R2RML of Web-Karma.
     * @param fileToTriplify file JSON,CSV,XML,AVRO to triplify in a .n3 format.
     */
    public File GenerationOfTripleWithKarmaFromAFile(File karmaModel,File fileToTriplify){
        try {
            GenericRDFGenerator rdfGenerator = new GenericRDFGenerator("DEFAULT_TEST");
            //Construct a R2RMLMappingIdentifier that provides the location of the model
            // and a name for the model and add the model to the JSONRDFGenerator.
            // You can add multiple models using this API.
            //"people-model" -> "/files/models/people-model.ttl".
            R2RMLMappingIdentifier modelIdentifier = new R2RMLMappingIdentifier(
                    FileUtil.filenameNoExt(karmaModel), karmaModel.toURI().toURL());

            // modelIdentifier : Provides a name and location of the model file
            rdfGenerator.addModel(modelIdentifier);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            N3KR2RMLRDFWriter writer = new N3KR2RMLRDFWriter(new URIFormatter(), pw);
            RDFGeneratorRequest request = new RDFGeneratorRequest(
                    FileUtil.filenameNoExt(karmaModel), //"people-model"
                    FileUtil.localPath(fileToTriplify.getAbsolutePath())); //"files/data/people.json"
            //inputData : Input Data as String
            //request.setInputData(inputData);
            //inputStream: Input data as a Stream
            //request.setInputStream(in);
            //inputFile: Input data file
            request.setInputFile(new File(FileUtil.convertFileToUri(fileToTriplify)));
            //addProvenance -> flag to indicate if provenance information should be added to the RDF
            request.setAddProvenance(true);
            //dataType: Valid values: CSV,JSON,XML,AVRO
            if(fileToTriplify.getAbsolutePath().toLowerCase().endsWith(".json")) request.setDataType(InputType.JSON);
            if(fileToTriplify.getAbsolutePath().toLowerCase().endsWith(".xml")) request.setDataType(InputType.XML);
            if(fileToTriplify.getAbsolutePath().toLowerCase().endsWith(".csv")) request.setDataType(InputType.CSV);
            if(fileToTriplify.getAbsolutePath().toLowerCase().endsWith(".csv")) request.setDataType(InputType.AVRO);
            else{
                SystemLog.error("This file:" + fileToTriplify.getAbsolutePath() +
                        " can't be triplify with Web-Karma these are the permittted format JSON.CSV,XML,AVRO");
                throw  new KarmaException("This file:" + fileToTriplify.getAbsolutePath() +
                        " can't be triplify with Web-Karma these are the permittted format JSON.CSV,XML,AVRO");
            }
            //writer -> Writer for the RDF output. This can be an N3KR2RMLRDFWriter or
            // JSONKR2RMLRDFWriter or BloomFilterKR2RMLRDFWriter
            request.addWriter(writer);
            //request : Provides all details for the Inputs to the RDF
            // Generator like the input data, setting for provenance etc
            rdfGenerator.generateRDF(request);
            SystemLog.message("Generated RDF: " + sw.toString());
            return fileToTriplify;
        } catch (KarmaException|IOException e) {
           SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Method to generate triple file with Web-KArma API from a local file:JSON,CSV,XML,AVRO.
     * @param karmaModel file Model R2RML of Web-Karma.
     * @param inStream input put like stream.
     * @param fileToTriplify file support where save the .n3 file generated.
     */
    public File GenerationOfTripleWithKarmaFromAFile(File karmaModel,InputStream inStream,File fileToTriplify){
        try {
            GenericRDFGenerator rdfGenerator = new GenericRDFGenerator("DEFAULT_TEST");
            //"people-model" -> "/files/models/people-model.ttl".
            R2RMLMappingIdentifier modelIdentifier = new R2RMLMappingIdentifier(
                    FileUtil.filenameNoExt(karmaModel), karmaModel.toURI().toURL());
            rdfGenerator.addModel(modelIdentifier);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            N3KR2RMLRDFWriter writer = new N3KR2RMLRDFWriter(new URIFormatter(), pw);
            RDFGeneratorRequest request;
            fileToTriplify = FileUtil.createFile(fileToTriplify);
            if (fileToTriplify!=null && fileToTriplify .exists()) {
                request = new RDFGeneratorRequest(
                        FileUtil.filenameNoExt(karmaModel), //"people-model"
                        FileUtil.localPath(fileToTriplify .getAbsolutePath())); //"files/data/people.json"
                request.setInputStream(inStream);
                request.setAddProvenance(true);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".json")) request.setDataType(InputType.JSON);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".xml")) request.setDataType(InputType.XML);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".csv")) request.setDataType(InputType.CSV);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".csv")) request.setDataType(InputType.AVRO);
                else{
                    SystemLog.error("This file:" + fileToTriplify.getAbsolutePath() +
                            " can't be triplify with Web-Karma these are the permittted format JSON.CSV,XML,AVRO");
                    throw  new KarmaException("This file:" + fileToTriplify.getAbsolutePath() +
                            " can't be triplify with Web-Karma these are the permittted format JSON.CSV,XML,AVRO");
                }
                request.addWriter(writer);
                rdfGenerator.generateRDF(request);
            }
            else{
                SystemLog.error("The InputStream is NULL!!!");
            }
            SystemLog.message("Generated RDF: " + sw.toString());
            return fileToTriplify;
        } catch (KarmaException|IOException e) {
            SystemLog.exception(e);
        }
        return null;
    }

    /**
     * Method to generate triple file with Web-KArma API from a local file:JSON,CSV,XML,AVRO.
     * @param karmaModel file Model R2RML of Web-Karma.
     * @param stringText input put like string.
     * @param fileToTriplify file support where save the .n3 file generated.
     */
    public File GenerationOfTripleWithKarmaFromAFile(File karmaModel,String stringText,File fileToTriplify){
        try {
            GenericRDFGenerator rdfGenerator = new GenericRDFGenerator("DEFAULT_TEST");
            //"people-model" -> "/files/models/people-model.ttl".
            R2RMLMappingIdentifier modelIdentifier = new R2RMLMappingIdentifier(
                    FileUtil.filenameNoExt(karmaModel), karmaModel.toURI().toURL());
            rdfGenerator.addModel(modelIdentifier);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            N3KR2RMLRDFWriter writer = new N3KR2RMLRDFWriter(new URIFormatter(), pw);
            RDFGeneratorRequest request;
            fileToTriplify = FileUtil.createFile(fileToTriplify);
            if (fileToTriplify!=null && fileToTriplify .exists()) {
                request = new RDFGeneratorRequest(
                        FileUtil.filenameNoExt(karmaModel), //"people-model"
                        FileUtil.localPath(fileToTriplify .getAbsolutePath())); //"files/data/people.json"
                request.setInputData(stringText);
                request.setAddProvenance(true);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".json")) request.setDataType(InputType.JSON);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".xml")) request.setDataType(InputType.XML);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".csv")) request.setDataType(InputType.CSV);
                if(fileToTriplify .getAbsolutePath().toLowerCase().endsWith(".csv")) request.setDataType(InputType.AVRO);
                else{
                    SystemLog.error("This file:" + fileToTriplify.getAbsolutePath() +
                            " can't be triplify with Web-Karma these are the permittted format JSON.CSV,XML,AVRO");
                    throw  new KarmaException("This file:" + fileToTriplify.getAbsolutePath() +
                            " can't be triplify with Web-Karma these are the permittted format JSON.CSV,XML,AVRO");
                }
                request.addWriter(writer);
                rdfGenerator.generateRDF(request);
            }
            else{
                SystemLog.error("The InputStream is NULL!!!");
            }
            SystemLog.message("Generated RDF: " + sw.toString());
            return fileToTriplify;
        } catch (KarmaException|IOException e) {
            SystemLog.exception(e);
        }
        return null;
    }
}
