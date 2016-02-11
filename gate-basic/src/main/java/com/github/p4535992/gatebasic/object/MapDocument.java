package com.github.p4535992.gatebasic.object;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
public class MapDocument {

    private List<MapAnnotationSet> listAnnotationSets;
    private MultiValueMap<String, MapAnnotationSet> mapDocs;
    private MultiValueMap<String, Map<String,Map<String,List<String>>>> mapStringDocs;
    private String docName;
    private int index;

    public MapDocument(){}

    public MapDocument(MapAnnotationSet mapAnnotationSet){
        this.index = 0;
        this.listAnnotationSets = Collections.singletonList(mapAnnotationSet);
        this.docName = setUpDefaultName();
        this.mapDocs = setUpMapDocs();
        this.mapStringDocs = setUpMapStringDocs();
    }

    public MapDocument(List<MapAnnotationSet> listAnnotationSets){
        this.index = 0;
        this.listAnnotationSets = listAnnotationSets;
        this.docName = setUpDefaultName();
        this.mapDocs = setUpMapDocs();
        this.mapStringDocs = setUpMapStringDocs();
    }

    public MapDocument(String nameDoc, MapAnnotationSet mapAnnotationSet){
        this.index = 0;
        this.listAnnotationSets = Collections.singletonList(mapAnnotationSet);
        this.docName = nameDoc;
        this.mapDocs = setUpMapDocs();
        this.mapStringDocs = setUpMapStringDocs();
    }

    public MapDocument(String nameDoc, List<MapAnnotationSet> listAnnotationSets){
        this.index = 0;
        this.listAnnotationSets = listAnnotationSets;
        this.docName = nameDoc;
        this.mapDocs = setUpMapDocs();
        this.mapStringDocs = setUpMapStringDocs();
    }

    //SETTER AND GETTER

    public List<MapAnnotationSet> getListAnnotationSets() {
        return listAnnotationSets;
    }

    public MultiValueMap<String, MapAnnotationSet> getMapDocs() {
        return mapDocs;
    }

    public MultiValueMap<String, Map<String, Map<String, List<String>>>> getMapStringDocs() {
        return mapStringDocs;
    }

    public Map<String,Map<String,Map<String,List<String>>>> getMap(){
        if(mapDocs== null)return new LinkedHashMap<>();
        Map<String,Map<String,Map<String,List<String>>>> result = new LinkedHashMap<>(mapDocs.size());
        for (Map.Entry<String, List<MapAnnotationSet>> entry : mapDocs.entrySet()) {
            for(MapAnnotationSet mapAnnotationSet : entry.getValue()) {
                result.put(entry.getKey(),mapAnnotationSet.getMap());
            }
        }
        return result;
    }

    //OTHER

    private String setUpDefaultName(){
        index++;
        return "Docs#"+index;
    }

    private MultiValueMap<String,MapAnnotationSet> setUpMapDocs(){
        MultiValueMap<String,MapAnnotationSet> map = new LinkedMultiValueMap<>();
        for(MapAnnotationSet mapAnnotationSet : listAnnotationSets){
            map.add(docName,mapAnnotationSet);
        }
        return map;
    }

    private MultiValueMap<String,Map<String,Map<String,List<String>>>> setUpMapStringDocs(){
        MultiValueMap<String,Map<String,Map<String,List<String>>>> map = new LinkedMultiValueMap<>();
        for(MapAnnotationSet mapAnnotationSet :  listAnnotationSets){
            map.add(docName,mapAnnotationSet.getMap());
        }
        return map;
    }

    public void add(MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.add(setUpDefaultName(),mapAnnotationSet);
        this.mapStringDocs.add(setUpDefaultName(),mapAnnotationSet.getMap());
    }

    public void add(String annotationSetName,MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.add(annotationSetName,mapAnnotationSet);
        this.mapStringDocs.add(annotationSetName,mapAnnotationSet.getMap());
    }

    public void put(MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.put(setUpDefaultName(),Collections.singletonList(mapAnnotationSet));
        this.mapStringDocs.put(setUpDefaultName(),Collections.singletonList(mapAnnotationSet.getMap()));
    }

