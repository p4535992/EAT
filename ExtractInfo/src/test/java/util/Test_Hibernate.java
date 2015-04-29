/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.TransformerConfigurationException;

import object.model.Website;
import org.xml.sax.SAXException;

/**
 *
 * @author Marco
 */
public class Test_Hibernate {
    
    
     public static void main(String args[]) throws NullPointerException, InterruptedException, InvocationTargetException, SAXException, IOException,TransformerConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException{

         //String path= System.getProperty("user.dir")+"\\src\\main\\java\\util\\hibernate\\cfg\\urldb\\hibernate.cfg.xml";
        // String path = "C:\\Users\\Marco\\IdeaProjects\\ExtractInfo\\src\\main\\java\\util\\hibernate\\cfg\\urldb\\hibernate.cfg.xml";
         //String path = "C:\\Users\\Marco\\IdeaProjects\\ExtractInfo\\src\\main\\resources\\hibernate\\cfg\\urldb\\hibernate.cfg.xml";
         String path = "resources/hibernate.cfg.xml";
         File file = new File(path);

         //XMLUtil.readXMLFileAndPrint(file);

         //XMLUtil.updateValueOfAttribute(file, "class", "table", "xxx");
         
         //XMLUtil.updateValueOfattributeSAX(path, "class", "table", "XXX");
         

     }
}
