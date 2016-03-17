/**
 * ManageJsonWithOSMAndJP.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti.
 * Classe per gestire la richiesta tramite url all' L'API OpenStreetMap e il Jackson Parser
 * Dato un'indirizzo restituisce le coordinate GPS. 
 * UTILE COME ALTERNATIVA A GOOGLE MAPS E COMPLETAMENTE OPEN SOURCE
 */
package com.github.p4535992.extractor.estrattori;

import com.github.p4535992.extractor.object.support.LatLng;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Class to manage JSON resposne with OpenStreetMap.
 */
@SuppressWarnings("unused")
public class ManageJsonWithOpenStreetMap {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger( ManageJsonWithOpenStreetMap.class);

    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";

    protected ManageJsonWithOpenStreetMap(){}
    private static ManageJsonWithOpenStreetMap instance = null;
    public static  ManageJsonWithOpenStreetMap getInstance(){
        if(instance == null) {
            instance = new ManageJsonWithOpenStreetMap();
        }
        return instance;
    }

    public LatLng getCoords(String rawAddress){
        return parse(rawAddress);
    }

    /**
     * Method support for parse inputStream with OPenStreetMap.
     * @param jsonStream inputStream.
     * @return LatLng object.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private LatLng parseInputStream(final InputStream jsonStream){
        LatLng coordinate = null;
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final List dealData = mapper.readValue(jsonStream, List.class);
            if (dealData != null && dealData.size() == 1) {
                final Map< String, Object > locationMap = (Map<String, Object >) dealData.get(0);
                if (locationMap != null && locationMap.containsKey(LATITUDE) && locationMap.containsKey(LONGITUDE)) {
                    final double lat = Double.parseDouble(locationMap.get(LATITUDE).toString());
                    final double lng = Double.parseDouble(locationMap.get(LONGITUDE).toString());
                    coordinate = new LatLng(lat, lng);
                 }
             } else {
                logger.warn("NO RESULT");
             }
         } catch (Exception e) {
            logger.error(e.getMessage(),e);
         }
    return coordinate;
    }
 
    private LatLng parse(String rawAddress) {
        LatLng coords = null;
        if (rawAddress != null && rawAddress.length() > 0 ) {
            String address;
            try {
                address = URLEncoder.encode(rawAddress, "utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(),e);
                return null;
            }
            //String geocodeURL = "http://nominatim.openstreetmap.org/search?format=json&amp;limit=1&amp;polygon=0&amp;addressdetails=0&amp;email=contact@EMAIL.ME&amp;countrycodes=us&amp;q=";
                //http://nominatim.openstreetmap.org/search?q=Arezzo+madonna+di+mezzastrada+47&countrycodes=IT&format=json&polygon=1&addressdetails=1
                //query google geocode api
                //String formattedUrl = geocodeURL + address;
                String formattedUrl = "http://nominatim.openstreetmap.org/search?q="+address
                        + "&format=json"
                        + "&limit=1&polygon=1&addressdetails=1"
                         //+ "&email=contact@EMAIL.ME"
                        + "&countrycodes=it";
            try (InputStream is = new URL(formattedUrl).openStream()){
                coords = parseInputStream(is);
            } catch (IOException ex) {
                logger.error(ex.getMessage(),ex);
            }
        }
        return coords;
    }
}