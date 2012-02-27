package com.pj.nottyNote.db;

import java.util.Date;

import android.database.Cursor;

public class Note {
	public static final String TYPE_STANDARD = "STANDARD";
	public static final String TYPE_TODO = "TODO";
	
	public long id;
	
	public String title;
	
	public String content;
	
	public long createDate;
	
	public long modificationDate;
	
	public String type;
	
	public int position;
	
	public Date getCreateDate() {
		return new Date(createDate);
	}
	
	public Date getModificationDate() {
		return new Date(modificationDate);
	}

	public static Note createFromCursor(Cursor c) {
		Note out = new Note();
		
		out.id = c.getInt(c.getColumnIndex(NoteTable.F_ID));
		out.title = c.getString(c.getColumnIndex(NoteTable.F_TITLE));
		out.content = c.getString(c.getColumnIndex(NoteTable.F_CONTENT));
		out.createDate = c.getLong(c.getColumnIndex(NoteTable.F_CREATE_DATE));
		out.modificationDate = c.getLong(c.getColumnIndex(NoteTable.F_MODIFICATION_DATE));
		out.type = c.getString(c.getColumnIndex(NoteTable.F_TYPE));
		out.position = c.getInt(c.getColumnIndex(NoteTable.F_POSITION));
		return out;
		
	}

	@Override
	public boolean equals(Object o) {
		
		if(!(o instanceof Note)) return false;
		
		Note note = (Note) o;

		return note.id == id;
	}
}
