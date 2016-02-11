package com.github.p4535992.gatebasic.object;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
public class MapAnnotationSet {

    private List<MapAnnotation> listAnnotations;
    private MultiValueMap<String, MapAnnotation> mapAnnotationSets;
    private MultiValueMap<String, Map<String,List<String>>> mapStringAnnotationSets;
    private String annotationSetName;
    private int index;

    public MapAnnotationSet(){}

    public MapAnnotationSet(MapAnnotation mapAnnotation){
        this.index = 0;
        this.listAnnotations = Collections.singletonList(mapAnnotation);
        this.annotationSetName = setUpDefaultName();
        this.mapAnnotationSets = setUpMapAnnotationSets();
        this.mapStringAnnotationSets = setUpMapStringAnnotationSets();
    }

    public MapAnnotationSet(List<MapAnnotation> listAnnotations){
        this.index = 0;
        this.listAnnotations = listAnnotations;
        this.annotationSetName = setUpDefaultName();
        this.mapAnnotationSets = setUpMapAnnotationSets();
        this.mapStringAnnotationSets = setUpMapStringAnnotationSets();
    }


    public MapAnnotationSet(String nameAnnotationSet, MapAnnotation mapAnnotation){
        this.index = 0;
        this.listAnnotations = Collections.singletonList(mapAnnotation);
        this.annotationSetName = nameAnnotationSet;
        this.mapAnnotationSets = setUpMapAnnotationSets();
        this.mapStringAnnotationSets = setUpMapStringAnnotationSets();
    }

    public MapAnnotationSet(String nameAnnotationSet, List<MapAnnotation> listAnnotations){
        this.index = 0;
        this.listAnnotations = listAnnotations;
        this.annotationSetName = setUpDefaultName();
        this.mapAnnotationSets = setUpMapAnnotationSets();
        this.mapStringAnnotationSets = setUpMapStringAnnotationSets();
    }
    //SETTER AND GETTER

    public List<MapAnnotation> getListAnnotations() {
        return listAnnotations;
    }

    public MultiValueMap<String, MapAnnotation> getMapAnnotationSets() {
        return mapAnnotationSets;
    }

    public MultiValueMap<String, Map<String, List<String>>> getMapStringAnnotationSets() {
        return mapStringAnnotationSets;
    }

    public Map<String,Map<String,List<String>>> getMap(){
        if(mapAnnotationSets== null)return new LinkedHashMap<>();
        Map<String, Map<String,List<String>>> result = new LinkedHashMap<>(mapAnnotationSets.size());
        for (Map.Entry<String, List<MapAnnotation>> entry : mapAnnotationSets.entrySet()) {
            for(MapAnnotation mapAnnotation : entry.getValue()) {
                result.put(entry.getKey(),mapAnnotation.getMap());
            }
        }
        return result;
    }

    //OTHER

    private String setUpDefaultName(){
        index++;
        return "AnnotationSet#"+index;
    }

    private MultiValueMap<String,MapAnnotation> setUpMapAnnotationSets(){
        MultiValueMap<String,MapAnnotation> map = new LinkedMultiValueMap<>();
        for(MapAnnotation mapAnnotation :  listAnnotations){
            map.add(annotationSetName,mapAnnotation);
        }
        return map;
    }

    private MultiValueMap<String,Map<String,List<String>>> setUpMapStringAnnotationSets(){
        MultiValueMap<String,Map<String,List<String>>> map = new LinkedMultiValueMap<>();
        for(MapAnnotation mapAnnotation :  listAnnotations){
            map.add(annotationSetName,mapAnnotation.getMap());
        }
        return map;
    }

    public void add(MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.add(setUpDefaultName(),mapAnnotation);
        this.mapStringAnnotationSets.add(setUpDefaultName(),mapAnnotation.getMap());
    }

    public void add(String annotationSetName,MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.add(annotationSetName,mapAnnotation);
        this.mapStringAnnotationSets.add(annotationSetName,mapAnnotation.getMap());
    }

    public void put(MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.put(setUpDefaultName(),Collections.singletonList(mapAnnotation));
        this.mapStringAnnotationSets.put(setUpDefaultName(),Collections.singletonList(mapAnnotation.getMap()));
    }

