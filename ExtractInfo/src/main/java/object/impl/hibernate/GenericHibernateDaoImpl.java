package object.impl.hibernate;

import object.dao.generic.IGenericHibernateDao;
import object.model.GeoDocument;
import org.hibernate.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marco on 23/04/2015.
 */
public class GenericHibernateDaoImpl<T> implements IGenericHibernateDao<T> {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GenericHibernateDaoImpl.class);
    protected DriverManagerDataSource driverManag;
    protected HibernateTemplate hibernateTemplate;
    protected String myInsertTable,mySelectTable;
    protected DataSource dataSource;
    protected SessionFactory sessionFactory;

    @PersistenceContext
    protected EntityManager em;
    protected Class<T> cl;
    protected String clName;
    protected String query;

    public GenericHibernateDaoImpl() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    @Override
    public void setDriverManager(String driver, String typeDb, String host, String port, String user, String pass, String database) {
        driverManag = new DriverManagerDataSource();
        driverManag.setDriverClassName(driver);//"com.sql.jdbc.Driver"
        driverManag.setUrl("" + typeDb + "://" + host + ":" + port + "/" + database); //"jdbc:sql://localhost:3306/jdbctest"
        driverManag.setUsername(user);
        driverManag.setPassword(pass);
        this.dataSource = driverManag;
    }

    @Override
    public void setDataSource(DataSource ds) { this.dataSource= ds;}

    @Override
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {this.hibernateTemplate = hibernateTemplate;}

    @Override
    public void setNewHibernateTemplate(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new HibernateTemplate(getSessionFactory());
    }

    @Override
    public void loadHibernateConfig(String filePathXml) {

    }

    @Override
    public DataSource getDataSource() {return this.dataSource;}

    @Override
    public SessionFactory getSessionFactory() {return this.sessionFactory; }

    @Override
    public void setSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addAnnotatedClasses(GeoDocument.class);
        this.sessionFactory = sessionBuilder.buildSessionFactory();
    }

    @Override
    public void setSessionFactory() {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addAnnotatedClasses(GeoDocument.class);
        this.sessionFactory = sessionBuilder.buildSessionFactory();
    }

    @Override
    public void setTableInsert(String nameOfTable) {this.myInsertTable= nameOfTable;}

    @Override
    public void setTableSelect(String nameOfTable) {this.mySelectTable= nameOfTable;}

    @Override
    public void saveSimpleH(T object) {hibernateTemplate.save(object);
    }

    @Override
    public void updateSimpleH(T object) {hibernateTemplate.update(object);}

    @Override
    public void deleteSimpleH(T object) {hibernateTemplate.delete(object);}


    @Override
    public T getHByColumn(String column) {
        // GeoDocument g = hibernateTemplate.get(GeoDocument.class,column);
        final Class cla = cl;
        T g = (T) hibernateTemplate.get(cla,column);
        return g;
    }

    //method to return one of given id
//    @Override
//    public InfoDocument  getHByColumn(String column){
//        InfoDocument g = hibernateTemplate.get(InfoDocument.class,column);
//        return g;
//    }

    @Override
    public List selectAllSimpleH() {
        List<Object> list = new ArrayList<>();
        final Class cla = cl;
        list = hibernateTemplate.loadAll(cla);
        return list;
    }

    @Override
    public List selectAllSimpleH(final String limit,final String offset) {
        List<GeoDocument> docs = new ArrayList<GeoDocument>();
        //METHOD 1 (Boring)
        /*
        Query q = getHibernateTemplate().getSession().createQuery("from User");
        q.setFirstResult(0); // modify this to adjust paging
        q.setMaxResults(limit);
        return (List<User>) q.list();
        */
        if(limit != null && offset != null) {
            //METHOD 2 (Probably the best)
            docs =
                    (List<GeoDocument>) hibernateTemplate.execute(new HibernateCallback() {
                        public Object doInHibernate(Session session) throws HibernateException {
                            Query query = session.createQuery("from " + mySelectTable + "");
                            query.setFirstResult(Integer.parseInt(offset));
                            query.setMaxResults(Integer.parseInt(limit));
                            return (List<GeoDocument>) query.list();
                        }
                    });
        }
        return docs;
    }

//    @Override
//    public List<Map<String, Object>>  getAll() {
//        return null;
//    }

    //method to return all
    @Override
    public List<T> getAllH(){return hibernateTemplate.loadAll(cl);}

    //method to return all
    @Override
    @Transactional
    public List<T>  selectAllH(){
        @SuppressWarnings("unchecked")
        List<T> list = (List<T>) sessionFactory.getCurrentSession()
        .createCriteria(GeoDocument.class)
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        return list;
    }







    // If you'd like to use HibernateTemplate you can do something like this:

    /*
    @SuppressWarnings("unchecked")
    public List<T> get(final int limit) {
        return hibernateTemplate.executeFind(new HibernateCallback<List<T>>() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                return session.createCriteria(cl).setMaxResults(limit).list();
            }
        });
    }
    */

    /*
    @SuppressWarnings("unchecked")
    public List<T> find(final int limit) {
        return hibernateTemplate.executeFind(new HibernateCallback<List<T>>() {
            @Override
            public List<T> doInHibernate(Session session) throws HibernateException {
                return session.createQuery("FROM "+mySelectTable+"").setMaxResults(limit).list();
            }
        });
    }
    */




}
