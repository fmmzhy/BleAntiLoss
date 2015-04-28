package com.android.jackapp.btantiloss;

import com.android.jackapp.btantiloss.audio.Audio;
import com.android.jackapp.btantiloss.db.SettingDB;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsBleFragment extends Fragment {
	protected static final String TAG = "SettingsBleFragment";
	private CheckBox audio_checkbox;
	//private RadioGroup audio_radiogroup;
	private static final String VOICE_1 = "alarm.ogg";
	private static final String VOICE_2 = "warn.wav";
	private static final String VOICE_3 = "warn2.wav";
	private static final String VOICE_4 = "chime.ogg";
	private RadioButton voice_1;
	private RadioButton voice_2;
	private RadioButton voice_3;
	private RadioButton voice_4;
	Context context;
	Audio audio;// = new Audio(context);
	private SettingDB mPD;
	private CheckBox shake_checkbox;
	private Spinner dfalarm_type_ringtime;
	private TextView setting_device_name;
	private RadioButton distance_radio1;
	private RadioButton distance_radio2;
	private RadioButton distance_radio3;
	private TextView setting_devinfo_name1;
	private TextView setting_devinfo_mac1;
	private TextView setting_devinfo_name2;
	private TextView setting_devinfo_mac2;
	private TextView setting_devinfo_name3;
	private TextView setting_devinfo_mac3;
	
	private OnCheckedChangeListener voiceListener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			if(arg1){				
				audio.stop();
				String name = null;
				if(arg0.getId() == R.id.voice_1){
					name = VOICE_1;//"alarm.ogg";
				}else if(arg0.getId() == R.id.voice_2){
					name = VOICE_2;//"warn.wav";
				}else if(arg0.getId() == R.id.voice_3){
					name = VOICE_3;//"warn2.wav";
				}else if(arg0.getId() == R.id.voice_4){				
					name = VOICE_4;//"chime.ogg";
				}
				
				audio.play(name);
				mPD.setSettingsInfoAlarmAudio(name);
				//update audio group selected 
				voice_1.setSelected(false);
				voice_2.setSelected(false);
				voice_3.setSelected(false);
				voice_4.setSelected(false);
				((RadioButton)arg0).setSelected(true);
			}
		}
	};
	
	private OnCheckedChangeListener distanceListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){		
			String distance = arg0.getText().toString();
			//Log.e(TAG, "distance = "+distance);
			mPD.setSettingsInfoAntiLossDistance(distance);
			
			//update audio group selected 
			distance_radio1.setSelected(false);
			distance_radio2.setSelected(false);
			distance_radio3.setSelected(false);
			((RadioButton)arg0).setSelected(true);
		}
		}
	};
	private View setting_device_info3;
	private View setting_device_info2;
	private View setting_device_info1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Log.e(TAG, "setting oncreateview");
		//switch
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_settings_ble, container, false);

		context = getActivity();
		//PD
		mPD = SettingDB.getInstance();
		//device name
		//setting_device_name = (TextView)rootView.findViewById(R.id.setting_device_name);
		
		//audio
		audio_checkbox = (CheckBox)rootView.findViewById(R.id.audio_checkbox);
		audio_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				checkboxChangedSub(arg0,arg1);
			}
		});
		
		//audio_radiogroup = (RadioGroup)rootView.findViewById(R.id.radiogroup);
		voice_1 = (RadioButton)rootView.findViewById(R.id.voice_1);
		voice_2 = (RadioButton)rootView.findViewById(R.id.voice_2);
		voice_3 = (RadioButton)rootView.findViewById(R.id.voice_3);
		voice_4 = (RadioButton)rootView.findViewById(R.id.voice_4);
		voice_1.setOnCheckedChangeListener(voiceListener);
		voice_2.setOnCheckedChangeListener(voiceListener);
		voice_3.setOnCheckedChangeListener(voiceListener);
		voice_4.setOnCheckedChangeListener(voiceListener);
		
		//shake
		shake_checkbox = (CheckBox)rootView.findViewById(R.id.shake_checkbox);
		shake_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				checkboxChangedSub(arg0,arg1);
			}
		});
		//alarm time
		dfalarm_type_ringtime = (Spinner)rootView.findViewById(R.id.dfalarm_type_ringtime);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.ringtime, R.layout.custom_spinner/*android.R.layout.simple_spinner_item*/);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dfalarm_type_ringtime.setAdapter(adapter);
		dfalarm_type_ringtime.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String selectStr = arg0.getSelectedItem().toString();
				mPD.setSettingsInfoAlarmTime(selectStr);
			}
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		
		//alarm distance
		distance_radio1 = (RadioButton)rootView.findViewById(R.id.distance_radio1);
		distance_radio2 = (RadioButton)rootView.findViewById(R.id.distance_radio2);
		distance_radio3 = (RadioButton)rootView.findViewById(R.id.distance_radio3);
		distance_radio1.setOnCheckedChangeListener(distanceListener);
		distance_radio2.setOnCheckedChangeListener(distanceListener);
		distance_radio3.setOnCheckedChangeListener(distanceListener);

		//防丢器设备信息
		setting_devinfo_name1 = (TextView) rootView.findViewById(R.id.setting_devinfo_name1);
		setting_devinfo_mac1 = (TextView) rootView.findViewById(R.id.setting_devinfo_mac1);
		setting_devinfo_name2 = (TextView) rootView.findViewById(R.id.setting_devinfo_name2);
		setting_devinfo_mac2 = (TextView) rootView.findViewById(R.id.setting_devinfo_mac2);
		setting_devinfo_name3 = (TextView) rootView.findViewById(R.id.setting_devinfo_name3);
		setting_devinfo_mac3 = (TextView) rootView.findViewById(R.id.setting_devinfo_mac3);
		setting_device_info1 = (View)rootView.findViewById(R.id.setting_device_info1);
		setting_device_info2 = (View)rootView.findViewById(R.id.setting_device_info2);
		setting_device_info3 = (View)rootView.findViewById(R.id.setting_device_info3);
		//Log.e(TAG, "setting oncreateview end");
		return rootView;
	}
	
	protected void checkboxChangedSub(CompoundButton arg0, boolean arg1) {
		int alarmtype;// = mPD.getSettingsInfoAlarmType();
		if(audio_checkbox.isChecked()&&shake_checkbox.isChecked()){
			alarmtype = 2;
		}else if(audio_checkbox.isChecked()&&(!shake_checkbox.isChecked())){
			alarmtype = 0;
		}else if (shake_checkbox.isChecked()&&(!audio_checkbox.isChecked())) {
			alarmtype = 1;
		}else{
			alarmtype = 3;
		}
		mPD.setSettingsInfoAlarmType(alarmtype);
		//update audio/shake checkbox ui
		if((alarmtype==2)||(alarmtype==0)){
			audio_checkbox.setSelected(true);
		}else{
			audio_checkbox.setSelected(false);
		}
		if((alarmtype==2)||(alarmtype==1)){
			shake_checkbox.setSelected(true);
		}else{
			shake_checkbox.setSelected(false);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		//Log.e(TAG, "setting onstart");

		if(mPD == null) {
			mPD = SettingDB.getInstance();
		}
		final int alarmType = mPD.getSettingsInfoAlarmType();
		final String audio_voice = mPD.getSettingsInfoAlarmAudio();
		final String ringtime = mPD.getSettingsInfoAlarmTime();
		final String distance = mPD.getSettingsInfoAntiLossDistance();
		//audio_checkbox
		if((alarmType==0)||(alarmType==2)){
			audio_checkbox.setSelected(true);
		}else{
			audio_checkbox.setSelected(false);
		}
		//audio 
		if(audio_voice.equalsIgnoreCase(VOICE_1)){
			voice_1.setSelected(true);
		}else if(audio_voice.equalsIgnoreCase(VOICE_2)){
			voice_2.setSelected(true);
		}else if(audio_voice.equalsIgnoreCase(VOICE_3)){
			voice_3.setSelected(true);
		}else if(audio_voice.equalsIgnoreCase(VOICE_4)){
			voice_4.setSelected(true);
		}
		//shake_checkbox
		if((alarmType == 1)||(alarmType==2)){
			shake_checkbox.setSelected(true);
		}else{
			shake_checkbox.setSelected(false);
		}
		//alarm time
		int position = 0;
		for(; !ringtime.equals((getResources().getStringArray(R.array.ringtime))[position]);position++){
		}
		dfalarm_type_ringtime.setSelection(position);
		//alarm distance
		distance_radio1.setSelected(false);
		distance_radio2.setSelected(false);
		distance_radio3.setSelected(false);
		if(distance.equalsIgnoreCase(getString(R.string.setting_close))){
			distance_radio1.setSelected(true);
		} else if(distance.equalsIgnoreCase(getString(R.string.setting_middle))){
			distance_radio2.setSelected(true);
		} else if(distance.equalsIgnoreCase(getString(R.string.setting_far))){
			distance_radio3.setSelected(true);
		}				
		
		//device info
		
		SharedPreferences msp = context.getSharedPreferences("many_device_status", Context.MODE_PRIVATE);
		int v1 = msp.getInt("device1_visible", View.GONE);
		setting_device_info1.setVisibility(v1);
		if(v1 == View.VISIBLE){
			setting_devinfo_name1.setText(msp.getString("device1_name", getString(R.string.device_unknown)));
			setting_devinfo_mac1.setText(msp.getString("device1_mac", getString(R.string.device_unknown)));
		}
		
		int v2 = msp.getInt("device2_visible", View.GONE);
		setting_device_info2.setVisibility(v2);
		if(v2 == View.VISIBLE){
			System.out.println("SettingsBleFragment.onStart() ===>"+msp.getString("device2_name", getString(R.string.device_unknown)));
			setting_devinfo_name2.setText(msp.getString("device2_name", getString(R.string.device_unknown)));
			setting_devinfo_mac2.setText(msp.getString("device2_mac", getString(R.string.device_unknown)));
		}
		
		int v3 = msp.getInt("device3_visible", View.GONE);
		setting_device_info3.setVisibility(v3);
		if(v3 == View.VISIBLE){
			setting_devinfo_name3.setText(msp.getString("device3_name", getString(R.string.device_unknown)));
			setting_devinfo_mac3.setText(msp.getString("device3_mac", getString(R.string.device_unknown)));
		}
		Log.e(TAG, "setting onstart end");
	}

	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser){
			audio = new Audio(context);
		}else{
			try {
				audio.stop();
				audio.release();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
}
