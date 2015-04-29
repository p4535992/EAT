package p4535992.util.reflection;


import p4535992.util.log.SystemLog;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

/**
 * Class for help me with the frist impact with java reflection library
 * To foolow all the url help me to create this class:
 * @href: http://my.safaribooksonline.com/video/-/9780133038118?cid=2012-3-blog-video-java-socialmedia
 * @href: http://www.asgteach.com/blog/?p=559
 * @href: http://stackoverflow.com/questions/709961/determining-if-an-object-is-of-primitive-type
 * @href: http://roadtobe.com/supaldubey/creating-and-reading-annotations-reflection-in-java/ (other util)
 * @href: https://github.com/dancerjohn/LibEx/blob/master/libex/src/main/java/org/libex/reflect/ReflectionUtils.java (href)
 * @param <T>
 */
public class ReflectionKit<T>{

   
    private Class<T> cl;
    private String clName;
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isWrapperType(Class<?> aClass) {
        return WRAPPER_TYPES.contains(aClass);
    }

    private static Set<Class<?>> getWrapperTypes(){
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
    
    public ReflectionKit(){
        java.lang.reflect.Type t = getClass().getGenericSuperclass();
        java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) t;
        this.cl = (Class) pt.getActualTypeArguments()[0];
        this.clName = cl.getSimpleName();
    }

    /** Method to get all basic information on a method */
    public static List<String[]> inspectFieldClass(Object obj) {       
        return inspectFieldClass(obj.getClass());
    }
      
    public static List<String[]> inspectFieldClass(Class<?> aClass) {
        Field[] fields = aClass.getDeclaredFields();
        List<String[]> oField = new ArrayList<String[]>();
        for (Field field : fields) {
            field.setAccessible(true); // if you want to modify private fields
            String modify = Modifier.toString(field.getModifiers());
            String type = field.getType().getSimpleName();
            String name = field.getName();
            oField.add(new String[]{modify,type,name});
        }
        return oField;
    }
    
    public static Map<Field,Annotation[]> inspectFieldAndAnnotationClass(Class<?> aClass){
        Map<Field,Annotation[]> map = new HashMap<>();
        for(Field field : aClass.getDeclaredFields()){
            //Class type = field.getType();
            //String name = field.getName();
            Annotation[] annotations = field.getDeclaredAnnotations();
            map.put(field, annotations);
        }
        return map;
    }
    
    public static List<String[]> inspectTypesMethod(Class<?> aClass,String nameOfMethhod) throws NoSuchMethodException{
        List<String[]> list = null;
        Method method = aClass.getMethod(nameOfMethhod, null);
        Type returnType = method.getGenericReturnType();
        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                Class typeArgClass = (Class) typeArgument;
                //System.out.println("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(),getClassReference(typeArgClass)});
            }
        }
        return list;
    }
    
    public static List<String[]> inspectTypesMethod(Class<?> aClass,Method method) throws NoSuchMethodException{
        List<String[]> list = null;      
        Type returnType = method.getGenericReturnType();
        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(),getClassReference(typeArgClass)});
            }
        }
        return list;
    }
    
    public static Class inspectSimpleTypesMethod(Class<?> aClass,Method method) throws NoSuchMethodException{
        List<String[]> list = null;      
        Type returnType = method.getGenericReturnType();      
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            for(Type typeArgument : typeArguments){
                Class typeArgClass = (Class) typeArgument;
                System.out.println("typeArgClass = " + typeArgClass);
                list.add(new String[]{typeArgClass.getName(),getClassReference(typeArgClass)});
            }      
        return returnType.getClass();
    }
    
    public static Map<String,Class> inspectAndLoadGetterObject(Object obj) throws NoSuchMethodException{
        List<Method> getter = getGettersClass(obj.getClass());
        Map<String,Class> map = new HashMap<String,Class>();
        for(Method met : getter){
            Class cl = met.getReturnType();
            String name = met.getName();
            map.put(name, cl);
        }
        return map;
    }
    
    public static Map<String,Class> inspectAndLoadSetterObject(Object obj) throws NoSuchMethodException{
        List<Method> setter = getSettersClass(obj.getClass());
        Map<String,Class> map = new HashMap<String,Class>();
        for(Method met : setter){
            Class cl = met.getReturnType();
            String name = met.getName();
            map.put(name, cl);
        }
        return map;
    }
    
    public static Integer countGetterAndsetter(Class<?> aClass){
        return getGettersSettersClass(aClass).size()/2;
    }
    
