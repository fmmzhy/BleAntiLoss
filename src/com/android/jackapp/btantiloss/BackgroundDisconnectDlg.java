package com.android.jackapp.btantiloss;


import java.util.ArrayList;

import com.android.jackapp.btantiloss.audio.Audio;
import com.android.jackapp.btantiloss.db.SettingDB;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class BackgroundDisconnectDlg extends Activity {
	private static final String TAG = "BackgroundDisconnectDlg";
	int alarmType = 0;
	private Vibrator vibrator;
	private Handler shakeTimeHandler;
	Audio audio;
	private String loss_device_name;
	private Handler ringTimeHandler;
	private Handler ringShakeTimeHandler;
	private Intent intentlocation;
	
	private Runnable ringTimeRunnable = new Runnable() {
		public void run() {
			audio.stop();
		}
	};

	private Runnable ringShakeTimeRunnable = new Runnable() {
		public void run() {
			audio.stop();
			vibrator.cancel();
		}
	};
	
	private BroadcastReceiver destroyMyself = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getExtras().getString("device_name").equalsIgnoreCase(loss_device_name)){
				finish();
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.background_disconnect_dialog);
		//mPd
		SettingDB mPD = SettingDB.getInstance();
		if(mPD == null){
			Log.i(TAG, "no DB ");
		//	mPD = SettingDB.getInstance();
			finish();
			return;
		}
		
		loss_device_name = getIntent().getExtras().getString("device_name");
		if(loss_device_name == null) {
			finish();
			return;
		}
		TextView device_disconnect_TextView = (TextView) findViewById(R.id.device_disconnect_TextViewid);
		String str_tmp = String.format(getResources().getString(R.string.device_lost_one), loss_device_name/*mPD.getSettingsInfoAlarmDeviceName()*/);
		device_disconnect_TextView.setText(str_tmp);

		audio = new Audio(this);
		//send disconnect broadcast
		Intent intent = new Intent();
		intent.setAction(UpdateUiBraodcastMsg.DEVICE_UNFOUND_MSG);
		sendBroadcast(intent);
		
		Button btOK = (Button) findViewById(R.id.background_disconnect_btOK);
		btOK.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
		//PlaySound
		alarmType = mPD.getSettingsInfoAlarmType();
		Log.i(TAG, "alarm type = "+ alarmType);
		if(alarmType == 0){
			String file = mPD.getSettingsInfoAlarmAudio();
			String ringtime = mPD.getSettingsInfoAlarmTime();
			
			ringTimeHandler = new Handler();
			ringTimeHandler.postDelayed(ringTimeRunnable , Long.parseLong(ringtime)*1000);
			audio.play(file);
		}else if(alarmType == 1){
			//shake
			String shaketime = mPD.getSettingsInfoAlarmTime();
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			long[] pattern = {500,1000};
			vibrator.vibrate(pattern, 0);//0:repeate从pattern的index0
			shakeTimeHandler = new Handler();
			shakeTimeHandler.postDelayed(shakeTimeRunnable, Long.parseLong(shaketime)*1000);
		}else if(alarmType == 2){
			//ring/shake
			String file = mPD.getSettingsInfoAlarmAudio();
			String ringtime = mPD.getSettingsInfoAlarmTime();
			
			ringShakeTimeHandler = new Handler();
			ringShakeTimeHandler.postDelayed(ringShakeTimeRunnable  , Long.parseLong(ringtime)*1000);
			audio.play(file);
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			long[] pattern = {500,1000};
			vibrator.vibrate(pattern, 0);//0:repeate从pattern的index0
		}else if(alarmType == 3){
			//mute
			
		}

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//注册receiver用于销毁自身
		IntentFilter intentfilter = new IntentFilter("destroy.bgbt.disconnect.window");
		registerReceiver(destroyMyself , intentfilter);

		
		Log.i(TAG, "location service start ");
		intentlocation = new Intent(this, com.android.jackapp.btantiloss.location.LocationService.class);
		if(isServiceRunning("com.android.jackapp.btantiloss.location.LocationService")){
			//stopService(intentlocation);
		}else{
			//判断网络情况
			if(isNetworkAvaliabe(this)){
				intentlocation.putExtra("loss_device_name", loss_device_name);
				startService(intentlocation);
				
				Handler locationhandler = new Handler();
				locationhandler.postDelayed(new Runnable() {
					public void run() {
						Log.i(TAG, "location service 15s timeout");
						stopService(intentlocation);
					}
				}, 15000);
			}
		}
		
		
	}
	
	private boolean isNetworkAvaliabe(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity == null){
			Log.e(TAG, "network is unaviable !");
			return false;
		}else{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if(info !=null){
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState()==NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Runnable shakeTimeRunnable = new Runnable() {
		public void run() {
			vibrator.cancel();
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(alarmType == 0){
				if(ringTimeHandler != null){
					ringTimeHandler.removeCallbacks(ringTimeRunnable);
				}
				if(audio != null){
					audio.stop();
				}
		}else if(alarmType == 1){
			//shake
			if(shakeTimeHandler != null){
				shakeTimeHandler.removeCallbacks(shakeTimeRunnable);
			}
			if(vibrator != null){
				vibrator.cancel();
			}
		}else if(alarmType == 2){
			//ring/shake
			if(ringShakeTimeHandler != null){
				ringShakeTimeHandler.removeCallbacks(ringShakeTimeRunnable);
			}
			if(audio != null){
				audio.stop();
			}
			if(vibrator != null){
				vibrator.cancel();
			}
		}else if(alarmType == 3){
			//mute
		}
		
		try {
			stopService(intentlocation);
			unregisterReceiver(destroyMyself);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private  boolean isServiceRunning(String className)  
	 {  
		try {
			
	  ActivityManager myManager=(ActivityManager)getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);  
	  ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(200);  
	  for(int i = 0 ; i<runningService.size();i++)  
	  {  
	   if(runningService.get(i).service.getClassName().toString().equals(className/*"com.android.BleWearApp.service.camera_shutter"*/))  
	   {  
	    return true;  
	   }  
	  }  
	  return false;  
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	 }
	
}
