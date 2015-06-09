package extractor.hibernate;

import object.model.GeoDocument;
import p4535992.util.reflection.ReflectionKit;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Class with methods for manage the annotation of Hibenrate on he model class object
 * or for modify file XML of configuration in runtime (...in progress).
 * Created by 4535992 on 14/05/2015.
 */
public class HibernateSupport<T> extends Hibernate4Kit<T> {

    public  <T> void updateAnnotationEntity(
           String nameOfAttribute, String newValueAttribute) {
        ReflectionKit.updateAnnotationClassValue(cl, javax.persistence.Entity.class, nameOfAttribute, newValueAttribute);
    }

    public  <T> void updateAnnotationTable(
            String nameOfAttribute, String newValueAttribute){
        ReflectionKit.updateAnnotationClassValue(cl, javax.persistence.Table.class, nameOfAttribute, newValueAttribute);
    }

    public  <T> void updateAnnotationColumn(
            String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.Column.class, nameField, nameOfAttribute, newValueAttribute);
    }

    public  <T> void updateAnnotationJoinColumn(
            String nameField, String nameOfAttribute, String newValueAttribute) throws NoSuchFieldException {
        ReflectionKit.updateAnnotationFieldValue(cl, javax.persistence.JoinColumn.class, nameField, nameOfAttribute, newValueAttribute);
    }

    public <T> List<Object[]> getAnnotationTable() {
        Annotation ann = GeoDocument.class.getAnnotation(javax.persistence.Table.class);
        return ReflectionKit.getAnnotationClass(ann);
    }

}
