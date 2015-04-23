/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.TransformerException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import util.xml.XMLKit;

/**
 *
 * @author Marco
 */
public class Test_Xml {
    
    
    //TEST
    
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException, JDOMException, org.jdom2.JDOMException, SAXException, TransformerException {
          String path= System.getProperty("user.dir")+
                 "\\src\\home\\utils\\xml\\hibernate.cfg.xml";
          File file = new File(path);
          // <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
          //XMLUtil2.updateInnerTextOfSpecificElement(path, "property", "name", "connection.driver_class", "XXXX");
          XMLKit.updateValueOfAttribute(file, "mapping", "resource", "YYY");
    }//main
    
}
