package com.android.jackapp.btantiloss.bluetooth;

import com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService.BleScanCallback;
import com.android.jackapp.btantiloss.bluetooth.ble.BLESearchDevices;

import android.content.Context;

public class BluetoothSearchService {
	Context contex;
	private BLESearchDevices bleSearchDevices;
	private BleScanCallback callbackWithStop;
	
	public BluetoothSearchService(Context context, BleScanCallback bleScanCallback) {
		this.contex = context;
		this.callbackWithStop = bleScanCallback;
	}
	
	public void search(long time){
		//ble
		bleSearchDevices = new BLESearchDevices(contex);
		if(BluetoothConstant.OK == bleSearchDevices.init()){
			//long time;
			bleSearchDevices.search(time, callbackWithStop);
		}
	}
	
	public void stop(){
		//ble
		bleSearchDevices.stop();
	}
}
