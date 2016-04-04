package com.github.p4535992.main.home;

import java.io.File;
import com.github.p4535992.extractor.estrattori.ExtractInfoSpring;
import com.github.p4535992.extractor.estrattori.silk.SilkUtilities;
import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.file.SimpleParameters;
import com.github.p4535992.util.log.logback.LogBackUtil;
import com.github.p4535992.util.repositoryRDF.sesame.Sesame2Utilities;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;


@SpringBootApplication
@ComponentScan("com.github.p4535992.main")
@PropertySource("classpath:application.properties")
public class Application{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Application.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer props = new PropertySourcesPlaceholderConfigurer();
        props.setLocations(new ClassPathResource("application.properties"));
        return props;
    }


    public static void main(String[] args) {
        //JUST FOR TEST
        /*ApplicationContext ctx = SpringApplication.run(Application.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/
        SpringApplication.run(Application.class, args);

        //GO FOR THE PROJECT
        try {
            //EventQueue.invokeLater(new Runnable() {
            extractByFileProperties();
        } catch (OutOfMemoryError e) {
            //reload the code
            logger.error("java.lang.OutOfMemoryError, Ricarica il programma modificando LIMIT e OFFSET.\n GATE execute in timeout");
        }
    }

    public void extractBySingleURL() {
        try {
            //LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM EXTRACT BY SINGLE URL =========");
            ExtractInfoSpring m = ExtractInfoSpring.getInstance();
            logger.info("START EXTRACT");
            m.extractInfoSingleURL("com.mysql.jdbc.Driver", "jdbc:mysql", "localhost", "3306", "siimobility",
                    "siimobility", "geodb", "geodb", "geodocument_2015_09_18", "websitehtml", "url", "60000", "3", false, false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public void extractByMultipleURL() {
        try {
            //LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM EXTRACT BY MULTIPLE URL =========");
            ExtractInfoSpring m = ExtractInfoSpring.getInstance();
            logger.info("START EXTRACT");
            m.extractInfoMultipleURL("com.mysql.jdbc.Driver", "jdbc:mysql", "localhost", "3306", "siimobility",
                    "siimobility", "geodb", "geodb", "geodocument_2015_09_18", "websitehtml", "url", "60003", "3", false, false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public void extractByFile() {
        try {
            //LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM EXTRACT BY FILE =========");
            ExtractInfoSpring m = ExtractInfoSpring.getInstance();
            logger.info("START EXTRACT");
            m.extractInfoFile("C:\\Users\\tenti\\Downloads\\parseWebUrls",
                    "com.mysql.jdbc.Driver", "jdbc:mysql", "localhost", "3306", "siimobility", "siimobility", "geoDb",
                    "geodocument_2015_09_18", "0", "3", false, false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public void extractTripleFromDatabase() {
        try {
            //LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM EXTRACTION TRIPLE FROM DATABASE =========");
            logger.info("START TRIPLIFY");
            File r2rml = new File("" +
                    "C:\\Users\\tenti\\Desktop\\R2RML_infodocument-model_2015-07-08.ttl");

            File output = new File(FileUtilities.getDirectoryFile(r2rml) + File.separator + "output.n3");
            ExtractInfoSpring m = ExtractInfoSpring.getInstance();
            m.convertTableToTriple(
                    "com.mysql.jdbc.Driver", "jdbc:mysql", "localhost", "3306", "siimobility",
                    "siimobility", "geodb", "geodocument_2015_09_18", "infodocument_2015_09_18",
                    RDFFormat.TURTLE.getName().toLowerCase(),
                    r2rml.getAbsolutePath(), output.getAbsolutePath(), true, true);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public void importFileToSesameRepository() {
        try {
            //LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM IMPORT FILE WITH SESAME =========");
            logger.info("START TRIPLIFY");
            File output = new File("" +
                    "C:\\Users\\tenti\\Desktop\\output-c.Turtle");
            Sesame2Utilities sesame = Sesame2Utilities.getInstance();
            Repository rep = sesame.connectToHTTPRepository("http://localhost:8080/openrdf-sesame/repositories/repKm4c1");
            sesame.setPrefixes();

            sesame.importIntoRepository(output, rep);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public void linkingWithSILK() {
        try {
            //LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            logger.info("===== START THE PROGRAMM LINKING WITH SILK =========");
            logger.info("START TRIPLIFY");
            File xmlConfig = new File("" +
                    "C:\\Users\\tenti\\Desktop\\output-c.xml");
            SilkUtilities.getInstance().generateRDF(xmlConfig, "interlink_id", 2, false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public void extractByFileProperties(final File file, final char separator) {
        try {
            LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            LogBackUtil.console();
            logger.info("===== START THE PROGRAMM EXTRACTION WITH FILE PROPERTIES =========");
            SimpleParameters params;
            if (file != null && file.exists()) {
                //params = FileUtilities.readFile(new File(args[0]), '=');
                params = new SimpleParameters(file, separator);
            } else {
                //C:\Users\tenti\Desktop\EAT\src\main\resources\input.properties
                //C:\Users\tenti\Desktop\EAT\ExtractInfo\src\main\resources\input.properties
                params = new SimpleParameters(
                        new File(System.getProperty("user.dir") + File.separator +
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

    public static void extractByFileProperties() {
        try {
            LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
            //LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
            LogBackUtil.console();
            logger.info("===== START THE PROGRAMM EXTRACTION WITH FILE PROPERTIES =========");
            logger.info("Using parameters:");
            //logger.info(env.toString());
            //if (env.getProperty("PARAM_TYPE_EXTRACTION").equals("SPRING")) {
                ExtractInfoSpring m = ExtractInfoSpring.getInstance();
                logger.info("START EXTRACT");
                m.Extraction();
            //}
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

    }

}
