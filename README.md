#########################
###Last Update: 2015-09-15
#########################

##EAT (Extraction and Triplify)
[![Release](https://img.shields.io/github/release/p4535992/EAT.svg?label=maven)](https://jitpack.io/p4535992/EAT)

Simple Basic Start Java Programm for Extract Information from the Web with the software [GATE](https://gate.ac.uk/), 
and triplify MySQL/SQL Table to file of triple with the software [Web-Karma](http://www.isi.edu/integration/karma/)

Here the database used for the test: [backup_test_1_sql](db/SQL TEST 2016-01.zip)

Example 0: Extract company information using the single properties file, 
here a example fo the [file properties](https://github.com/p4535992/EAT/blob/master/src/main/resources/input.properties) the documentation is [here](doc/[README] Manuale Utente Elaborato -  ITA 2015-03-06.xls):
```java
public void extractByFileProperties(final File file, final char separator) {
    try {
        EventQueue.invokeLater(new Runnable() {
            //SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
                    LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
                    logger.info("===== START THE PROGRAMM =========");
                    SimpleParameters params;
                    if (file.exists()) {
                        //params = FileUtilities.readFile(new File(args[0]), '=');
                        params = new SimpleParameters(file, separator);
                    } else {
                        params = new SimpleParameters(new File(System.getProperty("user.dir") + File.separator +
                                "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                                "input.properties"), '=');
                    }
                    logger.info("Using parameters:");
                    logger.info(params.toString());
                    if (params.getValue("PARAM_TYPE_EXTRACTION").equals("SPRING")) {
                        ExtractInfoSpring m = ExtractInfoSpring.getInstance(params);
                        logger.info("START EXTRACT");
                        m.Extraction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
            }
        });//runnable
    } catch (OutOfMemoryError e) {
        //reload the code
        logger.error("java.lang.OutOfMemoryError, Ricarica il programma modificando LIMIT e OFFSET.\n GATE execute in timeout");
    }
}
```
Example 1: Extract company information using the single url check:
```java
public static void extractBySingleURL() {
        try {
            LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM =========");
            ExtractInfoSpring m = ExtractInfoSpring.getInstance();
            logger.info("START EXTRACT");
            m.extractInfoSingleURL("com.mysql.jdbc.Driver","jdbc:mysql","localhost","3306","siimobility",
                    "siimobility","geoDb","geodb","geodocument_2015_09_18","websitehtml","url","10000","0",false,false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
}    
```
Example 2: Extract company information using the multiple urls check, different form the previous example only for the 
fact we try to analyze all the url together and we populate a Corpus GATE for doing that:
```java
public static void extractByMultipleURL() {
    try {
        LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
        LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
        logger.info("===== START THE PROGRAMM =========");
        ExtractInfoSpring m = ExtractInfoSpring.getInstance();
        logger.info("START EXTRACT");
        m.extractInfoMultipleURL("com.mysql.jdbc.Driver","jdbc:mysql","localhost","3306","siimobility",
                "siimobility","geoDb","geodb","geodocument_2015_09_18","websitehtml","url","10000","0",false,false);
    } catch (Exception e) {
        e.printStackTrace();
        logger.error(e.getMessage(), e);
    }
} 
```
Example 3:Extract company information using the multiple files check, we read file instead of url
```java
public static void extractByFile() {
    try {
        LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
        LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
        logger.info("===== START THE PROGRAMM =========");
        ExtractInfoSpring m = ExtractInfoSpring.getInstance();
        logger.info("START EXTRACT");
        m.extractInfoFile("C:\\Users\\tenti\\Downloads\\parseWebUrls",
                "com.mysql.jdbc.Driver", "jdbc:mysql","localhost","3306","siimobility","siimobility","geoDb",
                "geodocument_2015_09_18","0","3",false,false);
    } catch (Exception e) {
        e.printStackTrace();
        logger.error(e.getMessage(), e);
    }
}
```
Example 4: Convert a Table SQL to a Specific Format and convert in a File of Triple (rdf,turtle,ecc.)
```java
public static void extractTripleFromDatabase() {
     try {
         LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
         LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
         logger.info("===== START THE PROGRAMM =========");
         logger.info("START TRIPLIFY");
         File r2rml = new File("" +
                 "C:\\Users\\tenti\\Desktop\\R2RML_infodocument-model_2015-07-08.ttl");

         File output = new File(FileUtilities.getDirectoryFile(r2rml)+File.separator+"output.n3");
         ExtractInfoSpring m = ExtractInfoSpring.getInstance();
         m.convertTableToTriple(
                 "com.mysql.jdbc.Driver","jdbc:mysql","localhost","3306","siimobility",
                 "siimobility","geodb","geodocument_2015_09_18","infodocument_2015_09_18",
                 RDFFormat.TURTLE.getName().toLowerCase(),
                 r2rml.getAbsolutePath(),output.getAbsolutePath(),true,true);
     } catch (Exception e) {
         e.printStackTrace();
         logger.error(e.getMessage(), e);
     }
 }
```
Example 5: Import file of triple (rdf,turtle,..) in a Sesame repsitory
```java
 public static void importFileToSesameRepository(){
        try {
            LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM =========");
            logger.info("START TRIPLIFY");
            File output = new File("" +
                    "C:\\Users\\tenti\\Desktop\\output-c.Turtle");
            Sesame2Utilities sesame = Sesame2Utilities.getInstance();
            Repository rep = sesame.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");
            sesame.setPrefixes();

            sesame.importIntoRepository(output,rep);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }
```
Example 6: Matching triple beetween Sesame repositories with SILK
```java
  public static void linkingWithSILK(){
        try {
            LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM =========");
            logger.info("START TRIPLIFY");
            File xmlConfig = new File("" +
                    "C:\\Users\\tenti\\Desktop\\output-c.xml");
            SilkUtilities.getInstance().generateRDF(xmlConfig,"interlink_id",2,false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }
```

######Is Based on these maven module:

+ [ExtractInfo](https://github.com/p4535992/ExtractInfo)
+ [gate-basivc](https://github.com/p4535992/gate-basic)
+ [utility](https://github.com/p4535992/utility)

######Major API i used for this project:
+ Integration with [Hibernate 4](http://hibernate.org/) (almost done just finish the last test)
+ Integration with [Spring Framework](https://projects.spring.io/spring-framework/)
+ Integration with [Jena API](https://jena.apache.org/)
+ Integration with [Web-Karma API](http://usc-isi-i2.github.io/karma/)
+ Integration with [GATE Embedded API](https://gate.ac.uk/)
+ Integration with [SILK API (SIngle Machine)](https://www.assembla.com/spaces/silk/wiki/Silk_Single_Machine)
+ Integration with [Sesame OpenrRdf API](http://rdf4j.org/) 
+ Integration [Spring MVC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html) (in a private project for more info you can contact me)
+ Integration [JSOUP](http://jsoup.org/)





