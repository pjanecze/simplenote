package com.pj.simpleNote;

import java.util.ArrayList;

import com.commonsware.cwac.tlv.TouchListView;
import com.pj.simpleNote.db.DatabaseHelper;
import com.pj.simpleNote.db.Note;
import com.pj.simpleNote.db.NoteTable;
import comp.pj.simpleNote.utils.Tools;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NoteManagerActivity extends Activity implements OnCheckedChangeListener, OnClickListener{

	public static final String EDIT_ACTION = "EDIT_ACTION_MANAGER";
	
	TouchListView listView;
	
	CursorAdapter mAdapter;
	
	ArrayList<Note> checkedNotes = new ArrayList<Note>();
	
	View topMenu, selectorTopMenu, bottomMenu, selectorBottomMenu;
	
	ImageButton okCheckButton, remove;
	Button cancel;
	
	TextView checkLabel;
	
	Cursor cursor;
	
	DatabaseHelper mDbHelper;
	
	private boolean mChangeWidget = false;
	
	private int mWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_note_manager);
		
		mDbHelper = new DatabaseHelper(this);
		
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		remove = (ImageButton) findViewById(R.id.remove);
		remove.setOnClickListener(this);
		
		topMenu = findViewById(R.id.top_menu);
		selectorTopMenu = findViewById(R.id.selector_top_menu);
		
		bottomMenu = findViewById(R.id.bottom_menu);
		selectorBottomMenu = findViewById(R.id.selector_bottom_menu);
		
		
		listView = (TouchListView) findViewById(R.id.list_view);
		
		
		
		mAdapter = new ManagerAdapter(this, null, this);
		
		listView.setAdapter(mAdapter);
		
		listView.setDropListener(onDrop);
		listView.setRemoveListener(onRemove);
		
		mWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
		
		updateData();
		updateSelectorMenu();
		
	}
	
	private void updateData() {
		cursor = NoteTable.getAllCursor(mDbHelper.getReadableDatabase());
		mAdapter.changeCursor(cursor);
	}
	
	private void updateSelectorMenu() {
		if(checkedNotes.size() == 0) {
			selectorTopMenu.setVisibility(View.GONE);
			topMenu.setVisibility(View.VISIBLE);
			
			selectorBottomMenu.setVisibility(View.GONE);
			bottomMenu.setVisibility(View.VISIBLE);
		} else {
			selectorTopMenu.setVisibility(View.VISIBLE);
			topMenu.setVisibility(View.GONE);
			
			selectorBottomMenu.setVisibility(View.VISIBLE);
			bottomMenu.setVisibility(View.GONE);
			
			if(okCheckButton == null) {
				okCheckButton = (ImageButton) findViewById(R.id.ok_check);
				okCheckButton.setOnClickListener(this);
			}
			if(checkLabel == null)
				checkLabel = (TextView) findViewById(R.id.check_label);
			
			checkLabel.setText(String.format(getText(R.string.txt_choosed).toString(), checkedNotes.size()));
		}
	}
	
	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
				Note item=(Note) mAdapter.getItem(from);
				
				NoteTable.updatePosition(mDbHelper.getWritableDatabase(), item.id, from, to);
				updateData();
				mChangeWidget = true;
		}
	};
	
	private TouchListView.RemoveListener onRemove=new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
				//adapter.remove(adapter.getItem(which));
			mChangeWidget = true;
		}
	};
	
	
	
	private class ManagerAdapter extends CursorAdapter implements OnClickListener, OnLongClickListener{
		
		OnCheckedChangeListener mCheckedListener;
		
		public ManagerAdapter(Context context, Cursor c, OnCheckedChangeListener listener) {
			super(context, c);
			mCheckedListener = listener;
		}
		

		
		
		@Override
		public Object getItem(int position) {
			Cursor mCursor = (Cursor) super.getItem(position);
			return Note.createFromCursor(mCursor);
		}




		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View v = LayoutInflater.from(context).inflate(R.layout.list_item_manage, null);
			View clickable = v.findViewById(R.id.clickable);
			clickable.setOnClickListener(this);
			clickable.setOnLongClickListener(this);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Note note = Note.createFromCursor(cursor);
			view.setTag(note);
			
			
			CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
			checkbox.setTag(note);
			
			TextView modificationDate = (TextView) view.findViewById(R.id.modification_date);
			TextView createDate = (TextView) view.findViewById(R.id.create_date);
			TextView text = (TextView) view.findViewById(R.id.text);

			
			checkbox.setOnCheckedChangeListener(mCheckedListener);
			
			text.setText(note.content);
			
			modificationDate.setText(Tools.createDateText(note.modificationDate));
			
			createDate.setText(Tools.createDateText(note.createDate));
		}

		@Override
		public void onClick(View v) {
			Note note = (Note) ((View)v.getParent()).getTag();
			if(note != null) {
				Intent intent = new Intent(NoteManagerActivity.this, NoteActivity.class);
				intent.setAction(NoteManagerActivity.EDIT_ACTION);
				intent.putExtra(SimpleNoteWidgetProvider.EXTRA_ITEM, note.id);
				startActivityForResult(intent, 0);
			}
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

//	static class ViewHolder {
//		CheckBox checkbox;
//		TextView modificationDate, createDate, text;
//	}

	
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Note note = (Note) buttonView.getTag();
		if(note == null)
			return;
		
		View parent = (View) buttonView.getParent();
		if(checkedNotes.contains(note)) {
			checkedNotes.remove(note);
			parent.setBackgroundResource(R.color.list_item_background);
		} else {
			checkedNotes.add(note);
			parent.setBackgroundResource(R.color.list_item_background_selected);
		}
		
		updateSelectorMenu();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK) {
			updateData();
			mChangeWidget = true;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		if(v == okCheckButton) {
			final int count = listView.getChildCount();
			for(int i =0; i < count; i++) {
				
				final LinearLayout child = (LinearLayout) listView.getChildAt(i);
				final CheckBox checkbox = (CheckBox) child.getChildAt(0);
				checkbox.setChecked(false);
			}
			
			updateSelectorMenu();
		} else if(v == cancel) {
			finish();
		} else if(v == remove) {
			for(Note note : checkedNotes) {
				NoteTable.delete(mDbHelper.getWritableDatabase(), note);
			}
			mChangeWidget = true;
			updateData();
			Toast.makeText(this, R.string.txt_notes_deleted, Toast.LENGTH_SHORT).show();
		} 
	}

	
	
	@Override
	protected void onPause() {
		if(mChangeWidget) {
			Intent intent = new Intent(this,SimpleNoteWidgetProvider.class);
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

			// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
			// since it seems the onUpdate() is only fired on that:
			int[] ids = {mWidgetId};
			Log.i("test", "id: " + mWidgetId);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
			sendBroadcast(intent);
			mChangeWidget = false;
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cursor.close();
		mDbHelper.close();
	}
	
	
}
