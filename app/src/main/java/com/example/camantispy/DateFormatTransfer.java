package com.example.camantispy;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;

public class DateFormatTransfer {
    public static String format(TrackerInterface trackerInterface){

        //DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        DateFormat format = new SimpleDateFormat("HH:mm:ss , yyyy-MM-dd", Locale.getDefault());
        return format.format(trackerInterface.getUsageStats().getLastTimeUsed());
    }
}
