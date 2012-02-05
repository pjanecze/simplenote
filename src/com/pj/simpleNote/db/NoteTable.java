package com.pj.simpleNote.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteTable {

	private static final long CACHE_TIME = 1000 * 3600 * 24 * 30;

	private static final String TABLE = "notes";

	private static final String F_ID = "note_id";
	
	private static final String F_TITLE = "title";

	private static final String F_CONTENT = "content";

	private static final String F_CREATE_DATE = "create_date";

	private static final String F_MODIFICATION_DATE = "modification_date";


	public static void create(final SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE;
		sql += " ( " + F_ID + " INTEGER PRIMARY KEY";
		sql += ", " + F_TITLE + " TEXT";
		sql += ", " + F_CONTENT + " TEXT";
		sql += ", " + F_CREATE_DATE + " LONG";
		sql += ", " + F_MODIFICATION_DATE + " LONG";
		sql += " ) ";
		db.execSQL(sql);
	}

	public static void update(SQLiteDatabase db, Note uc) {
		if (uc.id <0) {
			return;
		}

		final ContentValues vals = new ContentValues();
		vals.put(F_ID, uc.id);
		vals.put(F_TITLE, uc.title);
		vals.put(F_CONTENT, uc.content);
		vals.put(F_CREATE_DATE, uc.createDate);
		vals.put(F_MODIFICATION_DATE, uc.modificationDate);
		db.replace(TABLE, "", vals);
	}

	public static Note get(SQLiteDatabase db, int noteId) {
		if (noteId <0) {
			return null;
		}

		Note out = null;

		final String[] cols = new String[] { F_TITLE, F_CONTENT, F_CREATE_DATE, F_MODIFICATION_DATE};
		final Cursor c = db.query(TABLE, cols, F_ID + "=" + noteId,
				null, null, null, null);

		while (c.moveToNext()) {
			out = new Note();
			out.id = noteId;
			out.title = c.getString(c.getColumnIndex(F_TITLE));
			out.content = c.getString(c.getColumnIndex(F_CONTENT));
			out.createDate = c.getInt(c.getColumnIndex(F_CREATE_DATE));
			out.modificationDate = c.getInt(c.getColumnIndex(F_MODIFICATION_DATE));
		}
		c.close();

		return out;
	}
	
	public static ArrayList<Note> getAll(SQLiteDatabase db) {
		ArrayList<Note> notes = new ArrayList<Note>();
		Cursor c = db.query(TABLE, null, null, null, null, null, F_MODIFICATION_DATE + " DESC");
		while(c.moveToNext()) {
			Note out = new Note();
			out.id = c.getInt(c.getColumnIndex(F_ID));
			out.title = c.getString(c.getColumnIndex(F_TITLE));
			out.content = c.getString(c.getColumnIndex(F_CONTENT));
			out.createDate = c.getInt(c.getColumnIndex(F_CREATE_DATE));
			out.modificationDate = c.getInt(c.getColumnIndex(F_MODIFICATION_DATE));
			notes.add(out);
		}
		c.close();
		c = null;
		return notes;
	}

	public static void delete(SQLiteDatabase db, int noteId) {
		db.delete(TABLE, F_ID + "=" + noteId, null);
	}

	public static void add(SQLiteDatabase db, Note note) {
		ContentValues cv = new ContentValues();
		cv.put(F_CONTENT, note.content);
		cv.put(F_CREATE_DATE, note.createDate);
		cv.put(F_MODIFICATION_DATE, note.modificationDate);
		cv.put(F_TITLE, note.title);
		
		note.id = db.insert(TABLE, null, cv);
	}

}
