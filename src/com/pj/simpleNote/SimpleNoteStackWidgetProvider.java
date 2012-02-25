package com.pj.simpleNote;

import comp.pj.simpleNote.utils.Tools;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class SimpleNoteStackWidgetProvider extends AbstractWidgetProvider {
	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("test", "arr: " + appWidgetIds[0]);
		for (int i = 0; i < appWidgetIds.length; i++) {
			Intent svcIntent = new Intent(ctxt, SimpleNoteStackWidgetService.class);

			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent
					.toUri(Intent.URI_INTENT_SCHEME)));

			RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
					R.layout.widget_stackview);

			widget.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, svcIntent);

			widget.setEmptyView(R.id.stack_view, R.id.empty_view);

			widget.setPendingIntentTemplate(R.id.stack_view,
					getListItemsIntent(ctxt, appWidgetIds, TYPE_STACK));

			widget.setOnClickPendingIntent(R.id.add_note,
					getAddButtonIntent(ctxt, appWidgetIds, TYPE_STACK));

			widget.setOnClickPendingIntent(R.id.manage_notes,
					getManageNotesIntent(ctxt, appWidgetIds, TYPE_STACK));

			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i],
					R.id.stack_view);
		}

		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}
}
