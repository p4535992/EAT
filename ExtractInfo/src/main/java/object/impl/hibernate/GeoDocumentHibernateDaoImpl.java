package object.impl.hibernate;

import object.dao.hibernate.IGeoDocumentHibernateDao;
import object.model.GeoDocument;
import org.hibernate.SessionFactory;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by 4535992 on 01/04/2015.
 */
@org.springframework.stereotype.Component("GeoDocumentHibernateDao")
public class GeoDocumentHibernateDaoImpl extends GenericHibernateDaoImpl<GeoDocument> implements IGeoDocumentHibernateDao {


    public GeoDocumentHibernateDaoImpl(){}

    @Override
    public void setDriverManager(String driver, String typeDb, String host,String port, String user, String pass, String database) {
        super.setDriverManager(driver,typeDb, host, port,user,  pass, database);
    }

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
    }

    @Override
    public void loadHibernateConfig(String filePathXml)  {
        super.loadHibernateConfig(filePathXml);
    }

    @Override
    public void setTableSelect(String nameOfTable){super.mySelectTable = nameOfTable;}

    @Override
    public void setTableInsert(String nameOfTable){
        super.myInsertTable = nameOfTable;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    //@Autowired
    //@Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    @Override

    public List<GeoDocument> getAllH(){return super.getAllH();}

    @Override
    public void saveH(GeoDocument g){ super.saveSimpleH(g);}

}
