package com.github.p4535992.extractor.estrattori;
import com.github.p4535992.extractor.object.model.GeoDomainDocument;
import com.github.p4535992.extractor.setInfoParameterIta.SetNazioneELanguage;
import com.github.p4535992.util.http.HttpUtilities;

import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.support.LatLng;
import com.github.p4535992.util.http.HttpUtilApache4;
import com.github.p4535992.util.string.StringUtilities;
import org.json.JSONException;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import org.json.JSONObject;


/**
 * ManageJsonWithGoogleMaps.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti.
 * Classe per gestire la richiesta tramite url all' L'API Google Maps, e la gestione della
 * risposta della stessa, tramite Json utilizzando la libreria JSONO e creazione dei
 * GeoDocument.
 * @author 4535992.
 * @version 2015-09-30.
 */
@SuppressWarnings("unused")
public class ManageJsonWithGoogleMaps {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( ManageJsonWithGoogleMaps.class);

    private String API_KEY_GM;
    //private static ManageJsonWithOSMAndJP o = new ManageJsonWithOSMAndJP();
    private static SetNazioneELanguage set = new SetNazioneELanguage();
    private static ManageJsonWithGoogleMaps instance = null;
    protected ManageJsonWithGoogleMaps(){}
    protected ManageJsonWithGoogleMaps(String API_KEY_GM){
        this.API_KEY_GM=API_KEY_GM;
    }
    public static ManageJsonWithGoogleMaps getInstance(){
        if(instance == null) {
            instance = new ManageJsonWithGoogleMaps();
        }
        return instance;
    }
    public static ManageJsonWithGoogleMaps getInstance(String API_KEY_GM){
        if(instance == null) {
            instance = new ManageJsonWithGoogleMaps(API_KEY_GM);
        }
        return instance;
    }

    private static final String JSON_EMPTY = "{}";
    private static final String JSON_ZERO_RESULT = "{\n" +
            "   \"results\" : [],\n" +
            "   \"status\" : \"ZERO_RESULTS\"\n" +
            "}";

    /**
     * Metodo per la connessione all'API Google Maps e al suo utilizzo
     * @param g il geodocument fornito di input.
     * @return le coordinate GPS ricavate attraverso le informazioni ricavate dalle
     *         annotazioni di Gate con lt'utilizzo dell'API Google Maps.
     * @throws URISyntaxException error.
     */
    public LatLng getCoords(GeoDocument g) throws URISyntaxException {
        List<String> kContentList = new ArrayList<>();
        if (StringUtilities.setNullForEmptyString(g.getIndirizzo()) != null) {
            kContentList.add(g.getRegione());
            kContentList.add(g.getIndirizzo());
            kContentList.add(g.getCity());
        } else if (StringUtilities.setNullForEmptyString(g.getEdificio()) != null
                && StringUtilities.setNullForEmptyString(g.getIndirizzo()) == null) {
            kContentList.add(g.getRegione());
            kContentList.add(g.getEdificio());
            kContentList.add(g.getCity());
        } else {
            kContentList.add(g.getRegione());
            kContentList.add(g.getCity());
        }
        String n;
        try{
            n= set.checkGMRegionByNazione(g.getNazione()).toLowerCase();
        }catch(java.lang.NullPointerException ne){n="it";}
        return getCoordinatesFromStringAddress(kContentList,n);
    }

    /**
     * Metodo per la connessione all'API Google Maps e al suo utilizzo.
     * @param g il geodomaindocument fornito di input.
     * @return le coordinate GPS ricavate attraverso le informazioni ricavate dalle
     *         annotazioni di Gate con lt'utilizzo dell'API Google Maps.
     * @throws URISyntaxException error.
     */
    public LatLng getCoords(GeoDomainDocument g) throws URISyntaxException {
        List<String> kContentList = new ArrayList<>();
        if (StringUtilities.setNullForEmptyString(g.getIndirizzo()) != null) {
            kContentList.add(g.getRegione());
            kContentList.add(g.getIndirizzo());
            kContentList.add(g.getCity());
        } else if (StringUtilities.setNullForEmptyString(g.getEdificio()) != null
                && StringUtilities.setNullForEmptyString(g.getIndirizzo()) == null) {
            kContentList.add(g.getRegione());
            kContentList.add(g.getEdificio());
            kContentList.add(g.getCity());
        } else {
            kContentList.add(g.getRegione());
            kContentList.add(g.getCity());
        }
        String n;
        try{
            n= set.checkGMRegionByNazione(g.getNazione()).toLowerCase();
        }catch(java.lang.NullPointerException ne){n="it";}
        return getCoordinatesFromStringAddress(kContentList,n);
    }

    /**
     * Method to prepare tha uri for Google Maps by a list of string information of the location.
     * @param kContentList a List of String of all information on the location.
     * @param nation the code of the nation where is situated the location.
     * @return a string to insert in the uri of the web service of google maps.
     */
    private String prepareRawAddress(List<String>  kContentList,String nation){
        String fua = "";
        for (String s : kContentList) {
            if(s!=null){
                String address = manageArrayString(s);
                fua = fua +"+"+ address+"+";
                fua = reduceString(fua);
            }
        }
        fua = StringUtilities.removeFirstAndLast(fua, "+");
        String prefix = "https://maps.googleapis.com/maps/api/geocode/json?";
        String address= "address="+fua;
        String apiKey = API_KEY_GM;
        String suffix = "&sensor=false";
        String region ="";
        if(!StringUtilities.isNullOrEmpty(nation))region ="&region="+nation;
        return prefix+address+region+suffix;
    }

