package com.android.jackapp.btantiloss;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class BaiduApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		// ��ʹ�� SDK �����֮ǰ��ʼ�� context ��Ϣ������ ApplicationContext
		
		SDKInitializer.initialize(this);
	}
	

}
