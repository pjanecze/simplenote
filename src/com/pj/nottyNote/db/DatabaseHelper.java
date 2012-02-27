package com.pj.nottyNote.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	/**
	 * The name of the database file.
	 */
	public static final String DATABASE_NAME = "simple_note.db";

	/**
	 * Contains the database version. Must be increased each time the schema is
	 * changed.
	 */
	private static final int DATABASE_VERSION = 1;

	private static final int ANALYZE_AND_VACUUM_INTERVAL = 3600000;

	/**
	 * Timestamp when ANALYZE and VACUUM commands were previously executed.
	 */
	private long lastAnalyzeVacuumTime = System.currentTimeMillis();

	public DatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		NoteTable.create(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);

		try {
			db.execSQL("PRAGMA cache_size = 2000");
			db.execSQL("PRAGMA synchronous=OFF");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		final SQLiteDatabase db = super.getWritableDatabase();
		analyzeAndVacuumIfExpired(db);
		return db;
	}

	/**
	 * Run ANALYZE and VACUUM commands if not executed for long time.
	 * 
	 * @param db
	 *            SQLiteDatabase reference.
	 */
	private void analyzeAndVacuumIfExpired(SQLiteDatabase db) {
		long now = System.currentTimeMillis();
		if (now - lastAnalyzeVacuumTime > ANALYZE_AND_VACUUM_INTERVAL) {
			lastAnalyzeVacuumTime = now;
			try {
				db.execSQL("ANALYZE");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				db.execSQL("VACUUM");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