    /**
     * Method to get coordinates with google maps from a list of informations.
     * @param rawAddress the list of information of the location.
     * @param nation the code of the nation where is situated the location.
     * @return the LatLngObject of the geographical coordinates.
     */
    private LatLng getCoordinatesFromStringAddress(List<String> rawAddress,String nation){
        JSONObject json;
        try {
            URL url = new URL(prepareRawAddress(rawAddress, nation));
            logger.info("URL for GM:" + url.toString());
            json = temporizzatorePerGoogleMaps(url);
            if (json != null) {
                if (!(json.toString().contains("\"status\":\"ZERO_RESULTS\"")) ||
                        !(json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\""))) {
                    try {
                        Double lat = (Double) json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat");
                        Double lng = (Double) json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng");
                        if (lat != null && lng != null) {
                            return new LatLng(lat, lng);
                        }
                    } catch (JSONException je) {
                        //SystemLog.warning("JSON:" + json.toString());
                        return new LatLng(null, null);
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
                } else {
                    if (json.toString().contains("\"status\":\"OVER_QUERY_LIMIT\"")) {
                        logger.error(json.toString());
                    }
                }
            }//json !=null
        }catch(javax.net.ssl.SSLHandshakeException e){
            try {
                HttpUtilities.waiter();
            } catch (InterruptedException e1) {
                logger.error(e1.getMessage(),e1);
            }
        } catch (IOException|java.lang.NullPointerException e){
            logger.error(e.getMessage(),e);
        }
        return new LatLng(null,null);
    }

    /**
     * Metodo per strutturare il contenuto dell'annotazione
     * @param address il contenuto dell'annotazione sottoforma di stringa
     * @return il contenuto struttturato
     */
    private String manageArrayString(String address){
         //String sgm="";
         address = address.replaceAll("\\W","+"); //replace all space to symbol
         address = address.replaceAll("(\\+)\\1+","+");//replace the multiple symbole to one
         return address;
   }
   /**
    * Metodo che struttura la stringa fornita per essere utilizzata
    * come input dall'API Google Maps.
    * @param fua la stringa da ridurre
    * @return ritorna la stringa ridotta
    */ 
   private String reduceString(String fua){
       String reduce="";
       List<String> list = new ArrayList<>();
       //removeFirstAndLast(fua, "+");
       StringTokenizer stringtokenizer = new StringTokenizer(fua, "+");
        while (stringtokenizer.hasMoreElements()) {
             String s= stringtokenizer.nextToken();
             if(!(list.contains(s)) && StringUtilities.setNullForEmptyString(s)!=null){list.add(s);}
        }
       for (String s : list) { reduce = reduce+"+"+s;}
       return reduce;
   }
    
    /////////////////////////////////////////////////////////////////////////////////////
    ///// METODI PER INVOCARE UN NUMERO INDEFINITO DI QUERY ALL'API GOOGLE MAPS//////////
    /////////////////////////////////////////////////////////////////////////////////////
    /**
     * Metodo che invoca ogni tot secondi in modo random le richieste GET HTTP
     * all'API Google Maps per evitare di superare il numero di query giornaliere
     * disponibili con la licenza freeware, è un semplice ritardo random tra una
     * richiesta e lt'altra verso lt'API Google Maps.
     * @param url identifica la richiesta all'API Google Maps .
     * @return ritorna il risultato della richiesta ma con un ritardo.
     * @throws IOException error.
     */
    public JSONObject temporizzatorePerGoogleMaps(URL url) throws IOException{
         ManageJsonWithGoogleMaps mgm = new ManageJsonWithGoogleMaps(API_KEY_GM);            
         //boolean result =false;
         //String last="";
         JSONObject json = new JSONObject();
         String jsonText = "";
         try{
            try{
                HttpUtilities.waiter();
                jsonText = org.jsoup.Jsoup.connect(url.toString()).ignoreContentType(true).execute().body();
            } catch (org.jsoup.HttpStatusException e) {
                logger.warn(e.getMessage(), e);
            } catch(org.json.JSONException e2){
                json = new JSONObject(JSON_ZERO_RESULT);
            } catch(java.net.SocketTimeoutException e3){
                HttpUtilities.waiter();
                jsonText = HttpUtilities.executeHTTPGetRequest(url.toString());
            } finally{
                if(StringUtilities.isNullOrEmpty(jsonText) ||
                        Objects.equals(jsonText, JSON_ZERO_RESULT) || Objects.equals(jsonText, JSON_EMPTY)){
                    HttpUtilities.waiter();
                    //jsonText = org.jsoup.Jsoup.connect(url.toString()).ignoreContentType(true).execute().body();
                    jsonText = HttpUtilities.executeHTTPGetRequest(url.toString());
                    if(StringUtilities.isNullOrEmpty(jsonText) ||
                            Objects.equals(jsonText, JSON_ZERO_RESULT) || Objects.equals(jsonText, JSON_EMPTY)) {
                        HttpUtilities.waiter();
                        //jsonText = HttpUtil.get(url.toString());
                        jsonText = HttpUtilApache4.getWithRetry(url.toString());
                    }

                }
                json = new JSONObject(jsonText);
                logger.warn("JSON:" + json.toString());
            }
         }catch(InterruptedException e){
             logger.warn(e.getMessage(),e);
         } finally{
             if(StringUtilities.isNullOrEmpty(jsonText) ||
                     Objects.equals(jsonText, JSON_ZERO_RESULT) || Objects.equals(jsonText, JSON_EMPTY)){
                    json = new JSONObject(JSON_ZERO_RESULT);
            }
        }
        return json;
                                 
    }

}//ManageJsonWithGoogleMaps.java.
