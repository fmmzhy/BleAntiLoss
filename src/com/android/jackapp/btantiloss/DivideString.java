package com.android.jackapp.btantiloss;

public class DivideString {
	private String div = ":::";
	private String name;
	private String time;
	private double latitude;
	private double longitude;
	
	public DivideString(String in) {
		String[] tmp = in.split(div);
		if(tmp.length == 4){
			name = tmp[0];
			time = tmp[1];
			latitude = Double.parseDouble(tmp[2]);
			longitude = Double.parseDouble(tmp[3]);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public String getTime(){
		return time;
	}
	
	public double getLatitude(){
		return latitude;
	}
	public double getLongitude(){
		return longitude;
	}
}
