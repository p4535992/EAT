/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Marco
 */
public class Test_Jena {
    
    
     public static void main(String args[]) throws NullPointerException, InterruptedException, InvocationTargetException, SAXException, IOException,TransformerConfigurationException{  
         //GeoDocumentDAO dao = new GeoDocumentDAO();
         
         //home.utils.log log = new home.utils.log("xxx",".txt");
         String path= System.getProperty("user.dir")+
                 "\\karma_files\\output\\triple_karma_output_20150226_164851.n3";
         File file = new File(path);
         //EncodingUtil.rewriteTheFiLeToUtf8(file);
         
         JenaKit.readQueryAndCleanTripleInfoDocument(
                 FileUtil.filenameNoExt(file),
                 FileUtil.path(file),
                 "output",
                 FileUtil.extension(file),
                 "ttl"
         );
            
         //JENAUtil.ConvertRDFTo(file,"csv");
         //JENAUtil.ConvertRDFTo(file,"xml");
         
         //JENAUtil6.test();
        
     }
}
