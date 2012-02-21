package comp.pj.simpleNote.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class Tools {
	public static String createDateText(long time) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
		Log.i("test", "time: " + time);
		if(Tools.isSameDay(date, Calendar.getInstance())) {
			return Tools.timeToString(date.getTime(), "HH:mm");
		} else if(Tools.isSameYear(date, Calendar.getInstance())){
			return Tools.timeToString(date.getTime(), "HH:mm dd-MM");
		} else {
			return Tools.timeToString(date.getTime(), "HH:mm dd-MM-yy");
		}
	}
	
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}
	
	public static boolean isSameYear(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR));
	}
	
	public static String timeToString(long time, String format) {
		Date date = new Date(time);
		return Tools.timeToString(date, format);
	}
	
	public static String timeToString(Date time, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(time);
	}
}
