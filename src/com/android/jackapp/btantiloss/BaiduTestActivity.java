package com.android.jackapp.btantiloss;

import java.util.ArrayList;
import com.android.jackapp.btantiloss.db.SettingDB;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class BaiduTestActivity extends Activity {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;

	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton;
	boolean isFirstLoc = true;// 是否首次定位
	private TextView losstime_value;
	private EditText latitude;
	private EditText longitude;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.custom_location_record, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.location_record:
			showRecordDetails(findViewById(R.id.location_record));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}	
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		
		if(!isNetworkAvaliabe(this)){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);  
			builder.setMessage(getString(R.string.no_network));  
			builder.setPositiveButton(getString(R.string.exit_string),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							finish();
						}
					}
					);
			builder.setCancelable(false);
			builder.create().show();
			return;
		}
		/**/
		latitude = (EditText) findViewById(R.id.latitudeEt);
		longitude = (EditText) findViewById(R.id.longitudeEt);
		
		losstime_value = (TextView) findViewById(R.id.losstime_value);
		SettingDB mPD = SettingDB.getInstance();
		if(!mPD.getSettingsInfoLossTime().equalsIgnoreCase("none")){
			losstime_value.setText(mPD.getSettingsInfoLossTime());
		}
		double loss_lati = mPD.getSettingsInfoLossLatitude();
		double loss_longi = mPD.getSettingsInfoLossLongitude();
		if((loss_lati == 0.0)&&(loss_longi == 0.0)){
			
		}else{
			latitude.setText(Double.toString(loss_lati));
			longitude.setText(Double.toString(loss_longi));
		}
		//////////
		requestLocButton = (Button) findViewById(R.id.button1);
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("普通");
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟随");
					mCurrentMode = LocationMode.FOLLOWING;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setText("普通");
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case FOLLOWING:
					requestLocButton.setText("罗盘");
					mCurrentMode = LocationMode.COMPASS;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);

		RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
		radioButtonListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.defaulticon) {
					// 传入null则，恢复默认图标
					mCurrentMarker = null;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, null));
				}
				if (checkedId == R.id.customicon) {
					// 修改为自定义marker
					mCurrentMarker = BitmapDescriptorFactory
							.fromResource(R.drawable.icon_geo);
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
				}
			}
		};
		group.setOnCheckedChangeListener(radioButtonListener);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		//init ui
		SharedPreferences sp = getSharedPreferences("loss_device_record", MODE_WORLD_READABLE);
		int pos = sp.getInt("record_cnt", 0);
		String str = sp.getString("record"+pos, "");
		if(str != null){
			DivideString tmp = new DivideString(str);
			updateUi(tmp.getTime(), tmp.getLatitude(), tmp.getLongitude());
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		if(mMapView !=null){
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(mMapView !=null){
			mMapView.onResume();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		try {
			// 退出时销毁定位
			mLocClient.stop();
			// 关闭定位图层
			mBaiduMap.setMyLocationEnabled(false);
			mMapView.onDestroy();
			mMapView = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}
	
	public void getLocation(View view){
		double x;
		double y;
		try{
			x = Double.parseDouble(latitude.getText().toString().trim());
			y = Double.parseDouble(longitude.getText().toString().trim());
			System.out.println("latitude="+x+";longitude="+y);
		}catch (Exception e) {
			Toast.makeText(getApplicationContext(), "请输入经纬度再查询！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		LatLng point = new LatLng(x,y);
		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		OverlayOptions options = new MarkerOptions().icon(icon).position(point);
		mBaiduMap.addOverlay(options);
		//GeoPoint geoPoint = new GeoPoint((int)(x * 1E6), (int)(y*1E6));
		
		//设定中心点坐标 
		//LatLng cenpt = new LatLng(30.663791,104.07281); 
		//定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder()
		.target(point)
		.zoom(12)
		.build();
		//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		//改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
	}
	
	
	public void showRecordDetails(View view){
		final Dialog dg = new Dialog(this,R.style.FullHeightDialog);
		dg.setCancelable(false);
		dg.setContentView(R.layout.loss_device_record);
		(dg.findViewById(R.id.loss_device_record_cancel_button)).setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				dg.cancel();
			}
		});
		final SharedPreferences sp = getSharedPreferences("loss_device_record", MODE_WORLD_READABLE);	
		ListView listview = (ListView) dg.findViewById(R.id.loss_record_listview);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				String selectedRecord = sp.getString("record"+arg2, "");
				DivideString div_tmp = new DivideString(selectedRecord);
				//更新UI
				updateUi(div_tmp.getTime(), div_tmp.getLatitude(), div_tmp.getLongitude());
				dg.cancel();
			}
		});
		LossRecordListViewAdapter adapter = new LossRecordListViewAdapter(dg.getContext());
		listview.setAdapter(adapter);
		adapter.clear();
		
		//int count = sp.getInt("record_cnt", 0);
		for(int i=0;i<=5;i++){
			String tmp = sp.getString("record"+i,"");
			DivideString divide = new DivideString(tmp);
			String name = divide.getName();
			if(name != null){
				if(!name.equalsIgnoreCase("")){
					adapter.add(name);
					adapter.notifyDataSetChanged();
				}
			}
		}
		
		dg.show();
	}
	
	private final class LossRecordListViewAdapter extends BaseAdapter{
		private ArrayList<String> list;
		private LayoutInflater listContainer;
		TextView time_str = null;
		
		public LossRecordListViewAdapter(Context context) {
			list = new ArrayList<String>();
			listContainer = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return list.size();
		}

		public Object getItem(int arg0) {
			
			return list.get(arg0);
		}

		public long getItemId(int arg0) {
			
			return arg0;
		}
		
		public void clear(){
			list.clear();
		}
		
		public void add(String time) {
	       // if(!list.contains(time)) {
	        	list.add(time);
	       // }
	    }
		
		public View getView(int i, View view, ViewGroup viewGroup) {
			
			if(view == null){
				view = listContainer.inflate(R.layout.loss_device_record_listview, null);
				time_str = (TextView) view.findViewById(R.id.loss_device_listview_time);
				view.setTag(time_str);
			}else{
				time_str = (TextView)view.getTag();
			}
			
	        String time = list.get(i);
	        time_str.setText(time);
	        return view;
		}
	}
	
	private void updateUi(String time, double lat, double lon){
		if(time == null){
			return;
		}
		losstime_value.setText(time);
		latitude.setText(Double.toString(lat));
		longitude.setText(Double.toString(lon));
	}
	
	private boolean isNetworkAvaliabe(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivity == null){
			Log.e("baidumap_activity", "network is unaviable !");
			return false;
		}else{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if(info !=null){
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState()==NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
