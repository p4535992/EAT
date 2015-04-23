package extractor.setInfoParameterIta;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe che integra in modo completo la relazione Nazione-Language.
 * Altra condizione primitiva, ma purtroppo i siti non inseriscono nel loro contenuto 
 * la "nazionalità" un controllo della lingua con GATE o del sito in esame,
 * risolverebbe il problema ma rallenterebbe anche di molto il programma
 * lascio a chi riprende il progetto la possibilità di esaminare una soluzione 
 * migliore e più automatica (senza che ci si rimetta mano sopra).
 * @author Marco
 */
public class SetNazioneELanguage {
    
    private Map<String,SetNazioneELanguage> map = new HashMap<String,SetNazioneELanguage>(); 
    private String alpha_1;
    private String alpha_2;
    private String iso_1;

    public SetNazioneELanguage(String alpha_1,String alpha_2,String iso_1){
        this.alpha_1 = alpha_1;
        this.alpha_2 = alpha_2;
        this.iso_1 = iso_1;
    }
    
    public SetNazioneELanguage(){
        map.put("Afghanistan",new SetNazioneELanguage("AF", "AFG", "004"));
        map.put("Albania",new SetNazioneELanguage("AL","ALB","8"));
        map.put("Algeria",new SetNazioneELanguage("DZ","DZA","12"));
        map.put("Andorra",new SetNazioneELanguage("AD","AND","20"));
        map.put("Angola",new SetNazioneELanguage("AO","AGO","24"));
        map.put("Anguilla",new SetNazioneELanguage("AI","AIA","660"));
        map.put("Antartide",new SetNazioneELanguage("AQ","ATA","10"));
        map.put("Antigua e Barbuda",new SetNazioneELanguage("AG","ATG","28"));
        map.put("Antille Olandesi",new SetNazioneELanguage("AN","ANT","530"));
        map.put("Arabia Saudita",new SetNazioneELanguage("SA","SAU","682"));
        map.put("Argentina",new SetNazioneELanguage("AR","ARG","32"));
        map.put("Armenia",new SetNazioneELanguage("AM","ARM","51"));
        map.put("Aruba",new SetNazioneELanguage("AW","ABW","533"));
        map.put("Australia",new SetNazioneELanguage("AU","AUS","36"));
        map.put("Austria",new SetNazioneELanguage("AT","AUT","40"));
        map.put("Azerbaijan",new SetNazioneELanguage("AZ","AZE","31"));

        map.put("Bahamas",new SetNazioneELanguage("BS","BHS","44"));
        map.put("Bahrain",new SetNazioneELanguage("BH","BHR","48"));
        map.put("Bangladesh",new SetNazioneELanguage("BD","BGD","50"));
        map.put("Barbados",new SetNazioneELanguage("BB","BRB","52"));
        map.put("Belgio",new SetNazioneELanguage("BE","BEL","56"));
        map.put("Belize",new SetNazioneELanguage("BZ","BLZ","84"));
        map.put("Benin",new SetNazioneELanguage("BJ","BEN","204"));
        map.put("Bermuda",new SetNazioneELanguage("BM","BMU","60"));
        map.put("Bielorussia",new SetNazioneELanguage("BY","BLR","112"));
        map.put("Bhutan",new SetNazioneELanguage("BT","BTN","64"));
        map.put("Bolivia",new SetNazioneELanguage("BO","BOL","68"));
        map.put("Bosnia Erzegovina",new SetNazioneELanguage("BA","BIH","70"));
        map.put("Botswana",new SetNazioneELanguage("BW","BWA","72"));
        map.put("Brasile",new SetNazioneELanguage("BR","BRA","76"));
        map.put("Brunei Darussalam",new SetNazioneELanguage("BN","BRN","96"));
        map.put("Bulgaria",new SetNazioneELanguage("BG","BGR","100"));
        map.put("Burkina Faso",new SetNazioneELanguage("BF","BFA","854"));
        map.put("Burundi	",new SetNazioneELanguage("BI","BDI","108"));

        map.put("Cambogia",new SetNazioneELanguage("KH","KHM","116"));
        map.put("Camerun",new SetNazioneELanguage("CM","CMR","120"));
        map.put("Canada",new SetNazioneELanguage("CA","	CAN","124"));
        map.put("Capo Verde",new SetNazioneELanguage("CV","CPV","132"));
        map.put("Ciad",new SetNazioneELanguage("TD","TCD","148"));
        map.put("Cile",new SetNazioneELanguage("CL","CHL","152"));
        map.put("Cina",new SetNazioneELanguage("CN","CHN","156"));
        map.put("Cipro",new SetNazioneELanguage("CY","CYP","196"));
        map.put("Città del Vaticano",new SetNazioneELanguage("VA","VAT","336"));
        map.put("Colombia",new SetNazioneELanguage("CO","COL","170"));
        map.put("Comore",new SetNazioneELanguage("KM","COM","174"));
        map.put("Corea del Nord",new SetNazioneELanguage("KP","PRK","408"));
        map.put("Corea del Sud",new SetNazioneELanguage("KR","KOR","410"));
        map.put("Costa Rica",new SetNazioneELanguage("CR","CRI","188"));
        map.put("Costa d'Avorio",new SetNazioneELanguage("CI","CIV","384"));
        map.put("Croazia",new SetNazioneELanguage("HR","HRV","191"));
        map.put("Cuba",new SetNazioneELanguage("CU","CUB","192"));

        map.put("Danimarca",new SetNazioneELanguage("DK","DNK","208"));
        map.put("Dominica",new SetNazioneELanguage("DM","DMA","212"));

        map.put("Ecuador",new SetNazioneELanguage("EC","ECU","218"));
        map.put("Egitto",new SetNazioneELanguage("EG","EGY","818"));
        map.put("Eire",new SetNazioneELanguage("IE","IRL","372"));
        map.put("El Salvador",new SetNazioneELanguage("	SV","SLV","222"));
        map.put("Emirati Arabi Uniti",new SetNazioneELanguage("AE","ARE","784"));
        map.put("Eritrea",new SetNazioneELanguage("ER","ERI","232"));
        map.put("Estonia",new SetNazioneELanguage("EE","EST","233"));
        map.put("Etiopia",new SetNazioneELanguage("ET","ETH","231"));

        map.put("Federazione Russa",new SetNazioneELanguage("RU","RUS","643"));
        map.put("Fiji",new SetNazioneELanguage("FJ","FJI","242"));
        map.put("Filippine",new SetNazioneELanguage("PH","PHL","608"));
        map.put("Finlandia",new SetNazioneELanguage("FI","FIN","246"));
        map.put("Francia",new SetNazioneELanguage("FR","FRA","250"));

        map.put("Gabon",new SetNazioneELanguage("GA","GAB","266"));
        map.put("Gambia",new SetNazioneELanguage("GM","GMB","270"));
        map.put("Georgia",new SetNazioneELanguage("GE","GEO","268"));
        map.put("Germania",new SetNazioneELanguage("DE","DEU","276"));
        map.put("Ghana",new SetNazioneELanguage("GH","GHA","288"));
        map.put("Giamaica",new SetNazioneELanguage("JM","JAM","388"));
        map.put("Giappone",new SetNazioneELanguage("JP","JPN","392"));
        map.put("Gibilterra",new SetNazioneELanguage("GI","GIB","292"));
        map.put("Gibuti",new SetNazioneELanguage("DJ","DJI","262"));
        map.put("Giordania",new SetNazioneELanguage("JO","JOR","400"));
        map.put("Grecia",new SetNazioneELanguage("GR","GRC","300"));
        map.put("Grenada",new SetNazioneELanguage("GD","GRD","308"));
        map.put("Groenlandia",new SetNazioneELanguage("GL","GRL","304"));
        map.put("Guadalupa",new SetNazioneELanguage("GP","GLP","312"));
        map.put("Guam",new SetNazioneELanguage("GU","GUM","316"));
        map.put("Guatemala",new SetNazioneELanguage("GT","GTM","320"));
        map.put("Guinea",new SetNazioneELanguage("GN","GIN","324"));
        map.put("Guinea-Bissau",new SetNazioneELanguage("GW","GNB","624"));
        map.put("Guinea Equatoriale",new SetNazioneELanguage("GQ","GNQ","226"));
        map.put("Guyana",new SetNazioneELanguage("GY","GUY","328"));
        map.put("Guyana Francese",new SetNazioneELanguage("GF","GUF","254"));

        map.put("Haiti",new SetNazioneELanguage("HT","HTI","332"));
        map.put("Honduras",new SetNazioneELanguage("HN","HND","340"));
        map.put("Hong Kong",new SetNazioneELanguage("HK","HKG","344"));

        map.put("India",new SetNazioneELanguage("IN","IND","356"));
        map.put("Indonesia",new SetNazioneELanguage("ID","IDN","360"));
        map.put("Iran",new SetNazioneELanguage("IR","IRN","364"));
        map.put("Iraq",new SetNazioneELanguage("IQ","IRQ","368"));
        map.put("Isola Bouvet",new SetNazioneELanguage("BV","BVT","74"));
        map.put("Isola di Natale",new SetNazioneELanguage("CX","CXR","162"));
        map.put("Isola Heard e Isole McDonald",new SetNazioneELanguage("HM","HMD","334"));
        map.put("Isole Cayman",new SetNazioneELanguage("KY","CYM","136"));
        map.put("Isole Cocos",new SetNazioneELanguage("CC","CCK","166"));
        map.put("Isole Cook",new SetNazioneELanguage("CK","COK","184"));
        map.put("Isole Falkland",new SetNazioneELanguage("FK","FLK","238"));
        map.put("Isole Faroe",new SetNazioneELanguage("FO","FRO","234"));
        map.put("Isole Marshall",new SetNazioneELanguage("MH","MHL","584"));
        map.put("Isole Marianne Settentrionali",new SetNazioneELanguage("MP","MNP","580"));
        map.put("Isole Minori degli Stati Uniti d'America",new SetNazioneELanguage("UM","UMI","581"));
        map.put("Isola Norfolk",new SetNazioneELanguage("NF","NFK","574"));
        map.put("Isole Solomon",new SetNazioneELanguage("SB","SLB","90"));
        map.put("Isole Turks e Caicos",new SetNazioneELanguage("TC","TCA","796"));
        map.put("Isole Vergini Americane",new SetNazioneELanguage("VI","VIR","850"));
        map.put("Isole Vergini Britanniche",new SetNazioneELanguage("VG","VGB","92"));
        map.put("Israele",new SetNazioneELanguage("IL","ISR","376"));
        map.put("Islanda",new SetNazioneELanguage("IS","ISL","352"));
        map.put("Italia",new SetNazioneELanguage("IT","ITA","380"));
        //map.put("Italia",new SetNazioneELanguage("LT","ITA","380"));
        //K
        map.put("Kazakhistan",new SetNazioneELanguage("KZ","KAZ","398"));
        map.put("Kenya",new SetNazioneELanguage("KE","KEN","404"));
        map.put("Kirghizistan",new SetNazioneELanguage("KG","KGZ","417"));
        map.put("Kiribati",new SetNazioneELanguage("KI","KIR","296"));
        map.put("Kuwait",new SetNazioneELanguage("KW","KWT","414"));
        //L
        map.put("Laos",new SetNazioneELanguage("LA","LAO","418"));
        map.put("Lettonia",new SetNazioneELanguage("LV","LVA","428"));
        map.put("Lesotho",new SetNazioneELanguage("LS","LSO","426"));
        map.put("Libano",new SetNazioneELanguage("LB","LBN","422"));
        map.put("Liberia",new SetNazioneELanguage("LR","LBR","430"));
        map.put("Libia",new SetNazioneELanguage("LY","LBY","434"));
        map.put("Liechtenstein",new SetNazioneELanguage("LI","LIE","438"));
        map.put("Lituania",new SetNazioneELanguage("LT","LTU","440"));
        map.put("Lussemburgo",new SetNazioneELanguage("LU","LUX","442"));
        //M
        map.put("Macao",new SetNazioneELanguage("MO","MAC","446"));
        map.put("Macedonia",new SetNazioneELanguage("MK","MKD","807"));
        map.put("Madagascar",new SetNazioneELanguage("MG","MDG","450"));
        map.put("Malawi",new SetNazioneELanguage("MW","MWI","454"));
        map.put("Maldive",new SetNazioneELanguage("MV","MDV","462"));
        map.put("Malesia",new SetNazioneELanguage("MY","MYS","458"));
        map.put("Mali",new SetNazioneELanguage("ML","MLI","466"));
        map.put("Malta",new SetNazioneELanguage("MT","MLT","470"));
        map.put("Marocco",new SetNazioneELanguage("MA","MAR","504"));
        map.put("Martinica",new SetNazioneELanguage("MQ","MTQ","474"));
        map.put("Mauritania",new SetNazioneELanguage("MR","MRT","478"));
        map.put("Maurizius",new SetNazioneELanguage("MU","MUS","480"));
        map.put("Mayotte",new SetNazioneELanguage("YT","MYT","175"));
        map.put("Messico",new SetNazioneELanguage("MX","MEX","484"));
        map.put("Moldavia",new SetNazioneELanguage("MD","MDA","498"));
        map.put("Monaco",new SetNazioneELanguage("MC","MCO","492"));
        map.put("Mongolia",new SetNazioneELanguage("MN","MNG","496"));
        map.put("Montserrat",new SetNazioneELanguage("MS","MSR","500"));
        map.put("Mozambico",new SetNazioneELanguage("MZ","MOZ","508"));
        map.put("Myanmar",new SetNazioneELanguage("MM","MMR","104"));
        //N
        map.put("Namibia",new SetNazioneELanguage("NA","NAM","516"));
        map.put("Nauru",new SetNazioneELanguage("NR","NRU","520"));
        map.put("Nepal",new SetNazioneELanguage("NP","NPL","524"));
        map.put("Nicaragua",new SetNazioneELanguage("NI","NIC","558"));
        map.put("Niger",new SetNazioneELanguage("NE","NER","562"));
        map.put("Nigeria",new SetNazioneELanguage("NG","NGA","566"));
        map.put("Niue",new SetNazioneELanguage("NU","NIU","570"));
        map.put("Norvegia",new SetNazioneELanguage("NO","NOR","578"));
        map.put("Nuova Caledonia",new SetNazioneELanguage("NC","NCL","540"));
        map.put("Nuova Zelanda",new SetNazioneELanguage("NZ","NZL","554"));
        //O
        map.put("Oman",new SetNazioneELanguage("OM","OMN","512"));
        //P
        map.put("Paesi Bassi",new SetNazioneELanguage("NL","NLD","528"));
        map.put("Pakistan",new SetNazioneELanguage("PK","PAK","586"));
        map.put("Palau",new SetNazioneELanguage("PW","PLW","585"));
        map.put("Panamá",new SetNazioneELanguage("PA","PAN","591"));
        map.put("Papua Nuova Guinea",new SetNazioneELanguage("PG","PNG","598"));
        map.put("Paraguay",new SetNazioneELanguage("PY","PRY","600"));
        map.put("Perù",new SetNazioneELanguage("PE","PER","604"));
        map.put("Pitcairn",new SetNazioneELanguage("PN","PCN","612"));
        map.put("Polinesia Francese",new SetNazioneELanguage("PF","PYF","258"));
        map.put("Polonia",new SetNazioneELanguage("PL","POL","616"));
        map.put("Portogallo",new SetNazioneELanguage("PT","PRT","620"));
        map.put("Porto Rico",new SetNazioneELanguage("PR","PRI","630"));
        //Q
        map.put("Qatar",new SetNazioneELanguage("QA","QAT","634"));
        //R
        map.put("Regno Unito",new SetNazioneELanguage("GB","GBR","826"));
        map.put("Repubblica Ceca",new SetNazioneELanguage("CZ","CZE","203"));
        map.put("Repubblica Centroafricana",new SetNazioneELanguage("CF","CAF","140"));
        map.put("Repubblica del Congo",new SetNazioneELanguage("CG","COG","178"));
        map.put("Repubblica Democratica del Congo",new SetNazioneELanguage("CD","COD","180"));
        map.put("Repubblica Dominicana",new SetNazioneELanguage("DO","DOM","214"));
        map.put("Reunion",new SetNazioneELanguage("RE","REU","638"));
        map.put("Romania",new SetNazioneELanguage("RO","ROU","642"));
        map.put("Ruanda",new SetNazioneELanguage("RW","RWA","646"));
        //S
        map.put("Sahara Occidentale",new SetNazioneELanguage("EH","ESH","732"));
        map.put("Saint Kitts e Nevis",new SetNazioneELanguage("KN","KNA","659"));
        map.put("Saint Pierre e Miquelon",new SetNazioneELanguage("PM","SPM","666"));
        map.put("Saint Vincent e Grenadine",new SetNazioneELanguage("VC","VCT","670"));
        map.put("Samoa",new SetNazioneELanguage("WS","WSM","882"));
        map.put("Samoa Americane",new SetNazioneELanguage("AS","ASM","16"));
        map.put("San Marino",new SetNazioneELanguage("SM","SMR","674"));
        map.put("Sant'Elena",new SetNazioneELanguage("SH","SHN","654"));
        map.put("Santa Lucia",new SetNazioneELanguage("LC","LCA","662"));
        map.put("Sao Tome e Principe",new SetNazioneELanguage("	ST","STP","678"));
        map.put("Senegal",new SetNazioneELanguage("SN","SEN","686"));
        map.put("Serbia e Montenegro",new SetNazioneELanguage("	CS","SCG","891"));
        map.put("Seychelles",new SetNazioneELanguage("SC","SYC","690"));
        map.put("Sierra Leone",new SetNazioneELanguage("SL","SLE","694"));
        map.put("Singapore",new SetNazioneELanguage("SG","SGP","702"));
        map.put("Siria",new SetNazioneELanguage("SY","SYR","760"));
        map.put("Slovacchia",new SetNazioneELanguage("SK","SVK","703"));
        map.put("Slovenia",new SetNazioneELanguage("SI","SVN","705"));
        map.put("Somalia",new SetNazioneELanguage("SO","SOM","706"));
        map.put("Spagna",new SetNazioneELanguage("ES","	ESP","724"));
        map.put("Sri Lanka",new SetNazioneELanguage("LK","LKA","144"));
        map.put("Stati Federati della Micronesia",new SetNazioneELanguage("FM","FSM","583"));
        map.put("Stati Uniti d'America",new SetNazioneELanguage("US","USA","840"));
        map.put("Sud Africa",new SetNazioneELanguage("ZA","ZAF","710"));
        map.put("Sud Georgia e Isole Sandwich",new SetNazioneELanguage("GS","SGS","239"));
        map.put("Sudan",new SetNazioneELanguage("SD","SDN","736"));
        map.put("Suriname",new SetNazioneELanguage("SR","SUR","740"));
        map.put("Svalbard e Jan Mayen",new SetNazioneELanguage("SJ","SJM","744"));
        map.put("Svezia",new SetNazioneELanguage("SE","SWE","752"));
        map.put("Svizzera",new SetNazioneELanguage("CH","CHE","756"));
        map.put("Swaziland",new SetNazioneELanguage("SZ","SWZ","748"));
        //T
        map.put("Tagikistan",new SetNazioneELanguage("TJ","TJK","762"));
        map.put("Tailandia",new SetNazioneELanguage("TH","THA","764"));
        map.put("Taiwan",new SetNazioneELanguage("TW","TWN","158"));
        map.put("Tanzania",new SetNazioneELanguage("TZ","TZA","834"));
        map.put("Territori Britannici dell'Oceano Indiano",new SetNazioneELanguage("IO","IOT","92"));
        map.put("Territori Francesi del Sud",new SetNazioneELanguage("TF","ATF","260"));
        map.put("Territori Palestinesi Occupati",new SetNazioneELanguage("PS","PSE","275"));
        map.put("Timor Est",new SetNazioneELanguage("TL","TLS","626"));
        map.put("Togo",new SetNazioneELanguage("TG","TGO","768"));
        map.put("Tokelau",new SetNazioneELanguage("TK","TKL","772"));
        map.put("Tonga",new SetNazioneELanguage("TO","TON","776"));
        map.put("Trinidad e Tobago",new SetNazioneELanguage("TT","TTO","780"));
        map.put("Tunisia",new SetNazioneELanguage("TN","TUN","788"));
        map.put("Turchia",new SetNazioneELanguage("TR","TUR","792"));
        map.put("Turkmenistan",new SetNazioneELanguage("TM","TKM","795"));
        map.put("Tuvalu",new SetNazioneELanguage("TV","TUV","798"));
        //U
        map.put("Ucraina",new SetNazioneELanguage("UA","UKR","804"));
        map.put("Uganda",new SetNazioneELanguage("UG","UGA","800"));
        map.put("Ungheria",new SetNazioneELanguage("HU","HUN","348"));
        map.put("Uruguay",new SetNazioneELanguage("UY","URY","858"));
        map.put("Uzbekistan",new SetNazioneELanguage("UZ","UZB","860"));
        //V
        map.put("Vanuatu",new SetNazioneELanguage("VU","VUT","548"));
        map.put("Venezuela",new SetNazioneELanguage("VE","VEN","862"));
        map.put("Vietnam",new SetNazioneELanguage("VN","VNM","704"));
        //W
        map.put("Wallis e Futuna",new SetNazioneELanguage("WF","WLF","876"));
        //Y
        map.put("Yemen",new SetNazioneELanguage("YE","YEM","887"));
        //Z
        map.put("Zambia",new SetNazioneELanguage("ZM","ZMB","894"));
        map.put("Zimbabwe",new SetNazioneELanguage("ZW","ZWE","716"));
    }
    
