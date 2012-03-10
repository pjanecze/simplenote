package com.pj.nottyNote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DummyActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();
//		Intent intent = new Intent(this, InfoActivity.class);
//		startActivityForResult(intent, 0);
//		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
		
		
	}
	
	
	
}
