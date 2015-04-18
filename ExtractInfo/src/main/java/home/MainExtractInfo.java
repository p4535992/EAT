package home;

import org.xml.sax.SAXException;
import util.FileUtil;
import util.SystemLog;
import util.cmd.SimpleParameters;
import util.estrattori.ExtractInfoMySQL;
import util.estrattori.ExtractInfoSpring;
import util.gate.GateDataStoreKit;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tenti Marco
 */
public class MainExtractInfo {


    //ALTRE VARIABILI
    private static Integer LIMIT;
    private static Integer OFFSET;
    private static GateDataStoreKit datastore;
//////////////////////////////////////////////////////////////
    public MainExtractInfo(){}
    // The storage for the command line parameters
    private static Map<String,String> mParameters = new HashMap<String, String>();
    public MainExtractInfo(Map<String, String> mParameters){
            this.mParameters = mParameters;
    }
    
    public static void main(final String[] args) throws NullPointerException, InterruptedException, InvocationTargetException{   
        try{          
            EventQueue.invokeLater(new Runnable() {	 
            //SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                   try{
                    SystemLog LOG = new SystemLog();
                    SystemLog.message("===== START THE PROGRAMM =========", "OUT");
                    /*long start = System.currentTimeMillis();*/                  
                    // Parse all the parameters
                    SimpleParameters params = new SimpleParameters();
                    if(args.length > 0){
                        params = new SimpleParameters(args);
                        SystemLog.message("Using parameters:", "OUT");
                        SystemLog.message(params.toString(), "OUT");
                    }else{
                        //C:\Users\Marco\Documents\GitHub\EAT\ExtractInfo\src\main\resources\input.properties
                        mParameters = FileUtil.ReadStringFromFileLineByLine(System.getProperty("user.dir")+"//ExtractInfo//src//main//resources//input.properties",'=',params);
                        //VARIABILI ALTRE
                        //PRINT SULLA CONSOLE
                        SystemLog.message("Using parameters:", "OUT");
                        SystemLog.message(params.toString(), "OUT");

                        String test = params.getValue("PARAM_PATH_FILE_CFG_DB_INPUT_KEYWORD");
//                        try {
                            if ( params.getValue("PARAM_TYPE_EXTRACTION").equals("MYSQL")) {
                                SystemLog.message("Selezionato Estrazione MySQL", "OUT");
                                SystemLog.message("Caricamento costruttore...");
                                ExtractInfoMySQL m = new ExtractInfoMySQL(params);
                                SystemLog.message("... construttore pronto.");
                                m.Extraction();
                            }else if(params.getValue("PARAM_TYPE_EXTRACTION").equals("SPRING")){
                                ExtractInfoSpring m = new ExtractInfoSpring(params);
                                m.Extraction();
                            }
                            /*} else if (params.getValue("PARAM_TYPE_EXTRACTION").equals("HIBERNATE_POJO")) {
                                SystemLog.write("Selezionato Estrazione Hibernate POJO", "OUT");
                                SystemLog.write("Caricamento costruttore...", "OUT");
                                ExtractInfoHibernatePOJO m = new ExtractInfoHibernatePOJO(params);
                                SystemLog.write("... construttore pronto.", "OUT");
                                m.Extraction();
                            } else if (params.getValue("PARAM_TYPE_EXTRACTION").equals("HIBERNATE_JIRA")) {
                                SystemLog.write("Selezionato Estrazione Hibernate JIRA", "OUT");
                                SystemLog.write("Caricamento costruttore...", "OUT");
                                ExtractInfoHibernateJIRA m = new ExtractInfoHibernateJIRA(params);
                                SystemLog.write("... construttore pronto.", "OUT");
                                m.Extraction();
                            }*/
//                        }catch(Exception e){
//
//                        }
                    }
                    //Ouput del tempo di elaborazione del progamma
                         /*System.out.println(String.format(
                               "------------ Processing took %s millis\n\n",
                         System.currentTimeMillis() - start));*/   
                }catch(InterruptedException ie){
                    ie.printStackTrace();
                       SystemLog.error("ERROR:" + ie.getMessage());
                    Logger.getLogger(MainExtractInfo.class.getName()).log(Level.SEVERE, null, ie);
                    //log(Logger.getLogger(home.MainExtractInfo.class.getName()).log(Level.SEVERE, null, ie));
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
        /*
        if(tentativiOutMemory ==2){OFFSET = OFFSET + i + 1;System.exit(0);}
        else{ 
            OFFSET = OFFSET + i;
            //LIMIT = LIMIT - OFFSET;
            Integer numero = Math.abs(LIMIT - OFFSET);
            System.err.println("NUOVO OFFSET:"+OFFSET+",NUOVO LIMIT:"+LIMIT);
            tentativiOutMemory++;
            if(numero < RANGE){mei.main(args);}
            else{System.exit(0);}           
        }   
        */
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