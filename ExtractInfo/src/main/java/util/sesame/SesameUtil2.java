package util.sesame;
import com.ontotext.trree.OwlimSchemaRepository;
import com.ontotext.jena.SesameDataset;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.rio.RDFParser;

/**
 * <p>
 * Class of Utils Method for Sesame Server Management
 * Support OWLIM Repsository type but you need to have the library on your classpath
 * </p>
 * <p>
 * This sample application is intended to illustrate how to prepare, configure and run a <a
 * href="http://www.openrdf.org">Sesame</a> repository using the <a
 * href="http://www.ontotext.com/owlim/">OWLIM</a> SAIL. The basic operations are demonstrated in separate
 * methods: get namespaces, evaluate queries, add and delete statements, parse and load files, etc.
 * </p>
 * <p>
 * Addition and removal are performed only when the input parameter 'updates' is set to 'true'. Thus,
 * potentially slow and irrelevant delete operations are avoided in case the example is adapted for loading
 * large data-sets.
 * </p>
 * <p>
 * This application can be used also as an easy test-bed for loading and querying different ontologies and
 * data-sets without needing to build a separate application.
 * </p>
 * <p>
 * The command line parameters are given as key=value' pairs.
 * A full list of parameters is given in the online OWLIM documentation at http://owlim.ontotext.com 
 * </p>
 * @author Marco Tenti
 */
public class SesameUtil2 {
    
    public SesameUtil2(){}
    
    
    //input parameter
    private static String PATH_FOLDER_STORAGE;
    private static String PATH_FOLDER_REPOSITORY;
    private static String TYPE_REPOSITORY_OWLIM;
    private static String RULESET;
    private static String SESAMESERVER;
    private static String TYPE_REPOSITORY;
    private static String ID_REPOSITORY;
    
    //loading parameter
    private static String CONTEXT = "context";
    private static String PARALLEL_LOAD = "true";
    private static String VERIFY = "true";
    private static String STOP_ON_ERROR = "true";
    private static String PRESERVE_BNODES = "true";
    private static String DATATYPE_HANDLING = RDFParser.DatatypeHandling.VERIFY.name();
    private static String CHUNK_SIZE ="500000";

    private static String SHOWSTATS = "false";
    private static String UPDATES = "false";
    
    //output parameter
    private static String OUTPUTFILE ="outputPathFileName";
    private static String OUTPUTFORMAT ="N3";
    private static boolean PRINT_RESULT_QUERY;
    
    public void setParameterLocalRepository(
            String TYPE_REPOSITORY,String PATH_FOLDER_STORAGE,String PATH_FOLDER_REPOSITORY,
            String TYPE_REPOSITORY_OWLIM,String RULESET
            ){
         this.TYPE_REPOSITORY=TYPE_REPOSITORY;
         this.PATH_FOLDER_STORAGE=PATH_FOLDER_STORAGE;
         this.PATH_FOLDER_REPOSITORY=PATH_FOLDER_REPOSITORY;
         this.TYPE_REPOSITORY_OWLIM=TYPE_REPOSITORY_OWLIM;
         this.RULESET=RULESET;        
    }
            
    public void setParameterRemoteRepository(
            String TYPE_REPOSITORY,String SESAMESERVER,String ID_REPOSITORY){
        this.TYPE_REPOSITORY=TYPE_REPOSITORY;
        this.SESAMESERVER=SESAMESERVER;        
        this.ID_REPOSITORY=ID_REPOSITORY;
    }   
    
    
    
    // A map of namespace-to-prefix
    //private static Map<String, String> namespacePrefixes = new HashMap<String, String>();

    // The repository manager
    //private static org.openrdf.repository.manager.RepositoryManager repositoryManager;

    // From repositoryManager.getRepository(...) - the actual repository we will
    // work with       
    private static org.openrdf.repository.RepositoryConnection conn;
     
     
    ////SESAME UTIL 2////////////////////////////////////////////////////////////////////////////////////
    public static void openSesameConnection(org.openrdf.repository.Repository repository) 
            throws org.openrdf.repository.RepositoryException{
         conn = repository.getConnection();       
    }
    
