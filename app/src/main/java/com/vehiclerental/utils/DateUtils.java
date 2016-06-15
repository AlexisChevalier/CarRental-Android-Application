/**
 * CarRental
 *
 * This file provides date management abstraction methods
 * I am using UTC as common timezone
 * The dates are encoded using the ISO8601 format to be sent over the network
 */

package com.vehiclerental.utils;

import com.vehiclerental.R;
import com.vehiclerental.CarRentalApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    //Results of the comparison system
    public final static int DATE1_BEFORE_DATE2 = -1;
    public final static int DATE1_AFTER_DATE2 = 1;
    public final static int DATE1_EQUAL_DATE2 = 0;

    //Default timezone
    private final static TimeZone timeZone = TimeZone.getTimeZone("UTC");

    /**
     * Returns a Java calendar rounded to the current day
     * @return the calendar
     */
    public static Calendar getCurrentDate() {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTimeZone(timeZone);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * Converts an ISO8601 string to a properly formatted android date (dd/mm/yyyy)
     * @param iso8601String ISO8601 date string
     * @return the formatted date string
     */
    public static String getFormattedDateFromIso8601String(String iso8601String) {
        return android.text.format.DateFormat.format(CarRentalApplication.getAppContext().getString(R.string.display_date_format), getDateFromIso8601String(iso8601String)).toString();
    }

    /**
     * Converts an ISO8601 date string to a Java Date
     * @param string ISO8601 date string
     * @return Date
     */
    public static Date getDateFromIso8601String(String string) {
        try {
            return getIso8601DateFormat().parse(string);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Formats a date to an ISO8601 date string
     * @param date the date
     * @return ISO8601 date string
     */
    public static String getIso8601DateString(Calendar date) {
        return getIso8601DateFormat().format(date.getTime());
    }

    /**
     * Generates an ISO8601-compliant date formatter
     * @return the date formatter
     */
    private static DateFormat getIso8601DateFormat() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ", Locale.ENGLISH);
        df.setTimeZone(timeZone);
        return df;
    }

    /**
     * Converts an ISO8601 date string to a Java Calendar
     * @param string ISO8601 date string
     * @return Calendar
     */
    public static Calendar getCalendarFromIso8601String(String string) {
        try {
            Calendar cal = Calendar.getInstance(timeZone);
            cal.setTime(getIso8601DateFormat().parse(string));
            cal.setTimeZone(timeZone);
            return cal;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Compare two calendars without time comparison
     * @param cal1 first calendar
     * @param cal2 second calendar
     * @return DATE1_EQUAL_DATE2 if the dates are the same, DATE1_BEFORE_DATE2 if the date1 is before date2 and DATE1_AFTER_DATE2 otherwise
     */
    public static int compareCalendar(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return 0;
        }

        if (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
            return DATE1_EQUAL_DATE2;
        }

        if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)
                || cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)
                || cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)) {
            return DATE1_BEFORE_DATE2;
        }

        return DATE1_AFTER_DATE2;
    }
}
