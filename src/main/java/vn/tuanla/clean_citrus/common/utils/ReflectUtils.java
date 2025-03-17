package vn.tuanla.clean_citrus.common.utils;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.apache.commons.text.StringEscapeUtils;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.tuanla.clean_citrus.common.AuditDelete;
import vn.tuanla.clean_citrus.common.ColumnDisplayInfo;
import vn.tuanla.clean_citrus.common.TableDisplayInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReflectUtils {

    private static final String ANNO_COLUMN_NAME = "name=";
    private static final String ANNO_TEMPORAL_TYPE = "value=";
    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    private ReflectUtils() {
    }

    public static Method getGetterByName(Class c, String methodName) {
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            if (isGetter(method) && method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static Method getGetterByName(List<Method> getters, String methodName) {
        for (Method method : getters) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static List<String> getColumnBeanNames(Class c) {
        List<String> result = new ArrayList<String>();
        List<Method> getters = getGetters(c);
        for (Method method : getters) {
            result.add(getColumnBeanName(method));
        }
        return result;
    }

    public static List<Method> getGetters(Class c) {
        List<Method> getters = new ArrayList<Method>();
        while (c != null) { // Lặp qua cả lớp cha
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                if (isGetter(method)) {
                    getters.add(method);
                }
            }
            c = c.getSuperclass(); // Lấy lớp cha tiếp theo
        }

        return getters;
    }

    public static List<Method> getSetters(Class c) {
        List<Method> setters = new ArrayList<Method>();
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            if (isSetter(method)) {
                setters.add(method);
            }
        }
        return setters;
    }

    public static String getAnnotationProperty(Class c, Method method, String property) {
        String result = "";
        Field[] arr = getAllFields(c);
        String attribute = method.getName();
        if (attribute.startsWith("get") && arr != null && arr.length > 0) {
            attribute = attribute.substring(3, 4).toLowerCase() + attribute.substring(4);
            for (Field field : arr) {
                if (field.getName().equals(attribute)) {
                    Annotation annos = field.getAnnotation(Column.class);
                    if (annos != null) {
                        String annoColumn = annos.toString();
                        result = annoColumn.substring(
                                annoColumn.indexOf(property) + property.length(),
                                annoColumn.indexOf(",", annoColumn.indexOf(property)));
                        if (DataUtils.isNullOrEmpty(result)) {
                            result = field.getName().toUpperCase();
                        }
                    }
                    break;
                }
            }
            if (DataUtils.isNullOrEmpty(result)) {
                result = attribute.replaceAll("(.)(\\p{Lu})", "$1_$2").toUpperCase();
            }
        }
        return result.replaceAll("\"", "");
    }
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();

        while (clazz != null) {
            // Lấy các field của class hiện tại và thêm vào danh sách
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                fieldList.add(field);
            }
            // Di chuyển lên class cha
            clazz = clazz.getSuperclass();
        }

        // Chuyển danh sách thành mảng Field[]
        return fieldList.toArray(new Field[0]);
    }
    public static String getAnnotationDisplayProperty(Class c, Method method, String property) {
        String result = "";
        Field[] arr = getAllFields(c);
        String attribute = method.getName();
        if (attribute.startsWith("get") && arr != null && arr.length > 0) {
            attribute = attribute.substring(3, 4).toLowerCase() + attribute.substring(4);
            for (Field field : arr) {
                if (field.getName().equals(attribute)) {
                    Annotation annos = field.getAnnotation(ColumnDisplayInfo.class);
                    if (annos != null) {
                        String annoColumn = annos.toString();
                        annoColumn = annoColumn.substring(annoColumn.indexOf(property) + property.length());
                        annoColumn = annoColumn.replace(")", "");
                        if (annoColumn.contains(",")) {
                            result = annoColumn.substring(0, annoColumn.indexOf(",")).replace("\"", "").trim();
                        } else {
                            result = annoColumn.replace("\"", "").trim();
                        }
//                        if(DataUtil.isStringNullOrEmpty(result)) {
//                            result = field.getName().toUpperCase();
//                        }
                    }
                    break;
                }
            }
//            if(DataUtil.isStringNullOrEmpty(result)) {
//                result = attribute.replaceAll("(.)(\\p{Lu})", "$1_$2").toUpperCase();
//            }
        }
        return result.equals("''") ? null : result;
    }


    public static String getTableName(Class c) {
        Annotation annos = c.getAnnotation(Table.class);
        String result = "";
        if (annos != null) {
            String annoColumn = annos.toString();
            annoColumn = annoColumn.substring(annoColumn.indexOf(ANNO_COLUMN_NAME) + ANNO_COLUMN_NAME.length());
            result = annoColumn.substring(0, annoColumn.indexOf(",")).trim();
        }
        return result.replaceAll("\"", "");
    }

    public static String getDisplayTableName(Class c) {
        Annotation annos = c.getAnnotation(TableDisplayInfo.class);
        String name = "displayName=";
        String result = "";
        if (annos != null) {
            String annoColumn = annos.toString();
            annoColumn = annoColumn.substring(annoColumn.indexOf(name) + name.length());
            annoColumn = annoColumn.replace(")", "");
            if (annoColumn.contains(",")) {
                result = annoColumn.substring(0, annoColumn.indexOf(",")).trim();
            } else {
                result = annoColumn.trim();
            }
        }
        return StringEscapeUtils.unescapeJava(result).replaceAll("\"", "");

    }

    public static String getColumnBeanName(Method method) {
        String methodName = method.getName();
        return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
    }

    public static String getColumnName(Class c, Method method) {
        return getAnnotationProperty(c, method, ANNO_COLUMN_NAME);
    }

    public static String getDisplayColumnName(Class c, Method method) {
        return getAnnotationDisplayProperty(c, method, "displayName=");
    }

    public static String getTemporalType(Class c, Method method) {
        return getAnnotationProperty(c, method, ANNO_TEMPORAL_TYPE);
    }

    public static boolean isGetter(Method method) {
        boolean result = true;
        if (!method.getName().startsWith("get")
                || method.getParameterTypes().length != 0
                || method.getReturnType().equals(Void.TYPE)
                || method.getAnnotation(Transient.class) != null) {
            result = false;
        }
        return result;
    }

    public static boolean isSetter(Method method) {
        boolean result = true;
        if (!method.getName().startsWith("set")
                || method.getParameterTypes().length != 1
                || !method.getReturnType().equals(Void.TYPE)) {
            result = false;
        }
        return result;
    }

    public static HashMap<String, Method> getMapColumnAndGetter(Class c) {
        List<Method> getters = getGetters(c);
        HashMap<String, Method> mapGetter = new HashMap();
        for (Method method : getters) {
            String columnName = getColumnName(c, method);
            if (columnName != null && !mapGetter.containsKey(columnName)) {
                mapGetter.put(columnName, method);
            }
        }
        return mapGetter;
    }

    public static HashMap<String, Method> getMapColumnAndSetter(Class c) {
        List<Method> getters = getGetters(c);
        HashMap<String, Method> mapSetter = new HashMap();
        for (Method method : getters) {
            if (method != null) {
                Method setter = getSetter(c, method);
                if (setter != null) {
                    String columnName = getColumnName(c, method);
                    if (columnName != null && !mapSetter.containsKey(columnName)) {
                        mapSetter.put(columnName, setter);
                    }
                }
            }
        }
        return mapSetter;
    }

    public static Method getSetter(Class c, Method getter) {
        Method setter = null;
        String getterName = getter.getName();
        if (getterName != null && getterName.length() > 1) {
            String setterName = "s" + getterName.substring(1);
            Class returnType = getter.getReturnType();
            try {
                setter = c.getMethod(setterName, returnType);
            } catch (Exception ex) {
                logger.error("ERR_95870001: Error at ReflectUtils.getSetter() ", ex);
            }
        }
        return setter;
    }

    public static HashMap getHashmapDisplayResource(String packagePath, String displayResouceName) throws Exception, Exception {
        HashMap result = null;
        try {
//            packagePath = "com.viettel.common";
//            displayResouceName = "Const.CONTRACT.CHANNEL_TYPE.MAP_DATA_NAME";
            String className = displayResouceName.substring(0, displayResouceName.lastIndexOf(".")).replace(".", "$");
            String hashmapName = displayResouceName.substring(displayResouceName.lastIndexOf(".") + 1, displayResouceName.length());
            Class<?> act = Class.forName(packagePath + "." + className);
            Field field = act.getDeclaredField(hashmapName);
            Object value = field.get(result);
            result = (HashMap) value;
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    public static Field getIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null; // or throw an exception if preferred
    }

    public static Object getIdGetterValue(Object entity) throws IllegalAccessException {
        if (entity == null) {
            return null;
        }

        if (entity instanceof HibernateProxy) {
            LazyInitializer initializer = ((HibernateProxy) entity).getHibernateLazyInitializer();
            return initializer.getIdentifier();
        }

        Class<?> clazz = entity.getClass();

        Field idField = getIdField(clazz);
        if (idField != null) {
            idField.setAccessible(true); // make the private field accessible
            return idField.get(entity);
        }
        return null; // or throw an exception if preferred
    }

    public static Field getAuditDeleteField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(AuditDelete.class)) {
                return field;
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            for (Field field : superclass.getDeclaredFields()) {
                if (field.isAnnotationPresent(AuditDelete.class)) {
                    return field;
                }
            }
        }
        return null; // or throw an exception if preferred
    }

    public static boolean getAuditGetterValue(Object entity) throws IllegalAccessException {
        if (entity == null) {
            return false;
        }
        Class<?> clazz = entity.getClass();
        Field idField = getAuditDeleteField(clazz);
        if (idField != null) {
            idField.setAccessible(true); // make the private field accessible
            return (boolean) idField.get(entity);
        }
        return false; // or throw an exception if preferred
    }

}
