package extractor.hibernate.interceptor;

import object.model.GeoDocument;
import org.hibernate.*;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 4535992 on 14/05/2015.
 */
public class GeoDocumentInterceptor extends EmptyInterceptor{
    private static final long serailVersionUID = 13L;
    Session session;
    SessionFactory sessionFactory;
    private Set<Object> inserts = new HashSet<>();
    private Set<Object> updates = new HashSet<>();
    private Set<Object> deletes = new HashSet<>();
    private Set<Object> saves = new HashSet<>();
    //Hibernate4Kit hbs = new Hibernate4Kit();

    public void setSession(Session session) {
        if(sessionFactory!=null) {
           this.session = sessionFactory.getCurrentSession();
        }else {
            this.session = session;
        }
    }

    public Session getSession(){
        return session;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * The postFlush method is  called after a flush that actually ends in execution of the
     * SQL statements required to synchronize in-memory state with the database.
     * The preFlush method on the other hand is called before a flush occurs.
     * The isTransient method is called to distinguish between transient and detached entities.
     * The return value determines the state of the entity with respect to the current session.
     * A true indicates the entity is transient and false that the the entity is detached.
     * The instantiate method is used to instantiate the entity class. It returns null to
     * indicate that Hibernate should use the default constructor of the class.
     * The identifier property of the returned instance should be initialized with the given
     * identifier.
     * The findDirty method is called by Hibernate from flush method. Its return value
     * determines whether the entity is updated. It returns an array of property indices
     * indicating the entity is dirty. An empty array indicates that object has not been modified.
     */

    //called before commit into database
    public void preFlush(Iterator entities) {
        System.out.println("preFlush: List of objects to flush... ");
        int i =0;
        while (entities.hasNext()) {
            Object element = (Object) entities.next();
            System.out.println("preFlush: " + (++i) + " : " + element);
        }
    }

    //called after committed into database
    public void postFlush(Iterator entities) {
        System.out.println("postFlush: List of objects that have been flushed... ");
        try{
            int i =0;
            while (entities.hasNext()) {
                Object element = (Object) entities.next();
                System.out.println("postFlush: " + (++i) + " : " + element);
            }

//            for (Iterator it = inserts.iterator(); it.hasNext();) {
//                GeoDocument entity = (GeoDocument) it.next();
//                System.out.println("postFlush - insert");
//                //hbs.setSessionFactory(sessionFactory);
//                //AuditLogUtil.LogIt("Saved",entity, session.connection());
//            }
//
//            for (Iterator it = updates.iterator(); it.hasNext();) {
//                GeoDocument entity = (GeoDocument) it.next();
//                System.out.println("postFlush - update");
//               // AuditLogUtil.LogIt("Updated",entity, session.connection());
//            }
//
//            for (Iterator it = deletes.iterator(); it.hasNext();) {
//                GeoDocument entity = (GeoDocument) it.next();
//                System.out.println("postFlush - delete");
//                //AuditLogUtil.LogIt("Deleted",entity, session.connection());
//            }

        } finally {
            inserts.clear();
            updates.clear();
            deletes.clear();
        }
    }


    public Boolean isTransient(Object entity) {
        System.out.println("isTransient: Checking object for Transient state... " + entity);
        return null;
    }

    public Object instantiate(String entityName, EntityMode entityMode,
                              Serializable id) {
        System.out.println("instantiate: Instantiating object " + entityName +
                " with id - " + id + " in mode " + entityMode);
        return null;
    }

    public int[] findDirty(Object entity, Serializable id,
                           Object[] currentState, Object[] previousState,
                           String[] propertyNames, Type[] types) {
        System.out.println("findDirty: Detects if object is dirty " + entity + " with id " + id);
        final int length = currentState.length;
        System.out.println("findDirty: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            System.out.println("findDirty: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " , previous state : " + previousState[i]
                    + " , current state : " + currentState[i]);
        }
        return null;
    }

    /**
     * The getEntityName method gets the entity name for a persistent or transient instance. The getEntity method
     * get a fully loaded entity instance that is cached externally
     */

    public String getEntityName(Object entity) {
        System.out.println("getEntityName: name for entity " + entity);
        return null;
    }

    public Object getEntity(String entityName, Serializable id) {
        System.out.println("getEntity: Returns fully loaded cached entity with name  " + entityName + " and id " + id);

        return null;
    }

