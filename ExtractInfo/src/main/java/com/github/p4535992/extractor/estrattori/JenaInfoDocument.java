package com.github.p4535992.extractor.estrattori;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.repositoryRDF.jena.Jena3Utilities;
import com.github.p4535992.util.string.StringUtilities;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.ResourceImpl;
import com.hp.hpl.jena.util.FileManager;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
/*import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;*/

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * Created by 4535992 on 20/04/2015.
 * @author 4535992.
 * @version 2015-07-03.
 */
@SuppressWarnings("unused")
public class JenaInfoDocument {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(JenaInfoDocument.class);

    protected JenaInfoDocument(){}
    private static JenaInfoDocument instance = null;
    public static JenaInfoDocument getInstance(){
        if(instance == null) {
            instance = new JenaInfoDocument();
        }
        return instance;
    }

    //Get all service KM4c information
    private static final String SPARQL_KM4C_SERVICE = "CONSTRUCT {?service ?p ?o.}\n" +
            "WHERE {\n" +
            "?service a  <http://www.disit.org/km4city/schema#Service>;\n" +
            "         ?p ?o .    \n" +
            "}";

    private static final String SPARQL_GOODRELATIONS_BUSINESS = "CONSTRUCT {?business ?p ?o }\n" +
            "WHERE { \n" +
            "   ?business <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#BusinessEntity>;\n" +
            "               ?p ?o .\n" +
            "   OPTIONAL{?business <http://purl.org/goodrelations/v1#hasPOS> ?location}\n" +
            "   OPTIONAL{?location <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?o}\n" +
            "   OPTIONAL{?location<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?o}\n" +
            "}";

    //Get all the triples on the model without the predicate schema:latitude and schema:longitude
    private static final String SPARQL_NO_SCHEMACOORDS =
              "CONSTRUCT {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location>  }"
            + " WHERE { "
            + " ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ."
            + " OPTIONAL{?s <http://schema.org/latitude> ?o .}"
            + " OPTIONAL{?s <http://schema.org/longitude> ?o .}"
            + " FILTER (!bound(?o))"
            + "}";

    //Get all the triples on the model without the predicate geo:lat and geo:long
    private static final String SPARQL_NO_WGS84COORDS =
              "CONSTRUCT {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location>  }"
            + " WHERE { "
            + " ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ."
            + " OPTIONAL{?s <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?o .}"
            + " OPTIONAL{?s <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?o .}"
            + " FILTER (!bound(?o))"
            + "}";

    //Get all <http://purl.org/goodrelations/v1#Location> with schema:latitude and schema:longitude not null
    private static final String SPARQL_SCHEMACOORDS =
            "SELECT ?location ?lat ?long "
            + " WHERE { "
            + " ?location <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ;"
            + "  <http://schema.org/latitude> ?lat ;"
            + "  <http://schema.org/longitude> ?long ."
            + "}";
    //Get all <http://purl.org/goodrelations/v1#Location> with geo:lat and geo:long not null
    private static final String SPARQL_WGS84COORDS =
            "SELECT ?location ?lat ?long "
            + " WHERE { "
            + " ?location <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ;"
            + "  <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat ;"
            + "  <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long ."
            + "}";


