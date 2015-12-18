package com.github.p4535992.extractor.estrattori;

import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.util.http.HttpUtilities;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.github.p4535992.util.string.StringUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Method utility for get the String content of a WebPage
 * @author 4535992.
 * @author 2015-07-03.
 */
@SuppressWarnings("unused")
public class ExtractorJSOUP {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ExtractorDomain.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

    public static boolean isEXIST_WEBPAGE() {
        return EXIST_WEBPAGE;
    }

    public static void setEXIST_WEBPAGE(boolean EXIST_WEBPAGE) {
        ExtractorJSOUP.EXIST_WEBPAGE = EXIST_WEBPAGE;
    }

    private static boolean EXIST_WEBPAGE;


    private static ExtractorJSOUP instance = null;
    protected ExtractorJSOUP(){}
    public static ExtractorJSOUP getInstance(){
        if(instance == null){
            instance = new ExtractorJSOUP();
        }
        return instance;
    }

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
        }catch(Exception e){
            tentativi++;
            HttpUtilities.waiter();
            if(tentativi < 3) GetTitleAndHeadingTags(url,geo);
            else doc = null;
        }
        if(doc==null){
            try{
                String html = HttpUtilities.executeHTTPGetRequest(url);
                doc = Jsoup.parse(html);
                logger.info("HTTP GET DONE");
                EXIST_WEBPAGE = true;
            }catch(Exception en){
                EXIST_WEBPAGE = false;
            }
        }else{
            logger.info("JSOUP GET DONE");
            EXIST_WEBPAGE = true;
        }

        if(doc !=null && EXIST_WEBPAGE){
            ExtractorGeoDocumentSupport egds = ExtractorGeoDocumentSupport.getNewInstance();
            //Elements titleTags = doc.trySelectWithRowMap("h1,h2");
            //SETTIAMO TITLE
            //Elements titleTags = doc.trySelectWithRowMap("title");
            //Elements h1Tags = doc.trySelectWithRowMap("h1");
            List<String> tagList = Arrays.asList(                  
                    "title","meta[name=dc:title]","meta[name=DC.Title]","meta[name=og:title]","h1" );
            //"title","dc:title","DC.Title","twitter:title","og:title","h1"
            for(String s: tagList){
                Elements Tags = doc.select(s);
                for(Element e : Tags){
                     result += egds.pulisciStringaEdificio(e.text())+" ";
                     if(result.contains("momentaneamente disabilitato")
                       //||result.contains("sito org.p4535992.mvc.webapp") //occupato,non più raggiungibile,nestitente
                             ){break;}
                     if(StringUtilities.setNullForEmptyString(result)!=null)break;
                }
                if(StringUtilities.setNullForEmptyString(result)!=null)break;
                if(StringUtilities.setNullForEmptyString(result)==null && doc.select(s).size()>0){
                       result += doc.select(s).first().attr("content");               
                }
                //if(setNullForEmptyString(result)!=null){break;}
            }
      
            if(StringUtilities.setNullForEmptyString(result)==null){
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
                     result += egds.pulisciStringaEdificio(e.text())+" ";
                }
                if(StringUtilities.setNullForEmptyString(result)==null && doc.select(s).size()>0){
                       result = doc.select(s).first().attr("content");               
                }
                if(StringUtilities.setNullForEmptyString(result)!=null){break;}
            }
            if(StringUtilities.setNullForEmptyString(result)==null){
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
                    if(!Objects.equals(s, "html")){
                     result += egds.pulisciStringaEdificio(e.text())+" ";
                    }
                }
                if(StringUtilities.setNullForEmptyString(result)==null && doc.select(s).size()>0){
                       result = doc.select(s).first().attr("lang");               
                }else if(StringUtilities.setNullForEmptyString(result)==null && doc.select(s).size()>0 && !Objects.equals(s, "html")){
                       result = doc.select(s).first().attr("content");   
                }
                if(StringUtilities.setNullForEmptyString(result)!=null){break;}
            }
            
            if(StringUtilities.setNullForEmptyString(result)==null){
                result="it";
            }
            geo.setNazione(result);    
            //SystemLog.message("DESCRIPTION:" + geo.getDescription());
            logger.info("EDIFICIO:" + geo.getEdificio());
            //SystemLog.message("LANGUAGE:" + geo.getNazione());
        }else{        
            logger.error("FAILED the HTTP GET for the web address:" + url);
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
}