    public void put(String annotationSetName,MapAnnotationSet mapAnnotationSet){
        setIfNull();
        this.listAnnotationSets.add(mapAnnotationSet);
        //re-set values
        this.mapDocs.put(annotationSetName,Collections.singletonList(mapAnnotationSet));
        this.mapStringDocs.put(annotationSetName,Collections.singletonList(mapAnnotationSet.getMap()));
    }

    private void setIfNull(){
        if(listAnnotationSets == null || listAnnotationSets.isEmpty()){
            listAnnotationSets = new ArrayList<>();
        }
    }

    public void clear(){
        listAnnotationSets.clear();
        mapDocs.clear();
        mapStringDocs.clear();
    }

    public int size(){
       return mapDocs.size();
        //return values().size();
    }

    public List<MapAnnotationSet> values(){
        Collection<List<MapAnnotationSet>> coll = mapDocs.values();
        List<MapAnnotationSet> list = new ArrayList<>();
        for(List<MapAnnotationSet> annSet : coll){
            list.addAll(annSet);
        }
        return list;
    }

    public Set<Map.Entry<String,List<MapAnnotationSet>>> entrySet(){
        return mapDocs.entrySet();
    }

    public List<MapAnnotationSet> get(String documentName){
        return mapDocs.get(documentName);
    }

    public List<MapAnnotationSet> get(Integer indexDocument){
        //List<List<MapAnnotationSet>> l = new ArrayList<>(mapDocs.values());
        return new ArrayList<>(mapDocs.values()).get(indexDocument);
    }

    public boolean hasValue(String key){
        List<MapAnnotationSet> value = mapDocs.get(key);
        return value != null && !value.isEmpty();
    }

    public boolean hasKey(MapAnnotationSet value){
        for (Map.Entry<String,List<MapAnnotationSet>> entry : mapDocs.entrySet()) {
            for(MapAnnotationSet annSet: entry.getValue()) {
                if (Objects.equals(value, annSet)) {
                    return true;
                }
            }
        }
        return false;
    }

    public MapAnnotationSet find(String nameDocument,String nameAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        for(MapAnnotationSet annSet : get(nameDocument)){
            for(Map.Entry<String,List<MapAnnotation>> entry : annSet.entrySet()){
                if(entry.getKey().equals(nameAnnotationSet)){
                    theMapAnnotationSet = annSet;
                    break;
                }
            }
            if(theMapAnnotationSet!=null)break;
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(String nameDocument,Integer indexAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        int i = 0;
        for(MapAnnotationSet annSet : get(nameDocument)){
            if(i == indexAnnotationSet){
                theMapAnnotationSet = annSet;
                break;
            }
            i++;
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(Integer indexDocument,String nameAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        int i = 0;
        for(MapAnnotationSet annSet : get(indexDocument)){
            for(Map.Entry<String,List<MapAnnotation>> entry : annSet.entrySet()){
                if(entry.getKey().equals(nameAnnotationSet)){
                    theMapAnnotationSet = annSet;
                    break;
                }
            }
            if(theMapAnnotationSet!=null)break;
            i++;
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(Integer indexDocument,Integer indexAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        int i = 0;
        for(MapAnnotationSet annSet : get(indexDocument)){
            if(i == indexAnnotationSet){
                theMapAnnotationSet = annSet;
                break;
            }
            i++;
        }
        return theMapAnnotationSet;
    }

    public MapAnnotationSet find(String nameAnnotationSet){
        //Search annotationSet
        MapAnnotationSet theMapAnnotationSet = null;
        for(Map.Entry<String,List<MapAnnotationSet>> mapAnnSet : mapDocs.entrySet()){
            for(MapAnnotationSet annSet: mapAnnSet.getValue()) {
                for (Map.Entry<String,List<MapAnnotation>> entry : annSet.entrySet()) {
                    if (entry.getKey().equals(nameAnnotationSet)) {
                        theMapAnnotationSet = annSet;
                        break;
                    }
                }
                if (theMapAnnotationSet != null)break;
            }
            if (theMapAnnotationSet != null)break;
        }
        return theMapAnnotationSet;
    }

    @Override
    public String toString() {
        return "MapDocument{" +
                "mapDocs=" + mapDocs +
                ", listAnnotationSets=" + listAnnotationSets +
                '}';
    }
}
