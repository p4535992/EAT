package extractor.setInfoParameterIta;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 *
 * @author Marco
 */
public class SetCodicePostale {
    
    private static Map<String,SetCodicePostale> lcp = new HashMap<String,SetCodicePostale>(); 
    private int identificativoComune;  
    private String capInizialeComune;
    private String capFinaleComune;
   
    public SetCodicePostale(int identificativoComune,String capInizialeComune,String capFinaleComune){       
        this.identificativoComune = identificativoComune;
        this.capInizialeComune = capInizialeComune;
        this.capFinaleComune = capFinaleComune;                     
    }
    public SetCodicePostale(){        
        // <editor-fold defaultstate="collapsed" desc="LISTA COMUNI">
        lcp.put("Cagliari",new SetCodicePostale(13,"09010","09170"));
        lcp.put("Villacidro",new SetCodicePostale(16,"09010","09170"));
        lcp.put("Rimini",new SetCodicePostale(64,"47010","47920"));
        lcp.put("Parma",new SetCodicePostale(59,"43010","43120"));
        lcp.put("Mantova",new SetCodicePostale(62,"46010","46100"));
        lcp.put("Cremona",new SetCodicePostale(38,"26010","26900"));
        lcp.put("Varese",new SetCodicePostale(32,"21010","21100"));
        lcp.put("Udine",new SetCodicePostale(47,"33010","33170"));
        lcp.put("Trani",new SetCodicePostale(93,"76011","76125"));
        lcp.put("Agrigento",new SetCodicePostale(109,"92010","92100"));
        lcp.put("Cuneo",new SetCodicePostale(21,"12010","12100"));
        lcp.put("Oristano",new SetCodicePostale(14,"09010","09170"));
        lcp.put("Benevento",new SetCodicePostale(96,"82010","82100"));
        lcp.put("Frosinone",new SetCodicePostale(3,"03010","03100"));
        lcp.put("Messina",new SetCodicePostale(115,"98020","98100"));
        lcp.put("Salerno",new SetCodicePostale(98,"84010","84100"));
        lcp.put("Fermo",new SetCodicePostale(80,"63061","63900"));
        lcp.put("Matera",new SetCodicePostale(90,"75010","75100"));
        lcp.put("Andria",new SetCodicePostale(91,"76011","76125"));
        lcp.put("Ancona",new SetCodicePostale(76,"60010","60100"));
        lcp.put("Verbania",new SetCodicePostale(42,"28010","28920"));
        lcp.put("Pesaro",new SetCodicePostale(77,"61010","61120"));
        lcp.put("Treviso",new SetCodicePostale(45,"31010","31100"));
        lcp.put("Trapani",new SetCodicePostale(108,"91010","91100"));
        lcp.put("Brindisi",new SetCodicePostale(87,"72012","72100"));
        lcp.put("Carbonia",new SetCodicePostale(17,"09010","09170"));
        lcp.put("Lecce",new SetCodicePostale(88,"73010","73100"));
        lcp.put("Foggia",new SetCodicePostale(86,"71010","71120"));
        lcp.put("Sondrio",new SetCodicePostale(34,"23010","23900"));
        lcp.put("Perugia",new SetCodicePostale(6,"06010","06100"));
        lcp.put("Belluno",new SetCodicePostale(46,"32010","32100"));
        lcp.put("Potenza",new SetCodicePostale(99,"85010","85100"));
        lcp.put("Piacenza",new SetCodicePostale(43,"29010","29120"));
        lcp.put("Bologna",new SetCodicePostale(56,"40010","40100"));
        lcp.put("Vercelli",new SetCodicePostale(22,"13010","13900"));
        lcp.put("Arezzo",new SetCodicePostale(68,"52010","52100"));
        lcp.put("Roma",new SetCodicePostale(0,"00010","00100"));
        lcp.put("Rome",new SetCodicePostale(0,"00010","00100"));
        lcp.put("Firenze",new SetCodicePostale(66,"50012","50100"));
        lcp.put("Florence",new SetCodicePostale(66,"50012","50100"));
        lcp.put("Iglesias",new SetCodicePostale(18,"09010","09170"));
        lcp.put("Lecco",new SetCodicePostale(35,"23010","23900"));
        lcp.put("Milano",new SetCodicePostale(30,"20010","20900"));
        lcp.put("Isernia",new SetCodicePostale(101,"86010","86170"));
        lcp.put("Palermo",new SetCodicePostale(107,"90010","90100"));
        lcp.put("Bari",new SetCodicePostale(85,"70010","70100"));
        lcp.put("Brescia",new SetCodicePostale(37,"25010","25100"));
        lcp.put("Rovigo",new SetCodicePostale(61,"45010","45100"));
        lcp.put("Siena",new SetCodicePostale(69,"53011","53100"));
        lcp.put("Barletta",new SetCodicePostale(92,"76011","76125"));
        lcp.put("Ferrara",new SetCodicePostale(60,"44011","44120"));
        lcp.put("Crotone",new SetCodicePostale(104,"88020","88900"));
        lcp.put("Massa",new SetCodicePostale(70,"54010","54100"));
        lcp.put("Cosenza",new SetCodicePostale(102,"87010","87100"));
        lcp.put("Ascoli Piceno",new SetCodicePostale(79,"63061","63900"));
        lcp.put("Como",new SetCodicePostale(33,"22010","22100"));
        lcp.put("Reggio Calabria",new SetCodicePostale(105,"89010","89900"));
        lcp.put("Alessandria",new SetCodicePostale(25,"15010","15120"));
        lcp.put("Lucca",new SetCodicePostale(71,"55011","55100"));
        lcp.put("Sassari",new SetCodicePostale(7,"07010","07100"));
        lcp.put("Genova",new SetCodicePostale(26,"16010","16100"));
        lcp.put("Vicenza",new SetCodicePostale(52,"36010","36100"));
        lcp.put("Bergamo",new SetCodicePostale(36,"24010","24100"));
        lcp.put("Chieti",new SetCodicePostale(83,"66010","66100"));
        lcp.put("Avellino",new SetCodicePostale(97,"83010","83100"));
        lcp.put("Livorno",new SetCodicePostale(73,"57014","57100"));
        lcp.put("Venezia",new SetCodicePostale(44,"30010","30100"));
        lcp.put("La Spezia",new SetCodicePostale(29,"19010","19100"));
        lcp.put("Imperia",new SetCodicePostale(28,"18010","18100"));
        lcp.put("Latina",new SetCodicePostale(4,"04010","04100"));
        lcp.put("Napoli",new SetCodicePostale(94,"80010","80100"));
        lcp.put("Enna",new SetCodicePostale(111,"94010","94100"));
        lcp.put("Verona",new SetCodicePostale(53,"37010","37100"));
        lcp.put("Tempio Pausania",new SetCodicePostale(9,"07010","07100"));
        lcp.put("Asti",new SetCodicePostale(24,"14010","14100"));
        lcp.put("Bolzano",new SetCodicePostale(55,"39010","39100"));
        lcp.put("Caserta",new SetCodicePostale(95,"81010","81100"));
        lcp.put("Pescara",new SetCodicePostale(82,"65010","65120"));
        lcp.put("Caltanissetta",new SetCodicePostale(110,"93010","93100"));
        lcp.put("Prato",new SetCodicePostale(75,"59013","59100"));
        lcp.put("Nuoro",new SetCodicePostale(10,"08010","08100"));
        lcp.put("Trieste",new SetCodicePostale(50,"34010","34170"));
        lcp.put("Tortolì",new SetCodicePostale(11,"08010","08100"));
        lcp.put("Macerata",new SetCodicePostale(78,"62010","62100"));
        lcp.put("Campobasso",new SetCodicePostale(100,"86010","86170"));
        lcp.put("Vibo Valentia",new SetCodicePostale(106,"89010","89900"));
        lcp.put("Pavia",new SetCodicePostale(40,"27010","27100"));
        lcp.put("Terni",new SetCodicePostale(5,"05010","05100"));
        lcp.put("Torino",new SetCodicePostale(19,"10010","10100"));
        lcp.put("Forlì",new SetCodicePostale(63,"47010","47920"));
        lcp.put("Trento",new SetCodicePostale(54,"38010","38120"));
        lcp.put("Padova",new SetCodicePostale(51,"35010","35100"));
        lcp.put("Grosseto",new SetCodicePostale(74,"58010","58100"));
        lcp.put("Taranto",new SetCodicePostale(89,"74010","74120"));
        lcp.put("Viterbo",new SetCodicePostale(1,"01010","01100"));
        lcp.put("Olbia",new SetCodicePostale(8,"07010","07100"));
        lcp.put("Savona",new SetCodicePostale(27,"17010","17100"));
        lcp.put("L'Aquila",new SetCodicePostale(84,"67010","67100"));       
        lcp.put("Catanzaro",new SetCodicePostale(103,"88020","88900"));
        lcp.put("Lanusei",new SetCodicePostale(12,"08010","08100"));
        lcp.put("Pisa",new SetCodicePostale(72,"56010","56120"));
        lcp.put("Pordenone",new SetCodicePostale(48,"33010","33170"));
        lcp.put("Teramo",new SetCodicePostale(81,"64010","64100"));
        lcp.put("Pistoia",new SetCodicePostale(67,"51010","51100"));
        lcp.put("Sanluri",new SetCodicePostale(15,"09010","09170"));
        lcp.put("Catania",new SetCodicePostale(112,"95010","95100"));
        lcp.put("Biella",new SetCodicePostale(23,"13010","13900"));
        lcp.put("Ragusa",new SetCodicePostale(114,"97010","97100"));
        lcp.put("Novara",new SetCodicePostale(41,"28010","28920"));
        lcp.put("Monza",new SetCodicePostale(31,"20010","20900"));
        lcp.put("Rieti",new SetCodicePostale(2,"02010","02100"));
        lcp.put("Lodi",new SetCodicePostale(39,"26010","26900"));
        lcp.put("Modena",new SetCodicePostale(57,"41011","41120"));
        lcp.put("Siracusa",new SetCodicePostale(113,"96010","96100"));
        lcp.put("Ravenna",new SetCodicePostale(65,"48010","48120"));
        lcp.put("Aosta",new SetCodicePostale(20,"11010","11100"));
        lcp.put("Gorizia",new SetCodicePostale(49,"34010","34170"));
        lcp.put("Reggio Emilia",new SetCodicePostale(58,"42010","42120"));
      //</editor-fold> 

    }//constructor
    
