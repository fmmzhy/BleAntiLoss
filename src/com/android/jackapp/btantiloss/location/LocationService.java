package com.android.jackapp.btantiloss.location;


import java.text.SimpleDateFormat;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {
	private static final String TAG = "LocationService";
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationClient mLocClient;
	private BDLocation location_last = null;
	private String loss_device_name = null;
	
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();

		loss_device_name = intent.getExtras().getString("loss_device_name");

		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		
		mLocClient.setLocOption(option);
		mLocClient.start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	public class MyLocationListenner implements BDLocationListener{
		public void onReceiveLocation(BDLocation location) {
			location_last = location;
		}

		public void onReceivePoi(BDLocation arg0) {
			
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mLocClient.stop();
		if((location_last == null) || (loss_device_name == null)){
			Log.e(TAG, "baidu location service not success");
			return;
		}
		double latitude = location_last.getLatitude();
		double longitude = location_last.getLongitude();
		
		//Toast.makeText(context, "latitude="+latitude+":"+"longitude="+longitude, Toast.LENGTH_SHORT).show();
		//获取系统时间和GPS坐标
	//	SettingDB mPD = SettingDB.getInstance();
		//systime
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String date = sDateFormat.format(new java.util.Date());  

/*		mPD.setSettingsInfoLossTime(date);
		mPD.setSettingsInfoLossLatitude(latitude);
		mPD.setSettingsInfoLossLongitude(longitude);*/
		Log.i(TAG, "get location info successful... latitude="+latitude+":longitude="+longitude);
		
/*		List<LossDeviceInfo> list = new ArrayList<LossDeviceInfo>();
		
		LossDeviceInfo info = new LossDeviceInfo(loss_device_name, date,latitude,longitude);
		list.add(info);*/

		SharedPreferences sp = getSharedPreferences("loss_device_record", Context.MODE_PRIVATE);
		int count = sp.getInt("record_cnt", 0);

		Editor ed = sp.edit();
		ed.putString("record"+count,loss_device_name+":::"+
		date+":::"+latitude+":::"+longitude);
		count = count+1;
		if(count >= 5) {
			count = 0;
		}
		ed.putInt("record_cnt", count);
		ed.commit();
	}
	
}
