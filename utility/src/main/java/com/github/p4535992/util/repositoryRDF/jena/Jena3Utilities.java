package com.github.p4535992.util.repositoryRDF.jena;


import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.string.StringUtilities;
import com.github.p4535992.util.string.Timer;
import com.github.p4535992.util.xml.XMLUtilities;

/** if you use Jena 2.X.X */
/*import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.rdf.model.impl.SelectorImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.sparql.resultset.RDFOutput;
import com.hp.hpl.jena.sparql.util.NodeUtils;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RSS;*/
import org.apache.jena.atlas.lib.*;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.*;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.graph.impl.LiteralLabelFactory;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.SelectorImpl;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.DatasetImpl;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.resultset.RDFOutput;
import org.apache.jena.sparql.util.NodeUtils;
import org.apache.jena.tdb.TDB;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RSS;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class utility for Jena
 * Created by 4535992 in 2015-04-28.
 * href: https://gist.github.com/ijdickinson/3830267
 * NOTE: Work with Jena 2.
 * @author 4535992.
 * @version 2015-12-07.
 */
@SuppressWarnings("unused")
public class Jena3Utilities {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Jena3Utilities.class);

    //CONSTRUCTOR
    protected Jena3Utilities() {
    }

    private static Jena3Utilities instance = null;

    public static Jena3Utilities getInstance() {
        if (instance == null) {
            instance = new Jena3Utilities();
        }
        return instance;
    }

    //PRIVATE
    public static String INFORMAT, OUTFORMAT;
    public static Lang OUTLANGFORMAT, INLANGFORMAT;
    public static RDFFormat OUTRDFFORMAT, INRDFFORMAT;

    public static void setInput(RDFFormat INRDFFORMAT) {
        INFORMAT = INRDFFORMAT.getLang().getName();
        INLANGFORMAT = INRDFFORMAT.getLang();
    }

    public static void setOutput(RDFFormat OUTRDFFORMAT) {
        OUTFORMAT = OUTRDFFORMAT.getLang().getName();
        OUTLANGFORMAT = OUTRDFFORMAT.getLang();
    }


    private static Model model;
    private static final Map<String, String> namespaces = new HashMap<>();
    public static final String RDF_FORMAT = "RDF/XML-ABBREV";

    /**
     * Method  to Write large model jena to file of text.
     *
     * @param fullPath     string of the path to the file.
     * @param model        jena model to write.
     * @param outputFormat the output format you want to write the model.
     * @return if true all the operation are done.
     */
    public static boolean writeModelToFile(String fullPath, Model model, String outputFormat) {
        fullPath = FileUtilities.getPath(fullPath) + File.separator + FileUtilities.getFilenameWithoutExt(fullPath) + "." + outputFormat.toLowerCase();
        logger.info("Try to write the new file of triple from:" + fullPath + "...");
        OUTLANGFORMAT = toLang(outputFormat);
        OUTRDFFORMAT = toRDFFormat(outputFormat);
        OUTFORMAT = outputFormat.toUpperCase();
        try {
            try (FileWriter out = new FileWriter(fullPath)) {
                model.write(out, OUTLANGFORMAT.getName());
            }
        } catch (Exception e1) {
            logger.warn("...there is was a problem to try the write the triple file at the first tentative...");
            try {
                FileOutputStream outputStream = new FileOutputStream(fullPath);
                model.write(outputStream, OUTLANGFORMAT.getName());
            } catch (Exception e2) {
                logger.warn("...there is was a problem to try the write the triple file at the second tentative...");
                try {
                    Writer writer = new FileWriter(new File(fullPath));
                    model.write(writer, OUTFORMAT);
                } catch (Exception e3) {
                    logger.warn("...there is was a problem to try the write the triple file at the third tentative...");
                    try {
                        Charset ENCODING = StandardCharsets.UTF_8;
                        FileUtilities.createFile(fullPath);
                        Path path = Paths.get(fullPath);
                        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
                            model.write(writer, null, OUTLANGFORMAT.getName());
                        }
                    } catch (Exception e4) {
                        logger.error("... exception during the writing of the file of triples:" + fullPath);
                        logger.error(e4.getMessage(), e4);
                        return false;
                    }
                }
            }
        }
        logger.info("... the file of triple to:" + fullPath + " is been wrote!");
        return true;
    }

    public static boolean write(File file, Model model, String outputFormat) {
        try {
            logger.info("Try to write the new file of triple from:" + file.getAbsolutePath() + "...");
            FileOutputStream outputStream = new FileOutputStream(file);
            model.write(outputStream, toLang(outputFormat).getName());
            logger.info("... the file of triple to:" + file.getAbsolutePath() + " is been wrote!");
            return true;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(File file, Model model, String outputFormat, String baseUri) {
        try {
            logger.info("Try to write the new file of triple from:" + file.getAbsolutePath() + "...");
            FileOutputStream outputStream = new FileOutputStream(file);
            model.write(outputStream, toLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to:" + file.getAbsolutePath() + " is been wrote!");
            return true;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(OutputStream stream, Model model, String outputFormat) {
        try {
            logger.info("Try to write the new file of triple from stream ...");
            model.write(stream, toLang(outputFormat).getName());
            logger.info("... the file of triple to stream is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(OutputStream stream, Model model, String outputFormat, String baseUri) {
        try {
            logger.info("Try to write the new file of triple from stream ...");
            model.write(stream, toLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to stream is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(Writer writer, Model model, String outputFormat) {
        try {
            logger.info("Try to write the new file of triple from write ...");
            model.write(writer, toLang(outputFormat).getName());
            logger.info("... the file of triple to write is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static boolean write(Writer writer, Model model, String outputFormat, String baseUri) {
        try {
            logger.info("Try to write the new file of triple from write ...");
            model.write(writer, toLang(outputFormat).getName(), baseUri);
            logger.info("... the file of triple to write is been wrote!");
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

/*    public static void write(OutputStream s, Dataset d, String o) {
        RDFDataMgr.write(s, d, toLang(o));
    }

    public static void write(OutputStream s, Dataset d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(OutputStream s, Dataset d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(OutputStream s, DatasetGraph d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(OutputStream s, DatasetGraph d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(OutputStream s, Model m, Lang l) {
        RDFDataMgr.write(s, m, l);
    }

    public static void write(OutputStream s, Model m, RDFFormat f) {
        RDFDataMgr.write(s, m, f);
    }

    public static void write(OutputStream s, Graph g, Lang l) {
        RDFDataMgr.write(s, g, l);
    }

    public static void write(OutputStream s, Graph g, RDFFormat f) {
        RDFDataMgr.write(s, g, f);
    }

    public static void write(StringWriter s, Dataset d, String o) {
        RDFDataMgr.write(s, d, toLang(o));
    }

    public static void write(StringWriter s, Dataset d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(StringWriter s, Dataset d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(StringWriter s, DatasetGraph d, Lang l) {
        RDFDataMgr.write(s, d, l);
    }

    public static void write(StringWriter s, DatasetGraph d, RDFFormat f) {
        RDFDataMgr.write(s, d, f);
    }

    public static void write(StringWriter s, Model m, Lang l) {
        RDFDataMgr.write(s, m, l);
    }

    public static void write(StringWriter s, Model m, RDFFormat f) {
        RDFDataMgr.write(s, m, f);
    }

    public static void write(StringWriter s, Graph g, Lang l) {
        RDFDataMgr.write(s, g, l);
    }

    public static void write(StringWriter s, Graph g, RDFFormat f) {
        RDFDataMgr.write(s, g, f);
    }
    */

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     *
     * @param sparql the String sparql query.
     * @param model  Jena Model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlOnModel(String sparql, Model model) {
        return execSparqlOn(sparql, model);
    }

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     *
     * @param sparql  the String sparql query.
     * @param dataset Jena Dataset.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlOnDataset(String sparql, Dataset dataset) {
        return execSparqlOn(sparql, dataset);
    }

    /**
     * Method to exec a SPARQL query to a remote service.
     *
     * @param sparql        the String sparql query.
     * @param remoteService the String address web page to the web service endpoint SPARQL.
     * @return the result of the query.
     */
    public static Model execSparqlOnRemote(String sparql, String remoteService) {
        /*HttpAuthenticator authenticator = new PreemptiveBasicAuthenticator(
                new ScopedAuthenticator(new URI(SPARQLR_ENDPOINT), SPARQLR_USERNAME, SPARQLR_PASSWORD.toCharArray())
        );*/
        return execSparqlOn(sparql, remoteService);
    }

    private static Model execSparqlOn(String sparql, Object onObject) {
        Query query = QueryFactory.create(sparql);
        Model resultModel;
        QueryExecution qexec;
        if (onObject instanceof Dataset) {
            qexec = QueryExecutionFactory.create(sparql, (Dataset) onObject);
        } else if (onObject instanceof Model) {
            qexec = QueryExecutionFactory.create(query, (Model) onObject);
        } else if (onObject instanceof String) {
            //QueryEngineHTTP qexec = new QueryEngineHTTP(remoteService, sparql);
            //qexec.setBasicAuthentication("siimobility", "siimobility".toCharArray());
            qexec = QueryExecutionFactory.sparqlService(String.valueOf(onObject), query);
        } else {
            qexec = QueryExecutionFactory.create(query);
        }

        if (query.isSelectType()) {
            ResultSet results;
            RDFOutput output = new RDFOutput();
            results = qexec.execSelect();
            //... make exit from the thread the result of query
            results = ResultSetFactory.copyResults(results);
            logger.info("Exec query SELECT SPARQL :" + sparql);
            return output.asModel(results);
        } else if (query.isConstructType()) {
            resultModel = qexec.execConstruct();
            logger.info("Exec query CONSTRUCT SPARQL :" + sparql);
            return resultModel;
        } else if (query.isDescribeType()) {
            resultModel = qexec.execDescribe();
            logger.info("Exec query DESCRIBE SPARQL :" + sparql);
            return resultModel;
        } else if (query.isAskType()) {
            logger.info("Exec query ASK SPARQL :" + sparql);
            logger.warn("ATTENTION the SPARQL query:" + sparql + ".\n is a ASK Query can't return a Model object");
            return null;
        } else if (query.isUnknownType()) {
            logger.info("Exec query UNKNOWN SPARQL :" + sparql);
            logger.warn("ATTENTION the SPARQL query:" + sparql + ".\n is a UNKNOWN Query can't return a Model object");
            return null;
        } else {
            logger.error("ATTENTION the SPARQL query:" + sparql + ".\n is a NULL Query can't return a Model object");
            return null;
        }
    }

    /**
     * Method for execute a CONSTRUCTOR SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlConstructorOnModel(String sparql, Model model) {
        /*Query query = QueryFactory.create(sparql) ;
        Model resultModel ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        resultModel = qexec.execConstruct();
        logger.info("Exec query CONSTRUCT SPARQL :" + sparql);
        return  resultModel;*/
        return execSparqlOnModel(sparql, model);
    }

    /**
     * Method for execute a DESCRIIBE SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result of the query allocated on a Jena model.
     */
    public static Model execSparqlDescribeOnModel(String sparql, Model model) {
        /*Query query = QueryFactory.create(sparql) ;
        Model resultModel ;
        QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
        resultModel = qexec.execDescribe();
        logger.info("Exec query DESCRIBE SPARQL :" + sparql);
        return resultModel;*/
        return execSparqlOnModel(sparql, model);
    }

    /**
     * Method for execute a SELECT SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result set of the query.
     */
    public static ResultSet execSparqlSelectOnModel(String sparql, Model model) {
        ResultSet results;
        QueryExecution qexec = QueryExecutionFactory.create(sparql, model);
        results = qexec.execSelect();
        //... make exit from the thread the result of query
        results = ResultSetFactory.copyResults(results);
        logger.info("Exec query SELECT SPARQL :" + sparql);
        return results;
    }

    /**
     * Method to convert a ResultSet to a Model.
     *
     * @param resultSet the ResultSet Jena to convert.
     * @return the Model Jena populate with the ResultSet.
     */
    public static Model toModel(ResultSet resultSet) {
        RDFOutput output = new RDFOutput();
        return output.asModel(resultSet);
    }

    /**
     * Method for execute a ASK SPARQL on a Jena Model.
     *
     * @param sparql sparql query.
     * @param model  jena model.
     * @return the result set of the query like a boolean value.
     */
    public static boolean execSparqlAskOnModel(String sparql, Model model) {
        Query query = QueryFactory.create(sparql);
        boolean result;
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        //try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
        result = qexec.execAsk();
        logger.info("Exec query ASK SPARQL :" + sparql);
        //}
        return result;
    }

    /**
     * Metodo per il caricamento di un file di triple in un'oggetto model di JENA.
     *
     * @param filename    name of the file of input.
     * @param filepath    path to the file of input wihtout the name.
     * @param inputFormat format of the file in input.
     * @return the jena model of the file.
     * @throws FileNotFoundException thriow if any "File Not Found" error is occurred.
     */
    public static Model loadFileTripleToModel(String filename, String filepath, String inputFormat) throws FileNotFoundException {
        Model m = ModelFactory.createDefaultModel();
        INLANGFORMAT = toLang(inputFormat);
        INRDFFORMAT = toRDFFormat(inputFormat);
        INFORMAT = INLANGFORMAT.getLabel().toUpperCase();
        // use the FileManager to find the input file
        File fileInput = new File(filepath + File.separator + filename + "." + inputFormat);
        InputStream in;
        try {
            in = FileManager.get().open(fileInput.getAbsolutePath());
        } catch (Exception e) {
            in = new FileInputStream(fileInput);
        }

        if (in == null || !fileInput.exists()) throw new IllegalArgumentException("File: " + fileInput + " not found");

        logger.info("Try to read file of triples from the path:" + fileInput.getAbsolutePath() + "...");
        try {
            FileManager.get().addLocatorClassLoader(Jena3Utilities.class.getClassLoader());
            try {
                m = FileManager.get().loadModel(fileInput.toURI().toString(), null, INFORMAT);
            }catch(Exception e){
                m = FileManager.get().readModel(m,fileInput.toURI().toString(),null,INFORMAT);
            }
        } catch (Exception e) {
            try {
                m.read(in, null, INFORMAT);
            } catch (Exception e1) {
               /* try {
                    RDFDataMgr.read(m, in, INLANGFORMAT);
                } catch (Exception e2) {
                    try {
                        //If you are just opening the stream from a file (or URL) then Apache Jena
                        RDFDataMgr.read(m, fileInput.toURI().toString());
                    } catch (Exception e3) {
                        logger.error("Failed read the file of triples from the path:" +
                                fileInput.getAbsolutePath() + ":" + e.getMessage(), e);
                    }
                }*/
            }
        }
        logger.info("...file of triples from the path:" + fileInput.getAbsolutePath() + " readed!!");
        return m;
    }

    /**
     * Method to load a a file of triple to a Jena Model.
     *
     * @param filePath string path to the file.
     * @return model model loaded with the file.
     * @throws java.io.FileNotFoundException throw if any "File Not Found" error is occurred.
     */
    public static Model loadFileTripleToModel(String filePath) throws FileNotFoundException {
        return loadFileTripleToModel(new File(filePath));
    }

    /**
     * Method for load a file of tuples to a jena model.
     *
     * @param file a input file.
     * @return the jena model of the file.
     * @throws FileNotFoundException throw if any "File Not Found" error is occurred.
     */
    public static Model loadFileTripleToModel(File file) throws FileNotFoundException {
        String filename = FileUtilities.getFilenameWithoutExt(file);
        String filepath = FileUtilities.getPath(file);
        String inputFormat = FileUtilities.getExtension(file);
        return loadFileTripleToModel(filename, filepath, inputFormat);
    }

    /**
     * Helper method that splits up a URI into a namespace and a local part.
     * It uses the prefixMap to recognize namespaces, and replaces the
     * namespace part by a prefix.
     *
     * @param prefixMap the PremixMapping of Jena.
     * @param resource the Resource Jena.
     * @return the Array of String.
     */
    public static String[] split(PrefixMapping prefixMap, Resource resource) {
        String uri = resource.getURI();
        if (uri == null) {
            return new String[] {null, null};
        }
        Map<String,String> prefixMapMap = prefixMap.getNsPrefixMap();
        Set<String> prefixes = prefixMapMap.keySet();
        String[] split = { null, null };
        for (String key : prefixes){
            String ns = prefixMapMap.get(key);
            if (uri.startsWith(ns)) {
                split[0] = key;
                split[1] = uri.substring(ns.length());
                return split;
            }
        }
        split[1] = uri;
        return split;
    }




    /**
     * A list of org.apache.jena.riot.Lang file formats.
     * return all the language Lang supported from jena.
     * exception : "AWT-EventQueue-0" java.lang.NoSuchFieldError: RDFTHRIFT  or CSV.
     */
    private static final Lang allFormatsOfRiotLang[] = new Lang[]{
            Lang.NTRIPLES, Lang.N3, Lang.RDFXML,
            Lang.TURTLE, Lang.TRIG, Lang.TTL,
            Lang.NQUADS,
            Lang.NQ,
            //org.apache.jena.riot.Lang.JSONLD,
            Lang.NT, Lang.RDFJSON,
            Lang.RDFNULL
            //org.apache.jena.riot.Lang.CSV,
            //org.apache.jena.riot.Lang.RDFTHRIFT
    };

    /**
     * A list of org.apache.jena.riot.RDFFormat file formats used in jena.
     * if you are not using the last version of jena you can found in build:
     * "AWT-EventQueue-0" java.lang.NoSuchFieldError: JSONLD_FLAT
     */
    private static final RDFFormat allFormatsOfRDFFormat[] = new RDFFormat[]{
            RDFFormat.TURTLE, RDFFormat.TTL,
            //RDFFormat.JSONLD_FLAT,
            //RDFFormat.JSONLD_PRETTY,
            //RDFFormat.JSONLD,
            RDFFormat.RDFJSON, RDFFormat.RDFNULL, RDFFormat.NQUADS, RDFFormat.NQ,
            // RDFFormat.NQUADS_ASCII,RDFFormat.NQUADS_UTF8,
            RDFFormat.NT, RDFFormat.NTRIPLES,
            // RDFFormat.NTRIPLES_ASCII,RDFFormat.NTRIPLES_UTF8,
            RDFFormat.RDFXML, RDFFormat.RDFXML_ABBREV,
            RDFFormat.RDFXML_PLAIN, RDFFormat.RDFXML_PRETTY, RDFFormat.TRIG, RDFFormat.TRIG_BLOCKS,
            RDFFormat.TRIG_FLAT, RDFFormat.TRIG_PRETTY, RDFFormat.TURTLE_BLOCKS, RDFFormat.TURTLE_FLAT,
            RDFFormat.TURTLE_PRETTY};
    //org.apache.jena.riot.RDFFormat.RDF_THRIFT,org.apache.jena.riot.RDFFormat.RDF_THRIFT_VALUES,

    /**
     * A list of com.hp.hpl.jena.datatypes.RDFDatatype file formats used in jena.
     * @return all the RDFFormat supported from jena.
     */
   /* private static final RDFFormat allFormatsOfRDFFormat[] = new RDFFormat[] {
            RDFDatatype.
    };*/

    /**
     * A list of com.hp.hpl.jena.datatypes.xsd.XSDDatatype.
     * return all the com.hp.hpl.jena.datatypes.RDFDatatype supported from jena.
     *
     * @param uri the String of the uri resource.
     * @return the RDFDatatype of the uri resource.
     */
    public static RDFDatatype toRDFDatatype(String uri) {
        return toXSDDatatype(uri);
    }


    /*public static com.hp.hpl.jena.datatypes.RDFDatatype convertXSDDatatypeToRDFDatatype(XSDDatatype xsdD){
        return xsdD;
    }

    public static XSSimpleType convertStringToXssSimpleType(String nameDatatype){
        SymbolHash fBuiltInTypes = new SymbolHash();
        return (XSSimpleType)fBuiltInTypes.get(nameDatatype);
    }

    public static XSDDatatype convertStringToXSDDatatype(String nameDatatype){
        XSSimpleType xss = convertStringToXssSimpleType(nameDatatype);
        return new XSDDatatype(xss,xss.getNamespace());
    }*/

    /**
     * A list of org.apache.xerces.impl.dv.XSSimpleType.
     * return all the XSSimpleType supported from jena.
     */
   /* private static final short[] allFormatOfXSSimpleType = new short[]{
            XSSimpleType.PRIMITIVE_ANYURI,XSSimpleType.PRIMITIVE_BASE64BINARY,XSSimpleType.PRIMITIVE_BOOLEAN,
            XSSimpleType.PRIMITIVE_DATE,XSSimpleType.PRIMITIVE_DATETIME,XSSimpleType.PRIMITIVE_DECIMAL,XSSimpleType.PRIMITIVE_DOUBLE,
            XSSimpleType.PRIMITIVE_DURATION,XSSimpleType.PRIMITIVE_FLOAT,XSSimpleType.PRIMITIVE_GDAY,XSSimpleType.PRIMITIVE_GMONTH,
            XSSimpleType.PRIMITIVE_GMONTHDAY,XSSimpleType.PRIMITIVE_GYEAR,XSSimpleType.PRIMITIVE_GYEARMONTH,
            XSSimpleType.PRIMITIVE_HEXBINARY,XSSimpleType.PRIMITIVE_NOTATION,XSSimpleType.PRIMITIVE_PRECISIONDECIMAL,
            XSSimpleType.PRIMITIVE_QNAME,XSSimpleType.PRIMITIVE_STRING,XSSimpleType.PRIMITIVE_TIME,XSSimpleType.WS_COLLAPSE,
            XSSimpleType.WS_PRESERVE,XSSimpleType.WS_REPLACE
    };*/

    /**
     * A list of com.hp.hpl.jena.datatypes.xsd.XSDDatatype.
     * return all the XSDDatatype supported from jena.
     */
    public static final XSDDatatype allFormatsOfXSDDataTypes[] = new XSDDatatype[]{
            XSDDatatype.XSDstring, XSDDatatype.XSDENTITY, XSDDatatype.XSDID, XSDDatatype.XSDIDREF,
            XSDDatatype.XSDanyURI, XSDDatatype.XSDbase64Binary, XSDDatatype.XSDboolean, XSDDatatype.XSDbyte,
            XSDDatatype.XSDdate, XSDDatatype.XSDdateTime, XSDDatatype.XSDdecimal, XSDDatatype.XSDdouble,
            XSDDatatype.XSDduration, XSDDatatype.XSDfloat, XSDDatatype.XSDgDay, XSDDatatype.XSDgMonth,
            XSDDatatype.XSDgMonthDay, XSDDatatype.XSDgYear, XSDDatatype.XSDgYearMonth, XSDDatatype.XSDhexBinary,
            XSDDatatype.XSDint, XSDDatatype.XSDinteger, XSDDatatype.XSDlanguage, XSDDatatype.XSDlong,
            XSDDatatype.XSDName, XSDDatatype.XSDNCName, XSDDatatype.XSDnegativeInteger, XSDDatatype.XSDNMTOKEN,
            XSDDatatype.XSDnonNegativeInteger, XSDDatatype.XSDnonPositiveInteger, XSDDatatype.XSDnormalizedString,
            XSDDatatype.XSDNOTATION, XSDDatatype.XSDpositiveInteger, XSDDatatype.XSDQName, XSDDatatype.XSDshort,
            XSDDatatype.XSDtime, XSDDatatype.XSDtoken, XSDDatatype.XSDunsignedByte, XSDDatatype.XSDunsignedInt,
            XSDDatatype.XSDunsignedLong, XSDDatatype.XSDunsignedShort
    };

    /**
     * Method convert a string to XSDDatatype.
     *
     * @param uri string uri of the XSDDatatype.
     * @return xsdDatatype of the string uri if exists.
     */
    public static XSDDatatype toXSDDatatype(String uri) {
        for (XSDDatatype xsdDatatype : allFormatsOfXSDDataTypes) {
            if (xsdDatatype.getURI().equalsIgnoreCase(XSDDatatype.XSD + "#" + uri)) return xsdDatatype;
            if (xsdDatatype.getURI().replace(XSDDatatype.XSD, "")
                    .toLowerCase().contains(uri.toLowerCase())) return xsdDatatype;
        }
        logger.error("The XSD Datatype '" + uri + "' is not recognised");
        throw new IllegalArgumentException("The XSD Datatype '" + uri + "' is not recognised");
    }

    /**
     * Method convert a string to a rdfformat.
     *
     * @param strFormat string name of the RDFFormat.
     * @return rdfformat the RDFFormat with the same name.
     */
    public static RDFFormat toRDFFormat(String strFormat) {
        if (strFormat.toUpperCase().contains("NT") ||
                strFormat.toUpperCase().contains("NTRIPLES") || strFormat.toUpperCase().contains("N3")) {
            strFormat = "N-Triples";
        }
        if (strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")) {
            strFormat = "Turtle";
        }
        //Collection<RDFFormat> allFormatsOfRDFFormat = RDFWriterRegistry.registered();
        for (RDFFormat rdfFormat : allFormatsOfRDFFormat) {
            if (rdfFormat.getLang().getName().equalsIgnoreCase(strFormat))
                return rdfFormat;
        }
        logger.error("The RDF format '" + strFormat + "' is not recognised");
        throw new IllegalArgumentException("The RDF format '" + strFormat + "' is not recognised");
    }

    /**
     * Method convert a string name of a RDFFormat to a language Lang.
     *
     * @param strFormat string name of a RDFFormat.
     * @return lang the language Lang for the same name.
     */
    public static Lang toLang(String strFormat) {
        if (strFormat.toUpperCase().contains("NT") ||
                strFormat.toUpperCase().contains("NTRIPLES") || strFormat.toUpperCase().contains("N3")) {
            strFormat = "N-Triples";
        }
        if (strFormat.toUpperCase().contains("TTL") || strFormat.toUpperCase().contains("TURTLE")) {
            strFormat = "Turtle";
        }
        for (Lang lang : allFormatsOfRiotLang) {
            String label = lang.getLabel();
            String name = lang.getName();

            if (lang.getName().equalsIgnoreCase(strFormat))
                return lang;
        }
        logger.error("The LANG format '" + strFormat + "' is not recognised");
        throw new IllegalArgumentException("The LANG format '" + strFormat + "' is not recognised");
    }

    /**
     * Method to create a JENA Query from a String SPARQL Query.
     *
     * @param querySPARQL the String SPARQL Query.
     * @return the JENA Query object.
     */
    public static Query toQuery(String querySPARQL) {
        return QueryFactory.create(querySPARQL);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param lang the String of the Language.
     * @param rdfDatatype the RDFDatatype.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(Object value, String lang, RDFDatatype rdfDatatype){
        return LiteralLabelFactory.createLiteralLabel(String.valueOf(value),lang,rdfDatatype);
    }
    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @return the LiteralLabel Jena Object.
     */

    public static LiteralLabel toLiteralLabel(Object value){
        return LiteralLabelFactory.createTypedLiteral(value);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param rdfDatatype the RDFDatatype.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(String value,RDFDatatype rdfDatatype){
        return LiteralLabelFactory.create(value,rdfDatatype);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param lang the String of the Language.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(String value,String lang){
        return LiteralLabelFactory.create(value,lang);
    }

    /**
     * Method to create LiteralLabel Jena Object.
     * @param value hte Object Value.
     * @param lang the String of the Language.
     * @param isXML the boolean value if a XML or not.
     * @return the LiteralLabel Jena Object.
     */
    public static LiteralLabel toLiteralLabel(String value,String lang,boolean isXML){
        return LiteralLabelFactory.create(value,lang,isXML);
    }

    /**
     * Method to print the resultSet to a a specific format of output.
     *
     * @param sparql             sparql query.
     * @param model              jena model.
     * @param fullPathOutputFile string to the path of the output file.
     * @param outputFormat       stirng of the output format.
     */
    private static void formatTheResultSetAndPrint(
            String sparql, Model model, String fullPathOutputFile, String outputFormat) {
        try {
            //JSON,CSV,TSV,,RDF,SSE,XML
            ResultSet results;
            if (outputFormat.toLowerCase().contains("csv") || outputFormat.toLowerCase().contains("xml")
                    || outputFormat.toLowerCase().contains("json") || outputFormat.toLowerCase().contains("tsv")
                    || outputFormat.toLowerCase().contains("sse") || outputFormat.toLowerCase().contains("bio")
                    || outputFormat.toLowerCase().contains("rdf") || outputFormat.toLowerCase().contains("bio")
                    ) {
//               try (com.hp.hpl.jena.query.QueryExecution qexec2 =
//                          com.hp.hpl.jena.query.QueryExecutionFactory.insert(sparql, model)) {
//                      results = qexec2.execSelect() ;
//               }
                results = execSparqlSelectOnModel(sparql, model);
                //PRINT THE RESULT
                logger.info("Try to write the new file of triple to:" + fullPathOutputFile + "...");
                FileOutputStream fos = new FileOutputStream(new File(fullPathOutputFile));
                if (outputFormat.toLowerCase().contains("csv")) {
                    ResultSetFormatter.outputAsCSV(fos, results);
                } else if (outputFormat.toLowerCase().contains("xml")) {
                    ResultSetFormatter.outputAsXML(fos, results);
                } else if (outputFormat.toLowerCase().contains("json")) {
                    ResultSetFormatter.outputAsJSON(fos, results);
                } else if (outputFormat.toLowerCase().contains("tsv")) {
                    ResultSetFormatter.outputAsTSV(fos, results);
                } else if (outputFormat.toLowerCase().contains("sse")) {
                    ResultSetFormatter.outputAsSSE(fos, results);
                }
                /** deprecated with jena 3 */
                /*  else if (outputFormat.toLowerCase().contains("bio")) {
                    ResultSetFormatter.outputAsBIO(fos, results);
                } else if (outputFormat.toLowerCase().contains("rdf")) {
                    ResultSetFormatter.outputAsRDF(fos, "RDF/XML", results);
                }*/
                logger.info("... the file of triple to:" + fullPathOutputFile + " is been wrote!");
            } else if (outputFormat.toLowerCase().contains("ttl")) {
                Model resultModel = execSparqlConstructorOnModel(sparql, model);
                OUTLANGFORMAT = toLang(outputFormat);
                OUTRDFFORMAT = toRDFFormat(outputFormat);
                OUTFORMAT = outputFormat.toUpperCase();
                //Writer writer = new FileWriter(new File(fullPathOutputFile));
                //model.write(writer, outputFormat);
                writeModelToFile(fullPathOutputFile, resultModel, OUTFORMAT);
                logger.info("... the file of triple to:" + fullPathOutputFile + " is been wrote!");
            }
        } catch (Exception e) {
            logger.error("error during the writing of the file of triples:" + fullPathOutputFile + ":" + e.getMessage(), e);
        }

    }

    /**
     * Method to convert a RDF fikle of triples to a another specific format.
     *
     * @param file         file to convert.
     * @param outputFormat string of the output format.
     * @throws IOException throw if any I/O is occurred.
     */
    public static void convertFileTripleToAnotherFormat(File file, String outputFormat) throws IOException {
        convertTo(file, outputFormat);
    }

    /**
     * Method to convert a RDF fikle of triples to a another specific format.
     *
     * @param file         file to convert.
     * @param outputFormat string of the output format.
     * @throws IOException throw if any I/O is occurred.
     */
    private static void convertTo(File file, String outputFormat) throws IOException {
        Model m = loadFileTripleToModel(file);
        String newName = FileUtilities.getFilenameWithoutExt(file) + "." + outputFormat.toLowerCase();
        String newPath = FileUtilities.getPath(file);
        String sparql;
        if (outputFormat.toLowerCase().contains("csv") || outputFormat.toLowerCase().contains("xml")
                || outputFormat.toLowerCase().contains("json") || outputFormat.toLowerCase().contains("tsv")
                || outputFormat.toLowerCase().contains("sse") || outputFormat.toLowerCase().contains("bio")
                || outputFormat.toLowerCase().contains("rdf") || outputFormat.toLowerCase().contains("bio")
                ) {
            sparql = "SELECT * WHERE{?s ?p ?o}";
        } else {
            sparql = "CONSTRUCT {?s ?p ?o} WHERE{?s ?p ?o}";
        }
        formatTheResultSetAndPrint(sparql, m, newPath + File.separator + newName, outputFormat.toLowerCase());
    }

   /*
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

    //-----------------------------------------------------------------------------------------------------------------

    /**
     * Method to find if exists some statement with a specific property.
     *
     * @param model    jena model.
     * @param subject  subject of the statement you want to check.
     * @param property string of property of the statement you want to check.
     * @return boolean result if exists or not.
     */
    public static boolean findProperty(Model model, Resource subject, String property) {
        boolean foundLocal = false;
        try {
            int pos = property.indexOf(":");
            String prefix = property.substring(0, pos);
            property = property.substring(pos + 1);
            String uri = namespaces.get(prefix);
            Property p = null;
            if (!"".equals(property)) {
                p = model.createProperty(uri, property);
            }
            StmtIterator iter = model.listStatements(
                    new SelectorImpl(subject, p, (RDFNode) null));
            while (iter.hasNext() && !foundLocal) {
                Statement stmt = iter.next();
                Property sp = stmt.getPredicate();

                if (uri.equals(sp.getNameSpace())
                        && ("".equals(property)
                        || sp.getLocalName().equals(
                        property))) {
                    foundLocal = true;
                }
            }
        } catch (Exception e) {
            logger.warn("Exception while try to find a property:" + e.getMessage(), e);
        }
        return foundLocal;
    }

    /**
     * Method to copy a Model to another Model with different uri and specific resources.
     *
     * @param model   jena model for the copy.
     * @param subject the resoures you want to copy.
     * @param uri     the uri for the new subject copied new model.
     * @return the copied model.
     */
    public static Model copyModel(Model model, Resource subject, String uri) {
        try {
            Model newModel = ModelFactory.createDefaultModel();
            Resource newSubject = newModel.createResource(uri);
            // Copy prefix mappings to the new model...
            newModel.setNsPrefixes(model.getNsPrefixMap());
            newModel = copyToModel(model, subject, newModel, newSubject);
            return newModel;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to copy a Model to another Model.
     *
     * @param srcModel  model for the copy.
     * @param srcRsrc   resource of the model for the copy.
     * @param destModel model copied.
     * @param destRsrc  resource of the model copied.
     * @return the copied model.
     */
    public static Model copyToModel(Model srcModel, Resource srcRsrc, Model destModel, Resource destRsrc) {
        try {
            if (srcModel != null && destModel != null) {
                StmtIterator iter = srcModel.listStatements(
                        new SelectorImpl(srcRsrc, null, (RDFNode) null));
                while (iter.hasNext()) {
                    Statement stmt = iter.next();
                    RDFNode obj = stmt.getObject();
                    if (obj instanceof Resource) {
                        Resource robj = (Resource) obj;
                        if (robj.isAnon() && destModel != null) {
                            Resource destSubResource = destModel.createResource();
                            destModel = copyToModel(srcModel, robj, destModel, destSubResource);
                            obj = destSubResource;
                        }
                    }
                    if (destModel != null) {
                        Statement newStmt = destModel.createStatement(destRsrc, stmt.getPredicate(), obj);
                        destModel.add(newStmt);
                    }
                }
                return destModel;
            } else {
                logger.error("try to copy a NULL Jena Model with another Jena Model:" + srcModel + "->" + destModel);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to merge two Jena Model.
     *
     * @param model    first jena model.
     * @param newModel second jena model.
     * @return merged jena model.
     */
    public static Model mergeModel(Model model, Model newModel) {
        try {
            if (model != null && newModel != null) {
                ResIterator ri = newModel.listSubjects();
                while (ri.hasNext()) {
                    Resource newSubject = ri.next();
                    Resource subject;
                    if (!newSubject.isAnon() && model != null) {
                        subject = model.createResource(newSubject.getURI());
                        model = copyToModel(newModel, newSubject, model, subject);
                    }
                    //else : nevermind; copyToModel will handle this case recursively
                }
                return model;
            } else {
                logger.error("try to merge a NULL Jena Model with another Jena Model:" + model + "<->" + newModel);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method to delete literal on a Jena Model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @param value    value of the literal of the statement to remove from Jena model.
     */
    public static void deleteLiteral(Model model, Resource subject, String property, String value) {
        int pos = property.indexOf(":");
        String prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        try {
            String uri = namespaces.get(prefix);
            Property p = model.createProperty(uri, property);
            RDFNode v = model.createLiteral(value);
            Statement s = model.createStatement(subject, p, v);
            model.remove(s);
        } catch (Exception e) {
            // nop;
            logger.warn("Exception while try to delete a literal:" + e.getMessage(), e);
        }
    }

    /**
     * Method to query/read for a literal on a Jena Model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @return string of the literal.
     */
    public static String queryLiteral(Model model, Resource subject, String property) {
        return findLiteral(model, subject, property);
    }

    /**
     * Method to query/read for a literal on a Jena Model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @return string of the literal.
     */
    public static String findLiteral(Model model, Resource subject, String property) {
        int pos = property.indexOf(":");
        String prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        try {
            Property p;
            String uri = namespaces.get(prefix);
            if (!isNullOrEmpty(uri)) {
                p = model.createProperty(uri, property);
            } else {
                p = model.createProperty(property);
            }
            StmtIterator iter = model.listStatements(new SelectorImpl(subject, p, (RDFNode) null));
            while (iter.hasNext()) {
                Statement stmt = iter.next();
                RDFNode obj = stmt.getObject();
                if (obj instanceof Literal) {
                    return obj.toString();
                }
            }
        } catch (Exception e) {
            logger.warn("you got a error while try to find the literal with Subject '"
                    + subject.toString() + "' and Property '" + property + "':" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Method to update a literal on a Jena model.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @param value    value of the literal of the statement to remove from Jena model.
     * @return if true all the operation are done.
     */
    public static boolean updateLiteral(Model model, Resource subject, String property, String value) {
        try {
            //int pos = property.indexOf(":");
            //String prefix = property.substring(0, pos);
            String rdfValue = queryLiteral(model, subject, property);
            if (value != null && !value.equals(rdfValue)) {
                logger.info("Updating " + property + "=" + value);
                deleteLiteral(model, subject, property, rdfValue);
                int pos = property.indexOf(":");
                String prefix = property.substring(0, pos);
                property = property.substring(pos + 1);
                String uri = namespaces.get(prefix);
                Property p = model.createProperty(uri, property);
                RDFNode v = model.createLiteral(value);
                Statement s = model.createStatement(subject, p, v);
                model.add(s);
                return true;
            }
            logger.warn("The value is:" + value + " while the rdfValue is:" + rdfValue);
            return false;
        } catch (Exception e) {
            logger.warn("Exception while try to update a literal:" + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to get/find the namespaces on a model jena.
     *
     * @param namespace string as uri of a namespace.
     * @return the prefix for the namespace.
     */
    public static String findNamespacePrefix(String namespace) {
        if (namespaces.containsValue(namespace)) {
            // find it...
            for (String prefix : namespaces.keySet()) {
                if (namespace.equals(namespaces.get(prefix))) {
                    return prefix;
                }
            }
            logger.warn("Internal error: this can't happen.");
            return null;
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
     * Method convert a XSDDatatype to a string.
     *
     * @param jenaObject XSDDatatype,Model of input.
     * @param outputFormat String of the output format.
     * @param baseURI String URI Graph Base.
     * @return the String rappresentation of the Object.
     */
    public static String toString(Object jenaObject,String outputFormat,String baseURI){
        if(jenaObject instanceof XSDDatatype){
            return ((XSDDatatype)jenaObject).getURI();
        }else if(jenaObject instanceof Model){
            try {
                if(baseURI != null){
                    //StringOutputStreamKit stringOutput = new StringOutputStreamKit();
                    Writer stringOutput = new StringWriter();
                    if (!isNullOrEmpty(outputFormat)) {
                        RDFFormat rdfFormat = toRDFFormat(outputFormat);
                        if (rdfFormat == null) {
                            outputFormat = "RDF/XML-ABBREV";
                        }
                    } else {
                        outputFormat = "RDF/XML-ABBREV";
                    }
                    ((Model)jenaObject).write(stringOutput, outputFormat, baseURI);
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
                    return XMLUtilities.xmlEncode(rawString);
                }else{
                    if (!isNullOrEmpty(outputFormat)) {
                        try {
                            RDFFormat rdfFormat = toRDFFormat(outputFormat);
                            outputFormat = rdfFormat.getLang().getName();
                        } catch (IllegalArgumentException e) {
                            outputFormat = "RDF/XML-ABBREV";
                        }
                    } else {
                        outputFormat = "RDF/XML-ABBREV";
                    }
                    StringWriter stringOut = new StringWriter();
                    //setCommonPrefixes(model);
                    ((Model)jenaObject).write(stringOut, outputFormat, RSS.getURI());
                    // http://base
                    stringOut.flush();
                    stringOut.close();
                    return stringOut.toString();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return "N/A";
            }
        }else{
            logger.error("Can't convert to String the Object:"+jenaObject.getClass().getName());
            return "N/A";
        }
    }

    /**
     * Method to delete a specific resource , property on model jena
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement.
     * @return jena model.
     */
    public static Model deleteProperty(Model model, Resource subject, String property) {
        String prefix;
        int pos = property.indexOf(":");
        prefix = property.substring(0, pos);
        property = property.substring(pos + 1);
        Property p = null;
        String uri = namespaces.get(prefix);
        if (!"".equals(property)) {
            p = model.createProperty(uri, property);
        }

        StmtIterator iter = model.listStatements(new SelectorImpl(subject, p, (RDFNode) null));
        while (iter.hasNext()) {
            Statement stmt = iter.next();
            p = stmt.getPredicate();
            if (p.getNameSpace() == null) {
                continue;
            }
            if (p.getNameSpace().equals(uri)) {
                String type = "literal";
                if (stmt.getObject() instanceof Resource) {
                    type = "resource";
                }
                logger.info("\tdelete " + type + ": " + prefix + ":" + p.getLocalName()
                        + "=" + stmt.getObject().toString());
                model.remove(stmt);
                return model;
            }
        }
        return null;
    }

    /**
     * Taken from com.idea.io.RdfUtils, modified for Jena 2
     */
    /*
     public static Resource updateProperties(Resource resource, GraphVertexChangeEvent vertex){
         setProperty(resource, RSS.title, vertex.getVertex())); return resource;
     }
    */

    /**
     * Method to update a property on a model jena.
     *
     * @param model    jena model.
     * @param subject  subject of the statement.
     * @param property property of the statement to set.
     * @param value    value of the object of the statement.
     * @return jena model.
     */
    public static Model updateProperty(Model model, Resource subject, Property property, Object value) {
        try {
            /*
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {iterator.next(); iterator.remove();}
            */
            //... you must already create the resources
            //subject = model.getResource(redirectionURI);
            //subject = model.toResource("");
            //...Delete all the statements with predicate p for this resource from its associated model.
            subject.removeAll(property);
            subject.addProperty(property, (RDFNode) value);
            return model;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Method for delete statement with specific proprety and literal on a Jena model.
     *
     * @param model           jena model.
     * @param subject         subject of the statement.
     * @param property        property of the statement to set.
     * @param languageLiteral language of the literal.
     * @param valueLiteral    value of the literal.
     * @return a jena model.
     */
    public Model deletePropertyAndObject(Model model, Resource subject, Property property,
                                         String languageLiteral, String valueLiteral) {
        NodeIterator nodeIterator = model.listObjectsOfProperty(property);
        RDFNode foundToDelete = null;
        while (nodeIterator.hasNext()) {
            RDFNode next = nodeIterator.next();
            boolean langsAreIdentical = next.asLiteral().getLanguage().equals(languageLiteral);
            boolean valuesAreIdentical = next.asLiteral().getLexicalForm().equals(valueLiteral);
            if (langsAreIdentical && valuesAreIdentical) {
                foundToDelete = next;
                break;
            }
        }
        model.remove(subject, property, foundToDelete);
        return model;
    }

    /**
     * Method to get/find first the value of the property
     * can use model.getProperty() directly now.
     *
     * @param subject  subject of the statement.
     * @param property property of the statement to set.
     * @return value of the literal.
     */
    public static RDFNode findFirstPropertyValue(Resource subject, Property property) {
        Statement statement = subject.getProperty(property);
        if (statement == null) {
            logger.warn("The statement found is NULL.");
            return null;
        }
        return statement.getObject();
    }

    /**
     * Method to get/find the rdf type from a Resource.
     *
     * @param subject subject of the statement.
     * @return the string of the RdfType.
     */
    public static String findRdfType(Resource subject) {
        if (subject.isAnon()) {
            // @@TODO this whole lot needs improving
            return "anon";
        }
        //show(resource);
        RDFNode type = findFirstPropertyValue(subject, RDF.type);
        if (type == null) {
            return "untyped";
        }
        return type.toString();
    }

    /**
     * Method to get the uri from a Reosurce on a model jena.
     *
     * @param resource resource uri.
     * @param uri      new uri for the resource.
     * @return if true all the operation are done.
     */
    public static boolean updateUri(Resource resource, URI uri) {
        try {
            Model m = resource.getModel();
            Resource newResource = m.createResource(uri.toString());
            StmtIterator iterator = resource.listProperties();
            // copy properties from old resource
            // buffer used to avoid concurrent modification
            Set<Statement> statements = new HashSet<>();
            while (iterator.hasNext()) {
                Statement stmt = iterator.next();
                statements.add(stmt);
                // changed for Jena 2
                newResource.addProperty(stmt.getPredicate(), stmt.getObject());
                //model.remove(stmt);
            }
            Iterator<Statement> setIterator = statements.iterator();
            Statement statement;
            while (setIterator.hasNext()) {
                statement = setIterator.next();
                if (m.contains(statement)) {
                    m.remove(statement);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to show a statement on a model jena to the console.
     *
     * @param statement statement to print to the console.
     */
    public static void show(Statement statement) {
        show(statement.getSubject());
        show(statement.getPredicate());
        if (statement.getObject() instanceof Resource) {
            show((Resource) statement.getObject());
        } else {
            logger.info(statement.getObject().toString());
        }
    }

    /**
     * Method to show a resource on a model jena to the console.
     *
     * @param resource resource to print to the console.
     */
    public static void show(Resource resource) {
        StmtIterator iterator = resource.listProperties();
        show(iterator);
    }

    /**
     * Method to show a iterators on a model jena to the console.
     *
     * @param iterator list of the statement to print on the console.
     */
    public static void show(StmtIterator iterator) {
        StringBuilder buffer = new StringBuilder("\n--v--");
        //StmtIterator iterator = resource.listProperties();
        while (iterator.hasNext()) buffer.append("\n").append(iterator.next().toString());
        buffer.append("\n--^--");
        logger.info(buffer.toString());
    }

    /**
     * Method to show a model jena to the console.
     *
     * @param model        jena model to print tot the console.
     * @param outputFormat string of the output format.
     */
    public static void show(Model model, String outputFormat) {
        logger.info(toString(model,outputFormat,null));
    }

    /**
     * Method to set the prefix on a model jena.
     *
     * @param model      jena model.
     * @param namespaces map of namespace with prefix.
     * @return the model jena with the prefix of namespace.
     */
    public static Model setCommonPrefixes(Model model, Map<String, String> namespaces) {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            //model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
            //model.setNsPrefix("vis", "http://ideagraph.org/xmlns/idea/graphic#");
            model.setNsPrefix(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return model;
    }

    /**
     * Method to replace a resource on a model jena.
     *
     * @param oldResource resource to replace.
     * @param newResource the new resource.
     * @return if true all the operation are done.
     */
    public static boolean updateResource(Resource oldResource, Resource newResource) {
        try {
            StmtIterator statements = model.listStatements();
            Statement statement;
            Resource subject;
            RDFNode object;
            // buffer in List to avoid concurrent modification exception
            List<Statement> statementList = new ArrayList<>();
            while (statements.hasNext()) {
                statementList.add(statements.next());
            }
            for (Statement aStatementList : statementList) {
                statement = aStatementList;
                subject = statement.getSubject();
                object = statement.getObject();
                if (subject.equals(oldResource)) {
                    updateSubjectResource(statement, newResource);
                }
                if ((object instanceof Resource) && (oldResource.equals(object))) {
                    updateObjectResource(statement, newResource);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to replace/update a subject/resource on a model jena.
     *
     * @param statement  statement with the resource to replace/update
     * @param newSubject new resource to add tot he model.
     * @return if true all the operation are done.
     */
    public static boolean updateSubjectResource(Statement statement, Resource newSubject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement = m.createStatement(newSubject,
                    statement.getPredicate(), statement.getObject());
            m.remove(statement);
            m.add(newStatement);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method to replace/update a object resource.
     *
     * @param statement statement with the object to replace/update
     * @param newObject new value of the object
     * @return if true all the operation are done.
     */
    public static boolean updateObjectResource(Statement statement, Resource newObject) {
        Statement newStatement;
        try {
            Model m = statement.getModel();
            newStatement = m.createStatement(statement.getSubject(),
                    statement.getPredicate(), newObject);
            m.remove(statement);
            m.add(newStatement);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method copies all properties across to new resource, just replaces type.
     *
     * @param resource the resource to update the type.
     * @param newType  the new type for the resource.
     * @return if true all the operation are done.
     */
    public static boolean updateTypeResource(Resource resource, Resource newType) {
        try {
            StmtIterator iterator = resource.listProperties();
            Property property = null;
            Statement statement = null;
            while (iterator.hasNext()) {
                statement = iterator.next();
                property = statement.getPredicate();
                if (property.equals(RDF.type)) {
                    break; // to stop concurrent mod exc
                }
            }
            if (property != null) {
                if (property.equals(RDF.type)) {
                    resource.getModel().remove(statement);
                    resource.addProperty(RDF.type, newType);
                }
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method approximate : returns first match.
     *
     * @param model   jena model.
     * @param rdfNode property to find.
     * @return resource you found.
     */
    public static Resource findParent(Model model, RDFNode rdfNode) {
        if (rdfNode instanceof Property) {
            return findParentResource(model, (Property) rdfNode);
        }
        return findParentProperty(model, rdfNode);
    }

    /**
     * Method  returns predicate of first statement with matching object.
     *
     * @param model   jena model.
     * @param rdfNode property to find.
     * @return poroperty you found.
     */
    public static Property findParentProperty(Model model, RDFNode rdfNode) {
        Statement statement = findParentStatement(model, rdfNode);
        if (statement == null) {
            logger.warn("The Statement founded is NULL.");
            return null;
        }
        return statement.getPredicate();
    }

    /**
     * Method approximate : returns first statement with matching object.
     *
     * @param model   jena model.
     * @param rdfNode resource to find.
     * @return the statement you found.
     */
    public static Statement findParentStatement(Model model, RDFNode rdfNode) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            if (rdfNode.equals(statement.getObject())) {
                //parent = statement.getSubject();
                if (!(RDF.type).equals(statement.getPredicate())) {
                    return statement;
                }
            }
        }
        logger.warn("The Statement founded is NULL.");
        return null;
    }

    /**
     * Method approximate : returns object of first statement with matching predicate.
     *
     * @param model    jena model.
     * @param property property to find.
     * @return resource you found.
     */
    public static Resource findParentResource(Model model, Property property) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            //changed for Jena 2
            if (property.equals(statement.getPredicate())) {
                return statement.getSubject();
            }
        }
        logger.warn("The Statement founded is NULL.");
        return null;
    }

    /**
     * Method approximate : gets first match (predicate and object).
     *
     * @param model    jena model.
     * @param property property to find.
     * @param object   object to find.
     * @return the subject you found.
     */
    public static Resource findSubject(Model model, Property property, RDFNode object) {
        Statement statement = findStatement(model, property, object);
        if (statement == null) {
            logger.warn("The Statement founded is NULL.");
            return null;
        }
        return statement.getSubject();
    }

    /**
     * Method approximate : gets first match (predicate and object).
     *
     * @param model    jena model.
     * @param property porperty to find.
     * @param object   object to find.
     * @return statemn you found.
     */
    public static Statement findStatement(Model model, Property property, RDFNode object) {
        Statement statement;
        StmtIterator iterator = model.listStatements();
        while (iterator.hasNext()) {
            statement = iterator.next();
            if (property.equals(statement.getPredicate()) && object.equals(statement.getObject())) {
                return statement;
            }
        }
        logger.warn("The Statement founded is NULL.");
        return null;
    }

    /**
     * Method to update a Property on a Specific Resource.
     * @param resource the resource to update.
     * @param property the Property to set.
     * @param object the Object value to reference with the new property.
     */
    public static void updateProperty(Resource resource, Property property, Resource object) {
        try {
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            resource.addProperty(property, object);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    /**
     * Method approximate : gets first match (object).
     *
     * @param resource resource to find.
     * @param property property to find.
     * @return string of the proerty you found.
     */
    public static RDFNode findObject(Resource resource, Property property) {
        RDFNode node = findFirstPropertyValue(resource, property);
        if (node == null) {
            logger.warn("The RDFNode founded is NULL.");
            return null;
        }
        return node;
    }

    /**
     * Method to set/replace/update a property-object.
     *
     * @param resource resource to find.
     * @param property property to find.
     * @param object   value of the object you found.
     * @return if true all the operation are done.
     */
    public static boolean updatePropertyObject(Resource resource, Property property, Resource object) {
        try {
            StmtIterator iterator = resource.listProperties(property);
            while (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
            resource.addProperty(property, object);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method for replace/update a literal value on the jena model.
     *
     * @param model   jena model.
     * @param literal literal to update.
     * @param value   new value of the literal.
     * @return if true all the operation are done.
     */
    public static boolean updateLiteralValue(Model model, Literal literal, String value) {
        try {
            Literal newLiteral = model.createLiteral(value);
            Set<Statement> statements = new HashSet<>();
            StmtIterator iterator = model.listStatements(null, null, literal);
            while (iterator.hasNext()) {
                statements.add(iterator.next());
            }
            Iterator<Statement> setIterator = statements.iterator();
            Statement statement;
            while (setIterator.hasNext()) {
                statement = setIterator.next();
                model.add(statement.getSubject(), statement.getPredicate(), newLiteral);
                model.remove(statement);
            }
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Method utility: create new property for a Model.
     *
     * @param modelOrUri the Jena Model where search the property.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    private static RDFNode createRDFNodeBase(
            Object modelOrUri, Object subjectIri, Lang lang, RDFDatatype rdfDatatype, Boolean isXmL) {

        if(subjectIri instanceof Node){
            if(modelOrUri == null) return createModel().asRDFNode((Node) subjectIri);
            else if(modelOrUri instanceof Model) return ((Model)modelOrUri).asRDFNode((Node) subjectIri);
            else return (RDFNode) subjectIri;
        }else if (subjectIri instanceof LiteralLabel) {
            return toRDFNode(toNode(subjectIri));
        } else if (lang != null && rdfDatatype != null) {
            return toRDFNode(toNode(String.valueOf(subjectIri), lang.getLabel(), rdfDatatype));
        } else if (lang != null && isXmL != null) {
            return toRDFNode(toNode(String.valueOf(subjectIri), lang.getLabel(), isXmL));
        } else if (rdfDatatype != null) {
            return toRDFNode(toNode(String.valueOf(subjectIri), rdfDatatype));
        } else {
            subjectIri = toId(subjectIri);
        }

        if (isIRI(subjectIri) || isUri(subjectIri)) {
            if (modelOrUri != null && modelOrUri instanceof Model) {
                try {
                    //return ((Model)modelOrUri).asRDFNode(NodeUtils.asNode(toIri(subjectIri)));
                    return toRDFNode(toNode(toIri(subjectIri)),((Model) modelOrUri));
                } catch (Exception e) {
                    //return ((Model)modelOrUri).asRDFNode(NodeUtils.asNode(toString(subjectIri)));
                    return toRDFNode(toNode(String.valueOf(subjectIri)),((Model) modelOrUri));
                }
            } else {
                try {
                    return toRDFNode(toNode(String.valueOf(toIri(subjectIri))));
                } catch (Exception e) {
                    return toRDFNode(toNode(String.valueOf(subjectIri)));
                    //return ((Model)modelOrUri).asRDFNode(NodeUtils.asNode(toString(subjectIri)));
                }
            }
        } else { //maybe is a Literal
            if (modelOrUri != null && modelOrUri instanceof Model) {
                //return ((Model) modelOrUri).asRDFNode(NodeFactory.createLiteral(String.valueOf(subjectIri)));
                return toRDFNode(toNode(String.valueOf(subjectIri)),((Model) modelOrUri));
            } else {
                return toRDFNode(toNode(String.valueOf(subjectIri)));
            }
        }
    }

    /**
     * Method utility: create new property for a Model.
     *
     * @param modelOrUri the Jena Model where search the property.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode toRDFNode(Object modelOrUri, Object subjectIri) {
        return createRDFNodeBase(null, subjectIri, null, null, null);
    }

    /**
     * Method utility: create new property for a Model.
     *
     * @param modelOrUri the Jena Model where search the property.
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode toRDFNode(Object modelOrUri, LiteralLabel subjectIri) {
        return createRDFNodeBase(null, subjectIri, null, null, null);
    }

    /**
     * Method utility: create new property for a Model.
     *
     * @param subjectIri string of the subject.
     * @return RDFNode.
     */
    public static RDFNode toRDFNode(Object subjectIri) {
        return createRDFNodeBase(null, subjectIri, null, null, null);
    }

    /**
     * Method to convert a Node to a RDFNode.
     * href: http://willware.blogspot.it/2010/02/jena-node-versus-rdfnode.html
     *
     * @param node  the Node to convert.
     * @param model the Model fo reference for create the RDFNode.
     * @return the RDFNode result.
     */
    public static RDFNode toRDFNode(Node node,Model model) {
        return createRDFNodeBase(model,node, null, null, null);
    }

    /**
     * Method to convert a Node to a RDFNode.
     * href: http://willware.blogspot.it/2010/02/jena-node-versus-rdfnode.html
     *
     * @param node the Node to convert.
     * @return the RDFNode result.
     */
    public static RDFNode toRDFNode(Node node) {
        return createRDFNodeBase(null,node, null, null, null);
    }

    /**
     * Method to create a Jena Resource.
     *
     * @param localNameOrUri the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    public static Resource toResource(String localNameOrUri) {
        return createResourceBase(null, localNameOrUri);
    }

    /**
     * Method to create a Jena Resource.
     *
     * @param localNameOrUri the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    public static Resource toResource(Object localNameOrUri) {
        return createResourceBase(null, localNameOrUri);
    }

    public static Resource toResource(RDFNode localNameOrUri) {
        return createResourceBase(null, localNameOrUri);
    }

    public static Resource toResource(Model model, Object localNameOrUri) {
        return createResourceBase(model, localNameOrUri);
    }

    public static Resource toResource(String graphUri, Object localNameOrUri) {
        return createResourceBase(graphUri, localNameOrUri);
    }

    public static Resource toResource(Object graphUriOrModel, Object localNameOrUri) {
        return createResourceBase(graphUriOrModel, localNameOrUri);
    }

    /**
     * Method to create a Jena Resource.
     *
     * @param graphUriOrModel the String iri or the Jena Model.
     * @param localNameOrUri  the String name local Graph or the String iri of the subject.
     * @return the Jena Resource.
     */
    private static Resource createResourceBase(Object graphUriOrModel, Object localNameOrUri) {
        if (!(localNameOrUri instanceof RDFNode)) localNameOrUri = toId(localNameOrUri);
        if (graphUriOrModel == null) {
            if (localNameOrUri instanceof RDFNode) {
                RDFNode rdfNode = (RDFNode) localNameOrUri;
                if (rdfNode.isResource() || rdfNode.isURIResource() || rdfNode.isAnon()) return rdfNode.asResource();
                else return null;
            } else {
                if (isIRI(localNameOrUri) || isUri(localNameOrUri))
                    return ResourceFactory.createResource(String.valueOf(localNameOrUri));
                else
                    return null;
            }
        } else {
            if (isString(graphUriOrModel)) {
                String s = String.valueOf(graphUriOrModel);
                if (s.endsWith("/") || s.endsWith("#")) {
                    String uri = s + localNameOrUri;
                    if (isIRI(uri) || isUri(uri)) return ResourceFactory.createResource(uri);
                    else return null;
                } else {
                    String uri = s + "/" + localNameOrUri;
                    if (isIRI(uri) || isUri(uri)) return ResourceFactory.createResource(uri);
                    else return null;
                }
            } else if (graphUriOrModel instanceof Model) {
                return toRDFNode(graphUriOrModel, localNameOrUri).asResource();
            } else return null;
        }
    }

    /**
     * Method to create a Jena Property.
     *
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param predicateUri       the String name local Graph or the String iri of the subject.
     * @param impl               if true use the PredicateImpl to create the predicate.
     * @return the Jena Predicate.
     */
    private static Property createPropertyBase(Object stringOrModelGraph, Object predicateUri, boolean impl) {
        if (stringOrModelGraph == null) {
            if (impl) {
                if (predicateUri != null) {
                    if (predicateUri instanceof RDFNode) return (Property) ((RDFNode) predicateUri).asResource();
                    else if (isIRI(predicateUri) || isUri(predicateUri))
                        return new PropertyImpl(String.valueOf(predicateUri));
                    else
                        return null;
                } else return null;
            } else {
                if (predicateUri instanceof RDFNode) return (Property) ((RDFNode) predicateUri).asResource();
                if (predicateUri != null) {
                    if (isIRI(predicateUri) || isUri(predicateUri))
                        return ResourceFactory.createProperty(String.valueOf(predicateUri));
                    else
                        return ResourceFactory.createProperty(String.valueOf(predicateUri));
                } else return null;
            }
        }
        if (isStringNoEmpty(stringOrModelGraph)) {
            if (!String.valueOf(stringOrModelGraph).endsWith("/") || !String.valueOf(stringOrModelGraph).endsWith("#")) {
                stringOrModelGraph = stringOrModelGraph + "/";
            }
            if (impl) {
                if (predicateUri != null) {
                    if (isIRI(predicateUri) || isUri(predicateUri) || String.valueOf(stringOrModelGraph).isEmpty()) {
                        return new PropertyImpl(String.valueOf(predicateUri));
                    } else if (isStringNoEmpty(stringOrModelGraph) &&
                            isIRI(String.valueOf(stringOrModelGraph) + "/" + String.valueOf(predicateUri))) {
                        return new PropertyImpl(String.valueOf(stringOrModelGraph), String.valueOf(predicateUri));
                    } else return null;
                } else return null;
            } else {
                if (predicateUri != null) {
                    if (isIRI(predicateUri) || isUri(predicateUri) || String.valueOf(stringOrModelGraph).isEmpty()) {
                        return ResourceFactory.createProperty(String.valueOf(predicateUri));
                    } else if (isStringNoEmpty(stringOrModelGraph) &&
                            isIRI(String.valueOf(stringOrModelGraph) + "/" + String.valueOf(predicateUri))) {
                        return ResourceFactory.createProperty(String.valueOf(stringOrModelGraph), String.valueOf(predicateUri));
                    } else return null;
                } else return null;
            }
        } else if (stringOrModelGraph instanceof Model && isStringNoEmpty(predicateUri)) {
            return toRDFNode(stringOrModelGraph, predicateUri).as(Property.class);
        } else return null;
    }

    /**
     * Method to create a Jena Property.
     *
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(Object stringOrModelGraph, String localNameOrSubject) {
        return createPropertyBase(stringOrModelGraph, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     *
     * @param stringOrModelGraph the String iri or the Jena Model.
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(Object stringOrModelGraph, Object localNameOrSubject) {
        return createPropertyBase(stringOrModelGraph, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     *
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(String localNameOrSubject) {
        return createPropertyBase(null, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     *
     * @param localNameOrSubject the RDFNode name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(RDFNode localNameOrSubject) {
        return createPropertyBase(null, localNameOrSubject, false);
    }

    /**
     * Method to create a Jena Property.
     *
     * @param localNameOrSubject the String name local Graph or the String iri of the subject.
     * @return the Jena Predicate.
     */
    public static Property toProperty(Object localNameOrSubject) {
        return createPropertyBase(null, localNameOrSubject, false);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param model          the Model jean where create the Literal.
     * @param stringOrObject the value of the Jena Literal.
     * @param datatype       the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    private static Literal createLiteralBase(Model model, Object stringOrObject, RDFDatatype datatype) {
        if (model == null) {
            if (stringOrObject instanceof RDFNode) {
                RDFNode x = (RDFNode) stringOrObject;
                if (x.asLiteral().getDatatype().equals(XSDDatatype.XSDstring)) {
                    return ResourceFactory.createPlainLiteral(x.asLiteral().getLexicalForm());
                    //return ((RDFNode) stringOrObject).asLiteral();
                } else if (x.isURIResource()) {
                    //TODO try to avoid this because is wrong but works....
                    return (Literal) x.asResource();
                } else {
                    return ResourceFactory.createTypedLiteral(stringOrObject);
                }
            } else if (isString(stringOrObject)) {
                if (datatype != null)
                    return ResourceFactory.createTypedLiteral(String.valueOf(stringOrObject), datatype);
                else return ResourceFactory.createPlainLiteral(String.valueOf(stringOrObject));
            } else {
                if (datatype != null)
                    return ResourceFactory.createTypedLiteral(String.valueOf(stringOrObject), datatype);
                return ResourceFactory.createTypedLiteral(stringOrObject);
            }
        } else {
            if (stringOrObject instanceof RDFNode) {
                if (datatype != null) return model.createTypedLiteral(stringOrObject, datatype);
                else {
                    RDFNode x = (RDFNode) stringOrObject;
                    if (x.asLiteral().getDatatype().equals(XSDDatatype.XSDstring)) {
                        return model.createLiteral(x.toString());
                    } else if (x.isURIResource()) {
                        //TODO try to avoid this because is wrong but works....
                        return (Literal) x.asResource();
                    } else {
                        return model.createTypedLiteral(stringOrObject);
                    }
                }
            } else if (isString(stringOrObject)) {
                if (datatype != null) return model.createTypedLiteral(String.valueOf(stringOrObject), datatype);
                else return model.createLiteral(String.valueOf(stringOrObject));
            } else {
                if (datatype != null) return model.createTypedLiteral(stringOrObject, datatype);
                return model.createTypedLiteral(stringOrObject);
            }
        }
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param model          the Model Jena where create the Literal.
     * @param stringOrObject the value of the Jena Literal.
     * @param datatype       the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Model model, Object stringOrObject, RDFDatatype datatype) {
        return createLiteralBase(model, stringOrObject, datatype);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the value of the Jena Literal.
     * @param datatype       the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Object stringOrObject, RDFDatatype datatype) {
        return createLiteralBase(null, stringOrObject, datatype);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the value of the Jena Literal.
     * @param typeUri        the Jena RDFDatatype of the literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Object stringOrObject, String typeUri) {
        return createLiteralBase(null, stringOrObject, toRDFDatatype(typeUri));
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the value of the Jena Literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(Object stringOrObject) {
        return createLiteralBase(null, stringOrObject, null);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the RDFNode of the Jena Literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(RDFNode stringOrObject) {
        return createLiteralBase(null, stringOrObject, null);
    }

    /**
     * Method utility: create new typed literal from uri.
     *
     * @param stringOrObject the value of the Jena Literal.
     * @return the Jena Literal.
     */
    public static Literal toLiteral(String stringOrObject) {
        return createLiteralBase(null, stringOrObject, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param model     the Jena Model.
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @return Statement.
     */
    public static Statement toStatement(Model model, String subject, String predicate, String object) {
        return createStatementBase(model, subject, predicate, object, null, null);
    }

    public static Statement toStatement(
            Model model, String subject, String predicate, Object object, String graphUri, XSDDatatype xsdDatatype) {
        return createStatementBase(model, subject, predicate, object, graphUri, xsdDatatype);
    }

    public static Statement toStatement(
            Model model, URI subject, URI predicate, URI object, String graphUri, XSDDatatype xsdDatatype) {
        return createStatementBase(model, subject, predicate, object, graphUri, xsdDatatype);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param model       the Jena Model.
     * @param subject     the iri subject.
     * @param predicate   the iri predicate.
     * @param object      the iri object.
     * @param graphUri    the iri of the graph.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    private static Statement createStatementBase(
            Model model, Object subject, Object predicate, Object object, Object graphUri, XSDDatatype xsdDatatype) {
        if (model == null) {
            if (graphUri == null || isStringOrUriEmpty(graphUri)) {
                return ResourceFactory.createStatement(toResource(subject),
                        toProperty(predicate), toLiteral(object, xsdDatatype));
            } else {
                return ResourceFactory.createStatement(toResource(graphUri, subject),
                        toProperty(graphUri, predicate), toLiteral(object, xsdDatatype));
            }
        } else {
            return model.createStatement(toResource(model, subject),
                    toProperty(model, predicate), toLiteral(object, xsdDatatype));
        }

    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param model     the Jena Model.
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @param graphUri  the iri of the graph.
     * @return Statement.
     */
    public static Statement toStatement(Model model, String subject, String predicate, Object object, String graphUri) {
        return createStatementBase(model, subject, predicate, object, graphUri, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @param graphUri  the iri of the graph.
     * @return Statement.
     */
    public static Statement toStatement(String subject, String predicate, String object, String graphUri) {
        return createStatementBase(null, subject, predicate, object, graphUri, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @return Statement.
     */
    public static Statement toStatement(RDFNode subject, RDFNode predicate, RDFNode object) {
        return createStatementBase(null, subject, predicate, object, null, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @return Statement.
     */
    public static Statement toStatement(String subject, String predicate, String object) {
        return createStatementBase(null, subject, predicate, object, null, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject   the iri subject.
     * @param predicate the iri predicate.
     * @param object    the iri object.
     * @return Statement.
     */
    public static Statement toStatement(String subject, String predicate, Object object) {
        return createStatementBase(null, subject, predicate, object, null, null);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject     the iri subject.
     * @param predicate   the iri predicate.
     * @param object      the iri object.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    public static Statement toStatement(String subject, String predicate, Object object, XSDDatatype xsdDatatype) {
        return createStatementBase(null, subject, predicate, object, null, xsdDatatype);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject     the iri subject.
     * @param predicate   the iri predicate.
     * @param object      the iri object.
     * @param graphUri    the URI to the graph base of the ontology.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    public static Statement toStatement(String subject, String predicate, Object object, String graphUri, XSDDatatype xsdDatatype) {
        return createStatementBase(null, subject, predicate, object, graphUri, xsdDatatype);
    }

    /**
     * Method utility: create statement form a jena Model.
     *
     * @param subject     the iri subject.
     * @param predicate   the iri predicate.
     * @param object      the iri object.
     * @param graphUri    the URI to the graph base of the ontology.
     * @param xsdDatatype the XSDDatatype of the Literal
     * @return Statement.
     */
    public static Statement toStatement(String subject, String predicate, Object object, String graphUri, String xsdDatatype) {
        return createStatementBase(null, subject, predicate, object, graphUri, toXSDDatatype(xsdDatatype));
    }

    /**
     * Method to create a Jena Dataset for the SPARQL query.
     *
     * @param dftGraphURI    the URI of the location of the resource with the triples.
     * @param namedGraphURIs the URI's of all locations with name.
     * @return the JENA Dataset.
     */
    public static Dataset createDataSet(String dftGraphURI, List<String> namedGraphURIs) {
       /* String dftGraphURI = "file:default-graph.ttl" ;
        List namedGraphURIs = new ArrayList() ;
        namedGraphURIs.add("file:named-1.ttl") ;
        namedGraphURIs.add("file:named-2.ttl") ;*/
        return DatasetFactory.create(dftGraphURI, namedGraphURIs);
    }

    /**
     * Method utility: create new default Jena Model.
     *
     * @return Jena Model.
     */
    public static Model createModel() {
        return ModelFactory.createDefaultModel();
    }

    /**
     * Method utility: create new default Jena Graph.
     *
     * @return Jena Graph.
     */
    public static Graph createGraph() {
        return GraphFactory.createDefaultGraph();
    }

    /**
     * Method to convert a Jena Model to a Jena Ontology Model.
     *
     * @param model the Jena Base Model.
     * @return the Jena Ontology Model.
     */
    public static OntModel toOntoModel(Model model) {
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
    }

    /**
     * Method to convert a standard Jena Model to a Jena InfModel.
     *
     * @param model the Jena Model.
     * @return the Jena InfModel.
     */
    public static InfModel toInfModel(Model model) {
        Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();
        return ModelFactory.createInfModel(reasoner, model);
    }
    /**
     * Method to convert a standard Jena Model to a Jena InfModel.
     *
     * @param model the Jena Model.
     * @param reasoner the Jena Reasoner to use.
     * @return the Jena InfModel.
     */
    public static InfModel toInfModel(Model model,Reasoner reasoner) {
        //Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        return ModelFactory.createInfModel(reasoner, model);
    }

    /**
     * Method to convert a dataset to a OntoModel, now you can add some rule to uor TDB Model.
     *
     * @param dataset   the DataSet Jena Object.
     * @param OwlOrSWRL the Rules to add to the DataSet Jena Object.
     * @return the Ontology Model.
     */
    public static OntModel toOntoModel(Dataset dataset, URL OwlOrSWRL) {
        Model m = dataset.getDefaultModel(); //the TDB data
        Model toto = ModelFactory.createDefaultModel();
        toto.read(OwlOrSWRL.toString()); // the OWL & SWRL rules inside
        Model union = ModelFactory.createUnion(m, toto); //Merging both
        OntModelSpec spec = OntModelSpec.RDFS_MEM_TRANS_INF;
        //return ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC ,union); // Use Pellet reasonner
        return ModelFactory.createOntologyModel(spec, union); // Use Pellet reasonner
    }

    /**
     * Method to convert a Jena TripleMatch to the Jena Triple.
     *
     * @param statement the Statement Jena.
     * @return the Jena triple.
     */
    public static Triple toTriple(Statement statement) {
        return statement.asTriple();
    }

    /**
     * Method to convert a set of Jena Graph Nodes to a Jena Graph Triple Object.
     * old name : createTriple.
     *
     * @param subject   the Jena Graph Node Subject of the triple.
     * @param predicate the Jena Graph Node Predicate of the triple.
     * @param object    the Jena Graph Node Object of the triple.
     * @return the Jena Graph Triple Object setted with the content of the jena Graph Nodes.
     */
    public Triple toTriple(Node subject, Node predicate, Node object) {
        return new Triple(subject, predicate, object);
    }

    /**
     * Method to convert a String uri to a jena Graph Node.
     * old name : createNode.
     * href: http://willware.blogspot.it/2010/02/jena-node-versus-rdfnode.html
     *
     * @param resource any element on API Jena can be converted to a Node.
     * @return the Jena Graph Node.
     */
    private static Node createNodeBase(Object resource, String lang, RDFDatatype rdfDatatype, Boolean xml){
        if(lang != null && rdfDatatype != null){
            return NodeFactory.createLiteral(String.valueOf(resource),lang,rdfDatatype);
        }else if(rdfDatatype != null){
            return NodeFactory.createLiteral(String.valueOf(resource),rdfDatatype);
        }else if(lang != null && xml != null){
            return NodeFactory.createLiteral(String.valueOf(resource),lang,xml);
        }else {
            if (resource instanceof Literal) {
                return ((Literal) resource).asNode();
            } else if (resource instanceof Resource) {
                return ((Resource) resource).asNode();
            } else if (resource instanceof RDFNode) {
                return ((RDFNode) resource).asNode();
            } else if (resource instanceof LiteralLabel) {
                return NodeFactory.createLiteral((LiteralLabel) resource);
            } else if (resource instanceof String) {
                if (isIRI(resource)) {
                    return NodeUtils.asNode(String.valueOf(resource));
                } else if (StringUtilities.isURI(resource)) {
                    return NodeFactory.createURI(String.valueOf(resource));
                } else {
                    return NodeFactory.createLiteral(String.valueOf(resource));
                }
            }
        }
        return null;
    }

    public static Node toNode(Object resource,String lang,RDFDatatype rdfDatatype){
        return createNodeBase(resource,null,rdfDatatype,null);
    }

    public static Node toNode(Object resource,String lang,boolean isXml){
        return createNodeBase(resource,null,null,isXml);
    }

    public static Node toNode(Object resource){
        return createNodeBase(resource,null,null,null);
    }

    public static Node toNode(Object resource,RDFDatatype rdfDatatype){
        return createNodeBase(resource,null,rdfDatatype,null);
    }

    /**
     * Method to convert a Jena Model to a Jena Graph.
     *
     * @param model the jena Model
     * @return the Jena Graph.
     */
    public Graph toGraph(Model model) {
        return model.getGraph();
    }

    /**
     * Method to convert a Jena DataSet to a Jena Graph.
     *
     * @param dataSet the Jena dataSet.
     * @return the Jena Graph.
     */
    public Graph toGraph(Dataset dataSet) {
        return dataSet.asDatasetGraph().getDefaultGraph();
    }

    /**
     * Method to convert a Jena DataSet to a Jena Graph.
     *
     * @param dataSet   the Jena dataSet.
     * @param nodeGraph the Node Jena .
     * @return the Jena Graph.
     */
    public Graph toGraph(Dataset dataSet, Node nodeGraph) {
        return dataSet.asDatasetGraph().getGraph(nodeGraph);
    }

    /**
     * Method to convert a Jena Graph to a Jena Model.
     *
     * @param graph the Jena Graph.
     * @return the Jena Model.
     */
    public static Model toModel(Graph graph) {
        return ModelFactory.createModelForGraph(graph);
    }

    /**
     * Method to convert a Jena DataSet to a Jena Model.
     *
     * @param dataSet the Jena dataSet.
     * @return the Jena Model.
     */
    public static Model toModel(Dataset dataSet) {
        return dataSet.getDefaultModel();
    }

    /**
     * Method to convert a Jena DataSet to a Jena Model.
     *
     * @param dataSet the Jena dataSet.
     * @param uri     the String URI of the Graph base to use.
     * @return the Jena Model.
     */
    public static Model toModel(Dataset dataSet, String uri) {
        return dataSet.getNamedModel(uri);
    }

    /**
     * Method to get a Dataset from a existent Jena Model.
     * old_name:getDataSetFromModel
     * @param model the Jena Model.
     * @return the Dataset extract from Jena Model.
     */
    public static Dataset toDataset(Model model) {
        Dataset dataset = DatasetFactory.createGeneral();
        dataset.setDefaultModel(model);
        return dataset;
    }

    /**
     * Method to get a Dataset from a existent List of Jena Models.
     * old_name: getDataSetFromListOfModel
     * @param baseModel the Jena Model.
     * @param listModel the Map of Model with the specific uri to add to the new Dataset.
     * @return the Dataset extract from a list of Jena Model.
     */
    public static Dataset toDataset(Model baseModel, Map<String, Model> listModel) {
        Dataset dataset = DatasetFactory.createGeneral();
        dataset.setDefaultModel(baseModel);
        for (Map.Entry<String, Model> entry : listModel.entrySet()) {
            dataset.addNamedModel(entry.getKey(), entry.getValue());
        }
        return dataset;
    }

    /**
     * Method to convert a Model to a DataSet.
     *
     * @param graph the Graph Jena to convert.
     * @return the DataSet Jena .
     */
    public static Dataset toDataset(Graph graph) {
        return new DatasetImpl(toModel(graph));
    }


    //////////////////////////////////////////////////////7
    //Some method with the deprecated Graph package.
    ////////////////////////////////////////////////////////77

    /**
     * Method to add a List of jena Graph triple to a Jena Graph Object.
     *
     * @param triples the List of jena Graph triples.
     * @param graph   the jena Graph Object.
     */
    public void addTriplesToGraph(List<Triple> triples, Graph graph) {
        GraphUtil.add(graph,triples);
    }

    //----------------------------------------
    //NEW METHODS
    //----------------------------------------

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     *
     * @param query the Query Select to analyze.
     * @param model the Jena Model.
     * @return the Long execution time for evaluate the query.
     */
    public static Long getExecutionQueryTime(String query, Model model) {
        return getExecutionQueryTime(toQuery(query), model);
    }

    /**
     * Method to get the execution time of the query on the remote repository Sesame.
     *
     * @param query the Query Select to analyze.
     * @param model the Jena Model.
     * @return the Long execution time for evaluate the query.
     */
    public static Long getExecutionQueryTime(Query query, Model model) {
        try {
            Timer timer = new Timer();
            //Dataset ds = qexec.getDataset();
            //Dataset ds = toDataset(model);
            long calculate;
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            if (query.isSelectType()) {
                timer.startTimer();
                qexec.execSelect();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else if (query.isConstructType()) {
                timer.startTimer();
                qexec.execConstruct();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else if (query.isAskType()) {
                timer.startTimer();
                qexec.execAsk();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else if (query.isDescribeType()) {
                timer.startTimer();
                qexec.execDescribe();
                calculate = timer.endTimer();   // Time in milliseconds.
            } else {
                calculate = 0L;
            }
            logger.info("Query JENA Model result(s) in " + calculate + "ms.");
            return calculate;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return 0L;
        }
    }


    //-----------------------------------------------------------------------
    // IMPORT and EXPORT
    //-----------------------------------------------------------------------

    /**
     * This Jena example re-uses named graphs stored in a TDB model as the imports in an ontology.
     * href: https://gist.github.com/ijdickinson/3830267
     *
     * @param graphName the String Graph Name.
     * @param model     the Model Jena.
     * @return the OntologyModel.
     */
    public static OntModel importModelToTDBModel(String graphName, Model model) {
        //Model model = createModel();
        //Initialise the local TDB image if necessary.
        String tdbPath = "./target/data/tdb";
        File file = new File(tdbPath);
        boolean mkdirs = file.mkdirs();
        Dataset ds = TDBFactory.createDataset(tdbPath);
        //Load some test content into TDB, unless it has already been initialized.
        if (!ds.containsNamedModel(graphName)) {
            Model m = createModel();
            m.createResource(graphName)
                    .addProperty(RDF.type, OWL.Ontology)
                    .addProperty(DCTerms.creator, "test import");
            ds.addNamedModel(graphName, m);
            TDB.sync(m);
            //loadExampleGraph( ONT1, ds, "The Dread Pirate Roberts" );
        }
        return importingOntologyToModel(ds, model);
    }

    /**
     * Now we create an ontology model that imports ont1 and ont2, but arrange
     * that these are obtained from the TDB image.
     * href: https://gist.github.com/ijdickinson/3830267
     * @param ds the DataStore Jena where allocate the import..
     * @param sourceModel the Model Jena to import.
     * @return  the OntModel Jena where is allocate dthe content of the <tt>Model</tt>.
     */
    public static OntModel importingOntologyToModel(Dataset ds, Model sourceModel) {
        // this is a test, so empty the base first just to be sure
        Model base = ds.getDefaultModel();
        base.removeAll();
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setImportModelGetter(new LocalTDBModelGetter(ds));
        OntModel om = ModelFactory.createOntologyModel(spec, base);
        // now read the source model
        StringReader in = new StringReader(toString(sourceModel,null,null));
        om.read(in, null, "Turtle");
        return om;
    }


    //--------------------------------
    //Utility private methods
    //--------------------------------

    /**
     * Method to check if a String uri is a IRI normalized.
     * http://stackoverflow.com/questions/9419658/normalising-possibly-encoded-uri-strings-in-java
     *
     * @param uri the String to verify.
     * @return if true the String is a valid IRI.
     */
    private static Boolean isIRI(Object uri) {
        try {
            if (isString(uri)) {
                IRIFactory factory = IRIFactory.uriImplementation();
                IRI iri = factory.construct(String.valueOf(uri));
           /* ArrayList<String> a = new ArrayList<>();
            a.add(iri.getScheme());
            a.add(iri.getRawUserinfo());
            a.add(iri.getRawHost());
            a.add(iri.getRawPath());
            a.add(iri.getRawQuery());
            a.add(iri.getRawFragment());*/
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method to convert a URI os String reference to a resource to a good ID.
     * NOTE: URI string: scheme://authority/path?query#fragment
     *
     * @param uriResource the String or URI reference Resource.
     * @return the String id of the Resource.
     */
    private static String toId(Object uriResource) {
        return URI.create(String.valueOf(uriResource).replaceAll("\\r\\n|\\r|\\n", " ").replaceAll("\\s+", "_").trim()).toString();
    }

    private static boolean isUri(Object uriResource) {
        if (uriResource instanceof URI) return true;
        else {
            try {
                //noinspection ResultOfMethodCallIgnored
                URI.create(String.valueOf(uriResource));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    private static boolean isStringNoEmpty(Object uriResource) {
        return (uriResource instanceof String && !isNullOrEmpty(String.valueOf(uriResource)));
    }

    private static boolean isStringEmpty(Object uriResource) {
        return (uriResource instanceof String && isNullOrEmpty(String.valueOf(uriResource)));
    }

    private static boolean isStringOrUriEmpty(Object uriResource) {
        return (
                (uriResource instanceof String && isNullOrEmpty(String.valueOf(uriResource))) ||
                        (uriResource instanceof URI && isNullOrEmpty(String.valueOf(uriResource)))
        );
    }

    private static boolean isString(Object uriResource) {
        return (uriResource instanceof String);
    }

    private static IRI toIri(Object uriResource) {
        return IRIFactory.uriImplementation().construct(String.valueOf(uriResource));
    }

    /**
     * Method to Returns true if the parameter is null or empty. false otherwise.
     *
     * @param text string text.
     * @return true if the parameter is null or empty.
     */
    private static boolean isNullOrEmpty(String text) {
        return (text == null) || text.equals("") || text.isEmpty() || text.trim().isEmpty();
    }

    /***********************************/
    /* Inner class definitions         */
    /***********************************/

    /**
     * <p>A type of model getter that loads models from a local TDB instance,
     * if they exist as named graphs using the model URI as the graph name.</p>
     */
    static class LocalTDBModelGetter implements ModelGetter {

        private final Dataset ds;

        public LocalTDBModelGetter(Dataset dataset) {
            ds = dataset;
        }

        @Override
        public Model getModel(String uri) {
            throw new NotImplemented("getModel( String  ) is not implemented");
        }

        @Override
        public Model getModel(String uri, ModelReader loadIfAbsent) {
            Model m = ds.getNamedModel(uri);
            // create the model if necessary. In actual fact, this example code
            // will not exercise this code path, since we pre-define the models
            // we want to see in TDB
            if (m == null) {
                m = ModelFactory.createDefaultModel();
                loadIfAbsent.readModel(m, uri);
                ds.addNamedModel(uri, m);
            }
            return m;
        }
    } // LocalTDBModelGetter


}//end of the class JenaKit
