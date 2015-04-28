package com.android.jackapp.btantiloss.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GetLocation{
	private LocationManager locationManager;
	private Context context;
	

	public GetLocation(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void gpsLocation(){
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
/*        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDERLocationManager.GPS_PROVIDER, 0,
                0, this);*/
	}

	public void networkLocation(){
		System.out.println("GetLocation.networkLocation()");
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER/*LocationManager.GPS_PROVIDER*/, 0,
                0, networkLocationListener);
	}
	
	private LocationListener networkLocationListener = new LocationListener() {
		
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
			System.out.println("GetLocation.onLocationChanged()::"+"latitude="+latitude+
					"----"+"longitude="+longitude);
			
			Toast.makeText(context, "latitude="+latitude+":"+"longitude="+longitude, Toast.LENGTH_SHORT).show();
		}
	};
	
	    public void stopLocation(){
	    	locationManager.removeUpdates(networkLocationListener);
	    }
}