     public static void closeSesameConnection(org.openrdf.repository.Repository repository) 
             throws org.openrdf.repository.RepositoryException{
         conn.close();
    }
    //private static org.openrdf.repository.Repository repository;
    
    public static org.openrdf.repository.Repository openSesameConnectionToRepository() 
            throws org.openrdf.repository.RepositoryException{
        org.openrdf.repository.Repository repository = null;
        //String typeRepository = TYPE_REPOSITORY;
        //String repositoryID = ID_REPOSITORY;
        File Datadir = new File(PATH_FOLDER_REPOSITORY) ;
        if(TYPE_REPOSITORY.toLowerCase().contains("owlim")){              
             OwlimSchemaRepository schema = new OwlimSchemaRepository();
             schema.setDataDir(Datadir);
             schema.setParameter("storage-folder", PATH_FOLDER_STORAGE);
             schema.setParameter("repository-type", TYPE_REPOSITORY_OWLIM);
             schema.setParameter("ruleset", RULESET);
             // wrap it into a Sesame SailRepository
             //SailRepository repository = new SailRepository(schema);
             repository  = 
                     new org.openrdf.repository.sail.SailRepository(schema);
        }
        else if(TYPE_REPOSITORY.toLowerCase().contains("memory")){
            //Create and initialize a non-inferencing main-memory repository
            //the MemoryStore will write its contents to the directory so that 
            //it can restore it when it is re-initialized in a future session
             repository = 
                 new org.openrdf.repository.sail.SailRepository(
                    new org.openrdf.sail.memory.MemoryStore(Datadir)
                           // .setDataDir(Datadir)
                 );
            //oppure
            /*
            org.openrdf.sail.memory.MemoryStore memStore=
                   new org.openrdf.sail.memory.MemoryStore(
                                 new File(PATH_FOLDER_REPOSITORY) 
                    ); 
            Repository repo = new org.openrdf.repository.sail.SailRepository(memStore); 
            */
        }      
        else if(TYPE_REPOSITORY.toLowerCase().contains("native")){
            //Creating a Native RDF Repository
            //does not keep data in main memory, but instead stores it directly to disk
            String indexes = "spoc,posc,cosp";
            repository = 
                new org.openrdf.repository.sail.SailRepository(
                        new org.openrdf.sail.nativerdf.NativeStore(Datadir,indexes)
                );
        }
        else if(TYPE_REPOSITORY.toLowerCase().contains("inferecing")){
            //Creating a repository with RDF Schema inferencing
            //ForwardChainingRDFSInferencer is a generic RDF Schema 
            //inferencer (MemoryStore and NativeStore support it)
            repository = 
                new org.openrdf.repository.sail.SailRepository(
                        new org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer( 
                                new org.openrdf.sail.memory.MemoryStore() )
                );
        }
        else if(TYPE_REPOSITORY.toLowerCase().contains("server")){
            //Accessing a server-side repository
            repository= new org.openrdf.repository.http.HTTPRepository(SESAMESERVER, ID_REPOSITORY);

        }
           
        // wrap it into a Sesame SailRepository
        //org.openrdf.repository.Repository repository = 
        //       new org.openrdf.repository.sail.SailRepository(schema);
        
        // initialize
        if(repository != null && !repository.isInitialized()){
            repository.initialize();             
            return repository;   
        }else{
            return null;
        }
    }
    
