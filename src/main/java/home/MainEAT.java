package home;

import org.xml.sax.SAXException;
import com.p4535992.util.file.FileUtil;
import com.p4535992.util.log.SystemLog;
import com.p4535992.util.file.SimpleParameters;
import extractor.estrattori.ExtractInfoSpring;
import extractor.gate.GateDataStoreKit;

import java.awt.EventQueue;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tenti Marco
 */
public class MainEAT {


    //ALTRE VARIABILI
    private static Integer LIMIT;
    private static Integer OFFSET;
    private static GateDataStoreKit datastore;
//////////////////////////////////////////////////////////////
    public MainEAT(){}
    // The storage for the command line parameters
    private static Map<String,String> mParameters = new HashMap<String, String>();
    public MainEAT(Map<String, String> mParameters){
            this.mParameters = mParameters;
    }
    
    public static void main(final String[] args) throws NullPointerException, InterruptedException, InvocationTargetException{   
        try{          
            EventQueue.invokeLater(new Runnable() {	 
            //SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                   try{
                    SystemLog LOG = new SystemLog();
                    SystemLog.message("===== START THE PROGRAMM =========");
                    /*long start = System.currentTimeMillis();*/                  
                    // Parse all the parameters
                    SimpleParameters params = new SimpleParameters();
                    if(args.length > 0){
                        params = new SimpleParameters(args);
                        SystemLog.message("Using parameters:");
                        SystemLog.message(params.toString());
                        //C:\Users\Marco\Documents\GitHub\EAT\src\main\resources\input.properties
                    }else{
                        //C:\Users\Marco\Documents\GitHub\EAT\ExtractInfo\src\main\resources\input.properties
                        mParameters = FileUtil.readStringFromFileLineByLine(
                                System.getProperty("user.dir") + File.separator +
                                // + "ExtractInfo" + File.separator +
                                        "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                                        "input.properties", '=', params);
                        //VARIABILI ALTRE
                        //PRINT SULLA CONSOLE
                        SystemLog.message("Using parameters:");
                        SystemLog.message(params.toString());

                        String test = params.getValue("PARAM_PATH_FILE_CFG_DB_INPUT_KEYWORD");
//                        if ( params.getValue("PARAM_TYPE_EXTRACTION").equals("MYSQL")) {
//                            SystemLog.message("Selezionato Estrazione MySQL", "OUT");
//                            SystemLog.message("Caricamento costruttore...");
//                            ExtractInfoMySQL m = new ExtractInfoMySQL(params);
//                            SystemLog.message("... construttore pronto.");
//                            m.Extraction();
//                        }else
                       if(params.getValue("PARAM_TYPE_EXTRACTION").equals("SPRING")){
                            ExtractInfoSpring m = new ExtractInfoSpring(params);
                            SystemLog.message("START EXTRACT");
                            m.Extraction();
                       }
                    }
                    //Ouput del tempo di elaborazione del progamma
                         /*System.out.println(String.format(
                               "------------ Processing took %s millis\n\n",
                         System.currentTimeMillis() - start));*/
                   }catch(InterruptedException ie){
                           ie.printStackTrace();
                           SystemLog.error("ERROR:" + ie.getMessage());
                           Logger.getLogger(MainExtractInfo.class.getName()).log(Level.SEVERE, null, ie);
                   }catch(InvocationTargetException iie){
                        iie.printStackTrace();
                        SystemLog.error("ERROR:" + iie.getMessage());
                        Logger.getLogger(MainExtractInfo.class.getName()).log(Level.SEVERE, null, iie);
                   } catch (IOException e) {
                       e.printStackTrace();
                   } catch (SAXException e) {
                       e.printStackTrace();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
        });//runnable    
    }catch(java.lang.OutOfMemoryError e){
        //ricarica il programma 
        SystemLog.error("java.lang.OutOfMemoryError, Ricarica il programma modificando LIMIT e OFFSET.\n GATE execute in timeout");
    }
}//main	  

      /**
       * GET the value of LIMIT
       * @return value
       */
       public Integer getLIMIT() {
            return LIMIT;
        }
        /**
         * GET the value of OFFSET
         * @return value
         */
        public Integer getOFFSET() {
            return OFFSET;
        }

        public void printOutputConsoleToFile() throws FileNotFoundException{
            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
        }

}