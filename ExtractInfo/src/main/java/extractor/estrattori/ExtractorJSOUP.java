/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package extractor.estrattori;

import object.model.GeoDocument;
import p4535992.util.log.SystemLog;
import p4535992.util.http.HttpUtilApache;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Marco
 */
public class ExtractorJSOUP {

    public static boolean isEXIST_WEBPAGE() {
        return EXIST_WEBPAGE;
    }

    public static void setEXIST_WEBPAGE(boolean EXIST_WEBPAGE) {
        ExtractorJSOUP.EXIST_WEBPAGE = EXIST_WEBPAGE;
    }

    private static boolean EXIST_WEBPAGE;


    
    public ExtractorJSOUP(){}
    int tentativi = 0;
    public GeoDocument GetTitleAndHeadingTags(String url,GeoDocument geo) throws IOException, InterruptedException{
        String result = "";
        Document doc = null;
        try{
            if(tentativi < 3){
                //doc = Jsoup.connect(url).get();
                //doc = Jsoup.parse(Jsoup.connect(url).ignoreContentType(true).execute().contentType());
                doc = Jsoup.connect(url).timeout(10*1000).get();
                EXIST_WEBPAGE = true;
            }else{
                tentativi = 0;
            }
        }catch(SocketTimeoutException e){           
            tentativi++;
            HttpUtilApache.waiter();
            if(tentativi < 3) GetTitleAndHeadingTags(url,geo);
            else doc = null;
        }catch(Exception en){        
            tentativi++;
            HttpUtilApache.waiter();
            if(tentativi < 3) GetTitleAndHeadingTags(url,geo);
            else doc = null;
        }

        if(doc==null){
            try{
                String html = HttpUtilApache.get(url);
                doc = Jsoup.parse(html);
                if(html!=null || html !=""){
                    SystemLog.message("HTTP GET HA AVUTO SUCCESSO");
                    EXIST_WEBPAGE = true;
                }else{
                    throw new Exception("HTTP GET HA FALLITO");
                }
            }catch(Exception en){
                //SystemLog.error("HTTP GET HA FALLITO:" + en.getMessage());
                EXIST_WEBPAGE = false;
            }
        }else{
            SystemLog.message("JSOUP GET HA AVUTO SUCCESSO");
            EXIST_WEBPAGE = true;
        }

        if(doc !=null && EXIST_WEBPAGE){
            //Elements titleTags = doc.trySelect("h1,h2");
            //SETTIAMO TITLE
            //Elements titleTags = doc.trySelect("title");
            //Elements h1Tags = doc.trySelect("h1");
            List<String> tagList = Arrays.asList(                  
                    "title","meta[name=dc:title]","meta[name=DC.Title]","meta[name=og:title]","h1" );
            //"title","dc:title","DC.Title","twitter:title","og:title","h1"
            for(String s: tagList){
                Elements Tags = doc.select(s);
                for(Element e : Tags){
                     result += pulisciStringaEdificio(e.text())+" "; 
                     if(result.contains("momentaneamente disabilitato")
                       //||result.contains("sito web") //occupato,non più raggiungibile,nestitente
                             ){break;}
                     if(setNullForEmptyString(result)!=null)break;
                }
                if(setNullForEmptyString(result)==null && doc.select(s).size()>0){   
                       result += doc.select(s).first().attr("content");               
                }
                //if(setNullForEmptyString(result)!=null){break;}
            }
      
            if(setNullForEmptyString(result)==null){
                result=null;
            }else
            {
                result = result.replaceAll("[\\-\\|\\;\\,\\^]", "").trim();
            }
            geo.setEdificio(result);


            //SETTTIAMO DESCRIPTION
            result ="";
            tagList = Arrays.asList("description","meta[name=description]","meta[property=og:description]");
            for(String s: tagList){
                Elements Tags = doc.select(s);
                for(Element e : Tags){
                     result += pulisciStringaEdificio(e.text())+" ";    
                }
                if(setNullForEmptyString(result)==null && doc.select(s).size()>0){   
                       result = doc.select(s).first().attr("content");               
                }
                if(setNullForEmptyString(result)!=null){break;}
            }
            if(setNullForEmptyString(result)==null){
                result=null;
            }
            geo.setDescription(result);
            //SETTIAMO LANGUAGE
            result="";
            tagList = Arrays.asList(
                    "html","meta[name=lang]","meta[name=dc.language]","meta[name=language]","http-equiv[content-language]");
            for(String s: tagList){
                Elements Tags = doc.select(s);
                for(Element e : Tags){
                    if(s != "html"){
                     result += pulisciStringaEdificio(e.text())+" ";    
                    }
                }
                if(setNullForEmptyString(result)==null && doc.select(s).size()>0){   
                       result = doc.select(s).first().attr("lang");               
                }else if(setNullForEmptyString(result)==null && doc.select(s).size()>0 && s!= "html"){   
                       result = doc.select(s).first().attr("content");   
                }
                if(setNullForEmptyString(result)!=null){break;}
            }
            
            if(setNullForEmptyString(result)==null){
                result="it";
            }
            geo.setNazione(result);    
            //SystemLog.message("DESCRIPTION:" + geo.getDescription());
            SystemLog.message("EDIFICIO:" + geo.getEdificio());
            //SystemLog.message("LANGUAGE:" + geo.getNazione());
        }else{        
            SystemLog.error("FALLITO IL GET PER LA PAGINA:" + url + " ANDIAMO ALLA SUCESSIVA");
            geo.setEdificio(null);
        }
        geo.setUrl(new URL(url));
        /*
        try{
            geo = littleUpdateEdificio(geo);
        }catch(URISyntaxException ue){}
        */
        return geo;
    }
  
