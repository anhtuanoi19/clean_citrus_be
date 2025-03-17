package vn.tuanla.clean_citrus.common;


import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static String CAMPAIGN_AIRDROP_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String BVD_DATETIME_FORMAT = "yyyyMMddHHmmss";
    public static String yyyyMMdd = "yyyy-MM-dd";
    public static String DDMMYYYY = "dd/MM/yyyy";
    public static String ONLY_DATE = "yyyy-MM-dd HH:mm:ss";
    public static String DDMMYYYYHHMMSS = "dd/MM/yyyy HH:mm:ss";
    public static String UTC7 = "UTC+07:00";
    public static final Date MINIMUM_DATE;

    static {
        try {
            MINIMUM_DATE = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-1900");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return format(dateTime, DDMMYYYYHHMMSS);
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static Date getCurrentDateGMT() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date dateGMT = cal.getTime();
        return dateGMT;
    }

    public static String dateToString(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        date.toInstant().atZone(ZoneId.of("Asia/Bangkok")).toLocalDateTime().format(formatter);
        return date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime().format(formatter);
    }

    public static String timestampToString(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timestamp.toInstant().atZone(ZoneId.of("Asia/Bangkok")).toLocalDateTime().format(formatter);
        return timestamp.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime().format(formatter);
    }

    public static Date dateWithoutTime(Date d) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(ONLY_DATE);
        return formatter.parse(formatter.format(d));
    }

    public static Timestamp getCurrentTimeStamp() {
        return Timestamp.valueOf(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
    }

    public static Date stringToDate(String dateString, String format) {
        if (dateString == null || dateString.trim().length() == 0) {
            return null;
        }
        DateFormat df = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            df.setLenient(false);
            date = df.parse(dateString.trim());

            return date;
        } catch (Exception e) {
            return null;
        }
    }

    public static String dateToString(Date date, String format) {
        if(date == null) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        String strDate = dateFormat.format(date);

        return strDate;
    }

    public static Boolean isInTime(Date openTime, Date closeTime, Date checkTime) {
        if (openTime == null || closeTime == null) {
            return true;
        }

        Calendar checkCal = Calendar.getInstance(), openCal = Calendar.getInstance(), closeCal = Calendar.getInstance();
        checkCal.setTime(checkTime);

        openCal.setTime(openTime);
        openCal.set(Calendar.YEAR, checkCal.get(Calendar.YEAR));
        openCal.set(Calendar.MONTH, checkCal.get(Calendar.MONTH));
        openCal.set(Calendar.DATE, checkCal.get(Calendar.DATE));

        closeCal.setTime(closeTime);
        closeCal.set(Calendar.YEAR, checkCal.get(Calendar.YEAR));
        closeCal.set(Calendar.MONTH, checkCal.get(Calendar.MONTH));
        closeCal.set(Calendar.DATE, checkCal.get(Calendar.DATE));

        if (openCal.getTime().after(closeCal.getTime())) {
            closeCal.add(Calendar.DATE, 1);
        }
        if (openCal.getTime().after(checkCal.getTime())) {
            checkCal.add(Calendar.DATE, 1);
        }
        return checkCal.getTime().after(openCal.getTime()) && checkCal.getTime().before(closeCal.getTime());
    }

    public static Date addDate(Date date, int addDate, Integer addHour, int addMinute) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, addDate);
        c.add(Calendar.HOUR, addHour == null ? 0 : addHour);
        c.add(Calendar.MINUTE, addMinute);
        return c.getTime();
    }

    public static long addTime(Date date, int addDate, Integer addHour, int addMinute) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, addDate);
        c.add(Calendar.HOUR, addHour == null ? 0 : addHour);
        c.add(Calendar.MINUTE, addMinute);
        return c.getTimeInMillis();
    }

    public static Date setTimeToDate(Date date, int hour, int minute, int second) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }

    public static Date truncateDate(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }

    public static String convertBVDDateTimeFormat() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(BVD_DATETIME_FORMAT);
        String strDate = formatter.format(date);

        return strDate;
    }

    public static Timestamp startOfDay(Timestamp time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        return new Timestamp(cal.getTimeInMillis());
    }

    public static Timestamp getCurrentTimeAdd7() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 7);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Boolean isInDate(Date checkDate, Date min, Date max) {
        checkDate = DateUtils.truncate(checkDate, Calendar.DATE);
        min = DateUtils.truncate(min, Calendar.DATE);
        max = DateUtils.truncate(max, Calendar.DATE);
        return (checkDate.compareTo(min) >= 0 && checkDate.compareTo(max) <= 0);
    }

    public static int getYearBwDate(Date date1, Date date2) {
        return getYear(date2) - getYear(date1);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Date removeTime(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }

    public static String getMonthOf(int minusMonth) {
        LocalDate now = LocalDate.now().withDayOfMonth(1).minusMonths(minusMonth);
        return now.toString();
    }

    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static String dateToStringDDMMYYY(Date date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DDMMYYYY);
        date.toInstant().atZone(ZoneId.of("Asia/Bangkok")).toLocalDateTime().format(formatter);
        return date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime().format(formatter);
    }

    public static Date convertToStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date convertToEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    // Trường hợp startDate > endDate => 1
    // Trường hợp startDate < endDate => -1
    // Trường hợp startDate = endDate => 0
    public static int compareDate(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        int result = startDate.compareTo(endDate);

        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    public static boolean checkDateInBetween(Date startDate, Date endDate, Date date) {

        if (startDate == null || endDate == null) {
            return false;
        }

        if (date.after(startDate) && date.before(endDate)) {
            return true;
        }
        return false;
    }

    public static boolean checkDateBefore(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        if (startDate.before(endDate)) {
            return true;
        }
        return false;
    }

    public static LocalDateTime dateSetTime(LocalDateTime current, int hour, int seconds, int milliseconds) {
        return current != null ? current.with(LocalTime.of(hour, seconds, milliseconds)) : null;
    }
    public static Date toDate(LocalDateTime current) {
        return current != null ? Date.from(current.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }

}
