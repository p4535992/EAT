package util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

/**
 * Created by Marco on 24/04/2015.
 */
public class Test_SILK {
    public static void main(String[] args) throws IOException {

        //File file = new File("C:\\Users\\Marco\\Documents\\GitHub\\EAT\\linking\\src\\main\\resources\\km4c-InfoDoc_M-wgs84_COORD_A\\triple_karma_output_20150215_181757-UTF8-c.ttl");
        //file = encodingFile(file);

        //file = new File("C:\\Users\\Marco\\Documents\\GitHub\\EAT\\linking\\src\\main\\resources\\km4c-InfoDoc_M-wgs84_COORD_A\\GraphTest_triples.n3");
        //file = encodingFile(file);
        File file = new File("C:\\Users\\Marco\\Documents\\GitHub\\EAT\\linking\\src\\main\\resources\\km4c-InfoDoc_M-wgs84_COORD_A\\SLS_km4c-InfoDoc_M-wgs84_COORD_A.xml");
        if(file.exists()) {
            //file,id del link, num thread, log
           de.fuberlin.wiwiss.silk.Silk.executeFile(file, "interlink_location", 2, true);
        }else{
            throw new IOException("File not found!!!");
        }
    }

    /**
     * @href: http://biercoff.com/malformedinputexception-input-length-1-exception-solution-for-scala-and-java/
     * @href: http://www.mkyong.com/java/how-to-write-to-file-in-java-fileoutputstream-example/
     * @param file
     * @return
     */
    public static File encodingFile(File file){
        FileInputStream input;
        String result = null;
        try {
            input = new FileInputStream(file);
            CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
            decoder.onMalformedInput(CodingErrorAction.IGNORE);
            InputStreamReader reader = new InputStreamReader(input, decoder);
            BufferedReader bufferedReader = new BufferedReader( reader );
            StringBuilder sb = new StringBuilder();
            String line = bufferedReader.readLine();
            while( line != null ) {
                sb.append( line );
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            result = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        //System.out.println(result);
        file.delete();
        try (FileOutputStream fop = new FileOutputStream(file)) {

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = result.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            //fop.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