    /**
     * The above methods are a part of Hibernate's transaction mechanism.They are called before/ after transaction
     * completion and after transaction beginning.
     */

    public void afterTransactionBegin(Transaction tx) {
        System.out.println("afterTransactionBegin: Called for transaction " + tx);
    }

    public void afterTransactionCompletion(Transaction tx) {
        System.out.println("afterTransactionCompletion: Called for transaction " + tx);
    }

    public void beforeTransactionCompletion(Transaction tx) {
        System.out.println("beforeTransactionCompletion: Called for transaction " + tx);
    }

    /**The onPrepareStatement method is called when sql queries are to be executed.
     * The other methods are called when collections are fetched/updated/deleted.
     */
    public String onPrepareStatement(String sql) {
        System.out.println("onPrepareStatement: Called for statement " + sql);

        String result = sql;

//        if (entity instanceof GeoDocument) {
//            for (int i = 0; i < propertyNames.length; i++) {
//                if (propertyNames[i].equalsIgnoreCase("url")) {
//                    if(state[i].toString().contains("://")){
//
//                    }else{
//                        state[i] = "http://"+state[i].toString();
//                    }
//                }
//            }
//        }
        return sql;
    }

    public void onCollectionRemove(Object collection, Serializable key)
            throws CallbackException {
        System.out.println("onCollectionRemove: Removed object with key " + key
                + " from collection " + collection);
    }

    public void onCollectionRecreate(Object collection, Serializable key)
            throws CallbackException {
        System.out.println("onCollectionRemove: Recreated collection " + collection + " for key " + key);
    }

    public void onCollectionUpdate(Object collection, Serializable key)
            throws CallbackException {
        System.out.println("onCollectionUpdate: Updated collection " + collection + " for key " + key);
    }

    /**
     * Overriding this method allows us to plugin to the delete mechanism.
     * The method receives the Entity being deleted, its identifier and arrays
     * of details regarding the object. Similar intercept methods include:
     */

    public boolean onSave(Object entity,Serializable id,
                          Object[] state,String[] propertyNames,Type[] types)
            throws CallbackException {
        System.out.println("onSave: Saving object " + entity + " with id " + id);
        final int length = state.length;
        System.out.println("onSave: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            System.out.println("onSave: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " , state : " + state[i]);
        }
        return false;//as no change made to object here

    }

    public boolean onFlushDirty(Object entity,Serializable id,
                                Object[] currentState,Object[] previousState,
                                String[] propertyNames,Type[] types)
            throws CallbackException {
        System.out.println("onFlushDirty: Detected dirty object " + entity + " with id " + id);
        final int length = currentState.length;
        System.out.println("onFlushDirty: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            System.out.println("onFlushDirty: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " , previous state : " + previousState[i]
                    + " , current state : " + currentState[i]);
        }
        if (entity instanceof GeoDocument){
            updates.add(entity);
        }
        return false;//as no change made to object here

    }

    public boolean onLoad(
            Object entity, Serializable id,
            Object[] state, String[] propertyNames, Type[] types) {
        System.out.println("onLoad: Attempting to load an object " + entity + " with id "
                + id);
        final int length = state.length;
        System.out.println("onLoad: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            System.out.println("onLoad: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " ,state : " + state[i]);
        }
//        if (entity instanceof GeoDocument) {
//            for (int i = 0; i < propertyNames.length; i++) {
//                if (propertyNames[i].equalsIgnoreCase("url")) {
//                    if(state[i].toString().contains("://")){
//
//                    }else{
//                        state[i] = "http://"+state[i].toString();
//                    }
//                }
//            }
//        }
        if (entity instanceof GeoDocument){
            saves.add(entity);
        }
        return false;
    }


    public void onDelete(Object entity, Serializable id,
                         Object[] state, String[] propertyNames,
                         Type[] types) {

        // Called before an object is deleted. It is not recommended that the
        // interceptor modify the state.
        System.out.println("onDelete: Attempting to delete an object " + entity + " with id "
                + id);
        final int length = state.length;
        System.out.println("onDelete: Object Details are as below: ");
        for (int i = 0; i < length; i++) {
            System.out.println("onDelete: propertyName : " + propertyNames[i]
                    + " ,type :  " + types[i]
                    + " ,state : " + state[i]);
        }
        if (entity instanceof GeoDocument){
            deletes.add(entity);
        }
    }


}
