package util;

import com.github.p4535992.extractor.object.dao.jdbc.IGeoDocumentDao;
import com.github.p4535992.extractor.object.dao.jdbc.IGeoDomainDocumentDao;
import com.github.p4535992.extractor.object.impl.jdbc.GeoDocumentDaoImpl;
import com.github.p4535992.extractor.object.impl.jdbc.GeoDomainDocumentDaoImpl;
import com.github.p4535992.extractor.object.model.GeoDocument;
import com.github.p4535992.extractor.object.model.GeoDomainDocument;
import com.github.p4535992.util.sql.SQLSupport;

import java.net.URL;
import java.util.List;

/**
 * Created by 4535992 on 11/05/2015.
 */
@SuppressWarnings("rawtypes")
public class Test_JDBC_util {

    public static void main(String[] args) throws Exception {

        IGeoDomainDocumentDao dao = new GeoDomainDocumentDaoImpl();
        IGeoDocumentDao dao2 = new GeoDocumentDaoImpl();
        GeoDocument geo = new GeoDocument(
                new URL("http://www.url.com"), "regione", "provincia", "city",
                "indirizzo", "iva", "email", "telefono", "fax",
                "edificio", 0.0, 0.0, "nazione", "description",
                "postalCode", "indirizzoNoCAP", "indirizzoHasNumber"
        );

        SQLSupport support = SQLSupport.getInstance(geo);

        dao.setDriverManager(
                "com.mysql.jdbc.Driver", "jdbc:mysql",
                "localhost","3306","siimobility","siimobility","geodb");
        dao.setTableSelect("geodocument");
        String[] columns = new String[]{"doc_id","url"};
        Object[] values = new Object[]{236,new URL("http://4bid.it")};
        dao2.setDriverManager(
                "com.mysql.jdbc.Driver", "jdbc:mysql",
                "localhost","3306","siimobility","siimobility","geodb");
        dao2.setTableInsert("geodocument2");
        dao2.insertAndTrim(geo);
        //TEST
        String q1 = dao.prepareSelectQuery(
                new String[]{columns[0]},new String[]{columns[0]},null,0,18,null);
        String sq1 = dao.prepareSelectQuery(
                new String[]{columns[0]},new String[]{columns[0]},values,0,18,null);
        String q2 = dao.prepareSelectQuery(columns, columns,values, null, null, "AND");
        String sq2 = dao.prepareSelectQuery(columns,columns,null, null, null, "AND");
        //String q2 ="SELECT * FROM geodocument WHERE doc_id  = '236' AND url  = 'http://4bid.it' ";


        String q3 = dao.prepareSelectQuery(new String[]{"url"},null,null,3,0,null);
        //WORK
        List<GeoDomainDocument> list = dao.trySelect(columns,columns,values,null,null,"AND");

        //WORK
        List<GeoDomainDocument> list2 = dao.trySelectWithRowMap(columns,columns,values,null,null,"AND");

        //WORK
        List<GeoDomainDocument> list3 = dao.trySelectWithResultSetExtractor(columns,columns,values,null,null,"AND");


    }
}
