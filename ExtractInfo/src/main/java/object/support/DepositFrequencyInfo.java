/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package object.support;

import object.model.GeoDocument;
import util.SystemLog;

import java.util.ArrayList;

/**
 *
 * @author Utente
 */
public class DepositFrequencyInfo {
    
    private String domain;
    private ArrayList<GeoDocument> listGeoDoc = new ArrayList<GeoDocument>();
    private Integer frequency;

    public DepositFrequencyInfo(String domain,ArrayList<GeoDocument> listGeoDoc,Integer frequency) {
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

    public ArrayList<GeoDocument> getListGeoDoc() {
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
        SystemLog.message(0,s);
        return s;
    }

}
