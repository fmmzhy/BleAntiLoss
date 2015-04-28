package com.android.jackapp.btantiloss.bluetooth;


import com.android.jackapp.btantiloss.R;
import com.android.jackapp.btantiloss.UpdateUiBraodcastMsg;
import com.android.jackapp.btantiloss.db.SettingDB;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class BluetoothBackgroundSearchService extends Service {
	protected static final String TAG = "BluetoothBackgroundSearchService";
	SettingDB mPD = SettingDB.getInstance();
	BluetoothSearchService bluetoothSearchService;
	long searchtime;
	private BTSearchThread btSearchThread;
	private boolean founddevice1;
	private boolean founddevice2;
	private boolean founddevice3;
	public boolean isStopThread;

	public boolean isOnceSearchStop;
	private BleScanCallback bleScanCallback;
	public int overthreshold_twice1 = 0;
	public int overthreshold_twice2 = 0;
	public int overthreshold_twice3 = 0;

	public class BleScanCallback implements LeScanCallback {
		public void onLeScan(BluetoothDevice device, int rssi, byte[] arg2) {
			String mac = device.getAddress();
			if(mPD == null){
				//mPD = SettingDB.getInstance();
				BluetoothBackgroundSearchService.this.stopSelf();
				Log.e(TAG, "xxxxxx 2");
				return;
			}
			//三个设备的处理
			SharedPreferences msp = getSharedPreferences("many_device_status", Context.MODE_PRIVATE);
			boolean device1_onoff = msp.getBoolean("device1_onoff", false);
			boolean device2_onoff = msp.getBoolean("device2_onoff", false);
			boolean device3_onoff = msp.getBoolean("device3_onoff", false);
			String device1_mac = msp.getString("device1_mac", getString(R.string.device_unknown));
			String device2_mac = msp.getString("device2_mac", getString(R.string.device_unknown));
			String device3_mac = msp.getString("device3_mac", getString(R.string.device_unknown));
			
			//if(mPD.getSettingsInfoAlarmDeviceAddress().equalsIgnoreCase(mac)){
			if(/*(device1_onoff || device2_onoff || device3_onoff) &&*/
					(device1_mac.equalsIgnoreCase(mac) || device2_mac.equalsIgnoreCase(mac)) || device3_mac.equalsIgnoreCase(mac)) {
				
				short rssi_threshold = -80;
				String distance = mPD.getSettingsInfoAntiLossDistance();
				if(distance.equalsIgnoreCase(getString(R.string.setting_close))){
					rssi_threshold = -75;
				}else if(distance.equalsIgnoreCase(getString(R.string.setting_middle))){
					rssi_threshold = -95;
				}else if(distance.equalsIgnoreCase(getString(R.string.setting_far))){
					rssi_threshold = -120;
				}
				//device 1
				if(device1_mac.equalsIgnoreCase(mac)&&device1_onoff) {
					founddevice1 = true;
				
					Log.e(TAG, "background find device 1 ...");
					
					Intent intent = new Intent();
					intent.putExtra("rssi", Integer.toString(rssi));
					intent.setAction(UpdateUiBraodcastMsg.DEVICE_FOUND_MSG);
					sendBroadcast(intent);
				
					if(rssi < rssi_threshold){
						overthreshold_twice1  = overthreshold_twice1+1;
						if(overthreshold_twice1>=3){
							overthreshold_twice1 = 0;
							founddevice1 = false;
							onStop();
						}
					}else{
						destroyBgBTDisconnectWindow(msp.getString("device1_name", ""));
						overthreshold_twice1 = 0;
					}
				}
				//device 2
				if(device2_mac.equalsIgnoreCase(mac)&&device2_onoff) {
					founddevice2 = true;
				
					Log.e(TAG, "background find device 2 ...");
					
					Intent intent = new Intent();
					intent.putExtra("rssi", Integer.toString(rssi));
					intent.setAction(UpdateUiBraodcastMsg.DEVICE_FOUND_MSG);
					sendBroadcast(intent);
				
					if(rssi < rssi_threshold){
						overthreshold_twice2   = overthreshold_twice2+1;
						if(overthreshold_twice2>=3){
							overthreshold_twice2 = 0;
							founddevice2 = false;
							onStop();
						}
					}else{
						destroyBgBTDisconnectWindow(msp.getString("device2_name", ""));
						overthreshold_twice2 = 0;
					}
				}
				//device 3
				if(device3_mac.equalsIgnoreCase(mac)&&device3_onoff) {
					founddevice3 = true;
				
					Log.e(TAG, "background find device 3 ...");
					
					Intent intent = new Intent();
					intent.putExtra("rssi", Integer.toString(rssi));
					intent.setAction(UpdateUiBraodcastMsg.DEVICE_FOUND_MSG);
					sendBroadcast(intent);
				
					if(rssi < rssi_threshold){
						overthreshold_twice3   = overthreshold_twice3+1;
						if(overthreshold_twice3>=3){
							overthreshold_twice3 = 0;
							founddevice3 = false;
							onStop();
						}
					}else{
						destroyBgBTDisconnectWindow(msp.getString("device3_name", ""));
						overthreshold_twice3  = 0;
					}
				}
			}
		}
		
		public void onStop(){
			Log.e(TAG, "ble search stop once...founddevice1="+founddevice1+
					" founddevice2="+founddevice2+" founddevice3="+founddevice3);
			isOnceSearchStop = true;
			SharedPreferences msp = getSharedPreferences("many_device_status", Context.MODE_PRIVATE);

			if(!founddevice1 && msp.getBoolean("device1_onoff", false)){
				Log.i(TAG, "device 1 is disconnected ...");				
				backgroundBTServiceSub(msp.getString("device1_name", ""));
			}else if(!founddevice2 && msp.getBoolean("device2_onoff", false)){
				Log.i(TAG, "device 2 is disconnected ...");				
				backgroundBTServiceSub(msp.getString("device2_name", ""));
			}else if(!founddevice3 && msp.getBoolean("device3_onoff", false)){
				Log.i(TAG, "device 3 is disconnected ...");				
				backgroundBTServiceSub(msp.getString("device3_name", ""));
			}
		}
	}
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "search service start");
		try {
			searchtime = Long.parseLong(intent.getStringExtra("searchtime"));
		} catch (Exception e) {
			// TODO: handle exception
			searchtime = 5000;
		}
		
		//ble scancall back
		bleScanCallback = new BleScanCallback();
		
		btSearchThread = new BTSearchThread();
		isStopThread = false;
		btSearchThread.start();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "serach service ondestroy...");
		try {
			//btSearchThread.stop();
			isStopThread = true;
		} catch (Exception e) {
			// TODO: handle exception
		}

		bluetoothSearchService.stop();
	}
	
	
	protected void backgroundBTServiceSub(String name) {
		// TODO Auto-generated method stub	
		Intent intent = new Intent(this, com.android.jackapp.btantiloss.BackgroundDisconnectDlg.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("device_name", name);
		startActivity(intent);
		
		//BluetoothBackgroundSearchService.this.stopSelf();
	}
	
	private void destroyBgBTDisconnectWindow(String name){
		Intent destroy_intent = new Intent("destroy.bgbt.disconnect.window");
		destroy_intent.putExtra("device_name", name);
		sendBroadcast(destroy_intent);
	}
	
	private class BTSearchThread extends Thread {
		public void run() {
			super.run();
			while(!isStopThread){
				founddevice1 = false;//开始搜索时设置为false，等找到设备后则设为true
				founddevice2 = false;
				founddevice3 = false;
				isOnceSearchStop = false;

				bluetoothSearchService =  new BluetoothSearchService(getApplicationContext(),bleScanCallback);	
				bluetoothSearchService.search(15000);
				//Log.e(TAG, "thread loop test 1");

				while(!isStopThread&& !isOnceSearchStop){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				//Log.e(TAG, "thread loop test 2");
				bluetoothSearchService.stop();
				//延时3秒
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					Log.e(TAG, "bt search service loop exception ...");
				}
			}
		}
	}
	
}
