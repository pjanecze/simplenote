package com.pj.simpleNote;

import java.util.ArrayList;
import java.util.List;

import com.pj.simpleNote.db.DatabaseHelper;
import com.pj.simpleNote.db.Note;
import com.pj.simpleNote.db.NoteTable;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class SimpleNoteWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
	}

}

class ListViewRemoteViewsFactory implements RemoteViewsFactory {
	private List<Note> mNotes = new ArrayList<Note>();
	private Context mContext;
	private int mAppWidgetId;
	private DatabaseHelper mDbHelper;
	
	
	public ListViewRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}
	
	@Override
	public void onCreate() {
		mDbHelper = new DatabaseHelper(mContext);
		
	}
	
	@Override
	public int getCount() {
		return mNotes.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		// You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
		final Note note = mNotes.get(position);
		RemoteViews rv;
//		if(note.type.equals(Note.TYPE_TODO)) {
//			rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_todo);
//			rv.setTextViewText(R.id.content, note.content);
//
//		} else {
			rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item);
			rv.setTextViewText(R.id.content, note.content);
		//}

			
		
		
		rv.setOnClickFillInIntent(R.id.row_id, getRowIntent(position));
		//rv.setOnClickFillInIntent(R.id.remove, getRemoveIntent(position));
		
		
		return rv;
	}

	private Intent getRowIntent(int position) {
		Intent i=new Intent();
		Bundle extras=new Bundle();
		
		extras.putLong(SimpleNoteWidgetProvider.EXTRA_ITEM, mNotes.get(position).id);
		i.setAction(SimpleNoteWidgetProvider.EDIT_ACTION);
		i.putExtras(extras);
		return i;
	}
	
	private Intent getRemoveIntent(int position) {
		Intent i=new Intent();
		Bundle extras=new Bundle();
		
		extras.putLong(SimpleNoteWidgetProvider.EXTRA_ITEM, mNotes.get(position).id);
		i.setAction(SimpleNoteWidgetProvider.REMOVE_ACTION);
		i.putExtras(extras);
		return i;
	}
	
	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	

	@Override
	public void onDataSetChanged() {
		mNotes.clear();
		mNotes = NoteTable.getAll(mDbHelper.getWritableDatabase());
	}

	@Override
	public void onDestroy() {
		mNotes.clear();
	}
	
}