package com.android.jackapp.btantiloss;

import com.android.jackapp.btantiloss.MainActivity.RadioGroupChangedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

	private RadioGroupChangedListener radioGroupChangedListener;

	public ScreenSlidePagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		Fragment fragment= null;

		if(arg0 == 0){
			fragment = new DeviceFragment();//new HomeFragment();
		}else if(arg0 == 1){
			fragment = new FindFragment();
		}else if(arg0 == 2){
			fragment = new SettingsBleFragment();// SettingsFragment();
		}else if(arg0 == 3){
			fragment = new HelpFragment();
		}
		
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}

}