    public String checkPostalCodeByCitta(String citta){
        String codicePostale = null;
        try{   
         for ( Map.Entry<String, SetCodicePostale> entry : lcp.entrySet()) { 
            String city = entry.getKey();
            int idComune = entry.getValue().getIdentificativoComune();
            //String capInitComune = entry.getValue().getCapInizialeComune();
            String capFinComune = entry.getValue().getCapFinaleComune();
                       
            if(citta.replaceAll("\\s+","").equalsIgnoreCase(city.replaceAll("\\s+",""))){              
                    codicePostale = capFinComune;                 
                break;
            }                             
        }//for       
        //map.clear();
        }catch(java.lang.NullPointerException ne){codicePostale=null;}
         return codicePostale;
    }
    
    
    
    public String GetPostalCodeByIndirizzo(String indirizzo){
        String result ="";
        try{
        String expression = "\\d{5,7}";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(indirizzo);
        while(matcher.find()){
             result = matcher.group().toString();   
             if(result != null && result != ""){break;}
        }
        }catch(NullPointerException ne){result = null;}
        return result;
    }
    
    public String GetNumberByIndirizzo(String indirizzo){
        String result ="";
        try{
        String expression = "(\\d{1,3})(((([^a-zA-Z\\d\\s:]{0,1})(\\d{1,3}))?)([a-zA-Z]{0,1})?)?";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(indirizzo);
        while(matcher.find()){
             result = matcher.group().toString();   
             if(result != null && result != ""){break;}
        }
        }catch(NullPointerException ne){result = null;}
        return result;
    }
    
