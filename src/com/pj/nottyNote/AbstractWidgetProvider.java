package com.pj.nottyNote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public abstract class AbstractWidgetProvider extends AppWidgetProvider{
	public static final String ADD_ACTION = "com.pj.simpleNote.ADD_ACTION";
	public static final String EDIT_ACTION = "com.pj.simpleNote.EDIT_ACTION";
	public static final String CHECKED_ACTION = "com.pj.simpleNote.CHECKED_ACTION";
	public static final String REMOVE_ACTION = "com.pj.simpleNote.REMOVE_ACTION";
	public static final String EXTRA_ITEM = "com.pj.simpleNote.EXTRA_ITEM";
	public static final String WIDGET_TYPE = "com.pj.simpleNote.WIDGET_TYPE";
	
	public static final String TYPE_STACK = "STACK";
	public static final String TYPE_LIST = "LIST";

	protected PendingIntent getListItemsIntent(Context context, int[] widgetId, String type) {
		Intent i = new Intent(context, NoteActivity.class);

		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		i.putExtra(WIDGET_TYPE, type);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	protected PendingIntent getAddButtonIntent(Context context, int[] widgetId, String type) {
		Intent i = new Intent(context, NoteActivity.class);

		i.setAction(ADD_ACTION);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		i.putExtra(WIDGET_TYPE, type);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		return pi;
	}

	protected PendingIntent getManageNotesIntent(Context context, int[] widgetId, String type) {
		Intent i = new Intent(context, NoteManagerActivity.class);

		i.setAction(ADD_ACTION);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		i.putExtra(WIDGET_TYPE, type);

		PendingIntent pi = PendingIntent.getActivity(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		return pi;

	}
}
