package home;

import org.xml.sax.SAXException;
import util.FileUtil;
import util.SystemLog;
import util.cmd.SimpleParameters;
import util.estrattori.ExtractInfoMySQL;
import util.estrattori.ExtractInfoSpring;
import util.gate.DataStoreApplication;

import javax.xml.transform.TransformerException;
import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private static String PARAM_LOG_FILE = "LOG_"+GetTimeAndDate();
    private static String PARAM_LOG_SUFFIX = "txt";
    private static Integer LIMIT;
    private static Integer OFFSET;
    private static DataStoreApplication datastore;
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
                    SystemLog LOG = new SystemLog(PARAM_LOG_FILE,PARAM_LOG_SUFFIX);
                    SystemLog.write("===== START THE PROGRAMM =========", "OUT");
                    /*long start = System.currentTimeMillis();*/                  
                    // Parse all the parameters
                    SimpleParameters params = new SimpleParameters();
                    if(args.length > 0){
                        params = new SimpleParameters(args);
                        SystemLog.write("Using parameters:", "OUT");
                        SystemLog.write(params.toString(), "OUT");
                    }else{
                        //C:\Users\Marco\Documents\GitHub\EAT\ExtractInfo\src\main\resources\input.properties
                        mParameters = FileUtil.ReadStringFromFileLineByLine(System.getProperty("user.dir")+"//ExtractInfo//src//main//resources//input.properties",'=',params);
                        //VARIABILI ALTRE
                        params.setDefaultValue("PARAM_LOG_FILE", PARAM_LOG_FILE + PARAM_LOG_SUFFIX);
                        //params.setDefaultValue(PARAM_LOG_SUFFIX,".txt");
                        //PRINT SULLA CONSOLE
                        SystemLog.write("Using parameters:", "OUT");
                        SystemLog.write(params.toString(), "OUT");

                        String test = params.getValue("PARAM_PATH_FILE_CFG_DB_INPUT_KEYWORD");
//                        try {
                            if ( params.getValue("PARAM_TYPE_EXTRACTION").equals("MYSQL")) {
                                SystemLog.write("Selezionato Estrazione MySQL", "OUT");
                                SystemLog.write("Caricamento costruttore...", "OUT");
                                ExtractInfoMySQL m = new ExtractInfoMySQL(params);
                                SystemLog.write("... construttore pronto.", "OUT");
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
                    SystemLog.write(ie.getMessage(), "ERROR");
                    Logger.getLogger(MainExtractInfo.class.getName()).log(Level.SEVERE, null, ie);
                    //log(Logger.getLogger(home.MainExtractInfo.class.getName()).log(Level.SEVERE, null, ie));
                }catch(InvocationTargetException iie){
                    iie.printStackTrace();
                    SystemLog.write(iie.getMessage(), "ERROR");
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
        SystemLog.write("java.lang.OutOfMemoryError, Ricarica il programma modificando LIMIT e OFFSET.\n GATE execute in timeout", "ERROR");
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

        /**
         * Meto che scriva data e ora come un'unico numero yyyyMMdd_HHmmss
         * @return formato stringa della data e ora attuali
         */
        public static String GetTimeAndDate(){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            return timeStamp;
        }  
        
        public void printOutputConsoleToFile() throws FileNotFoundException{
            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
        }

}