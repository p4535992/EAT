/**
 * GeoDomainDocument.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti.
 * La classe che costruisce lt'oggeto JAVA geoDomainDocument su cui andiamo a
 * inserire i risultati del'elaborazione di GATE nella tabella geodocument
 * e inserirli successivamente come record della tabella geodomaindocument del database geolocationdb
 */
package object.model;

import java.net.URL;

/**
 * Oggetto utilizzato per lt'immagazinnamento dei GeoDomainDocument
 * @author Marco Tenti
 */
import javax.persistence.*;
@Entity
@Table(name = "geodomaindocument_ann")
public class GeoDomainDocument {
    @Id @GeneratedValue
    @Column(name = "doc_id")
    private Integer doc_id;
    @Column(name = "url")
    private URL url;
    @Column(name = "regione")
    private String regione;
    @Column(name = "provincia")
    private String provincia;
    @Column(name = "city")
    private String city;
    @Column(name = "indirizzo")
    private String indirizzo;
    @Column(name = "iva")
    private String iva;
    @Column(name = "email")
    private String email;
    @Column(name = "telefono")
    private String telefono;  
    @Column(name = "edificio")
    private String edificio;
    @Column(name = "nazione")
    private String nazione;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    /////////////////////////
    @Column(name = "description")
    private String description;
    @Column(name = "indirizzoNoCAP")
    private String indirizzoNoCAP;
    @Column(name = "postalCode")
    private String postalCode;
    @Column(name = "fax")
    private String fax;
    /////////////////////////////////
    @Column(name = "indirizzoHasNumber")
    private String indirizzoHasNumber;

    public Integer getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Integer doc_id) {
        this.doc_id = doc_id;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getNazione() {
        return nazione;
    }

    public void setNazione(String nazione) {
        this.nazione = nazione;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndirizzoNoCAP() {
        return indirizzoNoCAP;
    }

    public void setIndirizzoNoCAP(String indirizzoNoCAP) {
        this.indirizzoNoCAP = indirizzoNoCAP;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getIndirizzoHasNumber() {
        return indirizzoHasNumber;
    }

    public void setIndirizzoHasNumber(String indirizzoHasNumber) {
        this.indirizzoHasNumber = indirizzoHasNumber;
    }

    public GeoDomainDocument (){}
   

    /*
     * GeoDocument da inserire nella tabrella InfoDocument
     * @param url
     * @param regione
     * @param provincia
     * @param city
     * @param indirizzo
     * @param iva
     * @param email
     * @param telefono
     * @param fax
     * @param edificio
     * @param latitude
     * @param longitude
     * @param nazione
     * @param description
     * @param postalCode
     * @param indirizzoNoCAP
     */
    public GeoDomainDocument(URL url, String regione, String provincia, String city,
			String indirizzo,String iva, String email, String telefono,String fax,
			String edificio,Double latitude, Double longitude,String nazione,String description,
                        String postalCode,String indirizzoNoCAP,String indirizzoHasNumber) {
		this.url = url;
		this.regione = regione;
        this.provincia = provincia;
		this.city = city;		
		this.indirizzo = indirizzo;
                this.iva = iva;
		this.email = email;
		this.telefono = telefono;
                this.fax = fax;
		this.edificio = edificio;
		this.latitude = latitude;
		this.longitude = longitude;
                this.nazione = nazione;
                this.description = description;
                this.indirizzoNoCAP = indirizzoNoCAP;
                this.postalCode = postalCode;
                this.indirizzoHasNumber = indirizzoHasNumber;
	}

    public GeoDomainDocument(Integer doc_id,URL url, String regione, String provincia, String city,
                             String indirizzo,String iva, String email, String telefono,String fax,
                             String edificio,Double latitude, Double longitude,String nazione,String description,
                             String postalCode,String indirizzoNoCAP,String indirizzoHasNumber) {
        this.doc_id = doc_id;
        this.url = url;
        this.regione = regione;
        this.provincia = provincia;
        this.city = city;
        this.indirizzo = indirizzo;
        this.iva = iva;
        this.email = email;
        this.telefono = telefono;
        this.fax = fax;
        this.edificio = edificio;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nazione = nazione;
        this.description = description;
        this.indirizzoNoCAP = indirizzoNoCAP;
        this.postalCode = postalCode;
        this.indirizzoHasNumber = indirizzoHasNumber;
    }



    @Override
    public String toString() {
        String s =
                "******************************************************************************************************" + System.getProperties().get("line.separator") + 
                "GeoDomainDocument{" 
                + "doc_id = " + doc_id 
                + ", url = " + url 
                + ", regione = " +  regione 
                + ", provincia = " + provincia 
                + ", city = " + city 
                + ", indirizzo = " + indirizzo 
                + ", iva = " + iva 
                + ", email = " + email 
                + ", telefono = " + telefono 
                + ", edificio = " + edificio 
                + ", nazione = " + nazione 
                + ", lat = " + latitude
                + ", lng = " + longitude
                + ", description = " + description 
                + ", indirizzoNoCAP = " + indirizzoNoCAP 
                + ", postalCode=" + postalCode 
                + ", fax = " + fax 
                + ", indirizzoHasNumber = " + indirizzoHasNumber 
                + "}" 
                + System.getProperties().get("line.separator") 
                + "******************************************************************************************************" + System.getProperties().get("line.separator");
        return s;
    }
    
}
