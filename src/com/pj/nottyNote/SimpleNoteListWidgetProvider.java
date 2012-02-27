package com.pj.nottyNote;

import com.pj.nottyNote.utils.Tools;


import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class SimpleNoteListWidgetProvider extends AbstractWidgetProvider{

	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int i = 0; i < appWidgetIds.length; i++) {
			Intent svcIntent = new Intent(ctxt, SimpleNoteListWidgetService.class);

			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent
					.toUri(Intent.URI_INTENT_SCHEME)));

			RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
					R.layout.widget_listview);

			widget.setRemoteAdapter(appWidgetIds[i], R.id.list_view, svcIntent);

			widget.setEmptyView(R.id.list_view, R.id.empty_view);

			widget.setPendingIntentTemplate(R.id.list_view,
					getListItemsIntent(ctxt, appWidgetIds, TYPE_LIST));

			widget.setOnClickPendingIntent(R.id.add_note,
					getAddButtonIntent(ctxt, appWidgetIds, TYPE_LIST));

			widget.setOnClickPendingIntent(R.id.manage_notes,
					getManageNotesIntent(ctxt, appWidgetIds, TYPE_LIST));

			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i],
					R.id.list_view);
		}

		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}
}
