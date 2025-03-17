package vn.tuanla.clean_citrus.common.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import vn.tuanla.clean_citrus.CleanCitrusBeApplication;
import vn.tuanla.clean_citrus.common.ApiRequestInfo;
import vn.tuanla.clean_citrus.common.Constans;
import vn.tuanla.clean_citrus.common.DateUtil;
import vn.tuanla.clean_citrus.domain.dto.request.ActionDetailDTO;
import vn.tuanla.clean_citrus.domain.entity.ActionAudit;
import vn.tuanla.clean_citrus.domain.entity.ActionDetail;
import vn.tuanla.clean_citrus.exception.ConstraintViolationExceptionCustom;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataUtils {

    private static final Logger log = LoggerFactory.getLogger(DataUtils.class);

    public static final char DEFAULT_ESCAPE_CHAR_QUERY = '\\';

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmptyOrBlank(Collection<String> collection) {
        return isNullOrEmpty(collection) || collection.stream().allMatch(String::isBlank);
    }


    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isNullOrZero(Object o) {
        return o == null || safeToLong(o) == 0L;
    }

    public static boolean isNullOrZeroCompare(Integer o, Integer c) {
        if(!isNullOrZero(o) && !isNullOrZero(c)) {
            return o < c;
        }
        return false;
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty() || "".equalsIgnoreCase(string);
    }

    public static BigDecimal safeToBigDecimal(Object obj) {
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        } else if (!isNull(obj)) {
            try {
                return new BigDecimal(obj.toString().trim());
            } catch (Exception e) {
                //                log.error(e.getMessage(), e);
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal safeToBigDecimal(Object obj, BigDecimal defaultValue) {
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        } else if (!isNull(obj) && !isNullOrEmpty(obj.toString())) {
            try {
                return new BigDecimal(obj.toString().trim());
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static String safeToString(Object obj) {
        return Optional.ofNullable(obj).map(Object::toString).orElse("");
    }

    public static String safeToString(Object obj, String defaultValue) {
        return Optional.ofNullable(obj).map(Object::toString).orElse(safeToString(defaultValue));
    }

    public static String setNullIfEmptyString(Object obj) {
        if (DataUtils.isNull(obj) || DataUtils.isNullOrEmpty(obj.toString())) {
            return null;
        }
        return obj.toString();
    }

    public static Object setNullIfEmptyObject(Object obj) {
        if (DataUtils.isNull(obj) || DataUtils.isNullOrEmpty(obj.toString())) {
            return null;
        }
        return obj;
    }

    public static String setNullIfEmptyString(String obj) {
        if (DataUtils.isNull(obj) || DataUtils.isNullOrEmpty(obj.toString())) {
            return null;
        }
        return obj;
    }

    public static Timestamp safeToTimestamp(Object obj) {
        return Optional.ofNullable(obj).map(o -> (Timestamp) o).orElse(null);
    }

    public static Integer safeToInteger(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            try {
                return Integer.parseInt(obj.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return 0;
            }
        }
    }

    public static Long safeToLong(Object obj1) {
        long result = 0L;
        if (obj1 != null) {
            if (obj1 instanceof BigDecimal) {
                return ((BigDecimal) obj1).longValue();
            }
            if (obj1 instanceof BigInteger) {
                return ((BigInteger) obj1).longValue();
            }
            try {
                result = Long.parseLong(obj1.toString());
            } catch (Exception ignored) {
            }
        }

        return result;
    }

    public static BigInteger safeToBigInteger(Object obj) {
        if (obj instanceof BigInteger) {
            return (BigInteger) obj;
        } else if (!isNull(obj)) {
            try {
                return new BigInteger(obj.toString());
            } catch (Exception e) {
                //                log.error(e.getMessage(), e);
                return BigInteger.ZERO;
            }
        }
        return BigInteger.ZERO;
    }

    public static String makeLikeQuery(String s) {
        if (isNullOrEmpty(s)) {
            return null;
        }

        // Escape các ký tự đặc biệt cho Oracle
        s = s.trim()
                .replace("\\", DEFAULT_ESCAPE_CHAR_QUERY + "\\") // Escape backslash
                .replace("%", DEFAULT_ESCAPE_CHAR_QUERY + "%")   // Escape %
                .replace("_", DEFAULT_ESCAPE_CHAR_QUERY + "_");  // Escape _

        // Thêm dấu % vào đầu và cuối chuỗi
        return "%" + s + "%";
    }

    public static String timestampToString(Timestamp fromDate, String pattern) {
        return Optional.ofNullable(fromDate).map(tmp -> {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(tmp);
        }).orElse("");
    }

    public static String formatNumberWithComma(Double number, String pattern) {
        return Optional.ofNullable(number).map(tmp -> {
            DecimalFormat df = new DecimalFormat(pattern);
            return df.format(tmp);
        }).orElse("");

    }

    public static String dateToString(Date fromDate, String pattern) {
        return Optional.ofNullable(fromDate).map(tmp -> {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(tmp);
        }).orElse("");
    }

    public static Double safeToDouble(Object obj, Double defaultValue) {
        return Optional.ofNullable(obj).map(o -> {
            try {
                return Double.parseDouble(o.toString());
            } catch (Exception e) {
                //                log.error(e.getMessage(), e);
                return defaultValue;
            }
        }).orElse(defaultValue);
    }

    public static Double safeToDouble(Object obj1) {
        return safeToDouble(obj1, 0.0);
    }

    public static Date safeToDate(Object obj) {
        if (obj instanceof Date) {
            return (Date) obj;
        } else if (obj instanceof LocalDateTime lt) {
            return Date.from(lt.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    public static LocalDate safeToLocalDate(Object obj) {
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        }
        return null;
    }

    public static LocalDateTime safeToLocalDateTime(Object obj) {
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        return null;
    }

    public static List<String> changeParamTypeSqlToJava(String sqlType) {
        String[] tmp = sqlType.trim().split(",");
        List<String> stringList = new ArrayList<>();
        for (String s : tmp) {
            s = s.trim().replaceAll("\\s+,", "");
            StringBuilder builder = new StringBuilder();
            String[] words = s.split("[\\W_]+");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (words.length > 1) {
                    if (i == 0) {
                        word = word.isEmpty() ? word : word.toLowerCase();
                    } else {
                        word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
                    }
                }
                builder.append(word);
            }
            stringList.add(builder.toString());
        }
        return stringList;
    }

    public static <T> List<T> convertListObjectsToClass(List<String> attConvert, List<Object[]> objects, Class<T> clazz) {
        if (DataUtils.isNullOrEmpty(objects)) {
            return new ArrayList<>();
        }
        return objects.stream().map(item -> {
            try {
                return convertObjectsToClass(attConvert, item, clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    public static <T> T convertObjectsToClass(List<String> attConvert, Object[] objects, Class<T> clazz) throws Exception {
        Object object = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < attConvert.size(); i++) {
            Field f;
            int finalIndex = i;
            f = Arrays.stream(fields).filter(item -> attConvert.get(finalIndex).equals(item.getName())).findFirst().orElse(null);
            if (f == null) throw new Exception("Can not find item :  " + attConvert.get(finalIndex));
            if (f != null) {
                f.setAccessible(true);
                Class<?> t = f.getType();
                if (objects[i] == null)
                    continue;
                switch (t.getName()) {
                    case "java.lang.String":
                        if (objects[i] instanceof String || objects[i] instanceof Long || objects[i] instanceof BigInteger ||
                                objects[i] instanceof Integer || objects[i] instanceof BigDecimal) {
                            f.set(object, DataUtils.safeToString(objects[i]));
                        } else if (objects[i] instanceof java.sql.Date || objects[i] instanceof Date
                                || objects[i] instanceof Timestamp
                        ) {
                            f.set(object, date2StringByPattern(DataUtils.safeToDate(objects[i]), "dd/MM/yyyy HH:mm:ss"));
                        }
                        break;
                    case "java.lang.Long":
                    case "long":
                        f.set(object, DataUtils.safeToLong(objects[i]));
                        break;
                    case "java.lang.Double":
                    case "double":
                        f.set(object, DataUtils.safeToDouble(objects[i]));
                        break;
                    case "java.lang.Boolean":
                    case "boolean":
                        f.set(object, objects[i]);
                        break;
                    case "java.util.Date":
                        f.set(object, DataUtils.safeToDate(objects[i]));
                        break;
                    case "java.time.LocalDate":
                        f.set(object, DataUtils.safeToLocalDate(objects[i]));
                        break;
                    case "java.time.LocalDateTime":
                        f.set(object, DataUtils.safeToLocalDateTime(objects[i]));
                        break;
                    case "java.sql.Timestamp":
                        f.set(object, DataUtils.safeToTimestamp(objects[i]));
                        break;
                    case "java.lang.Integer":
                    case "int":
                        f.set(object, DataUtils.safeToInteger(objects[i]));
                        break;
                    case "java.math.BigInteger":
                        f.set(object, DataUtils.safeToBigInteger(objects[i]));
                        break;
                    case "java.math.BigDecimal":
                        f.set(object, DataUtils.safeToBigDecimal(objects[i]));
                        break;
                    default:
                        break;
                }
            }
        }
        return (T) object;
    }

    public static String date2StringByPattern(Date date, String pattern) {
        if (date == null || DataUtils.isNullOrEmpty(pattern)) {
            return null;
        }

        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static Date stringToDate(String dateStr, String pattern) throws ParseException {
        if (dateStr == null || dateStr.isEmpty())
            return new Date();
        DateFormat sourceFormat = new SimpleDateFormat(pattern);
        return sourceFormat.parse(dateStr);
    }

    public static boolean isNotEquals(Object a, Object b) {
        if (a == null && b == null) {
            return false;
        }
        if (a != null && b != null) {
            Class cA = a.getClass();
            Class cB = b.getClass();
            if (cA == cB) {
                if (a.equals(b)) {
                    return false;
                }
                if (cA.equals(String.class)) {
                    String strA = String.valueOf(a);
                    String strB = String.valueOf(b);
                    if (strA.trim().equals(strB.trim())) {
                        return false;
                    }
                }
            }
            if ((cA.equals(Timestamp.class) && cB.equals(Date.class) && ((Timestamp) a).getTime() == ((Date) b).getTime())
                    || (cA.equals(Date.class) && cB.equals(Timestamp.class) && ((Date) a).getTime() == ((Timestamp) b).getTime())) {
                return false;
            }
        }
        return (a == null || b != null || !a.getClass().equals(String.class) || !String.valueOf(a).trim().isEmpty()) &&
                (b == null || a != null || !b.getClass().equals(String.class) || !String.valueOf(b).trim().isEmpty());
    }

    public static String StringValueOf(Object a, Object valueEntity) throws Exception {
        if (a == null) {
            return null;
        }
        Class c = a.getClass().getSuperclass();
        Class cb = a.getClass();
        if (c.equals(Date.class) || c.equals(Timestamp.class)) {
            return DataUtils.date2StringByPattern((Date) a, "dd/MM/yyyy HH:mm:ss");
        } else if(c.equals(LocalDateTime.class) || cb.equals(LocalDateTime.class)) {
            return DateUtil.formatLocalDateTime((LocalDateTime) a);
        }else if (c.getPackageName().contains("com.noffice.service.entity")) {
            Object valuee = ReflectUtils.getIdGetterValue(a);
            return valuee == null ? null : valuee.toString();
        } else if (c.getPackageName().contains("org.hibernate.collection.spi")) {
            return null;
        } else if (cb.equals(Date.class) || cb.equals(Timestamp.class)) {
            return DataUtils.date2StringByPattern((Date) a, "dd/MM/yyyy HH:mm:ss");
        }
        return String.valueOf(a).trim();

    }

    public static String StringValueOf(Object a) {
        if (a == null) {
            return null;
        }
        Class c = a.getClass();
        if (c.equals(Date.class) || c.equals(Timestamp.class)) {
            return DataUtils.date2StringByPattern((Date) a, "dd/MM/yyyy HH:mm:ss");
        }
        return String.valueOf(a).trim();
    }

    private static String getValueField(Object a, String name) {
        if (a == null || DataUtils.isNullOrEmpty(name)) {
            return null;
        }
        try {
            Field field = a.getClass().getDeclaredField(name);
            field.setAccessible(true);
            Class<?> c = field.getType();
            if (c.equals(Date.class) || c.equals(Timestamp.class)) {
                return DataUtils.date2StringByPattern((Date) field.get(a), "dd/MM/yyyy HH:mm:ss");
            }
            return DataUtils.safeToString(field.get(a));
        } catch (Exception e) {
            log.error("====NoSuchMethodException do not get value=====");
            return null;
        }

    }

    public static List<ActionDetailDTO> getListActionDetail(Object oldBO, Object newBO, Long objectId,
                                                            Date createDate, EntityManager em, Long userId,
                                                            List<String> ignored, String actionType, ErrorManagementUtils errorManagementUtils) throws Exception {
        List<ActionDetailDTO> result = new ArrayList<>();
        Class c = oldBO != null ? oldBO.getClass() : (newBO != null ? newBO.getClass() : null);
        if (c != null) {
            List<Method> getters = ReflectUtils.getGetters(c);
            if (getters != null && !getters.isEmpty()) {
                for (Method m : getters) {
                    if (m != null) {
                        Object oldValue = oldBO == null ? null : m.invoke(oldBO);
                        Object newValue = newBO == null ? null : m.invoke(newBO);
                        boolean isNotEquals = isNotEquals(oldValue, newValue);
                        boolean ignore = DataUtils.isNullOrEmpty(ignored) ?
                                !ReflectUtils.getColumnName(c, m).equalsIgnoreCase("id") : !ignored.contains(ReflectUtils.getColumnName(c, m).toUpperCase());
                        boolean checkListIgnoreOld = oldValue != null && oldValue.getClass().getPackageName().contains("org.hibernate.collection.spi");
                        boolean checkListIgnoreNew = newValue != null && newValue.getClass().getPackageName().contains("org.hibernate.collection.spi");
                        if (isNotEquals && ignore && !(checkListIgnoreOld || checkListIgnoreNew)) {
                            ActionDetailDTO processDetail = new ActionDetailDTO();
                            processDetail.setRowId(objectId);
                            processDetail.setIssueDate(createDate);
                            processDetail.setStaffId(userId);
                            processDetail.setTableName(ReflectUtils.getTableName(c));
                            processDetail.setTableDisplayName(ReflectUtils.getDisplayTableName(c));
                            processDetail.setColName(ReflectUtils.getColumnName(c, m));
                            processDetail.setColDisplayName(isNullOrEmpty(ReflectUtils.getDisplayColumnName(c, m)) ?
                                    ReflectUtils.getColumnName(c, m) :
                                    errorManagementUtils.getMessageByCode(ReflectUtils.getDisplayColumnName(c, m).replace("\"", "")));
                            processDetail.setOldValue(StringValueOf(oldValue, oldBO));
                            processDetail.setNewValue(StringValueOf(newValue, newBO));
                            processDetail.setActionType(actionType);
                            processDetail.setParentColumnOldValue(getValueField(oldBO,
                                    ReflectUtils.getAnnotationDisplayProperty(c, m, "displayParent=")));
                            processDetail.setPracticeColumnNewValue(getValueField(oldBO,
                                    ReflectUtils.getAnnotationDisplayProperty(c, m, "displayParent=")));
                            String sqlSelectName = ReflectUtils.getAnnotationDisplayProperty(c, m, "sqlSelectName=");

                            if (!DataUtils.isNullOrEmpty(sqlSelectName)) {
                                String sql =  sqlSelectName.replace("\\", "");
                                Query query = em.createQuery(sql);
                                String oldValueName = "", newValueName = "";
                                if (null != oldValue) {
                                    query.setParameter(1, StringValueOf(oldValue));
                                    try {
                                        List resultList = query.getResultList();
                                        if(!isNullOrEmpty(resultList)) {
                                            oldValueName = StringValueOf(resultList.get(0));
                                        }else {
                                            oldValueName = StringValueOf(oldValue);
                                        }
                                    } catch (Exception ex) {
                                        oldValueName = StringValueOf(oldValue);
                                    }
                                }
                                if (null != newValue) {
                                    query.setParameter(1, StringValueOf(newValue));
                                    try {
                                        newValueName = StringValueOf(query.getSingleResult());
                                    } catch (Exception ex) {
                                        throw new Exception("Không lấy được thông tin tên tương ứng với giá trị mới trường:" + processDetail.getColDisplayName());
                                    }
                                }
                                processDetail.setOldDisplayValue(oldValueName);
                                processDetail.setNewDisplayValue(newValueName);
                            } else {
                                String mapResource = ReflectUtils.getAnnotationDisplayProperty(c, m, "mapResource=");
                                String packagePath = ReflectUtils.getAnnotationDisplayProperty(c, m, "packagePath=");

                                if (!DataUtils.isNullOrEmpty(mapResource) && !DataUtils.isNullOrEmpty(packagePath)) {
                                    HashMap<String, String> map = ReflectUtils.getHashmapDisplayResource(packagePath, mapResource);
                                    processDetail.setOldDisplayValue(map.get(processDetail.getOldValue()));
                                    processDetail.setNewDisplayValue(map.get(processDetail.getNewValue()));
                                }
                            }
                            if (DataUtils.isNullOrEmpty(processDetail.getNewDisplayValue())) {
                                processDetail.setNewDisplayValue(processDetail.getNewValue());
                            }
                            if (DataUtils.isNullOrEmpty(processDetail.getOldDisplayValue())) {
                                processDetail.setOldDisplayValue(processDetail.getOldValue());
                            }
                            processDetail.setDeleted(Constans.DELETE.INACTIVE);
                            processDetail.setStatus(Constans.STATUS.ACTIVE);
                            result.add(processDetail);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String getStrFirstDayOfPreviousMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return new SimpleDateFormat("yyyy-MM").format(cal.getTime()) + "-01";
    }

    public static List getMaxLength(List<List> list) {
        return list.stream().max(Comparator.comparingInt(List::size)).get();
    }

    public static Date getDayOf(Date date, int minusMonth, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, minusMonth);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return cal.getTime();
    }

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();


    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationExceptionCustom(violations);
        }
    }

    public static boolean isValidDateTimeFormat(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false); // Thiết lập chế độ kiểm tra nghiêm ngặt

        try {
            Date date = sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static <T> T deepCopy(T object) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(object);
            out.flush();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

    public static <T extends Serializable> List<T> deepCopy(List<T> list) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(list);
            out.flush();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            return (List<T>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

    //  Hàm loại bỏ đấu phẩy ở cuối
    public static String removeLastComma(String value) {
        if (!isNullOrEmpty(value) && value.endsWith(",")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    public static InputStream getFileFromResource(String fileName) {
        try {
            return CleanCitrusBeApplication.class.getResourceAsStream(fileName);
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("File not found on path: " + fileName);
        }
    }

    public static ActionAudit getActionAuditVer2(Long objectId, String actionCode, String tableName,
                                                 List<ActionDetail> actionDetailList, String description, Long objectParentId) {
        if (isNullOrEmpty(actionDetailList)) {
            return null;
        }
        ActionAudit actionAudit = new ActionAudit();
        actionAudit.setObjectId(objectId);
        actionAudit.setActionCode(actionCode);
        actionAudit.setTableName(tableName);
        actionAudit.setActionDatetime(new Date());
        actionAudit.setStatus(Constans.STATUS.ACTIVE);
        actionAudit.setDeleted(Constans.DELETE.INACTIVE);
        actionAudit.setDescription(description);
        actionAudit.setObjectParentId(objectParentId == null ? objectId : objectParentId);
        return actionAudit;
    }

    //    todo hiện tại mới compare Date, Number và String, muốn sort kiểu khác thì thêm vào compareTo nhé
    public static <T> List<T> sortList(List<T> value, Class<T> clazz, List<String> fieldSort) {
        value = value == null ? new ArrayList<>() : value;
        if (isNullOrEmpty(fieldSort)) {
            return value;
        } else
            return value.stream()
                    .sorted((object1, object2) -> {
                        try {
                            for (String propertyName : fieldSort) {
                                try {
                                    Field field = clazz.getDeclaredField(propertyName);
                                    field.setAccessible(true);
                                    Object value1 = field.get(object1);
                                    Object value2 = field.get(object2);
                                    if (value1 == null && value2 == null) {
                                        continue;
                                    }
                                    if (value1 == null) {
                                        return 1;
                                    }
                                    if (value2 == null) {
                                        return -1;
                                    }
                                    int result = compareTo(value1, value2);

                                    if (result != 0) {
                                        return result;
                                    }
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                            return 0;
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        return 0;
                    }).collect(Collectors.toList());
    }

    public static <T> List<T> sortList(List<T> value, List<String> fieldSort) {
        Class clazz = value.getClass();
        value = value == null ? new ArrayList<>() : value;
        if (isNullOrEmpty(fieldSort)) {
            return value;
        } else
            return value.stream()
                    .sorted((object1, object2) -> {
                        try {
                            for (String propertyName : fieldSort) {
                                try {
                                    Field field = clazz.getDeclaredField(propertyName);
                                    field.setAccessible(true);
                                    Object value1 = field.get(object1);
                                    Object value2 = field.get(object2);
                                    if (value1 == null && value2 == null) {
                                        continue;
                                    }
                                    if (value1 == null) {
                                        return 1;
                                    }
                                    if (value2 == null) {
                                        return -1;
                                    }
                                    int result = compareTo(value1, value2);

                                    if (result != 0) {
                                        return result;
                                    }
                                } catch (Exception e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                            return 0;
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                        return 0;
                    }).collect(Collectors.toList());
    }

    private static int compareTo(Object a, Object b) throws Exception {
        if (a == null || b == null) {
            return -1;
        }
        Class c = a.getClass();
        if (c.equals(Date.class) || c.equals(Timestamp.class)) {
            return ((Date) a).compareTo((Date) b);
        }
        return safeToString(a).compareTo(safeToString(b));
    }

    public static boolean isLongNumberic(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.parseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    public static void trimObject(Object object, boolean isDataSearch, List<String> lsFieldUpper, List<String> lsFieldLower) throws Exception {
        try {
            if (object == null) {
                return;
            }

            Field[] fields = object.getClass().getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {

                Field f = fields[i];
                Class t = f.getType();

                if (t.isAssignableFrom(String.class)) {
                    f.setAccessible(true);
                    String value = safeToString(f.get(object));
                    if (!isNullOrEmpty(value)) {
//                        if(isDataSearch){
//                            value = value.replace("_", "\\_").replace("%", "\\%");
//                        }
                        if (!isNullOrEmpty(lsFieldUpper) && lsFieldUpper.contains(f.getName())) {
                            f.set(object, value.trim().toUpperCase());
                        } else if (!isNullOrEmpty(lsFieldLower) && lsFieldLower.contains(f.getName())) {
                            f.set(object, value.trim().toLowerCase());
                        } else {
                            f.set(object, value.trim());
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw e;
        }
    }

    public static String normalizeName(String input) {
        if (input == null || input.isEmpty()) {
            return ""; // Xử lý trường hợp input null hoặc rỗng
        }

        // Chuyển toàn bộ chuỗi thành viết hoa
        input = input.toUpperCase();

        // Thay thế 'Đ' (hoặc 'đ' nếu nhập sai) thành 'D'
        input = input.replaceAll("Đ", "D");

        // Loại bỏ dấu (diacritics)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noAccents = pattern.matcher(normalized).replaceAll("");

        // Chỉ giữ lại các ký tự chữ cái (A-Z) và số (0-9)
        return noAccents.replaceAll("[^A-Z0-9]", "");
    }

    public static String fullNameVn(String lastName, String middleName, String firstName) {
        return String.format("%s %s %s",
                lastName.trim(),
                Optional.ofNullable(middleName).orElse("").replaceAll("\\s+", " ").trim(),
                firstName.trim());
    }


    public static String fullName(String lastName, String middleName, String firstName) {
        return normalizeName(
                lastName.trim()
                        + Optional.ofNullable(middleName).orElse("").replaceAll("\\s+", "").trim()
                        + firstName.trim());
    }

    public static String removeExtraSpaces(String str) {
        return Optional.ofNullable(str).orElse("").replaceAll("\\s+", " ").trim();
    }

    public static String removeAllSpaces(String str) {
        return Optional.ofNullable(str).orElse("").replaceAll("\\s+", "").trim();
    }

    public static String removeSlashes(String date) {
        if (date != null) {
            return date.replace("/", "");
        }
        return null;
    }

    public static String dobTextSub(String dobText) {
        Integer year = 4;
        Integer may = 6;
        Integer date = 8;
        if (year.equals(dobText.length())) {
            return dobText;
        } else if (may.equals(dobText.length())) {
            return dobText = dobText.substring(0, 2) + "/" + dobText.substring(2, 6);
        } else if (date.equals(dobText.length())) {
            return dobText = dobText.substring(0, 2) + "/" + dobText.substring(2, 4) + "/" + dobText.substring(4, 8);
        } else {
            return dobText;
        }
    }

    public static boolean isValidDateDobText(String dateStr) {
        if(DataUtils.isNullOrEmpty(dateStr)){
            return false;
        }
        // Biểu thức chính quy cho các định dạng
        String dateRegex = "^(?:(\\d{2})/(\\d{2})/(\\d{4})|(\\d{2})/(\\d{4})|(\\d{4}))$";
        String regexYear = "^\\d{4}$";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(dateStr);

        if (!matcher.matches()) return false; // Không khớp với bất kỳ định dạng nào

        // Kiểm tra định dạng dd/MM/yyyy
        if (matcher.group(1) != null && matcher.group(2) != null && matcher.group(3) != null) {
            int day = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int year = Integer.parseInt(matcher.group(3));
            return isValidDayMonthYear(day, month, year);
        }

        // Kiểm tra định dạng MM/yyyy
        if (matcher.group(4) != null && matcher.group(5) != null) {
            int month = Integer.parseInt(matcher.group(4));
            return month >= 1 && month <= 12; // Chỉ cần kiểm tra tháng hợp lệ
        }

        if (matcher.group(6) != null) {
            int year = Integer.parseInt(matcher.group(6), 10);
            return year >= 1000; // Kiểm tra năm hợp lệ
        }

        // Kiểm tra trường hợp không hợp lệ như 0200
        if (dateStr.length() == 4 && dateStr.matches(regexYear)) {
            return false; // Không hợp lệ nếu là dạng YYYY và <= 999
        }

        return true; // Nếu chỉ có yyyy thì coi như hợp lệ
    }

    public static boolean isValidDayMonthYear(int day, int month, int year) {
        // Validate month first to avoid ArrayIndexOutOfBoundsException
        if (month < 1 || month > 12) {
            return false; // Invalid month
        }

        // Check if the year is a leap year
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);

        // Days in each month
        int[] daysInMonth = {
                31, isLeapYear ? 29 : 28, // February
                31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        };

        // Validate day
        return day >= 1 && day <= daysInMonth[month - 1];
    }

    public static String checkDateWithCurrentDate(String dobText, int format, LocalDate currentDate) {
        try {
            switch (format) {
                case 4: {
                    int year = Integer.parseInt(dobText);
                    if (year <= 0) {
                        return " Năm không hợp lệ.";
                    }
                    if (year > currentDate.getYear()) {
                        return " Năm sinh phải nhỏ hơn hoặc bằng năm hiện tại.";
                    }
                    break;
                }
                case 7: {
                    String[] parts = dobText.split("/");
                    if (parts.length != 2) {
                        return " Định dạng tháng/năm không hợp lệ.";
                    }
                    int month = Integer.parseInt(parts[0]);
                    int year = Integer.parseInt(parts[1]);
                    if (month < 1 || month > 12) {
                        return " Tháng không hợp lệ.";
                    }
                    if (year <= 0) {
                        return " Năm không hợp lệ.";
                    }
                    if (year > currentDate.getYear() || (year == currentDate.getYear() && month > currentDate.getMonthValue())) {
                        return " Tháng năm phải nhỏ hơn hoặc bằng tháng năm hiện tại.";
                    }
                    break;
                }
                case 10: {
                    String[] parts = dobText.split("/");
                    if (parts.length != 3) {
                        return " Định dạng ngày/tháng/năm không hợp lệ.";
                    }
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    if (year <= 0) {
                        return " Năm không hợp lệ.";
                    }
                    if (month < 1 || month > 12) {
                        return " Tháng không hợp lệ.";
                    }
                    if (day < 1 || day > 31) {
                        return " Ngày không hợp lệ.";
                    }
                    // Kiểm tra ngày hợp lệ sử dụng LocalDate
                    LocalDate date = LocalDate.of(year, month, day);
                    if (date.isAfter(currentDate)) {
                        return " Ngày phải nhỏ hơn hoặc bằng ngày hiện tại.";
                    }
                    break;
                }
                default:
                    return " Định dạng không được hỗ trợ.";
            }
        } catch (NumberFormatException e) {
            return " Giá trị nhập vào không hợp lệ.";
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            return " Ngày không hợp lệ.";
        }
        return "";
    }

    public static String subjectKey(String gender, String ethnicity, String birthDate, String fullName) {
        StringBuilder subjectKey = new StringBuilder();
        if (gender != null && !gender.isEmpty()) {
            String lastFourChars = birthDate.substring(Math.max(0, birthDate.length() - 4));
            subjectKey.append(lastFourChars);
        }
        if (ethnicity != null && !ethnicity.isEmpty()) {
            subjectKey.append(gender);
        }
        if (birthDate != null && !birthDate.isEmpty()) {
            subjectKey.append(ethnicity);
        }
        if (fullName != null && !fullName.isEmpty()) {
            subjectKey.append(fullName);
        }
        return DataUtils.removeAllSpaces(subjectKey.toString()).toUpperCase();
    }

    public static String toLowerExceptA1A2(String text) {
        String lowerText = text.toLowerCase();
        if (lowerText.endsWith("a1")) {
            lowerText = lowerText.substring(0, lowerText.length() - 2) + "A1";
        } else if (lowerText.endsWith("a2")) {
            lowerText = lowerText.substring(0, lowerText.length() - 2) + "A2";
        }
        return lowerText;
    }

    /* ham cat chuoi
       text: gia tri truyen vao
       length: muon cat o vi tri nao
    */
    public static String substringText(String text, int length) {
        if (isNullOrEmpty(text)) {
            return text;
        }

        if (text.length() <= length) {
            return text;
        }

        text = text.substring(0, text.length() - 2);
        return text;
    }

    public static HttpHeaders getApiRequestAnnotationValue(Class<?> clazz, String methodName) {
        String[] info = new String[2];
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                if (method.isAnnotationPresent(ApiRequestInfo.class)) {
                    ApiRequestInfo annotation = method.getAnnotation(ApiRequestInfo.class);
                    info[0] = annotation.apiName();
                    info[1] = annotation.apiAction();
                }
            }
        }
        if (info[0] != null && info[1] != null) {
            return createApiRequestLogHeaders(info[0], info[1]);
        }
        return null;
    }

    /**
     * Tạo org.springframework.http.HttpHeaders gán vào response phục vụ việc ghi Log Request
     *
     * @param apiName - Tên danh mục của API
     * @param apiAction - Tên hành động của API
     * @return org.springframework.http.HttpHeaders
     */
    public static HttpHeaders createApiRequestLogHeaders(String apiName, String apiAction) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("apiName", apiName);
        headers.add("apiAction", apiAction);
        return headers;
    }

    public static boolean isNullOrEmpty(Object object) {
        if (object != null){
            if (object instanceof String && ((String) object).isEmpty()){
                return true;
            }else {
                return false;
            }
        }else {
            return true;
        }
    }

    public static String removeTrailingZeros(String str) {
        try {
            BigDecimal number = new BigDecimal(str);
            return number.stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            return str;
        }
    }
}
