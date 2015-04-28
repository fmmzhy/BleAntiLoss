package com.android.jackapp.btantiloss.bluetooth;

public class BtDevices {
	private String name;
	private String address;
	private short rssi;
	
	
	public BtDevices() {
		// TODO Auto-generated constructor stub
	}
	
	public BtDevices(String name, String address,short rssi) {
		this.name = name;
		this.address = address;
		this.rssi = rssi;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the rssi
	 */
	public short getRssi() {
		return rssi;
	}

	/**
	 * @param rssi2 the rssi to set
	 */
	public void setRssi(short rssi2) {
		this.rssi = rssi2;
	}
	
}
