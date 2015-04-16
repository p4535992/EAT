package util.gate;
import object.support.AnnotationInfo;
import gate.*;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * ManageAnnotationAndContent.java. Estrai il contenuto dell annotazioni
 * semantiche da ogni documento del Corpus Struttura il contenuto in un oggetto
 * Java Keyword Per ogni documento da cui è estratta una Keyword inseriamo la
 * Keyword in una lista da utilizzare successivamente per l'inserimento nel
 * database
 */
public class ManageAnnotationAndContent {

    //private ArrayList<AnnotationInfo> annInfo = new ArrayList<AnnotationInfo>();
    private AnnotationInfo annInfo = new AnnotationInfo(null,null, null, null, null, null, null, null, null, null, null);

    public ManageAnnotationAndContent() {}
    
    /**
     * Il metodo principale,come dice il nome prende le Keyword dal Corpus di
     * documenti.
     * @param corpus
     * @param language identifica il linguaggio viene inviato come input a null
     * per un verificare una condizione ma niente vieta di invocarlo in questa
     * classe.
     * @return una lista di AnnotationInfo cioè il contenuto delle informazioni
     * semantiche strutturato
     */
     public AnnotationInfo getSingleAnnotationInfo(Corpus corpus, String language) {
         //prendiamo ogni documento del corpus su cui sono stati fatte
        //passare le nostre regole JAPE con le nostre annotazioni (Annotation)
        Iterator iter = corpus.iterator();
        while (iter.hasNext()) {
            Document doc = (Document) iter.next();
            //Essenziali per il nostro studio
            URL url = doc.getSourceUrl();
            String regione = takeTheContentOfTheAnnotation(doc, "MyRegione");
            regione = manageString23(regione);
            if(setNullForEmptyString(regione)!=null){regione.trim();}
            
            String provincia = takeTheContentOfTheAnnotation(doc, "MyProvincia");
            provincia = manageString23(provincia);
            if(setNullForEmptyString(provincia)!=null){provincia.trim();}
            
            String localita = takeTheContentOfTheAnnotation(doc, "MyLocalita");
            localita = manageString23(localita);
            if(setNullForEmptyString(localita)!=null){localita.trim();}
             
            String indirizzo = takeTheContentOfTheAnnotation(doc, "MyIndirizzo");             
            indirizzo = manageString23(indirizzo);         
            if(setNullForEmptyString(indirizzo)!=null){indirizzo.trim();}
             
            String edificio = takeTheContentOfTheAnnotation(doc, "MyEdificio");      
            edificio = manageString23(edificio);
            if(setNullForEmptyString(edificio)!=null){edificio.trim();}
                                   
            String iva= takeTheContentOfTheAnnotation(doc,"MyPartitaIVA");           
            iva = manageString23(iva);   
            if(setNullForEmptyString(iva)!=null){iva.trim();}
            
            String email= takeTheContentOfTheAnnotation(doc,"MyEmail");
            email = manageString23(email);
            if(setNullForEmptyString(email)!=null){email.trim();}
            
            String telefono= takeTheContentOfTheAnnotation(doc,"MyPhone");
            telefono = manageString23(telefono);
            if(setNullForEmptyString(telefono)!=null){telefono.trim();}
            
            String fax= takeTheContentOfTheAnnotation(doc,"MyFax");
            fax = manageString23(fax);
            if(setNullForEmptyString(fax)!=null){fax.trim();}
            //Identificazione del linguaggio
            String lang = "";
            if (language == null) {
                try {
                    lang = doc.getFeatures().get("LanguageType").toString();
                } catch (NullPointerException e) {
                    lang = language;
                }             
            } else {
                lang = language;
            }
            annInfo = new AnnotationInfo(url, regione, provincia, localita, indirizzo, iva, email, telefono,fax, edificio, lang);        
        }//while   
        return annInfo;
    }//getKeyword
     
