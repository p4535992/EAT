package com.github.p4535992.extractor.home;


import com.github.p4535992.util.database.sql.SQLUtilities;

import com.github.p4535992.util.database.sql.query.MySQLQuery;
import com.github.p4535992.util.file.csv.opencsv.OpenCsvUtilities;
import com.github.p4535992.util.log.logback.LogBackUtil;
import com.github.p4535992.util.repositoryRDF.jena.Jena3Utilities;
import com.github.p4535992.util.repositoryRDF.jenaAndSesame.Jena3SesameUtilities;
import com.github.p4535992.util.repositoryRDF.sesame.Sesame2Utilities;
import com.github.p4535992.util.repositoryRDF.sparql.SparqlUtilities;

import org.apache.jena.rdf.model.Model;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 06/10/2015.
 * @author 4535992.
 * @version 2015-10-06.
 */
public class MainSesameManager {

    //OK
    private static String SPARQL_SELECT_ALL_KM4cSERVCIE  = "CONSTRUCT {?service ?p ?o.} "
            + "WHERE {?service a <http://www.disit.org/km4city/schema#Service>;"
            + "       ?p ?o . } LIMIT 600000 OFFSET 0 ";
    //OK
    private static String SPARQL_SELECT_KM4C_SERVICE  = "SELECT ?service ?p ?o "
            + "WHERE {?service a <http://www.disit.org/km4city/schema#Service>; "
            + "       ?p ?o . } LIMIT 600 OFFSET 0 ";
    //OK
    private static String SQL_GET_ALL_SERVICE = "SELECT * FROM geodb.websitehtml ";
    //OK
    private static String SPARQL_SELECT_Q1 ="SELECT ?location ?lat ?long \n" +
            "WHERE {?location  a <http://purl.org/goodrelations/v1#Location> ;" +
            "<http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;  \n" +
            "<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long. \n" +
            "FILTER(!isBlank(?lat) && !isLiteral(?lat) && !isBlank(?long) && !isLiteral(?long)) \n" +
            "}";
    //OK
    private static String SQL_SELECT_Q1 ="SELECT city,latitude,longitude " +
            "FROM geodb.infodocument_2015_09_18 " +
            "WHERE city IS NOT NULL AND TRIM(city) <> '' AND concat('',longitude * 1) = longitude " +
            "AND concat('',latitude *1) = latitude;";

    //OK
    private static String SPARQL_SELECT_Q2 ="SELECT ?indirizzo "
            +"WHERE{ ?business a <http://purl.org/goodrelations/v1#BusinessEntity> ; "
            +" <http://schema.org/streetAddress> ?indirizzo. "
            +"SERVICE <http://localhost:8080/openrdfworkbench/repositories/repkm4c/query>{ "
            +"                 ?service a <http://www.disit.org/km4city/schema#Service>. } "
            +"FILTER EXISTS {?business  <http://www.w3.org/2002/07/owl#sameAs>  ?service}}";

    //OK
    private static String SQL_SELECT_Q2 ="SELECT geodb.infodocument_2015_09_18.indirizzo\n" +
            "FROM geodb.infodocument_2015_09_18,federated_table_1\n" +
            "WHERE  geodb.infodocument_2015_09_18.city IS NOT NULL AND TRIM(geodb.infodocument_2015_09_18.city) <> ''\n" +
            "       AND concat('',geodb.infodocument_2015_09_18.longitude * 1) = geodb.infodocument_2015_09_18.longitude\n" +
            "AND concat('',geodb.infodocument_2015_09_18.latitude *1) = geodb.infodocument_2015_09_18.latitude\n" +
            "       AND geodb.infodocument_2015_09_18.city = federated_table_1.city ;";

    private static String SPARQL_SELECT_Q3 = "SELECT ?tel ?fax " +
            "WHERE{  ?business a <http://purl.org/goodrelations/v1#BusinessEntity> ; " +
            "                    <http://schema.org/telephone> ?tel; " +
            "                    <http://schema.org/fax> ?fax. " +
            "FILTER(str(?tel) = str(?fax))" +
            "}";

    private static String SQL_SELECT_Q3 ="SELECT telefono,fax FROM geodb.infodocument_2015_09_18\n" +
            "WHERE telefono = fax AND\n" +
            "      city IS NOT NULL AND\n" +
            "      TRIM(city) <> '' AND\n" +
            "      concat('',longitude * 1) = longitude AND\n" +
            "      concat('',latitude *1) = latitude;";

