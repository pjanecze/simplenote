package com.pj.simpleNote;

import java.util.ArrayList;
import java.util.List;

import com.pj.simpleNote.db.DatabaseHelper;
import com.pj.simpleNote.db.Note;
import com.pj.simpleNote.db.NoteTable;
import comp.pj.simpleNote.utils.Tools;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class SimpleNoteListWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.i("test", "amount: "); 
		return new SimpleNoteRemoteViewsFactory(this.getApplicationContext(), intent, R.layout.list_item);
	}

}

