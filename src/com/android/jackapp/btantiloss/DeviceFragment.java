package com.android.jackapp.btantiloss;


import java.util.ArrayList;
import com.android.jackapp.btantiloss.bluetooth.BluetoothConstant;
import com.android.jackapp.btantiloss.bluetooth.BtDevices;
import com.android.jackapp.btantiloss.bluetooth.ble.BLESearchDevices;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceFragment extends Fragment {
	private static final String TAG = "DeviceFragment";
	private static final long ADAPT_BT_SEARCH_TIME = 20000;
	private static final String BACKGROUND_SEARCH_SERVICE = "com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService";
	protected static final String SEARCH_POLL_TIME = "5000";
	private Context context;
	private ListView devicefraglist;
	private DeviceFragmentListViewAdapter deviceFragmentListViewAdapter;
	private BLESearchDevices bleSearchDevices;
	private ProgressBar progressbar;
	//private TextView single_device_name;
	//private SettingDB mPD;
	private Intent intent_btservice;// = new Intent(getActivity(), com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService.class);
	private View device1_layout;
	private TextView device1_name;
	private CheckBox device1_switch;
	private View device2_layout;
	private TextView device2_name;
	private CheckBox device2_switch;
	private View device3_layout;
	private TextView device3_name;
	private CheckBox device3_switch;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_device, container, false);
		context = getActivity();
		intent_btservice = new Intent(context, com.android.jackapp.btantiloss.bluetooth.BluetoothBackgroundSearchService.class);
		//createManyDevicesSP();
		//pd
		//mPD = SettingDB.getInstance();
		//search imageview
		ImageView devicefrag_search = (ImageView)rootView.findViewById(R.id.devicefrag_search);
		devicefrag_search.setOnClickListener(searchDevicesListener);
		
		//my device
		//single_device_name = (TextView)rootView.findViewById(R.id.single_device_name);
		
		//listview
		devicefraglist = (ListView) rootView.findViewById(R.id.devicefraglist);
		deviceFragmentListViewAdapter = new DeviceFragmentListViewAdapter(context);
		devicefraglist.setAdapter(deviceFragmentListViewAdapter);
		devicefraglist.setOnItemClickListener(listViewItemOnClickListener );
		//search progress bar
		progressbar = (ProgressBar)rootView.findViewById(R.id.progress);
		
		//初始化最多三个可连接监控设备
		device1_layout = rootView.findViewById(R.id.device1_layout);
		device1_name = (TextView) rootView.findViewById(R.id.device1_name);
		device1_switch = (CheckBox) rootView.findViewById(R.id.device1_switch);
		
		device2_layout = rootView.findViewById(R.id.device2_layout);
		device2_name = (TextView) rootView.findViewById(R.id.device2_name);
		device2_switch = (CheckBox) rootView.findViewById(R.id.device2_switch);
		
		device3_layout = rootView.findViewById(R.id.device3_layout);
		device3_name = (TextView) rootView.findViewById(R.id.device3_name);
		device3_switch = (CheckBox) rootView.findViewById(R.id.device3_switch);
		
		device1_switch.setOnCheckedChangeListener(deviceSwitchListener);
		device2_switch.setOnCheckedChangeListener(deviceSwitchListener);
		device3_switch.setOnCheckedChangeListener(deviceSwitchListener);
		
		
		ImageView d1_del = (ImageView) rootView.findViewById(R.id.device1_del);
		d1_del.setOnClickListener(delDeviceSub);
		ImageView d2_del = (ImageView) rootView.findViewById(R.id.device2_del);
		d2_del.setOnClickListener(delDeviceSub);
		ImageView d3_del = (ImageView) rootView.findViewById(R.id.device3_del);
		d3_del.setOnClickListener(delDeviceSub);
		return rootView;
	}
	
	private OnClickListener delDeviceSub = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			SharedPreferences msp = context.getSharedPreferences("many_device_status", getActivity().MODE_WORLD_READABLE);
			Editor med = msp.edit();
			
			int id = view.getId();
			switch(id){
			case R.id.device1_del:
				device1_layout.setVisibility(View.GONE);
				device1_switch.setChecked(false);
				med.putInt("device1_visible", View.GONE);
				//med.putBoolean("device1_onoff", false);
				med.commit();
				break;
			case R.id.device2_del:
				device2_layout.setVisibility(View.GONE);
				device2_switch.setChecked(false);
				med.putInt("device2_visible", View.GONE);
				//med.putBoolean("device2_onoff", false);
				med.commit();
				break;
			case R.id.device3_del:
				device3_layout.setVisibility(View.GONE);
				device3_switch.setChecked(false);
				med.putInt("device3_visible", View.GONE);
				//med.putBoolean("device3_onoff", false);
				med.commit();
				break;
			}
		}
	};

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initViews();
		bleSearchDevices = new BLESearchDevices((Context)getActivity());
	}
	private void initViews() {
		//多设备
		SharedPreferences msp = getActivity().getSharedPreferences("many_device_status", getActivity().MODE_WORLD_READABLE);

		int device1_visible = msp.getInt("device1_visible", View.GONE);		
		device1_layout.setVisibility(device1_visible);
		if(device1_visible == View.VISIBLE) {
			device1_name.setText(msp.getString("device1_name", ""));			
		}
		boolean onoff1 = msp.getBoolean("device1_onoff", false);
		device1_switch.setChecked(onoff1);
		
		int device2_visible = msp.getInt("device2_visible", View.GONE);		
		device2_layout.setVisibility(device2_visible);	
		if(device2_visible == View.VISIBLE) {
			device2_name.setText(msp.getString("device2_name", ""));			
		}
		boolean onoff2 = msp.getBoolean("device2_onoff", false);
		device2_switch.setChecked(onoff2);

		int device3_visible = msp.getInt("device3_visible", View.GONE);		
		device3_layout.setVisibility(device3_visible);
		if(device3_visible == View.VISIBLE) {
			device3_name.setText(msp.getString("device3_name", ""));			
		}
		boolean onoff3 = msp.getBoolean("device3_onoff", false);
		device3_switch.setChecked(onoff3);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try {
			stopSearchSub();
			context.unregisterReceiver(hideProgressBar);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void updateDeviceOnoff(int id, boolean onoff, SharedPreferences msp) {
		Editor med = msp.edit();
		switch(id){
		case R.id.device1_switch:
			device1_switch.setChecked(onoff);
			med.putBoolean("device1_onoff", onoff);
			med.commit();
			break;
		case R.id.device2_switch:
			device2_switch.setChecked(onoff);
			med.putBoolean("device2_onoff", onoff);
			med.commit();
			break;
			
		case R.id.device3_switch:
			device3_switch.setChecked(onoff);
			med.putBoolean("device3_onoff", onoff);
			med.commit();
			break;	
		}
	}
	
	private void updateDeviceOnoffOnly(int id, boolean onoff, SharedPreferences msp) {
		Editor med = msp.edit();
		switch(id){
		case R.id.device1_switch:
			med.putBoolean("device1_onoff", onoff);
			med.commit();
			break;
		case R.id.device2_switch:
			med.putBoolean("device2_onoff", onoff);
			med.commit();
			break;
			
		case R.id.device3_switch:
			med.putBoolean("device3_onoff", onoff);
			med.commit();
			break;	
		}
	}
	
	private OnCheckedChangeListener deviceSwitchListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton arg0, boolean onoff) {
			int cb_id = arg0.getId();
			SharedPreferences msp = context.getSharedPreferences("many_device_status", Context.MODE_PRIVATE);
			updateDeviceOnoffOnly(cb_id, onoff, msp);
			boolean isrunning = isServiceRunning(BACKGROUND_SEARCH_SERVICE);
			if(onoff){
				if(isrunning){
					Log.e(TAG, "====onoff disable on");
					return;
				}
				Log.e(TAG, "====on===");
				updateDeviceOnoff(cb_id, onoff,msp);
				intent_btservice.putExtra("searchtime", SEARCH_POLL_TIME);
				getActivity().startService(intent_btservice);
			}else{
				boolean exceptDeviceOnoff = false;
				if(cb_id == R.id.device1_switch){
					exceptDeviceOnoff = msp.getBoolean("device2_onoff", false) ||
												 msp.getBoolean("device3_onoff", false);
				}else if(cb_id == R.id.device2_switch){
					exceptDeviceOnoff = msp.getBoolean("device1_onoff", false) ||
												 msp.getBoolean("device3_onoff", false);
				}else if(cb_id == R.id.device3_switch){
					exceptDeviceOnoff = msp.getBoolean("device1_onoff", false) ||
												 msp.getBoolean("device2_onoff", false);
				}
				if((!isrunning || exceptDeviceOnoff)){
					Log.e(TAG, "====onoff disable off");
					return;
				}
				Log.e(TAG, "====off===");
				updateDeviceOnoff(cb_id, onoff, msp);
				try {
					getActivity().stopService(intent_btservice);
				} catch (Exception e) {
				}
			}
		}
	};
	
	private OnClickListener searchDevicesListener = new OnClickListener() {
		public void onClick(View view) {
			startSearchSub();
		}
	};
	
	private LeScanCallback bleScanCallback = new LeScanCallback() {
		public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
			Log.e(TAG, "ble scan callback search devices :....rssi="+rssi);		
			getActivity().runOnUiThread(new Runnable() {
				private String PRODUCT_NAME = "SALT-Card";
				public void run() {
					if(device == null){
						Log.e(TAG, "errorxxxxxxxxxxxxx");
						return;
					}
					if(device.getName().equalsIgnoreCase(PRODUCT_NAME )){
						BtDevices btdevice = new BtDevices();
						btdevice.setAddress(device.getAddress());
						btdevice.setName(device.getName());
						btdevice.setRssi(Short.parseShort(Integer.toString(rssi)));
						
						deviceFragmentListViewAdapter.addDevice(btdevice);
						deviceFragmentListViewAdapter.notifyDataSetChanged();
					}
				}
			});
		}
	};
	private BroadcastReceiver hideProgressBar = new BroadcastReceiver() {
		public void onReceive(Context arg0, Intent arg1) {
			progressbar.setVisibility(View.GONE);
		}
	};
	
	
	protected void startSearchSub() {
		//ble	
		Log.e(TAG, "test 3");
		if(bleSearchDevices.init() == BluetoothConstant.OK){
			boolean ble_search_result = bleSearchDevices.search(progressbar,ADAPT_BT_SEARCH_TIME, bleScanCallback);
			Log.e(TAG, "test 1");
			if(!ble_search_result){
				return;//正在搜索时退出
			}
			Log.e(TAG, "test 2");
			deviceFragmentListViewAdapter.clear();
			progressbar.setVisibility(View.VISIBLE);
			IntentFilter filter = new IntentFilter();
			filter.addAction(UpdateUiBraodcastMsg.HIDE_PROGRESS_BAR);
			context.registerReceiver(hideProgressBar , filter);
		}else{
			//蓝牙初始化失败相关处理
			Toast.makeText(getActivity(), 
					getActivity().getResources().getString(R.string.bt_error_msg),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void stopSearchSub() {
		bleSearchDevices.stopsearch();
		progressbar.setVisibility(View.GONE);
	}
	
	private OnItemClickListener listViewItemOnClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			BtDevices device = deviceFragmentListViewAdapter.getDevice(arg2);
			String name = device.getName();
			String address = device.getAddress();
			
			showAdaptDeviceDialog(name,address);
		}
	};
	
	protected void showAdaptDeviceDialog(String name, String address) {
//		弹出一个确认适配设备的对话框
		final String mac_addr = address;
		final Dialog dg = new Dialog(getActivity(),R.style.FullHeightDialog);
		dg.setCancelable(false);
		dg.setContentView(R.layout.adapt_devices_dialog);
		
		final EditText et = (EditText) dg.findViewById(R.id.dfadapt_device_dlg_et);
		et.setText(name);
		
		Button dlg_btOK = (Button)dg.findViewById(R.id.dfAdapterAntiLoss_btOK);
		dlg_btOK.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				SharedPreferences msp = context.getSharedPreferences("many_device_status", Context.MODE_PRIVATE);				
				int d1_visible = msp.getInt("device1_visible", View.GONE);
				int d2_visible = msp.getInt("device2_visible", View.GONE);
				int d3_visible = msp.getInt("device3_visible", View.GONE);
				
				if(!et.getText().toString().equalsIgnoreCase("") || 
						(d1_visible == View.VISIBLE)&&(d2_visible == View.VISIBLE)&&(d3_visible == View.VISIBLE)){
					String name = et.getText().toString();
					//mPD.setSettingsInfoAlarmDeviceName(name);
					//mPD.setSettingsInfoAlarmDeviceAddress(mac_addr);
					Editor med = msp.edit();
					
					if(d1_visible == View.GONE){
						//更新到设备一
						med.putString("device1_name", name);
						med.putString("device1_mac", mac_addr);
						med.putBoolean("device1_onoff", false);
						med.putInt("device1_visible", View.VISIBLE);
						med.commit();
						
						device1_layout.setVisibility(View.VISIBLE);
						device1_name.setText(name);
						device1_switch.setChecked(false);
						
					}else if(d2_visible == View.GONE){
						//更新到设备二
						med.putString("device2_name", name);
						med.putString("device2_mac", mac_addr);
						med.putBoolean("device2_onoff", false);
						med.putInt("device2_visible", View.VISIBLE);
						med.commit();
						device2_layout.setVisibility(View.VISIBLE);
						device2_name.setText(name);
						device2_switch.setChecked(false);
					}else if(d3_visible == View.GONE){
						//更新到设备三
						med.putString("device3_name", name);
						med.putString("device3_mac", mac_addr);
						med.putBoolean("device3_onoff", false);
						med.putInt("device3_visible", View.VISIBLE);
						med.commit();
						
						device3_layout.setVisibility(View.VISIBLE);
						device3_name.setText(name);
						device3_switch.setChecked(false);
					}
					
					//
					Intent intent = new Intent();
					intent.setAction(UpdateUiBraodcastMsg.UPDATE_DEVICE_INFO_MSG);
					getActivity().sendBroadcast(intent);
					//更新当前的device name
					//single_device_name.setText(name);					
				}
				dg.cancel();
			}
		});
		
		Button dlg_btCancel = (Button)dg.findViewById(R.id.dfAdapterAntiLoss_btCancel);
		dlg_btCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				//Log.i(TAG, "show  adapt device dialog ...cancel");
				dg.cancel();
			}
		});
		
		dg.show();
	}

	
	
	/*	listview adapter*/
	private class DeviceFragmentListViewAdapter extends BaseAdapter{

			private ArrayList<BtDevices> devices;
			//private Context context;
			private LayoutInflater listContainer;
			
		
		    public DeviceFragmentListViewAdapter(Context context) {
				// TODO Auto-generated constructor stub
				//this.context = context;
				listContainer = LayoutInflater.from(context);
				devices = new ArrayList<BtDevices>();
			}

			public void addDevice(BtDevices device) {
				boolean isContain = false;
				
		        if(!devices.contains(device)) {
		        	for(int i= 0;i < devices.size();i++){
		        		if(devices.get(i).getAddress().equalsIgnoreCase(device.getAddress())){
		        			isContain = true;
		        		}
		        	}
		        	
		        	if(!isContain){
		        		devices.add(device);
		        	}
		        }
		    }
		    
		    public BtDevices getDevice(int position) {
		        return devices.get(position);
		    }

		    public void clear() {
		    	devices.clear();
		    }
		    
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return devices.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return devices.get(arg0);
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public View getView(int i, View view, ViewGroup viewGroup) {
				// TODO Auto-generated method stub
				ListDevicesView listDevicesView = null;
				if(view == null){
					listDevicesView = new ListDevicesView();
					view = listContainer.inflate(R.layout.adapt_devices_listview, null);
					listDevicesView.name = (TextView) view.findViewById(R.id.device_name);
					listDevicesView.address = (TextView) view.findViewById(R.id.device_address);
					listDevicesView.rssi = (TextView)view.findViewById(R.id.device_rssi);
					view.setTag(listDevicesView);
				}else{
					listDevicesView = (ListDevicesView)view.getTag();
				}
				
		        BtDevices device = devices.get(i);
		        final String deviceName = device.getName();
		        final String deviceAddr = device.getAddress();
		        final short deviceRssi = device.getRssi();
		        
		        listDevicesView.name.setText(deviceName);
		        listDevicesView.address.setText(deviceAddr);
		        listDevicesView.rssi.setText(deviceRssi+"");
		        
		        return view;
			}

			class ListDevicesView{
				TextView name;
				TextView address;
				TextView rssi;
			}
	}

	/*  检查后台service是否在运行*/

	private  boolean isServiceRunning(String className)  
	 {  
		try {
			
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
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	 }
	
	
	//创建多设备相关的存储信息
/*	private void createManyDevicesSP(){
		SharedPreferences msp = getActivity().getSharedPreferences("many_device_status", Context.MODE_PRIVATE);
		Editor med = msp.edit();

		med.putString("device1_name", "");
		med.putBoolean("device1_onoff", false);
		med.putInt("device1_visible", View.GONE);
		med.putString("device1_mac", getString(R.string.device_unknown));
		
		med.putString("device2_name", "");
		med.putBoolean("device2_onoff", false);
		med.putInt("device2_visible", View.GONE);
		med.putString("device2_mac", getString(R.string.device_unknown));
		
		med.putString("device3_name", "");
		med.putBoolean("device3_onoff", false);
		med.putInt("device3_visible", View.VISIBLE);
		med.putString("device3_mac", getString(R.string.device_unknown));
		
		med.commit();
	}
	*/
}
