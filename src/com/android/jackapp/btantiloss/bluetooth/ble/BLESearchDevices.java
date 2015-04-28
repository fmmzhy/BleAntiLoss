package com.android.jackapp.btantiloss.bluetooth.ble;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import com.android.jackapp.btantiloss.UpdateUiBraodcastMsg;
import com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService.BleScanCallback;
import com.android.jackapp.btantiloss.bluetooth.BluetoothConstant;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

public class BLESearchDevices {
	private static final String TAG = "BLESearchDevices";
	Context context;
	private BluetoothAdapter mBluetoothAdapter;
	private TimerTask searchtask;
	private Timer searchtimer = new Timer(); 
	LeScanCallback callback;
	private BleScanCallback blecallback;
	private TimerTask searchtask1;
	private Timer searchtimer1 = new Timer();
	
	
	public BLESearchDevices() {
	}
	
	public BLESearchDevices(Context context) {
		this.context = context;
	}

	public int init() {
		int result = BluetoothConstant.FAIL;
		
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        	result = BluetoothConstant.NOT_SUPPORT_BLE;
        	return result;
        }
        
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
        	result = BluetoothConstant.NOT_SUPPORT_BLE;
        	return result;
        }
        
        if (!mBluetoothAdapter.isEnabled()) {
        	result = BluetoothConstant.BT_DISABLE;
        }else{
        	result = BluetoothConstant.OK;
        }

        return result;
	}

	public boolean search(ProgressBar search_progressbar, long searchtime,LeScanCallback leScanCallback) {
		final ProgressBar progressbar = search_progressbar;
		callback = leScanCallback;
		if(mBluetoothAdapter.isDiscovering()){
			return false;
		}
		searchtask1 = new TimerTask() {
			public void run() {
				//searchtask1.cancel();
				//searchtimer1.cancel();
				Log.e(TAG, "search devices timeout");
				mBluetoothAdapter.stopLeScan(callback);

				if(progressbar != null){
					Intent intent = new Intent(UpdateUiBraodcastMsg.HIDE_PROGRESS_BAR);
					context.sendBroadcast(intent);					
				}
			}
		};
		searchtimer1.schedule(searchtask1, searchtime);
		
		mBluetoothAdapter.startLeScan(leScanCallback);
		return true;
	}
	
	
	public boolean search(long searchtime,BleScanCallback bleScanCallback) {
		blecallback = bleScanCallback;
		//Log.e(TAG, "yyy 1");
		if(mBluetoothAdapter.isDiscovering()){
			Log.e(TAG, "yyy 2");
			return false;
		}
		//Log.e(TAG, "yyy 3");
	//	searchtask = new TimerTask() {
		//	public void run() {
/*				  Message message = new Message();  
			        message.what = 1;  
			        handler.sendMessage(message); */
	//		}
	//	};
	//	searchtimer.schedule(searchtask, searchtime);
		mBluetoothAdapter.startLeScan(blecallback);
		ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(5);
		stpe.schedule(search_timeout, searchtime, TimeUnit.MILLISECONDS);
		
		return true;
	}
	
	private Runnable search_timeout = new Runnable() {
		public void run() {
	    	Log.e(TAG, "search service background timeout 1");
			//searchtask.cancel();
			//searchtimer.cancel();
			mBluetoothAdapter.stopLeScan(blecallback);
			Log.e(TAG, "search service background timeout 2");
			blecallback.onStop();
		}
	}; 
	
	public void stop(){
		Log.e(TAG, "search stop...");
		try {
			mBluetoothAdapter.stopLeScan(blecallback);
		} catch (Exception e) {
			// TODO: handle exception
		}

		try {
		//	searchtimer.cancel();
		//	searchtask.cancel();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void stopsearch() {
		//Log.e(TAG, "search stop...=======");
/*		try {
			mBluetoothAdapter.stopLeScan(callback);
		} catch (Exception e) {
			// TODO: handle exception
		}
			
		try {
			searchtimer1.cancel();
			searchtask1.cancel();
		} catch (Exception e) {
			// TODO: handle exception
		}*/
	}


/*	Handler handler = new Handler() {  
	    public void handleMessage(Message msg) {  
	        // 要做的事情  
	    	ScheduledThreadPoolExecutor
	    	Log.e(TAG, "search service background timeout 1");
			searchtask.cancel();
			searchtimer.cancel();
			mBluetoothAdapter.stopLeScan(blecallback);
			Log.e(TAG, "search service background timeout 2");
			blecallback.onStop();
	    	
	        super.handleMessage(msg);  
	    }  
	};  */
	
}
