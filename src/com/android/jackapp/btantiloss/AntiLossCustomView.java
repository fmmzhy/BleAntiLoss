package com.android.jackapp.btantiloss;

import com.android.jackapp.btantiloss.db.SettingDB;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AntiLossCustomView extends View {

	private static final String TAG = "AntiLossCustomView";
	private int width;
	private int height;
	private int paddingX;
	private Paint paint = new Paint();
	private int originPoint = Color.argb(255, 240, 240, 240);
	private int nearPoint = Color.argb(0xff, 0x73, 0xc2, 0xfb);//ff73c2fb
	private int normalPoint = Color.argb(0xff, 0x73, 0xc2, 0xfb);
	private int farPoint = Color.argb(0xff, 0x73, 0xc2, 0xfb);
	private int alarmColor = Color.argb(0xaa, 0xff, 0x00, 0x00);
	
	private float line_width = 3;
	private SettingDB mPD = SettingDB.getInstance();;
	Context context;
	String dev_name;
	String alarm_distance;
	boolean connect_status = false;
	private short rssi;
	
	public AntiLossCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	//	this.context = context;
	//	mPD = SettingDB.getInstance();
	}

	public AntiLossCustomView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	
		width = getWidth();
		height = getHeight();
		
		dev_name = mPD.getSettingsInfoAlarmDeviceName();
		alarm_distance = mPD.getSettingsInfoAntiLossDistance();
		
		//1 Ô­µã
		int originPx = width/2;
		int originPy = height/2;
		
		paint.setAntiAlias(true);
		paint.setStyle(Style.FILL);
		paint.setColor(originPoint);
		canvas.drawCircle(originPx, originPy, width/20, paint);
		
		//2.near
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(line_width);
		if(alarm_distance.equalsIgnoreCase(getResources().getString(R.string.antiloss_distance_near))){
			paint.setColor(alarmColor);
		}else{
			paint.setColor(nearPoint);
		}
		canvas.drawCircle(originPx, originPy, width/6, paint);
		
		//3.normal
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(line_width);
		if(alarm_distance.equalsIgnoreCase(getResources().getString(R.string.default_antiloss_distance))){
			paint.setColor(alarmColor);
		}else{
			paint.setColor(normalPoint);
		}
		canvas.drawCircle(originPx, originPy, width/4, paint);
		
		//4.Far
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(line_width);
		if(alarm_distance.equalsIgnoreCase(getResources().getString(R.string.antiloss_distance_far))){
			paint.setColor(alarmColor);
		}else{
			paint.setColor(farPoint);
		}
		canvas.drawCircle(originPx, originPy, width/3, paint);
		
		
		//5.update device location
		if(connect_status){
			paint.reset();
			//Log.i(TAG, "xxxxxx");
			paint.setTextSize(50);
			//paint.setTypeface(typeface)
			canvas.drawText(dev_name, 40, 40, paint);
			canvas.drawText(Short.toString(rssi), 80, 80, paint);
		}	
	}
	
	public void updateView(String device_name,boolean connect_status){
		dev_name = device_name;
		this.connect_status = connect_status;
		invalidate();
	}
	
	public void updateView(boolean conn_status){
		this.connect_status = conn_status;
		invalidate();
	}
	
	public void updateView(){
		invalidate();
	}

	public void updateView(String device_name, short rssi,
			boolean connect_status) {
		// TODO Auto-generated method stub
		this.rssi = rssi;
		dev_name = device_name;
		this.connect_status = connect_status;
		
		//Log.i(TAG, "update view :rssi="+rssi+"connect_status"+connect_status);
		invalidate();
	}
}
