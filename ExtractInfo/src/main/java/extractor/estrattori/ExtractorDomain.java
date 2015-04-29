package extractor.estrattori;

import extractor.ManageJsonWithGoogleMaps;
import object.support.DepositFrequencyInfo;
import object.model.GeoDocument;
import p4535992.util.log.SystemLog;
import object.impl.jdbc.GeoDomainDocumentDaoImpl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import object.support.LatLng;
import org.hibernate.SessionFactory;
/**
 * MainEstrazioneGeoDomainDocumentPerElaborato.java
 * Classe per lt'estrazione dei GeoDocument o InfoDocument relativi ai singoli domni web
 * attraverso un'analisi dei singoli GeoDocument e InfoDocument dei singoli URL segunedo
 * opportuni criteri di scelta e confronto.
 * @author Tenti Marco
 */
public class ExtractorDomain {
    
    //FREQUENZA DEGLI URL PER L'IDENTIFICAZIONE DEL DOMINIO
    private static Integer FREQUENZA_INTERVALLO_URL;
    //TABELLA DI INPUT GEODOCUMENT DA CONVERTIRE IN GEODOMAINDOCUMENT
    private static String TABLE_INPUT_GEODOMAIN; 
    //TABELLA DI OUTPUT GEODOMAINDOCUMENT
    private static String TABLE_OUTPUT_GEODOMAIN; 
    
    //NOME USER PER L'HOST MYSQL
    private static String USER;
    //PASSWORD PER L'HOST MYSQL
    private static String PASS;
    //DATABASE DELLA TABELLA DI INPUT
    private static String DB_INPUT_GEODOMAIN;  
    //DATABASE DELLA TABELLA DI OUTPUT
    private static String DB_OUTPUT_GEODOMAIN;
    
    private static Integer LIMIT;
    private static Integer OFFSET;

    private static SessionFactory factory = null;
    private static GeoDomainDocumentDaoImpl geoDomainDocDao;
    
    public ExtractorDomain(){}

     public ExtractorDomain(
             String USER, String PASS,
             Integer LIMIT, Integer OFFSET, Integer FREQUENZA_INTERVALLO_URL,
             String TABLE_INPUT_GEODOMAIN, String TABLE_OUTPUT_GEODOMAIN,
             String DB_INPUT_GEODOMAIN, String DB_OUTPUT_GEODOMAIN
     ){

        this.USER = USER;
        this.PASS = PASS;
        this.LIMIT = LIMIT;
        this.OFFSET = OFFSET;
        this.FREQUENZA_INTERVALLO_URL = FREQUENZA_INTERVALLO_URL;
        this.TABLE_INPUT_GEODOMAIN = TABLE_INPUT_GEODOMAIN;
        this.TABLE_OUTPUT_GEODOMAIN = TABLE_OUTPUT_GEODOMAIN;
        this.DB_INPUT_GEODOMAIN = DB_INPUT_GEODOMAIN;
        this.DB_OUTPUT_GEODOMAIN= DB_OUTPUT_GEODOMAIN;

    }

    public ExtractorDomain(GeoDomainDocumentDaoImpl dao, Integer LIMIT, Integer OFFSET, Integer FREQUENZA_INTERVALLO_URL
    ){
        this.LIMIT = LIMIT;
        this.OFFSET = OFFSET;
        this.FREQUENZA_INTERVALLO_URL = FREQUENZA_INTERVALLO_URL;
        this.geoDomainDocDao = dao;
    }
    //***********************************************************************************************************
    private static ArrayList<String> listDomains = new ArrayList<String>();
    private static ArrayList<String> listFinalDomains = new ArrayList<String>();
    private static ArrayList<DepositFrequencyInfo> listDepositFrequency = new ArrayList<DepositFrequencyInfo>();
  
