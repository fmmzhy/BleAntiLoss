package com.android.jackapp.btantiloss.db;

import com.android.jackapp.btantiloss.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SettingDB extends SQLiteOpenHelper {

	private static final String TAG = "SettingDB";
	private static SettingDB instance = null;
	private SQLiteDatabase mDB;
	Context context;
	
	public SettingDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
		// TODO Auto-generated method stub
		paramSQLiteDatabase.execSQL("CREATE TABLE settingsinfo (_id INTEGER PRIMARY KEY,key TEXT NOT NULL,value TEXT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public static void initInstance(Context paramContext){ 
		if (instance == null)
			instance = new SettingDB(paramContext, "antisettings.db", null, 1);
	}
		  
    public static SettingDB getInstance()
    {
        return instance;
    }

	public void initAll() {
		// TODO Auto-generated method stub
		mDB = getWritableDatabase();
	}
	
	public void closeAll() {
		// TODO Auto-generated method stub
	    mDB.close();
	    mDB = null;
	}
	
	/*settinginfo表单*/
  private String getSettingsInfoValue(String paramString1, String paramString2){
	    Cursor localCursor = this.mDB.query(true, "settingsinfo", new String[] { "key", "value" }, "key=\"" + paramString1 + "\"", null, null, null, null, null);
	    int i = localCursor.getCount();
	    if (i == 0)
	      return paramString2;
	    if (i > 1)
	      Log.i(TAG, "SettingDB.getDevinfoValue()  !! Assume that there is at most one row !!");
	    localCursor.moveToFirst();
	    String str = localCursor.getString(localCursor.getColumnIndex("value"));
	    localCursor.close();
	    return str;
	  }
		  
	  private void setSettingsInfoValue(String paramString1, String paramString2){
	    ContentValues localContentValues = new ContentValues();
	    localContentValues.put("key", paramString1);
	    localContentValues.put("value", paramString2);
	    Cursor localCursor = this.mDB.query(true, "settingsinfo", new String[] { "key", "value" }, "key=\"" + paramString1 + "\"", null, null, null, null, null);
	    int i = localCursor.getCount();
	    localCursor.close();
	    if (i == 0)
	    {
	      this.mDB.insert("settingsinfo", null, localContentValues);
	      return;
	    }
	    if (i > 1)
	    	Log.i(TAG, "SettingDB.setDevinfoValue()  !! Assume that there is at most one row !!");
	    this.mDB.update("settingsinfo", localContentValues, "key=\"" + paramString1 + "\"", null);
	  }
		  
		  //switch on/off状态存储
		public void setSettingsInfoSwitch(boolean value) {
			// TODO Auto-generated method stub
			setSettingsInfoValue("switch", Boolean.toString(value));
		}
		
		public boolean getSettingsInfoSwitch() {
			// TODO Auto-generated method stub
			return Boolean.parseBoolean(getSettingsInfoValue("switch", Boolean.toString(false)));
		}
	
		
		//提醒方式 //0-->ring/1-->shake/2-->ring+shake/3->mute
		public void setSettingsInfoAlarmType(int value) {
			// TODO Auto-generated method stub
			setSettingsInfoValue("alarmtype", Integer.toString(value));
		}
		
		public int getSettingsInfoAlarmType() {
			// TODO Auto-generated method stub
			return Integer.parseInt(getSettingsInfoValue("alarmtype", Integer.toString(0)));
		}
		//提醒铃声设置
		public void setSettingsInfoAlarmAudio(String value) {
			setSettingsInfoValue("alarmAudio", value);
		}
		public String getSettingsInfoAlarmAudio() {
			return getSettingsInfoValue("alarmAudio",(context.getResources().getStringArray(R.array.ringtype))[0]);
		}
		//提醒时间
		public void setSettingsInfoAlarmTime(String value) {
			setSettingsInfoValue("alarmTime", value);
		}
		
		public String getSettingsInfoAlarmTime() {
			return getSettingsInfoValue("alarmTime",(context.getResources().getStringArray(R.array.ringtime))[0]);
		}
		
		//防丢设备name
		public void setSettingsInfoAlarmDeviceName(String name){
			setSettingsInfoValue("devicename", name);
		}
		
		public String getSettingsInfoAlarmDeviceName(){
			return getSettingsInfoValue("devicename", context.getResources().getString(R.string.device_unknown));
		}
		
		/*三个监控设备*/
		public void setSettingsInfoAlarmDevice1Name(String name){
			setSettingsInfoValue("devicename1", name);
		}
		
		public String getSettingsInfoAlarmDevice1Name(){
			return getSettingsInfoValue("devicename1", context.getResources().getString(R.string.device_unknown));
		}
		
		public void setSettingsInfoAlarmDevice2Name(String name){
			setSettingsInfoValue("devicename2", name);
		}
		
		public String getSettingsInfoAlarmDevice2Name(){
			return getSettingsInfoValue("devicename2", context.getResources().getString(R.string.device_unknown));
		}
		
		public void setSettingsInfoAlarmDevice3Name(String name){
			setSettingsInfoValue("devicename3", name);
		}
		
		public String getSettingsInfoAlarmDevice3Name(){
			return getSettingsInfoValue("devicename3", context.getResources().getString(R.string.device_unknown));
		}
		
		
		//防丢设备name mac address
		public void setSettingsInfoAlarmDeviceAddress(String address){
			setSettingsInfoValue("deviceaddress", address);
		}
		
		public String getSettingsInfoAlarmDeviceAddress(){
			return getSettingsInfoValue("deviceaddress", context.getResources().getString(R.string.device_unknown));
		}
		
		public void setSettingsInfoAlarmDevice1Address(String address){
			setSettingsInfoValue("deviceaddress1", address);
		}
		
		public String getSettingsInfoAlarmDevice1Address(){
			return getSettingsInfoValue("deviceaddress1", context.getResources().getString(R.string.device_unknown));
		}
		
		public void setSettingsInfoAlarmDevice2Address(String address){
			setSettingsInfoValue("deviceaddress2", address);
		}
		
		public String getSettingsInfoAlarmDevice2Address(){
			return getSettingsInfoValue("deviceaddress2", context.getResources().getString(R.string.device_unknown));
		}
		public void setSettingsInfoAlarmDevice3Address(String address){
			setSettingsInfoValue("deviceaddress3", address);
		}
		
		public String getSettingsInfoAlarmDevice3Address(){
			return getSettingsInfoValue("deviceaddress3", context.getResources().getString(R.string.device_unknown));
		}
		
		//判断蓝牙类型 0:classic bt 1: ble
		public void setSettingsInfoBTType(int value){
			setSettingsInfoValue("bttype", Integer.toString(value));
		}
		
		public int getSettingsInfoBTType(){
			return Integer.parseInt(getSettingsInfoValue("bttype", "1"));
		}
		
		//防丢器距离设置
		public void setSettingsInfoAntiLossDistance(String value){
			setSettingsInfoValue("antilossdistance", value);
		}
		
		public String getSettingsInfoAntiLossDistance(){
			return getSettingsInfoValue("antilossdistance", (context.getResources().getStringArray(R.array.alarmdistance))[1]);
		}
		
		//卡片丢失时的位置Latitude
		public void setSettingsInfoLossLatitude(double value){
			setSettingsInfoValue("losslatitude", Double.toString(value));
		}
		
		public double getSettingsInfoLossLatitude(){
			return Double.parseDouble(getSettingsInfoValue("losslatitude", "0.0"));
		}
		//卡片丢失时的位置Longitude
		public void setSettingsInfoLossLongitude(double value){
			setSettingsInfoValue("losslongitude", Double.toString(value));
		}
		
		public double getSettingsInfoLossLongitude(){
			return Double.parseDouble(getSettingsInfoValue("losslongitude", "0.0"));
		}
		
		//定位时间
		public void setSettingsInfoLossTime(String value){
			setSettingsInfoValue("losstime", value);
		}
		
		public String getSettingsInfoLossTime(){
			return getSettingsInfoValue("losstime", "none");
		}
}