    private String removeCapOnAddress(String indirizzo){
         String newIndirizzo = indirizzo.replaceAll("\\d{5,6}", "").replace("-", "").trim();
         return newIndirizzo;
    }
       
    public int getIdentificativoComune() {
        return identificativoComune;
    }

    public String getCapInizialeComune() {
        return capInizialeComune;
    }

    public String getCapFinaleComune() {
        return capFinaleComune;
    }  

    public void setIdentificativoComune(int identificativoComune) {
        this.identificativoComune = identificativoComune;
    }

    private String newCodicePostale;
    private String newIndirizzo;

    public String getNewCodicePostale() {
        return newCodicePostale;
    }

    public String getNewIndirizzo() {
        return newIndirizzo;
    }
    
    public void checkPostalCodeByIndirizzo(String indirizzoConCAP){
        newCodicePostale = GetPostalCodeByIndirizzo(indirizzoConCAP);
        newIndirizzo = removeCapOnAddress(indirizzoConCAP);
    }
   
    
    //METODI
    private ArrayList<String> GetPostalCOdeByWeb(String indirizzo){
        ArrayList<String> result =new ArrayList<String>();
        String expression = "\\d{3,5}";       
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(indirizzo);
        while(matcher.find()){
             result.add(matcher.group().toString().replace("x","0"));   
             //if(result != null && result != ""){break;}   
            
        }
        return result;
    }
    
    private ArrayList<String> GetCityByWeb(String indirizzo){
        ArrayList<String> result =new ArrayList<String>();
        String expression = "\\((.*?)\\)";
        String m = "";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(indirizzo);
        while(matcher.find()){
             m = matcher.group().toString();   
             if(m !=null && m != ""){break;}          
        }
        String[] l = m.split(",");
        for(int i=0; i < l.length; i++){
            result.add(l[i].replace("(", "").replace(")", ""));
        }
        return result;
    }

    @Override
    public String toString() {
        return ""+identificativoComune+ ",\"" + capInizialeComune + "\",\""+capFinaleComune+"\"";
    }
    
