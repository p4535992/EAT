package util.gate;

import gate.*;
import gate.corpora.RepositioningInfo;
import gate.gui.MainFrame;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import util.FileUtil;
import util.SystemLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Marco on 17/04/2015.
 */
public class GateKit {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GateKit.class);

    /** The Corpus Pipeline application to contain ANNE,Lingpipe,Tools,ecc. */
    private static boolean showGate = false;
    private static CorpusController controller;
    private static File gappFile;
    private static Document gateDoc;

    public static boolean isShowGate() {
        return showGate;
    }

    public static void setShowGate(boolean showGate) {
        GateKit.showGate = showGate;
    }

    public static File getGappFile() {
        return gappFile;
    }

    public static void setGappFile(File gappFile) throws Exception {
       GateKit.gappFile = gappFile;
    }

    public static CorpusController getController() {
        return controller;
    }

    public static void setController(CorpusController controller) {
        GateKit.controller = controller;
    }

    public static void setUpGateEmbedded(String directoryFolderHome,String directoryFolderPlugin,
                            String configFileGate,String configFileUser,String configFileSession) throws Exception {
        //SETTIAMO GATE PER IL PROGRAMMA
        SystemLog.message("Inizializzazione GATE...");
        directoryFolderHome = System.getProperty("user.dir")+File.separator+directoryFolderHome;
        if(!new File(directoryFolderHome).exists())throw new Exception("WARNING: The folder directoryFolderHome of GATE not exists!");
        Gate.setGateHome(new File(directoryFolderHome));

        directoryFolderPlugin = directoryFolderHome + File.separator + directoryFolderPlugin;
        if(!new File(directoryFolderPlugin).exists())throw new Exception("WARNING: The folder directoryFolderPlugin of GATE not exists!");
        Gate.setPluginsHome(new File(directoryFolderPlugin));

        configFileGate = directoryFolderHome + File.separator + configFileGate;
        if(!new File(configFileGate).exists())throw new Exception("WARNING: The configFileGate of GATE not exists!");
        Gate.setSiteConfigFile(new File(configFileGate));

        configFileUser = directoryFolderHome + File.separator + configFileUser;
        if(!new File(configFileUser).exists())throw new Exception("WARNING: The configFileUser of GATE not exists!");
        Gate.setUserConfigFile(new File(configFileUser));

        configFileSession = directoryFolderHome + File.separator + configFileSession;
        if(!new File(configFileSession).exists())throw new Exception("WARNING: The configFileSession of GATE not exists!");
        Gate.setUserSessionFile(new File(configFileSession));
        try {
            Gate.init();
        }catch(gate.util.GateException e){
            //..Usuallly you got here for bad reading of the session file
            FileUtil.createFile(configFileSession);
            Gate.init();
        }
        SystemLog.message("...GATE initialised");
        if(showGate) {//Lavorare con l'interfaccia grafica di GATE
            MainFrame.getInstance().setVisible(true);
        }
    }

    /**
     * Initialise the GATE system. This creates a "corpus pipeline"
     * application that can be used to run sets of documents through
     * the extraction system.
     */
    public static void loadGapp(String base,String fileGapp) throws GateException, IOException {
        SystemLog.message("Loading file .gapp/.xgapp...");
        //File gapp = new File(home, "custom/gapp/geoLocationPipelineFast.xgapp");
        controller = (CorpusController) PersistenceManager.loadObjectFromFile(
                new File(Gate.getGateHome() + File.separator + base + File.separator  + fileGapp));
        //CorpusController  con = (CorpusController) PersistenceManager.loadObjectFromFile(gapp);
        SystemLog.message("... file .gapp/.xgapp loaded!");
    } // initAnnie()

    public static void setUpGateEmbeddedWithSpring(){

    }

    public static Set<String> countAnnotationsOnTheWebPage(Document doc){
        //codice aggiuntivo utile per avere un'idea del contenuto della pagina web
        // obtain a map of all named annotation sets
        Set<String> annotTypes= null;
        Map<String, AnnotationSet> namedASes = doc.getNamedAnnotationSets();
        System.out.println("No. of named Annotation Sets:"+ namedASes.size());
        // number of annotations each set contains
        for (String setName : namedASes.keySet()) {
            // annotation set
            AnnotationSet aSet = namedASes.get(setName);
            // number of annotations
            SystemLog.message("No. of Annotations for " + setName + ":" + aSet.size());
            // all annotation types
            annotTypes = aSet.getAllTypes();
            for(String aType : annotTypes) {
                System.out.print(" " + aType + ": " + aSet.get(aType).size()+"||");
            }//for aType
        }//for setName
        return annotTypes;
    }

    public static class SortedAnnotationList extends Vector {
        public SortedAnnotationList() {
            super();
        } // SortedAnnotationList

        public boolean addSortedExclusive(Annotation annot) {
            Annotation currAnot = null;
            // overlapping check
            for (int i=0; i<size(); ++i) {
                currAnot = (Annotation) get(i);
                if(annot.overlaps(currAnot)) {
                    return false;
                } // if
            } // for
            long annotStart = annot.getStartNode().getOffset().longValue();
            long currStart;
            // insert
            for (int i=0; i < size(); ++i) {
                currAnot = (Annotation) get(i);
                currStart = currAnot.getStartNode().getOffset().longValue();
                if(annotStart < currStart) {
                    insertElementAt(annot, i);
                    SystemLog.message("Insert start: " + annotStart + " at position: " + i + " size=" + size());
                    SystemLog.message("Current start: " + currStart);
                    return true;
                } // if
            } // for
            int size = size();
            insertElementAt(annot, size);
            SystemLog.message("Insert start: " + annotStart + " at size position: " + size);
            return true;
        } // addSorted
    } // SortedAnnotationList


    /**
     * Method to convert every gate document in a coprus in xml files with all the annotations you gate in them
     * @param corpus
     * @param addAnnotTypesRequired
     * @throws IOException
     */
    public static void createXMLFileForEachDoc(Corpus corpus,ArrayList<String> addAnnotTypesRequired) throws IOException{
        // for each document, get an XML document with the person,location,MyGeo names added
        Iterator iter = corpus.iterator();
        int count = 0;
        String startTagPart_1 = "<span GateID=\"";
        String startTagPart_2 = "\" title=\"";
        String startTagPart_3 = "\" style=\"background:Red;\">";
        String endTag = "</span>";

        while(iter.hasNext()) {
            Document doc = (Document) iter.next();
            AnnotationSet defaultAnnotSet = doc.getAnnotations();
            Set annotTypesRequired = new HashSet();
            for (String s : addAnnotTypesRequired) {
                annotTypesRequired.add(s);
                System.out.println(s);
            }
            // annotTypesRequired.add("Person");
            // annotTypesRequired.add("Location");
            Set<Annotation> newSetAnnotation =
                    new HashSet<Annotation>(defaultAnnotSet.get(annotTypesRequired));

            FeatureMap features = doc.getFeatures();
            String originalContent = (String)
                    features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
            RepositioningInfo info = (RepositioningInfo)
                    features.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);
            ++count;

            ///GENERAZIONE DEI DOCUMENTI
            String nameGateDocument0 = doc.getName();
            //(1)doc_0_http:\ 12
            // String str0 = nameGateDocument0.substring(13, nameGateDocument0.length()-5).replaceAll("\", "");
            String str0 = "";
            String fileName0 = "("+count+")"+str0+".html";
            //C:\Users\Utente\Desktop\WebApplication4\src\java\documents\OriginalContentNonNull
            File dir0 = new File ("src/java/documents/OriginalContentNonNull");
            File file = new File (dir0, fileName0);
            System.out.println("File name: '"+file.getAbsolutePath()+"'");
            if(originalContent != null && info != null) {
                System.out.println("OrigContent and reposInfo existing. Generate file...");

                Iterator it = newSetAnnotation.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

                while(it.hasNext()) {
                    currAnnot = (Annotation) it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while

                StringBuffer editableContent = new StringBuffer(originalContent);
                long insertPositionEnd;
                long insertPositionStart;
                // insert anotation tags backward
                System.out.println("Unsorted annotations count: "+newSetAnnotation.size());
                System.out.println("Sorted annotations count: "+sortedAnnotations.size());
                if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
                    for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                        currAnnot = (Annotation) sortedAnnotations.get(i);
                        insertPositionStart =
                                currAnnot.getStartNode().getOffset().longValue();
                        insertPositionStart = info.getOriginalPos(insertPositionStart);
                        insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
                        insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
                        if(insertPositionEnd != -1 && insertPositionStart != -1) {
                            editableContent.insert((int)insertPositionEnd, endTag);
                            editableContent.insert((int)insertPositionStart, startTagPart_3);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getType());
                            editableContent.insert((int)insertPositionStart, startTagPart_2);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getId().toString());
                            editableContent.insert((int)insertPositionStart, startTagPart_1);
                        } // if
                    } // for
                }// if size
                FileWriter writer = new FileWriter(file);
                writer.write(editableContent.toString());
                writer.close();
            } // if - should generate
            else if (originalContent != null) {
                System.out.println("OrigContent existing. Generate file...");

                Iterator it = newSetAnnotation.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

                while(it.hasNext()) {
                    currAnnot = (Annotation) it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while

                StringBuffer editableContent = new StringBuffer(originalContent);
                long insertPositionEnd;
                long insertPositionStart;
                // insert anotation tags backward
                SystemLog.message("Unsorted annotations count: " + newSetAnnotation.size());
                SystemLog.message("Sorted annotations count: " + sortedAnnotations.size());
                if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
                    for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                        currAnnot = (Annotation) sortedAnnotations.get(i);
                        insertPositionStart =
                                currAnnot.getStartNode().getOffset().longValue();
                        insertPositionEnd = currAnnot.getEndNode().getOffset().longValue();
                        if(insertPositionEnd != -1 && insertPositionStart != -1) {
                            editableContent.insert((int)insertPositionEnd, endTag);
                            editableContent.insert((int)insertPositionStart, startTagPart_3);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getType());
                            editableContent.insert((int)insertPositionStart, startTagPart_2);
                            editableContent.insert((int) insertPositionStart,
                                    currAnnot.getId().toString());
                            editableContent.insert((int)insertPositionStart, startTagPart_1);
                        } // if
                    } // for
                }//if size
                FileWriter writer = new FileWriter(file);
                writer.write(editableContent.toString());
                writer.close();
            }
            else {
                SystemLog.message("Content : " + originalContent);
                SystemLog.message("Repositioning: " + info);
            }

            String xmlDocument2 = doc.toXml(newSetAnnotation, false);
            String nameGateDocument2 = doc.getName();
            String str2 ="";
            String fileName2 = "("+count+")"+str2+".xml";
            File dir2 = new File ("src/java/documents/XML");
            File actualFile2 = new File (dir2, fileName2);
            FileWriter writer2 = new FileWriter(actualFile2);
            writer2.write(xmlDocument2);
            writer2.close();
        }
    }


}
