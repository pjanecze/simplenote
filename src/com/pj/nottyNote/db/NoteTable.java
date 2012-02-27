package com.pj.nottyNote.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteTable {

	private static final long CACHE_TIME = 1000 * 3600 * 24 * 30;

	public static final String TABLE = "notes";

	public static final String F_ID = "_id";
	
	public static final String F_TITLE = "title";

	public static final String F_CONTENT = "content";

	public static final String F_CREATE_DATE = "create_date";

	public static final String F_MODIFICATION_DATE = "modification_date";
	
	public static final String F_TYPE = "type";

	public static final String F_POSITION = "position";

	public static void create(final SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE;
		sql += " ( " + F_ID + " INTEGER PRIMARY KEY";
		sql += ", " + F_TITLE + " TEXT";
		sql += ", " + F_CONTENT + " TEXT";
		sql += ", " + F_CREATE_DATE + " INTEGER";
		sql += ", " + F_MODIFICATION_DATE + " INTEGER";
		sql += ", " + F_TYPE + " TEXT";
		sql += ", " + F_POSITION + " INTEGER"; 
		sql += " ) ";
		db.execSQL(sql);
	}

	public static void update(SQLiteDatabase db, Note uc) {
		if (uc.id <0) {
			return;
		}

		final ContentValues vals = new ContentValues();
		vals.put(F_TITLE, uc.title);
		vals.put(F_CONTENT, uc.content);
		vals.put(F_MODIFICATION_DATE, uc.modificationDate);
		vals.put(F_TYPE, uc.type);
		db.update(TABLE, vals, F_ID + " = " + uc.id, null);
	}
	
	public static void updatePosition(SQLiteDatabase db, long id, int from, int to) {
		if(id <0 || from == to) {
			return;
		}
		
		db.beginTransaction();
		try {
			final ContentValues vals = new ContentValues();
			vals.put(F_POSITION, to);
			db.update(TABLE, vals, F_ID + " = " + id, null);
			
			if(to >from) {
				db.execSQL("UPDATE " + TABLE + " SET " + F_POSITION + " = (" + F_POSITION + " -1) WHERE " +F_POSITION + " >" + from + " and " + F_POSITION + " <= " + to + " and " + F_ID + " <> " + id);
			} else {
				db.execSQL("UPDATE " + TABLE + " SET " + F_POSITION + " = (" + F_POSITION + " +1) WHERE " +F_POSITION + " <" + from + " and " + F_POSITION + " >= " + to + " and " + F_ID + " <> " + id);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
	}

	public static Note get(SQLiteDatabase db, long noteId) {
		if (noteId <0) {
			return null;
		}

		Note out = null;

		final String[] cols = new String[] { F_TITLE, F_CONTENT, F_CREATE_DATE, F_MODIFICATION_DATE, F_TYPE, F_POSITION};
		final Cursor c = db.query(TABLE, cols, F_ID + "=" + noteId,
				null, null, null, null);

		while (c.moveToNext()) {
			out = new Note();
			out.id = noteId;
			out.title = c.getString(c.getColumnIndex(F_TITLE));
			out.content = c.getString(c.getColumnIndex(F_CONTENT));
			out.createDate = c.getInt(c.getColumnIndex(F_CREATE_DATE));
			out.modificationDate = c.getInt(c.getColumnIndex(F_MODIFICATION_DATE));
			out.type = c.getString(c.getColumnIndex(F_TYPE));
			out.position = c.getInt(c.getColumnIndex(F_POSITION));
		}
		c.close();

		return out;
	}
	
	public static ArrayList<Note> getAll(SQLiteDatabase db) {
		ArrayList<Note> notes = new ArrayList<Note>();
		Cursor c = getAllCursor(db);
		while(c.moveToNext()) {
			Note out = new Note();
			out.id = c.getInt(c.getColumnIndex(F_ID));
			out.title = c.getString(c.getColumnIndex(F_TITLE));
			out.content = c.getString(c.getColumnIndex(F_CONTENT));
			out.createDate = c.getLong(c.getColumnIndex(F_CREATE_DATE));
			out.modificationDate = c.getLong(c.getColumnIndex(F_MODIFICATION_DATE));
			out.type = c.getString(c.getColumnIndex(F_TYPE));
			out.position = c.getInt(c.getColumnIndex(F_POSITION));
			notes.add(out);
		}
		c.close();
		c = null;
		return notes;
	}
	
	public static Cursor getAllCursor(SQLiteDatabase db) {
		return db.query(TABLE, null, null, null, null, null, F_POSITION + " ASC");
	}

	public static void delete(SQLiteDatabase db, Note note) {
		db.beginTransaction();
		try {
			db.delete(TABLE, F_ID + "=" + note.id, null);
			db.execSQL("UPDATE " + TABLE + " SET " + F_POSITION + " = (" + F_POSITION + " -1) WHERE " + F_POSITION + " > " + note.position);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public static void add(SQLiteDatabase db, Note note) {
		db.beginTransaction();
		try {
			db.execSQL("UPDATE " + TABLE + " SET " + F_POSITION + " = (" + F_POSITION + " +1)");
			
			ContentValues cv = new ContentValues();
			cv.put(F_CONTENT, note.content);
			cv.put(F_CREATE_DATE, note.createDate);
			cv.put(F_MODIFICATION_DATE, note.modificationDate);
			cv.put(F_TITLE, note.title);
			cv.put(F_TYPE, note.type);
			note.position = 0;
			cv.put(F_POSITION, note.position);
			note.id = db.insert(TABLE, null, cv);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
	}

	

}