    public String checkNazioneByDomain(String domain){
        String nazione = null;
         for ( Map.Entry<String, SetNazioneELanguage> entry : map.entrySet()) {
            //nazione = entry.getKey();
            //System.out.println(entry.getKey());
            String domain2 = domain.substring(domain.length()-3);            
             //String domain2 = getTheLastIdentificator(domain,".");
            String alpha_1 = entry.getValue().getAlpha_1().replaceAll("\\s+","").toLowerCase();
            //String alpha_1 = entry.getValue().getAlpha_1().toLowerCase();
            //System.out.println("[ALPHA_1]:"+alpha_1+"[DOMAIN]:"+domain+"[DOMAIN2]:"+domain2);
            if((domain.endsWith("."+alpha_1))                 
             || (domain2.contains("."+alpha_1))){
                //System.out.println("[1]ALPHA 1:"+alpha_1+" DOMAIN:"+domain);
                nazione = entry.getKey(); 
                break;
            }              
        }//for
        //System.out.println("NAZIONE:"+nazione);
        return nazione;
        
        /*
        if(domain.contains(".it")){nazione="Italia";}
        else if(domain.contains(".uk")){nazione="Regno Unito";}
        else if(domain.contains(".de")){nazione="Germania";}
        else if(domain.contains(".en")){nazione="Inghilterra";}
        else if(domain.contains(".fr")){nazione="Francia";}
        else{s=null;}
        */
        /*
        else if(domain.contains(".com")||domain.contains(".org")){geo.setNazione("Ente Commerciale");}
        else if(domain.contains(".gov")){geo.setNazione("Ente Governativo");}
        else if(domain.contains(".edu")){geo.setNazione("Ente Educativo");}
        */
        
    }
    