    public static com.hp.hpl.jena.rdf.model.Model convertSesameDataSetToJenaModel(
                org.openrdf.repository.Repository repository) throws org.openrdf.repository.RepositoryException{
            
        //org.openrdf.repository.RepositoryConnection conn= repository.getConnection();
        //conn = repository.getConnection();
        openSesameConnection(repository);
        // finally, insert the DatasetGraph instance
        SesameDataset dataset = new SesameDataset(conn);

        //From now on the SesameDataset object can be used through the Jena API 
        //as regular Dataset, e.g. to add some data into it one could something like the
        //following:
        com.hp.hpl.jena.rdf.model.Model model = 
                com.hp.hpl.jena.rdf.model.ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        /*
        com.hp.hpl.jena.rdf.model.Resource r1 = model.createResource("http://example.org/book#1") ;
        com.hp.hpl.jena.rdf.model.Resource r2 = model.createResource("http://example.org/book#2") ;
        r1.addProperty(com.hp.hpl.jena.vocabulary.DC.title, "SPARQL - the book")
        .addProperty(com.hp.hpl.jena.vocabulary.DC.description, "A book about SPARQL") ;
        r2.addProperty(com.hp.hpl.jena.vocabulary.DC.title, "Advanced techniques for SPARQL") ;
        */         
        return model;
    }
    
