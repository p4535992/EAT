package extractor.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by Marco on 27/04/2015.
 */
public interface IHibernate<T> {


    void setNewConfiguration();

    Configuration getConfiguration();

    void setNewServiceRegistry();

    ServiceRegistry getServiceRegistry();

    SessionFactory getSessionFactory();

    Session getSession();

    void shutdown();

    void openSession();

    Session getCurrentSession();

    void buildSessionFactory();

    void buildSessionFactory(URL uri);

    void buildSessionFactory(String filePath);

    void buildSessionFactory(File cfgFile);

    void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,
            String USER_HIBERNATE,
            String PASS_HIBERNATE,
            String DRIVER_DATABASE,
            String DIALECT_DATABASE,
            String DIALECT_DATABASE_HIBERNATE,
            String HOST_DATABASE,
            String PORT_DATABASE,
            List<Class> LIST_ANNOTATED_CLASS
    );

    void buildSessionFactory(
            String DB_OUTPUT_HIBERNATE,
            String USER_HIBERNATE,
            String PASS_HIBERNATE,
            String DRIVER_DATABASE,
            String DIALECT_DATABASE,
            String DIALECT_DATABASE_HIBERNATE,
            String HOST_DATABASE,
            String PORT_DATABASE,
            List<Class> LIST_ANNOTATED_CLASS,
            List<File> LIST_RESOURCE_XML
    );

    <T> void insert(T t);

    List<T> select();

    List<T> select(String nameColumn, int limit, int offset);

    int getCount();

    void update(T object, String whereColumn, Object whereValue);

    void delete(String whereColumn, Object whereValue);

    void updateAnnotationTable(String nameOfAttribute, String newValueAttribute);

    void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;

    void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
}
