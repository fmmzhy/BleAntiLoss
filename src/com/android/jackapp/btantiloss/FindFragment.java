package com.android.jackapp.btantiloss;

import java.util.ArrayList;
import com.android.jackapp.btantiloss.customview.DistanceProgressBar;
import com.android.jackapp.btantiloss.db.SettingDB;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FindFragment extends Fragment {
	protected static final short SIGNAL3_VALUE = -50;
	protected static final short SIGNAL2_VALUE = -80;
	protected static final short SIGNAL1_VALUE = -100;
	protected static final String TAG = "FindFragment";
	private TextView single_device_name;
	//private ImageView single_imageview;
	private Button serch_btn;
	private ImageView local_imageView;
	private Resources resource;
	Context context;
	//private SettingDB mPD;
	private DistanceProgressBar setting_sig_show;
	private boolean isOpenFind;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	//	return super.onCreateView(inflater, container, savedInstanceState);
		Log.e(TAG, "find create ");
		View view = inflater.inflate(R.layout.fragment_find, container,false);
		context = getActivity();
		resource = getResources();
		//PD
		//mPD = SettingDB.getInstance();
		//device name
		single_device_name = (TextView)view.findViewById(R.id.single_device_name);
		//rssi progressbar
		setting_sig_show = (DistanceProgressBar)view.findViewById(R.id.setting_sig_show);

		serch_btn = (Button)view.findViewById(R.id.serch_btn);
		if(!isOpenFind){
			serch_btn.setBackground(resource.getDrawable(R.drawable.ic_looking));
			serch_btn.setText(resource.getString(R.string.serch_start));
		}
		serch_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startStopFindService();
			}
		});
		
		local_imageView = (ImageView)view.findViewById(R.id.local_imageView);
		local_imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(getActivity().getApplicationContext(), com.android.jackapp.btantiloss.BaiduTestActivity.class);
				startActivity(intent);
			}
		});
		
		return view;
	}
	
	protected void startStopFindService() {
		if(!isServiceRunning("com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService")){
			Toast.makeText(context, getResources().getString(R.string.antiloss_service_notrunnig), Toast.LENGTH_SHORT).show();
			return ;
		}
		if(!isOpenFind){
			serch_btn.setBackground(resource.getDrawable(R.drawable.ic_look_stop));
			serch_btn.setText(resource.getString(R.string.serch_stop));
		}else{
			serch_btn.setBackground(resource.getDrawable(R.drawable.ic_looking));
			serch_btn.setText(resource.getString(R.string.serch_start));
		}
		isOpenFind = !isOpenFind;
	}

	private BroadcastReceiver uiReceiver = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent intent) {
			//如果find未打开 ，刚返回，
			String action = intent.getAction();
			
			if(action.equalsIgnoreCase(UpdateUiBraodcastMsg.UPDATE_DEVICE_INFO_MSG)){
				//single_device_name.setText(mPD.getSettingsInfoAlarmDeviceName());
			}else if(action.equalsIgnoreCase(UpdateUiBraodcastMsg.DEVICE_FOUND_MSG/*FIND_DEVICE_RSSI_UPDATE*/)){
				if(!isOpenFind){
					return;
				}
				short rssi = Short.parseShort(intent.getStringExtra("rssi"));
				if(rssi > SIGNAL3_VALUE){
					//single_imageview.setBackground(getResources().getDrawable(R.drawable.ic_serch_single_3));
				}else if(rssi > SIGNAL2_VALUE){
					//single_imageview.setBackground(getResources().getDrawable(R.drawable.ic_serch_single_2));
				}else if(rssi > SIGNAL1_VALUE){
					//single_imageview.setBackground(getResources().getDrawable(R.drawable.ic_serch_single_1));
				}
				
				int progress;			
				int rssi_tmp;
				if(rssi>=-80){
					rssi_tmp = Integer.parseInt(Short.toString(rssi));
					rssi_tmp = Math.abs(rssi_tmp);//50--80
					rssi_tmp = rssi_tmp + 20;//70--100
					/*70--100*/
					if(rssi_tmp > 100){
						rssi_tmp = 100;//弱
					}else if(rssi_tmp < 70 ){
						rssi_tmp = 70;//强
					}
					rssi_tmp = 100 - rssi_tmp;//0--30;
					rssi_tmp = rssi_tmp + 70;
				}else if(rssi >=-100){
					rssi_tmp = Integer.parseInt(Short.toString(rssi));
					rssi_tmp = Math.abs(rssi_tmp);//80--100
					/*40--70*/
					rssi_tmp = 100 - rssi_tmp;//20--0;  强-->弱
					/*把20--0映射到40--70*/
					if(rssi_tmp<2){
						rssi_tmp = 2;/*0--20;*/
					}
					rssi_tmp = (int) (40+rssi_tmp*1.5);
					
				}else{
					/*0--40*/
					if(rssi <-120){
						rssi =-120;
					}
					rssi_tmp = Integer.parseInt(Short.toString(rssi));
					rssi_tmp = Math.abs(rssi_tmp);//100--120 //强－－>弱
					rssi_tmp = rssi_tmp -100;//0--20;
					/*把0--20映射到0--40*/
					rssi_tmp = rssi_tmp*2;
					rssi_tmp = 40-rssi_tmp;
					if(rssi_tmp == 0){
						rssi_tmp = 2;
					}
				}

				progress = rssi_tmp;
				setting_sig_show.setProgress(progress);
				
				single_device_name.setText(intent.getStringExtra("rssi"));
				
				//Toast.makeText(getActivity(), "信号强度："+Short.parseShort(intent.getStringExtra("rssi")), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	@Override
	public void onStart() {
		super.onStart();
		Log.e(TAG, "find onstart");
		IntentFilter filter = new IntentFilter();
		filter.addAction(UpdateUiBraodcastMsg.UPDATE_DEVICE_INFO_MSG);
		filter.addAction(UpdateUiBraodcastMsg.DEVICE_FOUND_MSG);
		context.registerReceiver(uiReceiver, filter);
		
		//single_device_name.setText(mPD.getSettingsInfoAlarmDeviceName());
	}
	
	public void onStop() {
		super.onStop();
		context.unregisterReceiver(uiReceiver);
	}
	

	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			
		}else{
			try {
				serch_btn.setBackground(resource.getDrawable(R.drawable.ic_looking));
				serch_btn.setText(resource.getString(R.string.serch_start));
				isOpenFind = false;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}
	
	/*  检查后台service是否在运行*/
	private  boolean isServiceRunning(String className)  
	 {  
	  ActivityManager myManager=(ActivityManager)getActivity().getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);  
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
