package com.pj.simpleNote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class SimpleNoteWidgetProvider extends AppWidgetProvider {
	public static final String ADD_ACTION = "com.pj.simpleNote.ADD_ACTION";
	public static final String EDIT_ACTION = "com.pj.simpleNote.EDIT_ACTION";
	public static final String EXTRA_ITEM = "com.pj.simpleNote.EXTRA_ITEM";

	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("test", "arr: " + appWidgetIds[0]);
		for (int i = 0; i < appWidgetIds.length; i++) {
			Intent svcIntent = new Intent(ctxt, SimpleNoteWidgetService.class);

			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent
					.toUri(Intent.URI_INTENT_SCHEME)));

			RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
					R.layout.widget_listview);

			widget.setRemoteAdapter(appWidgetIds[i], R.id.list_view, svcIntent);

			widget.setEmptyView(R.id.list_view, R.id.empty_view);

			widget.setPendingIntentTemplate(R.id.list_view,
					getListItemsIntent(ctxt));

			widget.setOnClickPendingIntent(R.id.add_note,
					getAddButtonIntent(ctxt, appWidgetIds[i]));
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.list_view);
		}

		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}

	private PendingIntent getListItemsIntent(Context context) {
		Intent i = new Intent(context, NoteActivity.class);
		i.setAction(EDIT_ACTION);
		PendingIntent pi = PendingIntent.getActivity(context, 0,
				i, PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	private PendingIntent getAddButtonIntent(Context context, int widgetId) {
		Intent i = new Intent(context, NoteActivity.class);

		i.setAction(ADD_ACTION);
		i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

		PendingIntent pi = PendingIntent.getActivity(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		return pi;
	}
}
