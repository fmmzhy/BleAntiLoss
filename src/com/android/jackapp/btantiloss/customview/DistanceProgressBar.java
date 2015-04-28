package com.android.jackapp.btantiloss.customview;

import com.android.jackapp.btantiloss.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ProgressBar;

public class DistanceProgressBar extends ProgressBar {

	public DistanceProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		//setProgressDrawable(getResources().getDrawable(R.color.red));
	}
	
	@Override
	public synchronized void setProgress(int progress) {
		// TODO Auto-generated method stub
		super.setProgress(progress);
		if(progress>=70){
		setProgressDrawable(getResources().getDrawable(R.drawable.custom_distance_progressbar_green));
		}else if (progress>=40){
			setProgressDrawable(getResources().getDrawable(R.drawable.custom_distance_progressbar_yellow));
		}else{
			setProgressDrawable(getResources().getDrawable(R.drawable.custom_distance_progressbar_red));
		}
	}
	
}
