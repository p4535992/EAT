package com.github.p4535992.extractor.object.support;
/**
 * FrequencyInfo.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti.
 * Oggetto Java con il quale andiamo e immagazzinare la frequenza di un determinato
 * dominio org.p4535992.mvc.webapp avente i medesimi valori di latitudine e longitudine.
 */
public class FrequencyInfo {
    
    private String domain;
    private String city;
    private Double lat;
    private Double lng;
    private Integer frequency;
    
    public FrequencyInfo(String domain,String city,Double lat,Double lng,Integer frequency){
        this.domain = domain;
        this.city = city;
        this.lat = lat;
        this.lng = lng;
        this.frequency = frequency;
    }  

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    } 

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
}
