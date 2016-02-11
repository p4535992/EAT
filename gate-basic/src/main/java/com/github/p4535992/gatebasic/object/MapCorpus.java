package com.github.p4535992.gatebasic.object;

import java.util.Collections;
import java.util.List;

/**
 * Created by 4535992 on 10/02/2016.
 * @author 4535992.
 */
public class MapCorpus{

    private List<MapDocument> mapDocumentList;

    public MapCorpus(){}

    public MapCorpus(MapDocument mapDocument){
        this.mapDocumentList = Collections.singletonList(mapDocument);
    }

    public MapCorpus(List<MapDocument> mapDocumentList){
        this.mapDocumentList = mapDocumentList;
    }

    public List<MapDocument> getMapDocumentList() {
        return mapDocumentList;
    }

    public void setMapDocumentList(List<MapDocument> mapDocumentList) {
        this.mapDocumentList = mapDocumentList;
    }

    @Override
    public String toString() {
        return "MapCorpus{" +
                "mapDocumentList=" + mapDocumentList +
                '}';
    }
}