    private void getInfoOnTheElementsOnlyComuni() throws IOException{
        String baseURI = "http://www.comuni-italiani.it/cap/";
        Document doc = Jsoup.connect(baseURI).get();           
        //<table cellspacing="0" cellpadding="4" bordercolor="#E9E9E9" border="1" width="100%">  
        int identificatore =-1;
        Elements links = doc.select("a[href]"); // a with href
        int identificativoComune = 0;           
           for(Element e : links){
               if(e.ownText().contains("(")){
                   System.out.println(e.ownText());
                   ArrayList<String> listCap = GetPostalCOdeByWeb(e.ownText());
                   ArrayList<String> listCity = GetCityByWeb(e.ownText());
                   if(listCap.size() >0){                       
                       for(String s : listCity){                           
                           lcp.put(s.trim(),new SetCodicePostale(identificativoComune,listCap.get(0).toString().trim(),listCap.get(1).toString().trim()));
                           //for (Map.Entry entry : lcp.entrySet()) {
                               //System.out.println(entry.getKey() + ", " + entry.getValue());
                               //System.out.println(entry.getKey() +","+entry.getValue());
                           //}
                           identificativoComune++;  
                       }
                   }
               }
           }
           //PRINT FOR COPY TO SETCAP FOR ITALIA
           for (Map.Entry entry : lcp.entrySet()) {
               //System.out.println(entry.getKey() + ", " + entry.getValue());
               System.out.println("lcp.put(\""+entry.getKey()+"\",new SetCodicePostale("+entry.getValue()+"));");
           }
    }
    
    private void getInfoOnTheElementAllCity(){
        try {
            String baseURI = "http://www.comuni-italiani.it/cap/";
            Document doc = Jsoup.connect(baseURI).get();           
            //<table cellspacing="0" cellpadding="4" bordercolor="#E9E9E9" border="1" width="100%">  
            int identificatore =-1;
            //Elements links = doc.trySelect("a[href]"); // a with href
            //ecap.getInfoOnTheElementsOnlyComuni(links);
            
            ///////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////
            Elements links = doc.select("a"); // a with href             
            for(Element e : links){                
                System.out.println("//COMUNE:"+e.text()); //00010-00100 (Roma)
                if(e.text().contains("(")){                    
                    String s = e.attr("href").intern();
                    String newURI = baseURI+s;
                    System.out.println("//HREF NUOVA PAGINA:"+newURI);
                    Document doc2 = Jsoup.connect(newURI).get();                  
                    Element table = doc2.select("table").get(5);
                    Elements tr = table.select("tr");                                   
                    for(Element e3 : tr){                      
                        Elements td = e3.select("td");
                        String city ="";
                        String cap = "";
                        for(Element e4 : td){
                            Node content = e4.childNode(0);                                                   
                            if(content.toString().contains("href")){
                                content = content.childNode(0).childNode(0);
                                //System.out.println(content.toString());//Casape
                               city = content.toString();
                               SetCodicePostale ec = new  SetCodicePostale(identificatore, null, cap);
                               Boolean verify = false;
                                for (Map.Entry entry : lcp.entrySet()) {
                                    if(entry.getKey().equals(city)){verify = true;} //Controlla che la città nno sia già presente                                   
                                }
                                if(verify==false){
                                lcp.put(city, ec);
                                //System.out.println("ID["+identificatore+"]{CITY:"+city+",CAP:"+cap+"}");  //STAMPA
                                }
                               //lcp.put(city, ec);
                            }else{
                                //System.out.println(content.toString()); //00010  
                                cap = content.toString();
                            }               
                        }//for(Element e4 : td)                                                
                    }//for(Element e3 : tr)                 
                }
                identificatore++;               
            }//for(Element e : links)        
            //PRINT FOR COPY TO SETCAP FOR ITALIA
            for (Map.Entry entry : lcp.entrySet()) {
                //System.out.println(entry.getKey() + ", " + entry.getValue());
                System.out.println("lcp.put(\""+entry.getKey()+"\",new SetCodicePostale("+entry.getValue()+"));");
            }    
        } catch (IOException ex) {
            Logger.getLogger(SetCodicePostale.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
//    public static void  main(String args[]){
//        try {
//            String baseURI = "http://www.comuni-italiani.it/cap/";
//            Document doc = Jsoup.connect(baseURI).get();           
//            //<table cellspacing="0" cellpadding="4" bordercolor="#E9E9E9" border="1" width="100%">  
//            int identificatore =-1;
//            
//            SetCodicePostale ecap = new SetCodicePostale();
//            //ecap.getInfoOnTheElementsOnlyComuni();
//            ecap.getInfoOnTheElementAllCity();
//            ///////////////////////////////////////////////////////////////////////////
//            ////////////////////////////////////////////////////////////////////////////
//        } catch (IOException ex) {
//            Logger.getLogger(SetCodicePostale.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }//main
//    
}
