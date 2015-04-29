package p4535992.util.jena;

import p4535992.util.file.FileUtil;
import p4535992.util.log.SystemLog;
import p4535992.util.xml.XMLKit_Extends;
import p4535992.util.string.StringOutputStreamKit;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class utility for Jena
 * Created by 4535992 in 2015-04-28
 */
public class JenaKit {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JenaKit.class);
    //PRIVATE
    private static String SPARQL_QUERY;
    private static String INFORMAT;
    private static String OUTFORMAT;

    private static Hashtable namespaces = new Hashtable();
    private static com.hp.hpl.jena.rdf.model.Model model;
    private static org.apache.jena.riot.Lang OUTLANGFORMAT;
    private static org.apache.jena.riot.RDFFormat OUTRDFFORMAT;

    private static org.apache.jena.riot.Lang INLANGFORMAT;
    private static org.apache.jena.riot.RDFFormat INRDFFORMAT;

    //PUBLIC
    public static final String RDF_FORMAT ="RDF/XML-ABBREV";
    public static SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");


    public static com.hp.hpl.jena.rdf.model.Model setNewDefaultModel(){
        com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
        return model;
    }

    /**
     * Method  to Write large model jena to file of text
     * @param fullPath
     * @param model
     * @param outputFormat
     * @throws IOException
     */
    public static void writeModelToFile(String fullPath, com.hp.hpl.jena.rdf.model.Model model, String outputFormat) throws IOException {
        fullPath =  FileUtil.path(fullPath) + File.separator + FileUtil.filenameNoExt(fullPath)+"."+outputFormat.toLowerCase();
        SystemLog.message("Try to write the new file of triple of infodocument to:" + fullPath + "...");
        OUTLANGFORMAT = stringToRiotLang(outputFormat);
        OUTRDFFORMAT = stringToRDFFormat(outputFormat);
        OUTFORMAT = outputFormat.toUpperCase();
        try {
            writeModel2File2(fullPath, model);
        }catch(Exception e1){
            SystemLog.warning("...there is was a prblem to try the write the triple file at the first tentative...");
            try {
                writeModel2File3(fullPath, model);
            }catch(Exception e2){
                SystemLog.warning("...there is was a prblem to try the write the triple file at the second tentative...");
                try{
                    writeModel2File(fullPath, model);//unmappable character exception
                }catch (Exception e3){
                    SystemLog.error("... exception during the writing of the file of triples:" + fullPath);
                    SystemLog.exception(e3);
                }
            }
        }
        SystemLog.message("... the file of triple Infodoument to:" + fullPath + " is been wrote!");
    }

    /**
     * Method  to Write large model jena to file of text
     * @param fullPath
     * @param model
     * @deprecated  use {@link //com.hp.hpl.jena.rdf.model.Model.write(...)} instead.
     * @throws IOException
     */
	private static void writeModel2File(String fullPath, com.hp.hpl.jena.rdf.model.Model model) throws IOException {
        Charset ENCODING = StandardCharsets.ISO_8859_1;
        FileUtil.createFile(fullPath);
        Path path = Paths.get(fullPath);
	    try (BufferedWriter writer = Files.newBufferedWriter(path,ENCODING)) {
            org.apache.jena.riot.RDFDataMgr.write(writer, model, OUTLANGFORMAT);
            //model.write(writer, null, OUTFORMAT);
        }
	  }

    /**
     * Method  to Write large model jena to file of text
     * @param fullPath
     * @param model
     */
    private static void writeModel2File2(String fullPath, com.hp.hpl.jena.rdf.model.Model model) throws IOException {
        FileWriter out = new FileWriter(fullPath);
        try {
            model.write(out, OUTLANGFORMAT.getName());
        }
        finally {
            try {
                out.close();
            }
            catch (IOException closeException) {
                // ignore
            }
        }
    }

    /**
     * Method  to Write large model jena to file of text
     * @param fullPath
     * @param model
     */
    private static void writeModel2File3 (String fullPath, com.hp.hpl.jena.rdf.model.Model model) throws FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(fullPath);
        model.write(outputStream, OUTLANGFORMAT.getName());
    }

    /**
     * Method for Exec a SPARQL query to a Jena Model and return triple
     * @param sparql
     * @param model
     * @param typeSparql
     * @return
     * @throws FileNotFoundException
     */
	public static com.hp.hpl.jena.rdf.model.Model execQuerySparqlOnModel(String sparql,com.hp.hpl.jena.rdf.model.Model model,String typeSparql) throws FileNotFoundException{
		String queryString =sparql;
		com.hp.hpl.jena.query.Query query=null;
		com.hp.hpl.jena.query.QueryExecution qexec;
		com.hp.hpl.jena.rdf.model.Model resultModel=null;
		com.hp.hpl.jena.query.ResultSet results= null;
		switch(typeSparql){
		   case "SELECT":
			   /*
			   query = QueryFactory.insert(queryString) ;
			   try (com.hp.hpl.jena.query.QueryExecution qexec2 = com.hp.hpl.jena.query.QueryExecutionFactory.insert(query, model)) {
				   com.hp.hpl.jena.query.ResultSet results = qexec2.execSelect() ;
			     for ( ; results.hasNext() ; )
			     {
			       com.hp.hpl.jena.query.QuerySolution soln = results.nextSolution() ;
			       com.hp.hpl.jena.rdf.model.RDFNode x = soln.get("varName") ;       // Get a result variable by name.
			       com.hp.hpl.jena.rdf.model.Resource r = soln.getResource("VarR") ; // Get a result variable - must be a resource
			       com.hp.hpl.jena.rdf.model.Literal lt = soln.getLiteral("VarL") ;   // Get a result variable - must be a literal
			     }
			   }
			   */
//			   try (com.hp.hpl.jena.query.QueryExecution qexec2 =
//			   com.hp.hpl.jena.query.QueryExecutionFactory.insert(queryString, model)) {
//				   results = qexec2.execSelect() ;
//				   results = com.hp.hpl.jena.query.ResultSetFactory.copyResults(results) ;
//				   //return results ;    // Passes the result set out of the try-resources
//			   }
               com.hp.hpl.jena.query.QueryExecution qexec2 =
                            com.hp.hpl.jena.query.QueryExecutionFactory.create(queryString, model);
               results = qexec2.execSelect() ;
               results = com.hp.hpl.jena.query.ResultSetFactory.copyResults(results) ;
			   break;
		   case "CONSTRUCT":
			   //CONSTRUCT queries return a single RDF graph. 
			   //As usual, the query execution should be closed after use.
			   query = com.hp.hpl.jena.query.QueryFactory.create(queryString) ;
			   qexec = com.hp.hpl.jena.query.QueryExecutionFactory.create(query, model) ;
			   resultModel = qexec.execConstruct() ;
			   qexec.close() ;
			   break;
		   case "DESCRIBE":
			   //DESCRIBE queries return a single RDF graph.  
			   //Different handlers for the DESCRIBE operation can be loaded by added by the application.
			   query = com.hp.hpl.jena.query.QueryFactory.create(queryString) ;
			   qexec = com.hp.hpl.jena.query.QueryExecutionFactory.create(query, model) ;
			   resultModel = qexec.execDescribe() ;
			   qexec.close() ;
			   break;
		   case "ASK":
			   //The operation Query.execAsk() returns a boolean value indicating 
			   //whether the query pattern matched the graph or dataset or not.
			   query = com.hp.hpl.jena.query.QueryFactory.create(queryString) ;
			   qexec = com.hp.hpl.jena.query.QueryExecutionFactory.create(query, model) ;
			   boolean result = qexec.execAsk() ;
			   qexec.close() ;
			   break;
		}
		//PRINT THE RESULT
		/*
		com.hp.hpl.jena.query.ResultSetFormatter fmt = 
				new com.hp.hpl.jena.query.ResultSetFormatter(results, query) ;
	    fmt.printAll(System.out) ;
	    //or simply:
	    //com.hp.hpl.jena.query.ResultSetFormatter.out(System.out, results, query) ;
	    */
        /*
	    FileOutputStream out = new FileOutputStream("Jena_test.n3");
	    com.hp.hpl.jena.query.ResultSetFormatter.out(out, results, query) ;
        */
        com.hp.hpl.jena.rdf.model.Model myGraph =  resultModel;
	    return myGraph;
	}


    public static com.hp.hpl.jena.rdf.model.Model execSparqlConstructorOnModel(String sparql,com.hp.hpl.jena.rdf.model.Model model) {
        com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(sparql) ;
        com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.create(query, model) ;
        com.hp.hpl.jena.rdf.model.Model resultModel = qexec.execConstruct() ;
        SystemLog.sparql(sparql);
        qexec.close() ;
        return  resultModel;
    }

    public static com.hp.hpl.jena.rdf.model.Model execSparqlDescribeOnModel(String sparql,com.hp.hpl.jena.rdf.model.Model model) {
        com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(sparql) ;
        com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.create(query, model) ;
        com.hp.hpl.jena.rdf.model.Model resultModel = qexec.execDescribe() ;
        SystemLog.sparql(sparql);
        qexec.close() ;
        return resultModel;
    }

    public static com.hp.hpl.jena.query.ResultSet execSparqlSelectOnModel(String sparql,com.hp.hpl.jena.rdf.model.Model model) {
        com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.create(sparql, model);
        com.hp.hpl.jena.query.ResultSet  results = qexec.execSelect();
        results = com.hp.hpl.jena.query.ResultSetFactory.copyResults(results) ; //... make exit from the thread the result of query
        SystemLog.sparql(sparql);
        qexec.close();
        return results;
    }

    public static boolean execSparqlAskOnModel(String sparql,com.hp.hpl.jena.rdf.model.Model model) {
        com.hp.hpl.jena.query.Query query = com.hp.hpl.jena.query.QueryFactory.create(sparql) ;
        com.hp.hpl.jena.query.QueryExecution qexec = com.hp.hpl.jena.query.QueryExecutionFactory.create(query, model) ;
        boolean result = qexec.execAsk();
        SystemLog.sparql(sparql);
        qexec.close() ;
        return result;
    }
	/**
     * Metodo per il caricamento di un file di triple in un'oggetto model di JENA.
     * @param filename, nome del file da caricare.
     * @param filepath, path del file da caricare,
     * @param inputFormat, formato del fil di triple da caricare.
     * @return the jena model of the file
     */
    public static com.hp.hpl.jena.rdf.model.Model loadFileTriple(String filename,String filepath,String inputFormat){
        com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
        INLANGFORMAT = stringToRiotLang(inputFormat);
        INRDFFORMAT = stringToRDFFormat(inputFormat);
        INFORMAT = INLANGFORMAT.getLabel().toUpperCase();
        //1)
        //FileManager.get().addLocatorClassLoader(ExampleAPI_01.class.getClassLoader());
        //Model model = FileManager.get().loadModel("data/data.ttl", null, "TURTLE");
        //com.hp.hpl.jena.rdf.model.Model model3 = com.hp.hpl.jena.util.FileManager.get().loadModel("data/data.ttl", null, "NT");
        //2)
        //Model model= ModelFactory.createDefaultModel();
        //InputStream in= FileManager.get().open("data/somedata.nt");
        //model.read(in, "", "N-TRIPLES");

        // use the FileManager to find the input file
        InputStream in = com.hp.hpl.jena.util.FileManager.get().open( filepath+File.separator+filename+"."+inputFormat);
        if (in == null || !new File(filepath+File.separator+filename+"."+inputFormat).exists()) {
            throw new IllegalArgumentException( "File: " + filepath+File.separator+filename + " not found");
        }
        /*
         model.read(inputStream, null, "N-TRIPLES") ;
        RDFDataMgr.read(model, inputStream, LANG.NTRIPLES) ;
        If you are just opening the stream from a file (or URL) then Apache Jena
        will sort out the details. E.g.,
        RDFDataMgr.read(model, "file:///myfile.nt") ;
         */

        // read the RDF/XML file
        //org.apache.jena.riot.RDFDataMgr.read(model,filepath+"\\"+filename+"."+inputFormat);
        //org.apache.jena.riot.RDFDataMgr.read(model, in,Lang.N3);
        //org.apache.jena.riot.RDFDataMgr.read(model,in,stringToRiotLang("N3"));
        //org.apache.jena.riot.RDFDataMgr.read(model,in,stringToRiotLang(inputFormat.toUpperCase()));
        SystemLog.message("Try to read file of triples from the path:" + new File(filepath +File.separator+ filename + "." + inputFormat).getAbsolutePath()+"...");
        try{
            model.read(in, null,INFORMAT) ;
        }
        catch(Exception e1){
            try{
                org.apache.jena.riot.RDFDataMgr.read(model, in, INLANGFORMAT);
            }catch(Exception e2){
                try{
                    org.apache.jena.riot.RDFDataMgr.read(model,
                            new File(filepath + File.separator + filename + "." + inputFormat).toURI().toString());
                }catch(Exception e3){
                    e3.printStackTrace();
                    SystemLog.abort(0,"Failed read the file of triples from the path:" + new File(filepath +File.separator+ filename + "." + inputFormat).getAbsolutePath());
                }
            }
        }
        SystemLog.message("...file of triples from the path:" + new File(filepath +File.separator+ filename + "." + inputFormat).getAbsolutePath()+" readed!!");
        return model;
    }
    /**
     * Method per il caricamento di un file di triple in un'oggetto model di JENA.
     * @param file a input file
     * @return the jena model of the file
     */
     public static com.hp.hpl.jena.rdf.model.Model loadFileTriple(File file){
        // insert an empty model
         String filename = FileUtil.filenameNoExt(file);
         String filepath = FileUtil.path(file);
         String inputFormat = FileUtil.extension(file);
         com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();

         model = loadFileTriple(filename,filepath,inputFormat);

         return model;
    }

    /**
     * A list of org.apache.jena.riot.Lang file formats.
     */
 	private static final org.apache.jena.riot.Lang allFormatsOfRiotLang[] = new org.apache.jena.riot.Lang[] { 
 		org.apache.jena.riot.Lang.NTRIPLES, org.apache.jena.riot.Lang.N3,org.apache.jena.riot.Lang.RDFXML, 
 		org.apache.jena.riot.Lang.TURTLE, org.apache.jena.riot.Lang.TRIG, org.apache.jena.riot.Lang.TTL, 
 		org.apache.jena.riot.Lang.NQUADS ,
            //org.apache.jena.riot.Lang.CSV,
            org.apache.jena.riot.Lang.NQ,
 		//org.apache.jena.riot.Lang.JSONLD,
            org.apache.jena.riot.Lang.NT,org.apache.jena.riot.Lang.RDFJSON,
 		org.apache.jena.riot.Lang.RDFNULL,
        //    org.apache.jena.riot.Lang.RDFTHRIFT
        };
 	/**
     * A list of org.apache.jena.riot.RDFFormat file formats used in loadFile().
     */
 	private static final org.apache.jena.riot.RDFFormat allFormatsOfRDFFormat[] = new org.apache.jena.riot.RDFFormat[] { 	
 		org.apache.jena.riot.RDFFormat.TURTLE, org.apache.jena.riot.RDFFormat.TTL, 
 		org.apache.jena.riot.RDFFormat.JSONLD_FLAT,org.apache.jena.riot.RDFFormat.JSONLD_PRETTY,
 		org.apache.jena.riot.RDFFormat.JSONLD,
 		org.apache.jena.riot.RDFFormat.RDFJSON,org.apache.jena.riot.RDFFormat.RDFNULL,		
 		org.apache.jena.riot.RDFFormat.NQUADS,org.apache.jena.riot.RDFFormat.NQ,
 		org.apache.jena.riot.RDFFormat.NQUADS_ASCII,org.apache.jena.riot.RDFFormat.NQUADS_UTF8,	
 		org.apache.jena.riot.RDFFormat.NT,org.apache.jena.riot.RDFFormat.NTRIPLES,
 		org.apache.jena.riot.RDFFormat.NTRIPLES_ASCII,org.apache.jena.riot.RDFFormat.NTRIPLES_UTF8,	
 		//org.apache.jena.riot.RDFFormat.RDF_THRIFT,org.apache.jena.riot.RDFFormat.RDF_THRIFT_VALUES,
 		org.apache.jena.riot.RDFFormat.RDFXML,org.apache.jena.riot.RDFFormat.RDFXML_ABBREV,
 		org.apache.jena.riot.RDFFormat.RDFXML_PLAIN,org.apache.jena.riot.RDFFormat.RDFXML_PRETTY,		
 		org.apache.jena.riot.RDFFormat.TRIG,org.apache.jena.riot.RDFFormat.TRIG_BLOCKS,
 		org.apache.jena.riot.RDFFormat.TRIG_FLAT,org.apache.jena.riot.RDFFormat.TRIG_PRETTY,
 		org.apache.jena.riot.RDFFormat.TURTLE_BLOCKS,org.apache.jena.riot.RDFFormat.RDFXML.TURTLE_FLAT,
 		org.apache.jena.riot.RDFFormat.RDFXML.TURTLE_PRETTY};

    /*
    public static com.hp.hpl.jena.datatypes.RDFDatatype convertXSDDatatypeToRDFDatatype(
            com.hp.hpl.jena.datatypes.xsd.XSDDatatype xsdDatatype){
        com.hp.hpl.jena.datatypes.RDFDatatype rdfDatatype = null;
        return rdfDatatype;
    }
    */

    /**
     * All XSDDatatype
     */
    private static final com.hp.hpl.jena.datatypes.xsd.XSDDatatype allFormatsOfXSDDataTypes[] = new com.hp.hpl.jena.datatypes.xsd.XSDDatatype[]{
            com.hp.hpl.jena.datatypes.xsd.XSDDatatype.XSDstring
    };

    /**
     * Method convert a XSDDatatype to a string
     * @param xsdDatatype
     * @return string
     */
    public static String XSDDatatypeToString(com.hp.hpl.jena.datatypes.xsd.XSDDatatype xsdDatatype) {
            String uri = xsdDatatype.getURI();
            return  uri;
 	}

    /**
     * Method convert a string to XSDDatatype
     * @param uri
     * @return xsdDatatype
     */
    public static com.hp.hpl.jena.datatypes.xsd.XSDDatatype stringToXSDDatatypeToString(String uri) {
            for (com.hp.hpl.jena.datatypes.xsd.XSDDatatype xsdDatatype : allFormatsOfXSDDataTypes) {
                   if(xsdDatatype.getURI().equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#"+uri)){
                       return xsdDatatype;
                   }
             }
            throw new IllegalArgumentException("The XSD Datatype '" + uri + "' is not recognised");
 	}

    /**
     * Method convert a string to a rdfformat
     * @param strFormat
     * @return rdfformat
     */
 	public static org.apache.jena.riot.RDFFormat stringToRDFFormat(String strFormat) {
        if(strFormat.toUpperCase().contains("NT") ||
                strFormat.toUpperCase().contains("NTRIPLES")|| strFormat.toUpperCase().contains("N3")){
            strFormat="N-Triples";
        }
        if(strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")){
            strFormat="Turtle";
        }
 		for (org.apache.jena.riot.RDFFormat rdfFormat : allFormatsOfRDFFormat) {
 			if (rdfFormat.getLang().getName().equalsIgnoreCase(strFormat))
 				return rdfFormat;
 		}
 		throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
 	}

    /**
     * Method convert a strin formar to a Lang
     * @param strFormat
     * @return lang
     */
 	public static org.apache.jena.riot.Lang stringToRiotLang(String strFormat) {	
            if(strFormat.toUpperCase().contains("NT") ||
                    strFormat.toUpperCase().contains("NTRIPLES")|| strFormat.toUpperCase().contains("N3")){
                 strFormat="N-Triples";
             }
            if(strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")){
                 strFormat="Turtle";
             }
            for (org.apache.jena.riot.Lang lang : allFormatsOfRiotLang) {
                String label = lang.getLabel();
                String name = lang.getName();

                if (lang.getName().equalsIgnoreCase(strFormat))
                      return lang;
 		}
 		throw new IllegalArgumentException("The LANG format '" + strFormat + "' is not recognised");
 	}


    /**
     * Method to print the resultSet to a a specific format of output
     * @param sparql
     * @param model
     * @param fullPathOutputFile
     * @param outputFormat
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void formatTheResultSetAndPrint(
            String sparql,com.hp.hpl.jena.rdf.model.Model model,String fullPathOutputFile,String outputFormat)throws FileNotFoundException, IOException{
            //JSON,CSV,TSV,,RDF,SSE,XML
            com.hp.hpl.jena.query.Query query;
            com.hp.hpl.jena.query.QueryExecution qexec;
            com.hp.hpl.jena.rdf.model.Model resultModel;
            com.hp.hpl.jena.query.ResultSet results;
            /*
            query = QueryFactory.insert(queryString) ;
            try (com.hp.hpl.jena.query.QueryExecution qexec2 = com.hp.hpl.jena.query.QueryExecutionFactory.insert(query, model)) {
                    com.hp.hpl.jena.query.ResultSet results = qexec2.execSelect() ;
              for ( ; results.hasNext() ; )
              {
                com.hp.hpl.jena.query.QuerySolution soln = results.nextSolution() ;
                com.hp.hpl.jena.rdf.model.RDFNode x = soln.get("varName") ;       // Get a result variable by name.
                com.hp.hpl.jena.rdf.model.Resource r = soln.getResource("VarR") ; // Get a result variable - must be a resource
                com.hp.hpl.jena.rdf.model.Literal lt = soln.getLiteral("VarL") ;   // Get a result variable - must be a literal
              }
            }
            */
            if(outputFormat.toLowerCase().contains("csv")||outputFormat.toLowerCase().contains("xml")
               ||outputFormat.toLowerCase().contains("json")||outputFormat.toLowerCase().contains("tsv")
               ||outputFormat.toLowerCase().contains("sse")||outputFormat.toLowerCase().contains("bio")
               ||outputFormat.toLowerCase().contains("rdf")||outputFormat.toLowerCase().contains("bio")
                    ){
//               try (com.hp.hpl.jena.query.QueryExecution qexec2 =
//                          com.hp.hpl.jena.query.QueryExecutionFactory.insert(sparql, model)) {
//                      results = qexec2.execSelect() ;
//               }
                results = execSparqlSelectOnModel(sparql,model);
                //PRINT THE RESULT
                FileOutputStream fos = new FileOutputStream(new File(fullPathOutputFile));
                if(outputFormat.toLowerCase().contains("csv")){
                    com.hp.hpl.jena.query.ResultSetFormatter.outputAsCSV(fos,results);
                }else if(outputFormat.toLowerCase().contains("xml")){
                    com.hp.hpl.jena.query.ResultSetFormatter.outputAsXML(fos,results);
                }else if(outputFormat.toLowerCase().contains("json")){
                    com.hp.hpl.jena.query.ResultSetFormatter.outputAsJSON(fos,results);
                }else if(outputFormat.toLowerCase().contains("tsv")){
                    com.hp.hpl.jena.query.ResultSetFormatter.outputAsTSV(fos,results);
                }else if(outputFormat.toLowerCase().contains("sse")){
                    com.hp.hpl.jena.query.ResultSetFormatter.outputAsSSE(fos, results);
                }else if(outputFormat.toLowerCase().contains("bio")){
                   //com.hp.hpl.jena.query.ResultSetFormatter.outputAsBIO(fos,results);
                }else if(outputFormat.toLowerCase().contains("rdf")){
                    com.hp.hpl.jena.query.ResultSetFormatter.outputAsRDF(fos, "RDF/XML", results);
                }
            }

            else if(outputFormat.toLowerCase().contains("ttl")){
                resultModel = execSparqlConstructorOnModel(sparql,model);
                //FileOutputStream out = new FileOutputStream("Jena_test.n3");
                Writer writer = new FileWriter(new File(fullPathOutputFile));
                model.write(writer, outputFormat);
            }
    }

    /**
     * Method to convert to RDF to a specific format
     * @param file
     * @param outputFormat
     * @throws IOException
     */
    public static void ConvertRDFTo(File file,String outputFormat) throws IOException{
         com.hp.hpl.jena.rdf.model.Model model = loadFileTriple(file);
         String newName = FileUtil.filenameNoExt(file)+"."+outputFormat.toLowerCase();
         String newPath = FileUtil.path(file);
         String sparql;
        if(outputFormat.toLowerCase().contains("csv")||outputFormat.toLowerCase().contains("xml")
             ||outputFormat.toLowerCase().contains("json")||outputFormat.toLowerCase().contains("tsv")
             ||outputFormat.toLowerCase().contains("sse")||outputFormat.toLowerCase().contains("bio")
             ||outputFormat.toLowerCase().contains("rdf")||outputFormat.toLowerCase().contains("bio")
           ){
             sparql ="SELECT * WHERE{?s ?p ?o}";}
        else{
             sparql ="CONSTRUCT {?s ?p ?o} WHERE{?s ?p ?o}";}
        formatTheResultSetAndPrint(sparql,model,newPath+File.separator+newName,outputFormat.toLowerCase()  );
    }

	/*
		There are                               To go in the reverse direction:
		    Resource.asNode() -> Node               Model.asRDFNode(Node)
		    Literal.asNode() -> Node                Model.asStatement(Triple)
        Statement.asTriple() -> Triple              ModelFactory.createModelForGraph(Graph)
		    Model.getGraph() -> Graph

		If you do an ARQ query, you can get the Node-level result ResultSet.nextBinding()
		which maps Var to Node. Var is ARQ's extension of Node_Variable. Create with Var.alloc(...)
		You don't need to cast to Node_URI (it's an implementation class really) - at the SPI, there
		are "Nodes" as generic items (and you can insert Triples that aren't RDF like ones with variables).*/
   /**
    * Get the dc:relation property
    * @return dc:relation property
    */
    /*
   public List<Enhancement> getRelation(){
     if (relation == null && resource.hasProperty(DCTerms.relation)) {
       relation=new ArrayList<Enhancement>();
       final StmtIterator relationsIterator=resource.listProperties(DCTerms.relation);
       while (relationsIterator.hasNext()) {
         final Statement relationStatement=relationsIterator.next();
         relation.add(EnhancementParser.parse(relationStatement.getObject().asResource()));
       }
     }
     return relation;
   }
   */
    // </editor-fold >

    // <editor-fold defaultstate="collapsed" desc="JenaKit Method 2015-01">

    /**
     * Get current working directory as a URI.
     */
    public static String uriFromCwd() {
        String cwd = System.getProperty("user.dir");
        return uriFromFilename( cwd ) + "/" ;
    }

    /**
     * Convert File descriptor string to a URI.
     */
    public static String uriFromFile(File filespec){
        return uriFromFilename( filespec.getAbsolutePath() ) ;
    }

    /**
     * Convert filename string to a URI.
     * Map '\' characters to '/' (this might break if '\' is used in
     * a Unix filename, but this is assumed to be a very rare occurrence
     * as '\' is often used with special meaning on Unix.)
     * For unix-like systems, the absolute filename begins with a '/'
     * and is preceded by "file://".
     * For other systems an extra '/' must be supplied.
     */
    public static String uriFromFilename(String filename){
        StringBuffer mapfilename = new StringBuffer( filename ) ;
        for ( int i = 0 ; i < mapfilename.length() ; i++ )
        {
            if ( mapfilename.charAt(i) == '\\' )
                mapfilename.setCharAt(i, '/') ;
        }
        if (filename.charAt(0) == '/')
        {
            return "file://"+mapfilename.toString() ;
        }
        else
        {
            return "file:///"+mapfilename.toString() ;
        }
    }

    /**
     * Method to find a property
     * @param model
     * @param subject
     * @param property
     * @return bool result
     */
    public static boolean findProperty(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Resource subject,String property){
        boolean foundLocal = false;
        try {
            int pos = property.indexOf(":");
            String prefix = property.substring(0, pos);
            property = property.substring(pos + 1);

            String uri = (String) namespaces.get(prefix);

            com.hp.hpl.jena.rdf.model.Property p = null;
            if (!"".equals(property)) {
                p = model.createProperty(uri, property);
            }

            com.hp.hpl.jena.rdf.model.StmtIterator iter =
                    model.listStatements(
                            new com.hp.hpl.jena.rdf.model.impl.SelectorImpl( subject, p,(com.hp.hpl.jena.rdf.model.RDFNode) null));
            while (iter.hasNext() && !foundLocal) {
                com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement) iter.next();
                com.hp.hpl.jena.rdf.model.Property sp = stmt.getPredicate();

                if (uri.equals(sp.getNameSpace())
                        && ("".equals(property)
                        || sp.getLocalName().equals(
                        property))) {
                    foundLocal = true;
                }
            }
        } catch (Exception e) {
            // nop
        }
        return foundLocal;
    }

    /**
     * Method to copy a Model to another
     * @param model
     * @param subject
     * @param uri
     * @return
     */
    public static com.hp.hpl.jena.rdf.model.Model copyModel(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Resource subject,String uri) {
        try {
            com.hp.hpl.jena.rdf.model.Model newModel = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
            com.hp.hpl.jena.rdf.model.Resource newSubject = newModel.createResource(uri);
            // Copy prefix mappings to the new model...
            newModel.setNsPrefixes(model.getNsPrefixMap());
            copyToModel(model, subject, newModel, newSubject);
            return newModel;
        } catch (Exception e) {
            System.err.println("Failed: " + e);
            e.printStackTrace();
            System.exit(2);
        }
        return null;
    }

    /**
     * Method to copy a Model to another
     * @param srcModel
     * @param srcRsrc
     * @param destModel
     * @param destRsrc
     */
    public static void copyToModel(
            com.hp.hpl.jena.rdf.model.Model srcModel,com.hp.hpl.jena.rdf.model.Resource srcRsrc,
            com.hp.hpl.jena.rdf.model.Model destModel,com.hp.hpl.jena.rdf.model.Resource destRsrc) {
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator iter = srcModel.listStatements(
                    new com.hp.hpl.jena.rdf.model.impl.SelectorImpl(srcRsrc,null,(com.hp.hpl.jena.rdf.model.RDFNode) null));
            while (iter.hasNext()) {
                com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement) iter.next();
                com.hp.hpl.jena.rdf.model.RDFNode obj = stmt.getObject();

                if (obj instanceof com.hp.hpl.jena.rdf.model.Resource) {
                    com.hp.hpl.jena.rdf.model.Resource robj = (com.hp.hpl.jena.rdf.model.Resource) obj;
                    if (robj.isAnon()) {
                        com.hp.hpl.jena.rdf.model.Resource destSubResource = destModel.createResource();
                        copyToModel(srcModel, robj, destModel, destSubResource);
                        obj = destSubResource;
                    }
                }
                com.hp.hpl.jena.rdf.model.Statement newStmt =
                        destModel.createStatement(destRsrc,stmt.getPredicate(),obj);
                destModel.add(newStmt);
            }
        } catch (Exception e) {
            System.err.println("Failed: " + e);
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * Method to load a Model
     * @param filename
     * @return model
     */
    public static com.hp.hpl.jena.rdf.model.Model loadModel(String filename) {
        // I used to pass encoding in here, but that's dumb. I'm reading XML
        // which is self-describing.
        com.hp.hpl.jena.rdf.model.Model model = com.hp.hpl.jena.rdf.model.ModelFactory.createDefaultModel();
        SystemLog.message("Loading " + filename + "...");
        try {
            File inputFile = new File(filename);
            FileInputStream input = new FileInputStream(inputFile);
            if (input == null) {
                SystemLog.warning("Failed to open " + filename);
            }
            model.read(input, uriFromFile(inputFile));
            input.close();
        } catch (FileNotFoundException fnfe) {
            SystemLog.exception(fnfe);
        } catch (UnsupportedEncodingException e) {
            SystemLog.exception(e);
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return model;
    }

    /**
     * Method to merge two Model
     * @param model
     * @param newModel
     * @return model
     */
    public static com.hp.hpl.jena.rdf.model.Model mergeModel(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Model newModel) {
        try {
            com.hp.hpl.jena.rdf.model.ResIterator ri = newModel.listSubjects();
            while (ri.hasNext()) {
                com.hp.hpl.jena.rdf.model.Resource newSubject = (com.hp.hpl.jena.rdf.model.Resource) ri.next();
                com.hp.hpl.jena.rdf.model.Resource subject;
                if (newSubject.isAnon()) {
                    // nevermind; copyToModel will handle this case recursively
                } else {
                    subject = model.createResource(newSubject.getURI());
                    copyToModel(newModel, newSubject, model, subject);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed: " + e);
            e.printStackTrace();
            System.exit(2);
        }
        return model;
    }

    /**
     * Method to delete literal
     * @param model
     * @param subject
     * @param property
     * @param value
     */
    public static void deleteLiteral(com.hp.hpl.jena.rdf.model.Model model,
                                     com.hp.hpl.jena.rdf.model.Resource subject,String property,String value) {
        String prefix = "";
        int pos = property.indexOf(":");
        prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        try {
            String uri = (String) namespaces.get(prefix);
            com.hp.hpl.jena.rdf.model.Property p = model.createProperty(uri, property);
            com.hp.hpl.jena.rdf.model.RDFNode v =(com.hp.hpl.jena.rdf.model.RDFNode) model.createLiteral(value);
            com.hp.hpl.jena.rdf.model.Statement s = model.createStatement(subject, p, v);
            model.remove(s);
        } catch (Exception e) {
            // nop;
        }
    }

    /**
     * Method to query a literal
     * @param model
     * @param subject
     * @param property
     * @param prefix
     * @return string
     */
    public static String queryLiteral(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Resource subject,String property,String prefix){
        int pos = property.indexOf(":");
        prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        try {
            com.hp.hpl.jena.rdf.model.Property p = null;
            if(prefix!=null) {
                String uri = (String) namespaces.get(prefix);
                if(!uri.isEmpty()||uri!=null) {
                    p = model.createProperty(uri, property);
                }else{
                    p = model.createProperty(property);
                }
            }else{
                p = model.createProperty(property);
            }
            com.hp.hpl.jena.rdf.model.RDFNode v = null;
            com.hp.hpl.jena.rdf.model.StmtIterator iter = model.listStatements(
                    new com.hp.hpl.jena.rdf.model.impl.SelectorImpl(subject, p, v));
            while (iter.hasNext()) {
                com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement) iter.next();
                com.hp.hpl.jena.rdf.model.RDFNode obj = stmt.getObject();
                if (obj instanceof com.hp.hpl.jena.rdf.model.Literal) {
                    return obj.toString();
                }
            }
        } catch (Exception e){
        }
        return null;
    }

    /**
     * Method to update a literal on a model jena
     * @param model
     * @param subject
     * @param property
     * @param value
     * @param prefix
     */
    public static void updateLiteral(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Resource subject,String property,String value,String prefix) {
        try {
            String rdfValue = queryLiteral(model, subject, property,prefix);
            if (value != null && !value.equals(rdfValue)) {
                SystemLog.message("Updating " + property + "=" + value);
                deleteLiteral(model, subject, property, rdfValue);
                int pos = property.indexOf(":");
                prefix = property.substring(0, pos);
                property = property.substring(pos + 1);
                String uri = (String) namespaces.get(prefix);
                com.hp.hpl.jena.rdf.model.Property p = model.createProperty(uri, property);
                com.hp.hpl.jena.rdf.model.RDFNode v =(com.hp.hpl.jena.rdf.model.RDFNode) model.createLiteral(value);
                com.hp.hpl.jena.rdf.model.Statement s = model.createStatement(subject, p, v);
                model.add(s);
            }
        } catch (Exception e) {
            System.out.println("Exception in ul");
            e.printStackTrace();
            // nop;
        }
    }

    /**
     *  Method to get the namespaces on a model jena
     * @param namespace
     * @return
     */
    public static String getNamespacePrefix(String namespace) {
        if (namespaces.containsValue(namespace)) {
            // find it...
            Enumeration keys = namespaces.keys();
            while (keys.hasMoreElements()) {
                String prefix = (String) keys.nextElement();
                if (namespace.equals((String) namespaces.get(prefix))) {
                    return prefix;
                }
            }
            System.err.println("Internal error: this can't happen.");
            return "XXX";
        } else {
            // add it...
            String p = "dp";
            int num = 0;
            String prefix = p + num;
            while (namespaces.containsKey(prefix)) {
                num++;
                prefix = p + num;
            }
            namespaces.put(prefix, namespace);
            return prefix;
        }
    }

    /**
     * Method to convert a model to a string object
     * @param showRDF
     * @param baseURI
     * @return
     */
    public static String modelToString(com.hp.hpl.jena.rdf.model.Model showRDF, String baseURI) {
        StringOutputStreamKit stringOutput = new StringOutputStreamKit();
        showRDF.write(stringOutput, "RDF/XML-ABBREV", baseURI);
        String rawString = stringOutput.toString();
        // The rawString contains the octets of the utf-8 representation of the
        // data as individual characters. This is really unusual, but it's true.
        byte[] utf8octets = new byte[rawString.length()];
        for (int i = 0; i < rawString.length(); i++) {
            utf8octets[i] = (byte) rawString.charAt(i);
        }
        // Turn these octets back into a proper utf-8 string.
        try {
            rawString = new String(utf8octets, "utf-8");
        } catch (UnsupportedEncodingException uee) {
            // this can't happen
        }
        // Now encode it "safely" as XML
        String rdfString = XMLKit_Extends.xmlEncode(rawString);
        return rdfString;
    }

    /**
     * Method to delete a specific resource , property on model jena
     * @param model
     * @param subject
     * @param property
     */
    public static void delete(com.hp.hpl.jena.rdf.model.Model model, com.hp.hpl.jena.rdf.model.Resource subject, String property) {
        String prefix = "";
        int pos = property.indexOf(":");
        prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        com.hp.hpl.jena.rdf.model.Property p = null;
        String uri = (String) namespaces.get(prefix);
        if (!"".equals(property)) {
            p = model.createProperty(uri, property);
        }

        com.hp.hpl.jena.rdf.model.StmtIterator iter =
                model.listStatements(
                        new com.hp.hpl.jena.rdf.model.impl.SelectorImpl(subject,p,(com.hp.hpl.jena.rdf.model.RDFNode) null));
        while (iter.hasNext()) {
            com.hp.hpl.jena.rdf.model.Statement stmt = (com.hp.hpl.jena.rdf.model.Statement) iter.next();
            p = stmt.getPredicate();
            if (p.getNameSpace() == null) {
                continue;
            }
            if (p.getNameSpace().equals(uri)) {
                String type = "literal";
                if (stmt.getObject()instanceof com.hp.hpl.jena.rdf.model.Resource) {
                    type = "resource";
                }
                SystemLog.message("\tdelete " + type + ": " + prefix + ":" + p.getLocalName()
                        + "=" + stmt.getObject().toString());
                model.remove(stmt);
            }
        }
    }
    // </editor-fold >

    // <editor-fold defaultstate="collapsed" desc="JenaKit Method 2015-02">

    /*
    USAGE:
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2003);
    calendar.set(Calendar.MONTH, 10);
    calendar.set(Calendar.DATE, 28);
    calendar.set(Calendar.HOUR_OF_DAY, 10);
    calendar.set(Calendar.MINUTE, 5);
    calendar.set(Calendar.SECOND, 35);
    calendar.set(Calendar.ZONE_OFFSET, -18000000);
    Date date = calendar.getTime();
    String iso = toIsoDate(date);
    System.out.println("iso = " + iso);
    Date date2 = fromIsoDate(iso);
     */
    /**
     * Taken from com.idea.io.RdfUtils, modified for Jena 2
     */
    /*
     public static Resource updateProperties(Resource resource, GraphVertexChangeEvent vertex){
         setProperty(resource, RSS.title, vertex.getVertex())); return resource;
     }
    */

    /**
     * Method to set a property on a model jena
     * @param resource
     * @param property
     * @param value
     */
    public static void setProperty(
            com.hp.hpl.jena.rdf.model.Resource resource,com.hp.hpl.jena.rdf.model.Property property,Object value) {
        try {
            /*
            StmtIterator iterator =
                    resource.listProperties(property);
            while (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
            }
            */
            resource.removeAll(property);
            resource.addProperty(property, (com.hp.hpl.jena.rdf.model.RDFNode) value);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to get first the value of the property
     * can use model.getProperty() directly now?
     * @param resource
     * @param property
     * @return
     */
    public static com.hp.hpl.jena.rdf.model.RDFNode getFirstPropertyValue(
            com.hp.hpl.jena.rdf.model.Resource resource,com.hp.hpl.jena.rdf.model.Property property) {
        /*
        try {

            StmtIterator test = resource.listProperties();
            if (resource.hasProperty(property)) {

                    StmtIterator iterator =
                            resource.listProperties(property);
                    Statement statement =
                            (Statement) iterator.next();
                    return statement.getObject();
                    // changed for Jena 2
            }
        } catch (Exception exception) {
                exception.printStackTrace();
        }
        */
        com.hp.hpl.jena.rdf.model.Statement statement = resource.getProperty(property);
        if(statement == null){
            return null;
        }
        return statement.getObject();
    }

    /**
     * Method to get the rdf type
     * @param resource
     * @return
     */
    public static String getRdfType(com.hp.hpl.jena.rdf.model.Resource resource) {
        if (resource.isAnon()) {
            // @@TODO this whole lot needs improving
            return "anon";
        }
        //show(resource);
        com.hp.hpl.jena.rdf.model.RDFNode type = getFirstPropertyValue(resource, com.hp.hpl.jena.vocabulary.RDF.type);
        if (type == null) {
            return "untyped";
        }
        return type.toString();
    }

    /**
     * Method to get the uri froma reosurce on a model jena
     * @param resource
     * @param uri
     */
    public static void setUri(com.hp.hpl.jena.rdf.model.Resource resource, URI uri) {
        try {
            com.hp.hpl.jena.rdf.model.Model model = resource.getModel();
            // insert a new resource
            com.hp.hpl.jena.rdf.model.Resource newResource = model.createResource(uri.toString());
            com.hp.hpl.jena.rdf.model.StmtIterator iterator = resource.listProperties();
            // copy properties from old resource
            // buffer used to avoid concurrent modification
            Set statements = new HashSet();
            while (iterator.hasNext()) {
                com.hp.hpl.jena.rdf.model.Statement stmt =(com.hp.hpl.jena.rdf.model.Statement) iterator.next();
                statements.add(stmt);
                // changed for Jena 2
                newResource.addProperty(stmt.getPredicate(), stmt.getObject());
                //model.remove(stmt);
            }
            Iterator setIterator = statements.iterator();
            com.hp.hpl.jena.rdf.model.Statement statement;
            while (setIterator.hasNext()) {
                statement = (com.hp.hpl.jena.rdf.model.Statement) setIterator.next();
                if (model.contains(statement)) {
                    model.remove(statement);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to get the title from a ID
     * @param title
     * @return string
     */
    public static String titleToID(String title) {
        return title.replace(' ', '_');
    }

    /**
     * Method to get the isodate from a date
     * @param date
     * @return isodate
     */
    public static String toISODate(Date date) {
        return isoDate.format(date);
    }

    /**
     * Method to show a statement on a model jena
     * @param statement
     */
    public static void show(com.hp.hpl.jena.rdf.model.Statement statement) {
        show(statement.getSubject());
        show(statement.getPredicate());
        if (statement.getObject() instanceof com.hp.hpl.jena.rdf.model.Resource) {
            show((com.hp.hpl.jena.rdf.model.Resource) statement.getObject());
        } else {
            System.out.println(statement.getObject());
        }
    }

    /**
     * Method to show a resource on a model jena
     * @param resource
     */
    public static void show(com.hp.hpl.jena.rdf.model.Resource resource) {
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator iterator =resource.listProperties();
            show(iterator);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to show a iterators on a model jena
     * @param iterator
     */
    public static void show(com.hp.hpl.jena.rdf.model.StmtIterator iterator) {
        StringBuffer buffer = new StringBuffer("\n--v--");
        try {
            //StmtIterator iterator = resource.listProperties();
            while (iterator.hasNext()) {
                buffer.append( "\n" + iterator.next().toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        buffer.append("\n--^--");
        System.out.println(buffer);
    }

    /**
     * Method to show a model jena
     * @param model
     */
    public static void show(com.hp.hpl.jena.rdf.model.Model model) {
        System.out.println(modelToString(model));
    }

    /**
     * Method to convert a model jena to string
     * @param model
     * @return string of the model
     */
    public static String modelToString(com.hp.hpl.jena.rdf.model.Model model) {
        //System.out.println("MODEL TO STRPING");
        if (model == null) {
            return "Null Model.";
        }
        StringWriter stringOut = new StringWriter();
        try {
            //setCommonPrefixes(model);
            model.write(stringOut,"RDF/XML-ABBREV",com.hp.hpl.jena.vocabulary.RSS.getURI());
            // http://base
            stringOut.flush();
            stringOut.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return stringOut.toString();
    }



    /**
     * Method to store file of triple to a model jena in aspecific uri
     * @param model
     * @param uri
     * @param filename
     */
    // @@TODO needs updating to Stream rather than Writer
    @Deprecated
    public static void store(com.hp.hpl.jena.rdf.model.Model model, String uri,String filename) {
        //System.out.println("SToring");
        uri = com.hp.hpl.jena.vocabulary.RSS.getURI(); //@@
        try {
            /*
             * getPrettyWriter().write( model, new FileWriter(filename), uri);
             * setCommonPrefixes(model);
             */
            model.write(new FileWriter(filename), RDF_FORMAT);
            //RSS.getURI()
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to set the prefix on a model jena
     * @param model
     * @param namespaces
     * @return the model jena with the prefix
     */
    public static com.hp.hpl.jena.rdf.model.Model setCommonPrefixes(
            com.hp.hpl.jena.rdf.model.Model model,Map<String,String> namespaces) {
        for(Map.Entry entry : namespaces.entrySet()) {
            //model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
            //model.setNsPrefix("vis", "http://ideagraph.org/xmlns/idea/graphic#");
            model.setNsPrefix(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return model;
    }

    /**
     * Method to load a file of triple on a model jena
     * @param filename
     * @return
     */
    public static com.hp.hpl.jena.rdf.model.Model load(String filename) {
        return load(JenaKit.setNewDefaultModel(), filename);
    }

    /**
     * Method to load a file of triple on a model jena
     * @param model
     * @param filename
     * @return
     */
    // @@TODO needs updating to Stream rather than Reader
    @Deprecated
    public static com.hp.hpl.jena.rdf.model.Model load(
            com.hp.hpl.jena.rdf.model.Model model,String filename) {
        try {
            InputStream inputStream = new FileInputStream(filename);
            //model.read(new FileReader(filename), "");
            model.read(inputStream, "", RDF_FORMAT);
        } catch (Exception e) {
            System.out.println("not found file to read");
            //model = new ModelMem();
            store(model, filename);
        }
        return model;
    }

    /**
     * Method to replace a resource on a model jena
     * @param model
     * @param oldResource
     * @param newResource
     * @return
     */
    public static com.hp.hpl.jena.rdf.model.Model replaceResource(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Resource oldResource,
            com.hp.hpl.jena.rdf.model.Resource newResource) {
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator statements = model.listStatements();
            com.hp.hpl.jena.rdf.model.Statement statement;
            com.hp.hpl.jena.rdf.model.Resource subject;
            com.hp.hpl.jena.rdf.model.RDFNode object = null;
            // buffer in List to avoid concurrent modification exception
            List statementList = new ArrayList();
            while (statements.hasNext()) {
                statementList.add(statements.next());
            }

            for (int i = 0;i < statementList.size(); i++) {
                statement = (com.hp.hpl.jena.rdf.model.Statement) statementList.get(i);
                subject = statement.getSubject();
                object = statement.getObject();
                if (subject.equals(oldResource)) {
                    replaceSubjectResource(statement,newResource);
                }
                if ((object instanceof com.hp.hpl.jena.rdf.model.Resource) &&
                        (oldResource.equals((com.hp.hpl.jena.rdf.model.Resource) object))) {
                    replaceObjectResource( statement,newResource);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return model;
    }

    /**
     * Method to replace a subject/resource on a model jena
     * @param statement
     * @param newSubject
     */
    public static void replaceSubjectResource(
            com.hp.hpl.jena.rdf.model.Statement statement,com.hp.hpl.jena.rdf.model.Resource newSubject) {
        com.hp.hpl.jena.rdf.model.Statement newStatement;
        try {
            com.hp.hpl.jena.rdf.model.Model model = statement.getModel();
            newStatement = model.createStatement(newSubject,
                    statement.getPredicate(),statement.getObject());
            model.remove(statement);
            model.add(newStatement);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void replaceObjectResource(
            com.hp.hpl.jena.rdf.model.Statement statement,com.hp.hpl.jena.rdf.model.Resource newObject) {
        com.hp.hpl.jena.rdf.model.Statement newStatement;
        try {
            com.hp.hpl.jena.rdf.model.Model model = statement.getModel();
            newStatement =model.createStatement(statement.getSubject(),
                    statement.getPredicate(),newObject);
            model.remove(statement);
            model.add(newStatement);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Copies all properties across to new resource, just replaces type
     *
     * @param resource
     * @param newType
     */
    public static void replaceType(com.hp.hpl.jena.rdf.model.Resource resource,com.hp.hpl.jena.rdf.model.Resource newType) {
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator iterator = resource.listProperties();
            com.hp.hpl.jena.rdf.model.Property property = null;
            com.hp.hpl.jena.rdf.model.Statement statement = null;

            while (iterator.hasNext()) {
                statement = (com.hp.hpl.jena.rdf.model.Statement) iterator.next();
                property = statement.getPredicate();
                //		System.out.println("property = "+property);
                if (property.equals(com.hp.hpl.jena.vocabulary.RDF.type)) {
                    break; // to stop concurrent mod exc
                }
            }
            if (property.equals(com.hp.hpl.jena.vocabulary.RDF.type)) {
                resource.getModel().remove(statement);
                resource.addProperty(com.hp.hpl.jena.vocabulary.RDF.type, newType);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    //	approximate : returns first match
    public static com.hp.hpl.jena.rdf.model.Resource getParent(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.RDFNode rdfNode) {
            /*
             * Statement statement; // Resource parent; try { StmtIterator iterator =
             * model.listStatements(); while (iterator.hasNext()) { statement =
             * iterator.next(); if (rdfNode.equals(statement.getObject())) {
             * //parent = statement.getSubject(); if
             * (!(RDF.type).equals(statement.getPredicate())) { return
             * statement.getSubject(); } } } } catch (Exception exception) {
             * exception.printStackTrace(); } return null;
             */
        if (rdfNode instanceof com.hp.hpl.jena.rdf.model.Property) {
            return getParentResource(model,(com.hp.hpl.jena.rdf.model.Property) rdfNode);
        }
        return getParentProperty(model, rdfNode);
    }

    //	approximate : returns predicate of first statement with matching object

    /**
     *
     * @param model
     * @param rdfNode
     * @return
     */
    public static com.hp.hpl.jena.rdf.model.Property getParentProperty(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.RDFNode rdfNode) {
        com.hp.hpl.jena.rdf.model.Statement statement = getParentStatement(model, rdfNode);
        if (statement == null) {
            return null;
        }
        return statement.getPredicate();
        /*
         * Statement statement;
         *
         * try { StmtIterator iterator = model.listStatements();
         *
         * while(iterator.hasNext()) { statement = iterator.next();
         *
         * if(rdfNode.equals(statement.getObject())) { //parent =
         * statement.getSubject();
         * if(!(RDF.type).equals(statement.getPredicate())) { return
         * statement.getPredicate(); } } } } catch(Exception exception) {
         * exception.printStackTrace(); }
         *
         * return null;
         */
    }

    // approximate : returns first statement with matching object
    public static com.hp.hpl.jena.rdf.model.Statement getParentStatement(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.RDFNode rdfNode) {
        com.hp.hpl.jena.rdf.model.Statement statement;
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator iterator = model.listStatements();
            while (iterator.hasNext()) {
                statement = (com.hp.hpl.jena.rdf.model.Statement) iterator.next();
                if (rdfNode.equals(statement.getObject())) {
                    //parent = statement.getSubject();
                    if (!(com.hp.hpl.jena.vocabulary.RDF.type).equals(statement.getPredicate())) {
                        return statement;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    //	approximate : returns object of first statement with matching predicate
    public static com.hp.hpl.jena.rdf.model.Resource getParentResource(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Property property) {
        com.hp.hpl.jena.rdf.model.Statement statement;
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator iterator = model.listStatements();

            while (iterator.hasNext()) {
                statement = (com.hp.hpl.jena.rdf.model.Statement) iterator.next();
                //changed for Jena 2
                if (property.equals(statement.getPredicate())) {
                    return statement.getSubject();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    // approximate : gets first match (predicate and object)
    public static com.hp.hpl.jena.rdf.model.Resource getSubject(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Property property,
            com.hp.hpl.jena.rdf.model.RDFNode object) {
        com.hp.hpl.jena.rdf.model.Statement statement =getStatement(model, property, object);
        if (statement == null) {
            return null;
        }
        return statement.getSubject();
    }

    // approximate : gets first match (predicate and object)
    public static com.hp.hpl.jena.rdf.model.Statement getStatement(
            com.hp.hpl.jena.rdf.model.Model model,com.hp.hpl.jena.rdf.model.Property property,
            com.hp.hpl.jena.rdf.model.RDFNode object) {
        com.hp.hpl.jena.rdf.model.Statement statement;
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator iterator = model.listStatements();
            while (iterator.hasNext()) {
                statement = (com.hp.hpl.jena.rdf.model.Statement) iterator.next();
                if (property.equals(statement.getPredicate())&& object.equals(statement.getObject())) {
                    return statement;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    ///////////////// beware of the GMT!
    public static String toIsoDate(Date date) {
        return isoDate.format(date);
    }

    // 2003-10-29T10:05:35-05:00
    public static Date fromIsoDate(String string) {
        Date date = null;
        System.out.println(string);
        string =string.substring(0, 19)+ "GMT"+ string.substring(19);
        System.out.println(string);
        try {
            date = isoDate.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*
     * public static void setPropertyObject( Resource resource, Property
     * property, Resource object) { try { StmtIterator iterator =
     * resource.listProperties(property);
     *
     * while (iterator.hasNext()) { iterator.next(); iterator.remove(); }
     *
     * resource.addProperty(property, object); } catch (Exception exception) {
     * exception.printStackTrace(); } }
     */
    public static String getProperty(com.hp.hpl.jena.rdf.model.Resource resource,com.hp.hpl.jena.rdf.model.Property property) {
        //	System.out.println("����");
        //	show(resource.getModel());
        //	show(resource);
        //	System.out.println("resource = "+resource);
        //System.out.println("property = "+property);
        //show(property);
        com.hp.hpl.jena.rdf.model.RDFNode node = getFirstPropertyValue(resource, property);
        if (node == null) {
            return null;
        }
        return node.toString();
    }

    // @@TODO needs updating to Stream rather than Writer
    public static void store(com.hp.hpl.jena.rdf.model.Model model,String filename) {
        System.out.println("storing " + filename);
        //setCommonPrefixes(model);
        try {
            OutputStream outputStream =  new FileOutputStream(filename);
            model.write(outputStream, RDF_FORMAT);
            /*
             * getPrettyWriter().write( model, new FileWriter(filename),
             * RSS.getURI());
             */
        } catch (Exception exception) {
            System.err.println("error on storing - \nfilename:"+ filename
                    + "\n model : " + model);
            exception.printStackTrace();
        }
    }

    public static void setPropertyObject(
            com.hp.hpl.jena.rdf.model.Resource resource,com.hp.hpl.jena.rdf.model.Property property,
            com.hp.hpl.jena.rdf.model.Resource object) {
        try {
            com.hp.hpl.jena.rdf.model.StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }

            resource.addProperty(property, object);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void replaceLiteralValue(com.hp.hpl.jena.rdf.model.Model model,  com.hp.hpl.jena.rdf.model.Literal literal, String value){
        com.hp.hpl.jena.rdf.model.Literal newLiteral = model.createLiteral(value);
        Set statements = new HashSet();
        com.hp.hpl.jena.rdf.model.StmtIterator iterator =model.listStatements(null,null,literal);
        while (iterator.hasNext()) {
            statements.add(iterator.next());
        }
        Iterator setIterator = statements.iterator();
        com.hp.hpl.jena.rdf.model.Statement statement;
        while (setIterator.hasNext()) {
            statement = (com.hp.hpl.jena.rdf.model.Statement) setIterator.next();
            model.add(statement.getSubject(),statement.getPredicate(),newLiteral);
            model.remove(statement);
        }
    }

    public static com.hp.hpl.jena.rdf.model.Resource r (String BASE, String localname ) {
        return com.hp.hpl.jena.rdf.model.ResourceFactory.createResource ( BASE + localname );
    }

    public static com.hp.hpl.jena.rdf.model.Property p (String BASE, String localname ) {
        return com.hp.hpl.jena.rdf.model.ResourceFactory.createProperty(BASE, localname);
    }

    public static com.hp.hpl.jena.rdf.model.Property p (String uriref) {
        return com.hp.hpl.jena.rdf.model.ResourceFactory.createProperty(uriref);
    }

    public static com.hp.hpl.jena.rdf.model.Literal lp(String value) {
        return com.hp.hpl.jena.rdf.model.ResourceFactory.createPlainLiteral(value);
    }

    public static com.hp.hpl.jena.rdf.model.Literal lt(Object value) {
        return com.hp.hpl.jena.rdf.model.ResourceFactory.createTypedLiteral ( value );
    }

    public static com.hp.hpl.jena.rdf.model.Literal lt(String lexicalform, com.hp.hpl.jena.datatypes.RDFDatatype datatype) {
        return com.hp.hpl.jena.rdf.model.ResourceFactory.createTypedLiteral ( lexicalform, datatype );
    }

    public static InputStream getResourceAsStream(String filename) {
        InputStream in = JenaKit.class.getClassLoader().getResourceAsStream(filename);
        return in;
    }
}//end of the class JenaKit
