package com.pj.simpleNote;

import com.pj.simpleNote.db.DatabaseHelper;
import com.pj.simpleNote.db.Note;
import com.pj.simpleNote.db.NoteTable;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NoteActivity extends Activity implements OnClickListener{

	private static final int MAX_TITLE_LENGTH = 20;
	
	private Spinner mTypeView;
	private Button mCancel, mSave;
	private ImageButton mRemove;
	private EditText mContent;
	private DatabaseHelper mDbHelper;
	
	private int mWidgetId; 
	
	private String mAction;
	
	private Note mCurrentNote;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		mAction = intent.getAction();
		

		setContentView(R.layout.main_note);
		
//		mTypeView = (Spinner) findViewById(R.id.note_type);
//	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//	            this, R.array.types_array, android.R.layout.simple_spinner_item);
//	    adapter.setDropDownViewResource(R.layout.simple_spinner_item);
//	    mTypeView.setAdapter(adapter);
	    
	    mCancel = (Button) findViewById(R.id.cancel);
	    mCancel.setOnClickListener(this);
	    
	    mSave = (Button) findViewById(R.id.save);
	    mSave.setOnClickListener(this);
	    
	    mRemove = (ImageButton) findViewById(R.id.remove);
	    mRemove.setOnClickListener(this);
	    
	    mContent = (EditText) findViewById(R.id.note_edit);
	    
	    mDbHelper = new DatabaseHelper(this);
	    
	    mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
	    
	    
	    
	    final TextView title = (TextView) findViewById(R.id.title);
	    if(mAction.equals(SimpleNoteWidgetProvider.ADD_ACTION)) {
	    	title.setText(R.string.txt_title_add);
	    	mRemove.setVisibility(View.GONE);
	    } else if(mAction.equals(SimpleNoteWidgetProvider.EDIT_ACTION) || mAction.equals(NoteManagerActivity.EDIT_ACTION)){
	    	title.setText(R.string.txt_title_edit);
	    	Log.i("test", "id: " + intent.getLongExtra(SimpleNoteWidgetProvider.EXTRA_ITEM, -1));
	    	mCurrentNote = NoteTable.get(mDbHelper.getReadableDatabase(), intent.getLongExtra(SimpleNoteWidgetProvider.EXTRA_ITEM, -1)); 
	    	mContent.setText(mCurrentNote.content);
	    }
	    mContent.setSelection(mContent.getText().length());
	    
	    
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	    
	}

	@Override
	public void onClick(View v) {
		if(v == mCancel) {
			if(mAction.equals(NoteManagerActivity.EDIT_ACTION)) {
				setResult(RESULT_CANCELED);
			}
			finish();
		}else if(v == mSave) {
			String content = mContent.getText().toString();
			if(content.trim().length() ==0) {
				Toast.makeText(this, getText(R.string.txt_note_content_cannot_be_empty), Toast.LENGTH_SHORT).show();
			} else {
				Note note = new Note();
				note.content = content;
				note.createDate = System.currentTimeMillis();
				note.modificationDate = System.currentTimeMillis();
//				long selectedType = mTypeView.getSelectedItemId();
//				Log.i("test", "selectedType = " + selectedType);
				note.type = Note.TYPE_STANDARD;
//				if(selectedType == 0)
//					note.type = Note.TYPE_STANDARD;
//				else if(selectedType == 1)
//					note.type = Note.TYPE_TODO;
				if(note.content.length() > MAX_TITLE_LENGTH)
					note.title = note.content.substring(0, MAX_TITLE_LENGTH) + "...";
				else 
					note.title = note.content;
				if(mCurrentNote == null)
					NoteTable.add(mDbHelper.getWritableDatabase(), note);
				else {
					note.id = mCurrentNote.id;
					NoteTable.update(mDbHelper.getWritableDatabase(), note);
				}
				if(mAction.equals(NoteManagerActivity.EDIT_ACTION)) {
					setResult(RESULT_OK);
				} else {
					Intent intent = new Intent(this,SimpleNoteWidgetProvider.class);
					intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		
					// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
					// since it seems the onUpdate() is only fired on that:
					int[] ids = {mWidgetId};
					Log.i("test", "id: " + mWidgetId);
					intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
					sendBroadcast(intent);
				}
				Toast.makeText(this, getText(R.string.txt_note_saved), Toast.LENGTH_SHORT).show();
				finish();
			}
		} else if(v == mRemove) {
			
			NoteTable.delete(mDbHelper.getWritableDatabase(), mCurrentNote);
			
			if(mAction.equals(NoteManagerActivity.EDIT_ACTION)) {
				setResult(RESULT_OK);
			} else {
				Intent intent = new Intent(this,SimpleNoteWidgetProvider.class);
				intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	
				// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
				// since it seems the onUpdate() is only fired on that:
				int[] ids = {mWidgetId};
				Log.i("test", "id: " + mWidgetId);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
				sendBroadcast(intent);
			}
			Toast.makeText(this, getText(R.string.txt_note_deleted), Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onPause() {
		mDbHelper.close();
		super.onPause();
		
		finish();
	}

	

}
