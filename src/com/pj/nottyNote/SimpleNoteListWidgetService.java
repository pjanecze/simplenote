package com.pj.nottyNote;

import android.content.Intent;


import android.widget.RemoteViewsService;


public class SimpleNoteListWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {

		return new SimpleNoteRemoteViewsFactory(this.getApplicationContext(), intent, R.layout.list_item);
	}

}