    private static String SPARQL_SELECT_Q4 ="SELECT ?tel ?fax " +
            "WHERE{ ?business a <http://purl.org/goodrelations/v1#BusinessEntity> ; " +
            "                   <http://schema.org/telephone> ?tel;  " +
            "                   <http://schema.org/fax> ?fax. " +
            "FILTER(!isBlank(?lat) && !isLiteral(?lat) && !isBlank(?long) && !isLiteral(?long) && (str(?tel) = str(?fax)))" +
            "}";

    private static String SQL_SELECT_Q4 ="SELECT telefono,fax FROM geodb.infodocument_2015_09_18\n" +
            "WHERE telefono = fax AND\n" +
            "      city IS NOT NULL AND\n" +
            "      TRIM(city) <> '' AND\n" +
            "      concat('',longitude * 1) = longitude AND\n" +
            "      concat('',latitude *1) = latitude;";

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


    public static void main(String args[]) throws RepositoryException, MalformedQueryException, QueryEvaluationException, IOException, UpdateExecutionException, RepositoryConfigException, InterruptedException {
        LogBackUtil.console();
        initSparqlQueries();
        initSqlQueries();


        Connection conn = SQLUtilities.getMySqlConnection(
                "jdbc:mysql://localhost:3306/geodb?noDatetimeStringSync=true&user=siimobility&password=siimobility");

        Sesame2Utilities sesame = Sesame2Utilities.getInstance();
        //sesame.setURLRepositoryId("km4city04");
        //WORK
        Repository rep = sesame.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");
        sesame.setPrefixes();

        //sesame.setPrefixes();

        //NOT WORK WITH OWLIM
        /*RepositoryManager manager = sesame.connectToLocation(
                "C:\\Users\\tenti\\AppData\\Roaming\\Aduna\\OpenRDF Sesame\\repositories");
        //Repository rep = manager.getRepository("repKm4c1");*/

        //String query = (SparqlUtilities.preparePrefix()+SPARQL_SELECT_KM4C_SERVICE).trim();
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
        //data.add(new String[]{"SESAME", "JENA", "SQL","MYSQL"});
        //WORK
        //2669,160,44,185
        String query ;
        String sparql ;
        org.openrdf.model.Model sModel = sesame.toModel(rep);
        Jena3SesameUtilities jas = Jena3SesameUtilities.getInstance();
        Jena3Utilities j3u = Jena3Utilities.getInstance();
        Model jModel2 = jas.asJenaModel(sModel);
        //int count = sparqlQueries.size();
        int count = 10;
        int j = 0;
        for(int i = 0; i < count; i++) {
            Thread.sleep(3000);
            //query = sparqlQueries.get(i);
            //query = (SparqlUtilities.preparePrefixNoPoint()+SPARQL_SELECT_KM4C_SERVICE).trim();

            sparql = (SparqlUtilities.preparePrefixNoPoint()+SPARQL_SELECT_Q4).trim();
            Long ss = sesame.getExecutionQueryTime(sparql);

            Long yy = Jena3Utilities.getExecutionQueryTime(sparql, jModel2);

            //query = sqlQueries.get(i);
            query = SQL_SELECT_Q4;
            Long zz = SQLUtilities.getExecutionTime(query, conn);

            Long xx = MySQLQuery.getExecutionTime(query,conn);

            data.add(new String[]{String.valueOf(ss), String.valueOf(yy), String.valueOf(zz),String.valueOf(xx)});
            j++;
          /*  if(j >= 20){
                OpenCsvUtilities.writeCSVDataToFile(data,';',
                        new File(
                                "C:\\Users\\tenti\\Desktop\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testCSV1.csv"));
                data.clear();
                j = 0;
            }*/
        }
        //System.out.println("SESAME:"+ss+"ms, JENA:"+yy+"ms, Virtuoso:"+oo+", SQL:"+zz);
        OpenCsvUtilities.writeCSVDataToConsole(data);


      /*  OpenCsvUtilities.writeCSVDataToFile(data,';',
                new File(
                        "C:\\Users\\tenti\\Desktop\\EAT\\ExtractInfo\\src\\main\\java\\com\\github\\p4535992\\extractor\\home\\testCSV1.csv"));



        */


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
