package extractor.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * Created by 4535992 on 27/04/2015.
 */
public interface IHibernateKit<T> {


    SessionFactory getSessionFactory();
    Session getSession();
    void shutdown();
    void openSession();
    void closeSession();
    Session getCurrentSession();
    void insert(T newInstance);
    Object insertAndReturn(T newInstance);
    T Select(Object id);
    List<T> select();
    List<T> select(String nameColumn, int limit, int offset);
    int getCount();
    void update(T object, String whereColumn, Object whereValue);
    void delete(String whereColumn, Object whereValue);
    void updateAnnotationTable(String nameOfAttribute, String newValueAttribute);
    void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
    void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException;
}
