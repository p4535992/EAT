package com.github.p4535992.extractor.object.support;
import com.github.p4535992.util.string.impl.StringKit;

import java.io.Serializable;
/**
 * LatLng.java.
 * @author 4535992.
 * @version 2015-09-29.
 */
public class LatLng implements Serializable{
 
    private static final long serialVersionUID = 16549987563L;
 
    private Double lat;
    private Double lng;
 
    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng(Integer lat,Integer lng) {
        if(lat==null) this.lat = null;
        else this.lat = StringKit.convertIntegerToDouble(lat);
        if(lng==null) this.lng = null;
        else this.lng = StringKit.convertIntegerToDouble(lng);
    }
 
    public Double getLat() {
        return lat;
    }
 
    public void setLat(final double lat) {
        this.lat = lat;
    }
 
    public Double getLng() {
        return lng;
    }
 
    public void setLng(final double lng) {
        this.lng = lng;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LatLng other = (LatLng) obj;
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lng) != Double.doubleToLongBits(other.lng)) {
            return false;
        }
        return true;
    }
 
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.lng) ^ (Double.doubleToLongBits(this.lng) >>> 32));
        return hash;
    }

	@Override
	public String toString() {
		return "LatLng [lat=" + lat + ", lng=" + lng + "]";
	}
    
    
 
}