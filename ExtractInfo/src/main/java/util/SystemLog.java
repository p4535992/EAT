/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import org.slf4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;


/**
 * 2015-04-11
 * Class for print a log file
 * @author 4535992
 */
public class SystemLog {

    private static String LOGNAME;
    private static File LOGFILE;
    public static int VERBOSE = 1;
    /** {@code org.slf4j.Logger} */
    private static org.slf4j.Logger logger;
    /** Flag to provide basic support for debug information (not used within class). */
    public static boolean DEBUG = false;
    /** {@code DateFormat} instance for formatting log entries. */
    private static SimpleDateFormat logTimestamp = new SimpleDateFormat("[HH:mm:ss]");
    private static SimpleDateFormat logTimesAndDateStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");


    /** Default {@code DateFormat} instance, used when custom one not set. */
    private static DateFormat df;
    /** {@code PrintWriter} instance used for logging. */
    private static PrintWriter logWriter;
    /** Flag determining whether log entries are written to the log stream. */
    private static boolean logging = false;
    /** Flag determining whether the {@code LogWriter} is closed when {@link #close()} method is called. */
    private static boolean closeWriterOnExit = false;
    /** Separator string (between date and log message). */
    private static String separator = ": ";

    public SystemLog(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        this.LOGNAME = "LOG_"+timeStamp+".txt";
        this.logging = true;
        this.LOGFILE = new File(System.getProperty("user.dir")+"\\"+ LOGNAME);
        setLogWriter();
    }

    public SystemLog(String LOGNAME, String SUFFIX){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        this.LOGNAME = LOGNAME +"_"+timeStamp+"."+ SUFFIX;
        this.logging = true;
        this.LOGFILE = new File(System.getProperty("user.dir")+"\\"+ LOGNAME);
        setLogWriter();
    }

    public SystemLog(String LOGNAME, String SUFFIX,String PATHFILE){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        this.LOGNAME = LOGNAME +"_"+timeStamp+"."+ SUFFIX;
        this.logging = true;
        this.LOGFILE = new File(PATHFILE+"\\"+ LOGNAME);
        setLogWriter();
    }

    ///////////////////////////
    //WRTING AND READING METHOD
    ///////////////////////////
    /**
     * Method to Write the content of a message type for the log
     * @param message
     * @param err
     */
    public static void ticket(String message,String err) {
        String log;
        if(err.contains("OUT")){
            log =logTimestamp.format(new Date()) + message;
            System.out.println(log);
        }else if(err.contains("ERR")){
            log =logTimestamp.format(new Date()) + "[ERROR]" + message;
            System.err.println(log);
        }else if(err.contains("WAR")){
            log =logTimestamp.format(new Date()) + "[WARNING]" + message;
            System.err.println(log);
        }else if(err.contains("EXIT")){
            log =logTimestamp.format(new Date()) + "[EXIT 1]" + message;
            System.out.println(log);
            System.exit(1);
        }else if(err.contains("AVOID")){
            log =logTimestamp.format(new Date()) + message;
            //System.err.println(log);
        }else if(err.contains("ERR+AVOID")){
            log =logTimestamp.format(new Date()) + "[ERROR]" + message;
        }else{
            log =logTimestamp.format(new Date()) + message;
            System.out.println(log);
        }
        printString2File(message);

    }

    public static void ticket(String message) {
        printString2File(message);
    }


