package com.android.jackapp.btantiloss;


import java.util.ArrayList;

import com.android.jackapp.btantiloss.bluetooth.BluetoothConstant;
import com.android.jackapp.btantiloss.bluetooth.ble.BLESearchDevices;
import com.android.jackapp.btantiloss.db.SettingDB;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
	private static final String TAG = "Antiloss MainActivity";
	private static final int REQUEST_BT_ENABLE = 10;
	private ViewPager mPager;
	private ScreenSlidePagerAdapter mPagerAdapter;
	private RadioGroupChangedListener radioGroupChangedListener;
	private RadioButton radio_device;
	private RadioButton radio_single;
	private RadioButton radio_setting;
	private RadioButton radio_help;
	private RadioGroup radioGroup;
	
	private static SettingDB mPD;
	private static MainActivity instance;
	
	public static MainActivity getInstance(){
		return instance;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        
        //radiogroup
        radioGroupChangedListener = new RadioGroupChangedListener();
        radioGroup = (RadioGroup) findViewById(R.id.main_tabwidget);
        radio_device = (RadioButton)findViewById(R.id.radio_device);
        radio_single = (RadioButton)findViewById(R.id.radio_single);
        radio_setting = (RadioButton)findViewById(R.id.radio_setting);
        radio_help = (RadioButton)findViewById(R.id.radio_help);
        radioGroup.setOnCheckedChangeListener(radioGroupChangedListener);
        //实例化
        instance = MainActivity.this;
        
		mPager = (ViewPager)findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setOnPageChangeListener(new pageChangeListener());
		mPager.setAdapter(mPagerAdapter);
		
    	//创建database
        SettingDB.initInstance(getApplicationContext());
        mPD = SettingDB.getInstance();
        mPD.initAll();
	}

	int[] id_array={R.id.radio_device,R.id.radio_single,R.id.radio_setting,R.id.radio_help};
	
	private class pageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int position) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			radioGroup.check(id_array[position]);
		}
		
	}
	
	
	public class RadioGroupChangedListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup rg, int checkedId) {
			//Log.i(TAG, "radio group changed...");
			// TODO Auto-generated method stub
			switch(checkedId){
			case R.id.radio_device:
				mPager.setCurrentItem(0);
				break;
			case R.id.radio_single:
				mPager.setCurrentItem(1);
				break;
			case R.id.radio_setting:
				mPager.setCurrentItem(2);
				break;
			case R.id.radio_help:
				mPager.setCurrentItem(3);
				break;
				
			}
		}
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//界面打开时启动
		Log.i(TAG, "MainActivity onStart");
		BLESearchDevices btProbe = new BLESearchDevices(this);
		int value = btProbe.init();
		switch(value){
/*		case BluetoothConstant.NOT_SUPPORT_BLE:
			//不支持ble功能
			AlertDialog.Builder builder = new AlertDialog.Builder(this);  
			builder.setMessage(getResources().getString(R.string.device_not_ble));  
			builder.setPositiveButton("退出",  
					new DialogInterface.OnClickListener() {  
						public void onClick(DialogInterface dialog, int whichButton) {		   
						   //××××操作代码
							
						}  
					});  

			builder.show(); 
			break;*/
			
		case BluetoothConstant.BT_DISABLE:
			//蓝牙没有打开
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);		
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPD.closeAll();
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//Log.i(TAG, "on Key Down back...");
				ExitDialog(this).show();
		        return true;
		}else{
			return super.onKeyDown(keyCode, event); 
		}
		
	}
	
	private Dialog ExitDialog(Context context) {  
		AlertDialog.Builder builder = new AlertDialog.Builder(context);  
		builder.setMessage(getString(R.string.if_exit_string)/*"您确定要退出吗？"*/);  
		builder.setPositiveButton(getString(R.string.exit_string),  
				new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int whichButton) {		   
					   //××××操作代码

						if(isServiceRunning("com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService")){						
							Intent intent = new Intent(getApplicationContext(), com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService.class);
							getApplicationContext().stopService(intent);
							SharedPreferences sp = getSharedPreferences("exit_onoff_flag", Context.MODE_PRIVATE);
							Editor ed = sp.edit();
							ed.putString("onoff", "on");
							ed.commit();
							
						}else{
							SharedPreferences sp = getSharedPreferences("exit_onoff_flag", Context.MODE_PRIVATE);
							Editor ed = sp.edit();
							ed.putString("onoff", "off");
							ed.commit();
						}
						finish();
					}  
				});  
		builder.setNegativeButton(getString(R.string.bg_running)/*"后台运行"*/,  
				new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int whichButton) {  
						backgroundRunning();
					}  
				});  
		return builder.create();  
	}
	
	private void backgroundRunning(){
		PackageManager pm = getPackageManager();
		ResolveInfo homeInfo =pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);

		ActivityInfo ai = homeInfo.activityInfo;
		Intent startIntent = new Intent(Intent.ACTION_MAIN);
		startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
		
		startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	        try {    
		            startActivity(startIntent);    
		        } catch (ActivityNotFoundException e) {    
		            Toast.makeText(this, "null",    
	                    Toast.LENGTH_SHORT).show();    
		        } catch (SecurityException e) {    
		            Toast.makeText(this, "null",    
		                    Toast.LENGTH_SHORT).show();     
		        }   
	}
	
	
	/*  检查后台service是否在运行*/

	private  boolean isServiceRunning(String className)  
	 {  
	  ActivityManager myManager=(ActivityManager)this.getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);  
	  ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(200);  
	  for(int i = 0 ; i<runningService.size();i++)  
	  {  
	   if(runningService.get(i).service.getClassName().toString().equals(className/*"com.android.BleWearApp.service.camera_shutter"*/))  
	   {  
	    return true;  
	   }  
	  }  
	  return false;  
	 }
}
