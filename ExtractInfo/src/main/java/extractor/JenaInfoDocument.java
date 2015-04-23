package extractor;

import java.io.IOException;

/**
 * Created by Marco on 20/04/2015.
 */
public class JenaInfoDocument {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JenaInfoDocument.class);
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
        String SPARQL_QUERY = "CONSTRUCT {?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location>  }"
                + " WHERE { "
                + " ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.org/goodrelations/v1#Location> ."
                + " OPTIONAL{?s <http://schema.org/latitude> ?o .}"
                + " OPTIONAL{?s <http://schema.org/longitude> ?o .}"
                + " FILTER (!bound(?o))"
                + "}";
        SystemLog.ticket("Query SPARQL:" + SPARQL_QUERY, "OUT");
        //CREA IL TUO MODELLO DI JENA A PARTIRE DA UN FILE
        com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
        model = JenaKit.loadFileTriple(filenameInput, filepath, inputFormat);

        //ESEGUI LA QUERY SPARQL
        //execQuerySparqlOnModel(sparql,model,"SELECT");
        com.hp.hpl.jena.rdf.model.Model myGraph = model;

        myGraph = JenaKit.execQuerySparqlOnModel(SPARQL_QUERY, model, "CONSTRUCT");
        //model = loadFileAndCOnvertToTriple(filename,filepath);
        com.hp.hpl.jena.rdf.model.StmtIterator iter = myGraph.listStatements();

        while (iter.hasNext()) {
            try{
                com.hp.hpl.jena.rdf.model.Statement stmt  = iter.nextStatement();  // get next statement
                model.remove(stmt);
                //SystemLog.ticket("REMOVE 1:<" + stmt.getSubject() + "> <" + stmt.getPredicate() + "> <" + stmt.getObject() + ">.", "OUT");

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
                //SystemLog.ticket("REMOVE 2:<" + subject2 + "> <" + predicate2 + "> <" + object2 + ">.", "OUT");
            }catch(Exception e){
                SystemLog.error("ECCEZIONE IN FASE DI RIPULITURA DELLE TRIPLE:" + e.getMessage());
                e.printStackTrace();
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
            if (x.isLiteral()) {
                if(
                    //x.asLiteral().getDatatype().equals(stringToXSDDatatypeToString("string"))){
                        x.asLiteral().getDatatype().equals(com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDstring)){
                    //SystemLog.ticket("MODIFY STRING LITERAL:" + x.asLiteral().getLexicalForm(), "AVOID");
                    model2.add(stmt.getSubject(),
                            stmt.getPredicate(),
                            com.hp.hpl.jena.rdf.model.ResourceFactory.createPlainLiteral(x.asLiteral().getLexicalForm()
                            ));
                }else{
                    model2.add(
                            stmt.getSubject(),
                            stmt.getPredicate(),
                            com.hp.hpl.jena.rdf.model.ResourceFactory.createTypedLiteral(
                                    stmt.getObject().asLiteral().toString(),
                                    stmt.getObject().asLiteral().getDatatype()
                            )
                    );
                }

            } else if (x.isURIResource()) {
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
        String output = filepath + "\\" + fileNameOutput + "." + outputFormat;
        JenaKit.writeModelToFile(filepath + "\\" + fileNameOutput + "." + outputFormat, model2, outputFormat);
        //model.write(new FileWriter(output), "Turtle");
    }
}
