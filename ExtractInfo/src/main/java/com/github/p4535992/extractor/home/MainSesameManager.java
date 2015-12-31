package com.github.p4535992.extractor.home;


import com.github.p4535992.util.database.sql.SQLUtilities;

import com.github.p4535992.util.database.sql.query.MySQLQuery;
import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import com.github.p4535992.util.log.logback.LogBackUtil;
import com.github.p4535992.util.repositoryRDF.jena.JenaUtilities;
import com.github.p4535992.util.repositoryRDF.jenaAndSesame.JenaSesameUtilities;
import com.github.p4535992.util.repositoryRDF.sesame.SesameUtilities;
import com.github.p4535992.util.repositoryRDF.sparql.SparqlUtilities;

import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.base.RepositoryConnectionWrapper;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RepositoryManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
            + "       ?p ?o . } LIMIT 600 OFFSET 0 ";

    private static String SQL_GET_ALL_SERVICE = "SELECT * FROM geodb.websitehtml ";

    private static String SPARQL_SELECT_Q1 ="SELECT ?location,?lat, ?long "
            +"WHERE ?location   a <http://purl.org/goodrelations/v1#Location> ; "
            +"              <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat; "
            +"              <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long. "
            +"FILTER(!isBlank(?lat) && !isLiteral(?lat) && !isBlank(?long) && !isLiteral(?long)) } }";

    private static String SQL_SELECT_Q1 ="SELECT city,latitude,longitude\n" +
            "FROM geodb.infodocument_2015_09_18\n" +
            "WHERE  city IS NOT NULL AND TRIM(city) <> '' AND concat('',longitude * 1) = longitude\n" +
            "AND concat('',latitude *1) = latitude;";

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


    private static List<String> sparqlQueries = new ArrayList<>();
    private static List<String> sqlQueries = new ArrayList<>();

    private static void initSparqlQueries(){
        sparqlQueries.add(SPARQL_SELECT_Q1);
        /*sparqlQueries.add(SPARQL_SELECT_Q2);
        sparqlQueries.add(SPARQL_SELECT_Q3);
        sparqlQueries.add(SPARQL_SELECT_Q4);
        sparqlQueries.add(SPARQL_SELECT_Q5);*/
    }

    private static void initSqlQueries(){
        sqlQueries.add(SQL_SELECT_Q1);
    }


    public static void main(String args[]) throws RepositoryException, MalformedQueryException, QueryEvaluationException, IOException, UpdateExecutionException, RepositoryConfigException {
        LogBackUtil.console();
        initSparqlQueries();
        initSqlQueries();


        Connection conn = SQLUtilities.getMySqlConnection(
                "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true&user=siimobility&password=siimobility");

        SesameUtilities sesame = SesameUtilities.getInstance();
        //sesame.setURLRepositoryId("km4city04");
        //WORK
        Repository rep = sesame.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");

        //sesame.setPrefixes();

        //NOT WORK WITH OWLIM
        /*RepositoryManager manager = sesame.connectToLocation(
                "C:\\Users\\tenti\\AppData\\Roaming\\Aduna\\OpenRDF Sesame\\repositories");
        Repository rep = manager.getRepository("repKm4c1");*/

        String query = (SparqlUtilities.preparePrefix()+SPARQL_SELECT_KM4C_SERVICE).trim();
        //RepositoryConnectionWrapper wrap = sesame.setRepositoryConnectionWrapper(rep,rep.getConnection());
        //QueryLanguage sparql = sesame.stringToQueryLanguage("SPARQL");

        //QueryLanguage sparql = sesame.checkLanguageOfQuery(query);

        //wrap.setDelegate(rep.getConnection());
       /* GraphQuery gq = wrap.prepareGraphQuery(
                sparql,
                query,
                "http://www.disit.org/km4city/schema");*/


        /*TupleQuery tq = wrap.prepareTupleQuery(
                sparql,
                query,
                "http://www.disit.org/km4city/schema");*/
        //http://localhost:8080/openrdf-workbench/repositories/repKm4c1/update

//        params = { 'context': '<' + graph + '>' }
//        String repository
//        String endpoint = "http://localhost:8080/openrdf-sesame/repositories/%s/statements?%s" % (repository, urllib.urlencode(params))
        //Model jModel = Jena2Kit.execSparqlOnRemote(query,"http://localhost:8080/openrdf-workbench/repositories/repKm4c1/query");


        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"SESAME", "JENA", "SQL","MYSQL"});
        //WORK
        //2669,160,44,185
        for(int i = 0; i < sparqlQueries.size(); i++) {
            //query = sparqlQueries.get(i);
            query = (SparqlUtilities.preparePrefixNoPoint()+SPARQL_SELECT_KM4C_SERVICE).trim();
            Long ss = sesame.getExecutionQueryTime(query);
            org.openrdf.model.Model sModel = sesame.convertRepositoryToModel(rep);

            JenaSesameUtilities jas = JenaSesameUtilities.getInstance();
            com.hp.hpl.jena.rdf.model.Model jModel2 = jas.convertOpenRDFModelToJenaModel(sModel);
            Long yy = JenaUtilities.getExecutionQueryTime(query, jModel2);

            query = sqlQueries.get(i);

          /*  Connection conn3 = SQLUtilities.getMySqlConnection(
                    "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true&user=siimobility&password=siimobility");*/
            Long zz = SQLUtilities.getExecutionTime(query, conn);

            Long xx = MySQLQuery.getExecutionTime(SQL_SELECT_Q1,conn);

            data.add(new String[]{String.valueOf(ss), String.valueOf(yy), String.valueOf(zz),String.valueOf(xx)});
        }

        //System.out.println("SESAME:"+ss+"ms, JENA:"+yy+"ms, Virtuoso:"+oo+", SQL:"+zz);

        OpenCsvUtilities.writeCSVDataToConsole(data);



       /* sesame.setOutput(
                "C:\\Users\\tenti\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testKm4c2"
                , "ttl", true);
        sesame.executeQuerySPARQLFromString(query);
        sesame.shutDownRepository();*/


        //---------------------------------------------
        //WORK
        //---------------------------------------------
       /* sesame.setOutput(
                "C:\\Users\\tenti\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testKm4c2"
                , "csv", true);
        sesame.executeQuerySPARQLFromString(query);
        sesame.shutDownRepository();*/


        /*Sesame28Kit sesame2 = Sesame28Kit.getInstance();
        File file = new File("C:\\Users\\tenti\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testSesame.ttl");
        if(file.exists()) {
            sesame2.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");
            sesame2.importIntoRepository(file,"http://www.disit.org/km4city/schema","ttl");
        }*/
    }
}