    /**
     * Method for read,query and clean a specific set of triple.
     * @param filenameInput name of the file of input.
     * @param filepath file path to the folder containt the fileNameInput.
     * @param fileNameOutput name of the file output.
     * @param inputFormat input format of the triples.
     * @param outputFormat output format for the triples.
     * @throws IOException error.
     */
    public static void readQueryAndCleanTripleInfoDocument(
            String filenameInput,String filepath,String fileNameOutput,String inputFormat,String outputFormat)
            throws IOException{
        //Crea la tua query SPARQL

        logger.info(SPARQL_NO_WGS84COORDS);
        //CREA IL TUO MODELLO DI JENA A PARTIRE DA UN FILE
        //*********************************************************************************************
        //With 3.0.1
        //Model model = Jena3Utilities.toModel(filenameInput, filepath, inputFormat);
        //With 2.11
        Model model = read(new File(filepath+File.separator+filenameInput+"."+inputFormat),inputFormat);
        //*********************************************************************************************
        //ESEGUI LA QUERY SPARQL
        //*********************************************************************************************
        //With 3.0.1
        //Model myGraph = Jena3Utilities.execSparqlConstructorOnModel(SPARQL_NO_WGS84COORDS, model);
        //With 2.11
        Model myGraph = ModelFactory.createDefaultModel();
        Query query = QueryFactory.create(SPARQL_NO_WGS84COORDS);
        Model resultModel ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        myGraph = qexec.execConstruct();
        //*********************************************************************************************
        logger.info("Exec query CONSTRUCT SPARQL :" + SPARQL_NO_WGS84COORDS);
        StmtIterator iter = myGraph.listStatements();
        while (iter.hasNext()) {
            try{
                Statement stmt  = iter.nextStatement();  // get next statement
                model.remove(stmt);
                logger.info("REMOVE 1:<" + stmt.getSubject() + "> <" + stmt.getPredicate() + "> <" + stmt.getObject() + ">.");
                //com.hp.hpl.jena.rdf.model.Resource  subject   = stmt.getSubject();     // get the subject
                RDFNode object2  = stmt.getSubject();      // get the object
                Resource subject2 =
                        new ResourceImpl(object2.toString().replace("Location_",""));
                Property  predicate2 =
                        new PropertyImpl("http://purl.org/goodrelations/v1#hasPOS");   // get the predicate

                //model.remove(subject2,predicate2,object2);
                model.removeAll(null,predicate2,object2);
                logger.info("REMOVE 2:<" + subject2 + "> <" + predicate2 + "> <" + object2 + ">.");
            }catch(Exception e){
                logger.error(e.getMessage(),e);
            }
            //OPPURE
        }
        //RISCRIVIAMO GLI STATEMENT
        Model model2 = ModelFactory.createDefaultModel();

        //stmt.getPredicate().asResource().getURI().toLowerCase()
        //.contains("http://schema.org/latitude")||
        //stmt.getPredicate().asResource().getURI().toLowerCase()
        //.contains("http://schema.org/longitude")

        StmtIterator stats = model.listStatements();
        while (stats.hasNext()) {
            Statement stmt = stats.next();
            RDFNode x = stmt.getObject();
            if (x.isLiteral()) { //..if is a literal
                //x.asLiteral().getDatatype().equals(stringToXSDDatatypeToString("string"))){
                if(x.asLiteral().getDatatype().equals(XSDDatatype.XSDstring)){
                    //SystemLog.sparql("MODIFY STRING LITERAL:" + x.asLiteral().getLexicalForm());
                    model2.add(stmt.getSubject(),
                            stmt.getPredicate(),
                            ResourceFactory.createPlainLiteral(x.asLiteral().getLexicalForm() //...not print the ^^XMLString
                            ));
                }else{ //...if is a literal but is datatype is not string...
                    model2.add(
                            stmt.getSubject(),
                            stmt.getPredicate(),
                            ResourceFactory.createTypedLiteral(
                                    stmt.getObject().asLiteral().toString().replace("^^" + stmt.getObject().asLiteral().getDatatype().getURI(), ""),
                                    stmt.getObject().asLiteral().getDatatype()
                            )
                    );
                }
            //...if is not a litreal just add the uri to the model
            }else if (x.isURIResource()) {
                model2.add(stmt);
            }
        }//while
        model.close();
        /*
        com.hp.hpl.jena.rdf.model.StmtIterator stats = model.listStatements();
        while (stats.hasNext()) {
            com.hp.hpl.jena.rdf.model.Statement stmt = stats.next();
            Triple triple = stmt.asTriple();
            Node object = triple.getObject();
            if (object.isLiteral()) {
                    Literal literal = ResourceFactory.createPlainLiteral(object.getLiteralLexicalForm());
                    triple = new Triple(triple.getSubject(), triple.getPredicate(), literal.asNode());
                    com.hp.hpl.jena.rdf.model.Statement s =
                            ResourceFactory.toStatement(
                                    stmt.getSubject(),stmt.getPredicate(),literal);
                    model2.add(s);
            }
        }
        */
        //****************************************************************************
        //TEST HELPER FOR SILK GENERATE ADDITIONAL FILE
        //****************************************************************************
        /*String outputN3Knime = filepath + File.separator + "fileN3Knime.n3";
        Jena2Kit.writeModelToFile(outputN3Knime, model2, "n3");
        String outputTurtleKnime = filepath + File.separator + "fileTurtleKnimem.ttl";
        Jena2Kit.writeModelToFile(outputTurtleKnime, model2, "ttl");
        List<String> lines = EncodingUtil.convertUnicodeEscapeToUTF8(new File(outputN3Knime));
        EncodingUtil.writeLargerTextFileWithReplace2(outputN3Knime, lines);
        Jena2Kit.convertTo(new File(outputN3Knime),"csv");*/
        //*************************************************************************************

        //*************************************************************************************
        //Execute the SPARQL_WSG84COORDS and add the geometry statement
        //*************************************************************************************
        /*
        com.hp.hpl.jena.query.ResultSet result = Jena2Kit.execSparqlSelectOnModel(SPARQL_WGS84COORDS, model2);
        Map<com.hp.hpl.jena.rdf.model.Resource, String[]> map = new HashMap<>();
        while (result.hasNext()){
            com.hp.hpl.jena.query.QuerySolution row = result.next();
            String[] array = new String[2];
            array[0] = row.getLiteral("lat").getLexicalForm().replace("^^http://www.w3.org/2001/XMLSchema#float","");
            array[1] =  row.getLiteral("long").getLexicalForm().replace("^^http://www.w3.org/2001/XMLSchema#float","");
            map.put(row.getResource("location"), array);
        }
        //add new statement to the model
        for(Map.Entry<com.hp.hpl.jena.rdf.model.Resource, String[]> entry : map.entrySet()) {
            com.hp.hpl.jena.rdf.model.Resource subject = entry.getKey(); //...gr:Location
            com.hp.hpl.jena.rdf.model.Property predicate = new com.hp.hpl.jena.rdf.model.impl.PropertyImpl("http://www.w3.org/2003/01/geo/wgs84_pos#geometry");
            String value =entry.getValue()[0] + " " +  entry.getValue()[1];
            model2.addLiteral(subject, predicate,Jena2Kit.lt(value,com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDstring));
        }
        */

        //*************************************************************************************
        //Execute the SPARQL_WSG84COORDS and add the geometry statement without modification
        //*************************************************************************************
        String output = filepath + File.separator + fileNameOutput + "." + outputFormat;
        //*********************************************************************************************
        //With 3.0.1
        //Jena3Utilities.writeModelToFile(output, model2, outputFormat);
        //With 2.11
        write(new File(output),model2,Lang.TURTLE);
        //*********************************************************************************************
    }

    private static boolean write(File file, Model model, Lang outputFormat) {
        try {
            logger.info("Try to write the new file of triple from:" + file.getAbsolutePath() + "...");
            FileOutputStream outputStream = new FileOutputStream(file);
            model.write(outputStream, outputFormat.getName());
            logger.info("... the file of triple to:" + file.getAbsolutePath() + " is been wrote!");
            return true;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private static Model read(File file,String inputFormat){
        if(FileUtilities.isFileExists(file)) {
            Model m = ModelFactory.createDefaultModel();
            try {
                RDFDataMgr.read(m, file.toURI().toString());
            } catch (Exception e2) {
                try {
                    //If you are just opening the stream from a file (or URL) then Apache Jena
                    RDFDataMgr.read(m, file.toURI().toString(), Lang.N3);
                } catch (Exception e) {
                    logger.error("Failed read the file of triples from the path:" +
                            file.getAbsolutePath() + ":" + e.getMessage(), e);
                }
            }
            return m;
        }else{
            return null;
        }
    }
}
