package com.android.jackapp.btantiloss;

import com.baidu.mapapi.SDKInitializer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DFDeviceLossInfo extends DFBase {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.df_device_loss_info, container);
		
		customTitle(view);
		return view;
	}
}