    //METODI DI SUPPORTO EREDIATI DA TIKA
    /**
     * Ripulisce la stringa edificio da caratteri non voluti 
     * @param edificio la stringa da "ripulire"
     * @return la stringa "ripulita"
     */
    private String pulisciStringaEdificio(String edificio){
        List<String> badWords = Arrays.asList("INDEX","index","home", "HOME", "homepage","HOMEPAGE",
                "page","PAGE","Homepage","Page","Home","Chi siamo","Chi Siamo","Portale","portale",
                "NEWS","News","Benvenuto nel","benvenuto nel","Benvenuto","benvenuto"
                );
        for(String s: badWords){
            if(edificio.contains(s)){
                //System.out.println("STAMPA:"+s);
                edificio = edificio.replaceAll(s," ");
            }
        }
        //if(linkString.contains("|")){st = new StringTokenizer(linkString, "|");}
        //org.apache.commons.lang3.StringUtils.containsIgnoreCase("ABCDEFGHIJKLMNOP", "gHi");
//        StringTokenizer st = null;
//           
//        st = new StringTokenizer(edificio, "|");                
//        while (st.hasMoreTokens()) {
//                edificio = st.nextToken().toString();
//                if(setNullForEmptyString(edificio)==null){
//                    continue;              
//                }else{break;}            
//        }

        if(edificio.contains("...")){edificio = edificio.replace("...", "");}
        if(edificio.contains(".")){edificio = edificio.replace(".", ""); }
        if(edificio.contains("::")){edificio = edificio.replace(".", "");}    
        if(edificio.contains(":")){edificio = edificio.replace(".", ""); }
        //edificio = getTheFirstTokenOfATokenizer(edificio, "|");
        edificio = getTheFirstTokenOfATokenizer(edificio, "...");
        //edificio = getTheFirstTokenOfATokenizer(edificio, ",");
        edificio = getTheFirstTokenOfATokenizer(edificio, ".");
        edificio = getTheFirstTokenOfATokenizer(edificio, ";");
        //edificio = getTheFirstTokenOfATokenizer(edificio, "-");
        
        //System.out.println("TIKA:"+edificio);
        return edificio;
    }
    
    /**
     * Setta a null se verifica che la stringa non è
     * nulla, non è vuota e non è composta da soli spaceToken (white space)
     * @param s stringa di input
     * @return  il valore della stringa se null o come è arrivata
     */
     private String setNullForEmptyString(String s){     
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
      * Piccola modifica del campo edifico del GeoDocument in esame
      * @param geo il GeoDocument in esame
      * @return il GeoDocument in esame
      * @throws URISyntaxException 
      */
     private GeoDocument littleUpdateEdificio(GeoDocument geo) throws URISyntaxException{
         String edificio = geo.getEdificio();
         if(edificio.toLowerCase().contains(HttpUtilApache.getAuthorityName(geo.getUrl().toString().toLowerCase()))){
         } else {
             edificio = HttpUtilApache.getAuthorityName(geo.getUrl().toString()).toUpperCase()+" - "+edificio;
             geo.setEdificio(edificio);
        }
         return geo;
     } 
}
