package util;

import extractor.hibernate.interceptor.GeoDocumentInterceptor;
import object.dao.hibernate.IGeoDocumentHibernateDao;
import object.impl.hibernate.GeoDocumentHibernateDaoImpl;
import org.hibernate.Session;
import org.springframework.context.ApplicationContext;
import object.model.GeoDocument;

import java.net.URL;

/**
 * Created by 4535992 on 04/05/2015.
 */
public class Test_Hibernate_Spring {
    private ApplicationContext context;

    public static void main(String[] args) throws Exception
    {

        // Created
        IGeoDocumentHibernateDao dao = new GeoDocumentHibernateDaoImpl();
        GeoDocument geo = new GeoDocument(
            new URL("http://www.url.com"), "regione", "provincia", "city",
            "indirizzo", "iva", "email", "telefono", "fax",
            "edificio", 0.0,  0.0, "nazione", "description",
            "postalCode", "indirizzoNoCAP", "indirizzoHasNumber"
        );
        //String path = FileUtil.convertResourceToFile("spring_hibernate/spring-hibernate4v3.xml").getAbsolutePath();
        //String path = "C:\\Users\\Marco\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\resources\\spring_hibernate\\spring-hibernate4v3.xml";
        //String path ="C:\\Users\\Marco\\Documents\\GitHub\\EAT\\ExtractInfo\\src\\main\\resources\\spring_hibernate\\spring-hibernate4v2.xml";
        String path = "spring_hibernate/spring-context.xml";
        //ApplicationContext context = new ClassPathXmlApplicationContext(path);
        //dao.setContextFile(path);

        dao.setBeanIdSessionFactory("sessionFactory");
        dao.loadSpringContext(path);
        //dao.restartSession();

        ApplicationContext context = dao.getContext();

        //CRUD OPERATION
        //GeoDocument ges = dao.selectRow(244);
        //dao.insertRow(geo);

        //Prepare Interceptor
        //GeoDocumentInterceptor inter = new GeoDocumentInterceptor();
        //inter.setSessionFactory(dao.getSessionFactory());
        dao.setInterceptor(GeoDocumentInterceptor.class);
        GeoDocument ges = dao.selectRow(244);
        //Hibernate4Kit hbs = new Hibernate4Kit();
        //hbs.setNewInterceptor(GeoDocumentInterceptor.class);
        ///////////////////////////////////////////////////////
        //TEST DIRTY


        Session session = dao.getSession();


        ///////////////////////////////////////////////
        // Read
        //GeoDocument ges = dao.selectRow(244);
//        dao.restartSession();
//
//        // Update
//        Integer updateWeight = 90;
//        ges.setDoc_id(updateWeight);
//        dao.updateRow(ges);
//        GeoDocument updatedPerson = dao.selectRow(updateWeight);
//
//        dao.restartSession();
//
//        // Delete
//        dao.deleteRow(ges);
//        dao.restartSession();
//
//        //FIND NAME QUERY
//        dao.insertRow(geo);
    }




   /* public void testFindByName() throws Exception {
        PersonDao personDao = getPersonDao();
        Person person1 = new Person("Mellqvist", 88);
        personDao.create(person1);
        Person person2 = new Person("Doe", 80);
        personDao.create(person2);

        restartSession();

        List<Person> byName = personDao.findByName("Mellqvist");
        assertTrue(byName.size() == 1);
        assertEquals(person1.getWeight(), byName.get(0).getWeight());

        restartSession();

        personDao.delete(person1);
        personDao.delete(person2);
    }

    public void testIterateByWeight() throws Exception
    {
        PersonDao personDao = getPersonDao();
        Person person1 = new Person("Mellqvist", 88);
        personDao.create(person1);
        Person person2 = new Person("Doe", 80);
        personDao.create(person2);

        restartSession();

        Iterator<Person> byWeight = personDao.iterateByWeight(person1.getWeight());
        assertTrue(byWeight.hasNext());
        Person found = byWeight.next();
        assertEquals(person1.getWeight(), found.getWeight());

        restartSession();

        personDao.delete(person1);
        personDao.delete(person2);
    }*/




}
