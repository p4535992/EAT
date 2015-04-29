/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package p4535992.util.string;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p4535992.util.reflection.ReflectionKit;

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
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2015-04-25
 * @author 4535992
 * @href: http://stackoverflow.com/questions/9572795/convert-list-to-array-in-java
 * @href: http://stackoverflow.com/questions/11404086/how-could-i-initialize-a-generic-array
 */
public class StringKit<T> {
    private Class<T> cl;
    private String clName;
    private static final Logger logger = LoggerFactory.getLogger(StringKit.class);

    public StringKit(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

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
     public static InputStream StringToInputStream(String input,Charset encoding) {
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
     public static List<String> UTF82UnicodeEscape(File UTF8) throws IOException{
         List<String> list = new ArrayList<String>();
         if (UTF8==null) {
             System.out.println("Usage: java UTF8ToAscii <filename>");
             return null;
         }
         BufferedReader r = new BufferedReader(
                 new InputStreamReader(new FileInputStream(UTF8),"UTF-8" )
         );
         String line = r.readLine();

         while (line != null) {
             //System.out.println(unicodeEscape(line));
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
              //System.out.println("Usage: java UnicodeEscape2UTF8 <filename>");
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
    public static String RandomUUID(){ return  java.util.UUID.randomUUID().toString(); }
   /**
    * Metodo che converte una stringa a un'oggetto UUID
    * @param uuid
    * @return java.util.UUID
    */
    public static java.util.UUID convertString2UUID(String uuid){return java.util.UUID.fromString(uuid); }
       
    /**
    * Metodo che matcha e sostituisce determinati parti di una stringa attraverso le regular expression
    * @param input stringa di input
    * @param expression regular expression da applicare
    * @param replace setta la stringa con cui sostituire il risultato del match
    * @return il risultato in formato stringa della regular expression
    */
   public static String RegexAndReplace(String input,String expression,String replace){
       String result ="";
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
       String keyParameter=null;
       Integer keyValue =0;
       for ( Map.Entry<String, Integer> entry : map.entrySet()) {
           String key = entry.getKey();
           Integer value = entry.getValue();
           if(value >= keyValue && setNullForEmptyString(key)!=null && !key.equalsIgnoreCase("null")){
               keyValue = value;
               keyParameter = key;
           }
       }return keyParameter;
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
    public void copyString2File(String content, File outputPathFileName) {
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
                sbFile.append(line);// append the line of the file
                sbFile.append('@');// separate the line with a '@'
                line = br.readLine();// read the next line of the file
            }
            readFile = sbFile.toString();// this string contains the character sequence
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
    public static <T> T[] mergeArrays(T[] param, T[] value) throws Exception {
        T[] array;
        int j = 0;
        if(param.length==value.length) {
            //array = new T[param.length+value.length];
            array = (T[]) Array.newInstance(param[0].getClass(),param.length+value.length);
            for (int i = 0; i < param.length; i++) {
                if (i == 0)
                    j = j + i;
                else
                    j = j + 1;

                array[j] = param[i];
                j = j + 1;
                array[j] = value[i];
            }
        }else{
            //logger.error("WARNING: Check your array size");
            throw new Exception("WARNING: Check your array size");
        }
       return array;
    }

    /**
     * Method to convert a list to a array object
     * @param list
     * @return
     */
    public static <T> T[] convertList2Array(List<T> list){
        //return list.toArray(new Object[ list.size()]);
        T[] array = (T[]) Array.newInstance(list.get(0).getClass(), list.size());
        //T[] items=(T[]) new Object[size]
        if(ReflectionKit.isWrapperType(list.get(0).getClass())){ //if is a primitve class
            for(int i = 0; i < list.size(); i++) array[i] = list.get(i);
        }else{ //is is not a primitve class
            list.toArray(array);
        }
        return array;
    }

    public static int[] countElementOfAString() throws IOException {
        int i=0,j=0,k=0;
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        String s;
        s=br.readLine();//Enter File Name:
        br=new BufferedReader(new FileReader(s));
        while((s=br.readLine())!=null)
        {
            k++;
            StringTokenizer st=new StringTokenizer(s," .,:;!?");
            while(st.hasMoreTokens())
            {
                i++;
                s=st.nextToken();
                j+=s.length();
            }
        }
        br.close();
        return new int[]{i,j,k}; //Number of Words:,Number of Characters:,Number of Lines:
    }





}//end of the class StringKit
       