    /**
     * Method for print the strin to a specific file
     * @param content
     */
    private static void printString2File(String content) {
        if(VERBOSE >= 10) {System.out.println(content);}
        else if(VERBOSE == 0) {}
        else {System.out.println(content);}
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)))) {
            out.print(content+System.getProperty("line.separator"));
            out.flush();
            out.close();
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    /**
     * Method to print a string to a file
     * @param content
     */
    private static void print2File(String content) {
        if(VERBOSE >= 10) {System.err.println(content);}
        else if(VERBOSE == 0){/*avoid*/}
        else {System.out.println(content);}
        logWriter.print(content + System.getProperty("line.separator"));
        logWriter.flush();
        logWriter.close();
    }

    /**
     * Method to Read the content of a message type for the lo
     * @param message
     * @param err
     */
    public static void read(String message,String err) {
         String log;
        if(err.contains("OUT")){
            log =logTimestamp.format(new Date()) + message;
            System.out.println(log);
        }else if(err.contains("ERR")){
            log =logTimestamp.format(new Date()) + "[ERROR]" + message;
            System.err.println(log);
        }else if(err.contains("WAR")){
            log =logTimestamp.format(new Date()) + "[WARNING]" + message;
            System.err.println(log);
        }else if(err.contains("EXIT")){
            log =logTimestamp.format(new Date()) + "[EXIT 1]" + message;
            System.out.println(log);
            System.exit(1);
        }else if(err.contains("AVOID")){
            log =logTimestamp.format(new Date()) + message;
            //System.err.println(log);
        }else if(err.contains("ERR+AVOID")){
            log =logTimestamp.format(new Date()) + "[ERROR]" + message;
        }else{
            log =logTimestamp.format(new Date()) + message;
            System.out.println(log);
        }
    }

    /**
     * Writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    protected static void write(String logEntry) {
        if (!logging)
            return;
        StringBuilder sb = new StringBuilder();
        if (logTimestamp != null)
            //logTimestamp.format(new Date())
            sb.append(logTimestamp.format(new Date()));
        else
        {
            if (df == null)
                df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
            sb.append(df.format(logTimestamp));
        }
        if (separator != null)
            sb.append(separator);

        sb.append(logEntry);
        //logWriter.println(sb.toString()); //???
        print2File(sb.toString());
    }


    ///////////////
    //MESSAGE
    ///////////////

    public static void message(int level, String logEntry) {VERBOSE = level; write(logEntry);}

    /**
     * Writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    public static void message(String logEntry) {write(logEntry);}
    public static  void error(String logEntry) {VERBOSE = 10; write(logEntry);}

    /**
     * Writes a message to the log with an optional prefix.
     * @param prefix prefix string for the log entry
     * @param logEntry message to write as a log entry
     */
    public static void message(String prefix, String logEntry)
    {
        if (prefix == null || prefix.equals(""))
            write(logEntry);
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix);
            sb.append(logEntry);
            write(sb.toString());
        }
    }

    /**
     * Writes a message with a {@code Throwable} to the log file.
     * @param prefix prefix string for the log entry
     * @param logEntry message to write as a log entry
     * @param throwable {@code Throwable} instance to log with this entry
     */
    public static void message(String prefix, String logEntry, Throwable throwable)
    {
        if (!logging)
            return;
        if (prefix == null || prefix.equals(""))
            message(logEntry, throwable);
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix);
            sb.append(logEntry);
            message(sb.toString(), throwable);
        }
    }

    /**
     * Writes a message with a {@code Throwable} to the log file.
     * @param throwable {@code Throwable} instance to log with this entry
     * @param logEntry message to write as a log entry
     */
    public static void message(String logEntry, Throwable throwable)
    {
        if (!logging)
            return;
        write(logEntry);
        if (throwable != null)
        {
            throwable.printStackTrace(logWriter);
            logWriter.flush();
        }
    }

    /**
     * Writes a {@code Throwable} to the log file.
     * @param throwable {@code Throwable} instance to log with this entry
     */
    public static void message(Throwable throwable) {message(throwable.getMessage(), throwable);}

    ///////////////////////////
    //DEBUG
    ///////////////////////////

    /**
     * If debug is enabled, writes a message to the log.
     * @param logEntry message to write as a log entry
     */
    public static void debug(String logEntry) {
        if (isDebug())
            logEntry = "<!-- [debug] " + logEntry + " -->";
            write(logEntry);
    }

    /**
     * If debug is enabled, writes a message to the log with an optional prefix.
     * @param prefix prefix string for the log entry
     * @param logEntry message to write as a log entry
     */
    public static void debug(String prefix, String logEntry) {if (isDebug()) message(prefix, logEntry);}

    /**
     * If debug is enabled, writes a message with a {@code Throwable} to the log file.
     * @param logEntry message to write as a log entry
     * @param throwable {@code Throwable} instance to log with this entry
     */
    public static  void debug(String logEntry, Throwable throwable) {if (isDebug()) message(logEntry, throwable);}

    ///////////////////////////////
    //OTHER LOGGING PARAMETER
    ///////////////////////////////

    public static void abort(int rc, String message) {
        System.err.println(message);
        System.exit(rc);
    }

    public static String getUsageMessage() {
        String lines[] ={/*TEST MESSAGE*/};
        String usage = "";
        for (int count = 0;count < lines.length;count++) {usage += lines[count] + "\n";}
        return usage;
    }

    public static void logger(Class c){logger = org.slf4j.LoggerFactory.getLogger(c);}

    public static void logStackTrace(Exception e, Logger logger) {
        logger.debug(e.getMessage());
        for(int i=0; i<e.getStackTrace().length; i++)
            logger.error(e.getStackTrace()[i].toString());
    }


    /**
     * Sets the separator string between the date and log message (default &quot;: &quot;).
     * To set the default separator, call with a null argument.
     * @param sep string to use as separator
     */
    public static void setSeparator(String sep) {separator = (sep == null) ? ": " : sep;}

    /**
     * Sets the log stream and enables logging.
     * By default the {@code PrintWriter} is closed when the {@link #close()}
     * method is called.
     * @param writer {@code PrintWriter} to which to write log entries
     */
    public final  void setLog(PrintWriter writer) {setLog(writer, true);}

    /**
     * Sets the log stream and enables logging.
     * @param writer {@code PrintWriter} to which to write log entries
     * @param closeOnExit whether to close the {@code PrintWriter} when {@link #close()} is called
     */
    public  static void setLog(PrintWriter writer, boolean closeOnExit) {
        if (logWriter != null)
        {
            logWriter.flush();
            if (closeWriterOnExit)
                close();
        }
        if (logging = (writer != null))
            logWriter = writer;
        else
            logWriter = null;
        closeWriterOnExit = (logWriter != null) ? closeOnExit : false;
    }

    /**
     * Returns the current {@code PrintWriter} used to write to the log.
     * @return The current {@code PrintWriter} used to write to the log
     */

    private static  void setLogWriter()  {
        try {
            logWriter = new PrintWriter(new BufferedWriter(new FileWriter(LOGFILE.getAbsolutePath(), true)));
        } catch (IOException e) {
            SystemLog.logStackTrace(e,logger);
            e.printStackTrace();
        }
    }

    /**
     * Closes the log.
     */
    public static  void close() {
        logging = false;
        if (logWriter != null)
        {
            logWriter.flush();
            if (closeWriterOnExit)
                logWriter.close();
        }
        logWriter = null;
    }

    /**
     * Writes a {@code Throwable} to the log file.
     * @param throwable {@code Throwable} instance to log with this entry
     */
    public static void throwException(Throwable throwable) {message(throwable.getMessage(), throwable);}

    /**
     * Determines whether calls to the logging methods actually write to the log.
     * @param b flag indicating whether to write to the log
     */
    public static void setLogging(boolean b) {logging = b;}

    /**
     * Returns whether calls to the logging methods actually write to the log.
     * @return true if logging is enabled, false otherwise.
     */
    public static  boolean isLogging() {return logging;}

    /**
     * Determines whether to perform debug logging.
     * @param b flag indicating whether to perform debug logging
     */
    public static  void setDebug(boolean b) {DEBUG = b;}

    /**
     * Returns whether debug logging is enabled.
     * @return true if debug logging is enabled, false otherwise.
     */
    public static  boolean isDebug() {return DEBUG;}

        
}
