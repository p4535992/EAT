package p4535992.util.sql;

import p4535992.util.reflection.ReflectionKit;
import p4535992.util.string.StringKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Converts database types to Java class types.
 */
public class  SQLKit<T> {
    private static Connection conn;
    private static Statement stmt;

    private static Class<?> cl;
    private String clName;
    private String query;

    public SQLKit() {
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }


    public static Class convertSQLTypes2JavaClass(int type) {
        Class<?> result = Object.class;
        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = String.class;
                break;
            case Types.NUMERIC:
            case Types.DECIMAL:
                result = java.math.BigDecimal.class;
                break;
            case Types.BIT:
                result = Boolean.class;
                break;
            case Types.TINYINT:
                result = Byte.class;
                break;
            case Types.SMALLINT:
                result = Short.class;
                break;
            case Types.INTEGER:
                result = Integer.class;
                break;
            case Types.BIGINT:
                result = Long.class;
                break;
            case Types.REAL:
            case Types.FLOAT:
                result = Float.class;
                break;
            case Types.DOUBLE:
                result = Double.class;
                break;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = Byte[].class;
                break;
            case Types.DATE:
                result = java.sql.Date.class;
                break;
            case Types.TIME:
                result = java.sql.Time.class;
                break;

            case Types.TIMESTAMP:
                result = java.sql.Timestamp.class;
                break;
            case Types.NULL:
                result = new Object().getClass().getSuperclass();
        }
        return result;
    }

    public static int convertClass2SQLTypes(Class<?> aClass) {
        int result;
        if(aClass.getName().equals(String.class.getName()))result = Types.VARCHAR;
        else if(aClass.getName().equals(java.math.BigDecimal.class.getName()))result = Types.NUMERIC;
        else if(aClass.getName().equals(Boolean.class.getName()))result = Types.BIT;
        else if(aClass.getName().equals(Byte.class.getName()))result = Types.TINYINT;
        else if(aClass.getName().equals(Short.class.getName()))result = Types.SMALLINT;
        else if(aClass.getName().equals(Integer.class.getName()))result = Types.INTEGER;
        else if(aClass.getName().equals(Long.class.getName())) result = Types.BIGINT;
        else if(aClass.getName().equals(Float.class.getName()))result = Types.FLOAT;
        else if(aClass.getName().equals(Double.class.getName()))result = Types.DOUBLE;
        else if(aClass.getName().equals(Byte[].class.getName()))result = Types.VARBINARY;
        else if(aClass.getName().equals(java.sql.Date.class.getName())) result = Types.DATE;
        else if(aClass.getName().equals(java.sql.Time.class.getName()))result = Types.TIME;
        else if(aClass.getName().equals(java.sql.Timestamp.class.getName()))result = Types.TIMESTAMP;
        else result = Types.NULL;
        return result;
    }


    public static Map<Integer, String> getAllJdbcTypeNames() throws IllegalArgumentException, IllegalAccessException {
        Map<Integer, String> result = new HashMap<Integer, String>();
        for (Field field : Types.class.getFields()) {
            result.put((Integer)field.get(null), field.getName());
        }
        return result;
    }


    public static Map<String,Integer> getColumns(String database,String table,String column) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet result = metaData.getColumns( null, database, table, column );
        Map<String,Integer> map = new HashMap<>();
        while(result.next()){
            String columnName = result.getString(4);
            Integer columnType = result.getInt(5);
            map.put(columnName,columnType);
        }
        return map;
    }

    public static void openConnection(String url,String user,String pass) throws SQLException, ClassNotFoundException{
        //Class.forName("org.h2.Driver"); //Loading driver connection
        conn = DriverManager.getConnection(url, user, pass);
    }

    public static void closeConnection() throws SQLException{ conn.close();}

    /**
     * @href: http://www.java2s.com/Code/Java/Database-SQL-JDBC/convertingajavasqlTypesintegervalueintoaprintablename.htm
     * @param jdbcType
     */
    public static void getJdbcTypeName(int jdbcType) {
        Map map = new HashMap();
        // Get all field in java.sql.Types
        Field[] fields = java.sql.Types.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                String name = fields[i].getName();
                Integer value = (Integer) fields[i].get(null);
                map.put(value, name);
            } catch (IllegalAccessException e) {
            }
        }
        System.out.println(map);
    }

    public static void executeSQLCommand(String sql) throws Exception {
        stmt.executeUpdate(sql);
    }
    public static void checkData(String sql) throws Exception {
        java.sql.ResultSet rs = stmt.executeQuery(sql);
        java.sql.ResultSetMetaData metadata = rs.getMetaData();

        for (int i = 0; i < metadata.getColumnCount(); i++) {
            System.out.print("\t"+ metadata.getColumnLabel(i + 1));
        }
        System.out.println("\n----------------------------------");

        while (rs.next()) {
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                Object value = rs.getObject(i + 1);
                if (value == null) {
                    System.out.print("\t       ");
                } else {
                    System.out.print("\t"+value.toString().trim());
                }
            }
            System.out.println("");
        }
    }

    public static Connection getHSQLConnection(String database,String username,String password) throws Exception {
        Class.forName("org.hsqldb.jdbcDriver");
        String url = "jdbc:hsqldb:data/"+database;
        return  conn = DriverManager.getConnection(url, username,password);
    }

    public static Connection getMySqlConnection(String database,String username,String password) throws Exception {
        Class.forName("org.gjt.mm.mysql.Driver");
        String url = "jdbc:mysql://localhost/"+database;
        return conn = DriverManager.getConnection(url, username, password);
    }

    public static Connection getOracleConnection(String database,String username,String password) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = "jdbc:oracle:thin:@localhost:1521:"+database;// load Oracle driver
        return conn = DriverManager.getConnection(url, username, password);
    }


