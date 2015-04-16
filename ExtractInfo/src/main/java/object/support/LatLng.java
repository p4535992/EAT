package object.support;
import java.io.Serializable;
/**
 * LatLng.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti.
 * Semplice oggetto Java per raccogliere vla coppia latitudine e longitudine
 * relative alle coordinate GPS di una locazione.
 */
public class LatLng implements Serializable{
 
    private static final long serialVersionUID = 16549987563L;
 
    private double lat;
    private double lng;
 
    public LatLng(final double lat, final double lng) {
        this.lat = lat;
        this.lng = lng;
    }
 
    public double getLat() {
        return lat;
    }
 
    public void setLat(final double lat) {
        this.lat = lat;
    }
 
    public double getLng() {
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