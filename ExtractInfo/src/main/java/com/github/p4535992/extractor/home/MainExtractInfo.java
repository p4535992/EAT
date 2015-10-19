package com.github.p4535992.extractor.home;

import com.github.p4535992.extractor.estrattori.ExtractInfoSpring;
import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.file.SimpleParameters;
import com.github.p4535992.util.log.SystemLog;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Main Class of the Module ExtractInfo.
 * You can read hte file input.properties on the rsource folder or given the absolute pato to the file
 * like arguments on the main method.
 * @author 4535992
 * @version 2015-06-29
 */
public class MainExtractInfo {

    public MainExtractInfo(){}

    public static void main(final String[] args) throws NullPointerException, InterruptedException, InvocationTargetException{   
        try{          
            EventQueue.invokeLater(new Runnable() {	 
            //SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                   try {
                       SystemLog.setIsPRINT(false);
                       SystemLog.setIsLogOff(true);
                       SystemLog.message("===== START THE PROGRAMM =========");
                        /*long start = System.currentTimeMillis();*/
                       // The storage for the command line parameters
                       Map<String,String> mParameters = new HashMap<>();
                       // Parse all the parameters
                       SimpleParameters params = new SimpleParameters();
                       if (args.length > 0) {
                           mParameters = FileUtil.readStringFromFileLineByLine(args[0], '=', params);
                       } else{
                           //C:\Users\Marco\Documents\GitHub\EAT\ExtractInfo\src\main\resources\input.properties
                           // + "ExtractInfo" + File.separator +
                           mParameters = FileUtil.readStringFromFileLineByLine(
                                   System.getProperty("user.dir") + File.separator +
                                           "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                                           "input.properties", '=', params);
                        }
                        //VARIABILI ALTRE
                        //PRINT SULLA CONSOLE
                        SystemLog.message("Using parameters:");
                        SystemLog.message(params.toString());

                       if(params.getValue("PARAM_TYPE_EXTRACTION").equals("SPRING")){
                            ExtractInfoSpring m = ExtractInfoSpring.getInstance(params);
                            SystemLog.message("START EXTRACT");
                            m.Extraction();
                       }

                    //Ouput del tempo di elaborazione del progamma
                         /*System.out.println(String.format(
                               "------------ Processing took %s millis\n\n",
                         System.currentTimeMillis() - start));*/
                   }catch(Exception e){
                           SystemLog.error(e.getMessage());
                   }
               }
        });//runnable    
    }catch(OutOfMemoryError e){
        //reload the code
          e.printStackTrace();
          SystemLog.error("java.lang.OutOfMemoryError, Ricarica il programma modificando LIMIT e OFFSET.\n GATE execute in timeout");
    }
}//main

}