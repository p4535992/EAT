package com.github.p4535992.extractor.estrattori;

import com.github.p4535992.extractor.ManageJsonWithGoogleMaps;
import com.github.p4535992.extractor.object.model.GeoDomainDocument;
import com.github.p4535992.extractor.object.support.DepositFrequencyInfo;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.util.file.SimpleParameters;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.extractor.object.impl.jdbc.GeoDomainDocumentDaoImpl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.p4535992.extractor.object.support.LatLng;
import com.github.p4535992.util.string.StringKit;

/**
 * MainEstrazioneGeoDomainDocumentPerElaborato.java
 * Classe per lt'estrazione dei GeoDocument o InfoDocument relativi ai singoli domni org.p4535992.mvc.webapp
 * attraverso un'analisi dei singoli GeoDocument e InfoDocument dei singoli URL segunedo
 * opportuni criteri di scelta e confronto.
 * @author 4535992.
 * @version 2015-06-30.
 */
@SuppressWarnings("unused")
public class ExtractorDomain {
    
    //FREQUENZA DEGLI URL PER L'IDENTIFICAZIONE DEL DOMINIO
    private  Integer FREQUENZA_INTERVALLO_URL,LIMIT,OFFSET;
    private  GeoDomainDocumentDaoImpl geoDomainDocDao;
    private static ExtractorDomain instance = null;
    protected ExtractorDomain(){}

    protected ExtractorDomain(GeoDomainDocumentDaoImpl dao, Integer LIMIT, Integer OFFSET, Integer FREQUENZA_INTERVALLO_URL
    ){
        this.LIMIT = LIMIT;
        this.OFFSET = OFFSET;
        this.FREQUENZA_INTERVALLO_URL = FREQUENZA_INTERVALLO_URL;
        this.geoDomainDocDao = dao;
    }

    public static ExtractorDomain getInstance(){
        if(instance == null) {
            instance = new ExtractorDomain();
        }
        return instance;
    }

    public static ExtractorDomain getInstance(
            GeoDomainDocumentDaoImpl dao, Integer LIMIT, Integer OFFSET, Integer FREQUENZA_INTERVALLO_URL){
        if(instance == null) {
            instance = new ExtractorDomain(dao,LIMIT,OFFSET,FREQUENZA_INTERVALLO_URL);
    }
        return instance;
    }
    //***********************************************************************************************************
    private static List<String> listDomains = new ArrayList<>();
    private static List<String> listFinalDomains = new ArrayList<>();
    private static List<DepositFrequencyInfo> listDepositFrequency = new ArrayList<>();
  
    public void CreateTableOfGeoDomainDocument(String tipo){
        try{
        ExtractorDomain m = new ExtractorDomain();
        List<GeoDocument> listGeoDoc = new ArrayList<>();
        if(tipo.equals("sql")){
            //listGeoDoc = geoDomainDocDao.selectAllGeoDocument("*", LIMIT.toString(), OFFSET.toString());
            listGeoDoc = geoDomainDocDao.selectGeoDocuments("*",LIMIT,OFFSET);
        }
        Integer i = 0;
        for (GeoDocument geoDoc : listGeoDoc) {
             i++;
             //TENTA DI ESTRARRE IL DOMINIO HOST DELL'INDIRIZZO URL
             try{
                 String domain = m.getDomainName(geoDoc.getUrl().toString());                          
                 SystemLog.message("(" + i + ")" + "DOMAIN:" + domain);
                 if(!listFinalDomains.contains(domain)){
                   m.applyTheMemorizeRecordCordinatesRules(domain,geoDoc,tipo);  
                 }
                 //*********************************************************************************       
             } catch (URISyntaxException ex) {
                 SystemLog.exception(ex);
             }
         }//for
      } catch (RuntimeException e2) {
              SystemLog.exception(e2);
              e2.printStackTrace();                                      
      } finally{
            //MOSTRIAMO I NOSTRI DepositFrequencyInfo CON SUFFICIENTE VALORE DI SOGLIA DA INSERIRE NEL DATABASE
            for (DepositFrequencyInfo dfi2 : listDepositFrequency) {
                if(dfi2.getFrequency()>=FREQUENZA_INTERVALLO_URL){
                 SystemLog.message("CONTROL:" + dfi2.toString());
                }
            }
            listDomains = null;
            listFinalDomains = null;
            listDepositFrequency = null;
      }
    }
        
