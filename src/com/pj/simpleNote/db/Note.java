package com.pj.simpleNote.db;

import java.util.Date;

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
}
