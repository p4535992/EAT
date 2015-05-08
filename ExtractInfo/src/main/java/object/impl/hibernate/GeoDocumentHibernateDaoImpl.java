package object.impl.hibernate;

import object.dao.hibernate.IGeoDocumentHibernateDao;
import object.impl.hibernate.generic.GenericHibernateDaoImpl;
import object.model.GeoDocument;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 4535992 on 01/04/2015.
 */
@org.springframework.stereotype.Repository
@org.springframework.stereotype.Component("GeoDocumentHibernateDao")
public class GeoDocumentHibernateDaoImpl extends GenericHibernateDaoImpl<GeoDocument> implements IGeoDocumentHibernateDao {


    public GeoDocumentHibernateDaoImpl(){}
    public GeoDocumentHibernateDaoImpl(String s) throws FileNotFoundException {
        super(s);
        super.loadSpringContext(contextFile.getAbsolutePath());
    }

    @Override
    public void setContext(ApplicationContext context){
        super.setContextFile(context);
    }

    @Override
    public ApplicationContext getContext(){
        return super.getContextFile();
    }

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver, typeDb, host, port, user, pass, database);
    }

    @Override
    public void setDataSource(DataSource ds) {
       super.setDataSource(ds);
    }

  /*  @Override
    public List<GeoDocument> findByName(String name) {
        Object[] args = new Object[]{name};
        return null;
    }

    @Override
    public Iterator<GeoDocument> iterateByWeight(int weight) {
        return null;
    }*/

    @Override
    public IGeoDocumentHibernateDao getDao(String beanIdName) {
        if(context!=null){
            return super.getDao(beanIdName);
        }else{
            return null;
        }
    }

    @Override
    public void loadSpringContext(String filePathXml){
        super.contextFile = new File(filePathXml);
        super.loadSpringContext(filePathXml);
    }

    @Override
    public void setTableSelect(String nameOfTable){super.setTableSelect(nameOfTable);}

    @Override
    public void setTableInsert(String nameOfTable){
        super.setTableInsert(nameOfTable);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
       super.setSessionFactory(sessionFactory);
    }

    @Override
    public String getBeanIdSessionFactory() {
        return super.getBeanIdSessionFactory();
    }
    @Override
    public void setBeanIdSessionFactory(String beanIdSessionFactory) {
        super.setBeanIdSessionFactory(beanIdSessionFactory);
    }

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory() {
        return super.getSessionFactory();
    }

    @Override
    public Session getSession() {
        return super.getSession();
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void openSession() {

    }

    @Override
    public void closeSession() {

    }

    @Override
    public Session getCurrentSession() {
        return super.getCurrentSession();
    }

    @Override
    public void insert(GeoDocument newInstance) {
        super.insert(newInstance);
    }

    @Override
    public Object insertAndReturn(GeoDocument newInstance) {
        return super.insertAndReturn(newInstance);
    }

    @Override
    public  GeoDocument select(Serializable serial){
        return super.select(serial);
    }

    @Override
    public List<GeoDocument> selectAll(Serializable serial) {
        return super.selectAll(serial);
    }


    @Override
    public List<GeoDocument> select(String nameColumn, int limit, int offset) {
        return super.select(nameColumn,limit,offset);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public void delete(GeoDocument geo) {
        super.delete(geo);
    }

    @Override
    public void updateAnnotationTable(String nameOfAttribute, String newValueAttribute) {
        super.updateAnnotationTable(nameOfAttribute,newValueAttribute);
    }

    @Override
    public void updateAnnotationColumn(String nameField, String nameOfAttribute, String newValueAttribute){
        super.updateAnnotationColumn( nameField,nameOfAttribute,newValueAttribute);
    }

    @Override
    public void updateAnnotationJoinColumn(String nameField, String nameOfAttribute, String newValueAttribute){
        super.updateAnnotationJoinColumn(nameField, nameOfAttribute,newValueAttribute);
    }






}
