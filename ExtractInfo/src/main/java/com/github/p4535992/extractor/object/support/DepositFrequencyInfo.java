package com.github.p4535992.extractor.object.support;

import com.github.p4535992.extractor.object.model.GeoDocument;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 4535992
 * @version 2015-06-26
 */
public class DepositFrequencyInfo {
    
    private String domain;
    private List<GeoDocument> listGeoDoc = new ArrayList<GeoDocument>();
    private Integer frequency;

    public DepositFrequencyInfo(String domain,List<GeoDocument> listGeoDoc,Integer frequency) {
        this.domain = domain;
        this.listGeoDoc = listGeoDoc;
        this.frequency = frequency;
        
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<GeoDocument> getListGeoDoc() {
        return listGeoDoc;
    }

    public void setListGeoDoc(ArrayList<GeoDocument> listGeoDoc) {
        this.listGeoDoc = listGeoDoc;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        String s = 
                "******************************************************************************************************" 
                + System.getProperties().get("line.separator") + 
                "DepositFrequencyInfo{" + "domain=" + domain + ", listGeoDoc=" + listGeoDoc + ", frequency=" + frequency + '}' +
                "******************************************************************************************************" 
                + System.getProperties().get("line.separator");
        return s;
    }

}
