package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.gatebasic.object.MapAnnotation;
import com.github.p4535992.gatebasic.object.MapAnnotationSet;
import com.github.p4535992.gatebasic.object.MapContent;
import com.github.p4535992.gatebasic.object.MapDocument;

import java.util.*;

/**
 * Created by 4535992 on 25/06/2015.
 * @author 4535992
 * @version 2015-11-12
 */
@SuppressWarnings("unused")
public class GateSupport2 {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateSupport2.class);

    private MapAnnotationSet mapAnnotationSet;
    private MapAnnotation mapAnnotation;
    private MapDocument mapDocs;
    private String content;

    private static GateSupport2 instance = null;

    protected GateSupport2(){}

    protected GateSupport2(MapDocument mapDocs) {
        this.mapDocs = mapDocs;
    }

    public static GateSupport2 getInstance(MapDocument mapDocs){
        if(instance == null) {
            instance = new GateSupport2(mapDocs);
        }
        return instance;
    }

    public static GateSupport2 getInstance(MapDocument mapDocs, boolean isNull){
        if(isNull) instance = null;
        return  getInstance(mapDocs);
    }

    public static GateSupport2 getInstance(){
        if(instance == null) {
            instance = new GateSupport2();
        }
        return instance;
    }

    public static GateSupport2 getInstance(boolean isNull){
        if(isNull) instance = null;
        return getInstance();
    }

    public List<MapAnnotationSet> getDocument(Integer index,MapDocument mapDocs){
        this.mapDocs = mapDocs;
        return getDocument(index);
    }

    public List<MapAnnotationSet> getDocument(Integer index){
        if(index > mapDocs.size()){
            logger.warn("The index:" + index + " on the map of the documents you try to get not " +
                    "exists on this map of the result of GATE, return NULL");
             return null;
        }
        return mapDocs.get(index);
    }

    public List<MapAnnotationSet> getDocument(String nameDocument,MapDocument mapDocs){
        this.mapDocs = mapDocs;
        return getDocument(nameDocument,false);
    }

    public List<MapAnnotationSet> getDocument(String nameDocument){
        return getDocument(nameDocument,false);
    }

    public List<MapAnnotationSet> getDocument(String nameDocument,boolean ignorecase){
        for(Map.Entry<String,List<MapAnnotationSet>> entryDocs: mapDocs.entrySet()){
            if(ignorecase){
                if(entryDocs.getKey().equalsIgnoreCase(nameDocument)){
                    return mapDocs.get(nameDocument);
                }
            }else{
                if(entryDocs.getKey().equals(nameDocument)){
                    return mapDocs.get(nameDocument);
                }
            }

        }
        logger.warn("The document with the name:" + nameDocument + " not exists on this map of " +
                "the result of GATE, return NULL");
        return null;
    }

    public List<MapAnnotation> getAnnotationSet(Integer index,MapAnnotationSet mapAnnotationSet ){
        this.mapAnnotationSet = mapAnnotationSet;
        return getAnnotationSet(index);
    }

    public List<MapAnnotation> getAnnotationSet(Integer index){
        if(index > mapAnnotationSet.size()){
            logger.warn("The index on the map of the annotationSets you try to get not " +
                    "exists on this map of the result of GATE, return NULL");
            return null;
        }
        //List<MapAnnotation> list = new ArrayList<>(mapAnnotationSet.values());
        //this.mapAnnotation = list.get(index);
        //return list.get(index);
        return mapAnnotationSet.get(index);
    }


    public List<MapAnnotation> getAnnotationSet(String nameAnnotationSet,MapAnnotationSet mapAnnotationSet ){
        this.mapAnnotationSet = mapAnnotationSet;
        return getAnnotationSet(nameAnnotationSet,false);
    }

    public List<MapAnnotation> getAnnotationSet(String nameAnnotationSet){
        return getAnnotationSet(nameAnnotationSet,false);
    }

    public List<MapAnnotation> getAnnotationSet(String nameAnnotationSet,boolean ignorecase){
        for(Map.Entry<String,List<MapAnnotation>> entryDocs: mapAnnotationSet.entrySet()){
            if(ignorecase){
                if(entryDocs.getKey().equalsIgnoreCase(nameAnnotationSet)){
                    return mapAnnotationSet.get(nameAnnotationSet);
                }
            }else{
                if(entryDocs.getKey().equals(nameAnnotationSet)){
                    return mapAnnotationSet.get(nameAnnotationSet);
                }
            }

        }
        logger.warn("The annotationSet with the name:" + nameAnnotationSet + " not exists on this map of " +
                "the result of GATE, return NULL");
        return null;
    }

    public List<MapContent> getAnnotation(Integer index, MapAnnotation mapAnnotation ){
        this.mapAnnotation = mapAnnotation;
        return getAnnotation(index);
    }

    public List<MapContent> getAnnotation(Integer index){
        if(index > mapAnnotation.size()){
            logger.warn("The index:" + index + " on the map of the annotations you try to get not exists" +
                    " on this map of the result of GATE, return NULL");
            return null;
        }
        return mapAnnotation.get(index);
    }

    public List<MapContent> getAnnotation(String nameAnnotation,MapAnnotation mapAnnotation ) {
        this.mapAnnotation = mapAnnotation;
        return getAnnotation(nameAnnotation,false);
    }

    public List<MapContent> getAnnotation(String nameAnnotation) {
        return getAnnotation(nameAnnotation,false);
    }

    public List<MapContent> getAnnotation(String nameAnnotation,boolean ignorecase){
        for(Map.Entry<String,List<MapContent>> entryAnn: mapAnnotation.entrySet()){
            if(ignorecase){
                if(entryAnn.getKey().equalsIgnoreCase(nameAnnotation)){
                    return entryAnn.getValue();
                }
            }else{
                if(entryAnn.getKey().equals(nameAnnotation)){
                    return entryAnn.getValue();
                }
            }

        }
        logger.error("The annotation with the name:" + nameAnnotation + " not exists " +
                "on this map of the result of GATE, return NULL");
        return null;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET CONTENT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<String> getContent(String nameDocument,String nameAnnotationSet,String nameAnnotation){
        try {
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(nameDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(nameDocument,nameAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Name:"+
                        nameAnnotationSet+ " for the Document with Name "+nameDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(nameAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Name:"+
                        nameAnnotation+ " for the AnnotationSet with Name "+nameAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(nameAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation:"+nameAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String",ne);
            return new LinkedList<>();
        }
    }

    public List<String> getContent(Integer indexDocument,String nameAnnotationSet,String nameAnnotation){
        try{
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(indexDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(indexDocument,nameAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Name:"+
                        nameAnnotationSet+ " for the Document with Index "+indexDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(nameAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Name:"+
                        nameAnnotation+ " for the AnnotationSet with Name "+nameAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(nameAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation with Name"+nameAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String");
            return new LinkedList<>();
        }
    }

    public List<String> getContent(Integer indexDocument,Integer indexAnnotationSet,String nameAnnotation){
        try {
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(indexDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(indexDocument,indexAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Index:"+
                        indexAnnotationSet+ " for the Document with Index "+indexDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(nameAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Name:"+
                        nameAnnotation+ " for the AnnotationSet with Index "+indexAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(nameAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation with Name"+nameAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String");
            return new LinkedList<>();
        }
    }

    public List<String> getContent(Integer indexDocument,Integer indexAnnotationSet,Integer indexAnnotation){
        try {
            List<String> list = new ArrayList<>();
            List<MapAnnotationSet> annSets = getDocument(indexDocument);
            //Search annotationSet
            MapAnnotationSet theMapAnnotationSet = mapDocs.find(indexDocument,indexAnnotationSet);
            if(theMapAnnotationSet==null || theMapAnnotationSet.size() == 0){
                logger.error("Can't find any AnnotationSet with Index:"+
                        indexAnnotationSet+ " for the Document with Index "+indexDocument+" return empty list");
                return list;
            }
            //Search Annotation
            MapAnnotation theMapAnnotation = theMapAnnotationSet.find(indexAnnotation);
            if(theMapAnnotation==null || theMapAnnotation.size() == 0){
                logger.error("Can't find any Annotation with Index:"+
                        indexAnnotation+ " for the AnnotationSet with Index "+indexAnnotationSet+" return empty list");
                return list;
            }
            //Search Content
            List<MapContent> theMapContent = theMapAnnotation.get(indexAnnotation);
            if(theMapContent==null || theMapContent.isEmpty()){
                logger.error("Can't find the any Content for the Annotation with Index"+indexAnnotation+ " return empty list");
                return list;
            }
            for(MapContent content : theMapContent){
                list.add(content.getContent());
            }
            return list;
        }catch(NullPointerException ne){
            logger.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists," +
                    "return empty String");
            return new LinkedList<>();
        }
    }


}