    public static void TupleQueryEvalutation(
            org.openrdf.repository.Repository repo,String queryString){
        try { 
            //org.openrdf.repository.RepositoryConnection con = repo.getConnection();
            //conn = repo.getConnection();
            try {
                //String queryString = " SELECT ?x ?y WHERE { ?x ?p ?y } ";
                org.openrdf.query.TupleQuery tupleQuery = conn.prepareTupleQuery(
                        org.openrdf.query.QueryLanguage.SPARQL, queryString);
                org.openrdf.query.TupleQueryResult result = tupleQuery.evaluate();
                try {
                    List<String> bindingNames = result.getBindingNames();
                    while (result.hasNext()) {
                        org.openrdf.query.BindingSet bindingSet = result.next();
                        org.openrdf.model.Value firstValue = bindingSet.getValue(bindingNames.get(0));
                        org.openrdf.model.Value secondValue = bindingSet.getValue(bindingNames.get(1));
                        // do something interesting with the values here...
                    }
                } finally { 
                    result.close(); 
                }
            } finally { 
                //conn.close(); 
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
    }
    
     public static void GraphQueryEvalutation(
             org.openrdf.repository.Repository repo,String queryString){     
          try { 
            //org.openrdf.repository.RepositoryConnection con = repo.getConnection();
            //conn = repo.getConnection();
            try {
                 //String queryString = "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }";
                org.openrdf.query.GraphQueryResult result = conn.prepareGraphQuery(
                    org.openrdf.query.QueryLanguage.SPARQL,queryString ).evaluate();               
                try {                 
                    while (result.hasNext()) {
                        org.openrdf.model.Statement stmt = result.next();
                        // ... do something with the resulting statement here.
                    }
                } finally { 
                    result.close(); 
                }
            } finally { 
                //conn.close(); 
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
      }
     
       public static org.openrdf.model.Model convertGraphQueryEvalutationToSesameModel(
               org.openrdf.repository.Repository repo,String queryString){             
           org.openrdf.model.Model resultModel = null;
           try {             
             //org.openrdf.repository.RepositoryConnection 
             //conn = repo.getConnection();
            try {
                 //String queryString = "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o }";
                org.openrdf.query.GraphQueryResult result = conn.prepareGraphQuery(
                    org.openrdf.query.QueryLanguage.SPARQL,queryString ).evaluate();               
                try {                 
                    resultModel = org.openrdf.query.QueryResults.asModel(result);
                } finally { 
                    result.close(); 
                }
            } finally { 
                //conn.close(); 
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
        return resultModel;
     }
       
     public void printQueryGraphResult(org.openrdf.repository.Repository repository, 
             String queryString,String filePath,String outputFormat) throws FileNotFoundException{      
         try { 
            //org.openrdf.repository.RepositoryConnection con = repository.getConnection();
            //conn = repository.getConnection();
            try {
                OutputStream out;
                if(filePath==null){
                    out = System.out;
                }else{
                    out = new FileOutputStream(new File("."));
                }
                //org.openrdf.rio.RDFWriter writer = org.openrdf.rio.Rio.createWriter( 
                //        org.openrdf.rio.RDFFormat.TURTLE, out);
                SesameUtil s = new SesameUtil();
                org.openrdf.rio.RDFWriter writer = org.openrdf.rio.Rio.createWriter( 
                        stringToRDFFormat(outputFormat), out);
                conn.prepareGraphQuery(org.openrdf.query.QueryLanguage.SPARQL,
                     queryString).evaluate(writer);
            } finally { 
                //conn.close(); 
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
     }
     
     public void printQueryTupleResult(org.openrdf.repository.Repository repository, 
             String queryString,String filePath,String outputFormat) throws FileNotFoundException{      
         try { 
            //org.openrdf.repository.RepositoryConnection con = repository.getConnection();
            //conn = repository.getConnection();
            try {
                //org.openrdf.rio.RDFWriter writer = org.openrdf.rio.Rio.createWriter( 
                //        org.openrdf.rio.RDFFormat.TURTLE, System.out);
                OutputStream out;
                if(filePath==null){
                    out = System.out;
                }else{
                    out = new FileOutputStream(new File("."));
                }
                org.openrdf.query.TupleQueryResultHandler trh = null;
                
                if(outputFormat.equalsIgnoreCase("csv")){
                    trh = new org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriter(out);
                }else if(outputFormat.equalsIgnoreCase("json")){
                    trh = new org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter(out);
                }else if(outputFormat.equalsIgnoreCase("tsv")){
                    trh = new org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter(out);
                }else if(outputFormat.equalsIgnoreCase("xml")){
                    trh = new org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter(out);
                }else{
                    trh = new org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriter(out);
                }
                
                conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL,
                     queryString).evaluate(trh);
                
            } finally {                
                //conn.close(); 
            }
        } catch (org.openrdf.OpenRDFException e) {
         // handle exception
        }
     }
     
     public static void connvertTo(String filePath,String inputFormat,String outputFormat,String urlFile) {
        try {
            // open our input document
             URL documentUrl = null;
             org.openrdf.rio.RDFFormat format;
             InputStream inputStream = null;
            if(urlFile.contains("http://")){
                documentUrl = new URL(urlFile);
                //AutoDetecting the file format
                format = org.openrdf.rio.Rio.getParserFormatForFileName(documentUrl.toString());
                //RDFFormat format2 = Rio.getParserFormatForMIMEType("contentType");
               // RDFParser rdfParser = Rio.createParser(format);
                inputStream = documentUrl.openStream();
            }else{
               documentUrl = new URL("file::///"+filePath); 
               format = stringToRDFFormat(inputFormat) ;               
               inputStream = documentUrl.openStream();
             }
            BufferedReader in
                    = new BufferedReader(new InputStreamReader(inputStream));
            // insert a parser for Turtle and a writer for RDF/XML
            //org.openrdf.rio.RDFParser rdfParser = 
            //        org.openrdf.rio.Rio.createParser(stringToRDFFormat(inputFormat));
            org.openrdf.rio.RDFParser rdfParser = 
                    org.openrdf.rio.Rio.createParser(format);
            org.openrdf.rio.RDFWriter rdfWriter = 
                    org.openrdf.rio.Rio.createWriter(stringToRDFFormat(outputFormat),
                         new FileOutputStream(filePath+ "." +outputFormat));
            // link our parser to our writer...
            rdfParser.setRDFHandler(rdfWriter);
            // ...and start the conversion!       
            rdfParser.parse(in, documentUrl.toString());
        } catch (IOException e) {
        // handle IO problems (e.g. the file could not be read)
        } catch (org.openrdf.rio.RDFParseException ex) {
            Logger.getLogger(SesameUtil2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.openrdf.rio.RDFHandlerException ex) {
            Logger.getLogger(SesameUtil2.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
     
    
  
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    //method of support
    // A list of RDF file formats used in loadFile().
    private static final org.openrdf.rio.RDFFormat allFormats[] = 
            new org.openrdf.rio.RDFFormat[] {
        org.openrdf.rio.RDFFormat.NTRIPLES, org.openrdf.rio.RDFFormat.N3,
        org.openrdf.rio.RDFFormat.RDFXML, org.openrdf.rio.RDFFormat.TURTLE, 
        org.openrdf.rio.RDFFormat.TRIG, org.openrdf.rio.RDFFormat.TRIX, 
        org.openrdf.rio.RDFFormat.NQUADS };

    private static org.openrdf.rio.RDFFormat stringToRDFFormat(String strFormat) {
        if(strFormat.equalsIgnoreCase("NT")){
            strFormat = "N-Triples";
        }
        for (org.openrdf.rio.RDFFormat format : allFormats) {
                if (format.getName().equalsIgnoreCase(strFormat))
                        return format;
        }
        throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
    }

    // A list of datatype handling strategies
    private static final org.openrdf.rio.RDFParser.DatatypeHandling allDatatypeHandling[] = 
            new org.openrdf.rio.RDFParser.DatatypeHandling[] {
        org.openrdf.rio.RDFParser.DatatypeHandling.IGNORE, 
        org.openrdf.rio.RDFParser.DatatypeHandling.NORMALIZE, 
        org.openrdf.rio.RDFParser.DatatypeHandling.VERIFY
    };

    private static org.openrdf.rio.RDFParser.DatatypeHandling stringToDatatypeHandling(String strHandling) {
        for (org.openrdf.rio.RDFParser.DatatypeHandling handling : allDatatypeHandling) {
                if (handling.name().equalsIgnoreCase(strHandling))
                        return handling;
        }
        throw new IllegalArgumentException("Datatype handling strategy for parsing '" + strHandling + "' is not recognised");
    }
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //forgotten method
    /*
    private void querySelectOntoTextSchema(SesameDataset dataset,String querySelectString){
        //and can also be used to evaluate queries through the ARQ engine:
        // Query string.
        //String queryString = "PREFIX dc: <" + com.hp.hpl.jena.vocabulary.DC.getURI() + "> " +
        //                     "SELECT ?title WHERE {?x dc:title ?title . }";
        
        com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.insert(querySelectString);
        // Create a single execution of this query, apply to a model
        // which is wrapped up as a QueryExecution and then fetch the results
        com.hp.hpl.jena.query.QueryExecution qexec = 
                com.hp.hpl.jena.query.QueryExecutionFactory.insert(query, dataset.asDataset());
        try {
            // Assumption: it's a SELECT query.
            com.hp.hpl.jena.query.ResultSet rs = qexec.execSelect();
            // The order of results is undefined.
            for (; rs.hasNext();) {
                com.hp.hpl.jena.query.QuerySolution rb = rs.nextSolution();
                for (Iterator<String> iter = rb.varNames(); iter.hasNext();) {
                    String name = iter.next();
                    com.hp.hpl.jena.rdf.model.RDFNode x = rb.get(name);
                    if (x.isLiteral()) {
                        com.hp.hpl.jena.rdf.model.Literal titleStr = 
                                (com.hp.hpl.jena.rdf.model.Literal) x;
                        System.out.print(name + "=" + titleStr + "\t");
                    } else if (x.isURIResource()) {
                        com.hp.hpl.jena.rdf.model.Resource res = 
                                (com.hp.hpl.jena.rdf.model.Resource) x;
                        System.out.print(name + "=" + res.getURI() + "\t");
                    }
                    else
                        System.out.print(name + "=" + x.toString() + "\t");
                }
                System.out.println();                            
            }
        }catch(Exception e ) {
             System.out.println( "Exception occurred: " + e );
        }finally {
             // QueryExecution objects should be closed to free any system resources
             qexec.close();
        }
    }
    */
}
