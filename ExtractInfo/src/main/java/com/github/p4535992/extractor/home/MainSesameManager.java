package com.github.p4535992.extractor.home;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.repositoryRDF.sesame.Sesame28Kit;
import com.github.p4535992.util.repositoryRDF.sparql.SparqlKit;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.base.RepositoryConnectionWrapper;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by 4535992 on 06/10/2015.
 * @author 4535992.
 * @version 2015-10-06.
 */
public class MainSesameManager {

    private static String SPARQL_SELECT_ALL_KM4cSERVCIE  = "CONSTRUCT {?service ?p ?o.} "
            + "WHERE {?service a <http://www.disit.org/km4city/schema#Service>;"
            + "       ?p ?o . } LIMIT 600000 OFFSET 0 ";

    private static String SPARQL_SELECT_KM4C_SERVICE  = "SELECT ?service ?p ?o "
            + "WHERE {?service a <http://www.disit.org/km4city/schema#Service>; "
            + "       ?p ?o . } LIMIT 600000 OFFSET 0 ";

    private static String SPARQL_SELECT_Q1 ="SELECT ?location,?lat, ?long "
            +"WHERE ?location   a <http://purl.org/goodrelations/v1#Location> ; "
            +"              <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat; "
            +"              <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long. "
            +"FILTER(!isBlank(?lat) && !isLiteral(?lat) && !isBlank(?long) && !isLiteral(?long)) } }";

    private static String SPARQL_SELECT_Q2 ="SELECT ?indirizzo "
            +"WHERE{ ?business a <http://purl.org/goodrelations/v1#BusinessEntity> ; "
            +" <http://schema.org/streetAddress> ?indirizzo. "
            +"SERVICE <http://localhost:8080/openrdfworkbench/repositories/repkm4c/query>{ "
            +"                 ?service a <http://www.disit.org/km4city/schema#Service>. } "
            +"FILTER EXISTS {?business  <http://www.w3.org/2002/07/owl#sameAs>  ?service}}";

    private static String SPARQL_SELECT_Q3 = "SELECT ?tel,?fax " +
            "WHERE{  ?business a <http://purl.org/goodrelations/v1#BusinessEntity> ; " +
            "                    <http://schema.org/telephone> ?tel; " +
            "                       <http://schema.org/fax> ?fax. " +
            "FILTER{str(?tel) = str(fax)}}";

    private static String SPARQL_SELECT_Q4 ="SELECT ?tel,?fax " +
            "WHERE{ ?business a <http://purl.org/goodrelations/v1#BusinessEntity> ; " +
            "                   <http://schema.org/telephone> ?tel;  " +
            "                   <http://schema.org/fax> ?fax. " +
            "FILTER{str(?tel) = str(fax)}}";

    private static String SPARQL_SELECT_Q5 ="SELECT  ?business " +
            "WHERE{  ?business a <http://purl.org/goodrelations/v1#BusinessEntity> ; " +
            "                    <http://schema.org/telephone> \"xxxxxx\";  " +
            "                    <http://schema.org/fax> \"yyyyyy\"; " +
            "                    <http://schema.org/streetAddress> \"zzzzzz\". }";

    public static void main(String args[]) throws RepositoryException, MalformedQueryException, QueryEvaluationException, UnsupportedEncodingException, UpdateExecutionException {
        SystemLog.setIsPRINT(false);
        SystemLog.setIsLogOff(true);

        Sesame28Kit sesame = Sesame28Kit.getInstance();
        //sesame.setURLRepositoryId("km4city04");
        Repository rep = sesame.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");
        String query = (SparqlKit.preparePrefix()+SPARQL_SELECT_KM4C_SERVICE).trim();
        //RepositoryConnectionWrapper wrap = sesame.setNewRepositoryConnectionWrappper(rep);
        //QueryLanguage sparql = sesame.stringToQueryLanguage("SPARQL");

        //QueryLanguage sparql = sesame.checkLanguageOfQuery(query);

        //wrap.setDelegate(rep.getConnection());
        /*GraphQuery gq = wrap.prepareGraphQuery(
                sparql,
                query,
                "http://www.disit.org/km4city/schema");*/
        /*TupleQuery tq = wrap.prepareTupleQuery(
                sparql,
                query,
                "http://www.disit.org/km4city/schema");*/
        Long ss = sesame.getExecutionQueryTime(query);



       /* sesame.setOutput(
                "C:\\Users\\tenti\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testKm4c2"
                , "ttl", true);
        sesame.executeQuerySPARQLFromString(query);
        sesame.shutDownRepository();*/



        sesame.setOutput(
                "C:\\Users\\tenti\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testKm4c2"
                , "csv", true);
        sesame.executeQuerySPARQLFromString(query);
        sesame.shutDownRepository();
        /*Sesame28Kit sesame2 = Sesame28Kit.getInstance();
        File file = new File("C:\\Users\\tenti\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testSesame.ttl");
        if(file.exists()) {
            sesame2.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");
            sesame2.importIntoRepository(file,"http://www.disit.org/km4city/schema","ttl");
        }*/
    }
}