    public void CreateTableOfGeoDomainDocument(String tipo){
        try{
        ExtractorDomain m = new ExtractorDomain();
        List<GeoDocument> listGeoDoc = new ArrayList<>();
        if(tipo == "sql"){
            //listGeoDoc = geoDomainDocDao.selectAllGeoDocument("*", LIMIT.toString(), OFFSET.toString());
            listGeoDoc = geoDomainDocDao.selectGeoDocuments("*",LIMIT,OFFSET);
//        }else if(tipo == "hibernate_pojo"){
//
//            List<GeoDocument> listGeoDoc2 = geoDomainDocDao.getAllH(LIMIT.toString(), OFFSET.toString());
//            for(GeoDocument geo: listGeoDoc2){listGeoDoc.add(geo);}
//            listGeoDoc2.clear();
//
//         }else if(tipo == "hibernate_jira"){
//            List<GeoDocument> listGeoDoc2 = geoDomainDocDao.getAllH(LIMIT.toString(), OFFSET.toString());
//            for(GeoDocument geo: listGeoDoc2){listGeoDoc.add(geo);}
//            listGeoDoc2.clear();
//        }else{
//            new Exception("ERRORE IN FASE DI CREAZIONE DEI GEODOMAINDOCUMENT");
        }
        Integer i = 0;
        for (GeoDocument geoDoc : listGeoDoc) {
             i++;
             //TENTA DI ESTRARRE IL DOMINIO HOST DELL'INDIRIZZO URL
             try{                                                  
                 String domain = m.getDomainName(geoDoc.getUrl().toString());                          
                 SystemLog.message("(" + i + ")" + "DOMAIN:" + domain);
                 if(listFinalDomains.contains(domain)==false){                                 
                   m.applyTheMemorizeRecordCordinatesRules(domain,geoDoc,tipo);  
                 }
                 //*********************************************************************************       
           } catch (URISyntaxException ex) {
                continue;
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
            //listFrequencyInfo = null;
            listDepositFrequency = null;
      }
    }
        
   /**
    * Nuova regola per il tracciamento della frequenza si basa sugli oggetti java
    * DepositFrequencyInfo  e su quali sono i parametri dei relativi InfoDocument
    * più frequenti per un certo limite di soglia.
    * @param domain il dominio web a cui appartiene il campo url del geoDocument in analisi
    * @param geoDoc il geodocument in analisi
    */
    private void applyTheMemorizeRecordCordinatesRules(String domain,GeoDocument geoDoc,String tipo){
        try{                        
        //Se è la prima volta che appare un GeoDocument relativo a questo particalore dominio web
        if(listDomains.contains(domain)==false){  
            ArrayList<GeoDocument> lgd = new ArrayList<GeoDocument>();                                       
            lgd.add(geoDoc); 
            //Crea un nuovo DepositFreqencyInfo
            DepositFrequencyInfo dfi =new DepositFrequencyInfo(domain,lgd,0);
            listDomains.add(domain);
            listDepositFrequency.add(dfi);
             
        //Se il Dominio web di questo geoDocument è già presente
        }else if(listDomains.contains(domain)==true){
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
                    GeoDocument geo = prepareTheDomainWebGeoDocumentWithMoreCommonParameter(dfi2);
                    geo.setUrl(new URL("http://"+dfi2.getDomain()));
                    //INSERIAMO I NOSTRI GEODOMAINDOCUMENT NELLA TABELLA DEL DATABASE
                    SystemLog.message("INSERIMENTO GEODOMAINDOCUMENT NELLA TABELLA");
                    if(tipo == "sql"){
                        geoDomainDocDao.insertAndTrim(geo);
//                    }else if(tipo == "hibernate_pojo"){
//
//                        geoDomainDocDao.saveH(new GeoDomainDocument(
//                                geo.getUrl(), geo.getRegione(), geo.getProvincia(), geo.getCity(), geo.getIndirizzo(), geo.getIva(),
//                                geo.getEmail(), geo.getTelefono(), geo.getFax(), geo.getEdificio(),
//                                geo.getLat(), geo.getLng(), geo.getNazione(), geo.getDescription(), geo.getIndirizzoNoCAP(),
//                                geo.getPostalCode(), geo.getIndirizzoHasNumber()
//                        ));
//                    }else if(tipo == "hibernate_jira"){
//
//                        geoDomainDocDao.saveH(new GeoDomainDocument(
//                                geo.getUrl(), geo.getRegione(), geo.getProvincia(), geo.getCity(), geo.getIndirizzo(), geo.getIva(),
//                                geo.getEmail(), geo.getTelefono(), geo.getFax(), geo.getEdificio(),
//                                geo.getLat(), geo.getLng(), geo.getNazione(), geo.getDescription(), geo.getIndirizzoNoCAP(),
//                                geo.getPostalCode(), geo.getIndirizzoHasNumber()
//                        ));
                    }
                    
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ExtractorDomain.class.getName()).log(Level.SEVERE, null, ex);
                }finally{
                    
                }
            }
        }//for
        }catch(Exception e){ e.printStackTrace();
        }finally{

        }//finally
        
    }
    
    /**
     * Metodo che crea il GeoDomainInfoDocument/GeoDomainDocument per il suddetto limite di 
     * InfoDocument/GeoDocument attraverso un meccanismo di scelta dei valori più diffusi per ogni singolo 
     * parametro di ogni singolo InfoDocument/GeoDocument appartenente allo stesso dominio.
     * @param dfi oggetto java DepositFrequencyInfo
     * @return il GeoDocument utilizzato per creare i GeoDomainDocument da inserirre nel database mySQL
     * @throws MalformedURLException 
     */
    private GeoDocument prepareTheDomainWebGeoDocumentWithMoreCommonParameter(DepositFrequencyInfo dfi) throws MalformedURLException {
        //URL webDomain = new URL("http://www."+dfi.getDomain());
        //IDENTIFICHIAMO IL DOMINIO WEB SU CUI LAVORARE
        String domain = dfi.getDomain();
        URL webDomain = null;
        if(domain.contains("www")){webDomain = new URL("http://"+domain);
        }else{webDomain = new URL("http://www."+domain);}
        GeoDocument geo2 = new GeoDocument(webDomain, null,null, null, null, null, null, null, null, null, null, null,null,null,null,null,null);
        //PER OGNI PARAMETRO DI OGNI GEODOCUMENT DELLA LISTA PRESENTE NEL RELATIVO
        //DepositFrequencyInfo PRENDIAMO IL VALORE PIU' FREQUENTE O DIFFUSO ENTRO IL LIMITE
        //PRESTABILITO DI URL DA ANALIZZARE PER TALE DOMINIO WEB
        ArrayList<String> al = new ArrayList<String>();
        //A seconda di quello che vogliamo nel campo dominio si può specificare il protocollo http o no        
        geo2.setUrl(webDomain);       
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getRegione());}
        geo2.setRegione(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getProvincia());}
        geo2.setProvincia(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getCity());}
        geo2.setCity(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIndirizzo());}
        geo2.setIndirizzo(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIva());}
        geo2.setIva(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getEmail());}
        geo2.setEmail(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getTelefono());}
        geo2.setTelefono(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getFax());}
        geo2.setFax(getMoreCommonParameter(al)); 
        al.clear();
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getEdificio());}
        geo2.setEdificio(getMoreCommonParameter(al)); 
        al.clear();
        
        //LATITUDE E LONGITUDE NECESSITANO DI UN CONTROLLO PER LA CONVERSIONE STRING-DOUBLE
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {
            String lat = String.valueOf(geoDoc.getLat());      
                
            if(setNullForEmptyString(lat)==null || lat.contains("null") || lat.contains("NULL")){
                   geoDoc.setLat(null);                        
            } else{
                   geoDoc.setLat(Double.parseDouble(lat));                  
                   al.add(geoDoc.getLat().toString());     
            }
        }//for
        String lat2 = getMoreCommonParameter(al);
         if(setNullForEmptyString(lat2)==null || lat2.contains("null") || lat2.contains("NULL")|| al.isEmpty()){
               geo2.setLat(null); 
         } else{
              geo2.setLat(Double.parseDouble(lat2)); 
         }
         //System.out.println("LATITUDE:"+geo2.getLat());
         al.clear();
           
         for (GeoDocument geoDoc : dfi.getListGeoDoc()) {
            String lng = String.valueOf(geoDoc.getLng());      
                     
            if(setNullForEmptyString(lng)==null || lng.contains("null") || lng.contains("NULL")){
                   geoDoc.setLng(null);                        
            } else{
                   geoDoc.setLng(Double.parseDouble(lng));                  
                   al.add(geoDoc.getLng().toString());     
            }
        }//for
        String lng2 = getMoreCommonParameter(al);
         if(setNullForEmptyString(lng2)==null || lng2.contains("null") || lng2.contains("NULL")|| al.isEmpty()){
               geo2.setLng(null); 
         } else{
              geo2.setLng(Double.parseDouble(lng2)); 
         }
         //System.out.println("LONGITUDE:"+geo2.getLng());
         al.clear();
        
        //CONTROLLI PER CAMPI MENO IMPORTANTI
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getNazione());}
        geo2.setNazione(getMoreCommonParameter(al)); 
        al.clear();
        
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getPostalCode());}
        geo2.setPostalCode(getMoreCommonParameter(al)); 
        al.clear();
        
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIndirizzoNoCAP());}
        geo2.setIndirizzoNoCAP(getMoreCommonParameter(al)); 
        al.clear();
        
        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getIndirizzoHasNumber());}
        geo2.setIndirizzoHasNumber(getMoreCommonParameter(al)); 
        al.clear();
        