   /**
    * Nuova regola per il tracciamento della frequenza si basa sugli oggetti java
    * DepositFrequencyInfo  e su quali sono i parametri dei relativi InfoDocument
    * più frequenti per un certo limite di soglia.
    * @param domain il dominio org.p4535992.mvc.webapp a cui appartiene il campo url del geoDocument in analisi
    * @param geoDoc il geodocument in analisi
    */
    private void applyTheMemorizeRecordCordinatesRules(String domain,GeoDocument geoDoc,String tipo){
        try{                        
        //Se è la prima volta che appare un GeoDocument relativo a questo particalore dominio org.p4535992.mvc.webapp
        if(!listDomains.contains(domain)){
            List<GeoDocument> lgd = new ArrayList<>();
            lgd.add(geoDoc); 
            //Crea un nuovo DepositFreqencyInfo
            DepositFrequencyInfo dfi =new DepositFrequencyInfo(domain,lgd,0);
            listDomains.add(domain);
            listDepositFrequency.add(dfi);
             
        //Se il Dominio org.p4535992.mvc.webapp di questo geoDocument è già presente
        }else if(listDomains.contains(domain)){
            for (DepositFrequencyInfo dfi2 : listDepositFrequency) {
                //Se il dominio dell'url analizzato è lo stesso di quello già
                //presenta nella lista dei depositi di frequency allora immagazziniamo il record
                //e incrementiamo il valore della frequenza
                if(domain.contains(dfi2.getDomain()) || domain.equals(dfi2.getDomain())){                    
                    //Si memorizza il GeoDocument con tutti i suoi parametri 
                    //nel DepositFrequency relativo al dominio voluto
                   // Rimuove la prima occorrenza con quel dominio
                    //DepositFrequencyInfo dfi3 = dfi2;             
                    dfi2.getListGeoDoc().add(geoDoc);
                    //Incrementiamo di 1 il valore della frequenza
                    dfi2.setFrequency(dfi2.getFrequency()+1);
                    //listDepositFrequency.add(dfi3);
                    //listDepositFrequency.remove(dfi2); 
                    break;
                } 
            }//for
            //listDepositFrequency.;
        }//else if
        //PER OGNI DepositFrequencyInfo FINORA PRESENTE......
        for (DepositFrequencyInfo dfi2 : listDepositFrequency) {
            //...VERIFICA SE IL VALORE DI SOGLIA E' SUFFICIENTE E SE
            //IL SUO DOMINIO NON E' PRESENTE NELLA LISTA DEI DOMINI GIA' ANALIZZATI
            if(dfi2.getFrequency()>= FREQUENZA_INTERVALLO_URL && !(listFinalDomains.contains(dfi2.getDomain()))){
                listFinalDomains.add(dfi2.getDomain());
                try {
                    GeoDomainDocument geo = prepareTheDomainWebGeoDocumentWithMoreCommonParameter(dfi2);
                    geo.setUrl(new URL("http://"+dfi2.getDomain()));
                    //INSERIAMO I NOSTRI GEODOMAINDOCUMENT NELLA TABELLA DEL DATABASE
                    SystemLog.message("INSERIMENTO GEODOMAINDOCUMENT NELLA TABELLA");
                    if(Objects.equals(tipo, "sql")){
                        geoDomainDocDao.insertAndTrim(geo);
                    }
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ExtractorDomain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }//for
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * Metodo che crea il GeoDomainInfoDocument/GeoDomainDocument per il suddetto limite di 
     * InfoDocument/GeoDocument attraverso un meccanismo di scelta dei valori più diffusi per ogni singolo 
     * parametro di ogni singolo InfoDocument/GeoDocument appartenente allo stesso dominio.
     * @param dfi oggetto java DepositFrequencyInfo
     * @return il GeoDocument utilizzato per creare i GeoDomainDocument da inserirre nel database mySQL
     * @throws MalformedURLException 
     */
    private GeoDomainDocument prepareTheDomainWebGeoDocumentWithMoreCommonParameter(DepositFrequencyInfo dfi) throws MalformedURLException {
        String domain = dfi.getDomain();
        URL webDomain;
        if(domain.contains("www")){webDomain = new URL("http://"+domain);
        }else{webDomain = new URL("http://www."+domain);}
        GeoDomainDocument geo2 = new GeoDomainDocument(webDomain, null,null, null, null, null, null, null, null, null, null, null,null,null,null,null,null);
        //for each GeoDocument contains ont he relative DepositFrequencyInfo get the more common parameter for eahc field
        List<String> al = new ArrayList<>();
        geo2.setUrl(webDomain);       
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getRegione());}
        geo2.setRegione(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getProvincia());}
        geo2.setProvincia(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getCity());}
        geo2.setCity(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIndirizzo());}
        geo2.setIndirizzo(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIva());}
        geo2.setIva(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getEmail());}
        geo2.setEmail(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getTelefono());}
        geo2.setTelefono(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getFax());}
        geo2.setFax(StringKit.getMoreCommonParameter(al));
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getEdificio());}
        geo2.setEdificio(StringKit.getMoreCommonParameter(al));
        al.clear();
        
        //latitude and longitude neeed a conversion STRING-DOUBLE
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {
            String lat = String.valueOf(geoDoc.getLat());      
                
            if(setNullForEmptyString(lat)==null || lat.contains("null") || lat.contains("NULL") || lat.equals("0")){
                   geoDoc.setLat(null);                        
            } else{
                   geoDoc.setLat(Double.parseDouble(lat));                  
                   al.add(geoDoc.getLat().toString());     
            }
        }//for
        String lat2 = StringKit.getMoreCommonParameter(al);
         if(setNullForEmptyString(lat2)==null || lat2.contains("null") || lat2.contains("NULL")|| al.isEmpty()){
               geo2.setLatitude(null);
         } else{
              geo2.setLatitude(Double.parseDouble(lat2));
         }
         al.clear();
           
         for (GeoDocument geoDoc : dfi.getListGeoDoc()) {
            String lng = String.valueOf(geoDoc.getLng());      
                     
            if(setNullForEmptyString(lng)==null || lng.contains("null") || lng.contains("NULL")|| lng.equals("0")){
                   geoDoc.setLng(null);                        
            } else{
                   geoDoc.setLng(Double.parseDouble(lng));                  
                   al.add(geoDoc.getLng().toString());     
            }
        }//for
        String lng2 = StringKit.getMoreCommonParameter(al);
         if(setNullForEmptyString(lng2)==null || lng2.contains("null") || lng2.contains("NULL")|| al.isEmpty()){
               geo2.setLongitude(null);
         } else{
              geo2.setLongitude(Double.parseDouble(lng2));
         }
         al.clear();

        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getNazione());}
        geo2.setNazione(StringKit.getMoreCommonParameter(al));
        al.clear();
        
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getPostalCode());}
        geo2.setPostalCode(StringKit.getMoreCommonParameter(al));
        al.clear();
        
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIndirizzoNoCAP());}
        geo2.setIndirizzoNoCAP(StringKit.getMoreCommonParameter(al));
        al.clear();
        
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIndirizzoHasNumber());}
        geo2.setIndirizzoHasNumber(StringKit.getMoreCommonParameter(al));
        al.clear();

        return geo2;
    }

    
    /**
    * Setta a null se verifica che la stringa non è
    * nulla, non è vuota e non è composta da soli spaceToken (white space)
    * @param s stringa di input
    * @return  il valore della stringa se null o come è arrivata
    */
   private String setNullForEmptyString(String s){     
        if(s!=null && !s.isEmpty() && !s.trim().isEmpty()){return s;}
//        else if(s.contains("null")){return null;}
//        else if(s.contains("NULL")){return null;}
        else{return null;}
    } 
 
    /**
     * Semplice metodo che estare il domino org.p4535992.mvc.webapp di appartenenza dell'url analizzato
     * @param u url di ingresso in fromato stringa
     * @return il dominio org.p4535992.mvc.webapp dell'url in formato stringa
     * @throws URISyntaxException 
     */
   private String getDomainName(String u) throws URISyntaxException {     
          URI uri = new URI(u);
          return uri.getHost();
   }


    /**
     * Method for update all record with the Coordinates null or empty.
     */
    public void reloadNullCoordinates(){
        ManageJsonWithGoogleMaps j = ManageJsonWithGoogleMaps.getInstance();
        String[] columns_where = new String[]{"latitude","longitude"};
        Object[] values_where = new Object[]{null,null};
        try {
            List<GeoDomainDocument> listGeoDoc =
                    geoDomainDocDao.selectGeoDomainWihNoCoords(new String[]{"*"},columns_where, values_where, LIMIT, OFFSET, "AND");

            values_where = new Object[]{"",""};
            List<GeoDomainDocument> listGeoDoc2 =
                    geoDomainDocDao.selectGeoDomainWihNoCoords(new String[]{"*"},columns_where,values_where, LIMIT, OFFSET, "AND");
            listGeoDoc.addAll(listGeoDoc2);

            values_where = new Object[]{0,0};
            listGeoDoc2 = geoDomainDocDao.selectGeoDomainWihNoCoords(new String[]{"*"},columns_where,values_where, LIMIT, OFFSET, "AND");
            listGeoDoc.addAll(listGeoDoc2);
            for (GeoDomainDocument geo : listGeoDoc) {
                LatLng coord = j.getCoords(geo);
                if(coord.getLat() == 0 && coord.getLng()==0){
                    values_where = new Object[]{null,null};
                }else{
                    values_where = new Object[]{coord.getLat(),coord.getLng()};

                }
                geoDomainDocDao.update(columns_where, values_where, "url", geo.getUrl().toString().replace("http://",""));
            }
        }catch(URISyntaxException e){
            SystemLog.exception(e);
        }
    }

    public void deleteOverrideRecord(Map<String,String> map){
         String[] columns_where = new String[]{"url"};
         Object[] values_where;
        try {
            List<GeoDomainDocument> finalList = new ArrayList <>();
            for (Map.Entry<String,String> entry: map.entrySet()) {
                values_where = new Object[]{entry.getValue()};
                geoDomainDocDao.setTableSelect("geodomaindocument_h"); //464
                List<GeoDomainDocument> list = geoDomainDocDao.selectGeoDomainWihNoCoords(
                        new String[]{"*"},columns_where,values_where, 1, 0, null);
                GeoDomainDocument geo = list.get(0);
                geo.setDoc_id(Integer.parseInt(entry.getKey()));
                finalList.add(geo);
            }
            //geodomaindocument_coord_omogeneo_05052014,geodomaindocument_coord_omogeneo_120
            geoDomainDocDao.setTableInsert("geodomaindocument_coord_omogeneo_05052014"); //120
            for (GeoDomainDocument geoDoc : finalList) {
                //geoDoc.setUrl(new URL(geoDoc.getUrl().toString().replace("http://","")));
                geoDomainDocDao.insertAndTrim(geoDoc);
            }
        }catch(Exception e){
            SystemLog.exception(e);
        }

    }

    public void deleteOverrideRecord(String[] columns_where,Object[] values_where){
        try {
            geoDomainDocDao.setTableSelect("geodomaindocument_h"); //464
            List<GeoDomainDocument> list = geoDomainDocDao.selectGeoDomainWihNoCoords(
                    new String[]{"*"},columns_where,values_where, 1, 0, null);
            //geodomaindocument_coord_omogeneo_05052014,geodomaindocument_coord_omogeneo_120
            geoDomainDocDao.setTableInsert("geodomaindocument_coord_omogeneo_05052014"); //120
        }catch(Exception e){
            SystemLog.exception(e);
        }

    }
}