    /**
     * Il metodo principale,come dice il nome prende le Keyword dal Corpus di
     * documenti.
     * @param corpus corpus dei documenti
     * @param language identifica il linguaggio viene inviato come input a null
     * per un verificare una condizione ma niente vieta di invocarlo in questa
     * classe.
     * @return una lista di AnnotationInfo cioè il contenuto delle informazioni
     * semantiche strutturato
     */
     public ArrayList<AnnotationInfo> getMultipleAnnotationInfo(Corpus corpus, String language) {
         //prendiamo ogni documento del corpus su cui sono stati fatte
        //passare le nostre regole JAPE con le nostre annotazioni (Annotation)
        ArrayList<AnnotationInfo> listAnnInfo = new ArrayList<AnnotationInfo>();
        Iterator iter = corpus.iterator();
        while (iter.hasNext()) {
            Document doc = (Document) iter.next();
            //Essenziali per il nostro studio
            URL url = doc.getSourceUrl();
            String regione = takeTheContentOfTheAnnotation(doc, "MyRegione");
            regione = manageString23(regione);
            if(setNullForEmptyString(regione)!=null){regione.trim();}
                       
            String provincia = takeTheContentOfTheAnnotation(doc, "MyProvincia");
            provincia = manageString23(provincia);
            if(setNullForEmptyString(provincia)!=null){provincia.trim();}
            
            String localita = takeTheContentOfTheAnnotation(doc, "MyLocalita");
            localita = manageString23(localita);
            if(setNullForEmptyString(localita)!=null){localita.trim();}
             
            String indirizzo = takeTheContentOfTheAnnotation(doc, "MyIndirizzo");            
            indirizzo = manageString23(indirizzo);
            if(setNullForEmptyString(indirizzo)!=null){indirizzo.trim();}
             
            String edificio = takeTheContentOfTheAnnotation(doc, "MyEdificio");      
            edificio = manageString23(edificio);
            if(setNullForEmptyString(edificio)!=null){edificio.trim();}
                                   
            String iva= takeTheContentOfTheAnnotation(doc,"MyPartitaIVA");           
            iva = manageString23(iva);   
            if(setNullForEmptyString(iva)!=null){iva.trim();}
            
            String email= takeTheContentOfTheAnnotation(doc,"MyEmail");        
            email = manageString23(email);
            if(setNullForEmptyString(email)!=null){email.trim();}
            
            String telefono= takeTheContentOfTheAnnotation(doc,"MyPhone");
            telefono = manageString23(telefono);
            if(setNullForEmptyString(telefono)!=null){telefono.trim();}
            
            String fax= takeTheContentOfTheAnnotation(doc,"MyFax");
            fax = manageString23(fax);
            if(setNullForEmptyString(fax)!=null){fax.trim();}
            //Identificazione del linguaggio
            String lang = "";
            if (language == null) {
                try {
                    lang = doc.getFeatures().get("LanguageType").toString();
                } catch (NullPointerException e) {
                    lang = language;
                }          
            } else {
                lang = language;
            }
            annInfo = new AnnotationInfo(url, regione, provincia, localita, indirizzo, iva, email, telefono, fax, edificio, lang);          
            listAnnInfo.add(annInfo);
        }//while
        return listAnnInfo;
    }//getKeyword

    /**
     * Prende il contenuto delle annotazioni sottoforma di stringa.
     * @param doc il GATE Document preso in esame
     * @param nameAnnotation il nome dell'annotazione su cui lavoariamo
     * @return il contenuto dell'annotazione voluta.
     */
    private String takeTheContentOfTheAnnotation(Document doc, String nameAnnotation) {
        ArrayList<String> listAnnSet = new ArrayList<String>();
        boolean flag = false;
        String content = "";
        //set di annotazioni che lavora all'interno del tag HEAD del documento HTML
        listAnnSet.add("MyHEAD");
        //set di annotazioni che lavora all'interno del tag FOOTER del documento HTML
        listAnnSet.add("MyFOOTER");
        //set di annotazioni per gli speciali id
        listAnnSet.add("MySpecialID");
       //set di annotazioni che lavora su tutto il documento indipendemente dal fatto
        //che sia HTML o pdf o altro
        listAnnSet.add("MyAnnSet");
       //Cerchiamo le annotazioni rispettive ai vari AnnotationSet nell'
        //ordine con cui le abbiamo inserite.
       for(String nameAnnotationSet: listAnnSet){
                if (flag == false) {
                    //System.err.println(nameAnnotationSet.toString());
                    AnnotationSet annSet = setAnnotationSetWithDoc(nameAnnotationSet, doc);                    
                        String content2 = customAnn(doc, annSet, nameAnnotation);
                        content = content + " " + content2;
                    if (setNullForEmptyString(content) == null) {
                        continue;                  
                    } else if (setNullForEmptyString(content) != null) {
                        flag = true;                     
                        return content;
                    }//secondo if
                    else{return null;}
                }//primo if
            //}//try          
        }//for
        content = setNullForEmptyString(content);
        return content;
    }//takeTheContentOTheAnnotation

