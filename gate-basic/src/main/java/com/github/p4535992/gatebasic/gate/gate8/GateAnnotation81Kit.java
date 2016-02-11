package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.gatebasic.object.MapAnnotation;
import com.github.p4535992.gatebasic.object.MapAnnotationSet;
import com.github.p4535992.gatebasic.object.MapContent;
import com.github.p4535992.gatebasic.object.MapDocument;
import gate.*;

import java.util.*;

/**
 * Extract the contents of records
 * Semantics of each document of Corpus structure the content into an object
 * Java Keyword For each document from which is extracted a Keyword insert the
 * Keyword in a list to be used later for inclusion in Database.
 * @author 4535992.
 * @version 2015-11-12.
 */
@SuppressWarnings("unused")
public class GateAnnotation81Kit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateAnnotation81Kit.class);

    private static GateAnnotation81Kit instance = null;
    protected GateAnnotation81Kit(){}

    public static GateAnnotation81Kit getInstance(){
        if(instance == null) {
            instance = new GateAnnotation81Kit();
        }
        return instance;
    }

    /**
     * Method for get all the content annotated on  all documents in a corpus.
     * @param corpus corpus gate.
     * @param listNameAnnotation list string of names of annotations.
     * @param listNameAnnotationSet list string of name of AnnotationSet sorted.
     * @param firstAndExit if true exists after the first content not empty of a specific annotation.
     * @return map of all the contnet of all annotation of all annotationSet on the document.
     */
     public MapDocument getAllAnnotationInfo(Corpus corpus, List<String> listNameAnnotation, List<String> listNameAnnotationSet, boolean firstAndExit) {
         Iterator<Document> iter = corpus.iterator();
         MapDocument mapDocs = new MapDocument();
         MapAnnotationSet mapAnnSet = new MapAnnotationSet(); //AnnotationSet -> Annotation,Content
         MapAnnotation mapAnn = new MapAnnotation();
         while (iter.hasNext()) {
            Document document = iter.next();
            //URL url = document.getSourceUrl();
            //String lang = doc.getFeatures().get("LanguageType").toString();
            //int size = mapAnnSet.size();
             //get all the annotationSet and all the annotation...
             if(listNameAnnotationSet==null || listNameAnnotationSet.isEmpty()) {
                 Set<String> setNameAnnotationSet = document.getAnnotationSetNames();
                 listNameAnnotationSet = new ArrayList<>(setNameAnnotationSet);
             }
             Map<String,AnnotationSet> mapAnnotationSet = document.getNamedAnnotationSets();
             for(String nameAnnotationSet : listNameAnnotationSet){
                 if(isMapValueNullOrInexistent(mapAnnotationSet,nameAnnotationSet)){
                     continue;
                 }
                 AnnotationSet annSet =  mapAnnotationSet.get(nameAnnotationSet);
                 if(listNameAnnotation==null || listNameAnnotation.isEmpty()) {
                     Set<String> setNameAnnotation = annSet.getAllTypes();
                     listNameAnnotation = new ArrayList<>(setNameAnnotation);
                 }
                 for(String nameAnnotation: listNameAnnotation){
                     //set a empty string for avoid the NullPointerException...
                     mapAnn.put(nameAnnotation,new MapContent(""));
                 }
                 //List<Annotation> listAnnotation = annSet.inDocumentOrder();
                 mapAnnSet.put(nameAnnotationSet,mapAnn);
             }

             //get content from all the annotation in alll the annotaiotn set sorted....
            for (String nameAnnotation : listNameAnnotation) { //for each annotation...
                for(String nameAnnotationSet: listNameAnnotationSet) {//for each annotation set...
                    MapContent content; //empty string
                    content = getSingleAnnotationInfo(document, nameAnnotation, nameAnnotationSet);
                    //get the annotation on the first annotation set is have it without check the other annnotation set...
                    if (!content.isEmpty()) {
                        if(firstAndExit) {
                            //found it the annotation on this annotationSet...
                            mapAnn.put(nameAnnotation,content);
                            mapAnnSet.put(nameAnnotationSet, mapAnn);
                            //to the next annnotation...
                            break;
                        }else{
                            //update value on map....
                            mapAnn.put(nameAnnotation,content);
                            mapAnnSet.put(nameAnnotationSet, mapAnn);
                        }
                    }
                }//for each annotationset
            }//for each annotation...
             String name = document.getName();
             mapDocs.put(name,mapAnnSet);
        }//while
        return mapDocs;
    }//getKeyword

    /**
     * Method for get the content of annotation with a specific methods.
     * @param document gate document.
     * @param nameAnnotation strin name of the Annotation.
     * @param nameAnnotationSet string name of the AnnotationSet.
     * @return string content of the Annotation.
     */
    public MapContent getSingleAnnotationInfo(Document document,String nameAnnotation,String nameAnnotationSet) {
        MapContent content = new MapContent("");
        try {
            AnnotationSet annSet = GateAnnotation8Kit.getInstance().getAnnotationSetFromDoc(nameAnnotationSet, document);
            //SystemLog.message("Get content of the Annotation " + nameAnnotation + " on the AnnotationSet " + annSet.getName() + "...");
            //content = getContentLongestFromAnnnotationsOnAnnotationSet(document, nameAnnotation, annSet);         
            Annotation newAnn;
            for(Annotation ann: annSet){
                if(ann.getType().equals(nameAnnotation)){
                    newAnn = ann;
                    content.setContent(GateAnnotation8Kit.getInstance().getContentFromAnnotation(document,newAnn));
                    break;
                }
            }
            if(content.isEmpty())content.setContent("");
            //content =  getContentLastSingleAnnotationOnAnnotationSet(document, nameAnnotation, annSet);
        }catch(NullPointerException ne){
            logger.warn("The AnnotationSet "+nameAnnotationSet+" not have a single annotation for this document to the url: "+ document.getSourceUrl());
        }
        return content;
    }

    private <K,V> boolean isMapValueNullOrInexistent(Map<K,V> map,K key){
        V value = map.get(key);
        if (value != null) {
            return false;
        } else {
            // Key might be present...
            if (map.containsKey(key)) {
                // Okay, there's a key but the value is null
                return true;
            }
            // Definitely no such key
            return true;

        }
    }



}//ManageAnnotationAndContent.java