    public String checkNazioneByLanguageIdentificatorByTika(String languageCode){
        String nazione = null;
         for ( Map.Entry<String, SetNazioneELanguage> entry : map.entrySet()) {
            //nazione = entry.getKey();
            //System.out.println(key);
            String alpha_1 = entry.getValue().getAlpha_1();
            String alpha_2 = entry.getValue().getAlpha_2();
            String iso_1 = entry.getValue().getIso_1();
            
            if(languageCode.replaceAll("\\s+","").equalsIgnoreCase(alpha_1.replaceAll("\\s+",""))){
                //System.out.println("[2]ALPHA 1:"+alpha_1+" LANGUAGECODE:"+languageCode);
                nazione = entry.getKey();           
                break;
            }else if(languageCode.replaceAll("\\s+","").equalsIgnoreCase(alpha_2.replaceAll("\\s+",""))){
                //System.out.println("[2]ALPHA 1:"+alpha_1+" LANGUAGECODE:"+languageCode);
                nazione = entry.getKey();
                break;
            }else if(languageCode.replaceAll("\\s+","").equalsIgnoreCase(iso_1.replaceAll("\\s+",""))){
                //System.out.println("[2]ALPHA 1:"+alpha_1+" LANGUAGECODE:"+languageCode);
                nazione = entry.getKey();  
                break;
            }else{
                nazione = null;
            }  //if                
        }//for
        //System.out.println("NAZIONE:"+nazione);
        //map.clear();
        return nazione;
    }
    
    public String checkGMRegionByNazione(String nazione){
        String gmregion = null;
        for ( Map.Entry<String, SetNazioneELanguage> entry : map.entrySet()) {           
            String nazione2 = entry.getKey();
            try{
            if(nazione2.replaceAll("\\s+","").equalsIgnoreCase(nazione.replaceAll("\\s+",""))){
                 gmregion = entry.getValue().getAlpha_1();
                 break;
            }//if  
            }catch(java.lang.NullPointerException ne){
                if(nazione2.equalsIgnoreCase(nazione)){
                 gmregion = entry.getValue().getAlpha_1();
                 break;
            }//if  
            }catch(Exception e){continue;}
        }//for
        return gmregion;
    }

    public String getAlpha_1() {
        return alpha_1;
    }

    public void setAlpha_1(String alpha_1) {
        this.alpha_1 = alpha_1;
    }

    public String getAlpha_2() {
        return alpha_2;
    }

    public void setAlpha_2(String alpha_2) {
        this.alpha_2 = alpha_2;
    }

    public String getIso_1() {
        return iso_1;
    }

    public void setIso_1(String iso_1) {
        this.iso_1 = iso_1;
    }
    
    
    
    
    
}