    /**
     * Meccanismo di estrazione del contenuto delle Annotazioni Semantiche.
     * @param doc il GATE Document preso in esame
     * @param dateSet l'AnnotationSet (set di annotazioni)di GATE preso in esame
     * @param stringNameAnnotation l'Annotation (annotazione) di GATE preso in
     * esame
     * @return in forma di stringa il contenuto dell'annotazione
     * stringNameAnnotation all'interno del set di annotazioni dateSet
     */
    private String customAnn(Document doc, AnnotationSet dateSet, String stringNameAnnotation) {
        String content = "";
        List annList = new ArrayList<Annotation>();
        ArrayList<String> stringList = new ArrayList<String>();
        int begOffset;
        int endOffset;      
        int x = 0;
        int y = 0;       
        try {
            //Prendi tutte le annotazioni di tipo stringNameAnnotation all'interno del 
            //set di annotazioni date.set 
            AnnotationSet commentTokensAs = dateSet.get(stringNameAnnotation);
            //prendi il testo del documento ma viene chiamato solo per l'HEAD e il FOOTER
            //quindi non pesa troppo, per il body si va a prendere dirattamente il risultato delle regole JAPE
            //il perchè è che voglio essere sicuro di non mancare niente nell'HEAD e il FOOTER
            String myDoc = doc.getContent().toString();
            //crea una lista di annotazioni, aiuta a gestire il tutto
            for (Annotation commentTokenAnn : commentTokensAs) {
                annList.add(commentTokenAnn);              
            }
            //Itera attraverso le annotazioni trovate
            for (int i = 0; i < annList.size(); i++) {
                Annotation anAnn = (Annotation) annList.get(i);                             
                //Modificato il 29/10/2014
                //La successiva linea di codice permette di vedere le 4 fasi di analisi di 
                //un documento html con i 4 diversi annotation set.
                /*System.err.println(dateSet.getName());*/
                if (dateSet.getName().equals("MyAnnSet") || dateSet.getName().equals("MySpecialID")){
                    //prendo il contenuto della stringa                 
                    //String content2 = doc.getContent().getContent(anAnn.getStartNode().getOffset(), anAnn.getEndNode().getOffset()).toString();
                     String content2 = getLastAnnotationOnTheAnnotionSetWithSpecificType(doc, dateSet, stringNameAnnotation);
                    /* System.out.println("2["+stringNameAnnotation+"]:"+content2);*/
                    //Questo pezzo di codice serve solo a evitare di prendere due numeri di telefono,email,ecc. 
                    //uguali 
                    if (!(stringList.contains(content2))) {
                        stringList.add(content2);
                        for (String s : stringList) {
                            if (!(s.contains(content2))) {   
                                continue;
                            } else {
                               //Ho deciso di mettere i numeri di telefono uno dietro l'altro in un unica
                                //stringa concatenata separati d separati da ";" poi volendo con uno split 
                                //possiamo ricavarci la lista dei numeri ( a noi non interessa)
                                /*Modificato il 19-09-2014*/
                                content = content + ";" + content2;                                                          
                            }//if !(s.contains(content2))
                        }//for(String s : stringList)                                            
                    }//!(stringList.contains(content2)
            //Modificato il 29/10/2014
            }else if (dateSet.getName().equals("MyHEAD") || dateSet.getName().equals("MyFOOTER")) {
                    /*System.out.println("3["+stringNameAnnotation+"]");*/
                    begOffset = anAnn.getStartNode().getOffset().intValue();
                    endOffset = anAnn.getEndNode().getOffset().intValue();
                    if (i == 0) {
                        x = begOffset;
                        y = endOffset;
                    } else {
                        if (x > begOffset) {
                            x = begOffset;
                        }
                        if (y < endOffset) {
                            y = endOffset;
                        }
                    }
                    content = myDoc.substring(x, y);
                    if (i == annList.size()) {
                        return content;
                    }//if 
                }//else
                if(setNullForEmptyString(content)!=null){
                    break;
                }
            }//for   
        } catch (NullPointerException ep) {
            Logger.getLogger(ManageAnnotationAndContent.class.getName()).log(Level.SEVERE, null, ep);
        }       
        content = removeFirstAndLast(content, "+");
        return content;
    }//customAnn    

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    //METODI DI SUPPORTO
    //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Prende un determinato set di annotazione per un determinato documento.
     * @param nameAnnotationSet nome del set di annotazioni
     * @param doc il GATE Document preso in esame
     * @return il GATE Document preso in esame ma solo la parte coperta dal set
     * di annotazioni
     */
    private AnnotationSet setAnnotationSetWithDoc(String nameAnnotationSet, Document doc) {
        String nameAnnSet = nameAnnotationSet;
        AnnotationSet annSet = doc.getAnnotations(nameAnnSet);
        return annSet;
    }

    /**
     * Metodo per strutturare il contenuto dell'annotazione.
     * @param address il contenuto dell'annotazione sottoforma di stringa
     * @return il contenuto struttturato
     */
    private String manageArrayString(String address, String symbol) {
        String sgm = address;
        sgm = sgm.replaceAll("\\W", symbol);
        sgm = sgm.replaceAll("(\\" + symbol + ")\\1+", symbol);
        return sgm;
    }

