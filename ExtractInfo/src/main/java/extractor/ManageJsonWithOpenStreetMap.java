/**
 * ManageJsonWithOSMAndJP.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti.
 * Classe per gestire la richiesta tramite url all' L'API OpenStreetMap e il Jackson Parser
 * Dato un'indirizzo restituisce le coordinate GPS. 
 * UTILE COME ALTERNATIVA A GOOGLE MAPS E COMPLETAMENTE OPEN SOURCE
 */
package extractor;

import object.support.LatLng;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;
 
public class ManageJsonWithOpenStreetMap {
 
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lon";
 
    public LatLng parseInputStream(final InputStream jsonStream){
        LatLng coordinate = null;
        final ObjectMapper mapper = new ObjectMapper();
//        try {
//            final List<Object> dealData = mapper.readValue(jsonStream, List.class);
//            if (dealData != null && dealData.size() == 1) {
//                final Map< String, Object > locationMap = (Map< String, Object >) dealData.get(0);
//                if (locationMap != null && locationMap.containsKey(LATITUDE) && locationMap.containsKey(LONGITUDE)) {
//                    final double lat = Double.parseDouble(locationMap.get(LATITUDE).toString());
//                    final double lng = Double.parseDouble(locationMap.get(LONGITUDE).toString());
//                    coordinate = new LatLng(lat, lng);
//                 }
//             } else {
//                 Logger.getLogger(ManageJsonWithOpenStreetMap.class.getName()).log(Level.SEVERE, "NO RESULTS", "NO RESULTS");
//             }
//         } catch (Exception ex) {
//             Logger.getLogger(ManageJsonWithOpenStreetMap.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//         }
    return coordinate;
    }
 
    public LatLng parse(String rawAddress) {
        InputStream is = null;
        LatLng coords = null;
        if (rawAddress != null && rawAddress.length() > 0 ) {
            try {
                String address = URLEncoder.encode(rawAddress, "utf-8");
                //String geocodeURL = "http://nominatim.openstreetmap.org/search?format=json&amp;limit=1&amp;polygon=0&amp;addressdetails=0&amp;email=contact@EMAIL.ME&amp;countrycodes=us&amp;q=";
                //http://nominatim.openstreetmap.org/search?q=Arezzo+madonna+di+mezzastrada+47&countrycodes=IT&format=json&polygon=1&addressdetails=1
                String geocodeURL = "http://nominatim.openstreetmap.org/search?q="+address
                        + "&format=json"
                        + "&limit=1&polygon=1&addressdetails=1"
                         //+ "&email=contact@EMAIL.ME"
                        + "&countrycodes=it"
                        ;
                //query google geocode api
                //String formattedUrl = geocodeURL + address;
                String formattedUrl = geocodeURL;
                URL geocodeUrl = new URL(formattedUrl);
                is = geocodeUrl.openStream();
                coords = parseInputStream(is);
            } catch (IOException ex) {
                Logger.getLogger(ManageJsonWithOpenStreetMap.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    Logger.getLogger(ManageJsonWithOpenStreetMap.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return coords;
    }
 
//    public static void main(final String[] args) {
//        final String rawAddress = "Florence+,+FI" ;
//        ManageJsonWithOSMAndJP o = new ManageJsonWithOSMAndJP();       
//        LatLng ll = o.parse(rawAddress);
//        System.out.println("LL:"+ll);
//        System.out.println("Lat:"+ll.getLat()+",LON:"+ll.getLng());
//    }
    
    public static  LatLng tryWithOpenStreetMap(final String rawAddress){
        ManageJsonWithOpenStreetMap o = new ManageJsonWithOpenStreetMap();       
        LatLng ll = o.parse(rawAddress);
        //System.out.println("Lat:"+ll.getLat()+",LON:"+ll.getLng());  
        return ll;
    }
}