//    public static List<String[]> inspectMethods(Class<?> aClass){
//        Method[] methods = aClass.getMethods();
//        List<String[]> oMethod = new ArrayList<String[]>();
//        for (Method method : methods) {        
//            String modify = method.
//            String type = field.getType().getSimpleName();
//            String name = field.getName();
//            oMethod.add(new String[]{modify,type,name});
//        }
//        return oMethod;
//    }
//    
//    public static List<String[]> inspectAnnotations(Class<?> aClass) {
//        Annotation[] annotations = aClass.getAnnotations();
//        List<String[]> oAnn = new ArrayList<String[]>();
//        for (Annotation ann : annotations) {           
//            String test = ann.annotationType().
//            oAnn.add(new String[]{modify,type,name});
//        }
//        return oAnn;
//    }
    
    public static List<String[]> inspectConstructor(Class<?> aClass){
        Constructor[] constructors = aClass.getConstructors();
        List<String[]> oConst = new ArrayList<String[]>();
        for (Constructor cons : constructors) {
            String modify = Modifier.toString(cons.getModifiers());
            String type = cons.getTypeParameters().toString();
            String name = cons.getName();         
            oConst.add(new String[]{modify,type,name});
        }
        return oConst;
    } 
    
    public static List<Method> getGettersSettersClass(Class<?> aClass) {
        List<Method> list = new ArrayList<Method>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method) || isSetter(method))
                list.add(method);
        return list;
    }
    
    public static List<Method> getGettersClass(Class aClass){
        List<Method> list = new ArrayList<Method>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isGetter(method))
                list.add(method);
        return list;     
    }
    
     public static List<Method> getSettersClass(Class aClass){
        List<Method> list = new ArrayList<Method>();
        Method[] methods = aClass.getDeclaredMethods();
        for (Method method : methods)
            if (isSetter(method))
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
    public static List<Method> getMethodsByClass(Class<?> aClass){
        Method[] methods = aClass.getMethods();
        return Arrays.asList(methods);
    }

    /**
     * Method to get a specific method from a class
     *If you know the precise parameter types of the method you want to access,
     * you can do so rather than obtain the array all methods. This example returns
     * the public method named "nameOfMethod", in the given class which takes a String as parameter:
     */
    public static Method getMethodByName(Class<?> aClass,String nameOfMethod,Class[] param) throws NoSuchMethodException{
        Method method = null;
        //Class[] cArg = new Class[param.length];  
        //If the method you are trying to access takes no parameters, pass null as the parameter type array, like this:   
        if(param==null || param.length==0 )method = aClass.getMethod(nameOfMethod, null);
        else method = aClass.getMethod(nameOfMethod,param);// String.class        
        return method;
    }

    /**
     * Method to get a specific mehtod form a the reference class of the object
     * @param MyObject
     * @param nameOfMethod
     * @param param
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     */
    public static <T> Method getMethodByName(T MyObject,String nameOfMethod,Class[] param) throws NoSuchMethodException{
            return getMethodByName(MyObject.getClass(), nameOfMethod, param); //String.class
    }

    /** Method Parameters : Method where you can read what parameters a given method takes like this: */
    public static Class[] getParametersTypeMethod(Method method){
        return method.getParameterTypes();
    }
    /** Return Types: Method where you can access the return type of a method like this: */
    public static Class getReturnTypeMethod(Method method){
        return method.getReturnType();
    }

    private static Field[] getFieldsClass(Class<?> aClass){
        return aClass.getDeclaredFields();
    }

    public static Field getFieldByName(Class<?> aClass,String fieldName) throws NoSuchFieldException {
        try {
            return aClass.getField(fieldName);
        }catch(java.lang.NoSuchFieldException e){
            return aClass.getDeclaredField(fieldName);
        }
    }

    public static List<String> getFieldsNameClass(Class<?> aClass){
        List<String> names = new ArrayList<String>();
        for(Field field : getFieldsClass(aClass)){
            names.add(field.getName());
        }
        return names;
    }

    /**
     * Method to get the return type of a method in a specific class
     * get method that takes a String as argument
     * The null parameter is the object you want to invoke the method on. If the method is static you supply null instead
     * of an object instance. In this example, if doSomething(String.class) is not static, you need to supply a valid
     * MyObject instance instead of null;The Method.invoke(Object target, Object ... parameters) method takes an optional amount of parameters,
     * but you must supply exactly one parameter per argument in the method you are invoking. In this case it was
     * a method taking a String, so one String must be supplied.
     * @param MyObject
     * @param nameOfMethod
     * @param param
     * @return 
     */
    public static <T> T invokeObjectMethod(Object MyObject, String nameOfMethod, Class[] param, Class<T> aClass)
            throws IllegalAccessException,InvocationTargetException,NoSuchMethodException{
        Object obj =null;     
        Method method;
        if(param==null || param.length==0 )method = getMethodByName(MyObject,nameOfMethod,null);
        else method = getMethodByName(MyObject,nameOfMethod, param); //String.class       
        try{
            obj = method.invoke(null, param); //if the method you try to invoke is static...    
        }catch(java.lang.NullPointerException ne){
            obj = method.invoke(MyObject, param); //...if the methos is non-static
        }      
        return (T)obj;
    }

    /**
     * Method to get constructor that takes a String as argument
     * @param MyObject
     * @param param
     * @param <T>
     * @return
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T> T invokeConstructorMethod(T MyObject, Class[] param)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Constructor constructor = MyObject.getClass().getConstructor(param);
        T myObject = (T)constructor.newInstance("constructor-arg1");
        return myObject;
    }
    
    public static URL getCodeSourceLocation(Class aClass) {return aClass.getProtectionDomain().getCodeSource().getLocation(); }
    public static String getClassReference(Class aClass){ return aClass.getName();}
    
    /**Method for get all the class in a package with library reflections */
//    public Set<Class<? extends Object>> getClassesByPackage(String pathToPackage){
//        Reflections reflections = new Reflections(pathToPackage);
//        Set<Class<? extends Object>> allClasses = reflections.getSubTypesOf(Object.class);
//    }
    
    /**
     * 
     * @param aClass
     * @return 
     * @href: http://tutorials.jenkov.com/java-reflection/annotations.html
     * @href: http://stackoverflow.com/questions/20362493/how-to-get-annotation-class-name-attribute-values-using-reflection
     * @Usage: @Resource(name = "foo", description = "bar")
     * name: foo
     * type: class java.lang.Object
     * lookup: 
     * description: bar
     * authenticationType: CONTAINER
     * mappedName: 
     * shareable: true
     */
    public static <T> List<Object[]> inspectAllAnnotationClass(Class<?> aClass) 
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Annotation[] annotations = aClass.getAnnotations();
        List<Object[]> list = new ArrayList<Object[]>();
//        MyAnnotation myAnnotation = method.getAnnotation(Annotation.class); //specific class      
//        aClass.getDeclaredAnnotations(); // get all annotation
//        (aClass.getDeclaredMethods()[0]).getAnnotations(); //get annotation of a method
//        Annotation ety = aClass.getAnnotation(Annotation.class); // get annotation of particular annotation class
//        for(Annotation annotation : annotations){
//            if(annotation instanceof Annotation){
//                Annotation myAnnotation = (Annotation) annotation;
//                System.out.println("name: " + myAnnotation.annotationType().getName());
//                System.out.println("value: " + myAnnotation.annotationType().);
//            }
//        }
        for (Annotation annotation : aClass.getAnnotations()) {
            Object[] array = new Object[3];
            Class<? extends Annotation> type = annotation.annotationType();
            array[0] = type.getName();
            for (Method method : type.getDeclaredMethods()){
                array[1] = method.getName();
                array[2] = method.invoke(annotation, null);
                list.add(array);
            }
        }
        return list;
    }
    
    public static <T> List<String[]> inspectAnnotationClass(Class<T> aClass,String methodName) 
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException{
        Method method = aClass.getMethod(methodName, null);
        Annotation[] annotations = aClass.getAnnotations();       
        List<String[]> list = new ArrayList<String[]>();
        String[] array = new String[3];
        for (Annotation annotation : aClass.getAnnotations()){            
            Class<? extends Annotation> type = annotation.annotationType();
            array[0] = type.getName();    
            array[1] = type.getFields().toString();
            array[2] = method.getName();
            list.add(array);
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public static List<List<Object[]>> getAnnotationsFieldsOriginal(Class<?> aClass) throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> listOfAnnotation = new ArrayList<>();
        //for(Annotation ann : aClass.getAnnotations()){
            Field[] fields = aClass.getDeclaredFields();
            for(Field field  : fields ){
                Object[] array = new Object[3];
                //final Annotation annotation = field.getAnnotation(ann.getClass());//??????????
                for(Annotation annotation : field.getAnnotations()) {
                    if (annotation != null) {
                        Object handler = Proxy.getInvocationHandler(annotation);
                        Field f;
                        try {
                            //This is the name of the field.
                            f = handler.getClass().getDeclaredField("memberValues");
                        } catch (NoSuchFieldException | SecurityException e) {
                            throw new IllegalStateException(e);
                        }
                        f.setAccessible(true);
                        Map<String, Object> memberValues;
                        try {
                            memberValues = (Map<String, Object>) f.get(handler);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            throw new IllegalStateException(e);
                        }
                        List<Object[]> list = new ArrayList<>();
                        if (null != annotation) {
                            array[0] = annotation.annotationType().getName();//javax.persistence.column
                            for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                                array[1] = entry.getKey();
                                array[2] = entry.getValue();
                                list.add(array.clone());
                            }
                        }
                        listOfAnnotation.add(list);
                    }
                }
            }
        //}
        return listOfAnnotation;
    }

    /**
     * Method for check if exists a annotation a a filed of the specific class
     * Usage: System.out.println(isRequired(Employee.class, "email"));
     * @param aClass
     * @return
     * @throws NoSuchFieldException
     */
    @SuppressWarnings("rawtypes")
    public static List<List<Object[]>> getAnnotationsFields(Class<?> aClass) throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> listOfAnnotation = new ArrayList<>();
          Field[] fields = aClass.getDeclaredFields();
          for(Field field  : fields ){
              List<Object[]> list = new ArrayList<>();
              list = getAnnotationsField(aClass, field);
              if(list!=null) {
                  listOfAnnotation.add(list);
              }
          }
        return listOfAnnotation;
    }

    /**
     * Method for check if exists a annotation a a filed of the specific class
     * Usage: System.out.println(isRequired(Employee.class, "email"));
     * @required hibernate 
     * @param aClass
     * @param field
     * @return
     * @throws NoSuchFieldException 
     */
    @SuppressWarnings("rawtypes")
    public static List<Object[]> getAnnotationsField(Class<?> aClass, Field field)
            throws SecurityException, NoSuchFieldException {
        List<Object[]> list = new ArrayList<>();
        for(Annotation annotation : field.getAnnotations()) {
            String fieldName = field.getName();
            //final Annotation annotation = field.getAnnotation(annotationClass);
            list = getAnnotationField(aClass, annotation, fieldName);
            if(list.isEmpty()) {
                list = null;
            }
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public static List<Object[]> getAnnotationField(Annotation annotation)
            throws SecurityException, NoSuchFieldException {
        List<Object[]> list = new ArrayList<>();
        Object[] array = new Object[3];
        if(annotation!=null) {
            Object handler = Proxy.getInvocationHandler(annotation);
            Field f;
            try {
                //This is the name of the field.
                f = handler.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            f.setAccessible(true);
            Map<String, Object> memberValues;
            try {
                memberValues = (Map<String, Object>) f.get(handler);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            if (null != annotation) {
                array[0] = annotation.annotationType().getName();//javax.persistence.column
                for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                    array[1] = entry.getKey();
                    array[2] = entry.getValue();
                    list.add(array.clone());
                }
            }
            return list;
        }
        return null;
    }

    public static List<Object[]> getAnnotationField(Class<?> aClass, Annotation annotation,String fieldName )
            throws SecurityException, NoSuchFieldException {
        return getAnnotationField(annotation);
    }

    @SuppressWarnings("rawtypes")
    public static List<Object[]> getAnnotationField(Class<? extends Annotation> annotationClass,Field field)
            throws SecurityException, NoSuchFieldException {
        List<Object[]> list = new ArrayList<Object[]>();
        Object[] array = new Object[3];
        final Annotation annotation = field.getAnnotation(annotationClass);
        if(annotation!=null) {
            Object handler = Proxy.getInvocationHandler(annotation);
            Field f;
            try {
                //This is the name of the field.
                f = handler.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            f.setAccessible(true);
            Map<String, Object> memberValues;
            try {
                memberValues = (Map<String, Object>) f.get(handler);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            if (null != annotation) {
                array[0] = annotation.annotationType().getName();//javax.persistence.column
                for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                    array[1] = entry.getKey();
                    array[2] = entry.getValue();
                    list.add(array.clone());
                }
            }
            return list;
        }
        return null;
    }

    public static List<Object[]> getAnnotationField(Class<?> aClass, Class<? extends Annotation> annotationClass,String fieldName )
            throws SecurityException, NoSuchFieldException {
        return getAnnotationField(annotationClass,getFieldByName(aClass, fieldName));
    }

    public static List<List<Object[]>> getAnnotationsClass(Class<?> aClass)
            throws SecurityException, NoSuchFieldException {
        List<List<Object[]>> result = new ArrayList<List<Object[]>>();
        for(Annotation annotation : aClass.getAnnotations()){
            List<Object[]> list = getAnnotationClass(annotation);
            result.add(list);
        }
        return result;
    }

    public static List<Object[]> getAnnotationClass(Annotation annotation){
        List<Object[]> list = new ArrayList<>();
        Object[] array = new Object[3];
        if(annotation!=null) {
            Object handler = Proxy.getInvocationHandler(annotation);
            Field f;
            try {
                //This is the name of the field.
                f = handler.getClass().getDeclaredField("memberValues");
            } catch (NoSuchFieldException | SecurityException e) {
                throw new IllegalStateException(e);
            }
            f.setAccessible(true);
            Map<String, Object> memberValues;
            try {
                memberValues = (Map<String, Object>) f.get(handler);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
            if (null != annotation) {
                array[0] = annotation.annotationType().getName();//javax.persistence.column
                for (Map.Entry<String, Object> entry : memberValues.entrySet()) {
                    array[1] = entry.getKey();
                    array[2] = entry.getValue();
                    list.add(array.clone());
                }
            }
            return list;
        }
        return null;
    }




        /**
         * Method for check if exists a annotation a a filed of the specific class
         * Usage: System.out.println(isRequired(Employee.class, "email"));
         * @required hibernate
         * @param aClass
         * @return
         * @throws NoSuchFieldException
         */
    @SuppressWarnings("rawtypes")
    public static String[] getAnnotationHibernateClass(Class<?> aClass) throws SecurityException {
        String[] array = new String[3];
        boolean b = false;
        //Annotation ann = aClass.getAnnotation(Entity.class);
        //String[] array = new String[3];
        final javax.persistence.Entity entity = aClass.getAnnotation(javax.persistence.Entity.class);
        if (null != entity) {
            array[0] = entity.name();
        }
        final javax.persistence.Table table = aClass.getAnnotation(javax.persistence.Table.class);
        if (null != table) {
            array[1] = table.schema();
            array[2] = table.name();
        }
        return array;
    }

    /**
    * Changes the annotation value for the given key of the given annotation to newValue and returns
    * the previous value.
    * @Usage: final Something annotation = (Something) Foobar.class.getAnnotations()[0];
    *         System.out.println("oldAnnotation = " + annotation.someProperty());
    *         changeAnnotationValue(annotation, "someProperty", "another value");
    *         System.out.println("modifiedAnnotation = " + annotation.someProperty());
    * @href: http://stackoverflow.com/questions/14268981/modify-a-class-definitions-annotation-string-parameter-at-runtime/14276270#14276270
    */
   @SuppressWarnings("unchecked")
   private static Object updateAnnotationValue(Annotation annotation, String key, Object newValue){
       Object handler = Proxy.getInvocationHandler(annotation);
       Field f;
       try {
           //This is the name of the field.
           f = handler.getClass().getDeclaredField("memberValues");
       } catch (NoSuchFieldException | SecurityException e) {
           throw new IllegalStateException(e);
       }
       f.setAccessible(true);
       Map<String, Object> memberValues;
       try {
           memberValues = (Map<String, Object>) f.get(handler);
       } catch (IllegalArgumentException | IllegalAccessException e) {
           throw new IllegalStateException(e);
       }
       Object oldValue = memberValues.get(key);
       if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
           throw new IllegalArgumentException();
       }
       memberValues.put(key, newValue);
       return oldValue;
   }

    public static void updateAnnotationFieldValue(
            Class<?> aClass,Class<? extends Annotation> annotationClass,String fieldName,String attributeName,String attributeValue) throws NoSuchFieldException {
        Field fieldColumn = getFieldByName(aClass, fieldName);
        Annotation annColumn = fieldColumn.getAnnotation(annotationClass);
        if(annColumn!=null) {
            ReflectionKit.updateAnnotationValue(annColumn, attributeName, attributeValue);
        }
    }

    public static void updateAnnotationClassValue(Class<?> aClass,Class<? extends Annotation> annotationClass,String attributeName,String attributeValue){
        Annotation ann = aClass.getAnnotation(annotationClass);
        if(ann!=null) {
            ReflectionKit.updateAnnotationValue(ann, attributeName,attributeValue);
        }
    }

    public static void updateFieldClass(Class<?> aClass,Object myField,String fieldName)
            throws NoSuchFieldException,IllegalAccessException {
        //Class  aClass = MyObject.class
        Field field = aClass.getField(fieldName);
        //MyObject objectInstance = new MyObject();
        field.setAccessible(true);
        Object value = field.get(myField);
        field.set(myField, value);
    }

    public static void setField(Object object, String fieldName, Object value)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    public static Object getFieldValue(Object object, String fieldName)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        return getFieldValue(object.getClass(),fieldName);
    }

    public static Object getFieldValue(Class<?> aClass, String fieldName)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        Field field = aClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object returnValue = (Object) field.get(aClass);
        field.setAccessible(false);
        return returnValue;
    }

    public static List<Object> getFieldsValue(Class<?> aClass) throws NoSuchFieldException, IllegalAccessException {
        List<Object> list = new ArrayList<>();
        while(aClass.getSuperclass() != null){
            aClass = aClass.getSuperclass() ;
            //Fetch all fields ..
            for(Field field : aClass.getDeclaredFields()){
                list.add(getFieldValue(aClass,field.getName()));
            }
        }
        return list;
    }

    public static Object copyFieldToClass(Object sourceObject, Class targetClass) {
        Object targetValue = null;
        try {
            targetValue = (Object) targetClass.newInstance();
            for (Field field : sourceObject.getClass().getDeclaredFields()) {
                try {
                    setField(targetValue, field.getName(), getFieldValue(sourceObject, field.getName()));
                } catch (NoSuchFieldException e) {
                    //IgnoreLogging.logInfo(ReflectionUtil.class, "Ignored Field " + field.getName());
                }
            }
        } catch (Exception e) {
            SystemLog.exception(e);
        }
        return targetValue;
    }



}