    /**
     * Metodo evita di ripetere due volte la stessa informazione all'interno
     * dello stesso contenuto della medesima annotazione.
     * @param fua il contentuo dell'annotazione sottoforma di stringa
     * @param symbol il simbolo separatore imposto durante l'estrazione del
     * contenuto
     * @return ritorna il contenuto "filtrato" dell'anotazione
     */
    private String reduceStringv2(String fua, String symbol) {
        String reduce = "";
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(fua, symbol);
        while (st.hasMoreElements()) {
            String s = st.nextToken();
            s = manageArrayString(s, symbol);
            StringTokenizer st2 = new StringTokenizer(s, symbol);
            while (st2.hasMoreElements()) {
                String s2 = st2.nextToken();
                if (!(list.contains(s2)) && setNullForEmptyString(s2) != null) {
                    list.add(s2);
                }
            }
        }

        for (String s : list) {
            reduce = reduce + "+" + s;
            reduce = removeFirstAndLast(reduce, "+");
        }
        return reduce;
    }

    /**
     * Rimuove il simbolo separatore del contentuo se presente all'inzio o alla
     * fine della stringa.
     * @param fua il contentuo dell'annotazione sottoforma di stringa
     * @param symbol il simbolo separatore del contenuto
     * @return la stringa del contenuto senza simboli separatori all'inizio e
     * alla fine
     */
    private String removeFirstAndLast(String fua, String symbol) {
        if (fua != "") {
            //fua= fua.substring(0,fua.length()-1);
            //fua = fua.replaceAll("(\\+)\\1+","+");
            fua = fua.replaceAll("(\\" + symbol + ")\\1+", symbol);
            if (fua.substring(0, 1).contains(symbol)) {
                fua = fua.substring(1, fua.length());
            }
            if (fua.substring(fua.length() - 1, fua.length()).contains(symbol)) {
                fua = fua.substring(0, fua.length() - 1);
            }
        }
        return fua;
    }

    /**
     * Setta a null se verifica che la stringa non è nulla, non è vuota e non è
     * composta da soli spaceToken (white space).
     * @param s stringa di input
     * @return il valore della stringa se null o come è arrivata
     */
    private String setNullForEmptyString(String s) {
        if (s != null && !s.isEmpty() && !s.trim().isEmpty()) {
            return s;
        } else {
            return null;
        }
    }
    /**
     * Metodo che prende una lista di annotazioni dello stesso tipo
     * e poi decide di prendere l'ultimo tracciato semplicemente per un ragionamento
     * logico , questo metodo viene attivato solo dal set di annotazioni MyAnnSet e
     * MySpecialID in entrambi i casi in mancanza di FOOTER e HEADER l'ultima annotazione
     * di quel tipo riscontrata è probabilemnte la più esatta a rappresentare la pagina
     * web.
     * @param doc il GATE Document che si sta studiando
     * @param dateSet l'AnnotationSet di riferimento (MyAnnSet o MySpecialID)
     * @param stringNameAnnotation il tipo dell'annotazione in formato stringa
     * @return l'ultima annotazione di quel tipo per quell'AnnotationSet per il dato documento
     */
    private String getLastAnnotationOnTheAnnotionSetWithSpecificType(Document doc, AnnotationSet dateSet, String stringNameAnnotation){
      Integer i = 0;
      Integer j = 0;
      String content="";
      for(Annotation anAnn: dateSet){
          if(anAnn.getType().equals(stringNameAnnotation)){
          //System.out.println("["+anAnn.getType()+"("+anAnn.getId()+")]:"+content);
          if(i==0){i = anAnn.getId();content = gate.Utils.stringFor(doc, anAnn);}
          if(i > 0){
              j = anAnn.getId();
              if( j > i){         
              content = gate.Utils.stringFor(doc, anAnn);
              //System.out.println("["+anAnn.getType()+"("+anAnn.getId()+")]:"+content);
              }else{continue;}
          }//if(i >0)
          }
      }//for
      /*System.out.println("["+stringNameAnnotation+"]:"+content);*/
      return content;
    }
   
    /**
     * Metodo n°3 per la manutenzione delle stringhe.
     * @param s la stringa di input
     * @return la stringa di output
     */
    private String manageString3(String s){
        try{
            if(s!=null){          
              String[] su = s.split(";");
              //l'ultimo elemento della array splittato:
              return su[su.length -1];
              //return su[1];
            }else{         
              return null;
            }
        }catch(Exception e){return null;}
    }
    /**
     * Metodo n°23 per la manutenzione delle stringhe.
     * @param content la stringa di input
     * @return la stringa di output
     */
    private String manageString23(String content){   
        try{
            if(content.contains(";")){content = manageString3(content); return content;}
            if(content.equals(null)){return null;}
            else{
                //content = manageString(content); 
                return content;}
         
        }catch(Exception e){return null;}
    }

}//ManageAnnotationAndContent.java

