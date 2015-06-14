/**
 * ManageJsonWithGoogleMaps.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti.
 * Classe per gestire la richiesta tramite url all' L'API Google Maps, e la gestione della
 * risposta della stessa, tramite Json utilizzando la libreria JSONO e creazione dei 
 * GeoDocument.
 */
package extractor;
import com.p4535992.util.http.HttpUtilApache;
import extractor.setInfoParameterIta.SetNazioneELanguage;
import object.model.GeoDocument;
import object.support.LatLng;
import org.json.JSONException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import org.json.JSONObject;
import com.p4535992.util.log.SystemLog;


public class ManageJsonWithGoogleMaps {
    
    private static String API_KEY_GM;
    //private static ManageJsonWithOSMAndJP o = new ManageJsonWithOSMAndJP();
    private static SetNazioneELanguage set = new SetNazioneELanguage();
    private static LatLng coord;
    public ManageJsonWithGoogleMaps() throws  JSONException{}
    public ManageJsonWithGoogleMaps(String API_KEY_GM){
        this.API_KEY_GM=API_KEY_GM;
    }
    /**
     * Metodo per la connessione all'API Google Maps e al suo utilizzo
     * @param g il geodocument fornito di input
     * @return le coordinate GPS ricavate attraverso le informazioni ricavate dalle
     *         annotazioni di Gate con lt'utilizzo dell'API Google Maps.
     */
    public GeoDocument connection(GeoDocument g) throws URISyntaxException{
         JSONObject json = null;       
         URL url;
         //Final Url Address
         String fua = "";  
         
         ArrayList<String> kContentList = new ArrayList<String>();
         if(setNullForEmptyString(g.getIndirizzo())!=null){
            kContentList.add(g.getRegione());
            //kContentList.add(a.getProvincia());
            kContentList.add(g.getIndirizzo());
            kContentList.add(g.getCity());
            //kContentList.add(a.getEdificio());
         }else if(setNullForEmptyString(g.getEdificio())!=null && setNullForEmptyString(g.getIndirizzo())==null){
             kContentList.add(g.getRegione());            
             kContentList.add(g.getEdificio());
             kContentList.add(g.getCity()); 
         }else{
             kContentList.add(g.getRegione());
             //kContentList.add(home.getIndirizzo());
             kContentList.add(g.getCity());    
         }
         
         for (String s : kContentList) {
            if(s!=null){
                 String address = manageArrayString(s); 
                 fua = fua +"+"+ address+"+";
                 //System.out.println(fua);
                 /////////////////////////////////////
                 fua = reduceString(fua);
                 /////////////////////////////////////
            }
         }
         fua = removeFirstAndLast(fua, "+");
         //*********************
         //IMPORTANTE
         //System.out.println(fua);
         //**********************

         //http://maps.googleapis.com/maps/api/geocode/output?parameters
         //address — The address that you want to geocode.
         //components — A component filter for which you wish to obtain a geocode. See Component Filtering for more information. The components filter will also be accepted as an optional parameter if an address is provided.
         //sensor — Indicates whether or not the geocoding request comes from a device with a location sensor. This value must be either true or false.
         //Optional parameters in a geocoding request:
         //bounds — The bounding box of the viewport within which to bias geocode results more prominently. This parameter will only influence, not fully restrict, results from the geocoder. (For more information see Viewport Biasing below.)
         //key — Your application's API key. This key identifies your application for purposes of quota management. Learn how to get a key from the APIs Console.
         //language — The language in which to return results. See the list of supported domain languages. Note that we often update supported languages so this list may not be exhaustive. If language is not supplied, the geocoder will attempt to use the native language of the domain from which the request is sent wherever possible.
         //region — The region code, specified as a ccTLD ("top-level domain") two-character value. This parameter will only influence, not fully restrict, results from the geocoder. (For more information see Region Biasing below.)
         //components — The component filters, separated by a pipe (|). Each component filter consists of a component:value pair and will fully restrict the results from the geocoder. For more information see Component Filtering, below.
         String prefix = "https://maps.googleapis.com/maps/api/geocode/json?";
         String address= "address="+fua;
         String apiKey = API_KEY_GM;
         //String suffix = "&sensor=false&key="+apiKey;
         String suffix = "&sensor=false";
         //INFORMAZIONI AGGIUNTIVE
         //L' informazione della lingua o regione rende più efficace,veloce e
         //precisa la risposta di Google Maps
         String n = null;
         try{
         n= set.checkGMRegionByNazione(g.getNazione()).toLowerCase();
         }catch(java.lang.NullPointerException ne){n="it";}
         String region ="&region="+n;
        // String region ="&region="+home.getNazione();
         //Componenti aggiuntivi per lt'url
         //The components that can be filtered include:
         //1)route: matches long or short name of a route.
         //2)locality: matches against both locality and sublocality types.
         //3)administrative_area: matches all the administrative_area levels.
         //4)postal_code: matches postal_code and postal_code_prefix.
         //5)country matches a country name or a two letter ISO 3166-1 country code.
         //6)address=santa+cruz&components=country:ES
         //7)components=administrative_area:TX|country:FR
         //e.home.components=route:Annegatan|administrative_area:Helsinki|country:Finland

        try {   
            url =new URL(prefix+address+region+suffix);
            //*****************************************
            SystemLog.message("URL for GM:" + url.toString());
            //**************************************
            //Questo metodo è più veloce ma non tiene conto
            //del numero limitato di query che fornisce Google Maps
            //json = readJsonFromUrl(url.toString());
            //***************************************
            //Più lento ma ci permette di fare un numero di query
            //indefinito a Google Maps ritardando le richieste
            //fra di loro
            json =  temporizzatorePerGoogleMaps(url);

            if(!(json.toString().contains("\"status\":\"ZERO_RESULTS\"")) ||
               !(json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\""))||
                 json!=null){  
                try{                 
                   Double lat=(Double) json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat");                 
                   Double lng=(Double) json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng");             
                    if(lat!=null && lng!=null){
                        g.setLat(lat);
                        g.setLng(lng);
                    }
                }catch(org.json.JSONException je){
                    SystemLog.warning("JSON:" + json.toString());
                }catch(Exception ex){
                     SystemLog.warning("JSON:" + json.toString());

                }
            //Se Google Maps ha raggiunto il massimo numero di query possibili
            //prova con OpenStreetMap
//            }else if(json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\"")){
//                 try{
//                    LatLng ll = o.tryWithOpenStreetMap(fua);
//                    Double lat =(Double) ll.getLat();
//                    Double lng =(Double) ll.getLng();
//                    if(lat!=null && lng!=null){
//                        geoDoc.setLat(lat);
//                        geoDoc.setLng(lng);
//                    }
//                }catch(Exception ex){}  
            }else{
                if(json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\"")){
                    SystemLog.warning(json.toString());
                }
                g.setLat(null);
                g.setLng(null);
            }
        } catch (JSONException ex) {
            SystemLog.exception(ex);
        } catch (java.lang.NullPointerException ex) {            
            SystemLog.exception(ex);
        } catch (MalformedURLException ex) {
            SystemLog.exception(ex);
        
        } catch (Exception ex) {
            SystemLog.exception(ex);
        }        
      fua = "";      
      return g; 
        
    }

    /**
     * Metodo per la connessione all'API Google Maps e al suo utilizzo
     * @param g il geodocument fornito di input
     * @return le coordinate GPS ricavate attraverso le informazioni ricavate dalle
     *         annotazioni di Gate con lt'utilizzo dell'API Google Maps.
     */
    public LatLng getCoords(GeoDocument g) throws URISyntaxException{
        JSONObject json = null;
        URL url;
        String fua = "";
        ArrayList<String> kContentList = new ArrayList<String>();
        if(setNullForEmptyString(g.getIndirizzo())!=null){
            kContentList.add(g.getRegione());
            kContentList.add(g.getIndirizzo());
            kContentList.add(g.getCity());
        }else if(setNullForEmptyString(g.getEdificio())!=null && setNullForEmptyString(g.getIndirizzo())==null){
            kContentList.add(g.getRegione());
            kContentList.add(g.getEdificio());
            kContentList.add(g.getCity());
        }else{
            kContentList.add(g.getRegione());
            kContentList.add(g.getCity());
        }

        for (String s : kContentList) {
            if(s!=null){
                String address = manageArrayString(s);
                fua = fua +"+"+ address+"+";
                fua = reduceString(fua);
            }
        }
        fua = removeFirstAndLast(fua, "+");
        String prefix = "https://maps.googleapis.com/maps/api/geocode/json?";
        String address= "address="+fua;
        String apiKey = API_KEY_GM;
        String suffix = "&sensor=false";
        String n = null;
        try{
            n= set.checkGMRegionByNazione(g.getNazione()).toLowerCase();
        }catch(java.lang.NullPointerException ne){n="it";}
        String region ="&region="+n;
        try {
            url =new URL(prefix+address+region+suffix);
            SystemLog.message("URL for GM:" + url.toString());
            json =  temporizzatorePerGoogleMaps(url);
            if(!(json.toString().contains("\"status\":\"ZERO_RESULTS\"")) ||
                    !(json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\""))||
                    json!=null){
                try{
                    Double lat=(Double) json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat");
                    Double lng=(Double) json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng");
                    if(lat!=null && lng!=null){
                        coord = new LatLng(lat,lng);
                    }
                }catch(org.json.JSONException je){
                    SystemLog.warning("JSON:" + json.toString());
                }catch(Exception ex){
                    SystemLog.warning("JSON:" + json.toString());
                }
                //Se Google Maps ha raggiunto il massimo numero di query possibili
                //prova con OpenStreetMap
//            }else if(json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\"")){
//                 try{
//                    LatLng ll = o.tryWithOpenStreetMap(fua);
//                    Double lat =(Double) ll.getLat();
//                    Double lng =(Double) ll.getLng();
//                    if(lat!=null && lng!=null){
//                        geoDoc.setLat(lat);
//                        geoDoc.setLng(lng);
//                    }
//                }catch(Exception ex){}
            }else{
                if(json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\"")){
                    SystemLog.error(json.toString());
                }
            }
        } catch (JSONException ex) {
            SystemLog.exception(ex);
        } catch (java.lang.NullPointerException ex) {
            SystemLog.exception(ex);
        } catch (MalformedURLException ex) {
            SystemLog.exception(ex);

        } catch (Exception ex) {
            SystemLog.exception(ex);
        }
        fua = "";
        if(coord==null) coord = new LatLng(0,0);
        return coord;
    }

    /**
     * Metodo per strutturare il contenuto dell'annotazione
     * @param address il contenuto dell'annotazione sottoforma di stringa
     * @return il contenuto struttturato
     */
    private String manageArrayString(String address){
         //String sgm="";
         address = address.replaceAll("\\W","+");
         address = address.replaceAll("(\\+)\\1+","+");       
         return address;
   }
   /**
    * Metodo che strttura la stringa fornita per essere utilizzata
    * come input dall'API Google Maps.
    * @param fua la stringa da ridurre
    * @return ritorna la stringa ridotta
    */ 
   private String reduceString(String fua){
       String reduce="";
       ArrayList<String> list = new ArrayList<String>();

       //removeFirstAndLast(fua, "+");

       StringTokenizer stringtokenizer = new StringTokenizer(fua, "+");
        while (stringtokenizer.hasMoreElements()) {
             String s= stringtokenizer.nextToken();
             if(!(list.contains(s)) && setNullForEmptyString(s)!=null){list.add(s);}
        }
       for (String s : list) { reduce = reduce+"+"+s;}
       return reduce;
   }
   /**
    * Rimuove il simbolo separatore del contentuo se presente all'inzio o
    * alla fine della stringa
    * @param fua il contentuo dell'annotazione sottoforma di stringa
    * @param symbol il simbolo separatore del contenuto
    * @return la stringa del contenuto senza simboli separatori all'inizio e alla fine
    */
   private String removeFirstAndLast(String fua,String symbol){
       if(fua != ""){
            fua = fua.replaceAll("(\\"+symbol+")\\1+",symbol); 
            if(fua.substring(0,1).contains(symbol)){                
                fua = fua.substring(1,fua.length());
            }
            if(fua.substring(fua.length()-1,fua.length()).contains(symbol)){                
                fua = fua.substring(0,fua.length()-1);
            }
         }
       return fua;
   }
   /**
    * Setta a null se verifica che la stringa non è
    * nulla, non è vuota e non è composta da soli spaceToken (white space)
    * @param s stringa di input
    * @return  il valore della stringa se null o come è arrivata
    */
    private String setNullForEmptyString(String s){
        //verifica che la stringa non è nulla, non è vuota e non è composta da soli spaceToken (white space)
        if(s!=null && !s.isEmpty() && !s.trim().isEmpty()){return s;}
        else{return null;}
    }        
    
    
    /////////////////////////////////////////////////////////////////////////////////////
    ///// METODI PER INVOCARE UN NUMERO INDEFINITO DI QUERY ALL'API GOOGLE MAPS//////////
    /////////////////////////////////////////////////////////////////////////////////////
    /**
     * Metodo che invoca ogni tot secondi in modo random le richieste GET HTTP
     * all'API Google Maps per evitare di superare il numero di query giornaliere
     * disponibili con la licenza freeware, è un semplice ritardo random tra una
     * richiesta e lt'altra verso lt'API Google Maps.
     * @param url identifica la richiesta all'API Google Maps 
     * @return ritorna il risultato della richiesta ma con un ritardo
     */
    public JSONObject temporizzatorePerGoogleMaps(URL url) throws IOException{
         ManageJsonWithGoogleMaps mgm = new ManageJsonWithGoogleMaps(API_KEY_GM);            
         //boolean result =false;
         //String last="";
         JSONObject json = null;
         String jsonText = null;    
         try{
            try{
               HttpUtilApache.waiter();
               //FUNZIONA
               jsonText = HttpUtilApache.get(url.toString());
            }catch(Exception e){
            }finally{
                //UN SECONDO TENTATIVO IN CASO DI FALLIMENTO (TIMEOUT,ECC.)
                if(jsonText ==null || jsonText==""){
                    jsonText = HttpUtilApache.GETWithRetry(url.toString());
                }
            }
           //NON FUNZIONA (versiona sbagliata slfj4 ma ci server per far lavorare jena)
           //jsonText = HttpUtil.GETWithRetry(url.toString());
         /*
         HttpClient client = new DefaultHttpClient();
         HttpGet request = new HttpGet(webRequestStringEncoded);       
         HttpResponse response = client.execute(request);
         BufferedReader rd = new BufferedReader( new InputStreamReader(response.getEntity().getContent(),Charset.forName("UTF-8")));

         jsonText = readAll(rd);
         */
//                System.out.println("\nSending 'GET' request to URL : " + webRequestStringEncoded);
//                System.out.println("Response Code : "
//                 + response.getStatusLine().getStatusCode()); 
           
        
         json = new JSONObject(jsonText);
        }catch(Exception ex){
            
        }finally{
            //SE SUCCEDE QUALUNQUE COSA CON HTTP SI USA JSOUP IN EXTREMIS
            try{
                if(jsonText ==null || jsonText==""){        
                     jsonText = org.jsoup.Jsoup.connect(url.toString()).ignoreContentType(true).execute().body();
                     json = new JSONObject(jsonText);
                 } 
            }catch(Exception e){
                json = null;
            }
        }
        return json;
                                 
    }

   
    
    /**
     * Semplice metodo che estare il domino org.p4535992.mvc.webapp di appartenenza dell'url analizzato
     * @param u url di ingresso in fromato stringa
     * @return il dominio org.p4535992.mvc.webapp dell'url in formato stringa
     * @throws URISyntaxException 
     */
    public String getDomainName(String u) throws URISyntaxException {     
            URI uri = new URI(u);
            String domain = uri.getHost();           
            //return domain.startsWith("www.") ? domain.substring(4) : domain;
            return domain;
    }//getDomainName
    
}//ManageJsonWithGoogleMaps.java.
