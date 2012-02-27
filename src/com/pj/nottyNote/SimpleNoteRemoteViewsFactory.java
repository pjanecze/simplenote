package com.pj.nottyNote;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.pj.nottyNote.db.DatabaseHelper;
import com.pj.nottyNote.db.Note;
import com.pj.nottyNote.db.NoteTable;
import com.pj.nottyNote.utils.Tools;



public class SimpleNoteRemoteViewsFactory implements RemoteViewsFactory {
	private List<Note> mNotes = new ArrayList<Note>();
	private Context mContext;
	private int mAppWidgetId;
	private DatabaseHelper mDbHelper;
	private int mListItemId;
	
	public SimpleNoteRemoteViewsFactory(Context context, Intent intent, int listItemId) {
		mContext = context;
		mListItemId = listItemId;
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
		if(mNotes.size() ==0)
			return null;
		final Note note = mNotes.get(position);
		RemoteViews rv;

		rv = new RemoteViews(mContext.getPackageName(), mListItemId);
		rv.setTextViewText(R.id.content, note.content);

		rv.setTextViewText(R.id.modification_date, Tools.createDateText(note.modificationDate));
			
		
		
		rv.setOnClickFillInIntent(R.id.row_id, getRowIntent(position));
		//rv.setOnClickFillInIntent(R.id.remove, getRemoveIntent(position));
		
		
		return rv;
	}

	private Intent getRowIntent(int position) {
		Intent i=new Intent();
		Bundle extras=new Bundle();
		
		extras.putLong(AbstractWidgetProvider.EXTRA_ITEM, mNotes.get(position).id);
		i.setAction(AbstractWidgetProvider.EDIT_ACTION);
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