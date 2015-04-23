package spring.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marco on 21/04/2015.
 */
public class BeansKit<T> {

    private static ApplicationContext context;
    private static AbstractApplicationContext abstractContext;

    public void getBean(T model,String nameOfBean,String pathXmlBean){
        // create and configure beans
        AbstractApplicationContext context = new ClassPathXmlApplicationContext(pathXmlBean);
        T obj = (T) context.getBean(nameOfBean);
        //obj.getMessage();
        context.registerShutdownHook();
    }

    public T getBean(T typeService,String nameOfBean, Class<T> requiredType,String pathXmlBean){
        // create and configure beans
        context = new ClassPathXmlApplicationContext(pathXmlBean);
        // retrieve configured instance
        T service = context.getBean(nameOfBean, requiredType);
        return service;
    }


    public static ApplicationContext tryGetContextSpring(String filePathXml) throws IOException {
        if(new File(filePathXml).exists()) {
            try {
                //This container loads the definitions of the beans from an XML file.
                // Here you do not need to provide the full path of the XML file but
                // you need to set CLASSPATH properly because this container will look
                // bean configuration XML file in CLASSPATH.
                context = new ClassPathXmlApplicationContext(filePathXml);
            } catch (Exception e1) {
                    //This container loads the definitions of the beans from an XML file.
                    // Here you need to provide the full path of the XML bean configuration file to the constructor.
                    context = new FileSystemXmlApplicationContext(new File(filePathXml).getAbsolutePath());
                try{
                    AbstractApplicationContext abstractContext = new ClassPathXmlApplicationContext(filePathXml);
                    context = abstractContext;
                }catch(Exception e2){
                    try{
                        XmlBeanFactory factory = new XmlBeanFactory (new ClassPathResource(filePathXml));
                    }catch(Exception e3){
//                        //This container loads the XML file with definitions of all beans from within a web application.
//                        try{
//                            context = new XmlWebApplicationContext();
//                        }catch(Exception e4){
//                            e1.printStackTrace();
//                        }
                        e3.printStackTrace();
                    }
                }
            }
        }else{
            throw new IOException("Not found the comnfiguration spring file!");
        }
        return context;
    }


    public static ApplicationContext tryGetContextSpring(String[] filesPathsXml) throws IOException {
        boolean check =true;
        for(String spath : filesPathsXml){
            if(new File(spath).exists()) {}
            else{check = false;}
        }
        if(check) {
            try {
                //This container loads the definitions of the beans from an XML file.
                // Here you do not need to provide the full path of the XML file but
                // you need to set CLASSPATH properly because this container will look
                // bean configuration XML file in CLASSPATH.
                context = new ClassPathXmlApplicationContext(filesPathsXml);
            }
            catch (Exception e1) {
                try{
                    AbstractApplicationContext abstractContext = new ClassPathXmlApplicationContext(filesPathsXml);
                    context = abstractContext;
                }catch(Exception e2){
                        e2.printStackTrace();
                }
            }

        }else{
            throw new IOException("Not found the one or more comnfiguration spring file!");
        }
        return context;
    }
}
