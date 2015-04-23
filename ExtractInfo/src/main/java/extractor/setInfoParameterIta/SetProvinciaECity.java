package extractor.setInfoParameterIta;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marco on 18/04/2015.
 */
public class SetProvinciaECity {

    private static Map<String,String> simpleMap =  new HashMap<String,String>();


    private Map<String,Bucket> map = new HashMap<String,Bucket>();

    public SetProvinciaECity(){
        simpleMap.put("Firenze","bagno a ripoli");
        simpleMap.put("Firenze","barberino di mugello");
        simpleMap.put("Firenze","barberino val d'elsa");
        simpleMap.put("Firenze","borgo san lorenzo");
        simpleMap.put("Firenze","calenzano");
        simpleMap.put("Firenze","campi bisenzio");
        simpleMap.put("Firenze","capraia e limite");
        simpleMap.put("Firenze","castelfiorentino");
        simpleMap.put("Firenze","cerreto guidi");
        simpleMap.put("Firenze","certaldo");
        simpleMap.put("Firenze","dicomano");
        simpleMap.put("Firenze","empoli");
        simpleMap.put("Firenze","fiesole");
        simpleMap.put("Firenze","figline valdarno");
        simpleMap.put("Firenze","firenze");
        simpleMap.put("Firenze","firenzuola");
        simpleMap.put("Firenze","fucecchio");
        simpleMap.put("Firenze","gambassi terme");
        simpleMap.put("Firenze","greve in chianti");
        simpleMap.put("Firenze","impruneta");
        simpleMap.put("Firenze","incisa in val d'arno");
        simpleMap.put("Firenze","lastra a signa");
        simpleMap.put("Firenze","londa");
        simpleMap.put("Firenze","marradi");
        simpleMap.put("Firenze","montaione");
        simpleMap.put("Firenze","montelupo fiorentino");
        simpleMap.put("Firenze","montespertoli");
        simpleMap.put("Firenze","palazzuolo sul senio");
        simpleMap.put("Firenze","pelago");
        simpleMap.put("Firenze","pontassieve");
        simpleMap.put("Firenze","reggello");
        simpleMap.put("Firenze","rignano sull'arno");
        simpleMap.put("Firenze","rufina");
        simpleMap.put("Firenze","san casciano in val di pesa");
        simpleMap.put("Firenze","san godenzo");
        simpleMap.put("Firenze","san piero a sieve");
        simpleMap.put("Firenze","scandicci");
        simpleMap.put("Firenze","scarperia");
        simpleMap.put("Firenze","sesto fiorentino");
        simpleMap.put("Firenze","signa");
        simpleMap.put("Firenze","tavarnelle val di pesa");
        simpleMap.put("Firenze","vaglia");
        simpleMap.put("Firenze","vicchio");
        simpleMap.put("Firenze","vinci");
    }

    public static String checkProvincia(String city){
        String provincia = "";
        for(Map.Entry<String, String>  entry : simpleMap.entrySet()){
            if(entry.getValue().contains(city.toLowerCase().trim())) {
                provincia = entry.getKey();
                break;
            }
        }
        return provincia;
    }

    public static void  main(String args[]){
        try {
            String baseURI = "http://www.comuni-italiani.it/";
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.connect(baseURI).get();
            //<table cellspacing="0" cellpadding="4" bordercolor="#E9E9E9" border="1" width="100%">

            for (org.jsoup.nodes.Element table : doc.select("table")) {
                for (org.jsoup.nodes.Element row : table.select("tr")) {
                    org.jsoup.select.Elements tds = row.select("td");
                    if (tds.size() > 6) {
                        //System.out.println(tds.get(0).text() + ":" + tds.get(1).text());
                        //map.put(tds.get(0).text(),tds.get(1).text());
                    }
                }
            }
        } catch (IOException ex) {
            //Logger.getLogger(SetCodicePostale.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }//main
}

class Bucket
{

    private String city;
    private String href;
    private String text;

    public Bucket(String city,String href,String text){
        this.city = city;
        this.text = text;
        this.href = href;
    }

}
