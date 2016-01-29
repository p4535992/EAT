package com.github.p4535992.extractor.home;

import com.github.p4535992.extractor.estrattori.ExtractInfoSpring;
import com.github.p4535992.util.file.SimpleParameters;
import com.github.p4535992.util.log.logback.LogBackUtil;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;


/**
 * The Main Class of the Module ExtractInfo.
 * You can read hte file input.properties on the rsource folder or given the absolute pato to the file
 * like arguments on the main method.
 * @author 4535992
 * @version 2015-06-29
 */
public class MainExtractInfo {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(MainExtractInfo.class);

    public MainExtractInfo(){}

    public static void main(final String[] args) throws NullPointerException, InterruptedException, InvocationTargetException{   
        try{          
            EventQueue.invokeLater(new Runnable() {	 
            //SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    LogBackUtil.setLogpatternConsole(LogBackUtil.LOGPATTERN.PATTERN_COLORED1_METHOD_NOTIME);
                    LogBackUtil.init(LogBackUtil.LOGPATTERN.PATTERN_CLASSIC_NOTIME);
                    logger.info("===== START THE PROGRAMM =========");
                    /*long start = System.currentTimeMillis();*/
                    // The storage for the command line parameters
                    //Map<String,String> mParameters = new HashMap<>();
                    // Parse all the parameters
                    SimpleParameters params;
                    if (args.length > 0) {
                       //params = FileUtilities.readFile(new File(args[0]), '=');
                        params = new SimpleParameters(new File(args[0]), '=');
                    } else{
                       //C:\Users\Marco\Documents\GitHub\EAT\ExtractInfo\src\main\resources\input.properties
                       // + "ExtractInfo" + File.separator +
                      /* params = FileUtilities.readFile(
                               new File(System.getProperty("user.dir") + File.separator +
                                       "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                                       "input.properties"), '=');*/
                        params = new SimpleParameters(new File(System.getProperty("user.dir") + File.separator +
                                "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                                "input.properties"), '=');
                    }
                    //VARIABILI ALTRE
                    //PRINT SULLA CONSOLE
                    logger.info("Using parameters:");
                    logger.info(params.toString());

                    //logger.error("The file properties is not corrected implememnted, stop the programm");
                    //System.exit(0);


                    if(params.getValue("PARAM_TYPE_EXTRACTION").equals("SPRING")) {
                       ExtractInfoSpring m = ExtractInfoSpring.getInstance(params);
                        logger.info("START EXTRACT");
                        m.Extraction();
                    }
                    //Ouput del tempo di elaborazione del progamma
                         /*System.out.println(String.format(
                               "------------ Processing took %s millis\n\n",
                         System.currentTimeMillis() - start));*/
                   }catch(Exception e){
                           e.printStackTrace();
                           logger.error(e.getMessage(),e);
                   }
               }
        });//runnable    
    }catch(OutOfMemoryError e){
            //reload the code
            logger.error("java.lang.OutOfMemoryError, Ricarica il programma modificando LIMIT e OFFSET.\n GATE execute in timeout");
    }
}//main

}