//    public String getJavaType( String schema, String object, String column )throws Exception {
//        Connection con = null;
//        String fullName = schema + '.' + object + '.' + column;
//        String javaType = null;
//        if(columnMeta.first() ) {
//          int dataType = columnMeta.getInt( "DATA_TYPE" );
//          javaType = SQLTypeMap.convert( dataType );
//        }
//        else {
//          throw new Exception( "Unknown database column " + fullName + '.' );
//        }
//
//    return javaType;
//  }

    /**
     * Method for get from a java class all the information you need for insert
     * a data in a database a homemade very very very base similar hibernate usage
     * @ATTENTION: you need to be sure all the getter have reference to a field with a hibernate annotation and the attribute column.
     * @ATTENTION: you need all field of the object class have a hibernate annotation and the attribute column, or at least
     * a personal annotation with the attribute column and a value who is the column of the column.
     * @param object
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public static <T> SQLSupport insertSupport(T object)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Map<String,Class> l = ReflectionKit.inspectAndLoadGetterObject(object);
        Object[] values = new Object[l.size()];
        int[] types = new int[l.size()];
        String[] columns = new String[l.size()];
        int i = 0;
        for(Map.Entry<String,Class> entry : l.entrySet()) {
            Class[] arrayClass = new Class[]{entry.getValue()};
            values[i] = ReflectionKit.invokeObjectMethod(object, entry.getKey().toString(),arrayClass);
            types[i] = SQLKit.convertClass2SQLTypes(entry.getValue());
            i++;
        }
        i = 0;
        List<List<Object[]>> ssc = ReflectionKit.getAnnotationsFields(object.getClass());
        for(List<Object[]> list : ssc){
            int j =0;
            boolean flag = false;
            while(j < list.size()){
                int k = 0;
                while(k < list.get(j).length) {
                    if (list.get(j)[k].equals("column")) {
                        columns[i] = list.get(j)[++k].toString();
                        flag = true;
                        break;
                    }
                    k++;
                }
                if(flag==true)break;
                j++;
            }
            i++;
        }
        return new SQLSupport(columns,values,types);
    }

    public static Integer[] getArrayTypes(Class<?> clazz, Class<? extends Annotation> aClass){
        List<Integer> types = new ArrayList<>();
        Class[] classes = ReflectionKit.getClassesByFieldsByAnnotation(clazz, aClass);
        //GET TYPES SQL
        for(Class cl: classes){
            types.add(convertClass2SQLTypes(cl));
        }
        return StringKit.convertListToArray(types);
    }

    public static String[] getArrayColumns(Class<?> clazz, Class<? extends Annotation> aClass,String attributeNameColumnAnnotation) throws NoSuchFieldException {
        List<List<Object[]>> test4 = ReflectionKit.getAnnotationsFields(clazz,aClass);
        int j,i,x;
        boolean found = false;
        String[] columns = new String[test4.size()];
        j=0;
        for(List<Object[]> list : test4){
            i = 0;
            found = false;
            while(i < list.size()){
                x = 0;
                while (x < list.get(i).length) {
                    if (list.get(i)[x].toString().equals(attributeNameColumnAnnotation)) {
                        columns[j] = String.valueOf(list.get(i)[++x]);
                        j++;
                        found = true;
                        break;
                    }
                    x++;
                }
                if(found) break;
                else i++;
            }
        }
        return  columns;
    }

    public static <T> T invokeSetterSupport(T iClass, String column, Object value) throws NoSuchFieldException {
        try {
            Method method = ReflectionKit.findSetterMethod(iClass,column,value);
           Object[] values = new Object[]{value};
           iClass = ReflectionKit.invokeSetterMethod(iClass, method,values);
            return iClass;
        } catch (IllegalAccessException|
                InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }




}

