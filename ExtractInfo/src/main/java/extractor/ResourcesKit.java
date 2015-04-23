/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package extractor;

import org.springframework.core.io.ClassPathResource;
import java.io.*;
import java.net.MalformedURLException;


public class ResourcesKit {
  private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResourcesKit.class);
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

  public static InputStream getResourceAsStream( String name ) {
    return ResourcesKit.class.getClassLoader().getResourceAsStream(name);
  }

  public static File getResourceAsFile(String name){
    //ClassLoader classLoader = getClass().getClassLoader();
    //File file = new File(classLoader.getResource(filePathXml).getFile());
    return new File(ResourcesKit.class.getClassLoader().getResource(name).getFile());
  }

  public static String getResourceAsString(String fileName) {
    String result = "";
    //ClassLoader classLoader = getClass().getClassLoader();
    try {
      result = org.apache.commons.io.IOUtils.toString(ResourcesKit.class.getClassLoader().getResourceAsStream(fileName));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static File getSpringResourceAsFile(String fileName)
  {
      final org.springframework.core.io.Resource yourfile = new ClassPathResource(fileName);
      return (File) yourfile;
  }

  public static org.springframework.core.io.Resource getSpringResourceFromString(String uri) throws MalformedURLException {
    org.springframework.core.io.Resource resource;
    File file=new File(uri);
    if (file.exists()) {
      resource=new org.springframework.core.io.FileSystemResource(uri);
    }
    else   if (org.springframework.util.ResourceUtils.isUrl(uri)) {
      resource = new org.springframework.core.io.UrlResource(uri);
    }
    else {
      resource=new ClassPathResource(uri);
    }
    return resource;
  }

  public static String ReadSpringResources(String fileLocationInClasspath)
  {
    String s = "";
    try {
      org.springframework.core.io.Resource resource = new ClassPathResource(fileLocationInClasspath);
      BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()),1024);
      StringBuilder stringBuilder = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        stringBuilder.append(line).append('\n');
      }
      br.close();
      s = stringBuilder.toString();
    } catch (Exception e) {
      //LOGGER.error(e);
    }
    return s;
  }

  public static void printToConsoleResource(org.springframework.core.io.Resource resource)
  {
    //PRINT
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

//  public static org.springframework.context.ApplicationContext createApplicationContext(String uri) throws MalformedURLException {
//    org.springframework.core.io.Resource resource = getSpringResourceFromString(uri);
//    //LOG.debug("Using " + resource + " from " + uri);
//    try {
//      return new ResourceXmlApplicationContext(resource) {
//        @Override
//        protected void initBeanDefinitionReader(org.springframework.beans.factory.xml.XmlBeanDefinitionReader reader) {
//          reader.setValidating(true);
//        }
//      };
//    }
//  }




}
