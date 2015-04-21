package util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectionKit{
//	public static void createInstancesFromSpecfication() {
//	    String[] specifierFileNames = resourceMap.get("instances");
//	    if (null == specifierFileNames)
//	        return;
//	    List<SpecifierObjects> instances = new ArrayList<SpecifierObjects>();
//	    for (int i = 0; i < specifierFileNames.length; i++) {
//	        try {
//	            instances.addAll(XMLFileUtility.readXML(specifierFileNames[i],
//	                    new ClassInstantiationHandler()));
//	        } catch (ResourceNotAvailableException | DocumentException
//	                | ResourceDataFailureException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	    for (Iterator<SpecifierObjects> iterator = instances.iterator(); iterator
//	            .hasNext();) {
//	        InstanceSpecifierObject instanceSpecifierObject = (InstanceSpecifierObject) iterator
//	                .next();
//	        try {
//	            Class<?> cast = Class.forName(instanceSpecifierObject
//	                    .getInterfaceName());
//	            Class<?> clazz = Class.forName(instanceSpecifierObject
//	                    .getClassName());
//	            Constructor<?> constructor = clazz
//	                    .getConstructor(String[].class);
//	            String[] param = instanceSpecifierObject.getFilesName();
//	            for (int i = 0; i < param.length; i++)
//	                System.out.println(param[i]);
//	            Type[] params = constructor.getGenericParameterTypes();
//	            for (int i = 0; i < params.length; i++)
//	                System.out.println(params[i]);
//	            Object obj = constructor.newInstance(new Object[] { param });
//	            objects.put(instanceSpecifierObject.getObjectName(),
//	                    cast.cast(obj));
//	        } catch (ClassNotFoundException | InstantiationException
//	                | IllegalAccessException | IllegalArgumentException
//	                | InvocationTargetException | NoSuchMethodException
//	                | SecurityException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	}//createInstancesFromSpecfication

    //http://my.safaribooksonline.com/video/-/9780133038118?cid=2012-3-blog-video-java-socialmedia
//http://www.asgteach.com/blog/?p=559

    /** Method to get all basic information on a method */
    public static  List<List<String>> getObject(Object obj) {
        List<List<String>> ll = new ArrayList<List<String>>();
        List<String> l = new ArrayList<String>();
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                //field.setAccessible(true); // if you want to modify private fields
                String name = field.getName();
                l.add(name);
                //String value = field.getAnnotations();
                //l.add(value);
                String type = field.getType().toString();
                l.add(type);
                String getter = null;
                getter = field.get(obj).toString();
                l.add(getter);
                ll.add(l);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return ll;
    }
    /**
     * Method to find all variable in the specific class
     * OUTPUT:
     *private final char[] value
     *private final int offset
     *private final int count
     */
    public static List<String[]> inspect(Class<?> klazz) {
        Field[] fields = klazz.getDeclaredFields();
        List<String[]> oField = new ArrayList<String[]>();
        for (Field field : fields) {
            String modify = Modifier.toString(field.getModifiers());
            String type = field.getType().getSimpleName();
            String name = field.getName();
            oField.add(new String[]{modify,type,name});
        }
        return oField;
    }

    /**
     * Method to find getter and setter on a class
     *OUTPUT:
     *public java.lang.String Person.getName()
     *public void Person.setName(java.lang.String)
     *public int Person.getAge()
     */
    public static ArrayList<Method> findGettersSetters(Class<?> c) {
        ArrayList<Method> list = new ArrayList<Method>();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method) || isSetter(method))
                list.add(method);
        return list;
    }

    public static boolean isGetter(Method method) {
        if (Modifier.isPublic(method.getModifiers()) &&
                method.getParameterTypes().length == 0) {
            if (method.getName().matches("^get[A-Z].*") &&
                    !method.getReturnType().equals(void.class))
                return true;
            if (method.getName().matches("^is[A-Z].*") &&
                    method.getReturnType().equals(boolean.class))
                return true;
        }
        return false;
    }

    public static boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                method.getReturnType().equals(void.class) &&
                method.getParameterTypes().length == 1 &&
                method.getName().matches("^set[A-Z].*");
    }

    public static boolean isGetter2(Method method){
        if(!method.getName().startsWith("get"))      return false;
        if(method.getParameterTypes().length != 0)   return false;
        if(void.class.equals(method.getReturnType())) return false;
        return true;
    }

    public static boolean isSetter2(Method method){
        if(!method.getName().startsWith("set")) return false;
        if(method.getParameterTypes().length != 1) return false;
        return true;
    }

    /** Method to get all methods of a class */
    public Method[] getMethodsFromClass(Class<?> aClass){
        return aClass.getMethods();
    }

    /**
     *If you know the precise parameter types of the method you want to access,
     * you can do so rather than obtain the array all methods. This example returns
     * the public method named "nameOfMethod", in the given class which takes a String as parameter:
     */
    public static Method getMethodByName(Class<?> aClass,String nameOfMethod,Object param){
        Method method = null;
        try {
            method = aClass.getMethod(nameOfMethod, new Class[]{param.getClass()}); // String.class
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /** If the method you are trying to access takes no parameters, pass null as the parameter type array, like this: */
    public static Method getMethodByName(Class<?> aClass,String nameOfMethod){
        Method method = null;
        try {
            method = aClass.getMethod(nameOfMethod, null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return method;
    }

    /** Method Parameters : Method where you can read what parameters a given method takes like this: */
    public static Class[] readParameterMethod(Method method){
        return method.getParameterTypes();
    }
    /** Return Types: Method where you can access the return type of a method like this: */
    public static Class getReturnMethod(Method method){
        return method.getReturnType();
    }

    /**
     *get method that takes a String as argument
     *The null parameter is the object you want to invoke the method on. If the method is static you supply null instead
     *of an object instance. In this example, if doSomething(String.class) is not static, you need to supply a valid
     *MyObject instance instead of null;The Method.invoke(Object target, Object ... parameters) method takes an optional amount of parameters,
     *but you must supply exactly one parameter per argument in the method you are invoking. In this case it was
     *a method taking a String, so one String must be supplied.
     */
    public static Object getObjectFromMethod(Object MyObject,String nameOfMethod,Object paramValue){
// Class<?> enclosingClass = getClass();
//  method = enclosingClass.getDeclaredMethod(name, Double.class);
//  Object value = method.invoke(this, paramValue);
        Object obj =null;
        try {
            Method method = MyObject.getClass().getMethod(nameOfMethod, paramValue.getClass()); //String.class
            obj = method.invoke(null, paramValue);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /** Method to get the retyurn type of a mehtod in a sprecific class */
    public static Object getValueFromMethod(String nameOfMethod,String pathToClass, Object[] params){
        // patToClass = home.util.gate.GateKit
        Class[] parameters = new Class[]{};
//        for(Object s : params){
//            parameters.add(s.getClass());
//        }
        Class cls = null;
        Object obj = null;
        try {
            cls = Class.forName(pathToClass);
            obj = cls.newInstance();
            Method method = cls.getDeclaredMethod(nameOfMethod, parameters);
            if(params == null)method.invoke(obj, null);
            else method.invoke(obj, params);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return obj;
    }
}