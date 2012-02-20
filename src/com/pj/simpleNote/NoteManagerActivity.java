package com.pj.simpleNote;

import java.util.ArrayList;

import com.commonsware.cwac.tlv.TouchListView;
import com.pj.simpleNote.db.DatabaseHelper;
import com.pj.simpleNote.db.Note;
import com.pj.simpleNote.db.NoteTable;
import comp.pj.simpleNote.utils.Tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NoteManagerActivity extends Activity implements OnCheckedChangeListener, OnClickListener{

	TouchListView listView;
	
	ManagerAdapter mAdapter;
	
	ArrayList<Long> checkedIds = new ArrayList<Long>();
	
	View topMenu, selectorTopMenu, bottomMenu, selectorBottomMenu;
	
	ImageButton okCheckButton, remove, mark;
	Button cancel, save;
	
	TextView checkLabel;
	
	DatabaseHelper mDh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_note_manager);
		
		mDh = new DatabaseHelper(this);
		
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(this);
		remove = (ImageButton) findViewById(R.id.remove);
		remove.setOnClickListener(this);
		mark = (ImageButton) findViewById(R.id.mark);
		mark.setOnClickListener(this);
		
		topMenu = findViewById(R.id.top_menu);
		selectorTopMenu = findViewById(R.id.selector_top_menu);
		
		bottomMenu = findViewById(R.id.bottom_menu);
		selectorBottomMenu = findViewById(R.id.selector_bottom_menu);
		
		
		listView = (TouchListView) findViewById(R.id.list_view);
		
		mAdapter = new ManagerAdapter(this, this, mDh);
		listView.setAdapter(mAdapter);
		
		listView.setDropListener(onDrop);
		listView.setRemoveListener(onRemove);
		
		updateSelectorMenu();
	}
	
	private void updateSelectorMenu() {
		if(checkedIds.size() == 0) {
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
			
			checkLabel.setText(String.format(getText(R.string.txt_choosed).toString(), checkedIds.size()));
		}
	}
	
	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
				Note item=(Note) mAdapter.getItem(from);
				
				NoteTable.updatePosition(mDh.getWritableDatabase(), item.id, from, to);
				mAdapter.refresh();
		}
	};
	
	private TouchListView.RemoveListener onRemove=new TouchListView.RemoveListener() {
		@Override
		public void remove(int which) {
				//adapter.remove(adapter.getItem(which));
		}
	};
	
	private class ManagerAdapter extends BaseAdapter {

		private ArrayList<Note> mNotes;
		private DatabaseHelper dh;
		private Context mContext;
		private OnCheckedChangeListener mCheckedListener;
		
		public ManagerAdapter (Context context, OnCheckedChangeListener checkedListener, DatabaseHelper dh) {
			mContext = context;
			mCheckedListener = checkedListener;
			
			mNotes = new ArrayList<Note>();
			
			this.dh = dh;
			
			mNotes = NoteTable.getAll(dh.getReadableDatabase());
		}
		
		public void refresh() {
			
		}
		
		@Override
		public int getCount() {
			return mNotes.size();
		}

		@Override
		public Object getItem(int position) {
			return mNotes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mNotes.get(position).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Note note = (Note) getItem(position);
			
			ViewHolder holder;
			
			if(convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_manage, null);
				holder = new ViewHolder();
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
				
				holder.checkbox.setTag(note);
				
				holder.modificationDate = (TextView) convertView.findViewById(R.id.modification_date);
				holder.createDate = (TextView) convertView.findViewById(R.id.create_date);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			 
			holder.checkbox.setOnCheckedChangeListener(mCheckedListener);
			
			holder.text.setText(note.content + " " + note.position);
			
			holder.modificationDate.setText(Tools.createDateText(note.modificationDate));
			
			holder.createDate.setText(Tools.createDateText(note.createDate));
			
			return convertView;
		}
	}

	static class ViewHolder {
		CheckBox checkbox;
		TextView modificationDate, createDate, text;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Note note = (Note) buttonView.getTag();
		if(note == null)
			return;
		
		View parent = (View) buttonView.getParent();
		if(checkedIds.contains(note.id)) {
			checkedIds.remove(note.id);
			parent.setBackgroundResource(R.color.list_item_background);
		} else {
			checkedIds.add(note.id);
			parent.setBackgroundResource(R.color.list_item_background_selected);
		}
		
		updateSelectorMenu();
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
			
		} else if(v == save) {
			
		}
	}
}
