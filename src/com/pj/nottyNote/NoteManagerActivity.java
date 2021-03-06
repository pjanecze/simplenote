package com.pj.nottyNote;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

import com.commonsware.cwac.tlv.*;
import com.pj.nottyNote.db.DatabaseHelper;
import com.pj.nottyNote.db.Note;
import com.pj.nottyNote.db.NoteTable;
import com.pj.nottyNote.utils.Tools;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class NoteManagerActivity extends Activity implements OnClickListener{

	public static final String EDIT_ACTION = "EDIT_ACTION_MANAGER";

	public static final String ADD_NOTE = "ADD_ACTION_MANAGER";
	
	TouchListView listView;
	
	CursorAdapter mAdapter;
	
	ArrayList<Note> checkedNotes = new ArrayList<Note>();
	
	View topMenu, selectorTopMenu, bottomMenu, selectorBottomMenu;
	
	ImageButton okCheckButton, remove, addNote;
	Button cancel;
	
	TextView checkLabel;
	
	Cursor cursor;
	
	DatabaseHelper mDbHelper;
	
	private boolean mChangeWidget = false;
	
	private int[] mWidgetId;
	
	private String mWidgetType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setBackgroundDrawableResource(R.drawable.blur_background);
		
		setContentView(R.layout.main_note_manager);
		
		mDbHelper = new DatabaseHelper(this);
		
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		remove = (ImageButton) findViewById(R.id.remove);
		remove.setOnClickListener(this);
		addNote = (ImageButton) findViewById(R.id.add_note);
		addNote.setOnClickListener(this);
		
		
		topMenu = findViewById(R.id.top_menu);
		selectorTopMenu = findViewById(R.id.selector_top_menu);
		
		bottomMenu = findViewById(R.id.bottom_menu);
		selectorBottomMenu = findViewById(R.id.selector_bottom_menu);
		
		
		listView = (TouchListView) findViewById(R.id.list_view);
		
		//registerForContextMenu(listView);
		
		mAdapter = new ManagerAdapter(this, null);
		
		listView.setAdapter(mAdapter);
		
		listView.setDropListener(onDrop);
		listView.setRemoveListener(onRemove);
		
		mWidgetId = getIntent().getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_ID);
		mWidgetType = getIntent().getStringExtra(AbstractWidgetProvider.WIDGET_TYPE);
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
			
			listView.draggable = true;
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
			
			listView.draggable = false;
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
	
	
	
	private class ManagerAdapter extends CursorAdapter implements OnClickListener, OnLongClickListener, OnMenuItemClickListener{

		
		Note selectedNote = null;
		
		public ManagerAdapter(Context context, Cursor c) {
			super(context, c);
		}
		
		private void onEditAction(Note note) {
			Intent intent = new Intent(NoteManagerActivity.this, NoteActivity.class);
			intent.setAction(NoteManagerActivity.EDIT_ACTION);
			intent.putExtra(AbstractWidgetProvider.EXTRA_ITEM, note.id);
			intent.putExtra("forResult", true);
			startActivityForResult(intent, 0);
		}
		
		
		@Override
		public Object getItem(int position) {
			Cursor mCursor = (Cursor) super.getItem(position);
			return Note.createFromCursor(mCursor);
		}




		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final View v = LayoutInflater.from(context).inflate(R.layout.list_item_manage, null);
			
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Note note = Note.createFromCursor(cursor);
			view.setTag(note);
			
			
			CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
			checkbox.setTag(note);
			checkbox.setOnCheckedChangeListener(null);
			Log.i("test", "refresh");
			if(checkedNotes.contains(note)) {
				checkbox.setChecked(true);
				view.setBackgroundResource(R.color.list_item_background);
			} else {
				checkbox.setChecked(false);
				view.setBackgroundResource(android.R.color.transparent);
				//((View)checkbox.getParent()).setBackgroundColor(R.color.list_item_background);
			}
			
			TextView modificationDate = (TextView) view.findViewById(R.id.modification_date);
			TextView createDate = (TextView) view.findViewById(R.id.create_date);
			TextView text = (TextView) view.findViewById(R.id.text);
		
			
			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Log.i("test", "isChecked" + isChecked);
					Note note = (Note) buttonView.getTag();
					if(note == null)
						return;
					
					View parent = (View) buttonView.getParent();
					
					if(!isChecked && checkedNotes.contains(note)) {
						
						checkedNotes.remove(note);
						updateSelectorMenu();
						parent.setBackgroundResource(R.color.list_item_background);

					} else if(!checkedNotes.contains(note)){
						Log.i("test", "add");
						checkedNotes.add(note);
						updateSelectorMenu();
						parent.setBackgroundResource(R.color.list_item_background_selected);

					}
					
					
				}
			});
			
			text.setText(note.content);
			
			if(note.modificationDate != note.createDate)
				modificationDate.setText(Tools.createDateText(note.modificationDate));
			
			createDate.setText(Tools.createDateText(note.createDate));
			
			View clickable = view.findViewById(R.id.clickable);
			clickable.setTag(note);
			clickable.setOnClickListener(this);
			clickable.setOnLongClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if(listView.draggable) {
				Note note = (Note) ((View)v.getParent()).getTag();
				if(note != null) {
					onEditAction(note);
				}
			}
		}

		@Override
		public boolean onLongClick(View v) {
			if(listView.draggable) {
				Note note = (Note) v.getTag();
				if(note != null) {
					selectedNote = note;
					PopupMenu popup = new PopupMenu(NoteManagerActivity.this, v);
		
				    // This activity implements OnMenuItemClickListener
				    popup.setOnMenuItemClickListener(this);
				    popup.inflate(R.menu.context_manage_list);
				    popup.show();
					
				}
			}
			return true;
		}




		@Override
		public boolean onMenuItemClick(MenuItem item) {
			ContextMenuInfo info = item.getMenuInfo();
			switch (item.getItemId()) {
			case R.id.edit:
				onEditAction(selectedNote);
				selectedNote = null;
				break;
			case R.id.delete:
				NoteTable.delete(mDbHelper.getWritableDatabase(), selectedNote);
				selectedNote = null;
				
				mChangeWidget = true;
				updateData();
				Toast.makeText(NoteManagerActivity.this, R.string.txt_note_deleted, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			return false;
		}
		
	}

//	static class ViewHolder {
//		CheckBox checkbox;
//		TextView modificationDate, createDate, text;
//	}

	
	



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 0 && resultCode == RESULT_OK) {
			updateData();
			mChangeWidget = true;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	private void unselectCheckboxes() {
		checkedNotes.clear();
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		if(v == okCheckButton) {
			unselectCheckboxes();
			
			updateSelectorMenu();
		} else if(v == cancel) {
			moveTaskToBack(true);
			finish();
		} else if(v == remove) {
			int i;
			int size = checkedNotes.size();
			for(i = 0; i < size; i++) {
				NoteTable.delete(mDbHelper.getWritableDatabase(), checkedNotes.get(i));
			}
			
			unselectCheckboxes();
			
			mChangeWidget = true;
			updateData();
			Toast.makeText(this, R.string.txt_notes_deleted, Toast.LENGTH_SHORT).show();
			updateSelectorMenu();
			
		} else if(v == addNote) {
			Intent intent = new Intent(NoteManagerActivity.this, NoteActivity.class);
			intent.setAction(NoteManagerActivity.ADD_NOTE);
			intent.putExtra("forResult", true);
			startActivityForResult(intent, 0);
		}
	}

	
	
	@Override
	protected void onPause() {
		if(mChangeWidget) {
			if(mWidgetType.equals(AbstractWidgetProvider.TYPE_LIST)) {
				Tools.updateWidget(this, SimpleNoteListWidgetProvider.class, mWidgetId);
			} else {
				Tools.updateWidget(this, SimpleNoteStackWidgetProvider.class, mWidgetId);
			}
			mChangeWidget = false;
		}
		super.onPause();
	}
	
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		super.onCreateContextMenu(menu, v, menuInfo);
//	    MenuInflater inflater = getMenuInflater();
//	    inflater.inflate(R.menu.context_manage_list, menu);
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	moveTaskToBack(true);
	    	finish();
	    	return true;
	    } else { 
	        return super.onKeyDown(keyCode, event);
	    }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cursor.close();
		mDbHelper.close();
		
	}
	
	
}
