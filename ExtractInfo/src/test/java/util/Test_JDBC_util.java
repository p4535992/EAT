package util;

import object.dao.jdbc.IGeoDomainDocumentDao;
import object.impl.jdbc.GeoDomainDocumentDaoImpl;
import object.model.GeoDocument;
import object.model.GeoDomainDocument;
import p4535992.util.reflection.ReflectionKit;

import java.net.URL;
import java.util.List;

/**
 * Created by monica on 11/05/2015.
 */
public class Test_JDBC_util {

    public static void main(String[] args) throws Exception {

        IGeoDomainDocumentDao dao = new GeoDomainDocumentDaoImpl();
        GeoDocument geo = new GeoDocument(
                new URL("http://www.url.com"), "regione", "provincia", "city",
                "indirizzo", "iva", "email", "telefono", "fax",
                "edificio", (Double) 0.0, (Double) 0.0, "nazione", "description",
                "postalCode", "indirizzoNoCAP", "indirizzoHasNumber"
        );

        dao.setDriverManager(
                "com.mysql.jdbc.Driver", "jdbc:mysql",
                "localhost","3306","siimobility","siimobility","geodb");
        dao.setTableSelect("geodocument");
        String[] columns = new String[]{"doc_id","url"};
        Object[] values = new Object[]{236,new URL("http://4bid.it")};

        //WORK
        //List<GeoDomainDocument> list = dao.trySelect(columns,values,null,null,"AND");

        //TRY
        GeoDomainDocument supportObject =  ReflectionKit.invokeConstructor(GeoDomainDocument.class);
        //String query = dao.prepareSelectQuery(columns, values, null, null, "AND");
        String query2 ="SELECT * FROM geodocument WHERE doc_id  = '236' AND url  = 'http://4bid.it' ";
        List<GeoDomainDocument> list2 = dao.trySelect(query2,supportObject);
    }
}
