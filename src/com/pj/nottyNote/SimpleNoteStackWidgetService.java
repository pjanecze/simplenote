package com.pj.nottyNote;


import android.content.Intent;
import android.widget.RemoteViewsService;


public class SimpleNoteStackWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new SimpleNoteRemoteViewsFactory(this.getApplicationContext(), intent, R.layout.stack_item);
	}

}
