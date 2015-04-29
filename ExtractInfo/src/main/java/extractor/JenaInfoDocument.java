package extractor;

import p4535992.util.encoding.EncodingUtil;
import p4535992.util.jena.JenaKit;
import p4535992.util.log.SystemLog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marco on 20/04/2015.
 */
public class JenaInfoDocument {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JenaInfoDocument.class);

    //Get all the triples on the model without the predicate schema:latitude and schema:longitude
    private static String SPARQL_NO_SCHEMACOORDS =
              "CONSTRUCT {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location>  }"
            + " WHERE { "
            + " ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ."
            + " OPTIONAL{?s <http://schema.org/latitude> ?o .}"
            + " OPTIONAL{?s <http://schema.org/longitude> ?o .}"
            + " FILTER (!bound(?o))"
            + "}";

    //Get all the triples on the model without the predicate geo:lat and geo:long
    private static String SPARQL_NO_WSG84COORDS =
              "CONSTRUCT {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location>  }"
            + " WHERE { "
            + " ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ."
            + " OPTIONAL{?s <http://schema.org/latitude> ?o .}"
            + " OPTIONAL{?s <http://schema.org/longitude> ?o .}"
            + " FILTER (!bound(?o))"
            + "}";

    private static String SPARQL_SCHEMACOORDS =
            "SELECT ?location ?lat ?long "
            + " WHERE { "
            + " ?location <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ;"
            + "  <http://schema.org/latitude> ?lat ;"
            + "  <http://schema.org/longitude> ?long ."
            + "}";

    public JenaInfoDocument(){}

    /**
     * Method for read,query and clean a specific set of triple
     * @param filenameInput
     * @param filepath
     * @param fileNameOutput
     * @param inputFormat
     * @param outputFormat
     * @throws IOException
     */
    public static void readQueryAndCleanTripleInfoDocument(
            String filenameInput,String filepath,String fileNameOutput,String inputFormat,String outputFormat)
            throws IOException{
        //Crea la tua query SPARQL

        SystemLog.sparql(SPARQL_NO_SCHEMACOORDS);
        //CREA IL TUO MODELLO DI JENA A PARTIRE DA UN FILE
        com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
        model = JenaKit.loadFileTriple(filenameInput, filepath, inputFormat);

        //ESEGUI LA QUERY SPARQL
        com.hp.hpl.jena.rdf.model.Model myGraph = model;
        myGraph = JenaKit.execQuerySparqlOnModel(SPARQL_NO_SCHEMACOORDS, model, "CONSTRUCT");

        com.hp.hpl.jena.rdf.model.StmtIterator iter = myGraph.listStatements();

        while (iter.hasNext()) {
            try{
                com.hp.hpl.jena.rdf.model.Statement stmt  = iter.nextStatement();  // get next statement
                model.remove(stmt);
                SystemLog.sparql("REMOVE 1:<" + stmt.getSubject() + "> <" + stmt.getPredicate() + "> <" + stmt.getObject() + ">.");
                com.hp.hpl.jena.rdf.model.Resource  subject   = stmt.getSubject();     // get the subject

                com.hp.hpl.jena.rdf.model.RDFNode   object2  =
                        (com.hp.hpl.jena.rdf.model.RDFNode) subject;      // get the object
                com.hp.hpl.jena.rdf.model.Resource subject2 =
                        new com.hp.hpl.jena.rdf.model.impl.ResourceImpl(object2.toString().replace("Location_",""));
                com.hp.hpl.jena.rdf.model.Property  predicate2 =
                        new com.hp.hpl.jena.rdf.model.impl.PropertyImpl("http://purl.org/goodrelations/v1#hasPOS");   // get the predicate

                //model.remove(subject2,predicate2,object2);
                model.removeAll(
                        (com.hp.hpl.jena.rdf.model.Resource)null,
                        predicate2,
                        object2);
                SystemLog.sparql("REMOVE 2:<" + subject2 + "> <" + predicate2 + "> <" + object2 + ">.");
            }catch(Exception e){
                SystemLog.exception(e);
            }
            //OPPURE
        }
        //RISCRIVIAMO GLI STATEMENT
        com.hp.hpl.jena.rdf.model.Model model2 = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

        //stmt.getPredicate().asResource().getURI().toLowerCase()
        //.contains("http://schema.org/latitude")||
        //stmt.getPredicate().asResource().getURI().toLowerCase()
        //.contains("http://schema.org/longitude")

        com.hp.hpl.jena.rdf.model.StmtIterator stats = model.listStatements();
        while (stats.hasNext()) {
            com.hp.hpl.jena.rdf.model.Statement stmt = stats.next();
            com.hp.hpl.jena.rdf.model.RDFNode x = stmt.getObject();
            if (x.isLiteral()) { //..if is a literal
                //x.asLiteral().getDatatype().equals(stringToXSDDatatypeToString("string"))){
                if(x.asLiteral().getDatatype().equals(com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDstring)){
                    //SystemLog.sparql("MODIFY STRING LITERAL:" + x.asLiteral().getLexicalForm());
                    model2.add(stmt.getSubject(),
                            stmt.getPredicate(),
                            com.hp.hpl.jena.rdf.model.ResourceFactory.createPlainLiteral(x.asLiteral().getLexicalForm() //...not print the ^^XMLString
                            ));
                }else{ //...if is a literal but is datatype is not string...
                    model2.add(
                            stmt.getSubject(),
                            stmt.getPredicate(),
                            com.hp.hpl.jena.rdf.model.ResourceFactory.createTypedLiteral(
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
                            ResourceFactory.createStatement(
                                    stmt.getSubject(),stmt.getPredicate(),literal);
                    model2.add(s);
            }
        }
        */
        //****************************************************************************
        //TEST HELPER FOR SILK
        String outputN3Knime = filepath + File.separator + "fileN3Knimem.n3";
        JenaKit.writeModelToFile(outputN3Knime, model2, "n3");
        //String outputTurtleKnime = filepath + File.separator + "fileTurtleKnimem.ttl";
        //JenaKit.writeModelToFile(outputTurtleKnime, model2, "ttl");
        List<String> lines = EncodingUtil.UnicodeEscape2UTF8(new File(outputN3Knime));
        EncodingUtil.writeLargerTextFileWithReplace2(outputN3Knime, lines);
        JenaKit.ConvertRDFTo(new File(outputN3Knime),"csv");
        //*************************************************************************************

        //Execute the SPARQL_WSG84COORDS and add the geometry statement
        com.hp.hpl.jena.query.ResultSet result = JenaKit.execSparqlSelectOnModel(SPARQL_SCHEMACOORDS, model2);
        Map<com.hp.hpl.jena.rdf.model.Resource, String[]> map = new HashMap<>();
        while (result.hasNext()){
            com.hp.hpl.jena.query.QuerySolution row = result.next();
            //float f1 = row.getLiteral("lat").getFloat();
            //System.out.println("Lat: " + row.getLiteral("lat").getString()); //43.7891042^^http://www.w3.org/2001/XMLSchema#float
            //System.out.println("Lat: " + row.getLiteral("lat").getLexicalForm());//43.7891042^^http://www.w3.org/2001/XMLSchema#float
            //System.out.println("Lat: " + row.getLiteral("lat").getDatatype());//Lat: Datatype[http://www.w3.org/2001/XMLSchema#float -> class java.lang.Float]
            String[] array = new String[2];
            array[0] = row.getLiteral("lat").getLexicalForm().replace("^^http://www.w3.org/2001/XMLSchema#float","");
            array[1] =  row.getLiteral("long").getLexicalForm().replace("^^http://www.w3.org/2001/XMLSchema#float","");
            map.put(row.getResource("location"), array);
        }
        //add new statment to the model
        for(Map.Entry<com.hp.hpl.jena.rdf.model.Resource, String[]> entry : map.entrySet()) {
            com.hp.hpl.jena.rdf.model.Resource subject = entry.getKey(); //...gr:Location
            com.hp.hpl.jena.rdf.model.Property predicate = new com.hp.hpl.jena.rdf.model.impl.PropertyImpl("http://www.w3.org/2003/01/geo/wgs84_pos#geometry");
            //com.hp.hpl.jena.rdf.model.Literal literal1 = entry.getValue()[0];
            //com.hp.hpl.jena.rdf.model.Literal literal2 = entry.getValue()[1];
            String value =entry.getValue()[0] + " " +  entry.getValue()[1];
            //com.hp.hpl.jena.rdf.model.Resource instance1 = model.createResource(subject.getURI())
            model2.addLiteral(subject, predicate,JenaKit.lt(value,com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDstring));
            // Create statements
            //instance1.addProperty(predicate, literal1).addProperty(predicate, literal2); // Classification of instance1
            //instance2.addProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long", class2); // Classification of instance2
            //instance1.addProperty(hasName, instance2); // Edge between instance1 and instance2

           // model.addLiteral(subject,predicate,literal1.getString());
        }
        String output = filepath + File.separator + fileNameOutput + "." + outputFormat;
        JenaKit.writeModelToFile(output, model2, outputFormat);
        //model.write(new FileWriter(output), "Turtle");
    }
}