//        for (GeoDocument geoDoc : dfi.getListGeoDoc()) {al.add(geoDoc.getDescription());}
//        geo2.setDescription(getMoreCommonParameter(al)); 
//        al.clear();
        return geo2;
//          geo2.setRegione(setMoreCommonParameter(al, dfi,"regione"));
    }
    
    //METODI DI SUPPORTO
    
    /**
     * Metodo che assegna attraverso un meccanismo di "mapping" ad ogni valore 
     * disitno del parametro in questione un numero (la frequenza) prendeno il 
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
     * Semplice metodo che estare il domino web di appartenenza dell'url analizzato
     * @param u url di ingresso in fromato stringa
     * @return il dominio web dell'url in formato stringa
     * @throws URISyntaxException 
     */
   private String getDomainName(String u) throws URISyntaxException {     
          URI uri = new URI(u);
          String domain = uri.getHost();
          //return domain.startsWith("www.") ? domain.substring(4) : domain;
          return domain;
   }


    public void reloadNullCoordinates(){

        ManageJsonWithGoogleMaps j = new ManageJsonWithGoogleMaps();
        String[] columns = new String[]{"latitude","longitude"};
        Object[] values = new Object[]{null,null};
        try {
            List<GeoDocument> listGeoDoc = geoDomainDocDao.selectGeoDomainWihNoCoords(columns, values, LIMIT, OFFSET, "AND");
            values = new Object[]{"",""};
            List<GeoDocument> listGeoDoc2 = geoDomainDocDao.selectGeoDomainWihNoCoords(columns, values, LIMIT, OFFSET, "AND");
            listGeoDoc.addAll(listGeoDoc2);
            values = new Object[]{0,0};
            listGeoDoc2 = geoDomainDocDao.selectGeoDomainWihNoCoords(columns, values, LIMIT, OFFSET, "AND");
            listGeoDoc.addAll(listGeoDoc2);
            for (GeoDocument geo : listGeoDoc) {
                LatLng coord = j.getCoords(geo);
                if(coord.getLat() == 0 && coord.getLng()==0){
                    values = new Object[]{null,null};
                }else{
                    values = new Object[]{coord.getLat(),coord.getLng()};

                }
                geoDomainDocDao.update(columns, values, "url", geo.getUrl().toString().replace("http://",""));
            }
        }catch(URISyntaxException e){
            e.printStackTrace();
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
    }

    public void deleteOverrideRecord(Map<String,String> map){
         String[] columns = new String[]{"url"};
         Object[] values;
        try {
            List<GeoDocument> finalList = new ArrayList <>();
            for (Map.Entry<String,String> entry: map.entrySet()) {
                values = new Object[]{entry.getValue()};
                geoDomainDocDao.setTableSelect("geodomaindocument_h"); //464
                List<GeoDocument> list = geoDomainDocDao.selectGeoDomainWihNoCoords(columns, values, 1, 0, null);
                GeoDocument geo = list.get(0);
                geo.setDoc_id(Integer.parseInt(entry.getKey()));
                finalList.add(geo);
            }
            geoDomainDocDao.setTableInsert("geodomaindocument_coord_omogeneo_120"); //120
            for (GeoDocument geoDoc : finalList) {
                //geoDoc.setUrl(new URL(geoDoc.getUrl().toString().replace("http://","")));
                geoDomainDocDao.insertAndTrim(geoDoc);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

   
   /*
     public static void main(String args[]){
      EventQueue.invokeLater(new Runnable() {
         public void run() {       
                try{   
                   MainEstrazioneGeoDomainDocumentPerElaborato m = new MainEstrazioneGeoDomainDocumentPerElaborato();
                   ArrayList<GeoDocument> listGeoDoc = new ArrayList<GeoDocument>();
                   c.openConnection(DB_INPUT_GEODOMAIN,USER,PASS);               
                   listGeoDoc = c.getInfoDocumentByGeolocationDB(TABLE_INPUT_GEODOMAIN,LIMIT,OFFSET);
                   c.closeConnection();   
                   Integer i = 0;
                   for (GeoDocument geoDoc : listGeoDoc) {
                        System.out.println("("+i+")"+geoDoc.getUrl().toString());
                        i++;
                        //TENTA DI ESTRARRE IL DOMINIO HOST DELL'INDIRIZZO URL
                     
                        try{                                                  
                            String domain = m.getDomainName(geoDoc.getUrl().toString());                          
                            System.out.println("DOMAIN:"+domain);
                            
                            //MODALITA 1:TRACCIAMENTO DELLA FREQUENZA CHE SI BASA UNICAMENTE SULL'UGUAGLIANZA 
                            //DELLE COORDINATE GEOGRAFICHE RICAVATE ATTRAVERSO L'API GOOGLE MAPS DA 
                            //URL RELATIVI AL MEDESIMO DOMINIO. (CREA I GEODOCUMENT PER IL DATABASE)
                            //********************************************************************************
                            //FrequencyInfo frequencyInfo = new FrequencyInfo(null,null,null,null,null); 
                            //m.applyTheFrequencyCoordinatesRules(geoDoc,domain,frequencyInfo);  
                            //******************************************************************************++
                            
                            //MODALITA 2:TRACCIAMENTO DELLA FREQUENZA CHE SI BASA SU UNA SOGLIA MOLTO AMPIA
                            //PER POTER FARE UN'ANALISI STATISTICA E PRENDERE I SINGOLI VALORI PIU' DIFFUSI
                            //(INDIRIZZO,EDIFICIO,ECC.) NEL GRUPPO DEGLI URL (CON SOGLIA DA DEFINIRE) RELATIVI
                            //AL MEDESIMO DOMINIO WEB A CUI VENGONO ASSEGNATI TALI VALORI COME PARAMETRI 
                            //DEL GEODOCUMENT RELATIVO AL DOMINIO WEB STESSO. (CREA GLI INFODOCUMENT PER IL DATABASE)
                            //*********************************************************************************   
                            if(listFinalDomains.contains(domain)==false){                                 
                              m.applyTheMemorizeRecordCordinatesRules(domain,geoDoc);  
                            }
                            //*********************************************************************************       
                      } catch (URISyntaxException ex) {
                           //Logger.getLogger(MainEstrazioneGeoDomainDocumentPerElaborato.class.getName()).log(Level.SEVERE, null, ex);
                           continue;
                      }
                    }//for
                 } catch (RuntimeException e2) {
                         System.out.println("ECCEZIONE DI QUALCHE TIPO CAUSATA IN FASE DI RUN");
                         e2.printStackTrace();                                      
                 } finally{
                    //c.closeConnection(); 
                    //MOSTRIAMO I NOSTRI DepositFrequencyInfo CON SUFFICIENTE VALORE DI SOGLIA DA INSERIRE NEL DATABASE
                    for (DepositFrequencyInfo dfi2 : listDepositFrequency) {
                        if(dfi2.getFrequency()>=FREQUENZA_INTERVALLO_URL){
                         System.out.println("CONTROL:"+dfi2.toString());
                        }
                        //System.out.println("CONTROL:"+dfi2.toString());
                    }
                    listDomains = null;
                    listFinalDomains = null;
                    //listFrequencyInfo = null;
                    listDepositFrequency = null;
                 }
                
          }//run                 
        });//runnable     
      }//main  
   */
}
