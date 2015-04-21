/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util.string;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2015-04-03
 * @author 4535992
 */
public  class StringKit {

    private static final Logger logger = LoggerFactory.getLogger(StringKit.class);
    /*
     * Read String from InputStream and closes it
     */
    public static String InputStreamToString(InputStream is, Charset encoding) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        StringBuilder sb = new StringBuilder(1024);
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
        } catch (IOException io) {
            System.out.println("Failed to read from Stream");
            io.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException ioex) {
                System.out.println("Failed to close Streams");
                ioex.printStackTrace();
            }
        }
        return sb.toString();
    }
    /**
     * Convert a String to a InputStream
     * @param input string to convert
     * @return inputstream
     */
    public static InputStream StringToInputStream(String input,Charset encoding)
     {
         //encoding = StandardCharsets.UTF_8
         InputStream stream = new ByteArrayInputStream(input.getBytes(encoding));
         // read it with BufferedReader
         /*
         BufferedReader br = new BufferedReader(new InputStreamReader(is));
         String line;
         while ((line = br.readLine()) != null) {
                  System.out.println(line);
          }
          br.close();
          */
         return stream;
     }
    
     /**
        * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
        * escaped sequence for characters outside of ASCII.
        * It is equivalent to: native2ascii -encoding utf-8
        * @param UTF8
        * @return ASCII 
        * @throws UnsupportedEncodingException
        * @throws FileNotFoundException
        * @throws IOException 
        */
       public static List<String> UTF82UnicodeEscape(File UTF8) throws UnsupportedEncodingException, FileNotFoundException, IOException{
           List<String> list = new ArrayList<String>();
           if (UTF8==null) {
                System.out.println("Usage: java UTF8ToAscii <filename>");
                return null;
            }
            BufferedReader r = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(UTF8),"UTF-8" )
            );
            String line = r.readLine();

            while (line != null) {
                 System.out.println(unicodeEscape(line));                     
                 line = r.readLine();
                 list.add(line);
            }
            r.close();   
            return list;
       }

       private static final char[] hexChar = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
       private static String unicodeEscape(String s) {
           StringBuilder sb = new StringBuilder();
           for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if ((c >> 7) > 0) {
                   sb.append("\\u");
                   sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character for the left-most 4-bits
                   sb.append(hexChar[(c >> 8) & 0xF]); // hex for the second group of 4-bits from the left
                   sb.append(hexChar[(c >> 4) & 0xF]); // hex for the third group
                   sb.append(hexChar[c & 0xF]); // hex for the last group, e.g., the right most 4-bits
               }else {
                   sb.append(c);
               }
           }
           return sb.toString();
       }     

      /**
       * Reads file with unicode escaped characters and write them out to
       * stdout in UTF-8
       * This utility is equivalent to: native2ascii -reverse -encoding utf-8
       * @param ASCII
       * @return UTF8
       * @throws FileNotFoundException
       * @throws IOException 
       */
       public static List<String> UnicodeEscape2UTF8(File ASCII) throws FileNotFoundException, IOException { 
           List<String> list = new ArrayList<>();
           if (ASCII == null) {
               System.out.println("Usage: java UnicodeEscape2UTF8 <filename>");
               return null;
           }
           BufferedReader r = new BufferedReader(new FileReader(ASCII));
           String line = r.readLine();
           while (line != null) {
               line = convertUnicodeEscape(line);
               byte[] bytes = line.getBytes("UTF-8");
               System.out.write(bytes, 0, bytes.length);
               System.out.println();
               line = r.readLine();
               list.add(line);
           }
           r.close();
           return list;
       }

       static enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}



      /**
      *  convert unicode escapes back to char
      * @param s
      * @return
      */
       private static String convertUnicodeEscape(String s) {
           char[] out = new char[s.length()];
           ParseState state = ParseState.NORMAL;
           int j = 0, k = 0, unicode = 0;
           char c = ' ';
           for (int i = 0; i < s.length(); i++) {
               c = s.charAt(i);
               if (state == ParseState.ESCAPE) {
                   if (c == 'u') {
                       state = ParseState.UNICODE_ESCAPE;
                       unicode = 0;
                   }
                   else { // we don't care about other escapes
                       out[j++] = '\\';
                       out[j++] = c;
                       state = ParseState.NORMAL;
                   }
               }
               else if (state == ParseState.UNICODE_ESCAPE) {
                   if ((c >= '0') && (c <= '9')) {
                       unicode = (unicode << 4) + c - '0';
                   }
                   else if ((c >= 'a') && (c <= 'f')) {
                       unicode = (unicode << 4) + 10 + c - 'a';
                   }
                   else if ((c >= 'A') && (c <= 'F')) {
                       unicode = (unicode << 4) + 10 + c - 'A';
                   }
                   else {
                       throw new IllegalArgumentException("Malformed unicode escape");
                   }
                   k++;
                   if (k == 4) {
                       out[j++] = (char) unicode;
                       k = 0;
                       state = ParseState.NORMAL;
                   }
               }
               else if (c == '\\') {
                   state = ParseState.ESCAPE;
               }
               else {
                   out[j++] = c;
               }
           }//for
           if (state == ParseState.ESCAPE) {
               out[j++] = c;
           }
           return new String(out, 0, j);
       }
       
       /**
        *Creating a random UUID (Universally unique identifier).
       */
       public static String RandomUUID(){
        java.util.UUID uuid =  java.util.UUID.randomUUID();
        String randomUUIDString = uuid.toString();
 
        //System.out.println("Random UUID String = " + randomUUIDString);
        //System.out.println("UUID version       = " + uuid.version());
        //System.out.println("UUID variant       = " + uuid.variant());
        return randomUUIDString;
       }
       /**
        * Metodo che converte una stringa a un'oggetto UUID
        * @param uuid
        * @return java.util.UUID
        */
       public static java.util.UUID ConvertString2UUID(String uuid){
            // creating UUID      
            java.util.UUID uid = java.util.UUID.fromString(uuid);     
            // checking string representation
            //System.out.println("String value: "+uid.toString());  
            return uid;
       }
       
        /**
        * Metodo che matcha e sostituisce determinati parti di una stringa attraverso le regular expression
        * @param input stringa di input
        * @param expression regular expression da applicare
        * @param replace setta la stringa con cui sostituire il risultato del match
        * @return il risultato in formato stringa della regular expression
        */
       public static String RegexAndReplace(String input,String expression,String replace){
           String result ="";
           //String expression = "\\d{5,7}";
           ///[^a-z0-9\s]+/ig
           if(replace==null){
               Pattern pattern = Pattern.compile(expression);
               Matcher matcher = pattern.matcher(input);
               while(matcher.find()){
                    result = matcher.group().toString();   
                    if(result != null && result != ""){break;}
               }
           }else{
               input = input.replaceAll(expression, replace);
               result = input;
           }
           return result;
       }
       
        /**
        * Setta a null se verifica che la stringa non è
        * nulla, non è vuota e non è composta da soli spaceToken (white space)
        * @param s stringa di input
        * @return  il valore della stringa se null o come è arrivata
        */
        public static String setNullForEmptyString(String s){
            if(s!=null && !s.isEmpty() && !s.trim().isEmpty()){return s;}
            else{return null;}
        } //setNullforEmptyString 
       
        /**
        * Metodo che assegna attraverso un meccanismo di "mapping" ad ogni valore 
        * distinto del parametro in questione un numero (la frequenza) prendeno il 
        * valore con la massima frequenza abbiamo ricavato il valore più diffuso 
        * per tale parametro
        * @param al lista dei valori per il determianto parametro del GeoDocument
        * @return  il valore più diffuso per tale parametro
        */
       private String getMoreCommonParameter(ArrayList<String> al){       
           Map<String,Integer> map = new HashMap<String, Integer>();  
           for(int i=0;i<al.size();i++){              
               Integer count = map.get(al.get(i));         
               map.put(al.get(i), count==null?1:count+1);   //auto boxing and count  
           }  
           //System.out.println(map);  
           //ADESSO PER OGNI VALORE POSSIBILE DEL PARAMETRO ABBIAMO INSERITO IL 
           //NUMERO DI VOLTE IN CUI SONO STATI "TROVATI" NEI VARI RECORD ANALIZZATI
           String keyParameter=null;
           Integer keyValue =0;
           for ( Map.Entry<String, Integer> entry : map.entrySet()) {
               String key = entry.getKey();
               //System.out.println(key);
               Integer value = entry.getValue();
               if(value >= keyValue && setNullForEmptyString(key)!=null && !key.equals("null") && !key.equals("NULL")){
                   keyValue = value;
                   keyParameter = key;
               }
           }
           return keyParameter;
       }//getMoreCommonParameter

        /**
         * Metodo che "taglia" la descrizione dell'edificio al minimo indispensabile
         * @param content stringa del contenuto da tokenizzare
         * @param symbol simbolo del tokenizer
         * @return la stringa tokenizzata
         */
        private String getTheFirstTokenOfATokenizer(String content,String symbol){
           StringTokenizer st = new StringTokenizer(content, symbol);                
           while (st.hasMoreTokens()) {
                   content = st.nextToken().toString();
                   if(setNullForEmptyString(content)==null){
                       continue;              
                   }else{ break;}            
           }
           return content;
        }
        /**
         * Method for convert a cstrin to a OutputStream and print to a file
         * @param content, string to print on the file
         * @param outputPathFileName, file where i put the stream
         */
        public void convertStringToOutputStreamAndSaveIntoAFile(String content,String outputPathFileName) {					
		//String fileName = parameters.get(PARAM_EXPORT_FILE)+"."+parameters.get(PARAM_QUERYFILE);
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputPathFileName, true)))) {
		    out.print(content);		    	    	    
		    out.flush();
		    out.close();
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
	 }

    /**
     * Method for read the input on the console
     * @throws IOException
     */
    public String readConsole() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //System.out.print("Enter String");
        String input;
        do {
            input = br.readLine();
            //System.out.print("Enter Integer:");
        }while(br.read() > 0);
        try{
            //int i = Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            throw new NumberFormatException("Error while read in console:"+ nfe.getMessage());
        }
        return input;
    }


    /**
     * read from a file and append into a StringBuilder every new line
     * @param filename
     */
    public static String readFileWithStringBuilder(File filename) {
        String readFile = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            StringBuilder sbFile = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                // append the line of the file
                sbFile.append(line);
                // separate the line with a '@'
                sbFile.append('@');

                // read the next line of the file
                line = br.readLine();
            }
            // this string contains the character sequence
            readFile = sbFile.toString();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  readFile;
    }

    /**
     * Merge the content of two arrays of string with same size for
     * make the args for a main method java class with option
     * e.g. arrays1 = ["A","B"] arrays2 = ["--firstLetter",--"secondLetter"] -> arrays3 = ["--firstLetter","A",--"secondLetter","B"]
     * @param param
     * @param value
     * @return
     * @throws Exception
     */
    public static String[] mergeArraysString(String[] param,String[] value) throws Exception {
        String[] args2;
        int j = 0;
        if(param.length==value.length) {
            args2 = new String[param.length+value.length];
            for (int i = 0; i < param.length; i++) {
                if (i == 0)
                    j = j + i;
                else
                    j = j + 1;

                args2[j] = param[i];
                j = j + 1;
                args2[j] = value[i];
            }
        }else{
            logger.error("WARNING: Check your array size");
            throw new Exception("WARNING: Check your array size");
        }
       return args2;
    }

    /**
     * Method to convert a list to a array object
     * @param list
     * @return
     */
    public static Object[] convertListToArray(List<Object> list){
        Object[] array;
        array = new Object[ list.size()];
        list.toArray(array );
        return array;
    }



}//end of the class StringKit
       

