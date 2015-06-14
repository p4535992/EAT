package bean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ResourceLoader;
import com.p4535992.util.file.FileUtil;
import com.p4535992.util.log.SystemLog;

import java.io.*;
import java.net.MalformedURLException;

/**
 * Created by 4535992 on 21/04/2015.
 */
public class BeansKit<T> implements  org.springframework.context.ResourceLoaderAware{

    private static ApplicationContext context;
    private static AbstractApplicationContext abstractContext;
    private ResourceLoader resourceLoader;


    public T getBean(T model,String nameOfBean,String pathXmlBean){
        // create and configure beans
        AbstractApplicationContext context = new ClassPathXmlApplicationContext(pathXmlBean);
        T obj = (T) context.getBean(nameOfBean);
        //obj.getMessage();
        context.registerShutdownHook();
        return obj;
    }

    public T getBean(T typeService,String nameOfBean, Class<T> requiredType,String pathXmlBean){
        // create and configure beans
        context = new ClassPathXmlApplicationContext(pathXmlBean);
        // retrieve configured instance
        T service = context.getBean(nameOfBean, requiredType);
        return service;
    }


    public static ApplicationContext tryGetContextSpring(String filePathXml) throws IOException {
        ApplicationContext context = new GenericApplicationContext();
        try {
              /* if(filePathXml==null){
                file = contextFile.getAbsoluteFile();
            }else if(filePathXml.contains(":") && !filePathXml.contains("classes")) {
                file = new File(filePathXml);
            }else if(filePathXml.contains(":")){
                file = FileUtil.convertResourceToFile(filePathXml);
            }else{
                file = new File(filePathXml);
            }*/
            //This container loads the definitions of the beans from an XML file.
            // Here you do not need to provide the full path of the XML file but
            // you need to set CLASSPATH properly because this container will look
            // bean configuration XML file in CLASSPATH.
            String path = FileUtil.convertFileToStringUriWithPrefix(getResourceAsFile(filePathXml));
            context = new ClassPathXmlApplicationContext(path);
            if(context == null){
                //InputStream is = FileUtil.getResourceAsStream(BeansKit.class,filePathXml);
                context = new ClassPathXmlApplicationContext(filePathXml,BeansKit.class);
            }
        } catch (Exception e1) {
            try {
                //This container loads the definitions of the beans from an XML file.
                // Here you need to provide the full path of the XML bean configuration file to the constructor.
                context = new FileSystemXmlApplicationContext(new File(filePathXml).getAbsolutePath());
            }catch(Exception e2) {
                try {
                    AbstractApplicationContext abstractContext = new ClassPathXmlApplicationContext(filePathXml);
                    context = abstractContext;
                } catch (Exception e3) {
                        //XmlBeanFactory context = new XmlBeanFactory (new ClassPathResource(filePathXml));
//                        //This container loads the XML file with definitions of all beans from within a org.p4535992.mvc.webapp application.
//                        try{
//                            context = new XmlWebApplicationContext();
//                        }catch(Exception e4){
//                            e1.printStackTrace();
//                        }
                        SystemLog.exception(e3);
                }
            }
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
                //If it is a stand alone app, library, etc.. you would load your ApplicationContext as:
//                context = new ClassPathXmlApplicationContext(
//                        new String[] { "classpath:META-INF/conf/spring/this-xml-conf.xml",
//                                "classpath:META-INF/conf/spring/that-other-xml-conf.xml" } );
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


    public static InputStream getResourceAsStream( String name ) {
        return BeansKit.class.getClassLoader().getResourceAsStream(name);
    }

    public static File getResourceAsFile(String name){
        //ClassLoader classLoader = getClass().getClassLoader();
        //File file = new File(classLoader.getResource(filePathXml).getFile());
        return new File(BeansKit.class.getClassLoader().getResource(name).getFile());
    }

    public static String getResourceAsString(String fileName) {
        String result = "";
        //ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = org.apache.commons.io.IOUtils.toString(BeansKit.class.getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static File getResourceSpringAsFile(String fileName)
    {
        final org.springframework.core.io.Resource yourfile = new org.springframework.core.io.ClassPathResource(fileName);
        return (File) yourfile;
    }

    public static org.springframework.core.io.Resource getResourceSpringFromString(String uri) throws MalformedURLException {
        org.springframework.core.io.Resource resource;
        File file=new File(uri);
        if (file.exists()) {
            resource=new org.springframework.core.io.FileSystemResource(uri);
        }
        else   if (org.springframework.util.ResourceUtils.isUrl(uri)) {
            resource = new org.springframework.core.io.UrlResource(uri);
        }
        else {
            resource=new org.springframework.core.io.ClassPathResource(uri);
        }
        return resource;
    }

    public static String readResourceSpring(String fileLocationInClasspath)
    {
        String s = "";
        try {
            org.springframework.core.io.Resource resource =
                    new org.springframework.core.io.ClassPathResource(fileLocationInClasspath);
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            br.close();
            s = stringBuilder.toString();
        } catch (Exception e) {
            //LOGGER.org.p4535992.mvc.error(e);
        }
        return s;
    }

    public static String readResource( String name ) throws IOException {
        InputStream inputStream = getResourceAsStream( name );
        if( inputStream == null ) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[ 1024 ];
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
            int read;
            while( ( read = reader.read( buffer ) ) != -1 ) {
                stringBuilder.append( buffer, 0, read );
            }
        } finally {
            inputStream.close();
        }
        return stringBuilder.toString();
    }

    public static void printResourceSpringToConsole(org.springframework.core.io.Resource resource)
    {
        try{
            InputStream is = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void showResourceData(String absolutePathToFile) throws IOException
    {
        //This line will be changed for all versions of other examples : "file:c:/temp/filesystemdata.txt"
        org.springframework.core.io.Resource banner = resourceLoader.getResource("file:"+absolutePathToFile);
        InputStream in = banner.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            System.out.println(line);
        }
        reader.close();
    }

//  public static org.springframework.context.ApplicationContext createApplicationContext(String uri) throws MalformedURLException {
//    org.springframework.core.io.Resource resource = getSpringResourceFromString(uri);
//    //LOG.debug("Using " + resource + " from " + uri);
//    try {
//      return new ResourceXmlApplicationContext(resource) {
//        @Override
//        protected void initBeanDefinitionReader(org.springframework.beans.context.xml.XmlBeanDefinitionReader reader) {
//          reader.setValidating(true);
//        }
//      };
//    }
//  }
}
