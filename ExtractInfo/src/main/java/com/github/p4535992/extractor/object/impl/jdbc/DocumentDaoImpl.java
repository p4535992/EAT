package com.github.p4535992.extractor.object.impl.jdbc;

import com.github.p4535992.extractor.object.model.Document;
import com.github.p4535992.extractor.object.impl.jdbc.generic.GenericDaoImpl;
import com.github.p4535992.extractor.object.dao.jdbc.IDocumentDao;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;

/**
 * Created by 4535992 on 01/04/2015.
 * @author 4535992.
 * @version 2015-07-01.
 */
@org.springframework.stereotype.Component("DocumentDao")
public class DocumentDaoImpl extends GenericDaoImpl<Document> implements IDocumentDao {

    @Override
    public void setDriverManager(String driver, String dialectDB, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver, dialectDB, host, port,user,  pass, database);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void loadSpringConfig(String filePathXml) {
        context = new ClassPathXmlApplicationContext(filePathXml);
        DocumentDaoImpl g = context.getBean(DocumentDaoImpl.class);
    }

    @Override
    public void setTableSelect(String nameOfTable){
        this.mySelectTable = nameOfTable;
    }


    @Override
    public String selectValueForSpecificColumn(String column, String column_where, String value_where){
        String city ="";
        Object obj = super.select(column,column_where,value_where);
        if(obj != null){
            city = (String)obj;
        }
        return city;
    }





}
