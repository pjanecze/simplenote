package comp.pj.simpleNote.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.pj.simpleNote.AbstractWidgetProvider;
import com.pj.simpleNote.NoteActivity;
import com.pj.simpleNote.NoteManagerActivity;
import com.pj.simpleNote.R;
import com.pj.simpleNote.SimpleNoteListWidgetProvider;
import com.pj.simpleNote.SimpleNoteStackWidgetProvider;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class Tools {
	
	public static void updateWidget(Context context, Class<?> cl, int[] widgetIds) {
		Intent intent;
		intent = new Intent(context, cl);
		
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,widgetIds);
		context.sendBroadcast(intent);
	}
	
	public static String createDateText(long time) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(time);
	
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

	public static void startMainLayoutAnimation(Activity activity, boolean open) {
		
		LinearLayout layout = (LinearLayout) activity.findViewById(R.id.main_layout);
		if(open)
			layout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.note_in));
		else
			layout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.note_out));
		
	}
	
}
