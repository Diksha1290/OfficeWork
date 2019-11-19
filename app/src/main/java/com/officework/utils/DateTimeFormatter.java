package com.officework.utils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by prerna.kapoor on 2/23/2016.
 */
public class DateTimeFormatter {

    public static String SYNC_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String PICKUP_DATE_FORMAT = "yyyy-MM-dd";
    //public static String PICKUP_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static String PICKUP_DATE_TIME_FORMAT1 = "yyyy-MM-dd hh:mm aa";
    public static String PICKUP_TIME_FORMAT = "HH:mm";
    public static String SHUTTLE_AVAILABLE_TIME_FORMAT_ORG = "MM/dd/yy HH:mm";
    public static String SHUTTLE_AVAILABLE_TIME_FORMAT = "hh:mm aa";
    public static String CREDIT_CARD_EXPIRY = "MM-yyyy";

    public static String formatDate(String existingFormat, String date, String targetFormat) {
        String parsedDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(existingFormat);
            Date PassedDate = sdf.parse(date);
            SimpleDateFormat sdf1 = new SimpleDateFormat(targetFormat);
            parsedDate = sdf1.format(PassedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static Date returnDate(String date, String Format) {
        Date PassedDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Format);
            PassedDate = sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PassedDate;
    }

    public static boolean compareDates(Date DateToCompare, Date DateComparedWith) {
        boolean isLatest = false;
        try {
            if (DateToCompare.compareTo(DateComparedWith) > 0)
                isLatest = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isLatest;
    }

    public static String getCurrentDate(String Format) {
        String date = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Format);
            date = sdf.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static String getTimeAfterFourHours() {
        Calendar cal = Calendar.getInstance();
        Date currDate = new Date();
        cal.setTime(currDate);
        cal.add(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY + 4);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumIntegerDigits(2);
        formatter.setMinimumIntegerDigits(2);
        return "" + hours + ":" + formatter.format(minutes);
    }

    public static String getTimeAtTwelve() {
        SimpleDateFormat sdf = new SimpleDateFormat(SHUTTLE_AVAILABLE_TIME_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.AM_PM, 0);
        return sdf.format(cal.getTime());
    }

    public static String[] getFutureYears(int NumberOfYears) {
        String[] arrYears = new String[NumberOfYears];
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        for (int i = 0; i < NumberOfYears; i++) {
            arrYears[i] = String.valueOf(year);
            year++;
        }

        return arrYears;
    }

    public static String getDateAfterParticularDays(String Format, int Days) {
        String date = "";
        Calendar cal = Calendar.getInstance();
        Date currDate = new Date();
        cal.setTime(currDate);
        cal.add(Calendar.DAY_OF_MONTH, Days);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(Format);
            date = sdf.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        return currYear;
    }

    public static int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        int currMonth = c.get(Calendar.MONTH);
        return currMonth;
    }

    public static int getCurrentDayofMonth() {
        Calendar c = Calendar.getInstance();
        int currDay = c.get(Calendar.DAY_OF_MONTH);
        return currDay;
    }

}
