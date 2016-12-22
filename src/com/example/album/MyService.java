package com.example.album;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("00000","Myservice oncreate()");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("00000","Myservice ondestroy()");
	}

}
