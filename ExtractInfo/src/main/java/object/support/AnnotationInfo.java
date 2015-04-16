/**
 * AnnotationInfo.java.
 * @author Tenti Marco Elaborato Sistemi Distribuiti
 * Un oggetto java in cui metto tutti contenuti delle annnotazioni semantiche che 
 * carico  dai documenti relativi all'url di riferimento.
 * L'annotazione di regione contiene ad esempio il testo tracciato coem informazioni della regione 
 */
package object.support;
import util.SystemLog;

import java.net.URL;

public class AnnotationInfo {
    
    private URL url;
    private String regione;
    private String provincia;
    private String localita;
    private String indirizzo;
    private String iva;
    private String email;
    private String telefono;
    private String fax;
    private String edificio;
    private String nazione;
    //OLD
    /*
    public AnnotationInfo(URL url,String regione,String provincia,String localita,
             String indirizzo,String iva,String email,String telefono,
             String edificio,String nazione){
        this.url=url;
        this.regione=regione;
        this.provincia=provincia;
        this.localita=localita;
        this.indirizzo=indirizzo;
        this.iva=iva;
        this.email=email;
        this.telefono=telefono;     
        this.edificio=edificio;
        this.nazione=nazione;
    }  
     */ 
     public AnnotationInfo(URL url,String regione,String provincia,String localita,
             String indirizzo,String iva,String email,String telefono,String fax,
             String edificio,String nazione){
        this.url=url;
        this.regione=regione;
        this.provincia=provincia;
        this.localita=localita;
        this.indirizzo=indirizzo;
        this.iva=iva;
        this.email=email;
        this.telefono=telefono;
        this.fax=fax;
        this.edificio=edificio;
        this.nazione=nazione;
    }  
       
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    public void setNazione(String nazione) {
        this.nazione = nazione;
    }


    public String getNazione() {
        return nazione;
    }

    public String getEdificio() {
        return edificio;
    }

    public URL getUrl() {
        return url;
    }

    public String getRegione() {
        return regione;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getLocalita() {
        return localita;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getIva() {
        return iva;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setUrl(URL url) {       
        this.url = url;       
    }

    public void setRegione(String regione) {
        this.regione = regione;       
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public void setLocalita(String localita) {
        this.localita = localita;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
     public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

	@Override
	public String toString() {
		String s = 
                "******************************************************************************************************"
                + System.getProperties().get("line.separator") +
                "AnnotationInfo [url=" + url + ", regione=" + regione
        + ", provincia=" + provincia + ", localita=" + localita
        + ", indirizzo=" + indirizzo + ", iva=" + iva + ", email="
        + email + ", telefono=" + telefono + ", edificio=" + edificio
        + ", nazione=" + nazione + "]" +
                "******************************************************************************************************"
                + System.getProperties().get("line.separator");
        SystemLog.write(s, "OUT");
        return s;
	}
    
    
          
}
