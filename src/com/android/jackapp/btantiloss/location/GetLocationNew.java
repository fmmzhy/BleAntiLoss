package com.android.jackapp.btantiloss.location;

import java.text.SimpleDateFormat;

import com.android.jackapp.btantiloss.db.SettingDB;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GetLocationNew{
	protected static final String TAG = "GetLocationNew";
	private LocationManager locationManager;
	private Context context;
	

	public GetLocationNew(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void location(){
		Log.i(TAG, "location start");
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        
        //criteria
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        String provider = locationManager.getBestProvider(criteria, true);
        
        
		locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
	}
	
	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			
			//Toast.makeText(context, "latitude="+latitude+":"+"longitude="+longitude, Toast.LENGTH_SHORT).show();
			//获取系统时间和GPS坐标
			SettingDB mPD = SettingDB.getInstance();
			//systime
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
			String date = sDateFormat.format(new java.util.Date());  
			mPD.setSettingsInfoLossTime(date);
			mPD.setSettingsInfoLossLatitude(latitude);
			mPD.setSettingsInfoLossLongitude(longitude);
			Log.i(TAG, "get location info successful... latitude="+latitude+":longitude="+longitude);
			stopLocation();
		}
	};
	    public void stopLocation(){
	    	Log.i(TAG, "location stop");
	    	locationManager.removeUpdates(locationListener);
	    }
}
