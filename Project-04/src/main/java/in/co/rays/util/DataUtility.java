package in.co.rays.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for handling data type conversions and formatting.
 * Provides methods for String, Integer, Long, Date, and Timestamp handling.
 * 
 * @author 
 */
public class DataUtility {

    /** Application date format (dd-MM-yyyy) */
    public static final String APP_DATE_FORMAT = "dd-MM-yyyy";

    /** Application date-time format (dd-MM-yyyy HH:mm:ss) */
    public static final String APP_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

    /** Date formatter using APP_DATE_FORMAT */
    private static final SimpleDateFormat formatter = new SimpleDateFormat(APP_DATE_FORMAT);

    /** Date-Time formatter using APP_TIME_FORMAT */
    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat(APP_TIME_FORMAT);

    /**
     * Returns a trimmed string if not null, otherwise returns the original value.
     * 
     * @param val the input string
     * @return trimmed string or null
     */
    public static String getString(String val) {
        if (DataValidator.isNotNull(val)) {
            return val.trim();
        } else {
            return val;
        }
    }

    /**
     * Converts an Object to String. Returns empty string if object is null.
     * 
     * @param val the input object
     * @return string representation of object or empty string
     */
    public static String getStringData(Object val) {
        if (val != null) {
            return val.toString();
        } else {
            return "";
        }
    }

    /**
     * Converts String to int if valid integer, otherwise returns 0.
     * 
     * @param val the input string
     * @return integer value or 0
     */
    public static int getInt(String val) {
        if (DataValidator.isInteger(val)) {
            return Integer.parseInt(val);
        } else {
            return 0;
        }
    }

    /**
     * Converts String to long if valid, otherwise returns 0.
     * 
     * @param val the input string
     * @return long value or 0
     */
    public static long getLong(String val) {
        if (DataValidator.isLong(val)) {
            return Long.parseLong(val);
        } else {
            return 0;
        }
    }

    /**
     * Converts String to Date using APP_DATE_FORMAT.
     * 
     * @param val the input date string
     * @return Date object or null if parsing fails
     */
    public static Date getDate(String val) {
        Date date = null;
        try {
            date = formatter.parse(val);
        } catch (Exception e) {

        }
        return date;
    }

    /**
     * Converts Date to formatted String using APP_DATE_FORMAT.
     * 
     * @param date the input date
     * @return formatted string or empty string if error
     */
    public static String getDateString(Date date) {
        try {
            return formatter.format(date);
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Returns a modified Date by adding days (Not Implemented).
     * 
     * @param date the base date
     * @param day  number of days to add
     * @return modified date or null
     */
    public static Date getDate(Date date, int day) {
        return null;
    }

    /**
     * Converts String to Timestamp using APP_TIME_FORMAT.
     * 
     * @param val the input timestamp string
     * @return Timestamp object or null if parsing fails
     */
    public static Timestamp getTimestamp(String val) {

        Timestamp timeStamp = null;
        try {
            timeStamp = new Timestamp((timeFormatter.parse(val)).getTime());
        } catch (Exception e) {
            return null;
        }
        return timeStamp;
    }

    /**
     * Converts long (milliseconds) to Timestamp.
     * 
     * @param l the time in milliseconds
     * @return Timestamp object or null if error
     */
    public static Timestamp getTimestamp(long l) {

        Timestamp timeStamp = null;
        try {
            timeStamp = new Timestamp(l);
        } catch (Exception e) {
            return null;
        }
        return timeStamp;
    }

    /**
     * Returns the current system timestamp.
     * 
     * @return current Timestamp
     */
    public static Timestamp getCurrentTimestamp() {
        Timestamp timeStamp = null;
        try {
            timeStamp = new Timestamp(new Date().getTime());
        } catch (Exception e) {
        }
        return timeStamp;

    }

    /**
     * Converts Timestamp to long (milliseconds).
     * 
     * @param tm the timestamp object
     * @return milliseconds or 0 if error
     */
    public static long getTimestamp(Timestamp tm) {
        try {
            return tm.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Main method for testing utility methods.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Test getString
        System.out.println("getString Test:");
        System.out.println("Original: '  Hello World  ' -> Trimmed: '" + getString("  Hello World  ") + "'");
        System.out.println("Null input: " + getString(null));

        // Test getStringData
        System.out.println("\ngetStringData Test:");
        System.out.println("Object to String: " + getStringData(1234));
        System.out.println("Null Object: '" + getStringData(null) + "'");

        // Test getInt
        System.out.println("\ngetInt Test:");
        System.out.println("Valid Integer String: '124' -> " + getInt("124"));
        System.out.println("Invalid Integer String: 'abc' -> " + getInt("abc"));
        System.out.println("Null String: -> " + getInt(null));

        // Test getLong
        System.out.println("\ngetLong Test:");
        System.out.println("Valid Long String: '123456789' -> " + getLong("123456789"));
        System.out.println("Invalid Long String: 'abc' -> " + getLong("abc"));

        // Test getDate
        System.out.println("\ngetDate Test:");
        String dateStr = "10/15/2024";
        Date date = getDate(dateStr);
        System.out.println("String to Date: '" + dateStr + "' -> " + date);

        // Test getDateString
        System.out.println("\ngetDateString Test:");
        System.out.println("Date to String: '" + getDateString(new Date()) + "'");

        // Test getTimestamp (String)
        System.out.println("\ngetTimestamp(String) Test:");
        String timestampStr = "10/15/2024 10:30:45";
        Timestamp timestamp = getTimestamp(timestampStr);
        System.out.println("String to Timestamp: '" + timestampStr + "' -> " + timestamp);

        // Test getTimestamp (long)
        System.out.println("\ngetTimestamp(long) Test:");
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp ts = getTimestamp(currentTimeMillis);
        System.out.println("Current Time Millis to Timestamp: '" + currentTimeMillis + "' -> " + ts);

        // Test getCurrentTimestamp
        System.out.println("\ngetCurrentTimestamp Test:");
        Timestamp currentTimestamp = getCurrentTimestamp();
        System.out.println("Current Timestamp: " + currentTimestamp);

        // Test getTimestamp(Timestamp)
        System.out.println("\ngetTimestamp(Timestamp) Test:");
        System.out.println("Timestamp to long: " + getTimestamp(currentTimestamp));
    }
}