    public void put(String annotationSetName,MapAnnotation mapAnnotation){
        setIfNull();
        this.listAnnotations.add(mapAnnotation);
        //re-set values
        this.mapAnnotationSets.put(annotationSetName,Collections.singletonList(mapAnnotation));
        this.mapStringAnnotationSets.put(annotationSetName,Collections.singletonList(mapAnnotation.getMap()));
    }

    private void setIfNull(){
        if(listAnnotations == null || listAnnotations.isEmpty()){
            listAnnotations = new ArrayList<>();
        }
    }

    public void clear(){
        listAnnotations.clear();
        mapAnnotationSets.clear();
        mapStringAnnotationSets.clear();
    }

    public int size(){
        return mapAnnotationSets.size();
    }

    public List<MapAnnotation> values(){
        Collection<List<MapAnnotation>> coll = mapAnnotationSets.values();
        List<MapAnnotation> list = new ArrayList<>();
        for(List<MapAnnotation> annSet : coll){
            list.addAll(annSet);
        }
        return list;
    }

    public Set<Map.Entry<String,List<MapAnnotation>>> entrySet(){
        return mapAnnotationSets.entrySet();
    }

    public List<MapAnnotation> get(String annotationSetName){
        return mapAnnotationSets.get(annotationSetName);
    }

    public List<MapAnnotation> get(Integer indexAnnotationSet){
        return new ArrayList<>(mapAnnotationSets.values()).get(indexAnnotationSet);
    }

    public MapAnnotation find(String nameAnnotationSet,String nameAnnotation){
        MapAnnotation theMapAnnotation = null;
        for(MapAnnotation ann : get(nameAnnotationSet)){
            for(Map.Entry<String,List<MapContent>> entry : ann.entrySet()){
                if(entry.getKey().equals(nameAnnotation)){
                    theMapAnnotation = ann;
                    break;
                }
            }
            if(theMapAnnotation!=null)break;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(String nameAnnotationSet,Integer indexAnnotation){
        MapAnnotation theMapAnnotation = null;
        int i = 0;
        for(MapAnnotation ann : get(nameAnnotationSet)){
            if(i == indexAnnotation){
                theMapAnnotation = ann;
                break;
            }
            i++;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(Integer indexAnnotationSet,Integer indexAnnotation){
        MapAnnotation theMapAnnotation = null;
        int i = 0;
        for(MapAnnotation ann : get(indexAnnotationSet)){
            if(i == indexAnnotation){
                theMapAnnotation = ann;
                break;
            }
            i++;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(Integer indexAnnotation){
        MapAnnotation theMapAnnotation = null;
        for(Map.Entry<String,List<MapAnnotation>> mapAnnSet : mapAnnotationSets.entrySet()){
            int i = 0;
            for(MapAnnotation ann: mapAnnSet.getValue()) {
                for (Map.Entry<String,List<MapContent>> entry : ann.entrySet()) {
                    if (i == indexAnnotation) {
                        theMapAnnotation = ann;
                        break;
                    }
                    i++;
                }
                if (theMapAnnotation != null)break;
            }
            if (theMapAnnotation != null)break;
        }
        return theMapAnnotation;
    }

    public MapAnnotation find(String nameAnnotation){
        MapAnnotation theMapAnnotation = null;
        for(Map.Entry<String,List<MapAnnotation>> mapAnnSet : mapAnnotationSets.entrySet()){
            for(MapAnnotation ann: mapAnnSet.getValue()) {
                for (Map.Entry<String,List<MapContent>> entry : ann.entrySet()) {
                    if (entry.getKey().equals(nameAnnotation)) {
                        theMapAnnotation = ann;
                        break;
                    }
                }
                if (theMapAnnotation != null)break;
            }
            if (theMapAnnotation != null)break;
        }
        return theMapAnnotation;
    }

    @Override
    public String toString() {
        return "MapAnnotationSet{" +
                "listAnnotations=" + listAnnotations +
                ", mapAnnotationSets=" + mapAnnotationSets +
                '}';
    }
}