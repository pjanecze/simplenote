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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;

public class NoteActivity extends Activity implements OnClickListener{

	private static final int MAX_TITLE_LENGTH = 20;
	
	private Spinner mTypeView;
	private Button mCancel, mSave;
	private EditText mContent;
	private DatabaseHelper mDbHelper;
	
	private int mWidgetId; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_note);		
		
		mTypeView = (Spinner) findViewById(R.id.note_type);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.types_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(R.layout.simple_spinner_item);
	    mTypeView.setAdapter(adapter);
	    
	    mCancel = (Button) findViewById(R.id.cancel);
	    mCancel.setOnClickListener(this);
	    
	    mSave = (Button) findViewById(R.id.save);
	    mSave.setOnClickListener(this);
	    
	    mContent = (EditText) findViewById(R.id.note_edit);
	    
	    mDbHelper = new DatabaseHelper(this);
	    
	    Intent intent = getIntent();
	    mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
	}

	@Override
	public void onClick(View v) {
		if(v == mCancel) {
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
				long selectedType = mTypeView.getSelectedItemId();
				if(selectedType == 0)
					note.type = Note.TYPE_STANDARD;
				else if(selectedType == 1)
					note.type = Note.TYPE_TODO;
				if(note.content.length() > MAX_TITLE_LENGTH)
					note.title = note.content.substring(0, MAX_TITLE_LENGTH) + "...";
				else 
					note.title = note.content;
				NoteTable.add(mDbHelper.getWritableDatabase(), note);
				
				Intent intent = new Intent(this,SimpleNoteWidgetProvider.class);
				intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	
				// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
				// since it seems the onUpdate() is only fired on that:
				int[] ids = {mWidgetId};
				Log.i("test", "id: " + mWidgetId);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
				sendBroadcast(intent);
				
				Toast.makeText(this, getText(R.string.txt_note_saved), Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

}
